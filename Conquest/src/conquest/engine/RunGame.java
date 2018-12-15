// Copyright 2014 theaigames.com (developers@theaigames.com)

//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at

//        http://www.apache.org/licenses/LICENSE-2.0

//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//	
//    For the full copyright and license information, please view the LICENSE
//    file that was distributed with this source code.

package conquest.engine;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import conquest.bot.external.JavaBot;
import conquest.engine.Engine.EngineConfig;
import conquest.engine.Robot.RobotConfig;
import conquest.engine.replay.FileGameLog;
import conquest.engine.replay.GameLog;
import conquest.engine.replay.ReplayHandler;
import conquest.engine.robot.HumanRobot;
import conquest.engine.robot.IORobot;
import conquest.engine.robot.InternalRobot;
import conquest.engine.robot.ProcessRobot;
import conquest.game.*;
import conquest.game.move.MoveResult;
import conquest.game.world.Continent;
import conquest.game.world.Region;
import conquest.view.GUI;

public class RunGame
{
	
	public static class Config implements Cloneable {
		
		public String gameId = "GAME";
		
		/**
		 * Used by ENGINE as PLAYER IDENTIFIER of the player 1
		 * BETTER NOT TO ALTER AT ALL...
		 */
		public String playerId1 = "PLR1";
		/**
		 * Used by ENGINE as PLAYER IDENTIFIER of the player 2
		 * BETTER NOT TO ALTER AT ALL...
		 */
		public String playerId2 = "PLR2";
		
		/**
		 * Human-readable name of player 1 to display during visualization or to report into CSV.
		 */
		public String player1Name = "Bot1";
		/**
		 * Human-readable name of player 2 to display during visualization or to report into CSV.
		 */
		public String player2Name = "Bot2";
		
		public String bot1Init;
		public String bot2Init;
		
		public boolean visualize = true;
		
		public Boolean visualizeContinual = null;
		
		public Integer visualizeContinualFrameTimeMillis = null;
		
		public boolean logToConsole = true;
		
		public File replayLog = null;
		
		public EngineConfig engine = new EngineConfig();
		
		public String asString() {
			return gameId + ";" + playerId1 + ";" + playerId2 + ";" + player1Name + ";" + player2Name + ";" +
		           visualize + ";" + visualizeContinual + ";" + visualizeContinualFrameTimeMillis + ";" +
				   logToConsole + ";" + engine.asString();
		}
		
		@Override
		public Config clone() {
			Config result = fromString(asString());
			
			result.replayLog = replayLog;
			result.bot1Init = bot1Init;
			result.bot2Init = bot2Init;
			
			return result;
		}
		
		public String getCSVHeader() {
			return "ID;PlayerName1;PlayerName2;" + engine.getCSVHeader();
		}
		
		public String getCSV() {
			return gameId + ";" + player1Name + ";" + player2Name + ";" + engine.getCSV();
		}
		
		public static Config fromString(String line) {
			
			String[] parts = line.split(";");
			
			Config result = new Config();

			result.gameId = parts[0];
			result.playerId1 = parts[1];
			result.playerId2 = parts[2];
			result.player1Name = parts[3];
			result.player2Name = parts[4];
			result.visualize = Boolean.parseBoolean(parts[5]);
			result.visualizeContinual = (parts[6].toLowerCase().equals("null") ? null : Boolean.parseBoolean(parts[6]));
			result.visualizeContinualFrameTimeMillis = (parts[7].toLowerCase().equals("null") ? null : Integer.parseInt(parts[7]));
			result.logToConsole = Boolean.parseBoolean(parts[8]);
			
			int engineConfigStart = 0;
			for (int i = 0; i < 9; ++i) {
				engineConfigStart = line.indexOf(";", engineConfigStart);
				++engineConfigStart;
			}
			
			result.engine = EngineConfig.fromString(line.substring(engineConfigStart));
			
			return result;
		}
		
	}
	
	public static class GameResult {
		
		public Config config;
		
		public int player1Regions;
		public int player1Armies;
		
		public int player2Regions;
		public int player2Armies;
		
		public Team winner = null;
		
		/**
		 * Number of the round the game ended.
		 */
		public int round;

		public String getWinnerId() {
			if (winner == null) return "NONE";
			switch (winner) {
			case NEUTRAL: return "NONE";
			case PLAYER_1: return config == null ? "Player1" : config.playerId1;
			case PLAYER_2: return config == null ? "Player2" : config.playerId2;
			}
			return null;
		}
		
		public String getWinnerName() {
			if (winner == null) return "NONE";
			switch (winner) {
			case NEUTRAL: return "NONE";
			case PLAYER_1: return config == null ? "Bot1" : config.player1Name;
			case PLAYER_2: return config == null ? "Bot2" : config.player2Name;
			}
			return null;
		}
		
		public int getWinnerRegions() {
			return winner == Team.PLAYER_1 ? player1Regions : player2Regions;
		}
		
		public int getWinnerArmies() {
			return winner == Team.PLAYER_1 ? player1Armies : player2Armies;
		}

		public String asString() {
			return getWinnerId() + ";" + player1Regions + ";" + player1Armies + ";" + player2Regions + ";" + player2Armies + ";" + round;
		}
		
		public String getHumanString() {
			return "Winner: " + getWinnerId() + "[" + getWinnerName() + "] in round " + round + "\nPlayer1: " + player1Regions + " regions / " + player1Armies + " armies\nPlayer2: " + player2Regions + " regions / " + player2Armies + " armies";
		}
		
		public String getCSVHeader() {
			return "winnerName;winner;winnerId;player1Regions;player1Armies;player2Regions;player2Armies;round;" + config.getCSVHeader();
		}
		
		public String getCSV() {
			return getWinnerName() + ";" + (winner == null || winner == Team.NEUTRAL ? "NONE" : winner) + ";" + getWinnerId() + ";" + player1Regions + ";" + player1Armies + ";" + player2Regions + ";" + player2Armies + ";" + round + ";" + config.getCSV();
		}
		
	}
	
	Config config;
	
	int gameIndex = 1;

	Engine engine;
	ConquestGame game;
	
	public RunGame(Config config)
	{
		this.config = config;		
	}
	
	public GameResult goReplay(File replayFile) {
		try {
			System.out.println("starting replay " + replayFile.getAbsolutePath());
			
			ReplayHandler replay = new ReplayHandler(replayFile);
			
			this.config.engine = replay.getConfig().engine;
			
			PlayerInfo player1, player2;
			Robot robot1, robot2;
			
			//setup the bots: bot1, bot2
			robot1 = new IORobot(replay);
			robot2 = new IORobot(replay);
					
			player1 = new PlayerInfo(config.playerId1, config.player1Name, config.engine.startingArmies);
			player2 = new PlayerInfo(config.playerId2, config.player2Name, config.engine.startingArmies);
			
			return go(null, player1, player2, robot1, robot2);
		} catch (Exception e) {
			throw new RuntimeException("Failed to replay the game.", e);
		}
	}

	public GameResult go()
	{ 
		try {
			GameLog log = null;
			if (config.replayLog != null) {
				log = new FileGameLog(config.replayLog);
			}
			
			System.out.println("starting game " + config.gameId);
			
			PlayerInfo player1, player2;
			Robot robot1, robot2;
			
			//setup the bots: bot1, bot2
			robot1 = setupRobot(config.playerId1, config.bot1Init);
			robot2 = setupRobot(config.playerId2, config.bot2Init);
					
			player1 = new PlayerInfo(config.playerId1, config.player1Name, config.engine.startingArmies);
			player2 = new PlayerInfo(config.playerId2, config.player2Name, config.engine.startingArmies);
						
			return go(log, player1, player2, robot1, robot2);
		} catch (Exception e) {
			throw new RuntimeException("Failed to run/finish the game.", e);
		}
	}

	private GameResult go(GameLog log, PlayerInfo player1, PlayerInfo player2, Robot robot1, Robot robot2) throws InterruptedException {
		
		//setup the map
		GameMap initMap, map;
		
		initMap = makeInitMap();
		map = setupMap(initMap);

		// setup GUI
		GUI gui = null;
		if (config.visualize) {
			gui = new GUI(config.playerId1, config.playerId2, robot1.getRobotPlayerId(), robot2.getRobotPlayerId());
			if (config.visualizeContinual != null) {
				gui.setContinual(config.visualizeContinual);
			}
			if (config.visualizeContinualFrameTimeMillis != null) {
				gui.setContinualFrameTime(config.visualizeContinualFrameTimeMillis);
			}
		}
		
		//start the engine
		this.engine = new Engine(map, player1, player2, robot1, robot2, gui, config.engine);
		game = engine.game;
		
		if (log != null) {
			log.start(config);
		}
		
		// setup robots
		RobotConfig robot1Cfg =
			new RobotConfig(player1.getId(), player1.getName(), Team.PLAYER_1,
				            config.engine.botCommandTimeoutMillis, log, config.logToConsole, gui);
		
		RobotConfig robot2Cfg =
			new RobotConfig(player2.getId(), player2.getName(), Team.PLAYER_2,
					        config.engine.botCommandTimeoutMillis, log, config.logToConsole, gui);
				
		robot1.setup(robot1Cfg);
		robot2.setup(robot2Cfg);
		
		if (gui != null) {
			gui.setPlayerNames(robot1.getRobotPlayerName(), robot2.getRobotPlayerName());
		}		
				
		//send the bots the info they need to start
		robot1.writeInfo("settings your_bot " + player1.getId());
		robot1.writeInfo("settings opponent_bot " + player2.getId());
		robot2.writeInfo("settings your_bot " + player2.getId());
		robot2.writeInfo("settings opponent_bot " + player1.getId());
		sendSetupMapInfo(robot1, initMap);
		sendSetupMapInfo(robot2, initMap);
		this.engine.distributeStartingRegions(); //decide the player's starting regions
		this.engine.sendAllInfo();
		
		//play the game
		while(this.game.winningPlayer() == null && this.game.getRoundNr() <= config.engine.maxGameRounds)
		{
			if (log != null) {
				log.logComment("Engine", "Round " + this.game.getRoundNr());
			}
			this.engine.playRound();
		}

		GameResult result = finish(map, robot1, robot2);
		
		if (log != null) {
			log.finish(result);
		}
		
		return result;
	}

	private Robot setupRobot(String playerId, String botInit) throws IOException {
		if (botInit.startsWith("dir;process:")) {
			String cmd = botInit.substring(12);
			int semicolon = cmd.indexOf(";");
			if (semicolon < 0) throw new RuntimeException("Invalid bot torrent (does not contain ';' separating directory and commmend): " + botInit);
			String dir = cmd.substring(0, semicolon);
			String process = cmd.substring(semicolon+1);			
			return new ProcessRobot(playerId, dir, process);
		}
		if (botInit.startsWith("process:")) {
			String cmd = botInit.substring(8);
			return new ProcessRobot(playerId, cmd);
		}
		if (botInit.startsWith("internal:")) {
			String botFQCN = botInit.substring(9);
			return new InternalRobot(playerId, botFQCN);
		}
		if (botInit.startsWith("human")) {
			config.visualize = true;
			return new HumanRobot(playerId);
		}
		throw new RuntimeException("Invalid init string for player '" + playerId + "', must start either with 'process:' or 'internal:' or 'human', passed value was: " + botInit);
	}

	//aanpassen en een QPlayer class maken? met eigen finish
	private GameResult finish(GameMap map, Robot bot1, Robot bot2) throws InterruptedException
	{
		System.out.println("GAME FINISHED: stopping bots...");
		try {
			bot1.finish();
		} catch (Exception e) {			
		}
		
		try {
			bot2.finish();
		} catch (Exception e) {			
		}
		
		return this.saveGame(map, bot1, bot2);        
	}

	private GameMap makeInitMap()
	{
		GameMap map = new GameMap();
		
		// INIT SUPER REGIONS

		Map<Continent, ContinentData> continents = new TreeMap<Continent, ContinentData>(new Comparator<Continent>() {
			@Override
			public int compare(Continent o1, Continent o2) {
				return o1.id - o2.id;
			}			
		});
		
		for (Continent continent : Continent.values()) {
			ContinentData continentData = new ContinentData(continent, continent.id, continent.reward);
			continents.put(continent, continentData);
		}
		
		// INIT REGIONS
		
		Map<Region, RegionData> regions = new TreeMap<Region, RegionData>(new Comparator<Region>() {
			@Override
			public int compare(Region o1, Region o2) {
				return o1.id - o2.id;
			}
		});
		
		for (Region region : Region.values()) {
			RegionData regionData = new RegionData(region, region.id, continents.get(region.continent));
			regions.put(region, regionData);
		}
		
		// INIT NEIGHBOURS
		
		for (Region regionName : Region.values()) {
			RegionData region = regions.get(regionName);
			for (Region neighbour : regionName.getForwardNeighbours()) {
				region.addNeighbor(regions.get(neighbour));
			}
		}
		
		// ADD REGIONS TO THE MAP
		
		for (RegionData region : regions.values()) {
			map.add(region);
		}
		
		// ADD SUPER REGIONS TO THE MAP

		for (ContinentData superRegion : continents.values()) {
			map.add(superRegion);
		}

		return map;
	}
	
	//Make every region neutral with 2 armies to start with
	private GameMap setupMap(GameMap initMap)
	{
		GameMap map = initMap;
		for(RegionData region : map.regions)
		{
			region.setPlayerName("neutral");
			region.setArmies(2);
		}
		return map;
	}
	
	private void sendSetupMapInfo(Robot bot, GameMap initMap)
	{
		String setupSuperRegionsString, setupRegionsString, setupNeighborsString;
		setupSuperRegionsString = getSuperRegionsString(initMap);
		setupRegionsString = getRegionsString(initMap);
		setupNeighborsString = getNeighborsString(initMap);
		
		bot.writeInfo(setupSuperRegionsString);
		// System.out.println(setupSuperRegionsString);
		bot.writeInfo(setupRegionsString);
		// System.out.println(setupRegionsString);
		bot.writeInfo(setupNeighborsString);
		// System.out.println(setupNeighborsString);
	}
	
	private String getSuperRegionsString(GameMap map)
	{
		String superRegionsString = "setup_map super_regions";
		for(ContinentData superRegion : map.continents)
		{
			int id = superRegion.getId();
			int reward = superRegion.getArmiesReward();
			superRegionsString = superRegionsString.concat(" " + id + " " + reward);
		}
		return superRegionsString;
	}
	
	private String getRegionsString(GameMap map)
	{
		String regionsString = "setup_map regions";
		for(RegionData region : map.regions)
		{
			int id = region.getId();
			int superRegionId = region.getContinentData().getId();
			regionsString = regionsString.concat(" " + id + " " + superRegionId);
		}
		return regionsString;
	}
	
	//beetje inefficiente methode, maar kan niet sneller wss
	private String getNeighborsString(GameMap map)
	{
		String neighborsString = "setup_map neighbors";
		ArrayList<Point> doneList = new ArrayList<Point>();
		for(RegionData region : map.regions)
		{
			int id = region.getId();
			String neighbors = "";
			for(RegionData neighbor : region.getNeighbors())
			{
				if(checkDoneList(doneList, id, neighbor.getId()))
				{
					neighbors = neighbors.concat("," + neighbor.getId());
					doneList.add(new Point(id,neighbor.getId()));
				}
			}
			if(neighbors.length() != 0)
			{
				neighbors = neighbors.replaceFirst(","," ");
				neighborsString = neighborsString.concat(" " + id + neighbors);
			}
		}
		return neighborsString;
	}
	
	private Boolean checkDoneList(ArrayList<Point> doneList, int regionId, int neighborId)
	{
		for(Point p : doneList)
			if((p.x == regionId && p.y == neighborId) || (p.x == neighborId && p.y == regionId))
				return false;
		return true;
	}

	public GameResult saveGame(GameMap map, Robot bot1, Robot bot2) {

		GameResult result = new GameResult();
		
		result.config = config;
		
		for (RegionData region : map.regions) {
			if (region.ownedByPlayer(config.playerId1)) {
				++result.player1Regions;
				result.player1Armies += region.getArmies();
			}
			if (region.ownedByPlayer(config.playerId2)) {
				++result.player2Regions;
				result.player2Armies += region.getArmies();
			}
		}
		
		if (game.winningPlayer() != null) {
			if (config.playerId1.equals(game.winningPlayer().getId())) {
				result.winner = Team.PLAYER_1;
			} else
			if (config.playerId2.equals(game.winningPlayer().getId())) {
				result.winner = Team.PLAYER_2;
			}
		} else {
			result.winner = null;
		}
		
		result.round = game.getRoundNr()-1;
		
		System.out.println(result.getHumanString());
		
		return result;
	}
	
	public static void main(String args[]) throws Exception
	{	
		Config config = new Config();
		
		config.bot1Init = "internal:conquest.bot.BotStarter";
		//config.bot1Init = "human";
		config.bot2Init = "internal:conquest.bot.BotStarter";
		//config.bot2Init = "process:java -cp bin conquest.bot.BotStarter";
		//config.bot2Init = "dir;process:c:/my_bot/;java -cp bin conquest.bot.BotStarter";
		
		config.engine.botCommandTimeoutMillis = 24*60*60*1000;
		
		config.engine.maxGameRounds = 100;
		
		// visualize the map, if turned off, the simulation would run headless 
		config.visualize = true;
		
		config.replayLog = new File("./replay.log");
		
		RunGame run = new RunGame(config);
		GameResult result = run.go();
		
		System.exit(0);
	}
	

}
