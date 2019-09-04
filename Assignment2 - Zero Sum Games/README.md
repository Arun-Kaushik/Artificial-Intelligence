# Zero Sum Games

## Assignment 2 - Zero Sum Games

### 2D Tic-tac-toe
In this problem, a program that plays the game of Tic-Tac-Toe is implemented. The goal is to implement a strategy that allows the program to win (or not lose) as often as possible.

The game logic is as follows. There is an square board 𝐻, consisting of 16 cells. Two players, 𝑋 and 𝑌, take turns marking blank cells in 𝐻. The first player to mark 4 cells along a row wins. Here by a row we mean any 4 cells, whose centres lie along a straight line in 𝐻. So, any horizontal, vertical and diagonal row is winning. The goal is to find the best possible move for player 𝑋 given a particular state of the game within a time limit.

More information can be found here: [Assignment Description](https://kth.kattis.com/problems/kth.ai.tictactoe2d) & [Theory](https://github.com/alexandrahotti/Artificial-Intelligence/blob/master/Assignment2%20-%20Zero%20Sum%20Games/Assignment_Description.pdf).


#### AI perspective
The game is played as a zero sum game using the minimax algorithm with alpha beta pruning and a heuristic function.

<p float="left" align='center'>  
  <img src='http://theoryofprogramming.azurewebsites.net/wp-content/uploads/2017/12/minimax-1.jpg' width="30%" height="30%"
 />


In this assignment, our goal is to find the best possible move for player 𝑋 given a particular state of the game.

### 3D Tic-tac-toe
In this problem, a program that plays the game of Tic-Tac-Toe is implemented. The goal is to implement a strategy that allows the program to win (or not lose) as often as possible.

In this assignment a special case of 3-dimensional generalization of Tic-Tac-Toe game is considered. The rules are simple. There is a 3-dimensional hypercube H, consisting ofn 43 cells. Two players, X and Y, take turns marking blank cells in H. The first player to mark 4 cells along a row wins. Here by a row we mean any 4 cells, whose cetres lie along a straight line in H. Winning rows lie along the 48 orthogonal rows (those which are parallel to one of the edges of the cube), the 24 diagonal rows, or the 4 main diagonals of the cube, making 76 winning rows in total. Player X always starts the game. In this assignment, the goal is to find the best possible move for player X given a particular state of the game within a time limit.

More information can be found here: [Assignment Description](https://kth.kattis.com/problems/kth.ai.tictactoe3d) & [Theory](https://github.com/alexandrahotti/Artificial-Intelligence/blob/master/Assignment2%20-%20Zero%20Sum%20Games/Assignment_Description.pdf).

### Checkers
<p float="left" align='center'>  
  <img src='http://thinkartificial.org/wp-content/uploads/2007/07/180px-draughts.png' width="20%" height="20%"
 />

In this problem, a program that plays the game of checkers is implemented. The goal is to implement a strategy that allows your program to win (or not lose) as often as possible. You will be provided a skeleton which generates a list of valid moves for you to choose from (see section 2).

The game of checkers is played by two players, on opposite sides of a squared board. Each player has twelve pieces, which can move diagonally. The goal of the game is to leave your opponent without any movement possibility. This is achieved usually by capturing all of the opponents pieces by jumping over them.


More information can be found here: [Assignment Description](https://kth.kattis.com/problems/kth.ai.checkers) & [Theory](https://github.com/alexandrahotti/Artificial-Intelligence/blob/master/Assignment2%20-%20Zero%20Sum%20Games/Assignment_Description.pdf).

#### AI perspective
The game is played as a zero sum game using the minimax algorithm with alpha-beta pruning and a own designed heuristic function. The heuristic function is designed specifically for the checkers game logic in is thus a bit complex. In order to save time the efficiency of the alpha-beta pruning is improved using a [Killer Heuristic function](https://en.wikipedia.org/wiki/Killer_heuristic). The method  considers the best moves first. This is because the best moves are the ones most likely to produce a cutoff, a condition where the game playing program knows that the position it is considering could not possibly have resulted from best play by both sides and so need not be considered further. I.e. the game playing program will always make its best available move for each position. It only needs to consider the other player's possible responses to that best move, and can skip evaluation of responses to (worse) moves it will not make.
