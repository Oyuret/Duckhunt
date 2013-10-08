/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Yuri
 */
public abstract class HMMAbstract {

    public double[][] transition;
    public double[][] emission;
    public double[] initial;
    public int[] sequence;
    //public double[][] gamma;

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

    public String printTransition() {
        StringBuilder transitionout = new StringBuilder();

        // Add the dimensions
        transitionout.append(transition.length);
        transitionout.append(" ");
        transitionout.append(transition[0].length);
        transitionout.append(" ");

        for (double[] row : transition) {
            for (double column : row) {
                transitionout.append(column);
                transitionout.append(" ");
            }
        }

        // Remove the last space
        transitionout.deleteCharAt(transitionout.length() - 1);

        return transitionout.toString();
    }
    
    public String printEmission() {
        StringBuilder transitionout = new StringBuilder();

        // Add the dimensions
        transitionout.append(emission.length);
        transitionout.append(" ");
        transitionout.append(emission[0].length);
        transitionout.append(" ");

        for (double[] row : emission) {
            for (double column : row) {
                transitionout.append(column);
                transitionout.append(" ");
            }
        }

        // Remove the last space
        transitionout.deleteCharAt(transitionout.length() - 1);

        return transitionout.toString();
    }
    
    public double logplus(double plog, double qlog) {
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
        //return max + (diff < -37 ? 0 : Math.log(1 + Math.exp(diff)));
        return max + Math.log(1+Math.exp(diff));
    }
    
    public double BaumWelch() {
        int N = initial.length;
        int M = emission[0].length;
        int T = sequence.length;


        // [state][time]
        double[][] alpha = new double[N][T];
        double[] c = new double[T];

        //alpha pass

        // compute alpha[i][0]
        c[0] = 0;
        for (int i = 0; i < N; i++) {
            alpha[i][0] = initial[i] * emission[i][sequence[0]];
            c[0] = c[0] + alpha[i][0];
        }

        // scale the alpha[i][0]
        c[0] = 1.0 / c[0];
        for (int i = 0; i < N; i++) {
            alpha[i][0] = c[0] * alpha[i][0];
        }

        // compute alpha[i][t]
        for (int t = 1; t < T; t++) {
            c[t] = 0;
            for (int i = 0; i < N; i++) {
                alpha[i][t] = 0;
                for (int j = 0; j < N; j++) {
                    alpha[i][t] = alpha[i][t] + alpha[j][t - 1] * transition[j][i];
                }
                alpha[i][t] = alpha[i][t] * emission[i][sequence[t]];
                c[t] = c[t] + alpha[i][t];
            }

            // scale alpha[i][t]
            c[t] = 1.0 / c[t];
            for (int i = 0; i < N; i++) {
                alpha[i][t] = c[t] * alpha[i][t];
            }
        }

        // beta pass
        // [State][time]
        double[][] beta = new double[N][T];

        // Let beta[i][T-1] =1 scaled by c[T-1]
        for (int i = 0; i < N; i++) {
            beta[i][T - 1] = c[T - 1];
        }

        // Beta-pass
        for (int t = T - 2; t >= 0; t--) {
            for (int i = 0; i < N; i++) {
                beta[i][t] = 0;
                for (int j = 0; j < N; j++) {
                    beta[i][t] = beta[i][t] + (transition[i][j] * emission[j][sequence[t + 1]] * beta[j][t + 1]);
                }
                // scale the beta[i][t] with same scale factor as alpha[i][t]
                beta[i][t] = c[t] * beta[i][t];
            }
        }

        // compute gamma and digamma
        double[][] gamma = new double[N][T]; // [State][Time]
        double[][][] digamma = new double[N][N][T]; // [State][State][Time]

        for (int t = 0; t < T - 1; t++) {
            double denom = 0;
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    denom = denom + alpha[i][t] * transition[i][j] * emission[j][sequence[t + 1]] * beta[j][t + 1];
                }
            }
            for (int i = 0; i < N; i++) {
                gamma[i][t] = 0;
                for (int j = 0; j < N; j++) {
                    digamma[i][j][t] = (alpha[i][t] * transition[i][j] * emission[j][sequence[t + 1]] * beta[j][t + 1]) / denom;
                    gamma[i][t] = gamma[i][t] + digamma[i][j][t];
                }
            }
        }

//        // calculate the last gamma
//        for(int i=0; i<N; i++) {
//            double numer = alpha[i][T-1] * beta[i][T-1];
//            double denom = 0;
//            for(int j=0; j<N; j++) {
//                denom = denom + alpha[j][T-1];
//            }
//            
//            gamma[i][T-1] = numer / denom;
//        }

        // re-estimate pi
        for (int i = 0; i < N; i++) {
            initial[i] = gamma[i][0];
        }

        // re-estimate A
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                double numer = 0;
                double denom = 0;
                for (int t = 0; t < T - 1; t++) {
                    numer = numer + digamma[i][j][t];
                    denom = denom + gamma[i][t];
                }
                transition[i][j] = numer / denom;
            }
        }

        // re-estimate B
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                double numer = 0;
                double denom = 0;
                for (int t = 0; t < T - 1; t++) {
                    if (sequence[t] == j) {
                        numer = numer + gamma[i][t];
                    }
                    denom = denom + gamma[i][t];
                }
                emission[i][j] = numer / denom;

            }
        }

        double logprob = 0;
        for (int i = 0; i < T; i++) {
            logprob = logprob + Math.log(c[i]);
        }

        return -logprob;

    }
}
