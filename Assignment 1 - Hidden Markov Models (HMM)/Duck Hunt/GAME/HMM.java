import java.util.*;
import java.util.Arrays;
import java.lang.Math;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.DoubleStream;
import static java.lang.System.*;
import java.util.Random;


public class HMM{

  // Matricies used to store the estimate parameters of the HMM model.
  double [][] transitionMatrix;
  double [][] emissionMatrix;
  double [] initialStateProb;

  // Matricies used to estimate the parameters of the HMM model.
  double [][][] diGammat;
  double [][] gammat;

  // Number of states, time steps and number of observations for the model.
  int noStates = 5;
  int noEmissions = 9;
  int noTimeSteps;

  // Used for scaling during the baum welch algorithm.
  double [] C;
  double denominator;
  double numerator;

  // Seed used to initilize the HMM parameters.
  int seed = 397169450;

  // Used to avoid division by zero at several points in the code.
  final double epsilon = (Double) Math.pow(10, -50);


  public HMM() {

    initilizeHmmModel();

  }


  public void initilizeHmmModel(){
    /*
    initilizes the HMM model parameters:  λ = (A, B, π) such that a bird is more
    likely to stay in a state instead of transitioning to a new state.
    */

    // Used to set a seed to get the same pseudo random number generation process
    // when numbers are sampled for the model parameters.
    Random rand;

    rand = new Random();
    rand.setSeed( seed );

    this.transitionMatrix = new double[noStates][noStates];
    this.emissionMatrix = new double[noStates][noEmissions];
    this.initialStateProb = new double[noStates];


    // The matricies are initilized via the pseudo random number generator.
    initilizeTransitionMatrix( rand );
    initilizeEmissionMatrix( rand );
    initilizeInitialStateDistribution( rand );

  }


  public void initilizeTransitionMatrix( Random rand ){
    /*
    Initilizes the HMM model transition matrix so that a bird is more
    likely to stay in a state instead of transitioning to a new state.
    */

    boolean transitionToSameState;

    for (int i = 0; i < noStates; i++){

      double row_sum = 0;
      for (int j = 0; j < noStates; j++){

        transitionToSameState = (i == j);

        // If we are settng a value for a transition between the same state
        // make this value arbitrarily high such taht it is more likely to
        // stay in the same state.
        if( transitionToSameState ){

          this.transitionMatrix[i][j] = 90;
          row_sum += this.transitionMatrix[i][j];

        // Else make the transition less likely.
        }else{

          this.transitionMatrix[i][j] = (9 + (12 - 9)  * rand.nextDouble());
          row_sum += this.transitionMatrix[i][j];

        }
      }

      // Normalize the values to get a distribution.
      for ( int k = 0; k < noStates; k ++ ){

        this.transitionMatrix[i][k] = this.transitionMatrix[i][k] / ( row_sum + epsilon );

      }
    }
  }


  public void initilizeEmissionMatrix( Random rand ){
    /*
    Initilizes the HMM model Emission matrix.
    */

    for ( int i = 0; i < noStates; i ++ ){

      double row_sum = 0;
      // The values are set arbitrarily.
      for ( int j = 0; j < noEmissions; j ++ ){

        this.emissionMatrix[i][j] = (9 + (12 - 9)  * rand.nextDouble());
        row_sum += this.emissionMatrix[i][j];

      }

      // Normalize the values to get a distribution.
      for (int k = 0; k < noEmissions; k ++ ){

        this.emissionMatrix[i][k] = this.emissionMatrix[i][k] /( row_sum + epsilon );

      }
    }
  }

  public void initilizeInitialStateDistribution( Random rand ){
    /*
    Initilizes the HMM model probability distribution for the initial states in
    the first times step.
    */

    double row_sum = 0;

    for ( int i = 0; i < noStates; i ++ ){

      this.initialStateProb[i] = (9 + (12 - 9) * rand.nextDouble());

      row_sum += this.initialStateProb[i];
    }

    for ( int k = 0; k < noStates; k ++ ){

      this.initialStateProb[k] = this.initialStateProb[k] / ( row_sum + epsilon );

    }
  }


  public  double [][] alphaPassScaling( int [] observstionSeq ){
    /*
      alpha forward pass with scaling and that returns a logged probability for the
      observation sequence given the current estimated model parameters:  λ = (A, B, π).

      This probability is used to measure convergence so that we can know if the
      model is trained enough to shoot and guess which birds we are seeing.

    */

    // c is used for scaling the parameters.
    double [] cLocal;
    // alphat is used for the forward pass.
    double [][] alphat;

    cLocal = new double[noTimeSteps];
    alphat = new double [noStates][noTimeSteps];

    cLocal[0] = 0;

    // Computing all the values for the first time step: alpha_{0}(state).
    for (int state = 0; state < noStates; state ++ ) {

      alphat[state][0] = initialStateProb[state] * emissionMatrix[state][observstionSeq[0]];
      cLocal[0] += alphat[state][0];

    }

    // Scale the alpha values of the initial state.
    cLocal[0] = 1 / ( cLocal[0] + epsilon );

    for (int state = 0; state < noStates; state ++ ) {

      alphat[state][0] = cLocal[0] * alphat[state][0];

    }

    // Computing all the values for all states for all time steps except for
    // the first: alpha_{t}(state).
    for (int t = 1; t < noTimeSteps; t++) {

      cLocal[t]=0;

      for (int i = 0; i < noStates; i++) {

        cLocal[i] = 0;

        for (int j = 0; j < noStates; j++) {

          alphat[i][t] += alphat[j][t-1] * transitionMatrix[j][i];

        }

        alphat[i][t] = alphat[i][t] * emissionMatrix[i][observstionSeq[t]];
        cLocal[t] += alphat[i][t];
      }

      // Scale the alpha values of all states and time steps.

      cLocal[t] = 1 / ( cLocal[t] + epsilon );

      for (int state = 0; state < noStates; state ++ ) {

        alphat[state][t] = cLocal[t] * alphat[state][t];

      }
    }

    return alphat;
  }


  public double alphaPassProb( int [] observstionSeq ){
    /*
      alpha forward pass with scaling and that returns a logged probability for the
      observation sequence given the current estimated model parameters:  λ = (A, B, π).

      This probability is used to measure convergence so that we can know if the
      model is trained enough to shoot and guess which birds we are seeing.

    */

    // c is used for scaling the parameters.
    double [] cLocal;
    // alphat is used for the forward pass.
    double [][] alphat;

    cLocal = new double[noTimeSteps];
    alphat = new double [noStates][noTimeSteps];

    cLocal[0] = 0;

    // Computing all the values for the first time step: alpha_{0}(state).
    for (int state = 0; state < noStates; state ++ ) {

      alphat[state][0] = initialStateProb[state] * emissionMatrix[state][observstionSeq[0]];
      cLocal[0] += alphat[state][0];

    }

    // Scale the alpha values of the initial state.
    cLocal[0] = 1 / ( cLocal[0] + epsilon );

    for (int state = 0; state < noStates; state ++ ) {

      alphat[state][0] = cLocal[0] * alphat[state][0];

    }

    // Computing all the values for all states for all time steps except for
    // the first: alpha_{t}(state).
    for (int t = 1; t < noTimeSteps; t++) {

      cLocal[t]=0;

      for (int i = 0; i < noStates; i++) {

        cLocal[i] = 0;

        for (int j = 0; j < noStates; j++) {

          alphat[i][t] += alphat[j][t-1] * transitionMatrix[j][i];

        }

        alphat[i][t] = alphat[i][t] * emissionMatrix[i][observstionSeq[t]];
        cLocal[t] += alphat[i][t];
      }

      // Scale the alpha values of all states and time steps.

      cLocal[t] = 1 / ( cLocal[t] + epsilon );

      for (int state = 0; state < noStates; state ++ ) {

        alphat[state][t] = cLocal[t] * alphat[state][t];

      }
    }

    // Calculating the current probability to measure convergence
    // so that we can know if the model is trained enough to shoot and
    // guess which birds we are seeing.

    double logProb = 0;

    for( int t = 0 ; t < noTimeSteps; t++ ){

      logProb += Math.log( cLocal[t] + epsilon );

    }

    logProb = - logProb;

    return logProb;
  }

  public static double[][] transposeMatrix(double [][] matrixToTrans){
    /*
      Transposes a matrix.
    */
    double [][] transposedMatrix;
    int noRows, noCols;

    noCols = matrixToTrans.length;
    noRows = matrixToTrans[0].length;

    transposedMatrix = new double[noCols][noRows];

    for (int i = 0; i < noCols; i++){

      for (int j = 0; j < noRows; j++){

          transposedMatrix [j][i] = matrixToTrans[i][j];
      }
    }

    return transposedMatrix;
  }


  public  double [][] alfapassScaling(int [] observationSequence){
    // The forward algorithm i.e the forward pass with scaling.

    double [][] alphat;

    alphat = new double [noStates][noTimeSteps];

    C[0] = 0;

    // Computing all the values for the first time step: alpha_{0}(state).
    for (int i = 0; i < noStates; i++) {

      alphat[i][0] = initialStateProb[i] * emissionMatrix[i][observationSequence[0]];
      C[0] += alphat[i][0];

    }

    // Scale the alpha values of the initial state.
    C[0] =  1 / (C[0] + epsilon);

    for (int i = 0; i < noStates; i++) {

      alphat[i][0] = C[0] * alphat[i][0];

    }

    // Computing all the values for all states for all time steps except for
    // the first: alpha_{t}(state).
    for (int t = 1; t < observationSequence.length; t++) {

      C[t] = 0;

      for (int i = 0; i < noStates; i++) {

        alphat[i][t] = 0;

        for (int j = 0; j < noStates; j++) {

          alphat[i][t] = alphat[i][t] + alphat[j][t-1] * transitionMatrix[j][i];

        }

        alphat[i][t] = alphat[i][t] * emissionMatrix[i][observationSequence[t]];
        C[t] = C[t] + alphat[i][t];

      }
      // Scale the alpha values of all states and time steps.
      C[t] = 1 / (C[t] + epsilon);

      for (int i = 0; i < noStates; i++) {

        alphat[i][t] = C[t] * alphat[i][t];

      }
    }

  return alphat;
  }


  public double [][] betaPass( int [] observationSequence ){
    /* The Backward Procedure is implemented in accordance with: A Revealing Introduction to Hidden Markov Models, by Mark Stamp∗
    at Department of Computer Science - San Jose State University, https://www.cs.sjsu.edu/~stamp/RUA/HMM.pdf
    */

    double [][] betat;

    betat = new double [noStates][noTimeSteps];

    // Let β_{T−1}(i), be scaled by c_{T −1}
    for( int state = 0 ; state < noStates ; state ++ ){

      betat[state][ noTimeSteps - 1 ] = C[ noTimeSteps - 1 ];

    }

    // The beta pass.
    for ( int t = noTimeSteps - 2 ; t >= 0 ; t -- ){

      // Going through all states at the current time step.
      for ( int stateCurrT = 0 ; stateCurrT < noStates ; stateCurrT ++ ){

        // Going through all states at the next time step.
        for ( int statePrevT = 0 ; statePrevT < noStates; statePrevT++ ){

          betat[stateCurrT][t] = betat[stateCurrT][t] + transitionMatrix[stateCurrT][statePrevT] * emissionMatrix[statePrevT][observationSequence[t+1]] * betat[statePrevT][t+1];

        }

        betat[stateCurrT][t] = C[t] * betat[stateCurrT][t];
      }
    }

    return betat;
  }


  public void diGammaGamma(double [][] betat, double [][] alphat, int [] observationSequence){
    /* Computes gamma and digamma.

    diGammat is the probability of being in state i at time t given the observed sequence
     O and the parameters λ = (A, B, π).

     gammat is the probability of being in state i and j at times t and t+1 respectively
      given the observed sequence O and the parameters λ = (A, B, π).
    */

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
      for ( int stateCurrT = 0 ; stateCurrT < noStates; stateCurrT ++ ){

        gammat[stateCurrT][t] = 0;

        // For all states at the next time step that we could transition to.
        for ( int statePrevT = 0 ; statePrevT < noStates; statePrevT ++ ){

          // Avoid division by zero.
          if (denominator != 0){

             // Computing digamma by dividing the probability of being in state i at time
             // t given the observed sequence O and the parameters λ = (A, B, π) by  the
             // probability of making the observation O given the parameters λ = (A, B, π).

             diGammat[stateCurrT][statePrevT][t] = ( alphat[stateCurrT][t] * transitionMatrix[stateCurrT][statePrevT] * emissionMatrix[statePrevT][observationSequence[t+1]] * betat[statePrevT][t+1]) / ( denominator + epsilon );

             // Computing gamma.
             gammat[stateCurrT][t] += diGammat[stateCurrT][statePrevT][t];

          }
        }
      }
    }

    /*
    The (denominator) Denominator parameter is equivalent to the observation
    O given the parameters λ = (A, B, π).
    */

    denominator = 0;

    for ( int state = 0 ; state < noStates ; state ++ ){

      denominator += alphat[state][noTimeSteps-2];

    }

    for (int state = 0; state < noStates; state ++ ){

      gammat[state][noTimeSteps - 2] = alphat[state][noTimeSteps - 2] / (denominator+epsilon);

    }
  }


  public void reEstimateInitialStateProb(){
    /*
    The probability distribution for the initial state is computed as the
    expected frequency spent in state i at time  1.
    */

    // Compute the expected frequency spent in every state at the first time step.
    for (int state = 0; state < noStates; state ++ ) initialStateProb[state] = gammat [state][0];
  }


  public void reEstimateTransistionMatrix(){
    /* The transition matrix is estimated as the expected number of transitions from a state to
    the next state at the next time step compared to  the expected total number of transitions
    away from a state. To clarify, the number of transitions away from a state does not mean
    transitions to a different state , but to any state including itself.
    */

    double noTransitions;
    double totalNoTransitions;

    transitionMatrix = new double [noStates][noStates];

    for (int stateCurrT = 0; stateCurrT < noStates; stateCurrT ++ ){

      for (int nextStateT = 0; nextStateT < noStates; nextStateT ++ ){

         noTransitions = 0;
         totalNoTransitions = 0;

         for (int t = 0; t < noTimeSteps - 1; t ++ ){

           // The expected number of transitions from state stateCurrT to state nextStateT.
           noTransitions =+ diGammat[stateCurrT][nextStateT][t];

           // The expected total number of transitions away from a state.
           totalNoTransitions =+ gammat[stateCurrT][t];

         }

         transitionMatrix[stateCurrT][nextStateT] = noTransitions / totalNoTransitions;
      }
    }
  }


  public void reEstimateEmissionMatrix(int [] observationSequence){
    /*
    The emission matrix is equivalent to the number of times a state is observed
     in the sequence from t = 1 to t = T − 1.
    */

    double numerator;
    double denominator;

    for (int i = 0; i< noStates; i ++ ){

      for (int j = 0; j < emissionMatrix[0].length; j ++ ) {

      numerator = 0;
      denominator = 0;

      for ( int t = 0; t < noTimeSteps; t ++ ){

        // Update the frequency of observing the current state in the current time step and state.
        if( observationSequence[t] == j )  numerator += gammat[i][t];

        // Update the frequency of  transitioning between  these two states.
        denominator += gammat[i][t];
        }

        emissionMatrix[i][j] = numerator / denominator;
      }
    }
  }


  public double computeConvergenceCondition(){
    /*
      Compute the convergence condition using: log[P(O | λ)]
    */

    double logProb;

    logProb = 0;

    for( int state = 0; state < noTimeSteps; state ++ ){

      logProb = logProb + Math.log( C[state] );

    }

    logProb = -logProb;

    return logProb;
  }


  public double BaumWelch (int [] observationSequence){
    /*
    The Baum–Welch (a special case of the EM algorithm) used to find the unknown parameters of the
    hidden Markov model (HMM). It makes use of the forward-backward algorithm to compute the statistics
    for the expectation step. Given an observation sequence O and the dimensions N and M, it finds a
    model λ = (A, B, π) that maximizes the probability of O. This can be viewed as training a model
    to best fit the observed data. Alternatively, we can view this as a (discrete) hill climb on
    the parameter space represented by A, B and π.
    */

    // Used to estimate the HMM parameters.
    double [][] alphat, betat;
    int firstObservation;
    double oldLogProb, logProb;

    int maxNoIterations, currIteration;

    // Reset the old gamma and digamma values.
    diGammat = new double[noStates][noStates][noTimeSteps];
    gammat = new double[noStates][noTimeSteps];

    // Also, reset the scaling parameter.
    C = new double [noTimeSteps];

    firstObservation = observationSequence[0];


    // Setting max number of iterations to a large value.
    maxNoIterations = 1000;
    currIteration = 0;

    // Setting this variable to an arbitary small value.
    oldLogProb = - Double.MAX_VALUE;
    logProb = 0;

    // The first update of the Baum Welsh algorithm.

    /* Maximization step of the parameters used to estimate the HMM. */
    // Forward procedure.
    alphat = alfapassScaling( observationSequence );

    // Backward procedure.
    betat = betaPass( observationSequence );
    diGammaGamma( betat, alphat, observationSequence );

    /* Estimation step of the HMM parameters. */
    reEstimateInitialStateProb();
    reEstimateTransistionMatrix();
    reEstimateEmissionMatrix( observationSequence );

    // Update the convergence criteria.
    logProb = computeConvergenceCondition();
    currIteration ++;

    // Check for convergence or until the maximum number of iterations is reached.
    while ( logProb > oldLogProb && currIteration < maxNoIterations ){

      // Update the convergence parameter.
      oldLogProb = logProb;

      /* Maximization step of the parameters used to estimate the HMM. */
      // Forward procedure.
      alphat = alfapassScaling( observationSequence );

      // Backward procedure.
      betat = betaPass( observationSequence) ;

      /* Update/Estimation step of the HMM parameters. */
      // Temporary varibles computed using Bayes' theorem.

      // diGammat is the probability of being in state i at time t given the observed sequence
      // O and the parameters λ = (A, B, π).

      // gammat is the probability of being in state i and j at times t and t+1 respectively
      // given the observed sequence O and the parameters λ = (A, B, π).
      diGammaGamma (betat,alphat,observationSequence);

      // The probability distribution for the initial state is computed as the
      // expected frequency spent in state i at time  1.
      reEstimateInitialStateProb();

      // The transition matrix is estimated as the expected number of transitions from state i to state j
      //compared to  the expected total number of transitions away from state i. To clarify,
      //the number of transitions away from state i does not mean transitions to a
      //different state j, but to any state including itself.
      reEstimateTransistionMatrix();

      // The emission matrix is equivalent to the number of times state i is observed
      // in the sequence from t = 1 to t = T − 1.
      reEstimateEmissionMatrix( observationSequence );

      // Update the convergence criteria.
      logProb = computeConvergenceCondition();
      currIteration ++;
    }
    return logProb;
  }

}
