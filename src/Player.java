
import java.util.ArrayList;

class Player {
    // /constructor

    int numOfBirds;
    double hmmStability = 0.0;
    double moveStability = 0.70;

    // /There is no data in the beginning, so not much should be done here.
    public Player() {
    }

    /**
     * Shoot!
     *
     * This is the function where you start your work.
     *
     * You will receive a variable pState, which contains information about all
     * birds, both dead and alive. Each birds contains all past actions.
     *
     * The state also contains the scores for all players and the number of time
     * steps elapsed since the last time this function was called.
     *
     * @param pState the GameState object with observations etc
     * @param pDue time before which we must have returned
     * @return the prediction of a bird we want to shoot at, or cDontShoot to
     * pass
     */
    public Action shoot(GameState pState, Deadline pDue) {
        /*
         * Here you should write your clever algorithms to get the best action.
         * This skeleton never shoots.
         */
        if(pState.getBird(0).getSeqLength() < 50) return cDontShoot;
        
        numOfBirds = pState.getNumBirds();
        ArrayList<HMM> birds = new ArrayList<>();
        double[] birdStability = new double[numOfBirds];

        // create an HMM for each bird
        for (int i = 0; i < numOfBirds; i++) {
            int numOfObs = pState.getBird(i).getSeqLength();
            int[] seq = new int[numOfObs];
            for (int j = 0; j < numOfObs; j++) {
                seq[j] = pState.getBird(i).getObservation(j);
            }

            birds.add(new HMM(Initials.initialTransition, Initials.initialEmission, Initials.initialInitial, seq));
        }


        // train each bird
        for (int i = 0; i < numOfBirds; i++) {
            if (pState.getBird(i).isAlive()) {
                birdStability[i] = birds.get(i).train();
            }

        }

        // Pick the most stable hmm
        int mostStable = -1;
        double stability = 0;
        for (int i = 0; i < numOfBirds; i++) {
            if (pState.getBird(i).isAlive() && birdStability[i] > stability) {
                stability = birdStability[i];
                mostStable = i;
            }
        }

        // If this hmm was stable enough
        if (stability > hmmStability) {
            double[] emissions = birds.get(mostStable).getMostProbableEmission();

            // Check which emission is the most probable
            int mostProbable = -1;
            double probability = Double.NEGATIVE_INFINITY;
            for (int i = 0; i < Constants.COUNT_MOVE; i++) {
                if (emissions[i] > probability) {
                    mostProbable = i;
                    probability = emissions[i];
                }
            }

            // Check how accurate we are
            if (probability >= moveStability) {
                return new Action(mostStable, mostProbable);
            } else {
                return cDontShoot;
            }


        } else {
            // This line choose not to shoot
            return cDontShoot;
        }



        // This line would predict that bird 0 will move right and shoot at it
        // return Action(0, MOVE_RIGHT);
    }

    /**
     * Guess the species! This function will be called at the end of each round,
     * to give you a chance to identify the species of the birds for extra
     * points.
     *
     * Fill the vector with guesses for the all birds. Use SPECIES_UNKNOWN to
     * avoid guessing.
     *
     * @param pState the GameState object with observations etc
     * @param pDue time before which we must have returned
     * @return a vector with guesses for all the birds
     */
    public int[] guess(GameState pState, Deadline pDue) {
        /*
         * Here you should write your clever algorithms to guess the species of
         * each bird. This skeleton makes no guesses, better safe than sorry!
         */

        int[] lGuess = new int[pState.getNumBirds()];
        for (int i = 0; i < pState.getNumBirds(); ++i) {
            lGuess[i] = Constants.SPECIES_UNKNOWN;
        }
        return lGuess;
    }

    /**
     * If you hit the bird you were trying to shoot, you will be notified
     * through this function.
     *
     * @param pState the GameState object with observations etc
     * @param pBird the bird you hit
     * @param pDue time before which we must have returned
     */
    public void hit(GameState pState, int pBird, Deadline pDue) {
        System.err.println("HIT BIRD!!!");
    }

    /**
     * If you made any guesses, you will find out the true species of those
     * birds through this function.
     *
     * @param pState the GameState object with observations etc
     * @param pSpecies the vector with species
     * @param pDue time before which we must have returned
     */
    public void reveal(GameState pState, int[] pSpecies, Deadline pDue) {
    }
    public static final Action cDontShoot = new Action(-1, -1);
}
