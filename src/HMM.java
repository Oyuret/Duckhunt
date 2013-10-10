
import java.util.ArrayList;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Yuri
 */
public class HMM extends HMMAbstract {

    public HMM(String transition, String emission, String initial, int[] sequence) {
        parseInitial(initial);
        parseEmission(emission);
        parseTransition(transition);
        this.sequence = sequence;
    }
    
    public HMM() {
        
    }
    
    public HMM(double[][] transition, double[][] emission, double[] initial, int[] sequence) {
        this.transition = transition;
        this.emission = emission;
        this.initial = initial;
        this.sequence = sequence;
    }

    public double train() {
        int maxIters = 50;
        int iters = 0;
        double oldLogProb = Double.NEGATIVE_INFINITY;
        double logprob = BaumWelch();

        while (iters < maxIters && logprob > oldLogProb) {
            oldLogProb = logprob;
            logprob = BaumWelch();
            iters++;
        }
        
        return Math.exp(logprob);
    }

    public double[] getMostProbableEmission() {
        int N = initial.length;
        Viterbi vit = new Viterbi(this);
        ArrayList<Integer> states = vit.getStateSeq(sequence);

        int mostProbState = states.get(states.size() - 1);
        double[] initial = new double[N];
        initial[mostProbState] = 1.0;

        double[] nextEmission = new double[emission[0].length];

        for (int emissionIndex = 0; emissionIndex < emission[0].length; emissionIndex++) {

            for (int state = 0; state < initial.length; state++) {

                // get the correct row in the state transition matrix
                double[] transRow = transition[state];

                // for each probability in the row
                for (int nextState = 0; nextState < transRow.length; nextState++) {
                    nextEmission[emissionIndex] += initial[state] * transition[state][nextState] * emission[nextState][emissionIndex];
                }

            }
        }
        return nextEmission;
    }
}
