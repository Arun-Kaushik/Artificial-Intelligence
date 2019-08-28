import java.io.FileNotFoundException;
import java.io.File;
import java.util.Scanner;
import java.util.Arrays;
import java.lang.Math;
import java.util.Collections;


public class HMM2{


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


  public static int[]  arrayFiller( String inputLine ){
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


public static double maxFinder(double [] array){
  // Retrives the largest element in an array.

  double currentLargestValue;

  currentLargestValue = array[0];

  for( int i = 1; i < array.length; i++ ){

      if( array[i] > currentLargestValue ){

         currentLargestValue = array[i];

      }
  }
  return currentLargestValue;
}


public static double [] getColumn(double [][] matrix1, int colIndex){
  /*
  Retrives a column from a matrix basec on a column index.
  */

  double [] extractedColumn;
  int noRows;

  noRows = matrix1.length;
  extractedColumn = new double[matrix1.length];

  for ( int row = 0; row < noRows ; row ++ ){

    extractedColumn[row] = matrix1[row][colIndex];

  }
  return extractedColumn;
}


public static int getIndexOfLargest(double [] array){
  // Retrives the index of the largest element in an array.

  double currentLargestValue;
  int indexLargestValue;

  currentLargestValue = array[0];
  indexLargestValue = 0;

  for( int index = 1; index < array.length; index++ ){

      if( array[index] > currentLargestValue ){

         currentLargestValue = array[index];
         indexLargestValue = index;

      }
  }
  return indexLargestValue;
}


public static double[][]  readStringMatrix( Scanner inputScanner ){
  /*
  Reads a matrix from a line input and fills a matrix/array.
  */
  String matrix_string = inputScanner.nextLine();
  double [][] matrix = matrixFiller( matrix_string );

  return matrix;
}


public static int [] viterbiAlgorithm( int noStates, int noTimeSteps, int firstObservation, double [][] transitionMatrix, double [][]  emissionMatrix, double [] initialStateProb, int [] observationSequence){
  /*
  Given λ = (A, B, π) and an observation sequence O, we find an optimal state sequence for the
  underlying Markov process. In other words, we want to uncover the hidden part of the
  Hidden Markov Model. Note that in this version we try to find the maximum path while another
  option could have been to take all possible paths combined maximum.


  Implemented in accordance with: "Speech and Language Processing: An introduction to natural
  language processing, computational linguistics, and speech recognition."" - Daniel Jurafsky &
  James H. Martin.
  */

  double [][] deltat;
  int [][] indiciesMaxProb;
  int [] mostProbStateSequence;


  indiciesMaxProb = new int [noStates][noTimeSteps];
  deltat = new double [noStates][noTimeSteps];

  for( int state = 0 ; state < noStates; state ++ ){

    deltat[state][0] = Math.log( initialStateProb[state] * emissionMatrix[state][firstObservation] );
    indiciesMaxProb[state][0] = 0;

  }

  for ( int t = 1 ; t < noTimeSteps; t ++ ){

    for ( int currState = 0 ; currState < noStates; currState ++ ){

      double [] stateProbability = new double[noStates];

      for (int prevState = 0; prevState < noStates; prevState ++ ){

        stateProbability[prevState] = deltat[prevState][t-1] + Math.log( transitionMatrix[prevState][currState] ) + Math.log( emissionMatrix[currState][observationSequence[t]] );

      }

      deltat[currState][t] = maxFinder( stateProbability );
      indiciesMaxProb[currState][t] = getIndexOfLargest( stateProbability );
    }
  }

  mostProbStateSequence = backPropagation( deltat, noTimeSteps, indiciesMaxProb);
  return mostProbStateSequence;
}


public static int []  backPropagation(double[][] deltat, int noTimeSteps, int [][] indiciesMaxProb ){
  /*
  Backpropagation using the maximum path implemented in accordance with: "Speech and Language Processing:
  An introduction to natural language processing, computational linguistics, and speech recognition."" -
  Daniel Jurafsky & James H. Martin.
  */

  double maxProbLastTimeStep;
  int pointerMaxLastTimeStep;
  int [] mostProbStateSequence;

  maxProbLastTimeStep = maxFinder( getColumn( deltat, noTimeSteps - 1 ) );
  pointerMaxLastTimeStep = getIndexOfLargest( getColumn( deltat, noTimeSteps - 1 ) );

  mostProbStateSequence = new int [noTimeSteps];
  mostProbStateSequence[ noTimeSteps-1 ] = pointerMaxLastTimeStep;

  // Backpropagation through the using the backpointers to find the most (max) probable sequence of states.
  for (int t = noTimeSteps - 1 ; t > 0; t--){

    mostProbStateSequence[t-1] = indiciesMaxProb[pointerMaxLastTimeStep][t];
    pointerMaxLastTimeStep = indiciesMaxProb[pointerMaxLastTimeStep][t];

  }
  return mostProbStateSequence;
}


public static void main (String[] args){

  // A scanner used to read the transition and emission matricies and the
  // initial state probability distribution.
  Scanner inputScanner;

  inputScanner = new Scanner(System.in);

  // The HMM parameters.
  double [][] transitionMatrix, emissionMatrix;
  double [] initialStateProb;
  int [] observationSequence;
  int noStates, noTimeSteps, firstObservation;
  int [] mostProbStateSequence;
  String observationSequenceString;

  // Parameter used to store the (max) most probable state sequence.
  String mostProbStateSequenceString;

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


  // Initlize parameters fro the network
  firstObservation= observationSequence[0];
  noStates = transitionMatrix[0].length;
  noTimeSteps = observationSequence.length;

  /*
  Given λ = (A, B, π) and an observation sequence O, we find an optimal state sequence for the
  underlying Markov process. In other words, we want to uncover the hidden part of the
  Hidden Markov Model. Note that in this version we try to find the maximum path while another
  option could have been to take all possible paths combined maximum.
  */

  mostProbStateSequence = viterbiAlgorithm( noStates, noTimeSteps, firstObservation, transitionMatrix, emissionMatrix, initialStateProb, observationSequence);

  // The result is converted to a format accepted by kattis.se

  mostProbStateSequenceString = "" ;

  for ( int i = 0; i < mostProbStateSequence.length; i++ ){

    mostProbStateSequenceString += " " + Integer.toString( mostProbStateSequence[i] );

  }

  System.out.print("The most probable state sequence: ");
  System.out.print(mostProbStateSequenceString);
}
}
