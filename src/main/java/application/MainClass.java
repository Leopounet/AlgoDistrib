package application;

import io.jbotsim.core.Topology;
import io.jbotsim.ui.JViewer;

import graph.*;

public class MainClass {

    public static void main(String[] args) {

        Topology topology = new Topology();
 
        topology.setDefaultNodeModel(ColorableNode.class);
        // topology.setTimeUnit(10);
        RingGenerator.genRing(topology, "tmp.dot");
        ColorableNode.n = topology.getNodes().size();

        new JViewer(topology);
        topology.start();
    }

}