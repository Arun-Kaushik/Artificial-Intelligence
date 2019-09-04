import java.io.FileNotFoundException;
import java.io.File;
import java.util.Scanner;
import java.util.Arrays;
import java.lang.Math;


public class HMM1{

  public static float [][] matrixMultiplication(float [][] Matrix1,float [][] Matrix2 ){
    /* Takes two matricies as input and performs matrix multiplication between them.
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


public static int[]  arrayFiller(String inputLine){
  /* Fills an empty array from the content of a numerical text string values separated by white spaces.
  */
  int noElements, itterator;
  String [] stringArray;
  int[] integerArray;

  // Convert text string separated by whitespaces into array of string numericla values.
  stringArray = inputLine.split("\\s+");
  noElements = Integer.parseInt( stringArray[0] );

  // Array to store numerical array values.
  integerArray = new int [ noElements ];

  itterator = 1;

  // For every element in the array we place a value from the text string input
  //that we convert to an integer.
  for(int i = 0; i < integerArray.length; i++){

        integerArray[i] = Integer.parseInt( stringArray[itterator] );
        itterator ++;
  }

  return integerArray;
}


public static double [][]  matrixFiller(String inputLine){
  /* Fills an empty matrix from the content of a numerical text string values separated by white spaces.
  */
  String [] stringArray = inputLine.split( "\\s+" );
  String[] cloneOfArray = Arrays.copyOf( stringArray, stringArray.length );

  int no_rows, no_cols, itterator;

  no_rows = Integer.parseInt( stringArray[0] );
  no_cols = Integer.parseInt( stringArray[1] );

  itterator = 2;

  double [][] matrixFill = new double [no_rows][no_cols];

  // For every element in the matrix we place a value from the text string inputScanner.
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


public static double [][] forwardPass(int noStates, int noTimeSteps, int firstObservation, double [][] emissionMatrix, double [] initialStateProb, double [][] transitionMatrix, int [] observationSequence ){
  // The forward algorithm i.e the alpha pass used to compute:
  //P(O_{1,..t},x_t = q_s| \lambda) where t is a certain time step and s a certain state.
  // i.e. the probabilities for each posible state for a sequence of observations given a HMM model.

  double [][] alphat = new double [noStates][noTimeSteps];
  int firstTimeStep = 0;

  // Computing all the values for the first time step: alpha_{0}(state).
  for( int state = 0; state < noStates ; state ++ ){

    alphat[state][firstTimeStep] = initialStateProb[state] * emissionMatrix[state][firstObservation];

  }

  // Computing all the values for all states for all time steps except for
  // the first: alpha_{t}(state).

  for ( int t = 1; t < noTimeSteps; t++ ){

    double ct = 0;

    for ( int currState = 0; currState < noStates; currState++ ){

      for ( int prevState = 0 ; prevState < noStates; prevState++ ){

        alphat[currState][t] = alphat[currState][t] + alphat[prevState][t-1] * transitionMatrix[prevState][currState];

      }

      alphat[currState][t] = alphat[currState][t] * emissionMatrix[currState][observationSequence[t]];
      ct = ct + alphat[currState][t];
    }
  }

  return alphat;
}


public static double  computeObservationProbabilty( int noStates, int noTimeSteps, double [][] alphat ){
  /*
  Computes the probability of an observation sequence using probabilities on the form: P(O_{1,..t},x_t = q_s| \lambda).
  // i.e. the probabilities for each posible state for a sequence of observations given a HMM model.
  */
  double observationSequenceProbability;

  observationSequenceProbability = 0;

  for( int state = 0 ; state < noStates; state++ ){

    observationSequenceProbability = observationSequenceProbability + alphat[state][noTimeSteps-1];

  }
return observationSequenceProbability;
}


public static void main (String[] args){

        // A scanner used to read the transition and emission matricies and the
        // initial state probability distribution.
        Scanner inputScanner;

        // The HMM parameters.
        double [][] transitionMatrix, emissionMatrix;
        double [] initialStateProb;
        int [] observationSequence;
        int noStates, noTimeSteps, firstObservation;
        String observationSequenceString;

        // Parameters used to make computations with the HMM.
        double [][] alphat;
        double observationSequenceProbability;

        // A scanner used to read the input line by line.
        inputScanner = new Scanner( System.in );

        // The transition and emission matricies and the initial state probability
        // distribution.
        transitionMatrix = readStringMatrix( inputScanner );
        emissionMatrix = readStringMatrix( inputScanner );
        initialStateProb = readStringMatrix( inputScanner )[0];

        // Read the observation sequence form the input.
        observationSequenceString = inputScanner.nextLine();
        observationSequence = arrayFiller( observationSequenceString );


        firstObservation = observationSequence[0];
        noStates = transitionMatrix[0].length;
        noTimeSteps = observationSequence.length;

        // The forward algorithm i.e the alpha pass used to compute:
        //P(O_{1,..t},x_t = q_s| \lambda) where t is a certain time step and s a certain state.

        alphat = forwardPass(noStates, noTimeSteps, firstObservation, emissionMatrix, initialStateProb, transitionMatrix, observationSequence);

        observationSequenceProbability = computeObservationProbabilty( noStates, noTimeSteps, alphat);

        System.out.print( "The probability of the observation sequence: " );
        System.out.print( observationSequenceString );
        System.out.print(" . Probability: ");
        System.out.println( observationSequenceProbability );
    }
}
