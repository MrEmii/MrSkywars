package mr.emii.managers;

import mr.emii.models.ManageModel;
import mr.emii.models.PlayerModel;

import java.util.HashSet;
import java.util.Set;

public class PlayerManager implements ManageModel {

    private Set<PlayerModel> playersList;

    public <Object extends PlayerModel> PlayerManager() {
        playersList = new HashSet<>();
    }

    @Override
    public void addToList(Object model) {
        if(!this.playersList.contains(model)) this.playersList.add((PlayerModel) model);
    }

    @Override
    public Object getByString(String name) {
        for (PlayerModel players : this.playersList){
            if(players.getPlayer().getDisplayName().equalsIgnoreCase(name)) return players;
        }
        return null;
    }

    public Set<PlayerModel> getPlayersList() {
        return playersList;
    }
}
