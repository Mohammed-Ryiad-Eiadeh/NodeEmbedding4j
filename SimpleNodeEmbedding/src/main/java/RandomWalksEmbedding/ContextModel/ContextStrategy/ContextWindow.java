package RandomWalksEmbedding.ContextModel.ContextStrategy;

import RandomWalksEmbedding.SampleDataset.Pair;

import java.util.List;

/**
 * Interface for generating positive samples from a random walk
 * using a sliding window mechanism.
 */
public interface ContextWindow {
    /**
     * Generate positive (target, context) pairs from a walk.
     *
     * @param walk the generated random walk
     * @param windowSize size of the sliding window
     * @return list of (target, context) index pairs
     */
    List<Pair> generatePositivePairs(List<Integer> walk, int windowSize);
}
