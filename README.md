# Warlight

![alt tag](warlight.png)

Warlight is a two-player strategy game based on the classic board game [Risk](https://en.wikipedia.org/wiki/Risk_(game)).  This implementation in Java lets you write AI agents that play the game.

The code here is derived from the [original implementation](http://theaigames.com/competitions/warlight-ai-challenge) at theaigames.com.  My colleague Jakub Gemrot at Charles University made extensive changes, adding an interactive visual map so that human players can play against the computer.  I have made various changes as well, building on Jakub's work.

## Quick start

This repository contains .project and .classpath files that define 4 Eclipse projects (Warlight, Warlight-Bots, Warlight-Playground, Warlight-Tournament).  You can import these into Eclipse using the command Import -> General -> Existing&nbsp;Projects&nbsp;into&nbsp;Workspace.  (It should not be difficult to import the projects into other IDEs such as Intelli/J as well.)

The class `conquest.bot.custom.AggressiveBot` is an example bot that plays the game.  The main() method in this class launches a game where you can play interactively against AggressiveBot.  That's a good way to start learning about the game.

The class `conquest.bot.playground.MyBot` contains a dummy bot that plays randomly.  The main() method in this class launches a game where MyBot plays against AggressiveBot.  Usually MyBot will lose.  You can use MyBot as a starting point for developing your own game-playing agent.

The class `conquest.tournament.ConquestFightConsole` can play a series of games between two bots.  If you uncomment the line "`args = getTestArgs_1v1()`" in the main() method there, it will play 10 games of MyBot against AggressiveBot and will report win percentage statistics.  You can use this class to evaluate your bot's performance.

## Game rules

Warlight is played on a world map that contains 42 regions divided into 6 continents (Africa, Asia, Australia, Europe, North America, South America).

At the beginning of the game, each player must choose 3 starting regions.  The selection process happens as follows.  First the computer picks 12 random regions that are available for selection.  These will always include 2 regions on each of the 6 continents.  The players take turns picking a single region at a time from the available regions until they have each chosen 3 starting regions.  Each player receives 2 armies on each of their starting regions.  All other starting regions are initially neutral, and begin with 2 neutral armies.

Now the main part of the game begins.  The game proceeds in a series of rounds.  In each round, the following events occur:

1. player 1 places new armies
2. player 1 moves/attacks
3. player 2 places new armies
4. player 2 moves/attacks

In each round, each player can place a number of new armies on their territories.  Each player normally receives 5 new armies, plus bonus armies for each continent that they currently control.  A player controls a continent if they own all regions on that continent.  Larger continents yield more bonus armies; the game map displays the number of bonus armies available for each continent.

As an exception to the above, in the first round player 1 receives only 2 armies, not 5.  This is intended to compensate for the advantage of moving first.

In each round, each player can perform a series of moves or attacks.  A player can move armies between adjacent territories that they own.  As least one army must always stay behind in any move or attack; it is not possible to abandon a territory.

In an attack, a player moves a number of armies to an adjacent territory that is neutral or owned by the other player.  An attack consists of a series of combat rounds.  In each combat round, the attacker has a 70% chance of losing one army, and the defender has a 60% chance of losing one army.  The combat rounds continue until either the attacker or defender have no armies left.  If all attacking armies are destroyed, the attack has failed.  If all defending armies are destroyed, the attack succeeds, and the remaining armies occupy the territory.  If all attacking and defending armies are destroyed, the defender is granted one extra army and the attack fails.

In each game round, each army may move or attack only once.  For example, suppose that player 1 has 6 armies in North Africa, and moves 4 more armies from Congo to North Africa.  In that same round, the player may attack from North Africa to Brazil with at most 5 armies; the newly arrived armies may not attack.  Suppose that the attack succeeds and 4 surviving armies occupy Brazil.  The player must wait until the next round before launching a subsequent attack from Brazil.

The game is played until one player has no armies remaining; that player has lost.  If 100 rounds are played and both players still have armies on the board, the player with the most regions wins.  If both players have the same number of regions, the player with the most armies wins.  If both players have the same number of regions and armies (which is extremely unlikely), the game is a draw.

## User interface

When playing as a human against a bot, click the map or press the space bar to advance past informational messages such as "NEW ROUND".  When placing or moving armies, use the left mouse button to add armies to be placed/moved and use the right mouse button to subtract armies.  To move or attack, first click a source region, then repeatedly click a destination region until you have moved as many armies as you like.  Enter all your moves or attacks for a single round before pressing DONE.

When two bots are playing each other, you can left click the map or press the space bar to advance to the next action.  Press 'N' to skip to the next game round.  To fast forward through the game, right click the map and hold the mouse button down.  Alternatively, press 'C' to enter continuous mode, in which the game will periodically advance to the next action automatically.  In this mode, press '+' and '-' to adjust the time delay between actions.

## Writing a bot

Use `conquest.bot.playground.MyBot` as a starting point.  Here is [documentation](https://ksvi.mff.cuni.cz/~dingle/2019/ai/warlight_api.html) for the most important classes, fields and methods in the Warlight API.

## More notes

The following notes are from Jakub Gemrot and provide more detailed information about some of the changes he made to the game:

1) possible to play with "internal" players (i.e. bots directly on java classpath), which allows you to perform better Java bot debugging

-- you can hijack controls of internal players when the game is visualized (press 'H' to toggle PLR1 hijack, press 'J' to toggle PLR2 hijack)

2) may output replay log into the file

3) may perform replay (parses replay file and replays the match) ~ fully working

4) human player (you can play against your AI!), use "human" as bot init command

-- beware, some buttons can be "right clicked" to "reverse the effect" (e.g. when placing armies left click -> plus, right click -> minus or when moving armies left click -> OK, right click -> cancel)

5) better GameState abstraction than plain BotState provided, use GameBot as a base class for your bots

6) slim GameStateCompact representation of the game state that can be used for performance searches (not the best, but quite ok); use GameStateCompact.fromGameState(gameState) you have in your GameBot

7) Warlight-Tournament project can be used to automate matches between bots using command line tools (see ConquestFightConsole and ConquestTableConsole classes); see Warlight-Competition for example how to quickly setup tournament
batch files (be sure to stop by and read Warlight-Competition/readme.txt)

8) possibility to execute "process" player from within concrete directory, use "dir;process" as bot init command to specify the directory from which you would like the bot process to be executed

-- not a big feature but very handy for automation

-- if you deal with Java bots, you might want to run your Java bots as "external bots" using "dir;process" indirectly running JavaBot class instead of original bot class (i.e. make JavaBot to instantiate and run your tournament bot);
this will spare you the problems with "invalid" main of the bot you want to execute (as long as it has parameterless constructor)

## PROJECT STRUCTURE

**Warlight** -> simulator and visualizator of Warlight AI Challenge

**Warlight-Bots** -> project providing better support for Java bots; includes OOP as well as compact representation of the game state. AggressiveBot example runnable as is.

**Warlight-Playground** -> stub project for your code; it contains ConquestBost class that is copy-pasted code from AggressiveBot also runnable and an entry point for you to start coding your bot.

**Warlight-Tournament** -> automation of matches

**Warlight-Competition** -> folder stub for perfoming table-type tournaments

