package RandomWalksEmbedding.ContextModel;

import RandomWalksEmbedding.ContextModel.ContextStrategy.ContextWindow;
import RandomWalksEmbedding.SampleDataset.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates symmetric context pairs from a random walk using a sliding window,
 * where both forward and backward neighbors are treated as context.
 */
public class SymmetricSlidingWindow implements ContextWindow {
    /**
     * Generate positive (target, context) pairs from a walk.
     *
     * @param walk       the generated random walk
     * @param windowSize size of the sliding window
     * @return list of (target, context) index pairs
     */
    @Override
    public List<Pair> generatePositivePairs(List<Integer> walk, int windowSize) {
        List<Pair> positiveSamples = new ArrayList<>();
        for (int i = 0; i < walk.size(); i++) {
            for (int j = Math.max(0, i - windowSize); j <= Math.min(walk.size() - 1, i + windowSize); j++) {
                if (i != j) {
                    positiveSamples.add(new Pair(walk.get(i), walk.get(j)));
                }
            }
        }
        return positiveSamples;
    }
}
