package representation;

import representation.AdjacentList.Neighbor;

import java.util.List;
import java.util.Map;

/**
 * Interface for graph representers that expose an adjacency-list representation.
 *
 * @param <T> the vertex type
 */
public interface AdjacencyListData<T> {
    /**
     * Returns the adjacency list of the graph.
     * Each vertex is mapped to a list of outgoing edges.
     *
     * @return a map representing the adjacency list
     */
    Map<T, List<Neighbor<T>>> getAdjacentMap();
}
