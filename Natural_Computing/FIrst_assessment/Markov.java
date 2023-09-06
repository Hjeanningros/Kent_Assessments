import java.lang.Math;
import java.lang.module.ResolutionException;
import java.util.*;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;

import javax.print.attribute.standard.PrinterMessageFromOperator;

class Node {
    Map<Integer, Node> node_dest;
    double prob_new_dest;
    double prob_itself;

    public Node() {
        node_dest = new HashMap<Integer, Node>();
        prob_new_dest = 0.25;
        prob_itself = 0.0;
    }

    public void addDest(int dest, Node node) {
        node_dest.put(dest, node);
    }

}

class NodeCont {
    LinkedHashMap<Integer, Double> proba_dest = new LinkedHashMap<>();
    Map<Integer, Double> rate_dest = new HashMap<Integer, Double>();

    public NodeCont() {
        proba_dest = new LinkedHashMap<Integer, Double>();
    }

    public void addProbaDest(int dest, double proba) {
        proba_dest.put(dest, proba);
    }

    public void addRateDest(int dest, double rate) {
        rate_dest.put(dest, rate);
    }
}

class Markov{




//##################################
    private static Map<Integer, Node> createGraph(int k) {
        Map<Integer, Node> graph = new HashMap<Integer, Node>();
        int graph_size = (int)Math.sqrt((double)k);

        // Initialize the graph
        for (int i = 1; i <= k; i++)
            graph.put(i, new Node());

        // Initialize links and probabilities stay at the same place 
        // Check if the node is on a border, if yes, I add 0,25 to the probability to stay at the same place
        for (int i = 1; i <= k; i++) {

            if (i - graph_size <= 0)
                graph.get(i).prob_itself += 0.25;
            else
                graph.get(i).addDest(i - graph_size, graph.get(i - graph_size));
            
            if (i + graph_size > k) 
                graph.get(i).prob_itself += 0.25;
            else 
                graph.get(i).addDest(i + graph_size, graph.get(i + graph_size));

            if (i % graph_size == 0) 
                graph.get(i).prob_itself += 0.25;
            else 
                graph.get(i).addDest(i + 1, graph.get(i + 1));

            if (i % graph_size == 1) 
                graph.get(i).prob_itself += 0.25;
            else 
                graph.get(i).addDest(i - 1, graph.get(i - 1));   
            
        }

        return graph;


    }

    public static  double getTransProb(int i,int j,int k){
        // Create the graph with the probability to go in an other state
        Map<Integer, Node> graph = createGraph(k);
        double prob = 0.0;

        // Take the source node and the destination node
        Node node_i = graph.get(i);
        Node node_j = node_i.node_dest.get(j);

        // If the destination doesn't exist, the probability is 0
        // Else, we take the probability to go to the destination
        if (node_j == null)
            prob = 0;
        else if(i == j)
            prob = node_i.prob_itself;
        else
            prob = node_i.prob_new_dest;

        return prob;

    }


//##################################
    private static int towerSampling(int state, Node node) 
    {
        // Create a random number
        double r = Math.random();

        // Create a LinkedHashMap to store the probability to go in an other state because when we iterate on a 
        // a LinkedHashMap the values are returned in the order in which they were added.
        // So I add them in ascending order so i don't need to sort them. I start with 0, the itself probability if i have one (it will always be the same or an higher value)
        // and then the probability to go in another state
        LinkedHashMap<Integer, Double> t = new LinkedHashMap<Integer, Double>();
        double proba = 0.0;

        // Init Map with 0, the probability to stay at the same place and the probability to go in an other state
        t.put(0, 0.0);
        if (node.prob_itself != 0)
            t.put(state, node.prob_itself);
        for (Map.Entry<Integer, Node> dest : node.node_dest.entrySet()) 
            t.put(dest.getKey(), node.prob_new_dest);


        // Find new state 
        for (Map.Entry<Integer, Double>  probadest : t.entrySet()) {
            // Add probability to the current probability since r is smaller than the sum of the probabilities
            proba += probadest.getValue();
            if (r <= proba)
                return probadest.getKey();
        }
        return 0;
    }

    public static double getSejProb(int s1,int s2,int numStates,double TS){
        Map<Integer, Node> graph = createGraph(numStates);

        double[] count = new double[numStates + 1];
        int state = s1;
        int n = 500000;

        for (int i = 0; i < n; i++) {
            state = s1;
            for (double j = 0; j < TS; j++)
                state = towerSampling(state, graph.get(state));
            // add one to the count of the destination state we found on the array count
            count[state] += 1;
        }

        // divide the count of the destination state by the number of iterations
        return count[s2] / n;
    }

    //##################################
    public static double getBiasTransProb(int s1, int s2,double[] ssprob)
    {
        Map<Integer, Node> graph = createGraph(ssprob.length);
        double prob_acceptance = 0.0;
        // Calculate the number of transition available
        double nb_transition = graph.get(s1).node_dest.size() + (graph.get(s1).prob_itself == 0 ? 0 : 1);

        // Case transition to itself
        if (s1 == s2) {
            // Case Probability to stay at the same place but the probability to stay at the same place is 0
            if (graph.get(s1).prob_itself == 0)
                return 0.0;
            else {
                // Calculate the probability to stay at the same place
                // Add the probability of transition of every possible transition and substract the result by 1
                for (Map.Entry<Integer, Node>  node : graph.get(s1).node_dest.entrySet()) {
                    // Divide the steady state probability of the source by the steady state probability of the destination and take the highest value between one and the result
                    // Divide the probability of acceptance by the number of transition available
                    prob_acceptance += Math.min(1, (ssprob[node.getKey() - 1] / ssprob[s1 - 1])) * (1 / nb_transition);
                }
                return 1 - prob_acceptance;
            }
        }

        // Case other state
        // Divide the steady state probability of the source by the steady state probability of the destination and take the highest value between one and the result
        prob_acceptance = Math.min(1, (ssprob[s2 - 1] / ssprob[s1 - 1]));
        // Divide the probability of acceptance by the number of transition available
        double prob_trans = prob_acceptance * (1 / nb_transition);
        return prob_trans;
    }


//##################################



    public static double  getContTransProb(int s1,int s2,double[] rates){
        // Can't go to the same state on Continious time
        if (s1 == s2)
            return 0.0;
        
        // Case first node divide the destination rate by the sum of the rates linked to the source
        if (s1 == 1) 
            return rates[s2 % 2] / (rates[0] + rates[1]);   
        
        // Case first node divide the destination rate by the sum of the rates linked to the source
        if (s1 == 2) {
            int rate_index = s2 == 1 ? 2 : 3;
            return rates[rate_index] / (rates[2] + rates[3]);   
        }

        // Case third node divide the destination rate by the sum of the rates linked to the source
        if (s1 == 3)
            return rates[s2 + 3] / (rates[4] + rates[5]);

        return 0.0;
    }



    private static Map<Integer, NodeCont> createGraphCont(double[] rates) 
    {
        Map<Integer, NodeCont> graph = new HashMap<Integer, NodeCont>();

        // Create Graph and add the rates to the other node and the probability to go to another state
        // I use HashMap to store the probability. I add them by ascending order because the values are returned in the order in which they were added.
        for (int i = 1; i <= 3; i++) {
            NodeCont NodeCont = new NodeCont();
            
            if (i == 1) {
                if (getContTransProb(1, 2, rates) < getContTransProb(1, 3, rates)) {
                    NodeCont.addProbaDest(3, getContTransProb(1, 3, rates));
                    NodeCont.addProbaDest(2, getContTransProb(1, 2, rates));
                }
                else {
                    NodeCont.addProbaDest(3, getContTransProb(1, 2, rates));
                    NodeCont.addProbaDest(2, getContTransProb(1, 3, rates));
                }
                NodeCont.addRateDest(2, rates[0]);
                NodeCont.addRateDest(3, rates[1]);
            }
            else if (i == 2) {
                if (getContTransProb(2, 1, rates) < getContTransProb(2, 3, rates)) {
                    NodeCont.addProbaDest(3, getContTransProb(2, 3, rates));
                    NodeCont.addProbaDest(2, getContTransProb(2, 1, rates));
                }
                else {
                    NodeCont.addProbaDest(3, getContTransProb(2, 1, rates));
                    NodeCont.addProbaDest(2, getContTransProb(2, 3, rates));
                }
                NodeCont.addRateDest(1, rates[2]);
                NodeCont.addRateDest(3, rates[3]);
            }
            else if (i == 3) {
                if (getContTransProb(3, 1, rates) < getContTransProb(3, 2, rates)) {
                    NodeCont.addProbaDest(3, getContTransProb(3, 2, rates));
                    NodeCont.addProbaDest(2, getContTransProb(3, 1, rates));
                }
                else {
                    NodeCont.addProbaDest(3, getContTransProb(3, 1, rates));
                    NodeCont.addProbaDest(2, getContTransProb(3, 2, rates));
                }
                NodeCont.addRateDest(1, rates[5]);
                NodeCont.addRateDest(2, rates[5]);
            }
            graph.put(i, NodeCont);
        }
        return graph;
    }

    private static int towerSamplingCont(NodeCont node) 
    {
        double r = Math.random();
        double proba = 0.0;

        // Find new state, I use TreeMap to sort the map by key
        for (Map.Entry<Integer, Double>  probadest : node.proba_dest.entrySet()) {
            // Add probability to the current probability since r is smaller than the sum of the probabilities
            proba += probadest.getValue();
            if (r <= proba) {
                return probadest.getKey();
            }
        }

        return 0;
    }

    public static double getContSejProb(int s1,int s2,double[] rates,double TSC){
        Map<Integer, NodeCont> graph = createGraphCont(rates);
        double[] count = new double[3 + 1];
        int state = s1;
        int n = 500000;
        double deltaT = 0.0;
        double r = 0.0;
        double sumRates = 0.0;


        for (int i = 0; i < n; i++) {
            state = s1;
            deltaT = 0.0;
            while (deltaT < TSC) {
                sumRates = 0.0;
                r = Math.random();

                for (Map.Entry<Integer, Double> node : graph.get(state).rate_dest.entrySet()) 
                    sumRates += node.getValue();

                // Calcule deltaT with the formula we have seen in the lecture and add it to the current time since deltaT is higher than TSC
                deltaT += -1 * ((1 / sumRates) * Math.log(r));
                state = towerSamplingCont(graph.get(state));
            }
            // add one to the count of the destination state we found on the array count
            count[state] += 1;
        }
        // divide the count of the destination state by the number of iterations
        return count[s2] / n;
    }




}//end class