package RandomWalksEmbedding.WalkModel;

import Core.ImmutableGraphData;
import RandomWalksEmbedding.WalkModel.Strategy.WalkStrategy;
import representation.AdjacentList.Neighbor;
import Core.VertexIndexMapping;
import representation.AdjacentList.ImmutableAdjacentList;

import java.util.*;

/**
 * Implements the DeepWalk uniform random walk strategy over an immutable adjacency list.
 * Each step selects one outgoing neighbor with equal probability to form a walk sequence.
 */
public class DeepWalk<V> implements WalkStrategy<V> {
    private final Map<Integer, List<Neighbor<Integer>>> adjacentList;
    private final VertexIndexMapping<V> mapper;
    private final Random random;

    /**
     * Constructs a DeepWalk strategy by preprocessing the input graph into an
     * immutable adjacency list and initializing the vertex-to-index mapping.
     *
     * @param immutableGraphData immutable graph structure containing vertices and edges
     * @param mapping            mapping from generic vertices to internal integer node IDs
     * @param randomSeed seed for controlling randomness and ensuring reproducible sampling
     */
    public DeepWalk(ImmutableGraphData<V> immutableGraphData, VertexIndexMapping<V> mapping, long randomSeed) {
        ImmutableGraphData<V> immutableGraphDataObj = Objects.requireNonNull(immutableGraphData, "immutableGraphData");
        this.mapper = Objects.requireNonNull(mapping, "mapping");
        this.adjacentList = new ImmutableAdjacentList<>(immutableGraphDataObj, mapping)
                .getAdjacentMap();

        this.random = new Random(randomSeed);
    }

    /**
     * Generates a random walk starting from the given vertex for a fixed number of hops.
     *
     * @param start starting vertex of the walk
     * @param hops  number of transitions to perform
     * @return list of node indices representing the walk path
     */
    @Override
    public ArrayList<Integer> generateWalk(V start, int hops) {
        ArrayList<Integer> sequence = new ArrayList<>();
        int current = mapper.indexForVertex(start);
        sequence.add(current);
        for (int i = 0; i < hops; i++) {
            List<Neighbor<Integer>> neighbors = adjacentList.get(current);
            if (neighbors == null || neighbors.isEmpty()) {
                break;
            }

            int uniformNeighbor = this.random.nextInt(neighbors.size());
            int next = neighbors.get(uniformNeighbor).destination();
            sequence.add(next);
            current = next;
        }
        return sequence;
    }
}
