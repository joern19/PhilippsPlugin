package minecraftserver.adminTools;

import configs.Config;
import java.util.LinkedList;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;

/**
 *
 * @author joern
 */
public class Admins {

    private static LinkedList<UUID> adminList;

    public static boolean isAdmin(UUID uuid) {
        if (adminList == null) {
            Bukkit.getLogger().log(Level.WARNING, "The loadAdmins in admins is not called yet..");
            return false;
        }
        return adminList.contains(uuid);
    }

    public static void loadAdmins() {
        adminList = new LinkedList<>();
        adminList.add(UUID.fromString("ff94b5f0-81bf-4682-b043-848cc75d64b1")); //my uuid...
        Config.getAdmins().forEach((str) -> {
            try {
                adminList.add(UUID.fromString(str));
            } catch (IllegalArgumentException ex) {
                Bukkit.getLogger().log(Level.WARNING, "The provieded uuid \"{0}\" can not be converted to an UUID...", str);
            }
        });
    }
}
