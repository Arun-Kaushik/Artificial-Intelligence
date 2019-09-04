# Assignment 1
Assignment 1 deals with Hidden Markov Models.

## HMM0 Next Emission Distribution
The purpose of this task is to predict how the system will evolve over time and estimate the probability for different emissions / events in the system i.e. what can be observed from the HMM. Given parameters of the HMM i.e. the state probability distribution (i.e. the probability that the system is in each of the N states), the transition matrix (i.e. the matrix that gives the probability to transition from one state to another) and the emission matrix (i.e. the matrix that gives the probability for the different emissions / events / observations given a certain state). These parameters are commonly referred to as λ = (A, B, π).

More specifically, the task is to given the current state probability distribution estimate the probabity for the different emissions after the next transition, i.e. after the system has made a single transition.

The code can be found here: [HMM0](https://github.com/alexandrahotti/Artificial-Intelligence/tree/master/Assignment1%20-%20Hidden%20Markov%20Models%20(HMM)/A1)


## HMM1 Probability of Emission Sequence
In this task the probability to observe a certain emission sequence given a HMM model is estimated. In the task the HMM model and a sequence of observations are given (aka emissions, events, etc). The probability is estimated using the forward algorithm or α-pass algorithm which gives us. More specificallt the probabilities calculated are the probabilities of being  in a certain state i at time t after having observed a observation sequence up to the curren time  step t.

## HMM2 Estimate Sequence of States
In this task the probability to observe a certain emission sequence given a HMM model is estimated. In the task the HMM model and a sequence of observations are given (aka emissions, events, etc). The probability is estimated using the forward algorithm or α-pass algorithm which gives us. More specificallt the probabilities calculated are the probabilities of being  in a certain state i at time t after having observed a observation sequence up to the curren time  step t.

The code can be found here: [HMM1](https://github.com/alexandrahotti/Artificial-Intelligence/tree/master/Assignment1%20-%20Hidden%20Markov%20Models%20(HMM)/A2)

## HMM3 Estimate Model
In this task the model parameters for an HMM is estimated using the Baum Welch algorithm.

More specifically this is achieved by combining the backward and forward pass algorithms, also called: α-pass and β-pass.
To estimate the HMM parameters λ = (A, B, π) they are iteratively reestimated using two parameters called di-gamma γ_t(i,j) and gamma γ_t(i) until convergence is reached.

The code can be found here: [HMM3](https://github.com/alexandrahotti/Artificial-Intelligence/tree/master/Assignment1%20-%20Hidden%20Markov%20Models%20(HMM)/A4)

## Duck Hunt
<p float="left" align='center'>  
  <img src='https://www.mariowiki.com/images/7/75/WWIMM_DuckHunt.png' width="40%" height="30%"
 />

### Table of Contents
All code can be found here:
[Duck Hunt](https://github.com/alexandrahotti/Artificial-Intelligence/tree/master/Assignment1%20-%20Hidden%20Markov%20Models%20(HMM)/Duck%20Hunt)

The game logic can be found in the Player class:
[Player](https://github.com/alexandrahotti/Artificial-Intelligence/blob/master/Assignment1%20-%20Hidden%20Markov%20Models%20(HMM)/Duck%20Hunt/GAME/Player.java)

The Hidden Markov Model can be found here:
[HMM](https://github.com/alexandrahotti/Artificial-Intelligence/blob/master/Assignment1%20-%20Hidden%20Markov%20Models%20(HMM)/Duck%20Hunt/GAME/HMM.java)


#### The Game
This version of Duck Hunt is a generalized version of the original game: There is a sky with birds flying. The birds fly around the sky until they are shot down. The player has to observe the flight patterns of the birds and predict their next move in order to shoot them down. If the prediction is correct, the player will hit the bird and gain one point. If the prediction is wrong the player will miss the bird and lose one point. The game is over when all birds are shot down or when the time runs out.

Also, the birds have different species and will behave differently in the air. Most birds give one point when shot, but the black stork species is very rare and will hurt your score seriously if you shoot it. The species are identified after each round. In order to prioritize your targets you will need to identify them during flight.

The players may also bet on the species of each bird after each round finishes. The players will get one point for each correct guess and lose one point for each incorrect guess. Guessing is optional, you will choose to guess or not for each bird. The score will be unaffected if no guessing is made. You will get to know the correct species for each bird you make a guess on regardless of whether your guess was correct or not.

**The code for the game logic can be found in the Player class.**

#### AI Perspective
This version of the game can be broken down into three main, dependent, topics:

* Predicting the flight trajectory of the birds so that they can be shot down.
* Make decisions to shoot or not based on the confidence of prediction of bird species and flight trajectory.
* Identifying the bird species to avoid forbidden targets and maximize score.

**The code for the HMM can be found in the HMM class.**
