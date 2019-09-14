package mr.emii.models;

import java.util.HashSet;
import java.util.Set;

public abstract interface ManageModel {

    public void addToList(Object model);

    public Object getByString(String name);

}
