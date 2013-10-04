

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Yuri
 */
public class HMM extends HMMAbstract{


    public HMM() {
    }

    public void forwardProc() {

        int numStates = transition[0].length;

        alpha = new double[numStates][sequence.length];
        
        // Basfall
        c = new double[sequence.length];
        c[0] = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < numStates; i++) {
            alpha[i][0] = initial[i] + emission[i][sequence[0]];
            c[0] = logplus(c[0], alpha[i][0]);
        }
        
        //Scale basfall
        c[0] = 0 - c[0];
        
        for(int i = 0; i < numStates; i++) {
            alpha[i][0] = c[0]+alpha[i][0];
        }
        
        //För alla andra
        for (int t = 1; t < sequence.length; t++) {
            c[t] = Double.NEGATIVE_INFINITY;
            
            for (int i = 0; i < numStates; i++) {
                
                alpha[i][t] = Double.NEGATIVE_INFINITY;
                
                for (int j = 0; j < numStates; j++) {
                    alpha[i][t] = logplus(alpha[i][t], (alpha[j][t - 1] + transition[j][i]));
                }
                alpha[i][t] = alpha[i][t] + emission[i][sequence[t]];
                c[t] = logplus(c[t], alpha[i][t]);
                
            }
            
            c[t] = 0 - c[t];
            for(int i=0; i< numStates; i++) {
                alpha[i][t] = c[t] + alpha[i][t];
            }
        }
        //return alpha;
    }

    public double getForwardProb() {
        double prob = Double.NEGATIVE_INFINITY;
        forwardProc();
        //  add probabilities
        for (int i = 0; i < alpha.length; i++) { // for every state
            prob = logplus(prob, alpha[i][alpha[i].length - 1]);
        }
        return Math.exp(prob);
    }

    public void backwardProc() {
        int numStates = transition[0].length;

        int T = sequence.length;
        beta = new double[numStates][T];
        /* Basisfall */
        for (int i = 0; i < numStates; i++) {
            beta[i][T - 1] = c[T-1];
        }
        /* Induktion */
        for (int t = T - 2; t >= 0; t--) {
            for (int i = 0; i < numStates; i++) {
                beta[i][t] = Double.NEGATIVE_INFINITY;
                
                for (int j = 0; j < numStates; j++) {
                    beta[i][t] = logplus(beta[i][t], (beta[j][t + 1]
                            + transition[i][j]
                            + emission[j][sequence[t + 1]]));
                }
                beta[i][t] = c[t]+beta[i][t];
            }
        }
        //return bwd;
    }

    public double getBackWardProb() {

        double prob = Double.NEGATIVE_INFINITY;
        backwardProc();
        //  add probabilities
        for (int i = 0; i < beta.length; i++) { // for every state
            prob = logplus(prob, beta[i][0]
                    + Math.log(emission[i][sequence[0]])
                    + Math.log(initial[i]));
        }
        return Math.exp(prob);
    }


    public void calculateGammaDiGamma() {

        // [state][time]
        gamma = new double[initial.length][sequence.length];
        digamma = new double[initial.length][initial.length][sequence.length];
        
        
        for(int t=0; t < (sequence.length-1); t++) {
            double denom = Double.NEGATIVE_INFINITY;
            
            for(int i=0; i<initial.length; i++) {
                for(int j=0; j< initial.length; j++) {
                    denom = logplus(denom, alpha[i][t]+transition[i][j]+emission[j][sequence[t+1]]+beta[j][t+1]);
                }
            }
            
            for(int i=0; i< initial.length; i++) {
                gamma[i][t] = Double.NEGATIVE_INFINITY;
                for(int j=0; j< initial.length; j++) {
                    digamma[i][j][t] = (alpha[i][t]+transition[i][j]+emission[j][sequence[t+1]]+beta[j][t+1]) - denom;
                    gamma[i][t] = logplus(gamma[i][t], digamma[i][j][t]);
                }
            }
        }
    }

    public void train() {
        forwardProc();
        backwardProc();
        calculateGammaDiGamma();


        for (int i = 0; i < initial.length; i++) {
            initial[i] = gamma[i][0];
        }

        System.out.println();


        // update transition
        for (int i = 0; i < initial.length; i++) {
            for (int j = 0; j < initial.length; j++) {
                double numerator = Double.NEGATIVE_INFINITY;
                double denominator = Double.NEGATIVE_INFINITY;
                for (int t = 0; t < (sequence.length - 1); t++) {
                    numerator = logplus(numerator, digamma[i][j][t]);
                    denominator = logplus(denominator, gamma[i][t]);
                }
                transition[i][j] = numerator - denominator;
            }
        }

        // update emission
        for (int i = 0; i < initial.length; i++) {
            for (int j = 0; j < emission[0].length; j++) {
                double numerator = Double.NEGATIVE_INFINITY;
                double denominator = Double.NEGATIVE_INFINITY;

                for (int t = 0; t < (sequence.length-1); t++) {
                    if (sequence[t] == j) {
                        numerator = logplus(numerator, gamma[i][t]);
                    }
                    denominator = logplus(denominator, gamma[i][t]);
                }
                emission[i][j] = numerator - denominator;
            }
        }
    }
    
    public double test() {
        double logprob = 0;
        
        for(int i=0; i< sequence.length; i++) {
            logprob = logprob + c[i];
        }
        
        logprob = -logprob;
        return logprob;
    }
}
