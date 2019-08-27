//kolla hur manga modeller vi sparar. om vi bara sparar 6 stycken alltid.
//lagg till en vektor med logprobs for varje modell och jamfor om det finns ngn battre

/*
1. valj den basta modellen for varje fagel ex pigeon no 20. Gor detta for alla faglar
2.valj fageln med bast P(O|lambda)

*/

import java.util.*;
import java.util.stream.*;
import java.util.Arrays;
import java.util.Arrays.*;
import java.util.Iterator;

class Player {
  int time = 0;
  //public HMMnew [] birdHmmList=new HMMnew[Constants.COUNT_SPECIES];
  public ArrayList[] birdModelsHMM=new ArrayList[6]; //We wanna save all HMM models for every bird. Therefore we create a dynamic matrix
  final double epsilon =(Double) Math.pow(10, -50);
  int noStates = 5;
  int noEmissions = 9;
  int cG, fG,cS,tS;


  public HMMnew [] hmmsPerSpec =new HMMnew[Constants.COUNT_SPECIES];

  int [] lGuess;


    public Player() {

      cG = 0;
        fG = 0;
      cS=0;
      tS=0;

      for (int j=0; j<Constants.COUNT_SPECIES;j++){

        //birdModelsHMM[j]= new ArrayList(); //we wanna add a dynamic arraylist at
        birdModelsHMM[j]= new ArrayList<HMMnew>();
        //every position in arraylist. but we want to outer list to be an array
        //so that we can use the itterator
      }

    }

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
     public  double [] getColumn(double [][] matrix1,int c){
                double [] extractVector =new double[matrix1.length];
                for (int i=0; i<matrix1.length;i++){
                        extractVector[i]=matrix1[i][c];

                }return extractVector;
        }

public double [] findShootEmission(double [] emissionProbs,int [] pMovements){
  double [] res =new double [noEmissions];
  int [] noOcurr =new int [noEmissions];
  //System.err.println(Arrays.toString(pMovements));
  //System.err.println(Arrays.toString(emissionProbs));


  for(int i=0;i<pMovements.length;i++){

    if (pMovements[i]!=-1){ //ev ta bort detta
    noOcurr[pMovements[i]]+=1;
    res[pMovements[i]]+=emissionProbs[i];
  }else{
    res[9]+=emissionProbs[i];
    noOcurr[9]+=1;
  }
  }
  for(int i=0;i<res.length;i++){
    if(res[i]!=0){
    res[i]=res[i]/noOcurr[i];
  }
}
//System.err.println(Arrays.toString(noOcurr));
//System.err.println(Arrays.toString(res));
//System.err.println();
return res;

}
//
// public Action shoot(GameState pState, Deadline pDue) {
//   time++;
//   //System.err.println("round "+pState.getRound());
//   //System.err.println("v1 "+70+99*pState.getRound());
//   //System.err.println("time "+time);
//   int villkor =20+99*pState.getRound();
//
//         if( pState.getRound()!=0 && time>villkor){
//           //lagga while loop eftersom vi ej skjuter faglar
//           //while()
//
//           for(int i = 0; i < pState.getNumBirds(); ++i){
//
//             //System.err.println("###### For bird; "+i+"###### ");
//             if(pState.getBird(i).isAlive()){ // om if har for tidigare eller while har enbart
//
//             int noBirdObs = pState.getBird(i).getSeqLength();
//             int [] observstionSeq = new int [noBirdObs];
//             for (int p=0; p < noBirdObs; p++){
//               observstionSeq[p]=pState.getBird(i).getObservation(p);
//             }
//
//
//             double [] sumBirdEmissions=new double [noEmissions];
//             Arrays.fill(sumBirdEmissions,0);
//
//
//             for(int g = 0; g<Constants.COUNT_SPECIES; g++){ // ga igenom alla arter for en fagel
//               //System.err.println("The bird spieces "+g);
//             Iterator<HMMnew> it;
//             it = birdModelsHMM[g].iterator();
//
//             //for (HMMnew birdHmm: birdHmmList){
//
//             ArrayList<Double> lopProbBirdModelPicker =new ArrayList <Double>();
//
//
//             while (it.hasNext()){ //varje modell for varje art
//               HMMnew birdHmm =it.next();
//               //double [][] birdAlfaPass =birdHmm.alfaPassNO(observstionSeq);//alfapassnoscaling
//
//
//
//
//
//
//
//               double [][] birdAlfaPass =birdHmm.alfaPassNO(observstionSeq);//alfapassnoscaling
//               //System.err.println("##############################");
//               //System.err.println(Arrays.deepToString(birdAlfaPass));
//               //System.err.println();
//               double [] StateProbLastTimeStep =birdHmm.getColumn(birdAlfaPass,birdAlfaPass[0].length-1);
//               //System.err.println();
//               //System.err.println(Arrays.toString(StateProbLastTimeStep));
//
//
//               double [][] StateProbLastTimeStepMatrix = new double [1][StateProbLastTimeStep.length];
//               //System.err.println();
//               //System.err.println(Arrays.deepToString(StateProbLastTimeStepMatrix));
//             //  System.err.println();
//
//
//               for(int d=0;d<StateProbLastTimeStepMatrix.length;d++){
//                 for(int k=0;k<StateProbLastTimeStepMatrix[d].length;k++){
//                   StateProbLastTimeStepMatrix[d][k]=StateProbLastTimeStep[k];
//               }
//             }
//             //System.err.println(Arrays.deepToString(StateProbLastTimeStepMatrix));
//
//               //double [][] transposedTransMatrix = birdHmm.transposeMatrix(birdHmm.transitionMatrix);
//               double [][] probNextState = birdHmm.matrixMultiplication(StateProbLastTimeStepMatrix,birdHmm.transitionMatrix);
//               //System.err.println("##############################");
//               double [][] emissionDistributionNextState = birdHmm.matrixMultiplication(probNextState ,birdHmm.emissionMatrix);
//               //System.err.println(Arrays.deepToString(emissionDistributionNextState));
//                double [] emissionDistributionNextStateVector=emissionDistributionNextState[0];
//                //System.err.println(Arrays.toString(emissionDistributionNextStateVector));
//                //System.err.println(Arrays.toString(sumBirdEmissions));
//                for(int f=0; f<emissionDistributionNextStateVector.length;f++){
//                  sumBirdEmissions[f]+=emissionDistributionNextStateVector[f];
//                  //System.err.println(sumBirdEmissions[f]);
//                }
//                //System.err.println(Arrays.toString(sumBirdEmissions));
//                //System.err.println("##############################");
//
//             }
//           }
//           //Nu ar vi klara med en fagel
//
//
//
//           int emind =indexOfMaxFinder(sumBirdEmissions);
//           double probMax=maxFinder(sumBirdEmissions);
//           System.err.println(probMax);
//           System.err.println("stork "+sumBirdEmissions[Constants.SPECIES_BLACK_STORK]);
//
//           if (probMax>13 && emind != Constants.SPECIES_BLACK_STORK && sumBirdEmissions[Constants.SPECIES_BLACK_STORK]<7){//probMax>13 && emind != Constants.SPECIES_BLACK_STORK && sumBirdEmissions[Constants.SPECIES_BLACK_STORK]<3){ //lagg till vilkor typ: om blackstork prob ar under
//             System.err.println("heej"+ i);
//             tS++;
//             return new Action(i,emind);
//           }else{
//             return cDontShoot;
//           }
//
//           //Nu har vi valt ut det basta hmm;erna for varje art
//         }
//       }
//
//
//
//           //      HMMnew birdHmm =iterator.next();
//         //         double [][] emissionDistributionNextState;
//         //           //if(birdHmm !=null){
//         //
//         //           double [][] birdAlfaPass =birdHmm.alfaPassNO(observstionSeq);//alfapassnoscaling
//         //           double [] StateProbLastTimeStep =birdHmm.getColumn(birdAlfaPass,birdAlfaPass[0].length-1);
//         //           double [][] StateProbLastTimeStepMatrix = new double [1][StateProbLastTimeStep.length];
//         //
//         //
//         //
//         //           for(int d=0;d<StateProbLastTimeStepMatrix.length;d++){
//         //             for(int k=0;k<StateProbLastTimeStepMatrix[d].length;k++){
//         //               StateProbLastTimeStepMatrix[d][k]=StateProbLastTimeStep[k];
//         //           }
//         //         }
//         //           double [][] probNextState = birdHmm.matrixMultiplication(StateProbLastTimeStepMatrix,birdHmm.transitionMatrix);
//         //           emissionDistributionNextState = birdHmm.matrixMultiplication(probNextState ,birdHmm.emissionMatrix);
//         //           double [] emissionDistributionNextStateVector=emissionDistributionNextState[0];
//         //
//         //           double maxHmmEmission=maxFinder(emissionDistributionNextStateVector);
//         //           int maxHmmEmissionType = indexOfMaxFinder(emissionDistributionNextStateVector);
//         //           emissionProbs[itter]=maxHmmEmission;
//         //           pMovements[itter]=maxHmmEmissionType;
//         //
//         //         //}
//         //         itter++;
//         //         }
//         //
//         //
//         //         double [] totProbsShooting =findShootEmission(emissionProbs,pMovements); //pMovements[maxEmissionProbTotIndex];
//         //         int maxEmissionProbTotIndex = indexOfMaxFinder(totProbsShooting);
//         //         double maxEmissionProbTot = maxFinder(totProbsShooting);
//         //
//         //         //System.err.println(Arrays.toString(emissionProbs));
//         //         double shootCriteria=0.40;
//         //
//         //         if(maxEmissionProbTotIndex != Constants.SPECIES_BLACK_STORK)  {
//         //           //System.err.println();
//         //           tS+=1;
//         //
//         //           return new Action(i,maxEmissionProbTotIndex);
//         //         }else{
//         //           return cDontShoot;
//         //         }
//         //         //if(maxEmissionProbTotIndex == Constants.SPECIES_BLACK_STORK && maxEmissionProbTot>shootCriteria){
//         //           //samt kolla att sannolikeheten ar tillrackligt stor
//         //           //System.err.println("max em prob :"+maxEmissionProbTot);
//         //           //System.err.println("max em prob :"+(Double) Math.exp(maxEmissionProbTot ));
//         //           //System.err.println();
//         //
//         //         //(Double) Math.pow(10, -50);
//         //
//         //       }
//         //         }
//         //   }
//         // }else{
//         //   //time=0;
//         //   return cDontShoot;
//         //
//         // }
//
//          // for the first round or default in other rounds when u don't have time left or the prob for an emission was to low
//       }
//       return cDontShoot;
//     }



public int findBestBirdToShoot(double [] logProbsBirds ,int [] bestBirdsSpieces){
  int index = indexOfMaxFinderlog(logProbsBirds);

  for(int i =0; i <bestBirdsSpieces.length ; i++){
    if (bestBirdsSpieces[index] == Constants.SPECIES_BLACK_STORK || bestBirdsSpieces[index] == Constants.SPECIES_UNKNOWN){
    logProbsBirds[index]=0;}
  }

  index = indexOfMaxFinderlog(logProbsBirds);

  return index;

}

//################################
public Action shoot(GameState pState, Deadline pDue) {
  //newest ###################
  time++;
  //System.err.println("round "+pState.getRound());
  //System.err.println("v1 "+70+99*pState.getRound());
  //System.err.println("time "+time);
  int villkor =86+99*pState.getRound();

        if( pState.getRound()>2 && time>villkor){ // best 2
          //lagga while loop eftersom vi ej skjuter faglar
          //while()

          double maxLogProb = -Double.MAX_VALUE;

          //double allProbs []

          double [] logProbsBirds = new double [pState.getNumBirds()];
          HMMnew [] bestBirdsHMMs = new HMMnew [pState.getNumBirds()];
          int [] bestBirdsSpieces = new int [pState.getNumBirds()];

          for(int i = 0; i < pState.getNumBirds(); ++i){
            if(pState.getBird(i).isAlive() && pState.getBird(i).getSeqLength()>63){ // om if har for tidigare eller while har enbart

            int noBirdObs = pState.getBird(i).getSeqLength();
            int [] observstionSeq = new int [noBirdObs];
            for (int p=0; p < noBirdObs; p++){
              observstionSeq[p]=pState.getBird(i).getObservation(p);
            }


            for(int g = 0; g<Constants.COUNT_SPECIES; g++){ // Constants.COUNT_SPECIES-1 we assum that all the hmms will return low probabilities for storks. therefore we dont use the stork hmms to avoid bestBird being a stork
            Iterator<HMMnew> it;
            it = birdModelsHMM[g].iterator();
            //for (HMMnew birdHmm: birdHmmList){
            ArrayList<Double> lopProbBirdModelPicker =new ArrayList <Double>();

            while (it.hasNext()){ //varje modell for varje art
              HMMnew birdHmm =it.next();
              //double [][] birdAlfaPass =birdHmm.alfaPassNO(observstionSeq);//alfapassnoscaling
              double probObsSeqLog =birdHmm.alfaPassNOprob(observstionSeq);//alfapassnoscaling
              if(probObsSeqLog>maxLogProb){
                maxLogProb=probObsSeqLog;
                logProbsBirds[i]=probObsSeqLog;
                bestBirdsSpieces[i] = g;
                bestBirdsHMMs[i]=birdHmm;

              }

            }

        }
      }
    }

            //System.err.println("logprobs models "+Arrays.toString(logProbsBirds));
            //System.err.println("maxLogProb "+maxLogProb);
            int bestBird = findBestBirdToShoot(logProbsBirds, bestBirdsSpieces);
            int theBestBirdsSpieces=bestBirdsSpieces[bestBird];


            int noBirdObs = pState.getBird(bestBird).getSeqLength();
            int [] observstionSeq = new int [noBirdObs];

            if(maxLogProb>460 && bestBird !=-1 && theBestBirdsSpieces != Constants.SPECIES_BLACK_STORK){
              //double [] sumBirdEmissions=new double [noEmissions];

                  double []sumBirdEmissions=new double [noEmissions];

                  Arrays.fill(sumBirdEmissions,0);

                  //for(int g = 0; g<Constants.COUNT_SPECIES-1; g++){

                    Iterator<HMMnew> it2;
                    double [] tmpsumBirdEmissions=new double [noEmissions];

                    it2 = birdModelsHMM[theBestBirdsSpieces].iterator();

                    for (int p=0; p < noBirdObs; p++){
                      observstionSeq[p]=pState.getBird(bestBird).getObservation(p);
                    }

                    int no =1;
                    int maxnextemission=0;
                    double maxprobnextemission=0;

                    //while(it2.hasNext()){
                    HMMnew birdHmm =bestBirdsHMMs[bestBird];// it2.next();

                    double [][] birdAlfaPass =birdHmm.alfaPassNO(observstionSeq);
                    double [] StateProbLastTimeStep =birdHmm.getColumn(birdAlfaPass,birdAlfaPass[0].length-1);
                    double [][] StateProbLastTimeStepMatrix = new double [1][StateProbLastTimeStep.length];

                    for(int d=0;d<StateProbLastTimeStepMatrix.length;d++){
                      for(int k=0;k<StateProbLastTimeStepMatrix[d].length;k++){
                        StateProbLastTimeStepMatrix[d][k]=StateProbLastTimeStep[k];
                    }
                  }
                    double [][] probNextState = birdHmm.matrixMultiplication(StateProbLastTimeStepMatrix,birdHmm.transitionMatrix);
                    double [][] emissionDistributionNextState = birdHmm.matrixMultiplication(probNextState ,birdHmm.emissionMatrix);
                     double [] emissionDistributionNextStateVector=emissionDistributionNextState[0];

                     double probnextemission=maxFinder(emissionDistributionNextStateVector);
                     int nextemission = indexOfMaxFinder(emissionDistributionNextStateVector);

                     if(probnextemission>maxprobnextemission){
                       maxprobnextemission=probnextemission;
                       maxnextemission=nextemission;

                     }


                     // for(int f=0; f<emissionDistributionNextStateVector.length;f++){
                     //   tmpsumBirdEmissions[f]+=emissionDistributionNextStateVector[f];
                     //   //sumBirdEmissions[f]
                     // }
                     //no++;
                  //}

                  // for(int f=0; f<sumBirdEmissions.length;f++){
                  //   sumBirdEmissions[f]=tmpsumBirdEmissions[f];
                  // }
                //}

                //System.err.println(Arrays.toString(tmpsumBirdEmissions));
                // int emind =indexOfMaxFinder(tmpsumBirdEmissions);
                // double probMax=maxFinder(tmpsumBirdEmissions);
                //
                //System.err.println("probmax "+maxprobnextemission);
                //
                //naive assumption
                //System.err.println(Arrays.toString());
                int [] latestobs= Arrays.copyOfRange(observstionSeq, observstionSeq.length-5, observstionSeq.length-1);

                boolean equal = true;
                int first = latestobs[0];
                for(int i = 1; i < latestobs.length && equal; i++){
                  if (latestobs[i] != first) equal = false;
                }

                // if( equal==true && maxprobnextemission>0.6){
                //   //System.err.print("same direction");
                //   return new Action(bestBird,first);
                // }




                //System.err.println("maxnextprob" +maxprobnextemission);

                if (maxprobnextemission>0.61){ //&& sumBirdEmissions[Constants.SPECIES_BLACK_STORK]<3 ){//probMax>13 && emind != Constants.SPECIES_BLACK_STORK && sumBirdEmissions[Constants.SPECIES_BLACK_STORK]<3){ //lagg till vilkor typ: om blackstork prob ar under
                  tS++;
                  //System.err.p
                  return new Action(bestBird,maxnextemission);
                //}else if(emind != Constants.SPECIES_BLACK_STORK){
                  //kolla de senaste observationerna

              // }else if(equal ==true){
              //      tS++;
              //      return new Action(bestBird,first);

                }else{
                  return cDontShoot;
                }

}

         // for the first round or default in other rounds when u don't have time left or the prob for an emission was to low
}
           return cDontShoot;
      }





//################################




      // public Action shoot(GameState pState, Deadline pDue) {
      //   //sum all emissionprobs over one spieces. only look at one spieces for best bird
      //   time++;

      //   int villkor =75+99*pState.getRound();
      //
      //         if( pState.getRound()!=0 && time>villkor){
      //           //lagga while loop eftersom vi ej skjuter faglar
      //           //while()
      //
      //           int bestBird = 0;
      //           int bestBirdSpecies = -1;
      //           double maxLogProb = -1*Double.MAX_VALUE;
      //
      //           //double allProbs []
      //
      //
      //           for(int i = 0; i < pState.getNumBirds(); ++i){
      //
      //

      //             if(pState.getBird(i).isAlive()){ // om if har for tidigare eller while har enbart
      //
      //             int noBirdObs = pState.getBird(i).getSeqLength();
      //             int [] observstionSeq = new int [noBirdObs];
      //             for (int p=0; p < noBirdObs; p++){
      //               observstionSeq[p]=pState.getBird(i).getObservation(p);
      //             }
      //
      //
      //             double [] sumBirdEmissions=new double [noEmissions];
      //             Arrays.fill(sumBirdEmissions,0);
      //
      //
      //             for(int g = 0; g<Constants.COUNT_SPECIES; g++){ // Constants.COUNT_SPECIES-1 we assum that all the hmms will return low probabilities for storks. therefore we dont use the stork hmms to avoid bestBird being a stork
      //

      //             Iterator<HMMnew> it;
      //             it = birdModelsHMM[g].iterator();
      //
      //             //for (HMMnew birdHmm: birdHmmList){
      //
      //             ArrayList<Double> lopProbBirdModelPicker =new ArrayList <Double>();
      //
      //
      //             while (it.hasNext()){ //varje modell for varje art
      //               HMMnew birdHmm =it.next();
      //               //double [][] birdAlfaPass =birdHmm.alfaPassNO(observstionSeq);//alfapassnoscaling
      //               double probObsSeqLog =birdHmm.alfaPassNOprob(observstionSeq);//alfapassnoscaling
      //               if(probObsSeqLog>maxLogProb){
      //                 maxLogProb=probObsSeqLog;
      //                 bestBird = i;
      //                 bestBirdSpecies = g;
      //                 //maxProbperSpieces[d]=probObsSeq;
      //               }
      //
      //             }
      //
      //         }
      //       }
      //     }

      //             if(maxLogProb<-90){
      //                     Iterator<HMMnew> it2;
      //                     it2 = birdModelsHMM[bestBirdSpecies].iterator();
      //
      //                     int noBirdObs = pState.getBird(bestBird).getSeqLength();
      //                     int [] observstionSeq = new int [noBirdObs];
      //
      //                     for (int p=0; p < noBirdObs; p++){
      //                       observstionSeq[p]=pState.getBird(bestBird).getObservation(p);
      //                     }
      //
      //                     double []sumBirdEmissions=new double [noEmissions];
      //
      //                     while(it2.hasNext()){
      //                     HMMnew birdHmm =it2.next();
      //
      //                     double [][] birdAlfaPass =birdHmm.alfaPassNO(observstionSeq);
      //                     double [] StateProbLastTimeStep =birdHmm.getColumn(birdAlfaPass,birdAlfaPass[0].length-1);
      //                     double [][] StateProbLastTimeStepMatrix = new double [1][StateProbLastTimeStep.length];
      //
      //                     for(int d=0;d<StateProbLastTimeStepMatrix.length;d++){
      //                       for(int k=0;k<StateProbLastTimeStepMatrix[d].length;k++){
      //                         StateProbLastTimeStepMatrix[d][k]=StateProbLastTimeStep[k];
      //                     }
      //                   }
      //                     double [][] probNextState = birdHmm.matrixMultiplication(StateProbLastTimeStepMatrix,birdHmm.transitionMatrix);
      //                     double [][] emissionDistributionNextState = birdHmm.matrixMultiplication(probNextState ,birdHmm.emissionMatrix);
      //                      double [] emissionDistributionNextStateVector=emissionDistributionNextState[0];
      //
      //                      for(int f=0; f<emissionDistributionNextStateVector.length;f++){
      //                        sumBirdEmissions[f]+=emissionDistributionNextStateVector[f];
      //                      }
      //
      //                   }
      //
      //                 //System.err.println(Arrays.toString(sumBirdEmissions));
      //                 int emind =indexOfMaxFinder(sumBirdEmissions);
      //                 double probMax=maxFinder(sumBirdEmissions);
      //                 //System.err.println(probMax);
      //                 //System.err.println("stork "+sumBirdEmissions[Constants.SPECIES_BLACK_STORK]);
      //
      //                 if (probMax>6.2 && emind != Constants.SPECIES_BLACK_STORK ){//probMax>13 && emind != Constants.SPECIES_BLACK_STORK && sumBirdEmissions[Constants.SPECIES_BLACK_STORK]<3){ //lagg till vilkor typ: om blackstork prob ar under
      //                   //System.err.println("heej"+ i);
      //                   tS++;
      //                   return new Action(bestBird,emind);
      //                 }else{
      //                   return cDontShoot;
      //                 }
      // }
      //
      //          // for the first round or default in other rounds when u don't have time left or the prob for an emission was to low
      // }
      //           return cDontShoot;
      //       }








    // public Action shoot(GameState pState, Deadline pDue) {
    //   time++;

    //   int villkor =70+99*pState.getRound();
    //
    //         if( pState.getRound()!=0 && time>villkor){
    //           //lagga while loop eftersom vi ej skjuter faglar
    //           //while()
    //
    //           for(int i = 0; i < pState.getNumBirds(); ++i){
    //
    //             //System.err.println("###### For bird; "+i+"###### ");
    //             if(pState.getBird(i).isAlive()){ // om if har for tidigare eller while har enbart
    //
    //             int noBirdObs = pState.getBird(i).getSeqLength();
    //             int [] observstionSeq = new int [noBirdObs];
    //             for (int p=0; p < noBirdObs; p++){
    //               observstionSeq[p]=pState.getBird(i).getObservation(p);
    //             }
    //
    //             HMMnew [] bestBirdModels =new HMMnew [6]; //la enbart in 1 ist for Constants.COUNT_SPECIES
    //             HMMnew bestBirdHmm;
    //             double maxProb =-1000000000; //flyttade ut denna
    //
    //
    //             for(int g = 0; g<Constants.COUNT_SPECIES; g++){ // ga igenom alla arter for en fagel
    //
    //
    //             double [] emissionProbs =new double [noEmissions];
    //             int [] pMovements=new int [Constants.COUNT_SPECIES];
    //             int itter=0;
    //
    //
    //             int maxIndex=0;
    //
    //             Iterator<HMMnew> iterator;
    //             iterator = birdModelsHMM[g].iterator();
    //
    //             //for (HMMnew birdHmm: birdHmmList){
    //
    //             //ArrayList<Double> lopProbBirdModelPicker =new ArrayList <Double>();
    //             int b=0;
    //
    //             if (iterator.hasNext()){ //varje modell for varje art
    //               HMMnew birdHmm =iterator.next();
    //               double logProb = birdHmm.alfaPassNOprob(observstionSeq);
    //               //System.err.println(logProb);
    //               if(b==0){
    //                 bestBirdHmm=birdHmm;
    //               }
    //
    //               if(logProb>maxProb){ // hmm detta kanske blir fel
    //
    //                 maxProb=logProb;
    //                 //System.err.println(maxProb);
    //                 maxIndex =b;
    //                 bestBirdModels[0]=birdHmm;
    //                 bestBirdHmm=birdHmm;
    //
    //               }
    //               b++;
    //
    //             }
    //           }
    //           //System.err.println(bestBirdModels.length);
    //           double [] sumBirdEmissions=new double [noEmissions];//[noEmissions];
    //           Arrays.fill(sumBirdEmissions,0);
    //
    //           double [] emissionDistributionNextStateVector=new double [noEmissions]; //flyttade ut denna
    //           for (int r =0; r<bestBirdModels.length;r++){
    //             HMMnew birdHmm =bestBirdModels[r];
    //             if(birdHmm !=null){
    //                         double [][] birdAlfaPass =birdHmm.alfaPassNO(observstionSeq);//alfapassnoscaling
    //                         double [] StateProbLastTimeStep =birdHmm.getColumn(birdAlfaPass,birdAlfaPass[0].length-1);
    //                         double [][] StateProbLastTimeStepMatrix = new double [1][StateProbLastTimeStep.length];
    //
    //                         for(int d=0;d<StateProbLastTimeStepMatrix.length;d++){
    //                           for(int k=0;k<StateProbLastTimeStepMatrix[d].length;k++){
    //                             StateProbLastTimeStepMatrix[d][k]=StateProbLastTimeStep[k];
    //                         }
    //                       }
    //                         //double [][] transposedTransMatrix = birdHmm.transposeMatrix(birdHmm.transitionMatrix);
    //                         double [][] probNextState = birdHmm.matrixMultiplication(StateProbLastTimeStepMatrix,birdHmm.transitionMatrix);
    //                         double [][] emissionDistributionNextState = birdHmm.matrixMultiplication(probNextState ,birdHmm.emissionMatrix);
    //                         //System.err.println(Arrays.deepToString(emissionDistributionNextState));
    //                          emissionDistributionNextStateVector=emissionDistributionNextState[0];
    //
    //                         for(int f=0; f<emissionDistributionNextStateVector.length;f++){
    //                           sumBirdEmissions[f]+=emissionDistributionNextStateVector[f];
    //                         }
    //           }
    //           }
    //           //System.err.println("#############");
    //           //System.err.println("summed em: "+Arrays.toString(emissionDistributionNextStateVector));
    //           int emind =indexOfMaxFinder(sumBirdEmissions);
    //           double probMax=maxFinder(sumBirdEmissions);
    //           System.err.println(probMax);
    //           if (probMax>0.47 && emind != Constants.SPECIES_BLACK_STORK && emissionDistributionNextStateVector[Constants.SPECIES_BLACK_STORK]<0.35){ //lagg till vilkor typ: om blackstork prob ar under
    //             tS++;
    //             return new Action(i,emind);
    //           }else{
    //             return cDontShoot;
    //           }
    //
    //           //Nu har vi valt ut det basta hmm;erna for varje art
    //         }
    //       }
    //
    //
    //
    //           //      HMMnew birdHmm =iterator.next();
    //         //         double [][] emissionDistributionNextState;
    //         //           //if(birdHmm !=null){
    //         //
    //         //           double [][] birdAlfaPass =birdHmm.alfaPassNO(observstionSeq);//alfapassnoscaling
    //         //           double [] StateProbLastTimeStep =birdHmm.getColumn(birdAlfaPass,birdAlfaPass[0].length-1);
    //         //           double [][] StateProbLastTimeStepMatrix = new double [1][StateProbLastTimeStep.length];
    //         //
    //         //
    //         //
    //         //           for(int d=0;d<StateProbLastTimeStepMatrix.length;d++){
    //         //             for(int k=0;k<StateProbLastTimeStepMatrix[d].length;k++){
    //         //               StateProbLastTimeStepMatrix[d][k]=StateProbLastTimeStep[k];
    //         //           }
    //         //         }
    //         //           double [][] probNextState = birdHmm.matrixMultiplication(StateProbLastTimeStepMatrix,birdHmm.transitionMatrix);
    //         //           emissionDistributionNextState = birdHmm.matrixMultiplication(probNextState ,birdHmm.emissionMatrix);
    //         //           double [] emissionDistributionNextStateVector=emissionDistributionNextState[0];
    //         //
    //         //           double maxHmmEmission=maxFinder(emissionDistributionNextStateVector);
    //         //           int maxHmmEmissionType = indexOfMaxFinder(emissionDistributionNextStateVector);
    //         //           emissionProbs[itter]=maxHmmEmission;
    //         //           pMovements[itter]=maxHmmEmissionType;
    //         //
    //         //         //}
    //         //         itter++;
    //         //         }
    //         //
    //         //
    //         //         double [] totProbsShooting =findShootEmission(emissionProbs,pMovements); //pMovements[maxEmissionProbTotIndex];
    //         //         int maxEmissionProbTotIndex = indexOfMaxFinder(totProbsShooting);
    //         //         double maxEmissionProbTot = maxFinder(totProbsShooting);
    //         //
    //         //         //System.err.println(Arrays.toString(emissionProbs));
    //         //         double shootCriteria=0.40;
    //         //
    //         //         if(maxEmissionProbTotIndex != Constants.SPECIES_BLACK_STORK)  {
    //         //           //System.err.println();
    //         //           tS+=1;
    //         //
    //         //           return new Action(i,maxEmissionProbTotIndex);
    //         //         }else{
    //         //           return cDontShoot;
    //         //         }
    //         //         //if(maxEmissionProbTotIndex == Constants.SPECIES_BLACK_STORK && maxEmissionProbTot>shootCriteria){
    //         //           //samt kolla att sannolikeheten ar tillrackligt stor
    //         //           //System.err.println("max em prob :"+maxEmissionProbTot);
    //         //           //System.err.println("max em prob :"+(Double) Math.exp(maxEmissionProbTot ));
    //         //           //System.err.println();
    //         //
    //         //         //(Double) Math.pow(10, -50);
    //         //
    //         //       }
    //         //         }
    //         //   }
    //         // }else{
    //         //   //time=0;
    //         //   return cDontShoot;
    //         //
    //         // }
    //
    //          // for the first round or default in other rounds when u don't have time left or the prob for an emission was to low
    //       }
    //       return cDontShoot;
    //     }




    public  double maxFinder(double [] vector1){
        double biggest=vector1[0];
        for(int i=1; i<vector1.length;i++){
            if(vector1[i]>biggest){
                biggest=vector1[i];
            }
        } return biggest;
    }

    public  int indexOfMaxFinder(double [] vector1){
                double biggest=vector1[0];
                int indexOfBiggest=0;
                for(int i=1; i<vector1.length;i++){
                        if(vector1[i]>biggest){
                                biggest=vector1[i];
                                indexOfBiggest=i;
                        }
                } return indexOfBiggest;
        }

    public  int indexOfMaxArrayFinder(ArrayList<Double> AL){
                double biggest=AL.get(0);
                int indexOfBiggest=0;

                for(int i=1; i<AL.size();i++){
                        if(AL.get(i)>biggest){
                                biggest=AL.get(i);
                                indexOfBiggest=i;
                        }
                } return indexOfBiggest;
        }

    public  double maxArrayListFinder(ArrayList<Double> AL){
        double biggest=AL.get(0);
        for(int i=1; i<AL.size();i++){
            if(AL.get(i)>biggest){
                biggest=AL.get(i);
            }
        } return biggest;
    }
    public  double maxFinderlog(double [] vector1){
        double biggest=-10000000;//vector1[0];

        for(int i=0; i<vector1.length;i++){
            if(vector1[i]>biggest && vector1[i]!=0){
                biggest=vector1[i];
            }
        } return biggest;
    }

    public  int indexOfMaxFinderlog(double [] vector1){
        double biggest=-Double.MAX_VALUE;//vector1[0];
        int indexOfBiggest=-1;

        for(int i=0; i<vector1.length;i++){
            if(vector1[i]>biggest && vector1[i]!=0){
                biggest=vector1[i];
                indexOfBiggest=i;
            }
        }if(biggest==-Double.MAX_VALUE){
          return -1;
        }
    return indexOfBiggest;
      }


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
     public double secondLargestValue(double[] vector ) {
            double l = vector[0];
            double sl = vector[0];
            for (int i = 0; i < vector.length; i++) {
                if (vector[i] > l) {
                    sl = l;
                    l = vector[i];
                } else if (vector[i] > sl) {
                    sl= vector[i];
                }
            }
    return sl;
    }

    public double secondLargestValuelog(double[] vector ) {
       double l = -10000000;//vector[0];
       double sl = -100000000;//vector[0];
       for (int i = 0; i < vector.length; i++) {
         if (vector[i] > l && vector[i]!=0) {
           sl = l;
           l = vector[i];
         } else if (vector[i] > sl && vector[i]!=0) {
           sl= vector[i];
         }
       }
   return sl;
   }



    public int probabilityComparer(double [] modelSelectionprobabilities){
      //System.err.println(Arrays.toString(modelSelectionprobabilities));

      double maxProb = maxFinderlog(modelSelectionprobabilities);
      int maxIndex = indexOfMaxFinderlog(modelSelectionprobabilities);

      double secondLargestProb =  secondLargestValuelog(modelSelectionprobabilities);
      int secLarIndex = Arrays.asList(modelSelectionprobabilities).indexOf(secondLargestProb);

      //System.err.println(Arrays.toString(modelSelectionprobabilities));
      //System.err.println("maxProb "+Math.exp(maxProb));
      //System.err.println("secondLargestProb "+secondLargestProb);
      //System.err.println("maxProb "+maxProb);
      //System.err.println("secondLargestProb "+secondLargestProb);

      double div =maxIndex/(secLarIndex+epsilon);
      double minProb =-420;//(Double) Math.pow(10, -60);

      //System.err.println("div: "+ div);
      //System.err.println();

      //Check taht they are not to close
      if(maxProb>minProb){ //om vi kan fa tbx vardena i riktiga sannolikehter da vill vi aven kolla att sannolikehen ar stor nog
        return maxIndex;
      }else{
        return Constants.SPECIES_UNKNOWN;

      }
      //return maxIndex;

    }
    public int[] guess(GameState pState, Deadline pDue) {
      //System.err.println("No birds "+ pState.getNumBirds());
        /*
         * Here you should write your clever algorithms to guess the species of
         * each bird. This skeleton makes no guesses, better safe than sorry!
         */
        //System.err.println("get no birds "+pState.getNumBirds());
        Random rand = new Random();
        lGuess = new int[pState.getNumBirds()];

        for (int i = 0; i < pState.getNumBirds(); ++i)
            lGuess[i] = Constants.SPECIES_UNKNOWN; //ger ingen gissning

        if (pState.getNumBirds()==0){
              return lGuess;
            }else{
              if(pState.getRound()==0){
                for(int i = 0; i < pState.getNumBirds(); ++i){
                    lGuess[i] = rand.nextInt(Constants.COUNT_SPECIES-1); //change to the bird we wanna guess for each index
                  }


             }else{ //dvs om state ej ar noll
               for(int j = 0; j < pState.getNumBirds(); ++j){
                 //System.err.println();
                 //System.err.println("NY FAGEL: ");
                 //System.err.println();
                 //System.err.println("New bird: "+j);
                 //if(pState.getBird(j).wasAlive(0)){ // om fageln inte var dod fran borjan
                   //System.err.println("new bird; ");
                 //FOR VARJE FAGEL
                 // ta fram dess observationssekvesn
                 //lagga till hantering for att ta bort observationer nar den dott .remove?
                 int noBirdObs = pState.getBird(j).getSeqLength();
                 int [] observstionSeq = new int [noBirdObs];

                 for (int p=0; p < noBirdObs; p++){
                   //System.err.println(pState.getBird(j).getObservation(p));
                   if(pState.getBird(j).getObservation(p)!=Constants.MOVE_DEAD){
                   observstionSeq[p]=pState.getBird(j).getObservation(p);
                 }
                 //System.err.println(Arrays.toString(observstionSeq));
                 }
                 int birdGuess;

                 double [] maxProbperSpieces =new double [Constants.COUNT_SPECIES];
                 double [] meanProbperSpieces =new double [Constants.COUNT_SPECIES];
                 int s=0;
                 double maxProb =-100000;
                 int maxIndex=0;
                 //int secmaxIndex=0;
                 double [] probperSpieces =new double [Constants.COUNT_SPECIES];
                 Arrays.fill(probperSpieces,0);

                 for(int d = 0; d<Constants.COUNT_SPECIES; d++){
                   //System.err.println("Art ;"+d);


                 Iterator<HMMnew> iterator;
                 iterator = birdModelsHMM[d].iterator();

                 //System.err.println("iterator.hasNext()"+iterator.hasNext());

                 //ArrayList<Double> modelSelectionprobabilities =new ArrayList <Double>();
                 //ArrayList<Double> modelSelectionprobabilities =new ArrayList <Double>();
                 //System.err.println(Arrays.toString(modelSelectionprobabilities));

                 int nomodels =0;
                 double probSum=0;
                 int no=0;
                  while (iterator.hasNext()){ //we wanna look at all the models. which we save after every round.
                    //System.err.println("modell nr" + no);
                    //System.err.println("hhhhhh");
                    HMMnew hmmBirdSpiecesModelround =iterator.next();
                    double probObsSeq = hmmBirdSpiecesModelround.alfaPassNOprob(observstionSeq);
                    // double csum =0;
                    //
                    // for(int k=0;k<hmmBirdSpiecesModelround.C.length;k++){
                    //   csum +=hmmBirdSpiecesModelround.C[k];
                    // }
                    if (probObsSeq>maxProb){
                      maxProb=probObsSeq;
                      maxIndex=d;


                    }

                    probperSpieces[d]+=probObsSeq;//*csum;
                    //modelSelectionprobabilities.add(s,probObsSeq); //ta snittet av dessa ist for max ev.
                    //nomodels+=1;
                    //probSum+=probObsSeq;
                    no++;


                }
                probperSpieces[d]=probperSpieces[d]/no;
                //meanProbperSpieces[d]=probSum/nomodels;
                s++;
                //System.err.println("size ;"+modelSelectionprobabilities.size());
                //double [] modelSelectionprobabilitiesArray =modelSelectionprobabilities.toArray(new double [modelSelectionprobabilities.size()] );
                //maxProbperSpieces[d]=maxArrayListFinder(modelSelectionprobabilities);

              }
                //maxProbperSpieces[j]=maxFinder(modelSelectionprobabilities);
                //int birdGuess
                //lGuess[j]=birdGuess;
                //System.err.println(Arrays.toString(maxProbperSpieces));
                //int birdG=probabilityComparer(maxProbperSpieces);//indexOfMaxFinderlog(maxProbperSpieces);

                //System.err.println("birdG "+birdG);
                //System.err.println("maxindex "+maxIndex);
                //System.err.println(Arrays.toString(probperSpieces));

                int maxin =indexOfMaxFinderlog(probperSpieces);
                double probMax =maxFinderlog(probperSpieces);
                //System.err.println(probMax);
                //if()
                //if(probMax<-500){
                lGuess[j]=maxIndex;//=maxin;//indexOfMaxFinder(meanProbperSpieces);//maxIndex;//indexOfMaxFinder(meanProbperSpieces);//maxIndex; //birdGuess;
              //}
             }

           }
            }
          //System.err.println(Arrays.toString(lGuess));
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
       cS++;
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
      int noBirds=pSpecies.length;
      for(int i=0; i<noBirds;i++){
        if(pSpecies[i] != Constants.SPECIES_UNKNOWN){

        int numOfObs = pState.getBird(i).getSeqLength();
        int[] observationSequence = new int[numOfObs];

        for (int k = 0; k < numOfObs; k++) {
          if(pState.getBird(i).getObservation(k)!=Constants.MOVE_DEAD){
            observationSequence[k] = pState.getBird(i).getObservation(k);
          }
        }

          HMMnew birdHMM = new HMMnew();
          double logProb=birdHMM.BaumWelch(observationSequence);
          //System.err.println(Arrays.deepToString(birdHMM.transitionMatrix));

          birdModelsHMM[pSpecies[i]].add(birdHMM);



    if(lGuess[i] == pSpecies[i]){
      cG=cG+1;
    } else {
      fG=fG+1;
    }
  }

    int diff =tS-cS;

    //System..println(tS);
    //System.err.println("No correct guesses: " + cG + "| No wrong guesses: " + fG + "| Percentage of correct guesses: " + (new Double(cG)/(fG + cG)));
    //System.err.println("No correct shoots:" + cS + "| No failed shots " + diff + " |Percentage of correct shoots: " + (new Double(cS)/(tS)));


  }

}
    public static final Action cDontShoot = new Action(-1, -1);
}
