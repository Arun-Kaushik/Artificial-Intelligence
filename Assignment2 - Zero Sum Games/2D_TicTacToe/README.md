# 2D Tic-tac-toe

In this problem, you will implement a program that plays the game of Tic-Tac-Toe. The goal is to implement a strategy 
that allows your program to win (or not lose) as often as possible. You will be provided a skeleton which generates a 
list of valid moves for you to choose from (see section ).

There is an square board 𝐻, consisting of 16 cells. Two players, 𝑋 and 𝑌, take turns marking blank cells in 𝐻. The first
player to mark 4 cells along a row wins. Here by a row we mean any 4 cells, whose centres lie along a straight line in 𝐻.
So, any horizontal, vertical and diagonal row is winning. In this assignment, our goal is to find the best possible move 
for player 𝑋 given a particular state of the game.


## AI perspective
The game is played as a zero sum game using the minimax algorithm with alpha beta pruning and a heuristic function.

<p float="left" align='center'>  
  <img src='http://theoryofprogramming.azurewebsites.net/wp-content/uploads/2017/12/minimax-1.jpg' width="30%" height="30%"
 />


