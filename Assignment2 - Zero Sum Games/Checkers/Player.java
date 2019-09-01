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


     HelperMethods helperMethods;

     int player;

     Deadline deadline;

     double deadlineTime =Math.pow(10,8)+5*Math.pow(10,7);

     HashMap<Integer, Integer> map = new HashMap<>();
     HashZob hz =new HashZob();

     int WasPruned = 10000;
     int WasNotPruned = - 10000;


     public Player() {

       helperMethods  =  new HelperMethods();

     }

    public GameState play(final GameState gameState, final Deadline deadlineLimit) {
      /*
        Finds the next best move based on the current state by assigning next possible
        values using a heuristic function and the minimax algorithm with pruning.
      */

      Vector<GameState> nextStates;

      // Used to store the best moves found for the minimizing and maximizing player.
      int alpha, beta;

      GameState bestNextState, passMove;

      boolean noPossibleMoves;

      /* Given any evaluation function which can assign a heuristic for the game, we can
      truncate the search at a given maxDepth by including the number of levels left to analyze as an
      argument and decreasing it in each step.*/
      int maxDepth;

      int currentValue;

      int childHashValue;

      // The state at this point.
      nextStates = new Vector <GameState>();

      // Find possible moves at this point.
      gameState.findPossibleMoves( nextStates );


      // If there are no other moves possible we must play a "pass" move
      noPossibleMoves = ( nextStates.size() == 0 );

      if ( noPossibleMoves ) {

        passMove = new GameState( gameState, new Move() );
        return passMove;

      // Otherwise we wanna dig deeper into the tree to evaluate how good our
      // next move is.
      } else {

        deadline = deadlineLimit;

        // Initlize the best moves found for the minimizing and maximizing player
        // arbitrarily high and low.
        beta = Integer.MAX_VALUE;
        alpha = Integer.MIN_VALUE;

        // How many moves we want to look ahead at most.
        maxDepth = 11;

        // Arbitrarily set the best next move.
        bestNextState = nextStates.elementAt(0);

        // Get the current player, which in this assignment could be either X or O.
        player = gameState.getNextPlayer();

         // If the current next child state has not been visited before, i.e. we
         // have not calculated a score for this game board setting before then
         // we need to add it to our hash map where we store utility heuristic values
         // with the corresponding state.
        hashChildStates( nextStates );

        // We want to look at all possible moves up to maxDepth number of moves
        // further ahead in the game.
        for ( int depth = 0; depth < maxDepth; depth ++ ){

          /*
            Sort child states/next states in the hash map based on their heuristic values.
          */
          nextStates = sortStates( nextStates );

            // For every possible child node/ next move at this current depth.
            for (GameState state: nextStates){

              // The highest heuristic value for the current next state looking
              // a number of moves ahead given by: depth.
              currentValue = miniMaxAlgorithm( state, alpha, beta, depth, false );

              // alpha is the heuristic value of our best move. So, if we have
              // found a better move to make we update the alpha value.
              if( currentValue > alpha ) {

                  alpha = currentValue;
                  bestNextState = state;

              }

              // We prune the tree as we know that no better result may be achieved.
              // For more info se the assignment description.
              if( beta <= alpha ){

                childHashValue = hz.calcHash( hz.extractBoard( state.toMessage() ) );
                map.put( childHashValue, WasPruned );
                break;

              }
            }
      }
      return bestNextState;
    }
  }


  public void hashChildStates( Vector<GameState> states ){
    /* Adds states to a hash map and calculates a hash value for the state..*/

    boolean unseenState;

    int hashValue;

    // For every possible next state / child node.
    for ( GameState state: states ){

       // If the current next child state has not been visited before, i.e. we
       // have not calculated a score for this game board setting before then
       // we need to add it to our hash map where we store utility heuristic values
       // with the corresponding state.

       unseenState = ( !map.containsKey( hz.calcHash( hz.extractBoard( state.toMessage() ) ) ) );
      if( unseenState ){
        // Calculate an unique hashvalue for the current gameboard state.
        hashValue = hz.calcHash(hz.extractBoard(state.toMessage()));
        map.put( hashValue, WasNotPruned);

       }
     }

  }


  public Vector<GameState> sortStates( Vector<GameState> states ){
    /*
      Sort states in a hash map based on their heuristic values.
    */

    Collections.sort( states, new Comparator<GameState>(){
      public int compare(GameState state1, GameState state2){

        int hashValue1 = hz.calcHash(hz.extractBoard( state1.toMessage() ));
        int hashValue2 = hz.calcHash(hz.extractBoard( state2.toMessage() ));

        int heuristicValue1 = map.get( hashValue1 );
        int heuristicValue2 = map.get( hashValue2 );

        int largerHeuristicValue = heuristicValue1 < heuristicValue2 ? 1 : heuristicValue2 < heuristicValue1 ? -1 : 0;

        return largerHeuristicValue;
      }
    });

    return states;
  }


  public int miniMaxAlgorithm( GameState currState, int alpha, int beta, int depth, boolean XTurn ){
    /*
      Minimax algorithm with alpha-beta pruning.
      state: the current state we are analyzing.
      alpha: the current best value achievable by A.
      beta: the current best value achievable by B.
      XTurn: if it is player x:es turn to make a move.
      player : the current player.
      returns the minimax value of the state.
    */

    /*
    • terminalState: If a preset depth level is reached or we reach an actual terminal state,
      i.e. the game is over. Then we return the value of this state.
    • timesUp: If their is no time left to make a move.
    • .*/
    boolean terminalState, timesUp;

    int stateMinimaxValue;

    Vector<GameState> nextStates;

    int highestValue, currentValue, currentHashValue;

    terminalState = ( depth == 0|| currState.isEOG() );
    timesUp = ( deadline.timeUntil() < deadlineTime );

    if( terminalState || timesUp ){

      // The evaluation value of the current state.
      stateMinimaxValue = heuristicFunction( currState );

      return stateMinimaxValue;
    /*
      If the present depth level is larger than zero and the game is not over we
      keep digging deeper.
    */
    }else{

      nextStates = new Vector<GameState>();
      currState.findPossibleMoves(nextStates);

      /* If the current next child state has not been visited before, i.e. we
      have not calculated a score for this game board setting before then
      we need to add it to our hash map where we store utility heuristic values
      with the corresponding state.*/
      hashChildStates( nextStates );


      // Sort child states/next states in the hash map based on their heuristic values.
      nextStates = sortStates( nextStates );

      // If it is X:es turn to make a move. I.e. the maximizing players turn.
      if( XTurn ){

        // The move/state found with the highest score/heuristic value while
        // traversing the state tree using the miniMaxAlgorithm.
        highestValue = Integer.MIN_VALUE;

        // For each child node/ each possible move from this current state.
        for (GameState childMove: nextStates){

          // alpha is the current best value achived by the player X ( our player )
          // and currentvalue is the newly calculated value if we make the move childMove.
          // Therefore, we now want to know if this next move is btter than the previous
          // best move found.
          currentValue = miniMaxAlgorithm( childMove, alpha, beta, depth - 1 , false );
          highestValue = helperMethods.getLargerValue( currentValue, highestValue );
          alpha = helperMethods.getLargerValue( alpha, highestValue );

        // We prune the tree as we know that no better result may be achieved.
        // For more info se the assignment description.
        if( beta <= alpha ){

          currentHashValue = hz.calcHash( hz.extractBoard( childMove.toMessage() ) );
          map.put( currentHashValue, WasPruned );
          break;
        }
      }
      // return the current highest heuristic value.
      return highestValue;

    }else{

      highestValue = Integer.MAX_VALUE;

      // For each child node/ each possible move from this current state.
      for (GameState childMove: nextStates){

        // beta is the best value achived by the player O ( opponent )
        // and currentvalue is the newly calculated value if we make the move childMove.
        // Therefore, we now want to know if this next move is btter than the previous
        // best move found.
        currentValue = miniMaxAlgorithm( childMove, alpha, beta, depth - 1, true);
        highestValue = helperMethods.getSmallerValue( highestValue, currentValue );
        beta = helperMethods.getSmallerValue( beta, highestValue );

        // We prune the tree as we know that no better result may be achieved.
        // For more info se the assignment description.
        if( beta <= alpha ){
          // We store the current state and register that it was pruned so that we'll
          // know that it is a bad move.
          currentHashValue = hz.calcHash( hz.extractBoard( childMove.toMessage() ) );
          map.put( currentHashValue, WasPruned );
          break;
        }
      }
      return highestValue;
      }
    }
  }


  public int heuristicFunction( GameState currState ){

    int totalScore = 0;

    int no_white = 0;
    int no_red = 0;

    int no_red_kings = 0;
    int no_white_kings = 0;


    int board_side_red = 0;
    int board_side_white = 0;

    int red_kings_advancing = 0;
    int white_kings_advancing = 0;


    int no_red_protected = 0;
    int no_white_protected = 0;

    int itter_left = 4;
    int itter_right = 3;

  	int unprotect_itter = 5;
  	int unprotect_itter_2 = 8;

  	int three_1 = 0;
  	int three_2 = 0;

  	int no_white_under_attack = 0;
  	int no_red_under_attack = 0;


    Move lastMove = currState.getMove();
    int moveType = lastMove.getType();
    int lastPlayer = currState.getNextPlayer();


    boolean redWinningMove, whiteWinningMove, drawMove;
    boolean playerRed, playerWhite;

    playerRed = ( player == Constants.CELL_RED );
    playerWhite = ( player == Constants.CELL_WHITE );

    redWinningMove = lastMove.isRedWin();
    whiteWinningMove = lastMove.isWhiteWin();
    drawMove = ( moveType == Move.MOVE_DRAW );


    if( redWinningMove && playerRed ) return 1000000;

    else if( whiteWinningMove && playerWhite ) return 1000000;

    else if( whiteWinningMove && playerWhite ) return - 1000000;

    else if( redWinningMove && playerRed ) return - 1000000;

    else if( drawMove ) return 0;


  if(lastMove.isJump() && lastPlayer != player){
    int noJumps =moveType;
    return 10000*noJumps;

  }else if(lastMove.isJump() && lastPlayer == player){
      int noJumps =moveType;
      return -10000*noJumps;


  }else if(moveType==Move.MOVE_NORMAL){

    for (int i = 0; i < currState.NUMBER_OF_SQUARES; i++) {
      //COUNTING PIECES
      if (0!=(currState.get(i)&Constants.CELL_WHITE)) {
        no_white++;
        if (0 != (currState.get(i)&Constants.CELL_KING)){
           no_white_kings++;
         }

      }else if (0!=(currState.get(i)& Constants.CELL_RED)) {
            no_red++;
            if (0 != (currState.get(i)&Constants.CELL_KING)){
               no_red_kings++;
             }
          }

          if(0!=(currState.get(i)&Constants.CELL_RED) && i>=20){
            board_side_red++;
          }else if(0!=(currState.get(i)&Constants.CELL_WHITE) && i<=11){
            board_side_white++;
          }

          if(i==itter_left){
            itter_left+=8;
            if(0!=(currState.get(i)&Constants.CELL_RED)){
               no_red_protected++;}
            if(0!=(currState.get(i)&Constants.CELL_WHITE)){
            no_white_protected++;}

          }else if(i==itter_right){
            itter_right+=8;
            if(0!=(currState.get(i)&Constants.CELL_RED)){
              no_red_protected++;
            }
            if(0!=(currState.get(i)&Constants.CELL_WHITE)){
               no_white_protected++;
             }
          }

  				if(i==unprotect_itter && i<24){

  					if(currState.get(i)==Constants.CELL_RED){
  						if ( (currState.get(i-4)==Constants.CELL_EMPTY)  || (currState.get(i-5)==Constants.CELL_EMPTY)  ){
  								//no_red_attackable+=1;
  								if ((currState.get(i+4)==Constants.CELL_WHITE)|| (currState.get(i+3)==Constants.CELL_WHITE)){
  										no_red_under_attack++;
  								}
  							}
  						else if ( (currState.get(i-4)==Constants.CELL_WHITE)  || (currState.get(i-5)==Constants.CELL_WHITE)  ){
  								//no_red_attackable+=1;
  								if ((currState.get(i+4)==Constants.CELL_EMPTY)|| (currState.get(i+3)==Constants.CELL_EMPTY)){
  									if (0 != (currState.get(i)&Constants.CELL_KING)){
  										no_red_under_attack++;
  									}
  								}
  							}

  					}else if((currState.get(i)==Constants.CELL_WHITE)){
  						if(  (currState.get(i+4)==Constants.CELL_EMPTY)   || (currState.get(i+3) ==Constants.CELL_EMPTY) ){
  								//no_white_attackable+=1;
  								if ((currState.get(i-4)==Constants.CELL_RED) || (currState.get(i-5)==Constants.CELL_RED)){
  										no_white_under_attack++;
  								}
  							}
  						if(  (currState.get(i+4)==Constants.CELL_RED)   || (currState.get(i+3) ==Constants.CELL_RED) ){
  								//no_white_attackable+=1;
  								if ((currState.get(i-4)==Constants.CELL_EMPTY) || (currState.get(i-5)==Constants.CELL_EMPTY)){
  									if (0 != (currState.get(i)&Constants.CELL_KING)){
  										no_white_under_attack++;
  									}
  								}
  							}
  					}
  					unprotect_itter++;
  					three_1++;
  					if(three_1%3==0){
  						unprotect_itter+=5;
  						three_1=0;
  					}
  				}

  				//right 8,9,10,16,17,18,24,25,26
  				if(i==unprotect_itter_2 && i<27){

  					if(currState.get(i)==Constants.CELL_RED){
  						if ( (currState.get(i-4)==Constants.CELL_EMPTY)   ||  (currState.get(i-3)==Constants.CELL_EMPTY)  ){
  								//no_red_attackable+=1;
  								if ( (currState.get(i+4)==Constants.CELL_WHITE) || (currState.get(i+5)==Constants.CELL_WHITE) ){
  										no_red_under_attack++;
  								}
  						}
  						else if ( (currState.get(i-4)==Constants.CELL_WHITE)   ||  (currState.get(i-3)==Constants.CELL_WHITE)  ){
  								//no_red_attackable+=1;
  								if ( (currState.get(i+4)==Constants.CELL_EMPTY) || (currState.get(i+5)==Constants.CELL_EMPTY) ){
  									if (0 != (currState.get(i)&Constants.CELL_KING)){
  										no_red_under_attack++;
  									}
  								}
  						}

  					}

  					else if(currState.get(i)==Constants.CELL_WHITE){
  						if( (currState.get(i+4) ==Constants.CELL_EMPTY) ||  (currState.get(i+5) ==Constants.CELL_EMPTY) ){
  									//no_white_attackable+=1;
  									if ((currState.get(i-4)==Constants.CELL_RED) || (currState.get(i-3)==Constants.CELL_RED) ){
  											no_white_under_attack++;
  									}
  						}
  						else if( (currState.get(i+4) ==Constants.CELL_RED) ||  (currState.get(i+5) ==Constants.CELL_RED) ){
  									//no_white_attackable+=1;
  									if ((currState.get(i-4)==Constants.CELL_EMPTY) || (currState.get(i-3)==Constants.CELL_EMPTY) ){
  										if (0 != (currState.get(i)&Constants.CELL_KING)){
  											no_white_under_attack++;
  										}
  									}
  						}

  				}
  					//getting the indices for unprotected rows shifted to the right
  					unprotect_itter_2++;
  					three_2++;

  					if(three_2%3==0){
  						unprotect_itter_2+=5;
  						three_2=0;
  					}
  				}
        }
  }

  if(Constants.CELL_RED==player){
      totalScore += piecediff(no_red,no_white);
      totalScore+=piecediff(no_red_kings,no_white_kings);
      totalScore+=piecediff(board_side_red,board_side_white);
      totalScore+=40*piecediff(no_red_protected,no_white_protected);
  		totalScore+=70*piecediff(no_white_under_attack, no_red_under_attack);

  }else{
    totalScore += piecediff(no_white,no_red);
    totalScore+=piecediff(no_white_kings,no_red_kings);
    totalScore+=piecediff(board_side_white,board_side_red);
    totalScore+=40*piecediff(no_white_protected,no_red_protected);
  	totalScore+=70*piecediff(no_red_under_attack, no_white_under_attack);

  }
  return totalScore;
  }

  int piecediff(int no_p1, int no_p2 ){
    return no_p1-no_p2;
  }
}
