import java.util.*;
import java.util.Arrays;
import java.util.Iterator;


public class HelperClass {

  public HelperClass() {
}

public  static double [] getColumn( double [][] matrix, int colNo ){
  /*
  Extracts a column from a matrix based on a column index.
  */
  double [] extractedColumn;
  int no_rows;

  no_rows = matrix.length;
  extractedColumn = new double[no_rows];

  for (int row = 0 ; row < no_rows ; row ++ ){

    extractedColumn[row] = matrix[row][colNo];

  }
  return extractedColumn;
}

public static double secondLargestValueLog( double[] array ) {
  /*
  Retrives the second largest logged (Smallest logged) value from an array.
  */

  double largestVal, secondLargestVal;
  int noVals;
  boolean foundLargerVal, elementsLeft, foundValInBetween;

  // Initilize arbitrarily small (or large if you unlog the values).
  largestVal = - 10000000;
  secondLargestVal = - 100000000;

  noVals = array.length;

  for (int i = 0; i < noVals; i++) {

    // Criteria for a new second largest value.
    foundLargerVal = ( array[i] > largestVal );
    elementsLeft = ( array[i] != 0 );
    foundValInBetween = ( array[i] > secondLargestVal );

    // If we foun a value we want to update the stored largest and second lagest value.
    if ( foundLargerVal && elementsLeft ) {

      secondLargestVal = largestVal;
      largestVal = array[i];

    } else if ( foundValInBetween && elementsLeft ) {

      secondLargestVal = array[i];
   }

}
return secondLargestVal;
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


public static int indexLargestLogVal(double [] array){
  // Retrives the index of the largest element in an array of logged values.

  double largetsVal;
  int noValues, indexLargetsVal;
  boolean foundLargerVal, elementsLeft;

  // Initilize arbitrarily small (or large if you unlog the values).
  largetsVal = - 10000000;

  // initilize to -1 to signal that no value was found.
  indexLargetsVal = - 1 ;

  noValues = array.length;

  for( int i = 0 ; i < noValues ; i ++ ){

    foundLargerVal = ( array[i] > largetsVal );
    elementsLeft = ( array[i] != 0 );

    if( foundLargerVal && elementsLeft ){

      largetsVal = array[i];
      indexLargetsVal = i;

    }
  }
  return indexLargetsVal;
}


public double [][] matrixMultiplication( double [][] Matrix1, double [][] Matrix2 ){
  /*
  Takes two matricies as inputScanner and performs matrix multiplication between them.
  */
  int i, j, k, m, n;

  int noRowsMatrix1 = Matrix1.length;
  int noColsMatrix1 = Matrix1[0].length;
  int noRowsMatrix2 = Matrix2.length;
  int noColsMatrix2 = Matrix2[0].length;

  // The resulting matrix will have dimensions given by the number of rows
  // of matrix 1 and the number of columns of matrix 2.
  double[][] MatrixMultiplied = new double [noRowsMatrix1][noColsMatrix2];

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


public  double [] getColumn( double [][] matrix, int colNo ){
  /*
  Extracts a column from a matrix based on a column index.
  */
  double [] extractedColumn;
  int no_rows;

  no_rows = matrix.length;
  extractedColumn = new double[no_rows];

  for (int row = 0 ; row < no_rows ; row ++ ){

      extractedColumn[row] = matrix[row][colNo];

  }

  return extractedColumn;
}


public static double maxFinderlog( double [] array ){
  // Retrives the value of the largest element in an array of logged values.

  double largetsVal;
  int noValues;
  boolean foundLargerVal, elementsLeft;

  // Initilize arbitrarily small (or large if you unlog the values).
  largetsVal = - 10000000;

  noValues = array.length;

  for( int i = 0 ; i < noValues ; i ++ ){

    foundLargerVal = ( array[i] > largetsVal );
    elementsLeft = ( array[i] != 0 );

    if( foundLargerVal && elementsLeft ){

      largetsVal = array[i];

    }

  }
  return largetsVal;
}




}
