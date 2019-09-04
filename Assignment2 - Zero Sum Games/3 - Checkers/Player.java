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


  public int miniMaxAlgorithm( GameState currentState, int alpha, int beta, int depth, boolean XTurn ){
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

    terminalState = ( depth == 0|| currentState.isEOG() );
    timesUp = ( deadline.timeUntil() < deadlineTime );

    if( terminalState || timesUp ){

      // The evaluation value of the current state.
      stateMinimaxValue = heuristicFunction( currentState );

      return stateMinimaxValue;
    /*
      If the present depth level is larger than zero and the game is not over we
      keep digging deeper.
    */
    }else{

      nextStates = new Vector<GameState>();
      currentState.findPossibleMoves(nextStates);

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


  public int heuristicFunction( GameState currentState ){

    /*

      Note: This/any heuristic function is really complicated to understand if you do
      not know the rules of the game.
    */

    int totalScore = 0;

    int no_white_pieces = 0;
    int no_red_pieces = 0;

    int no_red_pieces_kings = 0;
    int no_white_pieces_kings = 0;


    int board_side_red = 0;
    int board_side_white = 0;

    int red_kings_advancing = 0;
    int white_kings_advancing = 0;


    int no_red_pieces_protected = 0;
    int no_white_pieces_protected = 0;

    int protected_left_side = 4;
    int protected_right_side = 3;
    int next_protected_position = 8;

  	int unprotect_upper = 5;
  	int unprotect_lower = 8;

  	int three_1 = 0;
  	int three_2 = 0;

  	int no_white_pieces_under_attack = 0;
  	int no_red_pieces_under_attack = 0;


    Move lastMove = currentState.getMove();
    int moveType = lastMove.getType();
    int lastPlayer = currentState.getNextPlayer();

    // Used to evaluate the final move ove the game.
    boolean redWinningMove, whiteWinningMove, drawMove;
    boolean playerRed, playerWhite;

    boolean isJumpingMove,isRegularMove, byOurPlayer, byOpponent;

    // When a piece jumps over another piece it can jump over several pieces
    // in one go.
    int noJumps;

    int noValidPositions;

    int cellContent, whitePiece, redPiece, king;
    boolean containsWhitePiece, containsRedPiece, containsKing ;

    boolean onOwnRedSide, onOwnWhiteSide;


    boolean isProtectedLeftSide, isProtectedRightSide;


    boolean unProtectedUpper, unProtectedLower, unProtectedBehindLeft, unProtectedBehindRight, unProtectedFrontLeft, unProtectedFrontRight;
    boolean whiteAttacking, redAttacking, kingAttacking;
    boolean unProtected, underAttack, unProtectedBehind, unProtectedFront;
    boolean underAttackLeft, underAttackRight;

    playerRed = ( player == Constants.CELL_RED );
    playerWhite = ( player == Constants.CELL_WHITE );

    redWinningMove = lastMove.isRedWin();
    whiteWinningMove = lastMove.isWhiteWin();
    drawMove = ( moveType == Move.MOVE_DRAW );
    isRegularMove = ( moveType == Move.MOVE_NORMAL );



    /*
      When we get to the final move it is either a winning move, a losing move or
      a draw move.
    */

    if( redWinningMove && playerRed ) return 1000000;

    else if( whiteWinningMove && playerWhite ) return 1000000;

    else if( whiteWinningMove && playerWhite ) return - 1000000;

    else if( redWinningMove && playerRed ) return - 1000000;

    else if( drawMove ) return 0;



    isJumpingMove = lastMove.isJump();
    byOurPlayer = ( lastPlayer != player );
    byOpponent = ( lastPlayer == player );



    // If the last move was a jumping move.
    if ( isJumpingMove ){

      // If the last jumping move was made by our player.
      if( byOurPlayer ){

        noJumps = moveType;

        // The totalScore is weighted by the number of jumps in this move.
        // More jumps means that more pieces are killed and is thus more advantageous.
        totalScore = 10000 * noJumps;

        return totalScore;

      // If the last jumping move was made by our opponent.
      }else if( byOurPlayer ){

        noJumps = moveType;

        // The totalScore is weighted by the number of jumps in this move.
        // More jumps means that more pieces are killed and is thus more advantageous.
        totalScore = - 10000 * noJumps;

        return totalScore;

      }

    // If the last move was a regular move.
    }else if( isRegularMove ){

      noValidPositions = currentState.NUMBER_OF_SQUARES;

      // We go through every position on the board where a piece could be placed.
      for ( int position = 0; position < noValidPositions; position ++ ) {

        redPiece = Constants.CELL_RED;
        whitePiece = Constants.CELL_WHITE;
        cellContent = currentState.get( position );
        containsWhitePiece = 0 != ( cellContent & whitePiece );
        containsRedPiece = 0 != (cellContent & redPiece);


        // First we check if the cell contains a white piece.
        if ( containsWhitePiece ) {

            // We keep track of the total number of white pieces on the board at this state.
            no_white_pieces ++;

            king = Constants.CELL_KING;
            containsKing = ( 0 != ( cellContent & king ));

            // If the cell contains a king we update the number of white kings left
            // at this state in the game.
            if ( containsKing ) no_white_pieces_kings ++;


        }else if ( containsRedPiece ) {

              // We keep track of the total number of red pieces on the board at this state.
              no_red_pieces ++;

              king = Constants.CELL_KING;
              containsKing = ( 0 != ( cellContent & king ));

              // If the cell contains a king we update the number of red kings left
              // at this state in the game.
              if ( containsKing ) no_red_pieces_kings ++;

            }

        /*
          Here the pieces location of the board is taken into consideration.
          It is more advantageous to be closer to the other side of the board.

          For reference, this is how the locations on the board is indicesed:

          * Cells are numbered as follows:
          *
          *    col 0  1  2  3  4  5  6  7
          * row  -------------------------
          *  0  |     0     1     2     3 |  0
          *  1  |  4     5     6     7    |  1
          *  2  |     8     9    10    11 |  2
          *  3  | 12    13    14    15    |  3
          *  4  |    16    17    18    19 |  4
          *  5  | 20    21    22    23    |  5
          *  6  |    24    25    26    27 |  6
          *  7  | 28    29    30    31    |  7
          *      -------------------------
          *        0  1  2  3  4  5  6  7

        */

        // Checks if the players pieces are still close to the starting position
        // at the players own side of the board, or if they are more agressive and
        // have moved to the other side of the board.
        onOwnRedSide = ( containsRedPiece && position >= 20 );
        if( onOwnRedSide ) board_side_red ++;

        onOwnWhiteSide = ( containsWhitePiece && position <= 11 );
        if( onOwnWhiteSide ) board_side_white ++;



        // When a piece is located against the walls it is protected such that the
        // others players pieces can not jump over it, but it can still jump over them.

        isProtectedRightSide = position == protected_right_side;
        isProtectedLeftSide = position == protected_left_side;

        if( isProtectedLeftSide ){

            protected_left_side += next_protected_position;

            if( containsRedPiece ) no_red_pieces_protected ++;
            if( containsWhitePiece ) no_white_pieces_protected ++;

        }else if( isProtectedRightSide ){

              protected_right_side += next_protected_position;

              if( containsRedPiece ) no_red_pieces_protected ++;
              if( containsWhitePiece ) no_white_pieces_protected ++;

        }


      unProtectedUpper = position == unprotect_upper && position < 24;

      if( unProtectedUpper ){

        /*
        If a red piece is unprotected from behind.
        */
				if( containsRedPiece ){

          unProtectedBehindLeft = ( currentState.get( position - 5 ) == Constants.CELL_EMPTY );
          unProtectedBehindRight = ( currentState.get( position - 4 ) == Constants.CELL_EMPTY );

          unProtectedFrontLeft = ( currentState.get( position - 4 ) == Constants.CELL_WHITE );
          unProtectedFrontRight = ( currentState.get( position - 5 ) == Constants.CELL_WHITE );


					if ( unProtectedBehindLeft || unProtectedBehindRight  ){

              whiteAttacking = ( currentState.get( position + 4 ) == Constants.CELL_WHITE) || ( currentState.get( position + 3 ) == Constants.CELL_WHITE );
							if ( whiteAttacking ) no_red_pieces_under_attack ++;


          }else if ( unProtectedFrontLeft || unProtectedFrontRight ){

							unProtected = ( currentState.get( position + 4 ) == Constants.CELL_EMPTY ) || ( currentState.get( position + 3 ) == Constants.CELL_EMPTY );
							if ( unProtected ){

                kingAttacking = 0 != (currentState.get( position )&Constants.CELL_KING);

                if (kingAttacking) no_red_pieces_under_attack ++;

							}
						}

        /*
        If a white piece is unprotected from behind.
        */
				}else if( containsWhitePiece ){

          // unProtectedBehindLeft = ( currentState.get( position - 5 ) == Constants.CELL_EMPTY );
          // unProtectedBehindRight = ( currentState.get( position - 4 ) == Constants.CELL_EMPTY );

          unProtectedFrontLeft = (currentState.get( position + 3 ) ==Constants.CELL_EMPTY);
          unProtectedFrontRight = (currentState.get( position + 4 ) == Constants.CELL_EMPTY) ;

          underAttack = ( currentState.get( position + 4 ) == Constants.CELL_RED ) || ( currentState.get( position + 3 ) == Constants.CELL_RED );


					if( unProtectedFrontRight || unProtectedFrontLeft ){

              redAttacking = ( currentState.get( position - 4 ) == Constants.CELL_RED) || (currentState.get( position - 5 ) == Constants.CELL_RED );
							if ( redAttacking ) no_white_pieces_under_attack ++;

				  } else if( underAttack ){
              unProtected = ( currentState.get( position - 4 ) == Constants.CELL_EMPTY ) || ( currentState.get( position - 5 ) == Constants.CELL_EMPTY );

              if ( unProtected ){

                kingAttacking = 0 != ( currentState.get( position ) & Constants.CELL_KING );
								if ( kingAttacking ) no_white_pieces_under_attack ++;

							}
						}
			}

				unprotect_upper++;
				three_1++;

				if( three_1 % 3 == 0 ){

					unprotect_upper += 5;
					three_1 = 0;

				}
			}

      unProtectedLower = position == unprotect_lower && position < 27;

      /*
      If a piece is unprotected in the lower half of the board.
      */
    	if( unProtectedLower ){

        /* If a red piece is unprotected in the lower half of the board. */
        if( containsRedPiece ){

    				if( containsRedPiece ){



              underAttackLeft = ( currentState.get( position - 4 ) == Constants.CELL_WHITE );
              underAttackRight = ( currentState.get( position - 3 ) == Constants.CELL_WHITE );

              unProtectedFrontLeft = ( currentState.get( position - 4 ) == Constants.CELL_EMPTY );
              unProtectedFrontRight = ( currentState.get( position - 3 ) == Constants.CELL_EMPTY );

    					if ( unProtectedFrontLeft  ||  unProtectedFrontRight  ){

                  whiteAttacking = ( currentState.get( position + 4) == Constants.CELL_WHITE) || (currentState.get(position + 5) == Constants.CELL_WHITE);

                  if ( whiteAttacking ) no_red_pieces_under_attack++;

    					}else if ( underAttackLeft || underAttackRight ){

                  unProtected = ( currentState.get( position + 4 ) == Constants.CELL_EMPTY)  || ( currentState.get( position + 5 ) == Constants.CELL_EMPTY );

  								if ( unProtected ){
                    kingAttacking = 0 != ( currentState.get( position ) & Constants.CELL_KING );
  									if ( kingAttacking ) no_red_pieces_under_attack++;

  								}
    						}

    					}

          /* If a white piece is unprotected in the lower half of the board. */

    			else if( containsWhitePiece ){

            unProtectedBehind = (currentState.get( position + 4 ) == Constants.CELL_EMPTY ) ||  (currentState.get( position + 5 ) == Constants.CELL_EMPTY ) ;
            underAttack = (currentState.get(position+4) == Constants.CELL_RED ) ||  ( currentState.get( position + 5 ) == Constants.CELL_RED );

						if( unProtectedBehind ){

              redAttacking = ( currentState.get( position - 4 ) == Constants.CELL_RED ) || ( currentState.get( position - 3 ) == Constants.CELL_RED );
							if ( redAttacking ) no_white_pieces_under_attack ++;
            // Only the king can walk in the opposite direction. other pieces have
            // to move towards the other side of the board.
						}else if( underAttack ){

              unProtectedFront = (currentState.get(position-4)==Constants.CELL_EMPTY) || (currentState.get(position-3)==Constants.CELL_EMPTY) ;
							if ( unProtectedFront ){

                kingAttacking = 0 != (currentState.get( position ) & Constants.CELL_KING);
							  if ( kingAttacking ) no_white_pieces_under_attack ++;

							}
						}

    			}
    					//getting the indices for unprotected rows shifted to the right
    					unprotect_lower ++;
    					three_2 ++;

    					if( three_2 % 3 == 0 ){

    						unprotect_lower += 5;
    						three_2 = 0;
    					}
    				}
          }
        }

        /*
          The heuristic value is calculated differently depending on if the current player is
          red or white.

          The score is calculated as the sum of the differences between each players varibles.
          For instance, first the totalScore is increased by the difference between the
          total piece count for each player. The logic behind this is that it is more
          advantageous to have more pieces left on the board.

          Note that extra weight is given to having protected pieces and attacking
          other pieces.
        */

        if( playerRed ){

            totalScore += piecediff( no_red_pieces, no_white_pieces );
            totalScore += piecediff( no_red_pieces_kings, no_white_pieces_kings );
            totalScore += piecediff( board_side_red, board_side_white );
            totalScore += 40 * piecediff( no_red_pieces_protected, no_white_pieces_protected );
        		totalScore += 70 * piecediff( no_white_pieces_under_attack, no_red_pieces_under_attack );

        }else{

            totalScore += piecediff( no_white_pieces, no_red_pieces );
            totalScore += piecediff( no_white_pieces_kings, no_red_pieces_kings );
            totalScore += piecediff( board_side_white, board_side_red );
            totalScore += 40 * piecediff( no_white_pieces_protected, no_red_pieces_protected );
          	totalScore += 70 * piecediff( no_red_pieces_under_attack, no_white_pieces_under_attack );

        }
      }
      return totalScore;
    }

  int piecediff( int no_p1, int no_p2 ){

    return no_p1 - no_p2;
  }
  
}
