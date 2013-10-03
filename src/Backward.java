/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Yuri
 */
public class Backward {

    HMM current;
    int numStates;

    public Backward(HMM current) {
        this.current = current;
        this.numStates = current.transition[0].length;
    }

    public double[][] backwardProc(int[] o) {
        int T = o.length;
        double[][] bwd = new double[numStates][T];
        /* Basisfall */
        for (int i = 0; i < numStates; i++) {
            bwd[i][T - 1] = 1;
        }
        /* Induktion */
        for (int t = T - 2; t >= 0; t--) {
            for (int i = 0; i < numStates; i++) {
                bwd[i][t] = 0;
                for (int j = 0; j < numStates; j++) {
                    bwd[i][t] += (bwd[j][t + 1] * current.transition[i][j] * current.emission[j][o[t + 1]]);
                }
            }
        }
        return bwd;
    }

    public double getProbability(int[] o) {
        double prob = 0.0;
        double[][] forward = this.backwardProc(o);
        //  add probabilities
        for (int i = 0; i < forward.length; i++) { // for every state
            prob += forward[i][0] * current.emission[i][o[0]] * current.initial[i];
        }
        return prob;
    }

    static double logplus(double plog, double qlog) {
        double max, diff;
        if (plog > qlog) {
            if (qlog == Double.NEGATIVE_INFINITY) {
                return plog;
            } else {
                max = plog;
                diff = qlog - plog;
            }
        } else {
            if (plog == Double.NEGATIVE_INFINITY) {
                return qlog;
            } else {
                max = qlog;
                diff = plog - qlog;
            }
        }
        // Now diff <= 0 so Math.exp(diff) will not overflow
        return max + (diff < -37 ? 0 : Math.log(1 + Math.exp(diff)));
    }
    
    public double[][] backwardProcTest(int[] o) {
        int T = o.length;
        double[][] bwd = new double[numStates][T];
        /* Basisfall */
        for (int i = 0; i < numStates; i++) {
            bwd[i][T - 1] = 0; // log(1)
        }
        /* Induktion */
        for (int t = T - 2; t >= 0; t--) {
            for (int i = 0; i < numStates; i++) {
                bwd[i][t] = Double.NEGATIVE_INFINITY;
                for (int j = 0; j < numStates; j++) {
                    bwd[i][t] = logplus(bwd[i][t], bwd[j][t + 1] + Math.log(current.transition[i][j]) + Math.log(current.emission[j][o[t + 1]]));
                }
            }
        }
        return bwd;
    }

    public double getProbabilityTest(int[] o) {
        double prob = Double.NEGATIVE_INFINITY;
        double[][] forward = this.backwardProcTest(o);
        //  add probabilities
        for (int i = 0; i < forward.length; i++) { // for every state
            prob = logplus(prob, forward[i][0] + Math.log(current.emission[i][o[0]]) + Math.log(current.initial[i]));
        }
        return Math.exp(prob);
    }
}
