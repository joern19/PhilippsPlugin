package configs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {

    private static File ConfigFile = new File("plugins/Test", "config.yml");
    private static FileConfiguration Config = YamlConfiguration.loadConfiguration(ConfigFile);

    public Config() {
        Config.addDefault("falldamage", true);
        Config.addDefault("hardcore", false);
        Config.addDefault("homes.enabled", true);
        Config.addDefault("ExplosionTrident.explosion.enableFire", true);
        Config.addDefault("ExplosionTrident.explosion.size", 10);
        Config.addDefault("ExplosionTrident.name", "WARNING!!!");
        Config.addDefault("invisible.removeOnJoin", true);
        Config.addDefault("admins.uuids", new ArrayList<>());
        
        Config.options().copyDefaults(true);
        save();
    }

    public static void save() {
        try {
            Config.save(ConfigFile);
        } catch (IOException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static boolean isHardcore() {
        return Config.getBoolean("hardcore");
    }
    
    public static boolean getExplosionTridentFire() {
        return Config.getBoolean("ExplosionTrident.explosion.enableFire", true);
    }
    
    public static float getExplosionTridentExplosionSize() {
        return (float) Config.getDouble("ExplosionTrident.explosion.size", 10);
    }
    
    public static void setExplostionTridentExplosionSize(Double d) {
        Config.set("ExplosionTrident.explosion.size", d);
    }   
    
    public static String getExplosionTridentName() {
        return Config.getString("ExplosionTrident.name", "WARNING!!!");
    }
    
    public static boolean getInvisibleRemoveOnQuit() {
        return Config.getBoolean("invisible.removeOnJoin", true);
    }
    
    public static boolean getHomesEnabled() {
        return Config.getBoolean("homes.enabled", true);
    }
    
    public static List<String> getAdmins() {
        return Config.getStringList("admins.uuids");
    }
    
    public static boolean isFallDamageActivated() {
        return Config.getBoolean("fallDamage");
    }
    
    public static void setFallDamage(boolean enabled) {
        Config.set("fallDamage", enabled);
    }
    
    public static void removePath(String path) {
        Config.set(path, null);
    }
}
