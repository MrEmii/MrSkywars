package mr.emii.game;


import mr.emii.Main;
import mr.emii.models.PlayerModel;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;

public class GameManager {


	private Set<Game> gameList;

	public GameManager(){
		this.gameList = new HashSet<>();
	}

	public void addGame(Game game){
		if(!this.gameList.contains(game))
			this.gameList.add(game);
	}

	public void removeGame(Game game){
		if(this.gameList.contains(game))
			this.gameList.remove(game);
	}


	public Game getGameByName(String name){
		for(Game game : this.gameList){
			if(game.getName().equalsIgnoreCase(name)) return game;
		}
		return null;
	}

	public Game getGameByPlayerName(String name){
		PlayerModel player = (PlayerModel) Main.getInstance().getPlayerManager().getByString(name);
		for(Game game : this.gameList){
			if(game.getInGame().contains(player)){
				return  game;
			}
		}
		return null;
	}

	public Game getGameByBlockLocation(Location blockLocation){
		for(Game game : this.gameList){
			if(game.getSpawns().get(0).getWorld().getBlockAt(blockLocation) != null) return game;
		}
		return null;
	}

	public Set<Game> getGameList() {
		return gameList;
	}
}
