package RandomWalksEmbedding.SampleDataset;

import Core.VertexIndexMapping;
import RandomWalksEmbedding.ContextModel.SymmetricSlidingWindow;
import RandomWalksEmbedding.NegativeSamplingModel.UniformNegativeSample;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Generates positive and negative training samples from random walk sequences
 * over an immutable graph structure for node embedding algorithms.
 *
 * @param <V> The vertex (node) type of the graph
 */
public class PositiveAndNegativeSamples<V> {
    private final VertexIndexMapping<V> mapper;
    private final ArrayList<ArrayList<Integer>> sequences;
    private final int windowSize;
    private final boolean allowSampleDuplicate;
    private final Random random;

    /**
     * Creates a generator for positive and negative samples based on graph walks.
     *
     * @param mapper Mapping between vertices and integer indices
     * @param sequences Random walk sequences used for positive sampling
     * @param allowSampleDuplicate deduplicate positive-negative samples
     * @param randomSeed seed for controlling randomness and ensuring reproducible sampling
     * @param windowSize size of the sliding windows
     *
     * @throws IllegalArgumentException if sequences is empty
     */
    public PositiveAndNegativeSamples(VertexIndexMapping<V> mapper, ArrayList<ArrayList<Integer>> sequences, int windowSize, boolean allowSampleDuplicate, long randomSeed) {
        this.mapper = Objects.requireNonNull(mapper, "mapper cannot be null");

        this.random = new Random(randomSeed);

        Objects.requireNonNull(sequences, "sequences cannot be null");
        if (sequences.isEmpty()) {
            throw new IllegalArgumentException("sequences is empty");
        }
        if  (windowSize <= 0) {
            throw new IllegalArgumentException("windowSize must be greater than 0");
        }
        this.windowSize = windowSize;
        this.sequences = new ArrayList<>(sequences);
        this.allowSampleDuplicate = allowSampleDuplicate;
    }

    /**
     * Generates a fallback set of negative samples by randomly selecting vertices
     * from the graph when no explicit negative samples are provided.
     *
     * <p>
     * The method shuffles all vertices and selects up to 10% of them,
     * with a maximum cap of 20 vertices, to form the negative sample set.
     * </p>
     *
     * @return A randomly selected set of vertices used as negative samples
     */
    private Set<Integer> fallBackRandomNegativeSamples() {
        ArrayList<Integer> randomSamples = new ArrayList<>(mapper.getVertexToIndex().values());
        Collections.shuffle(randomSamples, new Random());

        return randomSamples
                .stream()
                .skip(randomSamples.size() - (int) Math.min(randomSamples.size() * 0.10, 20))
                .collect(Collectors.toSet());
    }

    /**
     * Generates shuffled positive and negative samples from random walks using graph-aware negative sampling.
     *
     * @return unmodifiable list of SampleDataset
     */
    public List<Sample> generatePositiveNegativeSampleDataset() {
        List<Sample> datasets = new ArrayList<>();
        // move these two to constructor
        SymmetricSlidingWindow symmetricSlidingWindow = new SymmetricSlidingWindow();
        UniformNegativeSample<V> uniformNegativeSample = new UniformNegativeSample<>(this.mapper);
        //
        sequences.removeIf(walk -> walk.size() < 2);
        for (ArrayList<Integer> walk : sequences) {
            List<Pair> positivePairs = symmetricSlidingWindow.generatePositivePairs(walk, this.windowSize);
            for (Pair positivePair : positivePairs) {
                String label = "1";
                Sample positiveSample = new Sample(positivePair.v1(), positivePair.v2(), label);
                datasets.add(positiveSample);
            }
            for (var target : walk) {
                List<Pair> negativePairs = uniformNegativeSample
                        .generatePositivePairs(target,
                        new HashSet<>(forbiddingNegatives(target, walk, this.windowSize)),
                        this.windowSize);
                for (Pair negativePair : negativePairs) {
                    String label = "0";
                    Sample negativeSample = new Sample(negativePair.v1(), negativePair.v2(), label);
                    datasets.add(negativeSample);
                }
            }
        }
        if (!allowSampleDuplicate) {
            datasets = new ArrayList<>(new LinkedHashSet<>(datasets));
        }
        Collections.shuffle(datasets, random);
        return datasets;
    }

    /**
     * Computes the set of nodes that must be excluded from negative sampling
     * for a given target within a sliding window of a walk.
     *
     * @return nodes forbidden for negative sampling of a target.
     */
    private List<Integer> forbiddingNegatives(int target, List<Integer> walk, int windowSize) {
        List<Integer> forbidding = new ArrayList<>();
        int i = walk.indexOf(target);
        forbidding.add(target);
        for (int j = Math.max(0, i - windowSize); j <= Math.min(walk.size() - 1, i + windowSize); j++) {
            if (i != j) {
                forbidding.add(walk.get(j));
            }
        }
        return forbidding;
    }
}

