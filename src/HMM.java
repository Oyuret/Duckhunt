

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Yuri
 */
public class HMM {

    public double[][] transition;
    public double[][] emission;
    public double[] initial;
    public int[] sequence;

    public HMM() {
    }

    public void parseTransition(String b) {

        // Parse the transition matrix
        String[] tmp = b.split(" ");

        // Create the transistion matrix
        transition = new double[Integer.parseInt(tmp[0])][Integer.parseInt(tmp[1])];

        // Add each value to the transition matrix
        int rows = Integer.parseInt(tmp[0]);
        int columns = Integer.parseInt(tmp[1]);

        // Remove the unnecesary lines
        String[] transNumbers = new String[tmp.length - 2];

        for (int i = 2; i < tmp.length; i++) {
            transNumbers[i - 2] = tmp[i];
        }

        // add the numbers to the transition matrix
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                transition[i][j] = Double.parseDouble(transNumbers[(i * columns) + j]);
            }
        }

    }

    public void parseEmission(String b) {

        // Parse the transition matrix
        String[] tmp = b.split(" ");

        // Create the transistion matrix
        emission = new double[Integer.parseInt(tmp[0])][Integer.parseInt(tmp[1])];

        // Add each value to the transition matrix
        int rows = Integer.parseInt(tmp[0]);
        int columns = Integer.parseInt(tmp[1]);

        // Remove the unnecesary lines
        String[] emissionNumbers = new String[tmp.length - 2];

        for (int i = 2; i < tmp.length; i++) {
            emissionNumbers[i - 2] = tmp[i];
        }

        // add the numbers to the transition matrix
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                emission[i][j] = Double.parseDouble(emissionNumbers[(i * columns) + j]);
            }
        }

    }

    public void parseInitial(String b) {
        // Parse the transition matrix
        String[] tmp = b.split(" ");

        // Create the transistion matrix
        initial = new double[Integer.parseInt(tmp[1])];

        // Add each value to the transition matrix
        int columns = Integer.parseInt(tmp[1]);

        // Remove the unnecesary lines
        String[] initialNumbers = new String[tmp.length - 2];

        for (int i = 2; i < tmp.length; i++) {
            initialNumbers[i - 2] = tmp[i];
        }

        // add the numbers to the transition matrix
        for (int i = 0; i < columns; i++) {
            initial[i] = Double.parseDouble(initialNumbers[i]);
        }
    }

    public void parseSequence(String b) {
        // Parse the transition matrix
        String[] tmp = b.split(" ");

        // Create the transistion matrix
        sequence = new int[Integer.parseInt(tmp[0])];

        // Add each value to the transition matrix
        int columns = Integer.parseInt(tmp[0]);

        // Remove the unnecesary lines
        String[] initialNumbers = new String[tmp.length - 1];

        for (int i = 1; i < tmp.length; i++) {
            initialNumbers[i - 1] = tmp[i];
        }

        // add the numbers to the transition matrix
        for (int i = 0; i < columns; i++) {
            sequence[i] = Integer.parseInt(initialNumbers[i]);
        }
    }

    private double[][] forwardProc() {

        int numStates = transition[0].length;

        double[][] f = new double[numStates][sequence.length];
        for (int l = 0; l < f.length; l++) {
            f[l][0] = Math.log(initial[l]) + Math.log(emission[l][sequence[0]]);
        }
        for (int i = 1; i < sequence.length; i++) {
            for (int k = 0; k < f.length; k++) {
                double sum = Double.NEGATIVE_INFINITY;
                for (int l = 0; l < numStates; l++) {
                    sum = logplus(sum, f[l][i - 1] + Math.log(transition[l][k]));
                }
                f[k][i] = sum + Math.log(emission[k][sequence[i]]);
            }
        }
        return f;
    }

    public double getForwardProb() {
        double prob = Double.NEGATIVE_INFINITY;
        double[][] forward = forwardProc();
        //  add probabilities
        for (int i = 0; i < forward.length; i++) { // for every state
            prob = logplus(prob, forward[i][forward[i].length - 1]);
        }
        return Math.exp(prob);
    }

    public double[][] backwardProc() {
        int numStates = transition[0].length;

        int T = sequence.length;
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
                    bwd[i][t] = logplus(bwd[i][t], bwd[j][t + 1]
                            + Math.log(transition[i][j])
                            + Math.log(emission[j][sequence[t + 1]]));
                }
            }
        }
        return bwd;
    }

    public double getBackWardProb() {

        double prob = Double.NEGATIVE_INFINITY;
        double[][] backward = backwardProc();
        //  add probabilities
        for (int i = 0; i < backward.length; i++) { // for every state
            prob = logplus(prob, backward[i][0]
                    + Math.log(emission[i][sequence[0]])
                    + Math.log(initial[i]));
        }
        return Math.exp(prob);
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

    public double[][] getGamma() {
        double[][] a = forwardProc();
        double[][] b = backwardProc();

        // [state][time]
        double[][] gamma = new double[initial.length][sequence.length];

        // for every time in the sequence
        for (int t = 0; t < sequence.length; t++) {
            double normalizer = Double.NEGATIVE_INFINITY;

            // for every state
            for (int i = 0; i < initial.length; i++) {
                gamma[i][t] = a[i][t] + b[i][t];
                normalizer = logplus(normalizer, gamma[i][t]);
            }

            // for every state
            normalizer = -normalizer;
            for (int i = 0; i < initial.length; i++) {
                gamma[i][t] = gamma[i][t] + normalizer;
            }

        }


        return gamma;
    }

    public double[][][] getDigamma() {
        // [i][j][time]
        double[][][] digamma = new double[initial.length][initial.length][sequence.length];
        double[][] alpha = forwardProc();
        double[][] beta = backwardProc();

        for (int t = 0; t < (sequence.length - 1); t++) {
            double normalizer = Double.NEGATIVE_INFINITY;

            for (int i = 0; i < initial.length; i++) {
                for (int j = 0; j < initial.length; j++) {
                    double test;
                    test = (Math.log(emission[j][sequence[t + 1]]) + beta[j][t + 1]);
                    test = test + Math.log(transition[i][j]);
                    test = test + alpha[i][t];
                    digamma[i][j][t] = test;
                    normalizer = logplus(normalizer, digamma[i][j][t]);
                }
            }
            
            normalizer = -normalizer;

            for (int i = 0; i < initial.length; i++) {
                for (int j = 0; j < initial.length; j++) {
                    
                    digamma[i][j][t] = digamma[i][j][t] + normalizer;
                }
            }
        }

        return digamma;
    }

    public void train() {
        double[][] gamma = getGamma();
        double[][][] digamma = getDigamma();


        for (int i = 0; i < initial.length; i++) {
            initial[i] = Math.exp(gamma[0][i]);
        }
        
        //System.out.println();


        // update transition
        for (int i = 0; i < initial.length; i++) {
            for (int j = 0; j < initial.length; j++) {
                double numerator = Double.NEGATIVE_INFINITY;
                double denominator = Double.NEGATIVE_INFINITY;
                for (int t = 0; t < (sequence.length - 1); t++) {
                    numerator = logplus(numerator, digamma[i][j][t]);
                    denominator = logplus(denominator, gamma[i][t]);
                }
                denominator = -denominator;
                transition[i][j] = Math.exp(numerator + denominator);
            }
        }
        
        // update emission
        for (int j = 0; j < initial.length; j++) {
            for (int k = 0; k < emission[0].length; k++) {
                double numerator = Double.NEGATIVE_INFINITY;
                double denominator = Double.NEGATIVE_INFINITY;
                
                for(int t=0; t< sequence.length; t++) {
                   if(sequence[t] == k) {
                       numerator = logplus(numerator, gamma[j][t]);
                   }
                   denominator = logplus(denominator, gamma[j][t]);
                }
                denominator = -denominator;
                emission[j][k] = Math.exp(numerator + denominator);
            }
        }

        
    }
}
