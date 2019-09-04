### Checkers
<p float="left" align='center'>  
  <img src='http://thinkartificial.org/wp-content/uploads/2007/07/180px-draughts.png' width="20%" height="20%"
 />

In this problem, a program that plays the game of checkers is implemented. The goal is to implement a strategy that 
allows your program to win (or not lose) as often as possible. You will be provided a skeleton which generates a list 
of valid moves for you to choose from (see section 2).

The game of checkers is played by two players, on opposite sides of a squared board. Each player has twelve pieces, which
can move diagonally. The goal of the game is to leave your opponent without any movement possibility. This is achieved usually 
by capturing all of the opponents pieces by jumping over them.


More information can be found here: [Assignment Description](https://kth.kattis.com/problems/kth.ai.checkers) & 
[Theory](https://github.com/alexandrahotti/Artificial-Intelligence/blob/master/Assignment2%20-%20Zero%20Sum%20Games/Assignment_Description.pdf).

#### AI perspective
The game is played as a zero sum game using the minimax algorithm with alpha-beta pruning and a own designed heuristic function. 
The heuristic function is designed specifically for the checkers game logic in is thus a bit complex. In order to save time the 
efficiency of the alpha-beta pruning is improved using a [Killer Heuristic function](https://en.wikipedia.org/wiki/Killer_heuristic). 
The method  considers the best moves first. This is because the best moves are the ones most likely to produce a cutoff, a condition 
where the game playing program knows that the position it is considering could not possibly have resulted from best play by both sides 
and so need not be considered further. I.e. the game playing program will always make its best available move for each position. It only
needs to consider the other player's possible responses to that best move, and can skip evaluation of responses to (worse) moves it will 
not make.
