package graph;

import io.jbotsim.core.Topology;
import io.jbotsim.io.format.dot.DotTopologySerializer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.FileSystems;

public class RingGenerator {

    public static void genRing(Topology topology, String graphName) {
        DotTopologySerializer dts = new DotTopologySerializer();
        Path path = FileSystems.getDefault().getPath("graphs", graphName);

        try {
            String content = Files.readString(path);
            dts.importFromString(topology, content);
        }
        catch(IOException exception) {
            System.out.println(exception.getMessage());
        } 
    }

}
