package mr.emii.managers;

import mr.emii.models.KitModel;
import mr.emii.models.ManageModel;

import java.util.HashSet;
import java.util.Set;

public class KitsManager implements ManageModel {

    private Set<KitModel> kitList;

    public <Object extends KitModel> KitsManager() {
        this.kitList = new HashSet<>();
    }

    @Override
    public void addToList(Object model) {
        this.kitList.add((KitModel) model);
    }

    public Set<KitModel> getKitList() {
        return kitList;
    }

    public void removeKitByName(KitModel model){
        if(getKitList().contains(model)) getKitList().remove(model);
    }

    @Override
    public Object getByString(String name) {
        for (KitModel kit : this.kitList){
            if(kit.getName().equalsIgnoreCase(name)) return kit;
        }
        return null;
    }


}
