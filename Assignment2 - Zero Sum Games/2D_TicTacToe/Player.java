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

    // The best next move that we should make at this point.
    GameState optimalMove;

    HelperMethods helperMethods;

    public Player() {

      optimalMove  =  null;
      helperMethods  =  new HelperMethods();

    }

    public GameState play( final GameState gameState, final Deadline deadline ) {
        /*
          Finds the next best move based on the current state by assigning next possible
          values using a heuristic function and the minimax algorithm with pruning.
        */

        Vector<GameState> nextStates;

        GameState bestNextState, passMove;

        boolean noPossibleMoves;

        double alpha, beta;

        /* Given any evaluation function which can assign a heuristic for the game, we can
        truncate the search at a given depth by including the number of levels left to analyze as an
        argument and decreasing it in each step.*/
        int depth;

        // The state at this point.
        nextStates  =  new Vector <GameState>();

        // Find possible moves at this point.
        gameState.findPossibleMoves(nextStates);

        // If there are no other moves possible we must play a "pass" move
        noPossibleMoves = ( nextStates.size()  ==  0 );

        if ( noPossibleMoves ) {

            passMove = new GameState( gameState, new Move() );
            return passMove;

        // Otherwise we wanna dig deeper into the tree to evaluate how good our
        // next move is.
        } else {

          // Initlize the best moves found for the minimizing and maximizing player
          // arbitrarily high and low.
          alpha  =  - Double.MAX_VALUE;
          beta  =  Double.MAX_VALUE;

          // How many moves we want to look ahead at most.
          depth = 6;

          // Update the optimalMove parameter so that we can find the next best state.
          miniMaxAlgorithm( gameState, alpha, beta, gameState.getNextPlayer(), depth );

          return optimalMove;

        }
  }


  public double miniMaxAlgorithm( GameState currState, double alpha, double beta, int player, int depth ){
    /*
      Minimax algorithm with alpha-beta pruning.
      state: the current state we are analyzing.
      alpha: the current best value achievable by A.
      beta: the current best value achievable by B.
      player : the current player.
      returns the minimax value of the state.
    */

    boolean terminalState, XTurn;
    double currentValue, highestValue;
    int noPossibleMoves;
    GameState childMove, bestMove;
    Vector<GameState> nextStates;

    // The evaluation value of the current state.
    int stateMinimaxValue;


    /*
      If a preset depth level is reached or we reach an actual terminal state,
      i.e. the game is over. Then we return the value of this state.
    */

    terminalState = ( depth == 0|| currState.isEOG() );

    if( terminalState ){

      // The evaluation value of the current state.
      stateMinimaxValue = eval(currState);

      return stateMinimaxValue;


      /*
        If the present depth level is larger than zero and the game is not over we
        keep digging deeper.
      */
    }else{

      // The best move found so far.
      bestMove  = null;
      nextStates = new Vector<GameState>();

      // Find all possible moves from this state.
      currState.findPossibleMoves(nextStates);

      // If it is X:es turn to make a move. I.e. the maximizing players turn.
      XTurn = ( player  == Constants.CELL_X );
      if( XTurn ){

        // The move/state found with the highest score/heuristic value while
        // traversing the state tree using the miniMaxAlgorithm.
        highestValue = - Double.MAX_VALUE;

        // The current heuristic value for the current state.
        currentValue = - Double.MAX_VALUE;

        // For each child node/ each possible move from this current state.
        noPossibleMoves = nextStates.size();
        for( int move = 0; move < noPossibleMoves; move ++ ){

          // One of the next possible moves which is a child node in the state tree.
          childMove  =  nextStates.elementAt( move );

          // alpha is the current best value achived by the player X ( our player )
          // and currentvalue is the newly calculated value if we make the move childMove.
          // Therefore, we now want to know if this next move is btter than the previous
          // best move found.
          currentValue  = helperMethods.getLargerValue( currentValue, miniMaxAlgorithm( childMove, alpha, beta, Constants.CELL_O, depth - 1 ));
          alpha  =  helperMethods.getLargerValue( alpha, currentValue );

          // Store the node of the highest heuristic value found.
          if( currentValue > highestValue ){

            highestValue =  currentValue;
            bestMove = childMove;

          }

          // We prune the tree as we know that no better result may be achieved.
          // For more info se the assignment description.

          if( alpha >= beta ){

            optimalMove = bestMove;
            return highestValue;

          }
        }

        optimalMove = bestMove;
        return highestValue;

      // If it is O:es turn to make a move. I.e. the minimizing players turn.
      }else{

        // The move/state found with the highest score/heuristic value while
        // traversing the state tree using the miniMaxAlgorithm for the opponent.
        highestValue = Double.MAX_VALUE;

        // The current heuristic value for the current state.
        currentValue = Double.MAX_VALUE;

        // For each child node/ each possible move from this current state.
        noPossibleMoves = nextStates.size();

        // For each child node/ each possible move from this current state.
        for( int move = 0; move < noPossibleMoves; move ++ ){

          // One of the next possible moves which is a child node in the state tree.
          childMove  =  nextStates.elementAt( move );

          // The heuristic value for the current child node.
          currentValue  = helperMethods.getSmallerValue( currentValue, miniMaxAlgorithm( childMove, alpha, beta, Constants.CELL_X, depth - 1 ));

          // beta is the current best value achived by the player O (our opponent)
          // and currentvalue is the newly calculated value if we make the move childMove.
          // Therefore, we now want to know if this next move is btter than the previous
          // best move found.
          beta  =  helperMethods.getSmallerValue( beta, currentValue );

          // Store the node of the highest heuristic value found.
          if( currentValue < highestValue ){

            highestValue =  currentValue;
            bestMove = childMove;

          }

          // We prune the tree as we know that no better result may be achieved.
          // For more info se the assignment description.

          if( alpha >= beta ){

            optimalMove = bestMove;
            return highestValue;

          }
        }
        optimalMove = bestMove;
        return highestValue;
      }
    }
  }


  public int eval(GameState state){
    /*
    A heuristic function that attempts to estimates how advantageous the provided
    state is for this specific game. Thus, it is an estimate of an utility funciton.

    This is a static evaluation function which assigns a utility value to each
    board position by assigning weights to each of the 8 possible ways to win
    in a game of tic-tac-toe and then summing up those weights.

    The effect is that a move where it is possible that X can win or that X is
    winning gets a high score, one where X is blocked gets no score and where O
    is winning gets a negative score.

     More specifically:
     + 10000 for each four-in-a-row for the AI.
     +  1000 for each three-in-a-row for the AI.
     +   100 for each two-in-a-row (and empty cell) for the AI.
     +     1 for each one-in-a-row (and two empty cells) for the AI.
     -   100 for each one-in-a-row (and two empty cells) for the other player.
     -    10 for each two-in-a-row (and empty cell) for the other player.
     -  1000 for each three-in-a-row for the other player.
     - 10000 for each four-in-a-row for the other player.
           0 for all other states.
    */


    // Used to rate how good a specific move is by giving it a score.
    int totalScore;

    // Total number of possible winning positions.
    int noWinningPositions;

    // Number of pieces needed in a winning move.
    int noGamePieces;

    // Used further below to keep track of how good a move is.
    int maximizer, minimizer;

    // Used to evaluate a final move at the end of the game.
    boolean winningMove, losingMove, gameOver;

    // Used to check which players piece is placed at a certian position.
    boolean isContentX, isContentO;
    // Tells which players piece is placed at a certian position, or if it empty.
    int cellContent;

    totalScore  =  0;

    // Gets whether or not the current move marks the end of the game.
    gameOver = state.isEOG();

    // Gets whether or not this move would make player X (my player) the winner.
    winningMove = state.isXWin();

    // Gets whether or not this move would make player X (my player) the loser.
    losingMove = state.isOWin();

    // If this move results in the game ending we want to check if
    // it is a losing or winning move.
    if( gameOver ){

      // If it is a winning move we return an arbitrarily large score since we
      // want the game to make this move.
      if( winningMove ){

        totalScore = 100000;
        return totalScore;

      // If it is a losing move we return an arbitrarily small score since we
      // want the game engine to avoid the game at all cost.
      }else if( losingMove ){

        totalScore = - 100000;
        return totalScore;

      // If it is a draw move we return zero as a score since we
      // want the game engine to be neutral.
      }else{

        totalScore = 0;
        return totalScore;
      }
    }

    /*
    Each row in the winningPositions matrix contains the combination of positions
    on the tic tac toe board that will result in a win. As this is a 4x4 tic tac
    toe game each poition in the board is indexed with a value between 0-15 from
    the uppper left corner to the lower right.
    */
    int [][] winningPositions = {
      {  0,  1,  2,  3 },
      {  4,  5,  6,  7 },
      {  8,  9, 10, 11 },
      { 12, 13, 14, 15 },
      {  0,  4,  8, 12 },
      {  1,  5,  9, 13 },
      {  2,  6, 10, 14 },
      {  3,  7, 11, 15 },
      {  0,  5, 10, 15 },
      {  3,  6,  9, 12 }
    };

    /*
    Each row in the winningPositions matrix contains the combination of positions
    on the tic tac toe board that will result in a win. As this is a 4x4 tic tac
    toe game each poition in the board is indexed with a value between 0-15 from
    the uppper left corner to the lower right.
    */

    int [][] pointsPerMove = {
      {     0, -1, -100, -1000, -10000},
      {     1,  0,    0,     0,      0},
      {   100,  0,    0,     0,      0},
      {  1000,  0,    0,     0,      0},
      { 10000,  0,    0,     0,      0}
    };

    // Total number of possible winning positions.
    noWinningPositions = winningPositions.length;

    // Number of pieces needed in a winning move.
    noGamePieces = winningPositions[0].length;

    for ( int i = 0; i < noWinningPositions; i ++ ){

      maximizer = 0; minimizer = 0;

      // Going through all winning position indicies for the current winnig move being analyzed.
      for ( int j = 0; j < noGamePieces; j ++ ){

        /*
        Gets the content of a cell in the board from a board index.
        This tells us if the content is: X, O or empty.
        */
        cellContent = state.at( winningPositions[i][j] );

        // If the content is an X the maximizers value is increased.
        // If it is O the minimizers value is increased.
        isContentX = ( state.at( cellContent ) == Constants.CELL_X );
        isContentO = ( state.at( cellContent ) == Constants.CELL_O );

        if ( isContentX ) maximizer = maximizer + 1;
        if ( isContentO ) minimizer = minimizer + 1;
    }
    /*
    When all of the positions in the current winnig combination of positions
    have been looked at, the heuristic score for player X is updated.

    The score is updated by using the maximizer and minimizer variables above
    as column and row indicies in the pointsPerMove matrix. The effect is that
    a move where it is possible that X can win or that X is winning gets a high score,
    one where X is blocked gets no score and where O is winning gets a negative score.
    */

    totalScore += pointsPerMove[maximizer][minimizer];

    }
    return totalScore;
  }
}
