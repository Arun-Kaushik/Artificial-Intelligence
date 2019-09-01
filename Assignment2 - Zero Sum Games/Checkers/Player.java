//#####CHECKERS FINAL VERSION########################
//https://web.archive.org/web/20071031100051/http://www.brucemo.com/compchess/programming/hashing.htm
import java.util.*;

public class Player {

    /**
     * Performs a move
     *
     * @param pState
     *            the current state of the board
     * @param deadline
     *            time before which we must have returned
     * @return the next state the board is in after our move
     */


     int player;

     Deadline dead;

     double deadlineTime =Math.pow(10,8)+5*Math.pow(10,7);

     HashMap<Integer, Integer> map = new HashMap<>();
     HashZob hz =new HashZob();

     int WasPruned =10000;
     int WasNotPruned =-10000;

    public GameState play(final GameState pState, final Deadline deadline) {

      Vector<GameState> nextStates = new Vector<GameState>();

      pState.findPossibleMoves(nextStates);

      if (nextStates.size() == 0) {
          return new GameState(pState, new Move());
      }

      dead=deadline;

      int beta = Integer.MAX_VALUE;
      int alpha =Integer.MIN_VALUE;

      int maxd=11;

      GameState topPerfState=nextStates.elementAt(0);
      player = pState.getNextPlayer();


      int max_score = Integer.MIN_VALUE;
      int bestd=1;

       for (GameState state: nextStates){
          if(!map.containsKey(hz.calcHash(hz.extractBoard(state.toMessage())))){
           map.put(hz.calcHash(hz.extractBoard(state.toMessage())),WasNotPruned);

         }
       }

      for (int d=0;d<maxd; d++){
        Collections.sort(nextStates, new Comparator<GameState>(){
          public int compare(GameState stateA, GameState stateB){
            int hashvalA = hz.calcHash(hz.extractBoard(stateA.toMessage()));
            int hashvalB = hz.calcHash(hz.extractBoard(stateB.toMessage()));

            int valueA =map.get(hashvalA);
            int valueB =map.get(hashvalB);
            return valueA < valueB ? 1 : valueB < valueA ? -1 : 0;

          }
        });

          for (GameState stateS: nextStates){
              int v=alphaBetaPruning(stateS,alpha,beta,d,false); // har kor vi ju for maximizing player df borde vara false
              if(v > alpha) {
                  alpha = v;
                  topPerfState = stateS;
              }
              if(beta <= alpha){
                int hashval = hz.calcHash(hz.extractBoard(stateS.toMessage()));
                map.put(hashval,WasPruned);
                 break;
               }

            }
      }
      return topPerfState;

      /**
       * Here you should write your algorithms to get the best next move, i.e.
       * the best next state.
       **/
    }
public int heuristic111(GameState currState){

  int totalScore=0;

  int no_white=0;
  int no_red=0;

  int no_red_kings=0;
  int no_white_kings=0;


  int board_side_red =0;
  int board_side_white =0;

  int red_kings_advancing =0;
  int white_kings_advancing =0;


  int no_red_protected =0;
  int no_white_protected =0;

  int itter_left=4;
  int itter_right=3;

	int unprotect_itter = 5;
	int unprotect_itter_2 = 8;

	int three_1 = 0;
	int three_2 = 0;

	int no_white_under_attack = 0;
	int no_red_under_attack = 0;


  Move lastMove = currState.getMove();
  int moveType = lastMove.getType();
  int lastPlayer =currState.getNextPlayer();



  if(lastMove.isRedWin() && player ==Constants.CELL_RED ){
    return 1000000;
  }else if(lastMove.isWhiteWin() && player ==Constants.CELL_WHITE ){
      return 1000000;
  }else if(lastMove.isWhiteWin() && player !=Constants.CELL_WHITE ){
        return -1000000;
}else if(lastMove.isRedWin() && player !=Constants.CELL_RED ){
  return -1000000;
  }else if(moveType==Move.MOVE_DRAW){
    return 0;
  }

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


public int alphaBetaPruning(GameState currState,int alpha,int beta, int d,boolean ismaxplayer){

    if((d==0) || (currState.isEOG()) || dead.timeUntil()<deadlineTime){
      return heuristic111(currState);
    }
      Vector<GameState> nextStates = new Vector<GameState>();
      currState.findPossibleMoves(nextStates);

      for (int g =0;g<nextStates.size();g++){

        if(!map.containsKey(hz.calcHash(hz.extractBoard(nextStates.elementAt(g).toMessage())))){
          map.put(hz.calcHash(hz.extractBoard(nextStates.elementAt(g).toMessage())),WasNotPruned);
        }

      }

      Collections.sort(nextStates, new Comparator<GameState>(){
        public int compare(GameState stateA, GameState stateB){
          int hashvalA = hz.calcHash(hz.extractBoard(stateA.toMessage()));
          int hashvalB = hz.calcHash(hz.extractBoard(stateB.toMessage()));

          int valueA =map.get(hashvalA);
          int valueB =map.get(hashvalB);

          return valueA < valueB ? 1 : valueB < valueA ? -1 : 0;

        }
      });

      if(ismaxplayer){


        int vbest=Integer.MIN_VALUE;
        for (GameState cNode: nextStates){

        int v =alphaBetaPruning(cNode,alpha,beta,d-1,false);
        vbest=getMaximum(v,vbest);
        alpha = getMaximum(alpha, vbest);

        if(beta<=alpha){
          int hashval = hz.calcHash(hz.extractBoard(cNode.toMessage()));
          map.put(hashval,WasPruned);
          break;}
      }
      return vbest;
    }else{
      int vbest=Integer.MAX_VALUE;
      for (GameState cNode: nextStates){

      int v =alphaBetaPruning(cNode,alpha,beta,d-1,true);
      vbest =getMinimum(vbest,v);
      beta = getMinimum(beta, vbest);
      if(beta<=alpha){
        int hashval = hz.calcHash(hz.extractBoard(cNode.toMessage()));
        map.put(hashval,WasPruned);
        break;}
    }
    return vbest;
    }
}

int piecediff(int no_p1, int no_p2 ){
  return no_p1-no_p2;
}

    public int getMaximum(int vold, int vnew){
      if(vold>=vnew){
       return vold;
     }
      return vnew;
    }

    public int getMinimum(int vold, int vnew){
      if(vold<=vnew){
      return vold;
    }
      return vnew;
    }
  }
