
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Yuri
 */
public class Hmm1 {

    double[][] transition;
    double[][] emission;
    double[] initial;

    public Hmm1() {
    }

    private void parseTransition(ArrayList<String> b) {

        // Parse the transition matrix
        String[] tmp = b.get(0).split(" ");

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

    private void parseEmission(ArrayList<String> b) {

        // Parse the transition matrix
        String[] tmp = b.get(1).split(" ");

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

    private void parseInitial(ArrayList<String> b) {
        // Parse the transition matrix
        String[] tmp = b.get(2).split(" ");

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

    private String calculateNext() {
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
        
        StringBuilder out = new StringBuilder();
        out.append(1);
        out.append(" ");
        out.append(nextEmission.length);
        out.append(" ");
        
        for(double emissions : nextEmission) {
            out.append(emissions);
            out.append(" ");
        }
        
        return out.toString().trim();
    }

    // Read the lines
    public static void main(String[] args) throws IOException {
        Hmm1 hmm = new Hmm1();
        ArrayList<String> b = new ArrayList<>();

        String line;

        BufferedReader br = new BufferedReader(
                new InputStreamReader(System.in));

        while (!br.ready());
        while (br.ready()) {
            line = br.readLine();
            b.add(line);
        } // End while

        hmm.parseTransition(b);
        hmm.parseEmission(b);
        hmm.parseInitial(b);

        String out = hmm.calculateNext();
        System.out.println(out);


    }
}
