
import java.util.ArrayList;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Yuri
 */
class Tnode {

    public double prob;
    public int state;

    public Tnode(double prob, int state) {
        this.prob = prob;
        this.state = state;
    }
}

public class Viterbi {

    HMM current;
    int numOfStates;
    int numOfObs;
    double[][] V;
    int[] o;

    public Viterbi(HMM current) {
        this.current = current;
        this.numOfStates = current.initial.length;
    }

    public ArrayList<Integer> getStateSeq(int[] o) {
        this.o = o;
        numOfObs = o.length;


        V = new double[numOfObs][numOfStates];
        ArrayList<Integer>[] path = (ArrayList<Integer>[]) new ArrayList[numOfStates];

        // initialize the paths
        for (int i = 0; i < numOfStates; i++) {
            path[i] = new ArrayList<>();
        }

        // initialize Viterbi
        for (int i = 0; i < numOfStates; i++) {
            V[0][i] = Math.log(current.initial[i]) + Math.log(current.emission[i][o[0]]);
            path[i].add(i);
        }

        // Run viterbi for t>0
        for (int t = 1; t < numOfObs; t++) {
            ArrayList<Integer>[] newpath = (ArrayList<Integer>[]) new ArrayList[numOfStates];

            // initialize the paths
            for (int i = 0; i < numOfStates; i++) {
                newpath[i] = new ArrayList<>();
            }


            // for each state
            for (int y = 0; y < numOfStates; y++) {
                Tnode max = getMaxState(t,y);
                V[t][y] = max.prob;
                newpath[y].addAll(path[max.state]);
                newpath[y].add(y);
            }
            path = newpath;
            
        }
        
        double maxProb = Double.NEGATIVE_INFINITY;
        int maxState = -1;
        for(int i=0; i<numOfStates; i++) {
            double currentProb = V[numOfObs-1][i];
            
            if(currentProb > maxProb) {
                maxProb = currentProb;
                maxState = i;
            }
        }

        return path[maxState];
    }

    private Tnode getMaxState(int t, int y) {
        
        double maxprob = Double.NEGATIVE_INFINITY;
        int maxstate =0;
        
        for(int y0=0; y0 < numOfStates; y0++) {
            double currentprob = V[t-1][y0] + Math.log(current.transition[y0][y]) + Math.log(current.emission[y][o[t]]);
            
            if(currentprob > maxprob) {
                maxprob = currentprob;
                maxstate = y0;
            }
            
        }
        
        return new Tnode(maxprob, maxstate);
    }
}
