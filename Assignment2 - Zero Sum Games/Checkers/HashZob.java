import java.util.*;



public class HashZob{
  Random rand = new Random();

 int [][]ZobristMatrix =new int [32][4] ;

  public int indexOf(char piece){
    if (piece=='r') return 0;
    else if (piece=='w') return 1;
    else if (piece=='W') return 2;
    else if (piece=='R') return 3;
    else{//empty cell ie .
      return -1;
    }
  }

    public void createTable(){
    for (int i = 0; i<32; i++){
        for (int k = 0; k<4; k++){
          ZobristMatrix[i][k] = rand.nextInt();

      }
    }
  }

public String extractBoard(String state){
  String board =state.substring(0,32);
  return board;



}

public int calcHash(String board) {
    int hval = 0;

    for (int i = 0; i<32; i++){
      if(board.charAt(i)!='.'){
        int piece = indexOf(board.charAt(i));
        hval = hval^ZobristMatrix[i][piece];
      }

    }
    return hval;
}

  public HashZob(){
    rand.setSeed(5);
    createTable();

  }



}
