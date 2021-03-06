# Duck Hunt
<p float="left" align='center'>  
  <img src='https://www.mariowiki.com/images/7/75/WWIMM_DuckHunt.png' width="40%" height="30%"
 />

## Table of Contents
The game logic can be found in the Player class:
[Player](https://github.com/alexandrahotti/Artificial-Intelligence/blob/master/Assignment1%20-%20Hidden%20Markov%20Models%20(HMM)/Duck%20Hunt/GAME/Player.java)

The Hidden Markov Model can be found here:
[HMM](https://github.com/alexandrahotti/Artificial-Intelligence/blob/master/Assignment1%20-%20Hidden%20Markov%20Models%20(HMM)/Duck%20Hunt/GAME/HMM.java)


## The Game
This version of Duck Hunt is a generalized version of the original game: There is a sky with birds flying. The birds fly around the sky until they are shot down. The player has to observe the flight patterns of the birds and predict their next move in order to shoot them down. If the prediction is correct, the player will hit the bird and gain one point. If the prediction is wrong the player will miss the bird and lose one point. The game is over when all birds are shot down or when the time runs out.

Also, the birds have different species and will behave differently in the air. Most birds give one point when shot, but the black stork species is very rare and will hurt your score seriously if you shoot it. The species are identified after each round. In order to prioritize your targets you will need to identify them during flight.

The players may also bet on the species of each bird after each round finishes. The players will get one point for each correct guess and lose one point for each incorrect guess. Guessing is optional, you will choose to guess or not for each bird. The score will be unaffected if no guessing is made. You will get to know the correct species for each bird you make a guess on regardless of whether your guess was correct or not.

**The code for the game logic can be found in the Player class.**

## AI Perspective
This version of the game can be broken down into three main, dependent, topics:

* Predicting the flight trajectory of the birds so that they can be shot down.
* Make decisions to shoot or not based on the confidence of prediction of bird species and flight trajectory.
* Identifying the bird species to avoid forbidden targets and maximize score.

**The code for the HMM can be found in the HMM class.**
