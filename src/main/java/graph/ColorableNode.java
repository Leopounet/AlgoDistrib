package graph;

import io.jbotsim.core.Node;
import io.jbotsim.core.Message;
import io.jbotsim.core.Color;

import java.util.List;

import java.util.ArrayList;

import util.Algorithmic;
import util.StepRing;

public class ColorableNode extends Node {

    // x value of this node
    private int x;

    // Counter used to know when to stop the algorithm
    private int l;
    private int l2;

    // ID of the successor and predecessor (determined later)
    private int succID;
    private int predID;

    // What is the current step of the algorithm?
    private StepRing currentStep = StepRing.COMP_SUCC_PRED;

    // Keys to access data in the messages
    private final String idKey = "id";
    private final String xKey = "x";
    private final String colorKey = "color";
    private final String recolorKey = "recolor";

    // Number of vertices in the ring
    public static int n;

    // A message placeholder
    private Message message = new Message();

    // The current color of the node
    private int color = -1;

    @Override
    public void onStart() {

        // Initialize x to the ID
        this.x = this.getID();

        // Initialize l and l2 (used to stop the COMPUTE_X step)
        this.l = (int)(Math.ceil(Math.log(n)));
        this.l2 = -1;

        // Creates defaults values in the message
        this.message.setProperty(this.idKey, this.x);
        this.message.setProperty(this.xKey, this.x);
        this.message.setProperty(this.colorKey, this.x);
        this.message.setProperty(this.recolorKey, false);

        // Sends the first message to determine the successors and predecessors
        this.sendAll(message);
    }

    @Override
    public void onClock() {

        // First step of the algorithm: compute successor and predecessor
        if(this.currentStep == StepRing.COMP_SUCC_PRED) {
            this.computeSuccPred();
        }

        // Second step of the algorithm: fix edges with double orientation
        else if(this.currentStep == StepRing.FIX_DOUBLE_DIRECTED) {
            // Not useful for now
            this.currentStep = StepRing.COMPUTE_X;
        }

        // Third step of the algorithm: compute the x value for this node
        else if(this.currentStep == StepRing.COMPUTE_X) {
            this.computeX();
        }

        // Fourth step of the algorithm: go from 6 to 4 colors
        else if(this.currentStep == StepRing.FIRST_REDUCE) {
            this.reduceColor();
        }

        // Fifth step of the algorithm: go from 4 to 3 colors
        else if(this.currentStep == StepRing.SECOND_REDUCE) {
            this.secondReduce();
        }

        else if(this.currentStep == StepRing.FIND_ISSUES) {
            this.findIssues();
        }

        else if(this.currentStep == StepRing.FIX) {
            this.fix();
        }

        // Extra step: visually color the nodes
        else if(this.currentStep == StepRing.SET_COLOR) {
            this.computeColor();
        }

        // Done
        else if(this.currentStep == StepRing.DONE) {
            // skip
        }

        // Can not happen normally, just for completion sake
        else {
            System.out.println("Something went wrong, this step does not exist.");
        }
    }

    private void computeSuccPred() {

        // Get all the incoming messages (id + x value + color)
        List<Message> messages = this.getMailbox();

        // Array to store the two incoming IDs
        int nID[] = {0, 0};
        int index = 0;

        // Receive and store both IDs
        for(Message m: messages) {
            nID[index] = (int)m.getProperty(this.idKey);
            index += 1;
        }

        // Set the successor and predecessor of the current node
        this.succID = Math.max(nID[0], nID[1]);
        this.predID = Math.min(nID[0], nID[1]);

        // Change the current step
        this.currentStep = StepRing.COMPUTE_X;

        // Send a message to everyone (to change?)
        this.sendAll(message);
    }

    private void computeX() {

        // Get all the incoming messages (id + x value + color)
        List<Message> messages = getMailbox();

        // Placeholders used to know which x value corresponds to the successor
        // and which one corresponds to the predecessor
        int succX = 0;
        int predX = 0;
        
        // Read all messages and assign the x values accordingly
        for(Message m: messages) {
            int id = (int)m.getProperty(this.idKey);
            if(id == succID) succX = (int)m.getProperty(this.xKey);
            if(id == predID) predX = (int)m.getProperty(this.xKey);
        }

        // Compute the new x value
        this.x = Algorithmic.PosDiff(Algorithmic.PosDiff(predX, this.x), 
                                     Algorithmic.PosDiff(this.x, succX));

        //

        // Compute the new value of l to know if this part of the algorithm is done
        this.l2 = l;
        this.l = 1 + (int)(Math.ceil(Math.log(1 + Math.ceil(Math.log(this.l)))));

        // If l and l2 are equal then this part of the algorithm is done
        if(this.l == this.l2) {

            // Update the current step
            this.currentStep = StepRing.FIRST_REDUCE;

            // Set the current color of the node in [0; 5] and update the message
            this.color = this.x;
            this.message.setProperty(this.colorKey, this.color);
        }

        // Send a new messages with the updated x value
        this.message.setProperty(this.xKey, this.x);
        this.sendAll(message);
    }

    private void reduceColor() {

        // If the color of this node needs to be reduced
        if(this.color >= 4) {

            // Get all the incoming messages (id + x value + color)
            List<Message> messages = this.getMailbox();

            // A list to hold all the colors of the neighbors
            // and more if necessary (i.e: this node's color = 5)
            List<Integer> X = new ArrayList<Integer>();

            // Will be set to true if one of the neighbors of this node
            // has a color = 4 or 5
            boolean neighborColorTooBig = false;


            // Read all messages
            for(Message m: messages) {

                // Get the color of the node and add it to the list X
                int c = (int)m.getProperty(this.colorKey);
                X.add(c);

                // if the color is too big, set the flag to true
                if(c >= 4) {
                    neighborColorTooBig = true;
                }
            }


            // If one of the neighbor is too big then add the
            // corresponding colors [0, 1] iff this node's color is 5
            if(neighborColorTooBig) {
                for(int i = 0; i < (this.color - 4) * 2; i++) {
                    X.add(i);
                }
            }

            // Apply first free to compute the new color of this node
            this.color = Algorithmic.FirstFree(X);
        }

        // Switch to the next step
        this.currentStep = StepRing.SECOND_REDUCE;

        // Update the color of this node and send a new pulse
        message.setProperty(this.colorKey, this.color);
        this.sendAll(message);
    }

    private void secondReduce() {
        // If the color of this node is still too big
        if(this.color == 3) {

            // Get all the incoming messages (id + x value + color)
            List<Message> messages = this.getMailbox();

            // Array to store the colors of the neighbors
            List<Integer> X = new ArrayList<Integer>();

            // Read all messages and add the color to the X array
            for(Message m: messages) {
                int c = (int)m.getProperty(this.colorKey);
                X.add(c);
            }

            // Apply first free to find the new color of this node
            this.color = Algorithmic.FirstFree(X);
        }

        // Update the current step
        this.currentStep = StepRing.FIND_ISSUES;

        // Update the color of this node and send a new pulse
        message.setProperty(this.colorKey, this.color);
        this.sendAll(message);
    }

    private void findIssues() {

        // Get all the incoming messages (id + x value + color)
        List<Message> messages = this.getMailbox();

        // Flag set to true if this node has to be recolored
        boolean recolor = false;

        // Read all messages, this node will be recolored if
        // one of its neighbors has the same color and has a 
        // bigger ID
        for(Message m: messages) {
            if((int)m.getProperty("color") == this.color &&
               (int)m.getProperty(this.idKey) > this.getID()) {
                recolor = true;
            }
        }

        // Update the current step
        this.currentStep = StepRing.FIX;

        // Set the recolor flag and send the message
        message.setProperty(this.recolorKey, recolor);
        this.sendAll(message);
    }

    private void fix() {
        // If this node has to be recolored
        if((boolean)message.getProperty(this.recolorKey)) {

            // Get all the incoming messages (id + x value + color + recolor)
            List<Message> messages = this.getMailbox();

            // Stores the colors of the neighbors to apply first free
            List<Integer> X = new ArrayList<Integer>();

            // Read all messages, if any neighbor has to be recolored and has a
            // greater ID, than this node will wait for the next round
            for(Message m: messages)  {
                if((boolean)m.getProperty(this.recolorKey) && 
                   (int)m.getProperty(this.idKey) > this.getID()) {
                    return;
                }
                X.add((int)m.getProperty(this.colorKey));
            }

            // Apply first free to find the new color of this node
            this.color = Algorithmic.FirstFree(X);
        }

        // Update the current step (this step can only last a maximum
        // of two rounds, so it's find to switch steps right away)
        this.currentStep = StepRing.SET_COLOR;

        // Send a new message with the new color of this node
        message.setProperty(this.colorKey, this.color);
        message.setProperty(this.recolorKey, false);
        this.sendAll(message);
    }

    private void computeColor() {

        // Compute a visual color for this node in the viewer
        switch(this.color) {
            case 0: this.setColor(Color.RED);
            break;
            case 1: this.setColor(Color.BLUE);
            break;
            case 2: this.setColor(Color.GREEN);
            break;
            default: System.out.println(this.color);
        }

        // The algorithm is now done
        this.currentStep = StepRing.DONE;
    }
}