import java.util.*;
import java.util.stream.*;
import java.util.Arrays;
import java.util.Iterator;


class Player {

  // Used to keep track of how much time has passed in a game round.
  int time;

  // An action event that we return to signal that we do not want to try to shoot
  // a specific bird at a specific time step.
  public static final Action cDontShoot = new Action(-1, -1);

  //We wanna save all HMM models for every bird species. Therefore we create a list with
  // a length corresponding to the number of species and place a dynamic matrix at each
  // position in the array. In these dynamic matricies we store every HMM that we have trained
  // for each bird that we have seen previously.
  public ArrayList[] birdModelsHMM;

  // The number of states i.e. possible flight patterns and the number of emissions
  // i.e. possible bird moves. A possible bird move is to fly: up, down, right, left,
  // south west, south east, north west, north east and to not move at all.
  int noStates;
  int noEmissions;

  // Used for storing our guesses of the birds that are in the skies species at each round.
  int [] birdSpeciesGuesses;

  // Parameters used when shooting birds.
  double [] logProbsBirds;
  HMM [] optimalBirdsHMMs;
  int [] optimalBirdsSpieces;
  int maxLogProb, optimalBirdToShoot;
  double maxProbNextEmission;

  HelperClass helperMethods = new HelperClass();


    public Player() {
      // Initlizing parameters of the HMMs.
      noEmissions = 9;
      noStates = 5;

      birdModelsHMM = new ArrayList[6];


      // Setting the time of the game to zero.
      time = 0;

      // Storing a dynamic list at each position in the bird species HMM array
      // which is explained in more detail above.
      for ( int j = 0; j <Constants.COUNT_SPECIES; j ++ ){
        birdModelsHMM[j]= new ArrayList<HMM>();
      }

  }


  public double [] removeStorksUnknownBirds (int [] optimalBirdsSpieces, double [] logProbsBirds, int idxLargestVal ){
    /*
      Remove birds that are unknown or black storks.
    */

    boolean isBlackStork, isUnknown ;

    for( int i  = 0; i < optimalBirdsSpieces.length ; i ++ ){

      isUnknown = ( optimalBirdsSpieces[idxLargestVal] == Constants.SPECIES_UNKNOWN );
      isBlackStork = ( optimalBirdsSpieces[idxLargestVal] == Constants.SPECIES_BLACK_STORK );

      // Set these to zero.
      if ( isUnknown || isBlackStork ){

        logProbsBirds[idxLargestVal] = 0;
      }
    }
    return logProbsBirds;
  }


  public int getBirdLargetProb( double [] logProbsBirds , int [] optimalBirdsSpieces ){
    /*
      Finds the bird which past observations fits our saved HMM models for
    */

    int idxLargestVal;

    // Remove birds that are unknown or black storks.
    logProbsBirds = removeStorksUnknownBirds (optimalBirdsSpieces, logProbsBirds, helperMethods.indexLargestLogVal(logProbsBirds) );

    idxLargestVal = helperMethods.indexLargestLogVal(logProbsBirds);

    return idxLargestVal;
  }


  public int retriveOptimalBirdToShoot ( int noBirds, GameState pState, int birdSeqLengthLimit, double maxLogProb ) {

    /*
      For every single bird in the sky we want to figure out which one we have the
      best HMM stored to figure out where it will fly next. Therefore we calculate
      the probability of the past observations using our stored HMM:s. The one that
      achives the highest probability is our best shoot.
    */

    int noBirdObs, optimalBirdToShoot;
    boolean isBirdAlive, isCertainShotWillHit;

    double probObsSeqLog;

    Iterator<HMM> speciesHMMiterator;
    ArrayList<Double> lopProbBirdModelPicker;

    HMM birdHmm;
    int [] observationSequence;


    for( int bird = 0; bird < noBirds; ++ bird ){

      noBirdObs = pState.getBird( bird ).getSeqLength();
      isBirdAlive = pState.getBird( bird ).isAlive();

      // We only wanna try to shoot the bird if it is alive and if we have enough
      // number of observations from it.
      if( isBirdAlive && noBirdObs > birdSeqLengthLimit ){

        // Extract the birds observation sequence.
        observationSequence =  getBirdObservationSequence ( noBirdObs, bird, pState);

        // We find the bird in the sky which we currently are most sure where
        // it will fly next.

        for( int species = 0; species < Constants.COUNT_SPECIES; species ++ ){

          // An iterator over All previously trained HMMs for a certain species.
          speciesHMMiterator = birdModelsHMM[ species ].iterator();

          lopProbBirdModelPicker = new ArrayList <Double>();


          // For all trained HMM:s for the current species we calculate the probability
          // of getting the current observation sequence given this specific HMM model.
          while ( speciesHMMiterator.hasNext() ){

            // Retrive the first HMM for this species.
            birdHmm = speciesHMMiterator.next();

            // Calculate the conditional probability of the observation.
            probObsSeqLog = birdHmm.alphaPassProb( observationSequence );

            // Determine if we are certain enough to try to shoot the bird.
            isCertainShotWillHit = probObsSeqLog > maxLogProb;
            if( isCertainShotWillHit ){

              // If we are certain enough to shoot the bird we store information
              // about it.

              // Update the naximum found probability value.
              maxLogProb = probObsSeqLog;

              // Update the logged probability for the current birds observation sequence
              // and current species.
              logProbsBirds[bird] = probObsSeqLog;
              // Save the birds species.
              optimalBirdsSpieces[bird] = species;
              // Save the birds HMM.
              optimalBirdsHMMs[bird] = birdHmm;

            }
          }
        }
      }
    }

    // Retrive the bird with past emissions with the highest probability.
    optimalBirdToShoot = getBirdLargetProb( logProbsBirds, optimalBirdsSpieces );

    return optimalBirdToShoot;
  }


  public double [][] getStateProbDistLastTimeStep( double [][] probObsSeq, HMM birdHmm ){

    double [] stateProbLastTimeStep;
    double [][] stateProbLastTimeStepMatrix;

    // Probability distribution fro the last time step is retrived.
    stateProbLastTimeStep = helperMethods.getColumn( probObsSeq, probObsSeq[0].length - 1);

    // We need this array in a matrix format to multiply it with
    stateProbLastTimeStepMatrix = new double [1][ stateProbLastTimeStep.length ];

    for( int d = 0; d < stateProbLastTimeStepMatrix.length; d ++ ){

      for( int k = 0; k < stateProbLastTimeStepMatrix[d].length; k ++ ){

        stateProbLastTimeStepMatrix[d][k] = stateProbLastTimeStep[k];

      }
    }
    return stateProbLastTimeStepMatrix;
  }


  public double [][] computeProbabilityNextTimeStep ( int noEmissions, GameState pState, int noBirdObs, int optimalBirdToShoot, int optimalBirdSpecies) {
    /*
      Computes the probability distribution for the next time step based on previous observations.
    */

    int [] observationSequence;
    double [][] probNextState, birdAlfaPass, stateProbLastTimeStep, probObsSeq;
    HMM birdHmm;

    // Extract the birds observation sequence.
    observationSequence =  getBirdObservationSequence ( noBirdObs, optimalBirdToShoot, pState);

    // Retrive the HMM for the bird we want to shoot.
    birdHmm = optimalBirdsHMMs[ optimalBirdToShoot ];

    // Calculate the probability based on the past observations for all time steps.
    probObsSeq = birdHmm.alphaPassScaling( observationSequence );

    // Get the distribution for the last time step.
    stateProbLastTimeStep = getStateProbDistLastTimeStep( probObsSeq, birdHmm);

    // Matrix multiplication between the transition matrix ans the state probability distribution
    // for the last time step.
    probNextState = helperMethods.matrixMultiplication( stateProbLastTimeStep, birdHmm.transitionMatrix );

    return probNextState;
  }


  public Action shootOrHold( double maxProbNextEmission, int optimalBirdToShoot, int maxNextEmission, double probCertaintyLimit ){
    /*
      Deciding of we should shoot or not at this specific time step.
    */

    boolean certainEnoughToShoot;

    // If we are certain enough about where this specific bird will fly to at the
    // next time step then we make a shoot. Otherwise, we hold our horses!

    certainEnoughToShoot = ( maxProbNextEmission > probCertaintyLimit );

    if ( certainEnoughToShoot ) {

      return new Action(optimalBirdToShoot, maxNextEmission);

    }else{

      return cDontShoot;
    }
  }

  public double [] computeEmissionProbNextState( int noEmissions, GameState pState, int noBirdObs, int optimalBirdToShoot, int optimalBirdSpecies ){


        int [] observstionSequence;
        double [][] birdAlfaPass, StateProbLastTimeStepMatrix, probNextState;
        double [] emissionDistributionNextState;
        HMM birdHmm;
        int maxnextemission;
        double maxprobnextemission;

        // Get the observation sequence for the current bird that we are trying to classify.
        observstionSequence = getBirdObservationSequence ( noBirdObs, optimalBirdToShoot, pState );

        maxnextemission = 0;
        maxprobnextemission = 0;

        // A HMM used for this specific bird.
        birdHmm = optimalBirdsHMMs[optimalBirdToShoot];

        // Calculate the probability of the specific observation sequence
        // from the bird.
        probObservations = birdHmm.alfapassScaling( observstionSequence );

        // Get the state probability distribution for the latest observed timestep.
        stateProbT = getStateProbDistLastTimeStep( probObservations, birdHmm );

        // Estimate the probability distribution for the next time step.
        probNextState = helperMethods.matrixMultiplication( stateProbT, birdHmm.transitionMatrix );

        // Compute emission distribution fo the next time step.
        emissionDistributionNextState = helperMethods.matrixMultiplication( probNextState, birdHmm.emissionMatrix )[0];

        return emissionDistributionNextState;

  }


  public int mostProbableNextEmission( GameState pState, int noBirdObs, int optimalBirdToShoot, int optimalBirdSpecies ){

    double [] emissionDistributionNextState;
    int no, maxNextEmission, nextemission;
    double maxProbNextEmission, probNextEmission;
    boolean foundBetterBirdToShoot;

    maxNextEmission = 0;
    maxProbNextEmission = 0;

    // The emission distribution for the next state.
    emissionDistributionNextState = computeEmissionProbNextState ( noEmissions, pState, noBirdObs, optimalBirdToShoot, optimalBirdSpecies );

    // The most probable next emission and its index.
    probNextEmission = helperMethods.maxFinder( emissionDistributionNextState );
    nextemission = helperMethods.getIndexOfLargest( emissionDistributionNextState );

    // If we found a better bird to shoot, i.e. one that we are more certain
    // that we will hit, we update which one we will shoot at this time step.
    foundBetterBirdToShoot = ( probNextEmission > maxProbNextEmission );

    if( foundBetterBirdToShoot ){

      maxProbNextEmission = probNextEmission;
      maxNextEmission = nextemission;

    }

    return maxNextEmission;
  }


  public Action shoot( GameState pState, Deadline pDue ) {
    /**
    * Shoot!
    *
    * This is the function where you start your work.
    *
    * You will receive a variable pState, which contains information about all
    * birds, both dead and alive. Each bird contains all past moves.
    *
    * The state also contains the scores for all players and the number of
    * time steps elapsed since the last time this function was called.
    *
    * @param pState the GameState object with observations etc
    * @param pDue time before which we must have returned
    * @return the prediction of a bird we want to shoot at, or cDontShoot to pass
    */


    time ++;

    int timeLimit, roundMargin, birdSeqLengthLimit, noBirdObs, noBirds;
    boolean roundMarginMeet, timeLimitMeet;
    boolean isBirdAlive;

    // Information about the birt that we are most certain about at a specific
    // time step.
    int optimalBirdToShoot, optimalBirdSpecies;
    int [] observationSequence;
    boolean isNotBlackStork, isNotUnknown, certainPreviousFligtPattern;
    double maxLogProb;


    double probCertaintyLimit;
    Action shootOrHoldAction;
    int probNextEmission;
    int maxNextEmission;


    // We set a time limit so that we have a margin of 14 time steps before we shoot
    // the bird in each round.
    timeLimit = 86 + 99 * pState.getRound();
    timeLimitMeet = ( time > timeLimit );

    // We want to wait a few rounds before we start shooting birds so that we have
    // time to train the HMM:s enough to get good results.
    roundMargin = 2;
    roundMarginMeet = ( pState.getRound() > 2 );

    // Number of observations needed from a bird to be sure enough where it will fly next.
    birdSeqLengthLimit = 63;

    maxLogProb = - Double.MAX_VALUE;
    probCertaintyLimit = 0.61;

    // If we have trained the HMM:s enough.
    // i.e. if a few rounds have passed and if
    // enough time has passed.
    if( roundMarginMeet && timeLimitMeet ){

      // Number of birds in this round.
      noBirds = pState.getNumBirds();
      maxLogProb = - Double.MAX_VALUE;

      // Stats to store about the birds in the sky to decide which bird we should
      // try to shoot and also if we should shoot it.
      logProbsBirds = new double [ noBirds ];
      optimalBirdsHMMs = new HMM [ noBirds ];
      optimalBirdsSpieces = new int [ noBirds ];


      // We find the bird that we feel most certain about where it will fly next
      // in the sky based on past observations.
      optimalBirdToShoot = retriveOptimalBirdToShoot ( noBirds, pState, birdSeqLengthLimit, maxLogProb );
      optimalBirdSpecies = optimalBirdsSpieces[ optimalBirdToShoot ];

      noBirdObs = pState.getBird( optimalBirdToShoot ).getSeqLength();

      isNotBlackStork = ( optimalBirdSpecies != Constants.SPECIES_BLACK_STORK );
      isNotUnknown = ( optimalBirdToShoot != - 1 );
      certainPreviousFligtPattern = ( maxLogProb > 460 );

      /*
      If we are certain enough about how the bird has been flying previously
      and we do not think that this bird is a black stork.
      Then, we want to estimate where it will fly next using HMM:s.
      */
      if( certainPreviousFligtPattern && isNotBlackStork && isNotUnknown ){

        // The most probable next emission for the bird.
        maxNextEmission = mostProbableNextEmission( pState, noBirdObs, optimalBirdToShoot, optimalBirdSpecies );

        // We shoot or hold based on how certain we are that the bird will be at
        // our estimated next emission.
        shootOrHoldAction = shootOrHold( maxProbNextEmission, optimalBirdToShoot, maxNextEmission, probCertaintyLimit);

        return shootOrHoldAction;
      }
      // For the first round or default in other rounds when u don't have time left
      // or the prob for an emission was to low.
    }
    return cDontShoot;
  }


  public int indexLargestArrayList( ArrayList<Double> AL ){
    // Retrives the index of the largest element in an array list.

    double largetsVal;
    int indexLargetsVal, noValues;

    // Initilize arbitrarily.
    largetsVal = AL.get(0);
    indexLargetsVal = 0;

    noValues = AL.size();

    for( int idx = 1 ; idx < noValues; idx ++ ){

      // If a larger value is found, the "largest values" value and index
      // is updated.
      if( AL.get(idx) > largetsVal ){

          largetsVal = AL.get(idx);
          indexLargetsVal = idx;

      }
    } return indexLargetsVal;
  }


  public double largestArrayListValue( ArrayList<Double> AL ){
    // Retrives the value of the largest element in an array list.

    double largetsVal;
    int noValues;

    // Initilize arbitrarily.
    largetsVal = AL.get(0);

    noValues = AL.size();


    for( int i = 1 ; i< noValues; i ++ ){

      if( AL.get(i) > largetsVal ){

        largetsVal = AL.get(i);

      }
    }
    return largetsVal;
  }


  public int[] initilizeBirdSpecies ( int noBirds, int[] birdSpeciesGuesses ){
    /*
    The birds species is initilized to unknow so that we do not make a guess
    and risk losing points as default.
    */
    birdSpeciesGuesses = new int[noBirds];

    for (int i = 0; i < noBirds; ++ i ) birdSpeciesGuesses[i] = Constants.SPECIES_UNKNOWN;

    return birdSpeciesGuesses;
  }


  public int[] randomlyGuessBirdSpecies ( int noBirds, int[] randomGuesses, Random rand ){
    /*
    At this point we just randomly make a guess from a predefined set of known species.
    */

    int randomGuess;

    for( int bird = 0; bird < noBirds; ++ bird ){

      randomGuess = rand.nextInt( Constants.COUNT_SPECIES - 1 );
      randomGuesses[bird] = randomGuess;

    }
    return randomGuesses;
  }


  public int [] getBirdObservationSequence ( int noBirdObs, int bird, GameState pState){
    /*
    Extract the observation sequence from a given bird.
    */

    int [] observstionSequence;
    boolean isBirdDead;

    observstionSequence = new int [ noBirdObs ];

    for (int timeStep = 0; timeStep < noBirdObs; timeStep ++ ){

      isBirdDead = ( pState.getBird( bird ).getObservation( bird ) != Constants.MOVE_DEAD );

      // If the bird is not dead.
      if( isBirdDead ){

        // Extract its observation sequence.
        observstionSequence[ timeStep ] = pState.getBird( bird ).getObservation( timeStep );

      }
    }
    return observstionSequence;
  }


  public int getBirdsMostProbableSpecies( int [] observstionSequence ){
    /*
    Based on a birds flight pattern this function finds a birds most probable species
    using the combined classification of previo trained HMM:s.
    */

    double [] maxprobPerSpieces, probPerSpieces;

    double probObsSeq;
    HMM hmmBirdSpiecesModelround;
    int scalingFactor, speciesClassification;


    maxprobPerSpieces = new double [Constants.COUNT_SPECIES];
    probPerSpieces = new double [Constants.COUNT_SPECIES];

    // Initlize the probability of the current bird being a certain species to zero.
    Arrays.fill( probPerSpieces, 0 );

    // Go through every possible bird species and compute the probability of the bird
    // being every possible species.
    for( int birdSpecies = 0; birdSpecies < Constants.COUNT_SPECIES; birdSpecies ++ ){

      // Create a iterator to go through all previously saved HMM:s trained on birds
      // for a certain species.
      Iterator<HMM> iterator;
      iterator = birdModelsHMM[birdSpecies].iterator();

      // A scaling factor used to make the result into a probability
      // equivalent to the number of models for each species  or the total no of classifications
      // that we make for each bird.
      scalingFactor = 0;

      // For every HMM model that we found for the current bird species that we are considering
      while ( iterator.hasNext() ){

        hmmBirdSpiecesModelround = iterator.next();

        // Classify the bird using the current HMM model for a certain species.
        probObsSeq = hmmBirdSpiecesModelround.alphaPassProb( observstionSequence );

        // Add up all the HMM:s probabilities fo this bird being a certain species.
        probPerSpieces[birdSpecies] += probObsSeq;

        // Increase the scaling factor.
        scalingFactor ++;

      }

      // Smooth the summed probabilities for the current species being considered
      // for the current bird.
      probPerSpieces[ birdSpecies ] = probPerSpieces[ birdSpecies ] / scalingFactor;

    }

    // Classify the bird based on the highest scaled probability found.
    speciesClassification = helperMethods.indexLargestLogVal( probPerSpieces );

    return speciesClassification;
  }


  public int[] guess( GameState pState, Deadline pDue ) {
    /**
     * Guess the species!
     * This function will be called at the end of each round, to give you
     * a chance to identify the species of the birds for extra points.
     *
     * Fill the vector with guesses for the all birds.
     * Use SPECIES_UNKNOWN to avoid guessing.
     *
     * @param pState the GameState object with observations etc
     * @param pDue time before which we must have returned
     * @return a vector with guesses for all the birds
     */

     Random rand;

     int noBirds, noBirdObs, birdGuess, birdSpeciesClassification;
     int [] observstionSequence;

     boolean isFirstRound, noBirdsFlying;

     // Number of birds in the sky.
     noBirds = pState.getNumBirds();

     rand = new Random();


     birdSpeciesGuesses = initilizeBirdSpecies (noBirds, birdSpeciesGuesses );


     noBirdsFlying = ( noBirds == 0 );


      // If there is no birds in the sky, then just return the initilized guesses
      // for the birds, i.e. species unknown.
      if ( noBirdsFlying ){

        return birdSpeciesGuesses;

      // If there are birds in the sky.
      }else{

        isFirstRound = ( pState.getRound() == 0 );

        // If it is the first round our HMM parameters are just ranodmly initilized so we
        // cannot really make an educated guess. So, we guess randomly.

        if( isFirstRound ){

          birdSpeciesGuesses = randomlyGuessBirdSpecies (  noBirds, birdSpeciesGuesses, rand );

        // If there are birds in the sky and it is not the first round, i.e. we have updated
        // the HMM parameters, we wanna try to guess the birds species.
        }else{
          /*
            So, here we go through every single bird that is flying in the sky.
          */

          for( int bird = 0 ; bird < noBirds; ++ bird ){

            noBirdObs = pState.getBird(bird).getSeqLength();

            // Get the observation sequence for the current bird that we are trying to classify.
            observstionSequence = getBirdObservationSequence ( noBirdObs, bird, pState );

            // Now we wnat to find the probability of this bird being a certain species based on its flight
            // patter.

            birdSpeciesClassification = getBirdsMostProbableSpecies( observstionSequence );

            birdSpeciesGuesses[bird] = birdSpeciesClassification;
          }
        }
      }
    return birdSpeciesGuesses;
  }


  public void hit(GameState pState, int pBird, Deadline pDue) {
    /**
     * If you hit the bird you were trying to shoot, you will be notified
     * through this function.
     *
     * @param pState the GameState object with observations etc
     * @param pBird the bird you hit
     * @param pDue time before which we must have returned
     */

      System.err.println("HIT BIRD!!!");
  }


  public void reveal( GameState pState, int[] pSpecies, Deadline pDue ) {
    /**
    * If you made any guesses, you will find out the true species of those
    * birds through this function.
    *
    * @param pState the GameState object with observations etc
    * @param pSpecies the vector with species
    * @param pDue time before which we must have returned
    */
    int noBirds, noBirdObs;
    int[] observationSequence;

    HMM birdHMM;
    double logProb;

    boolean isSpeciesUnknown;

    noBirds = pSpecies.length;

    // For every bird from the last round we want to store a HMM with its species in
    // an array list.
    for( int bird = 0; bird < noBirds; bird ++ ){

      isSpeciesUnknown = ( pSpecies[bird] != Constants.SPECIES_UNKNOWN );

      // If the birds species is not unkown.
      if( isSpeciesUnknown ){

        noBirdObs = pState.getBird(bird).getSeqLength();

        // Extract the birds observation sequence.
        observationSequence =  getBirdObservationSequence ( noBirdObs, bird, pState);

        // Create a HMM for this bird.
        birdHMM = new HMM();
        logProb = birdHMM.BaumWelch(observationSequence);

        // Store this HMM in an array list with its species as a key.
        birdModelsHMM[ pSpecies[bird] ].add( birdHMM );

      }
    }
  }

}
