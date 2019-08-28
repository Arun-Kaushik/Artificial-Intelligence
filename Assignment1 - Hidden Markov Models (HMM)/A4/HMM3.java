import java.io.FileNotFoundException;
import java.io.File;
import java.util.Scanner;
import java.util.Arrays;
import java.lang.Math;
import java.util.Collections;


public class HMM3{

  static double [] c;

  public static double [][] alphaPass( double [][] transitionMatrix, double [][] emissionMatrix, int firstObservation, int noStates, int noTimeSteps, double [] initialStateProb, int [] observationSequence ){
    // The forward algorithm i.e the forward pass.

    double [][] alphat;

    alphat = new double [noStates][noTimeSteps];

    c = new double[noTimeSteps];
    c[0] = 0;

    // Computing all the values for the first time step: alpha_{0}(state).
    for( int state = 0; state < noStates ; state ++ ){

        alphat[state][0] = initialStateProb[state] * emissionMatrix[state][firstObservation];
        c[0] = c[0] + alphat[state][0];

    }

    // Scale the alpha values of the initial state.
    c[0] = 1 / c[0];
    for( int state = 0 ; state < noStates; state ++ ){

       alphat[state][0] = alphat[state][0] * c[0];

     }

     // Computing all the values for all states for all time steps except for
     // the first: alpha_{t}(state).

    for (int t = 1 ; t < noTimeSteps ; t ++ ){

      c[t] = 0;

      for ( int currState = 0; currState < noStates; currState ++ ){
        for ( int prevState = 0 ; prevState < noStates; prevState ++ ){

            alphat[currState][t] = alphat[currState][t] + alphat[prevState][t-1] * transitionMatrix[prevState][currState];

        }

        alphat[currState][t] = alphat[currState][t] * emissionMatrix[currState][observationSequence[t]];
        c[t] = c[t] + alphat[currState][t];
      }

      // Scale the alpha values of all states and time steps.
      c[t] = 1 / c[t];
      for ( int currState = 0 ; currState < noStates; currState ++ ){

        alphat[currState][t] = c[t] * alphat[currState][t];

      }
    }
    return alphat;
  }


  public static double [][] betaPass( double [][]transitionMatrix, double [][] emissionMatrix, int firstObservation, int noStates, int noTimeSteps, double [] initialStateProb, int [] observationSequence ){
    /* The Backward Procedure is implemented in accordance with: A Revealing Introduction to Hidden Markov Models, by Mark Stamp∗
    at Department of Computer Science - San Jose State University, https://www.cs.sjsu.edu/~stamp/RUA/HMM.pdf
    */

    double [][] betat;

    betat = new double [noStates][noTimeSteps];

    // Let β_{T−1}(i), be scaled by c_{T −1}
    for( int state = 0 ; state < noStates ; state ++ ){
      betat[state][ noTimeSteps - 1 ] = c[ noTimeSteps - 1 ];
    }

    // The beta pass.
    for ( int t = noTimeSteps - 2 ; t >= 0 ; t -- ){

      // Going through all states at the current time step.
      for ( int stateCurrT = 0 ; stateCurrT < noStates ; stateCurrT ++ ){

        // Going through all states at the next time step.
        for ( int statePrevT = 0 ; statePrevT < noStates; statePrevT++ ){

          betat[stateCurrT][t] = betat[stateCurrT][t] + transitionMatrix[stateCurrT][statePrevT] * emissionMatrix[statePrevT][observationSequence[t+1]] * betat[statePrevT][t+1];

        }
        betat[stateCurrT][t] = c[t] * betat[stateCurrT][t];
      }
    }

  return betat;
  }


  public static double [][][] diGamma( double [][] transitionMatrix, double [][] emissionMatrix, int firstObservation, int noStates, int noTimeSteps, double [] initialStateProb, int [] observationSequence, double [][] alphat , double [][] betat ){
    /* diGammat is the probability of being in state i at time t given the observed sequence
      O and the parameters λ = (A, B, π).
    */

    double [][][] diGammaMatrix;
    double denominator;

    diGammaMatrix = new double[noStates][noStates][noTimeSteps];

    for ( int t = 0; t < noTimeSteps - 1; t ++ ){

      denominator = 0;
      // For all states at the current time step.
      for ( int stateCurrT = 0 ; stateCurrT < noStates; stateCurrT ++ ){

        // For all states at the next time step that we could transition to.
        for ( int statePrevT = 0; statePrevT < noStates; statePrevT ++ ){

          denominator += alphat[stateCurrT][t] * transitionMatrix[stateCurrT][statePrevT] * emissionMatrix[statePrevT][observationSequence[t+1]] * betat[statePrevT][t+1];
        }
      }
      // For all states at the current time step.
      for (int stateCurrT = 0 ; stateCurrT < noStates; stateCurrT ++ ){
        // For all states at the next time step that we could transition to.
        for (int statePrevT = 0 ; statePrevT < noStates; statePrevT ++ ){
          // Computing di gamma by dividing the probability of being in state i at time
          // t given the observed sequence O and the parameters λ = (A, B, π) by  the
          // probability of making the observation O given the parameters λ = (A, B, π).
          diGammaMatrix[stateCurrT][statePrevT][t] = ( alphat[stateCurrT][t] * transitionMatrix[stateCurrT][statePrevT] * emissionMatrix[statePrevT][observationSequence[t+1]] * betat[statePrevT][t+1]) / denominator;

        }
      }
    }
    return diGammaMatrix;
  }

  public static double [] reEstimateInitialStateProb( double [][] gamma, int noStates ){
    /*
    The probability distribution for the initial state is computed as the
    expected frequency spent in state i at time  1.
    */
    double [] pi;

    pi = new double [noStates];

    // Compute the expected frequency spent in every state at the first time step.
    for (int state = 0; state < noStates; state ++ ) pi[state] = gamma [state][0];

    return pi;
  }

  public static double [][] reEstimateTransistionMatrix( double [][] gammaMatrix, int noStates, double [][][] diGammaMatrix, int noTimeSteps ){
    /* The transition matrix is estimated as the expected number of transitions from a state to
    the next state at the next time step compared to  the expected total number of transitions
    away from a state. To clarify, the number of transitions away from a state does not mean
    transitions to a different state , but to any state including itself.
    */

    double [][] transitionMatrix;
    double noTransitions;
    double totalNoTransitions;

    transitionMatrix = new double [noStates][noStates];

    for (int stateCurrT = 0; stateCurrT < noStates; stateCurrT ++ ){
      for (int nextStateT = 0; nextStateT < noStates; nextStateT ++ ){

        noTransitions = 0;
        totalNoTransitions = 0;

        for (int t = 0; t < noTimeSteps - 1; t ++ ){
          // The expected number of transitions from state stateCurrT to state nextStateT.
          noTransitions =+ diGammaMatrix[stateCurrT][nextStateT][t];
          // The expected total number of transitions away from a state.
          totalNoTransitions =+ gammaMatrix[stateCurrT][t];
        }
        transitionMatrix[stateCurrT][nextStateT] = noTransitions / totalNoTransitions;
      }
    }
  return transitionMatrix;
  }

  public static double [][] reEstimateEmissionMatrix( double [][] gammaMatrix, int noStates, int noTimeSteps, int [] observationSequence, double [][] emissionMatrix ){
    /*
    The emission matrix is equivalent to the number of times a state is observed
     in the sequence from t = 1 to t = T − 1.
    */

    double [][] newEmissionMatrix;
    double numerator;
    double denominator;

    newEmissionMatrix = new double [noStates][emissionMatrix[0].length];

    for (int i = 0; i< noStates; i ++ ){

      for (int j = 0; j < emissionMatrix[0].length; j ++ ) {

        numerator = 0;
        denominator = 0;

        for ( int t = 0; t < noTimeSteps; t ++ ){

          // Update the frequency of observing the current state in the current time step and state.
          if( observationSequence[t] == j )  numerator += gammaMatrix[i][t];

          // Update the frequency of  transitioning between  these two states.
          denominator += gammaMatrix[i][t];
        }
        newEmissionMatrix[i][j] = numerator / denominator;
      }
    }
    return newEmissionMatrix;
  }



  public static double [][] gamma( double [][] transitionMatrix, double [][] emissionMatrix, int firstObservation, int noStates, int noTimeSteps, double [] initialStateProb, int [] observationSequence, double [][] alphat, double [][] betat , double [][][] diGammaMatrix ){
    /* gammat is the probability of being in state i and j at times t and t+1 respectively
     given the observed sequence O and the parameters λ = (A, B, π).
    */
    double [][] gammaMatrix;
    double probObsGivenLambda;

    gammaMatrix = new double[noStates][noTimeSteps];

    // Going through all time steps.
    for (int t=0; t< noTimeSteps-1 ; t++ ){
      // Going through all states at the current time step.
      for (int stateCurrT = 0; stateCurrT < noStates; stateCurrT ++ ){
        // Going through all states at the next time step.
        for (int statePrevT = 0; statePrevT < noStates; statePrevT ++ ){

          gammaMatrix[stateCurrT][t] = gammaMatrix[stateCurrT][t] + diGammaMatrix[stateCurrT][statePrevT][t];

          }
        }
      }

    /*
    The (probObsGivenLambda) Denominator parameters is equivalent to the observation
    O given the parameters λ = (A, B, π).
    */

    probObsGivenLambda = 0 ;

    for ( int state = 0 ; state < noStates ; state ++ ){

        probObsGivenLambda =+ alphat[state][noTimeSteps-2];

    }
    for (int state = 0; state < noStates; state ++ ){
      gammaMatrix[state][noTimeSteps - 2] = alphat[state][noTimeSteps - 2] / probObsGivenLambda;
    }

    return gammaMatrix;
  }


  public static float [][] matrixMultiplication(float [][] Matrix1,float [][] Matrix2 ){
    /* Takes two matricies as inputScanner and performs matrix multiplication between them.
    */
    int i, j, k, m, n;

    int noRowsMatrix1 = Matrix1.length;
    int noColsMatrix1 = Matrix1[0].length;
    int noRowsMatrix2 = Matrix2.length;
    int noColsMatrix2 = Matrix2[0].length;

    // The resulting matrix will have dimensions given by the number of rows
    // of matrix 1 and the number of columns of matrix 2.
    float[][] MatrixMultiplied = new float [noRowsMatrix1][noColsMatrix2];

    // Compute the result for every element in the resulting matrix.
    for (i = 0; i < noRowsMatrix1; i++) {
      for(j = 0; j < noColsMatrix2; j++) {
       for(k = 0; k < noColsMatrix1; k++) {
         MatrixMultiplied[i][j] += Matrix1[i][k] * Matrix2[k][j];
       }
      }
    }
    return MatrixMultiplied;
  }


  public static int[]  arrayFiller(String inputScannerLine){
    /* Fills an empty array from the content of a numerical text string values separated by white spaces.
    */
    int noElements, itterator;
    String [] stringArray;
    int[] integerArray;

    // Convert text string separated by whitespaces into array of string numericla values.
    stringArray = inputScannerLine.split("\\s+");
    noElements = Integer.parseInt( stringArray[0] );

    // Array to store numerical array values.
    integerArray = new int [ noElements ];

    itterator = 1;

    // For every element in the array we place a value from the text string inputScanner
    //that we convert to an integer.
    for(int i = 0; i < integerArray.length; i++){

          integerArray[i] = Integer.parseInt( stringArray[itterator] );
          itterator ++;
    }

    return integerArray;
  }


  public static double computeConvergenceCondition( int noTimeSteps ){
    /*
      Compute the convergence condition using: log[P(O | λ)]
    */

    double logProb;

    logProb = 0;

    for( int state = 0; state < noTimeSteps; state ++ ){

      logProb = logProb + Math.log( c[state] );

    }

    logProb = -logProb;
    return logProb;
  }


  public static double [][]  matrixFiller( String inputScannerLine ){
    /*
    Fills an empty matrix from the content of a numerical text string values separated by white spaces.
    */
    String [] stringArray = inputScannerLine.split( "\\s+" );
    String[] cloneOfArray = Arrays.copyOf( stringArray, stringArray.length );

    int no_rows, no_cols, itterator;

    no_rows = Integer.parseInt( stringArray[0] );
    no_cols = Integer.parseInt( stringArray[1] );

    itterator = 2;

    double [][] matrixFill = new double [no_rows][no_cols];

    // For every element in the matrix we place a value from the text string inputScannerScanner.
    for(int row = 0; row < matrixFill.length; row ++) {
      for(int col = 0; col < matrixFill[row].length; col++){

          matrixFill[row][col] = Float.parseFloat( stringArray[itterator] );
          itterator++;
      }
    }
    return matrixFill;
  }


  public static double[][]  readStringMatrix( Scanner inputScanner ){
    /*
    Reads a matrix from a line input and fills a matrix/array.
    */
    String matrix_string = inputScanner.nextLine();
    double [][] matrix = matrixFiller( matrix_string );

    return matrix;
  }


  public static void BaumWelch (int [] observationSequence, double [][] transitionMatrix, double [][] emissionMatrix, double [] initialStateProb){
    /*
    The Baum–Welch (a special case of the EM algorithm) used to find the unknown parameters of the
    hidden Markov model (HMM). It makes use of the forward-backward algorithm to compute the statistics
    for the expectation step. Given an observation sequence O and the dimensions N and M, it finds a
    model λ = (A, B, π) that maximizes the probability of O. This can be viewed as training a model
    to best fit the observed data. Alternatively, we can view this as a (discrete) hill climb on
    the parameter space represented by A, B and π.
    */

    // Used to estimate the HMM parameters.
    double [][] alphaf, betaf, gammat;
    double [][][] diGammat;
    int noStates, noTimeSteps, firstObservation;
    double oldLogProb, logProb;

    int maxNoIterations, currIteration;

    firstObservation = observationSequence[0];

    noStates = transitionMatrix[0].length;
    noTimeSteps = observationSequence.length;

    // Setting max number of iterations to a large value.
    maxNoIterations = 1000;
    currIteration = 0;

    // Setting this variable to an arbitary small value.
    oldLogProb = Math.log( 0 );
    logProb = 0;

    // The first update of the Baum Welsh algorithm.

    /* Maximization step of the parameters used to estimate the HMM. */
    // Forward procedure.
    alphaf = alphaPass( transitionMatrix, emissionMatrix, firstObservation, noStates, noTimeSteps, initialStateProb, observationSequence );

    // Backward procedure.
    betaf = betaPass( transitionMatrix, emissionMatrix, firstObservation, noStates, noTimeSteps, initialStateProb, observationSequence );
    diGammat = diGamma( transitionMatrix, emissionMatrix, firstObservation, noStates, noTimeSteps, initialStateProb, observationSequence, alphaf, betaf );
    gammat = gamma( transitionMatrix, emissionMatrix, firstObservation, noStates, noTimeSteps,  initialStateProb, observationSequence, alphaf, betaf, diGammat );

    /* Estimation step of the HMM parameters. */
    initialStateProb = reEstimateInitialStateProb( gammat, noStates );
    transitionMatrix = reEstimateTransistionMatrix( gammat, noStates, diGammat, noTimeSteps );
    emissionMatrix = reEstimateEmissionMatrix( gammat, noStates, noTimeSteps, observationSequence, emissionMatrix );

    // Update the convergence criteria.
    logProb = computeConvergenceCondition( noTimeSteps );
    currIteration ++;

    // Check for convergence or until the maximum number of iterations is reached.
    while ( currIteration< maxNoIterations && logProb > oldLogProb ){

      // Update the convergence parameter.
      oldLogProb = logProb;

      /* Maximization step of the parameters used to estimate the HMM. */
      // Forward procedure.
      alphaf = alphaPass( transitionMatrix, emissionMatrix, firstObservation, noStates, noTimeSteps, initialStateProb, observationSequence);

      // Backward procedure.
      betaf = betaPass( transitionMatrix, emissionMatrix, firstObservation, noStates, noTimeSteps, initialStateProb, observationSequence);

      /* Update/Estimation step of the HMM parameters. */
      // Temporary varibles computed using Bayes' theorem.

      // diGammat is the probability of being in state i at time t given the observed sequence
      // O and the parameters λ = (A, B, π).
      diGammat = diGamma( transitionMatrix, emissionMatrix, firstObservation, noStates, noTimeSteps, initialStateProb, observationSequence, alphaf, betaf);

      // gammat is the probability of being in state i and j at times t and t+1 respectively
      // given the observed sequence O and the parameters λ = (A, B, π).
      gammat = gamma( transitionMatrix, emissionMatrix, firstObservation, noStates, noTimeSteps, initialStateProb, observationSequence, alphaf, betaf, diGammat);

      // The probability distribution for the initial state is computed as the
      // expected frequency spent in state i at time  1.
      initialStateProb = reEstimateInitialStateProb( gammat, noStates );

      // The transition matrix is estimated as the expected number of transitions from state i to state j
      //compared to  the expected total number of transitions away from state i. To clarify,
      //the number of transitions away from state i does not mean transitions to a
      //different state j, but to any state including itself.
      transitionMatrix = reEstimateTransistionMatrix( gammat, noStates, diGammat, noTimeSteps );

      // The emission matrix is equivalent to the number of times state i is observed
      // in the sequence from t = 1 to t = T − 1.
      emissionMatrix = reEstimateEmissionMatrix( gammat, noStates, noTimeSteps, observationSequence, emissionMatrix );

      // Update the convergence criteria.
      logProb = computeConvergenceCondition( noTimeSteps );
      currIteration ++;

    }
     DisplayResults( transitionMatrix, emissionMatrix, initialStateProb );
  }


  public static void DisplayResults( double [][] transitionMatrix, double [][] emissionMatrix, double [] initialStateProb ){

    /*
    Displays the results of the Baum Welch algorithm, i.e. the initial state probability
    distribution and the transition and emission matricies.
    */

    String rowColsTransitionMatrix = Integer.toString( transitionMatrix.length ) + " " + Integer.toString( transitionMatrix[0].length );

    System.out.print("The number of rows and columns of the transition matrix: ");
    System.out.print( rowColsTransitionMatrix );
    System.out.println(" ");
    System.out.print("The transition matrix: ");

    for( int r = 0 ; r < transitionMatrix.length ; r++ ){
      for ( int c = 0; c < transitionMatrix[0].length; c++ ){

          System.out.print(" ");
          System.out.print( transitionMatrix[r][c] );

      }
    }

    System.out.println("");
    System.out.println("");
    System.out.print("The number of rows and columns of the Emission matrix: ");

    String rowColsEmissionMatrix = Integer.toString( emissionMatrix.length ) + " " + Integer.toString( emissionMatrix[0].length );

    System.out.print( rowColsEmissionMatrix );
    System.out.println(" ");
    System.out.print("The Emission matrix: ");

    for( int r = 0 ; r < emissionMatrix.length ; r ++ ){
      for ( int c = 0; c < emissionMatrix[0].length ; c ++ ){
          System.out.print(" ");
          System.out.print( emissionMatrix[r][c] );
      }
    }
  }


  public static void main (String[] args){

          // A scanner used to read the transition and emission matricies and the
          // initial state probability distribution.
          Scanner inputScanner;

          // Used to initilize the HMM parameters.
          double [][] transitionMatrix, emissionMatrix;
          double [] initialStateProb;

          // The observed sequence used to estimate the HMM.
          int [] observationSequence;

          // Used to estimate the HMM parameters.
           Object [] HMMParameters;

          String observationSequenceString;


          inputScanner = new Scanner( System.in );

          // The transition and emission matricies and the initial state probability
          // distribution.
          transitionMatrix = readStringMatrix( inputScanner );
          emissionMatrix = readStringMatrix( inputScanner );
          initialStateProb = readStringMatrix( inputScanner )[0];

          // Read the observation sequence form the input.
          observationSequenceString = inputScanner.nextLine();
          observationSequence = arrayFiller( observationSequenceString );

          /*
          The Baum–Welch (a special case of the EM algorithm) used to find the unknown parameters of the
          hidden Markov model (HMM). It makes use of the forward-backward algorithm to compute the statistics
          for the expectation step. Given an observation sequence O and the dimensions N and M, it finds a
          model λ = (A, B, π) that maximizes the probability of O. This can be viewed as training a model
          to best fit the observed data. Alternatively, we can view this as a (discrete) hill climb on
          the parameter space represented by A, B and π.
          */

          BaumWelch (observationSequence, transitionMatrix, emissionMatrix, initialStateProb);



        }

    }
