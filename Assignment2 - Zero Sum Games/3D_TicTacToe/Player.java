import java.util.*;

public class Player {


    /**
     * Performs a move
     *
     * @param gameState
     *            the current state of the board
     * @param deadline
     *            time before which we must have returned
     * @return the next state the board is in after our move
     */

     int[][] indicies =
     {
     { 0,1,2,3},
     { 4,5,6,7},
     { 8,9,10,11},
     { 12,13,14,15},
     { 16,17,18,19},
     {20,21,22,23},
     {24,25,26,27},
     {28,29,30,31},
     {32,33,34,35},
     {36,37,38,39},
     {40,41,42,43},
     {44,45,46,47},
     {48,49,50,51},
     {52,53,54,55},
     {56,57,58,59},
     {60,61,62,63},
     {0,4,8,12},
     {1,5,9,13},
     {2,6,10,14},
     {3,7,11,15},
     {16,20,24,28},
     {17,21,25,29},
     {18,22,26,30},
     {19,23,27,31},
     {32,36,40,44},
     {33,37,41,45},
     {34,38,42,46},
     {35,39,43,47},
     {48,52,56,60},
     {49,53,57,61},
     {50,54,58,62},
     {51,55,59,63},
     {0,5,10,15},
     {3,6,9,12},
     {16,21,26,31},
     {19,22,25,28},
     {32,37,42,47},
     {35,38,41,44},
     {48,53,58,63},
     {51,54,57,60},
     {0,16,32,48},
     {1,17,33,49},
     {2,18,34,50},
     {3,19,35,51},
     {4,20,36,52},
     {5,21,37,53},
     {6,22,38,54},
     {7,23,39,55},
     {8,24,40,56},
     {9,25,41,57},
     {10,26,42,58},
     {11,27,43,59},
     {12,28,44,60},
     {13,29,45,61},
     {14,30,46,62},
     {15,31,47,63},
     {0,17,34,51},
     {4,21,38,55},
     {8,25,42,59},
     {12,29,46,63},
     {3,18,33,48},
     {7,22,37,52},
     {11,26,41,56},
     {15,30,45,60},
     {0,20,40,60},
     {1,21,41,61},
     {2,22,42,62},
     {3,23,43,63},
     {12,24,36,48},
     {13,25,37,49},
     {14,26,38,50},
     {15,27,39,51},
     {0,21,42,63},
     {3,22,41,60},
     {15,26,37,48},
     {12,25,38,51}};


     int [][] pointsPerPlayer = {
       {0,-10,-100,-1000,-10000},
       {10,0,0,0,0},
       {100,0,0,0,0},
       {1000,0,0,0,0},
       {10000,0,0,0,0}
     };


     GameState topPerfState=new GameState();;

     int maximizingPlayer = Constants.CELL_X;
     int minimizingPlayer = Constants.CELL_O;



    public GameState play(final GameState gameState, final Deadline deadline) {

      Vector<GameState> nextStates = new Vector<GameState>();

      gameState.findPossibleMoves(nextStates);
      ///. You will be provided a skeleton which generates a list of
      //valid moves for you to choose from
      GameState bestNextState;
      if (nextStates.size() == 0) {
          // Must play "pass" move if there are no other moves possible.
          return new GameState(gameState, new Move());
      }

      double beta = Double.MAX_VALUE;
      double alpha =Double.MIN_VALUE;

      int d=3;

      alphaBetaPruning(gameState,alpha,beta, gameState.getNextPlayer(),d);
//GameState currState,double alpha,double beta, int player,int d,int itters){
      return topPerfState;//nextStates.elementAt(bestindex);

      /**
       * Here you should write your algorithms to get the best next move, i.e.
       * the best next state. This skeleton returns a random move instead.
       **/
    }

    public double alphaBetaPruning(GameState currState,double alpha,double beta, int currPlayer,int d){
      ///nextStates.elementAt(j),alpha,beta, gameState.getNextPlayer());

        if(d==0|| currState.isEOG()){//d==1){//if d=0 or node is a terminal node is has no next states     //currState.isEOG()){//d == 0 || nextStates.isEmpty()){ /// prova; currState.isEOG()
          return evalv4(currState);

        }else{
          GameState bestMove =new GameState();
          Vector<GameState> nextStates = new Vector<GameState>();
          currState.findPossibleMoves(nextStates);
          double topScore, v;

          if(currPlayer ==maximizingPlayer){ //Maxes turn


            topScore=Double.MIN_VALUE; //-Double.MAX_VALUE;
            v=Double.MIN_VALUE;

            for(int i=0;i<nextStates.size();i++){
            GameState cNode = nextStates.elementAt(i);
            //for(GameState cNode: nextStates){

            v =getMaximum(v,alphaBetaPruning(cNode,alpha,beta,minimizingPlayer,d-1));
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

          topScore=Double.MAX_VALUE;
          v=Double.MAX_VALUE;

          for(int i=0;i<nextStates.size();i++){
          GameState cNode = nextStates.elementAt(i);


          v =getMinimum(v,alphaBetaPruning(cNode,alpha,beta,maximizingPlayer,d-1));
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



    public int evalv4(GameState state){

      int totalScore = 0;

      int noPosLineUps= indicies.length;
      int lengthLineUp = indicies[0].length;

      for (int r =0; r<noPosLineUps; r++){
        int maximizer =0;
        int minimizer = 0;

        for (int c = 0; c<lengthLineUp; c++){
          // int r =state.cellToRow(indicies[i][j]);
          // int c =state.cellToCol(indicies[i][j]);
          // int l =state.cellToLay(indicies[i][j]);

          if (state.at(indicies[r][c])==maximizingPlayer) maximizer=maximizer+1;
          if (state.at(indicies[r][c])==minimizingPlayer) minimizer=minimizer+1;
      }
      //System.err.println(maximizer);
      //System.err.println(minimizer);
      totalScore+=pointsPerPlayer[maximizer][minimizer];

      }
      return totalScore;


    }



    // public int evalv2(GameState gameState){
    //
    //   //System.err.println(gameState.toMessage());
    //
    //   int totScore=0;
    //   int [][] rowbools = new int[4][4];
    //
    //   int colrow = 0;
    //   int [][] colbools = new int[4][4];
    //
    //
    //   int [][] diagonals=new int[2][4];
    //
    //   int scorediag1=0;
    //   int scorediag2=0;
    //
    //   int cellindex=0;
    //
    //   //Going through every row
    //   for(int i=0;i<4;i++){
    //     int scoreRow=0;
    //     int scoreCol=0;
    //
    //     for(int j=0;j<4;j++){
    //
    //     ///rows
    //     int cellcontentrow = gameState.at(i,j);
    //     //System.err.println(cellcontent);
    //     if(cellcontentrow==Constants.CELL_X){
    //       scoreRow++;
    //      rowbools[i][j] =scoreRow;
    //    }
    //     if(cellcontentrow==Constants.CELL_O){
    //       scoreRow=scoreRow+100;
    //       rowbools[i][j]=scoreRow;
    //     }  // 100 ar ett dummy varde
    //
    //     if(cellcontentrow==Constants.CELL_EMPTY){
    //       scoreRow=0;
    //       rowbools[i][j]=scoreRow;
    //     }
    //
    //
    //     //columns
    //     int cellcontentcol = gameState.at(j,i);
    //     if(cellcontentcol==Constants.CELL_X){
    //       scoreCol++;
    //     colbools[j][i] = scoreCol;
    //   }
    //     if(cellcontentcol==Constants.CELL_O){
    //       scoreCol=scoreCol+100;
    //     colbools[j][i]=scoreCol; // 100 ar ett dummy varde
    //
    //   }
    //     if(cellcontentcol==Constants.CELL_EMPTY){
    //       scoreCol=0;
    //       rowbools[j][i]=scoreCol;
    //     }
    //
    //     //diagonal1
    //
    //
    //
    //     if(cellcontentrow==Constants.CELL_X && i==j){
    //       scorediag1++;
    //      diagonals[0][j] =scorediag1;
    //    }
    //     if(cellcontentrow==Constants.CELL_O && i==j){
    //       scorediag1=scorediag1+100;
    //       diagonals[0][j]=scorediag1;
    //     }  // 100 ar ett dummy varde
    //
    //     if(cellcontentrow==Constants.CELL_EMPTY && i==j){
    //       scorediag1=0;
    //       diagonals[0][j]=scorediag1;
    //     }
    //
    //
    //     //diagonal2
    //
    //
    //
    //
    //     if(cellcontentrow==Constants.CELL_X && cellindex%3==0 ){
    //       scorediag2++;
    //      diagonals[1][j] =scorediag2;
    //    }
    //     if(cellcontentrow==Constants.CELL_O && cellindex%3==0 ){
    //       scorediag2=scorediag2+100;
    //       diagonals[1][j]=scorediag2;
    //     }  // 100 ar ett dummy varde
    //
    //     if(cellcontentrow==Constants.CELL_EMPTY && cellindex%3==0 ){
    //       scorediag2=0;
    //       diagonals[1][j]=scorediag2;
    //     }
    //
    //
    //
    //
    //
    //
    //   }
    //   //System.err.println(Arrays.toString(rowbools[i]));
    //
    //   int pointsrow = maxFinder(rowbools[i]);
    //   //System.err.println(pointsrow);
    //   if(pointsrow==1) totScore++;
    //   if(pointsrow==2) totScore=totScore+10;
    //   if(pointsrow==3) totScore=totScore+100;
    //   if(pointsrow==4) totScore=totScore+1000;
    //
    //   if(pointsrow >=100 && pointsrow<200) totScore--; // da har opponent 1
    //   if(pointsrow >=200 && pointsrow<300) totScore=totScore-10;// da har opponent 2 i rad
    //   if(pointsrow >=300 && pointsrow<400) totScore=totScore-100;// da har opponent 3 i rad
    //   if(pointsrow >=400) totScore=totScore-1000;// da har opponent 4 i rad
    //
    //   //System.err.println(Arrays.toString(getColumn(colbools,i)));
    //
    //   int pointscol = maxFinder(getColumn(colbools,i));
    //   //System.err.println(maxFinder(getColumn(colbools,i)));
    //
    //   if(pointscol==1) totScore++;
    //   if(pointscol==2) totScore=totScore+10;
    //   if(pointscol==3) totScore=totScore+100;
    //   if(pointscol==4) totScore=totScore+1000;
    //
    //   if(pointscol >=100 && pointsrow<200) totScore--; // da har opponent 1
    //   if(pointscol >=200 && pointsrow<300) totScore=totScore-10;// da har opponent 2 i rad
    //   if(pointscol >=300 && pointsrow<400) totScore=totScore-100;// da har opponent 3 i rad
    //   if(pointscol >=400) totScore=totScore-1000;// da har opponent 4 i rad
    //
    // cellindex++;
    // }
    //
    // int pointsdiag1 = maxFinder(diagonals[0]);
    //
    // if(pointsdiag1==1) totScore++;
    // if(pointsdiag1==2) totScore=totScore+10;
    // if(pointsdiag1==3) totScore=totScore+100;
    // if(pointsdiag1==4) totScore=totScore+1000;
    //
    // if(pointsdiag1 >=100 && pointsdiag1<200) totScore--; // da har opponent 1
    // if(pointsdiag1 >=200 && pointsdiag1<300) totScore=totScore-10;// da har opponent 2 i rad
    // if(pointsdiag1 >=300 && pointsdiag1<400) totScore=totScore-100;// da har opponent 3 i rad
    // if(pointsdiag1 >=400) totScore=totScore-1000;// da har opponent 4 i rad
    //
    // int pointsdiag2 = maxFinder(diagonals[1]);
    //
    // if(pointsdiag2==1) totScore++;
    // if(pointsdiag2==2) totScore=totScore+10;
    // if(pointsdiag2==3) totScore=totScore+100;
    // if(pointsdiag2==4) totScore=totScore+1000;
    //
    // if(pointsdiag2 >=100 && pointsdiag2<200) totScore--; // da har opponent 1
    // if(pointsdiag2 >=200 && pointsdiag2<300) totScore=totScore-10;// da har opponent 2 i rad
    // if(pointsdiag2 >=300 && pointsdiag2<400) totScore=totScore-100;// da har opponent 3 i rad
    // if(pointsdiag2 >=400) totScore=totScore-1000;// da har opponent 4 i rad
    //
    //
    //
    //
    //
    // return totScore;
    // }



    // public double eval(GameState gameState){
    //   double scoreX = 0;
    //   double scoreY = 0;
    //
    //
    //   for (int i = 0; i<4; i++){
    //   for(int j=0; j<4;j++){
    //     int cellcontent = gameState.at(i,j);
    //     if(cellcontent==Constants.CELL_O) scoreX++;
    //     if(cellcontent==Constants.CELL_X) scoreY++;
    //   }
    //   }
    //   return scoreX-scoreY;
    // }




    public double miniMax(GameState currState, int player){
        GameState maxState = new GameState();
        Vector<GameState> nextStates = new Vector<>();
        currState.findPossibleMoves(nextStates);
        //System.err.println("player "+player +"d "+d);

        double v;

        GameState bestMove =new GameState();
        if(currState.isEOG()){//d==1){//if d=0 or node is a terminal node is has no next states     //currState.isEOG()){//d == 0 || nextStates.isEmpty()){ /// prova; currState.isEOG()
          //System.err.println("hej");
          return utility(currState); // return value of node
          //int g=7;
          //System.err.println("v "+v);



        }else{
          if(player ==Constants.CELL_X){
            v=-Double.MAX_VALUE;
            for(int i=0;i<nextStates.size();i++){
            GameState cNode = nextStates.elementAt(i);
            double vtmp=miniMax(cNode, Constants.CELL_O);

            if(vtmp>v){//isNewValBigger(v, vtmp)){
              v= vtmp;
              //bestMove=cNode;
            }
          }
          //System.err.println(v);
          return v;
        }else{ // if player == Constants.CELL_X
            v=Double.MAX_VALUE;
            for(int i=0;i<nextStates.size();i++){
            GameState cNode = nextStates.elementAt(i);
            double vtmp=miniMax(cNode, Constants.CELL_X);// now it is X turn
            if(vtmp<v){//isNewValBigger(v, vtmp)==false){
              vtmp= v;
              //bestMove=cNode;
            }
        }
        return v;


        }
      }


      //return v;
    }

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




    public double utility(GameState lastState){

      if(lastState.isXWin()){
        return 1000;
      }
      else if(lastState.isOWin()){
        return -1000;
      }
      return 0;

    }

}
