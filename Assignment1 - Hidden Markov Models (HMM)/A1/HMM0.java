import java.io.FileNotFoundException;
import java.io.File;
import java.util.Scanner;
import java.util.Arrays;
import java.lang.Math;


public class HMM0{

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


public static float[][]  matrixFiller(String inputLine){
  /* Fills an empty matrix from the content of a numerical text string values separated by white spaces.
  */
  String [] lineArray = inputLine.split( "\\s+" );
  String[] cloneOfArray = Arrays.copyOf( lineArray, lineArray.length );

  int no_rows, no_cols, itterator;

  no_rows = Integer.parseInt( lineArray[0] );
  no_cols = Integer.parseInt( lineArray[1] );

  itterator = 2;

  float[][] matrixFill = new float [no_rows][no_cols];

  // For every element in the matrix we place a value from the text string input.
  for(int row = 0; row < matrixFill.length; row ++) {
    for(int col = 0; col < matrixFill[row].length; col++){

        matrixFill[row][col] = Float.parseFloat( lineArray[itterator] );
        itterator++;
    }
  }
  return matrixFill;
}

public static float[][]  readStringMatrix( Scanner inputScanner ){
  /*
  Reads a matrix from a line input and fills a matrix/array.
  */
  String matrix_string = inputScanner.nextLine();
  float [][] matrix = matrixFiller( matrix_string );

  return matrix;
}




public static void main (String[] args){

        // A scanner used to read the transition and emission matricies and the
        // initial state probability distribution.
        Scanner inputScanner;
        inputScanner = new Scanner(System.in);

        // The transition and emission matricies and the initial state probability
        // distribution.
        float [][] transitionMatrix = readStringMatrix( inputScanner );
        float [][] emissionMatrix = readStringMatrix( inputScanner );
        float [][] initialStateProb = readStringMatrix( inputScanner );

        // A transition from the initial state to the next state using the initial state probablities and the
        // transition matrix.
        float [][] secondStateProb = matrixMultiplication( initialStateProb, transitionMatrix );

        // Probabilities for the next possible emissions after the first transition, i.e. in the second state.
        float [][] emissionProbsSecondState = matrixMultiplication( secondStateProb, emissionMatrix);


        // Presenting the result on a format adapted for kattis.se
        int final_row; int final_col; String emissionProbsSecondStateString;

        final_row = emissionProbsSecondState.length;
        final_col = emissionProbsSecondState[0].length;

        emissionProbsSecondStateString = Integer.toString(final_row) + " " + Integer.toString( final_col );

        for( float val: emissionProbsSecondState[0] ){
            emissionProbsSecondStateString += " " + val;
        }

        System.out.print("Emission probabilities in the second state, i.e. after making one transition: ");
        System.out.println(emissionProbsSecondStateString);
    }
}
