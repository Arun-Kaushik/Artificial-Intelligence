import java.util.*;
import java.util.Arrays;
import java.util.Iterator;


public class HelperMethods {

  public HelperMethods() {

  }

  public static int [] getColumn( int [][] matrix, int colNo ){
    /*
    Extracts a column from a matrix based on a column index.
    */
    int [] extractedColumn;
    int no_rows;

    no_rows = matrix.length;
    extractedColumn = new int[no_rows];

    for (int row = 0 ; row < no_rows ; row ++ ){

      extractedColumn[row] = matrix[row][colNo];

    }
    return extractedColumn;
  }

  public static Double getLargerValue( double vold, double vnew ){

    if(vold>= vnew){
     return vold;
   }
    return vnew;
  }

  public static Double getSmallerValue(double vold, double vnew){
    if(vold <=  vnew){
    return vold;
  }
    return vnew;
  }

  /*

  public Double getLargerValue( double vold, double vnew ){

    if(vold>= vnew){
     return vold;
   }
    return vnew;
  }

  public Double getSmallerValue(double vold, double vnew){
    if(vold <=  vnew){
    return vold;
  }
    return vnew;
  }
*/


  public static int maxFinder( int [] array ){
    // Retrives the largest element in an array.

    int currentLargestValue;

    currentLargestValue = array[0];

    for( int i = 1; i < array.length; i++ ){

        if( array[i] > currentLargestValue ){

           currentLargestValue = array[i];

        }
    }
    return currentLargestValue;
  }



}
