package mr.emii.utils;

import mr.emii.Main;
import mr.emii.models.KitModel;

public class KitFileUtils {

    public static FileUtils createKitModelFile(KitModel kit) {
        FileUtils m_kit = new FileUtils(kit.getName(), Main.getInstance().getDataFolder() + "/kits");
        if (m_kit.createFile()) {
            m_kit.set("name", kit.getName());
            m_kit.set("price", kit.getPrice());
            m_kit.createSection("items");
            System.out.println(kit.getItems());
            for (int i = 0; i < kit.getItems().size(); i++) {
                System.out.println(i);
                if (kit.getItems().get(i) != null) m_kit.createSection("items." + i, kit.getItems().get(i).serialize());
            }
            m_kit.save();
        }
        return m_kit;
    }
    public static FileUtils updateKitFile(KitModel kit) {
        FileUtils m_kit = new FileUtils(kit.getName(), Main.getInstance().getDataFolder() + "/kits");
        if (!m_kit.createFile()) {
            m_kit.set("name", kit.getName());
            m_kit.set("price", kit.getPrice());
            m_kit.createSection("items");
            System.out.println(kit.getItems());
            for (int i = 0; i < kit.getItems().size(); i++) {
                System.out.println(i);
                if (kit.getItems().get(i) != null) m_kit.createSection("items." + i, kit.getItems().get(i).serialize());
            }
            m_kit.save();
            return m_kit;
        }else{
            return createKitModelFile(kit);
        }
    }
}
