//**********#************###################************####*#
import java.util.*;

public class Player {

    //double bestv=0;
    /**
     * Performs a move
     *
     * @param gameState
     *            the current state of the board
     * @param deadline
     *            time before which we must have returned
     * @return the next state the board is in after our move
     */



     GameState topPerfState=null;



    public GameState play(final GameState gameState, final Deadline deadline) {
        Vector<GameState> nextStates = new Vector<GameState>();
        //Deadline timeleft = new Deadline();
        gameState.findPossibleMoves(nextStates);
        ///. You will be provided a skeleton which generates a list of
        //valid moves for you to choose from

        GameState bestNextState;

        if (nextStates.size() == 0) {
            // Must play "pass" move if there are no other moves possible.
            return new GameState(gameState, new Move());
        }

        double alpha = - Double.MAX_VALUE;
        double beta = Double.MAX_VALUE;
        //double bestScore=-Double.MAX_VALUE; //worst case for max
        //int bestindex=0;

        int d=6;
        //d2=d;
        alphaBetaPruning(gameState,alpha,beta, gameState.getNextPlayer(),d);
//GameState currState,double alpha,double beta, int player,int d,int itters){
        return topPerfState;//nextStates.elementAt(bestindex);

        /**
         * Here you should write your algorithms to get the best next move, i.e.
         * the best next state. This skeleton returns a random move instead.
         */
    }





    public double alphaBetaPruning(GameState currState,double alpha,double beta, int player,int d){
      ///nextStates.elementAt(j),alpha,beta, gameState.getNextPlayer());

        if(d==0|| currState.isEOG()){//d==1){//if d=0 or node is a terminal node is has no next states     //currState.isEOG()){//d == 0 || nextStates.isEmpty()){ /// prova; currState.isEOG()
          //System.err.println("hej");
          //if(cutofftest(state,depth)){//d==1){
          //return evalv2(currState); // byt till eval istallet
          //return evalv2(currState);
          //return evalv4(currState);//itters);//evalv3(currState);
          //return utility(currState);
          //return utility2(currState,itters);
          return evalv4(currState);
          //int g=7;
          //System.err.println("v "+v);



        }else{
          GameState bestMove =null;
          Vector<GameState> nextStates = new Vector<GameState>();
          currState.findPossibleMoves(nextStates);

          if(player ==Constants.CELL_X){ //Maxes turn
            double topScore, v;
            topScore=-Double.MAX_VALUE; //-Double.MAX_VALUE;
            v=-Double.MAX_VALUE;

            for(int i=0;i<nextStates.size();i++){
            GameState cNode = nextStates.elementAt(i);
            //for(GameState cNode: nextStates){
            v =getMaximum(v,alphaBetaPruning(cNode,alpha,beta,Constants.CELL_O,d-1));
            alpha = getMaximum(alpha, v);

            if(v>topScore){
              topScore= v;
              bestMove=cNode;}
            //if(vtmp>=beta)return v; //i.e we prune since min will not go down this path
            if(alpha>=beta){//break; //the best value for alpha
              topPerfState=bestMove;
              return topScore;
            }

          }
          //System.err.println(v);
          topPerfState=bestMove;
          return topScore;
        }else{ // if player == Constants.CELL_X
          double topScore, v;
          topScore=Double.MAX_VALUE;
          v=Double.MAX_VALUE;

          for(int i=0;i<nextStates.size();i++){
          GameState cNode = nextStates.elementAt(i);

          v =getMinimum(v,alphaBetaPruning(cNode,alpha,beta,Constants.CELL_X,d-1));
          beta = getMinimum(beta, v);

          if(v<topScore){
             topScore= v;
             bestMove=cNode;}
          //if(vtmp>=beta)return v; //i.e we prune since min will not go down this path
          if(alpha>=beta){//break; //the best value for alpha
            topPerfState=bestMove;
            return topScore;
          }

        }
        //System.err.println(v);
        topPerfState=bestMove;
        return topScore;
        }
      }


      //return v;
    }

    /*
    +100 for each three-in-a-row for the AI
    +10 for each two-in-a-row (and empty cell) for the AI
    +1 for each one-in-a-row (and two empty cells) for the AI
    -1 for each one-in-a-row (and two empty cells) for the other player
    -10 for each two-in-a-row (and empty cell) for the other player
    -100 for each three-in-a-row for the other player
    0 for all other states
    */


    public int evalv3(GameState state){
      int totalScore = 0;

      int[][] indicies = {
        {0,1,2, 3 },
        {4, 5, 6,7},
        {8, 9, 10,11},
        {12, 13,14,15},
       {0, 4, 8, 12},
        { 1, 5, 9, 13},
        { 2, 6, 10, 14},
        { 3, 7, 11, 15},
        { 0, 5, 10, 15},
        { 3,6,9,12}
      };

      int [][] pointsPerPlayer = {
        {0,-1,-100,-1000,-10000},
        {1,0,0,0,0},
        {100,0,0,0,0},
        {1000,0,0,0,0},
        {10000,0,0,0,0}
      };


      int noPosLineUps= indicies.length;
      int lengthLineUp = indicies[0].length;

      for (int i =0; i<noPosLineUps; i++){
        int maximizer =0;
        int minimizer = 0;

        for (int j = 0; j<lengthLineUp; j++){
          int indexCell = indicies[i][j];

          if (state.at(indexCell) == Constants.CELL_X) maximizer=maximizer+1;
          if (state.at(indexCell) == Constants.CELL_O) minimizer=minimizer+1;
      }
      totalScore+=pointsPerPlayer[maximizer][minimizer];

      }
      return totalScore;

    }

    public int evalv4(GameState state){

      if(state.isEOG()){

      if(state.isXWin()){
        return 100000;
      }
      else if(state.isOWin()){
        return -100000;
      }else{
      return 0;
    }
  }

      int totalScore = 0;

      int[][] indicies = {
        {0,1,2, 3 },
        {4, 5, 6,7},
        {8, 9, 10,11},
        {12, 13,14,15},
       {0, 4, 8, 12},
        { 1, 5, 9, 13},
        { 2, 6, 10, 14},
        { 3, 7, 11, 15},
        { 0, 5, 10, 15},
        { 3,6,9,12}
      };

      int [][] pointsPerPlayer = {
        {0,-1,-100,-1000,-10000},
        {1,0,0,0,0},
        {100,0,0,0,0},
        {1000,0,0,0,0},
        {10000,0,0,0,0}
      };


      int noPosLineUps= indicies.length;
      int lengthLineUp = indicies[0].length;

      for (int i =0; i<noPosLineUps; i++){
        int maximizer =0;
        int minimizer = 0;

        for (int j = 0; j<lengthLineUp; j++){
          int indexCell = state.at(indicies[i][j]);

          if (state.at(indexCell) == Constants.CELL_X) maximizer=maximizer+1;
          if (state.at(indexCell) == Constants.CELL_O) minimizer=minimizer+1;
      }
      totalScore+=pointsPerPlayer[maximizer][minimizer];

      }
      return totalScore;


    }



    public int evalv2(GameState gameState){

      //System.err.println(gameState.toMessage());

      int totScore=0;
      int [][] rowbools = new int[4][4];

      int colrow = 0;
      int [][] colbools = new int[4][4];


      int [][] diagonals=new int[2][4];

      int scorediag1=0;
      int scorediag2=0;

      int cellindex=0;

      //Going through every row
      for(int i=0;i<4;i++){
        int scoreRow=0;
        int scoreCol=0;

        for(int j=0;j<4;j++){

        ///rows
        int cellcontentrow = gameState.at(i,j);
        //System.err.println(cellcontent);
        if(cellcontentrow==Constants.CELL_X){
          scoreRow++;
         rowbools[i][j] =scoreRow;
       }
        if(cellcontentrow==Constants.CELL_O){
          scoreRow=scoreRow+100;
          rowbools[i][j]=scoreRow;
        }  // 100 ar ett dummy varde

        if(cellcontentrow==Constants.CELL_EMPTY){
          scoreRow=0;
          rowbools[i][j]=scoreRow;
        }


        //columns
        int cellcontentcol = gameState.at(j,i);
        if(cellcontentcol==Constants.CELL_X){
          scoreCol++;
        colbools[j][i] = scoreCol;
      }
        if(cellcontentcol==Constants.CELL_O){
          scoreCol=scoreCol+100;
        colbools[j][i]=scoreCol; // 100 ar ett dummy varde

      }
        if(cellcontentcol==Constants.CELL_EMPTY){
          scoreCol=0;
          rowbools[j][i]=scoreCol;
        }

        //diagonal1



        if(cellcontentrow==Constants.CELL_X && i==j){
          scorediag1++;
         diagonals[0][j] =scorediag1;
       }
        if(cellcontentrow==Constants.CELL_O && i==j){
          scorediag1=scorediag1+100;
          diagonals[0][j]=scorediag1;
        }  // 100 ar ett dummy varde

        if(cellcontentrow==Constants.CELL_EMPTY && i==j){
          scorediag1=0;
          diagonals[0][j]=scorediag1;
        }


        //diagonal2




        if(cellcontentrow==Constants.CELL_X && cellindex%3==0 ){
          scorediag2++;
         diagonals[1][j] =scorediag2;
       }
        if(cellcontentrow==Constants.CELL_O && cellindex%3==0 ){
          scorediag2=scorediag2+100;
          diagonals[1][j]=scorediag2;
        }  // 100 ar ett dummy varde

        if(cellcontentrow==Constants.CELL_EMPTY && cellindex%3==0 ){
          scorediag2=0;
          diagonals[1][j]=scorediag2;
        }






      }
      //System.err.println(Arrays.toString(rowbools[i]));

      int pointsrow = maxFinder(rowbools[i]);
      //System.err.println(pointsrow);
      if(pointsrow==1) totScore++;
      if(pointsrow==2) totScore=totScore+10;
      if(pointsrow==3) totScore=totScore+100;
      if(pointsrow==4) totScore=totScore+1000;

      if(pointsrow >=100 && pointsrow<200) totScore--; // da har opponent 1
      if(pointsrow >=200 && pointsrow<300) totScore=totScore-10;// da har opponent 2 i rad
      if(pointsrow >=300 && pointsrow<400) totScore=totScore-100;// da har opponent 3 i rad
      if(pointsrow >=400) totScore=totScore-1000;// da har opponent 4 i rad

      //System.err.println(Arrays.toString(getColumn(colbools,i)));

      int pointscol = maxFinder(getColumn(colbools,i));
      //System.err.println(maxFinder(getColumn(colbools,i)));

      if(pointscol==1) totScore++;
      if(pointscol==2) totScore=totScore+10;
      if(pointscol==3) totScore=totScore+100;
      if(pointscol==4) totScore=totScore+1000;

      if(pointscol >=100 && pointsrow<200) totScore--; // da har opponent 1
      if(pointscol >=200 && pointsrow<300) totScore=totScore-10;// da har opponent 2 i rad
      if(pointscol >=300 && pointsrow<400) totScore=totScore-100;// da har opponent 3 i rad
      if(pointscol >=400) totScore=totScore-1000;// da har opponent 4 i rad

    cellindex++;
    }

    int pointsdiag1 = maxFinder(diagonals[0]);

    if(pointsdiag1==1) totScore++;
    if(pointsdiag1==2) totScore=totScore+10;
    if(pointsdiag1==3) totScore=totScore+100;
    if(pointsdiag1==4) totScore=totScore+1000;

    if(pointsdiag1 >=100 && pointsdiag1<200) totScore--; // da har opponent 1
    if(pointsdiag1 >=200 && pointsdiag1<300) totScore=totScore-10;// da har opponent 2 i rad
    if(pointsdiag1 >=300 && pointsdiag1<400) totScore=totScore-100;// da har opponent 3 i rad
    if(pointsdiag1 >=400) totScore=totScore-1000;// da har opponent 4 i rad

    int pointsdiag2 = maxFinder(diagonals[1]);

    if(pointsdiag2==1) totScore++;
    if(pointsdiag2==2) totScore=totScore+10;
    if(pointsdiag2==3) totScore=totScore+100;
    if(pointsdiag2==4) totScore=totScore+1000;

    if(pointsdiag2 >=100 && pointsdiag2<200) totScore--; // da har opponent 1
    if(pointsdiag2 >=200 && pointsdiag2<300) totScore=totScore-10;// da har opponent 2 i rad
    if(pointsdiag2 >=300 && pointsdiag2<400) totScore=totScore-100;// da har opponent 3 i rad
    if(pointsdiag2 >=400) totScore=totScore-1000;// da har opponent 4 i rad





    return totScore;
    }

    public  int [] getColumn(int [][] matrix1,int c){
        int [] extractVector =new int[matrix1.length];
        for (int i=0; i<matrix1.length;i++){
            extractVector[i]=matrix1[i][c];

        }return extractVector;
    }


    public  int maxFinder(int [] vector1){
        int biggest=vector1[0];
        for(int i=1; i<vector1.length;i++){
            if(vector1[i]>biggest){
                biggest=vector1[i];
            }
        } return biggest;
    }

    public double eval(GameState gameState){
      double scoreX = 0;
      double scoreY = 0;


      for (int i = 0; i<4; i++){
      for(int j=0; j<4;j++){
        int cellcontent = gameState.at(i,j);
        if(cellcontent==Constants.CELL_O) scoreX++;
        if(cellcontent==Constants.CELL_X) scoreY++;
      }
      }
      return scoreX-scoreY;
    }




    // public double miniMax(GameState currState, int player){
    //     GameState maxState = new GameState();
    //     Vector<GameState> nextStates = new Vector<>();
    //     currState.findPossibleMoves(nextStates);
    //     //System.err.println("player "+player +"d "+d);
    //
    //     double v;
    //
    //     GameState bestMove =new GameState();
    //     if(currState.isEOG()){//d==1){//if d=0 or node is a terminal node is has no next states     //currState.isEOG()){//d == 0 || nextStates.isEmpty()){ /// prova; currState.isEOG()
    //       //System.err.println("hej");
    //       return utility(currState); // return value of node
    //       //int g=7;
    //       //System.err.println("v "+v);
    //
    //
    //
    //     }else{
    //       if(player ==Constants.CELL_X){
    //         v=-Double.MAX_VALUE;
    //         for(int i=0;i<nextStates.size();i++){
    //         GameState cNode = nextStates.elementAt(i);
    //         double vtmp=miniMax(cNode, Constants.CELL_O);
    //
    //         if(vtmp>v){//isNewValBigger(v, vtmp)){
    //           v= vtmp;
    //           //bestMove=cNode;
    //         }
    //       }
    //       //System.err.println(v);
    //       return v;
    //     }else{ // if player == Constants.CELL_X
    //         v=Double.MAX_VALUE;
    //         for(int i=0;i<nextStates.size();i++){
    //         GameState cNode = nextStates.elementAt(i);
    //         double vtmp=miniMax(cNode, Constants.CELL_X);// now it is X turn
    //         if(vtmp<v){//isNewValBigger(v, vtmp)==false){
    //           vtmp= v;
    //           //bestMove=cNode;
    //         }
    //     }
    //     return v;
    //
    //
    //     }
    //   }
    //
    //
    //   //return v;
    // }

    public Double getMaximum(double vold, double vnew){
      if(vold>=vnew){
       return vold;
     }
      return vnew;
    }

    public Double getMinimum(double vold, double vnew){
      if(vold<=vnew){
      return vold;
    }
      return vnew;
    }




}
