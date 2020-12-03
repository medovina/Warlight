# Warlight

![alt tag](warlight.png)

Warlight is a strategy game based on the classic board game [Risk](https://en.wikipedia.org/wiki/Risk_(game)).  This implementation in Java lets you write AI agents that play the game.

The code here is derived from the [original implementation](http://theaigames.com/competitions/warlight-ai-challenge) at theaigames.com.  My colleague Jakub Gemrot extended that version, adding an interactive visual map.  I have made many more changes: I added a new SVG-based user interface, greatly simplified the code, and added a new command-line interface along with other options and features.

## Quick start

To play the game from the keyboard on Linux or macOS, run

```
$ ./warlight
```

Or, on Windows:

```
> .\warlight
```

By default, you will play against the example agent Attila.  To play against a different agent, specify its name on the command line:

```
$ ./warlight Julius
```

Specify two agent names to watch one of them play the other:

```
$ ./warlight Julius Attila
```

To run a series of games between two agents with no visualization, specify the '-sim' option with a number of games to play, e.g.

```
$ ./warlight Julius Attila -sim 10
```

## Game rules

Warlight is played on a world map that contains 42 regions divided into 6 continents (Africa, Asia, Australia, Europe, North America, South America).

At the beginning of the game, the computer selects 4 random starting regions for each player.  No two starting regions for different opponents will border each other, and each player will receive at most 2 starting regions on any continent.  Each player receives 2 armies on each of their starting regions.  All other starting regions are initially neutral, and begin with 2 neutral armies.

Now the main part of the game begins.  The game proceeds in a series of rounds.  In each round, the following events occur:

1. player 1 places new armies
2. player 1 moves/attacks
3. player 2 places new armies
4. player 2 moves/attacks

In each round, each player can place a number of new armies on their territories.  Each player normally receives 5 new armies, plus bonus armies for each continent that they currently control.  A player controls a continent if they own all regions on that continent.  Larger continents yield more bonus armies; the game map displays the number of bonus armies available for each continent.

As an exception to the above, in the first round player 1 receives only 3 armies, not 5.  This is intended to compensate for the advantage of moving first.

In each round, each player can perform a series of moves or attacks.  A player can move armies between adjacent territories that they own.  As least one army must always stay behind in any move or attack; it is not possible to abandon a territory.

In an attack, a player moves a number of armies to an adjacent territory that is neutral or owned by the other player.  In an attack, the defenders will lose a number of armies equal to 60% of the number of attacking armies.  The attackers will lose a number of armies equal to 70% of the number of defending armies.  In this calculation, any fractional number is rounded probabilistically to the nearest whole number.  For example, if there are 3 attacking armies, then (60%)(3) = 1.8, so there is an 80% chance that the attackers will destroy 2 defending armies, and a 20% change that they will destroy only one.

If all defending armies are destroyed and at least one attacking army survives, then the attack succeeds, and the remaining armies occupy the territory.  If all attacking and defending armies are destroyed, the defender is granted one extra army and the attack fails.

In each game round, each army may move or attack only once.  For example, suppose that player 1 has 6 armies in North Africa, and moves 4 more armies from Congo to North Africa.  In that same round, the player may attack from North Africa to Brazil with at most 5 armies; the newly arrived armies may not attack.  Suppose that the attack succeeds and 4 surviving armies occupy Brazil.  The player must wait until the next round before launching a subsequent attack from Brazil.

The game is played until one player has no armies remaining; that player has lost.  If 100 rounds are played and both players still have armies on the board, the player with the most regions wins.  If both players have the same number of regions, the player with the most armies wins.  If both players have the same number of regions and armies (which is extremely unlikely), the game is a draw.

## User interface

When playing as a human against a computer player, click the map or press the space bar to advance past informational messages such as "New Round".  When placing or moving armies, use the left mouse button to add armies to be placed/moved and use the right mouse button to subtract armies.  To move or attack, first click a source region, then repeatedly click a destination region until you have moved as many armies as you like.  Enter all your moves or attacks for a single round before pressing DONE.

When two agents are playing each other, you can left click the map or press the space bar to advance to the next action.  Press 'N' to skip to the next game round.  Alternatively, press 'C' to enter continuous mode, in which the game will periodically advance to the next action automatically.  In this mode, press '+' and '-' to adjust the time delay between actions.

## Game variations

If you specify the -manual option on the command line, then players will choose their starting regions manually.  Specifically, they will take turns picking a single region at a time until they have each chosen 4 starting regions.

If you specify the -warlords option, then each player will start with only 3 regions, each on a separate continent.  These will be assigned automatically by default.  However, if you combine the -manual and -warlords options, then the computer will select 6 random regions, once per continent, and the players will choose these manually.

## Writing an agent

The class `MyAgent` contains a dummy agent that plays randomly, i.e. it is equivalent to RandomAgent.  Usually MyAgent will lose.  You can use MyAgent as a starting point for developing your own game-playing agent.

Here is [documentation](https://ksvi.mff.cuni.cz/~dingle/2020-1/ai_1/warlight/warlight_api.html) for the Warlight API.
