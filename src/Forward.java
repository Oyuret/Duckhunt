
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Yuri
 */
public class Forward {

    HMM current;
    int numStates;

    public Forward(HMM current) {
        this.current = current;
        this.numStates = current.transition[0].length;
    }

    private double[][] forwardProc(int[] o) {
        
        double[][] f = new double[numStates][o.length];
        for (int l = 0; l < f.length; l++) {
            f[l][0] = current.initial[l] * current.emission[l][o[0]];
        }
        for (int i = 1; i < o.length; i++) {
            for (int k = 0; k < f.length; k++) {
                double sum = 0;
                for (int l = 0; l < numStates; l++) {
                    sum += f[l][i - 1] * current.transition[l][k];
                }
                f[k][i] = sum * current.emission[k][o[i]];
            }
        }
        return f;
    }

    public double getProbability(int[] o) {
        double prob = 0.0;
        double[][] forward = this.forwardProc(o);
        //  add probabilities
        for (int i = 0; i < forward.length; i++) { // for every state
            prob += forward[i][forward[i].length - 1];
        }
        return prob;
    }
    
    
    private double[][] forwardProcTest(int[] o) {
        
        double[][] f = new double[numStates][o.length];
        for (int l = 0; l < f.length; l++) {
            f[l][0] = Math.log(current.initial[l]) + Math.log(current.emission[l][o[0]]);
        }
        for (int i = 1; i < o.length; i++) {
            for (int k = 0; k < f.length; k++) {
                double sum = Double.NEGATIVE_INFINITY;
                for (int l = 0; l < numStates; l++) {
                    sum = logplus(sum, f[l][i - 1] + Math.log(current.transition[l][k]));
                }
                f[k][i] = sum + Math.log(current.emission[k][o[i]]);
            }
        }
        return f;
    }

    public double getProbabilityTest(int[] o) {
        double prob = Double.NEGATIVE_INFINITY;
        double[][] forward = this.forwardProcTest(o);
        //  add probabilities
        for (int i = 0; i < forward.length; i++) { // for every state
            prob = logplus(prob, forward[i][forward[i].length - 1]);
        }
        return Math.exp(prob);
    }
    
    static double logplus(double plog, double qlog) {
    double max, diff;
    if (plog > qlog) {
      if (qlog == Double.NEGATIVE_INFINITY) {
            return plog;
        }
      else {
        max = plog; diff = qlog - plog;
      } 
    } else {
      if (plog == Double.NEGATIVE_INFINITY) {
            return qlog;
        }
      else {
        max = qlog; diff = plog - qlog;
      }
    }
    // Now diff <= 0 so Math.exp(diff) will not overflow
    return max + (diff < -37 ? 0 : Math.log(1 + Math.exp(diff)));
  }
}
