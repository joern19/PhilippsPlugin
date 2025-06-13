package configs;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * This is The configuration File where all Respond Messages are saved.
 * @author joern
 */
public class LangFile {

    private static File ConfigFile = new File("plugins/Test", "language.yml");
    private static FileConfiguration Config = YamlConfiguration.loadConfiguration(ConfigFile);
    
    private static void save() {
        try {
            Config.save(ConfigFile);
        } catch (IOException ex) {
            Logger.getLogger(LangFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public LangFile() {
        
        Config.addDefault("setHomeInfo", "§cPlease use /sethome <1/2/3/4/5>");
        Config.addDefault("rmHomeInfo", "§cPlease use /rmhome <1/2/3/4/5>");
        Config.addDefault("tpToHomeInfo", "§cPlease use /home <1/2/3/4/5>");
        Config.addDefault("homeNotSet", "§cHome {homeNumber} not set.");
        Config.addDefault("onlyPlayers", "§cOnly Player can do this.");
        Config.addDefault("teleported", "§aYou were Teleported.");
        Config.addDefault("notAnInteger", "§c'{text}'Is not an Integer.");
        Config.addDefault("homeDeleted", "§cHome {homeNumber} deleted.");
        Config.addDefault("homeSet", "§aHome {homeNumber} set.");
        
        Config.options().copyDefaults(true);
        save();
    }

    public static String getSetHomeInfo() {
        return Config.getString("setHomeInfo");
    }
    
    public static String getTpToHomeInfo() {
        return Config.getString("tpToHomeInfo");
    }
    
    public static String getOnlyForPlayer() {
        return Config.getString("onlyPlayers");
    }
    
    public static String getTeleported() {
        return Config.getString("teleported");
    }
    
    public static String getHomeNotSet() {
        return Config.getString("homeNotSet");
    }
    
    public static String getNotAnInteger() {
        return Config.getString("notAnInteger");
    }
    
    public static String getHomeDeleted() {
        return Config.getString("homeDeleted");
    }
    
    public static String getHomeSet() {
        return Config.getString("homeSet");
    }
    
    public static String getHomeDeleteInfo() {
        return Config.getString("rmHomeInfo");
    }
    
}
