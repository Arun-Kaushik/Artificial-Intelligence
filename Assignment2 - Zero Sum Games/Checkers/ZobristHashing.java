import java.util.*;

/*
Zobrist hashing is a hash function construction used in computer programs that
play abstract board games, such as chess and Go, to implement transposition tables,
a special kind of hash table that is indexed by a board position and used to avoid
analyzing the same position more than once.
*/

public class ZobristHashing{

  Random rand = new Random();

  // The different game pieces in checkers.
  char redPiece, whitePiece;
  char whiteKing, redKing;

  // transposition table parameters.
  int [][] transpositionTable;
  int noBoardPositions, noPieceTypes;


  public ZobristHashing(){
    // A uniform distribution used for sampling initial values
    // for the transposition table.
    rand.setSeed(5);

    redPiece = 'r';
    whitePiece = 'w';
    whiteKing = 'W';
    redKing = 'R';

    noBoardPositions = 32;
    noPieceTypes = 4;

    transpositionTable = new int [noBoardPositions][noPieceTypes];

    createTranspositionTable();
  }


  public int getPieceIdentifier( char piece ){
    //Gets a identifier for a provided game piece that can be used in the transposition
    // table to retrive the correct transposition table value.

    if ( piece == redPiece ) return 0;
    else if ( piece == whitePiece ) return 1;
    else if ( piece == whiteKing ) return 2;
    else if ( piece == redKing ) return 3;
    else return - 1;

  }


  public void createTranspositionTable(){
    // Initlizing the transposition table with a uniform distribution.

    for (int boardPosition = 0; boardPosition < noBoardPositions; boardPosition ++ ){
      for (int pieceType = 0; pieceType < noPieceTypes; pieceType ++){

        transpositionTable[boardPosition][pieceType] = rand.nextInt();

        }
      }
  }


  public String getTranspositionTable( String state ){
    // Retrives the transposition table for the current
    // game state.
    String transpositionTable;

    transpositionTable = state.substring( 0, 32 );

    return transpositionTable;

  }


  public int calculateHashValue( String board ) {
    // The Zobrist hash is computed by combining each combination of a
    // piece and a position bitstrings using bitwise XOR.

    int hashValue, piece;
    boolean containsGamePiece;

    hashValue = 0;

    for (int boardPosition = 0; boardPosition < noBoardPositions; boardPosition ++ ){

      containsGamePiece = board.charAt(boardPosition) != '.';

      if( containsGamePiece ){

        piece = getPieceIdentifier( board.charAt(boardPosition) );

        // Calculating the hash by combining a piece and a position bitstrings
        // using bitwise XOR.
        hashValue = hashValue ^ transpositionTable[boardPosition][piece];
      }

    }
    return hashValue;
  }

}
