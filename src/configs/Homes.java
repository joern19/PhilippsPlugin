package configs;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import utilities.BasicFunctions;

/**
 * Permissions: homes.<how many homes he is allowed to have>
 *
 * @author joern
 */
public class Homes {

    private static final File ConfigFile = new File("plugins/Test", "homes.yml");
    private static final FileConfiguration Config = YamlConfiguration.loadConfiguration(ConfigFile);

    public static void save() {
        try {
            Config.save(ConfigFile);
        } catch (IOException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static boolean isEnabled = false;

    public static void init() {
        if (configs.Config.getHomesEnabled()) {
            isEnabled = true;
            Config.options().copyDefaults(true);
            save();
        } else {
            BasicFunctions.unRegisterBukkitCommand("rmHome");
            BasicFunctions.unRegisterBukkitCommand("setHome");
            BasicFunctions.unRegisterBukkitCommand("home");
        }
    }

    /**
     *
     * @param sender
     * @param cmd
     * @param args
     * @return returns true if the function knew the command
     */
    public static boolean onCommand(CommandSender sender, Command cmd, String[] args) {
        if (!isEnabled) {
            return false;
        }
        if (!Arrays.stream(new String[]{"home", "sethome", "rmhome"}).anyMatch(cmd.getName()::equalsIgnoreCase)) {
            return false;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(LangFile.getOnlyForPlayer());
            return true;
        }

        Player p = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("sethome")) {
            switch (args.length) {
                case 0:
                    sethome(p, "1");
                    break;
                case 1:
                    sethome(p, args[0]);
                    break;
                default:
                    p.sendMessage(LangFile.getSetHomeInfo());
                    break;
            }
        } else if (cmd.getName().equalsIgnoreCase("home")) {
            switch (args.length) {
                case 0:
                    toHome(p, "1");
                    break;
                case 1:
                    toHome(p, args[0]);
                    break;
                default:
                    p.sendMessage(LangFile.getTpToHomeInfo());
                    break;
            }
        } else {
            if (args.length == 1) {
                rmHome(p, args[0]);
            } else {
                p.sendMessage(LangFile.getHomeDeleteInfo());
            }
        }
        return true;
    }

    private static void rmHome(Player p, String homeNumber) {
        Integer parsed = BasicFunctions.tryParse(homeNumber);
        if (parsed == null) {
            p.sendMessage(LangFile.getNotAnInteger().replace("{text}", "homeNumber"));
            return;
        }
        if (parsed > 0 && parsed <= 5) {
            Config.set(p.getName() + ".home" + homeNumber, null);
            save();
            p.sendMessage(LangFile.getHomeDeleted().replace("{homeNumber}", homeNumber));
        } else {
            p.sendMessage(LangFile.getSetHomeInfo());
        }
    }

    private static void toHome(Player p, String homeNumber) {
        Integer parsed = BasicFunctions.tryParse(homeNumber);
        if (parsed == null) {
            p.sendMessage(LangFile.getNotAnInteger().replace("{text}", "homeNumber"));
            return;
        }
        if (parsed > 0 && parsed <= 5) {

            if (Config.getString(p.getName() + ".home" + homeNumber) != null) {
                tptohome(homeNumber, p);
            } else {
                p.sendMessage(LangFile.getHomeNotSet().replace("{homeNumber}", homeNumber));
            }
        } else {
            p.sendMessage(LangFile.getSetHomeInfo());
        }
    }

    private static void sethome(Player p, String homeNumber) {
        Integer parsed = BasicFunctions.tryParse(homeNumber);
        if (parsed == null) {
            p.sendMessage(LangFile.getNotAnInteger().replace("{text}", "homeNumber"));
            return;
        }
        if (parsed > 0 && parsed <= 5) {
            Location loc = p.getLocation();
            Config.set(p.getName() + ".home" + homeNumber + ".bol", "TRUE");
            Config.set(p.getName() + ".home" + homeNumber + ".world", loc.getWorld().getName());
            Config.set(p.getName() + ".home" + homeNumber + ".x", loc.getX());
            Config.set(p.getName() + ".home" + homeNumber + ".y", loc.getY());
            Config.set(p.getName() + ".home" + homeNumber + ".z", loc.getZ());
            save();
            p.sendMessage(LangFile.getHomeSet().replace("{homeNumber}", homeNumber));
        } else {
            p.sendMessage(LangFile.getSetHomeInfo());
        }
    }

    private static void tptohome(String homenumber, Player p) {
        if (Config.getString(p.getName() + ".home" + homenumber) != null) {
            World w2 = BasicFunctions.getWorldbyName(Config.getString(p.getName() + ".home" + homenumber + ".world"));
            Double x2 = Config.getDouble(p.getName() + ".home" + homenumber + ".x");
            Double y2 = Config.getDouble(p.getName() + ".home" + homenumber + ".y");
            Double z2 = Config.getDouble(p.getName() + ".home" + homenumber + ".z");
            Location loc2 = new Location(w2, x2, y2, z2);
            p.teleport(loc2);
            p.sendMessage(LangFile.getTeleported());
        } else {
            p.sendMessage(LangFile.getHomeNotSet().replace("{homeNumber}", homenumber));
        }
    }

    public static HashMap<Integer, Location> getHomes(String playerName) {
        HashMap<Integer, Location> res = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            if (Config.getString(playerName + ".home" + i) != null) {
                World w = BasicFunctions.getWorldbyName(Config.getString(playerName + ".home1.world"));
                Double x = Config.getDouble(playerName + ".home" + i + ".x");
                Double y = Config.getDouble(playerName + ".home" + i + ".y");
                Double z = Config.getDouble(playerName + ".home" + i + ".z");
                Location loc = new Location(w, x, y, z);
                res.put(i, loc);
            }
        }
        return res;
    }

}
