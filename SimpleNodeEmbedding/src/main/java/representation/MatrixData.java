package representation;

/**
 * Interface for graph representers that expose a Matrix representation.
 *
 * @param <Mat> the matrix type
 */
public interface MatrixData<Mat> {
    /**
     * Returns the matrix representation of the graph.
     *
     * @return the graph matrix
     */
    Mat getMatrix();
}
