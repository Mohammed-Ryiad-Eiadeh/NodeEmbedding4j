package MainPackage;

import Core.GraphBuilder;
import Core.GraphType;
import Core.VertexIndexMapping;
import RandomWalksEmbedding.SampleDataset.PositiveAndNegativeSamples;
import RandomWalksEmbedding.WalkModel.DeepWalk;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws Exception {
        var graphDataFile = Paths.get(System.getProperty("user.dir"), "BioGraphs", "bio-CE-GN.txt");
        var graphReader = Files.newBufferedReader(graphDataFile);

        var graphBuilder = new GraphBuilder<Integer>(GraphType.Directed);
        var headerLineId = 1;

        graphReader.lines().skip(headerLineId).forEach(line -> {
            String[] currentLine = line.trim().split(" ");
            var source = Integer.parseInt(currentLine[0]);
            var destination = Integer.parseInt(currentLine[1]);
            var weight = currentLine.length >= 3 ? Float.parseFloat(currentLine[2]) : 1.0f;
            graphBuilder.addConnection(source, destination, weight);
        });
        graphReader.close();

        var builder = graphBuilder
                .ifNotEmpty()
                .build();

        var numOfEdges = builder.edgeCount();
        var numOfVertices = builder.vertexCount();
        System.out.printf("Number of nodes: %s, Number of edges: %s\n", numOfVertices, numOfEdges);

        var mapper = new VertexIndexMapping<>(builder);

        var deepWalk = new DeepWalk<>(builder,
                mapper,
                12345L);

        ArrayList<ArrayList<Integer>> RWs = new ArrayList<>();
        for (var i = 0; i < numOfVertices; i++) {
            RWs.add(deepWalk.generateWalk(mapper.getVertex(i), 100));
        }

        var positiveNegativeSample = new PositiveAndNegativeSamples<>(mapper,
                RWs,
                2,
                false,
                12345L);

        var positiveNegativeSampleDatasets = positiveNegativeSample
                .generatePositiveNegativeSampleDataset();

        positiveNegativeSampleDatasets.forEach(System.out::println);
    }
}
