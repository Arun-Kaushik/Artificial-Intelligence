import java.util.*;
import java.util.Arrays;
import java.util.Iterator;


public class HelperMethods {

  public HelperMethods() {

  }

  public static Double getLargerValue( double value1, double value2 ){
    /*
      Returns the larger value out of two provided values.
    */

    if( value1 >= value2 ){

     return value1;

    }

    return value2;
  }


  public static Double getSmallerValue( double value1, double value2 ){
    /*
      Returns the smaller value out of two provided values.
    */

    if ( value1 <=  value2 ){

      return value1;

    }

    return value2;
  }

}
