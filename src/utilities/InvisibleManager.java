package utilities;

import java.util.ArrayList;
import java.util.UUID;
import configs.Config;
import net.minecraft.server.v1_15_R1.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This Class Works with UUIDs..
 *
 * @author joern
 */
public class InvisibleManager {

    private static final ArrayList<UUID> INVISIBLES = new ArrayList(); //stores the uuids
    private static JavaPlugin plugin;

    /**
     * Has to be called before any other Function in this class. I Recommend to
     * call it in the onEnable Function.
     *
     * The Constructor of EventManager has to be called First..
     *
     * @param main the Class where you wrote "extends JavaPlugin"
     */
    public static void init(JavaPlugin main) {
        plugin = main;
        EventManager.getInstance().addEvent(PlayerJoinEvent.class, (Listener ll, Event event) -> {
            onPlayerJoin((PlayerJoinEvent) event);
        });
        EventManager.getInstance().addEvent(TabCompleteEvent.class, (Listener ll, Event event) -> {
            onTabComplete((TabCompleteEvent) event);
        });
        if (Config.getInvisibleRemoveOnQuit()) {
            EventManager.getInstance().addEvent(PlayerQuitEvent.class, (Listener ll, Event event) -> {
                onPlayerQuit((PlayerQuitEvent) event);
            });
        }
    }

    public static void onTabComplete(TabCompleteEvent e) {
        INVISIBLES.forEach((uuid) -> {
            e.getCompletions().remove(Bukkit.getOfflinePlayer(uuid).getName());
        });
    }
    
    public static void onPlayerJoin(PlayerJoinEvent e) {
        for (UUID invisibleUUID : INVISIBLES) {
            Player invisible = BasicFunctions.getPlayerByUUID(invisibleUUID);
            if (invisible == null) {
                INVISIBLES.remove(invisibleUUID);
                continue;
            }
            PacketPlayOutPlayerInfo removePacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) invisible).getHandle());
            ((CraftPlayer) e.getPlayer()).getHandle().playerConnection.sendPacket(removePacket);
            e.getPlayer().hidePlayer(plugin, invisible);
        }
    }

    public static void onPlayerQuit(PlayerQuitEvent e) {
        if (INVISIBLES.contains(e.getPlayer().getUniqueId())) {
            INVISIBLES.remove(e.getPlayer().getUniqueId());
        }
    }

    /**
     * Also checks if the Player is currently online.
     *
     * @param uuid
     * @return self explaining
     */
    public static boolean isInvisible(UUID uuid) {
        if (!BasicFunctions.isPlayerOnlineByUUID(uuid)) {
            INVISIBLES.remove(uuid);
        }
        return INVISIBLES.contains(uuid);
    }

    /**
     * Warning: Automaticly checks is uuid is online or invisible
     *
     * @param uuid
     * @return the value if the action was success ful
     */
    public static boolean removeInvisible(UUID uuid) {
        if (!isInvisible(uuid)) {
            return false;
        }
        boolean res = INVISIBLES.remove(uuid);
        if (res) {
            Player invisible = BasicFunctions.getPlayerByUUID(uuid);
            PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer) invisible).getHandle());
            Bukkit.getOnlinePlayers().stream().forEach((all) -> {
                ((CraftPlayer) all).getHandle().playerConnection.sendPacket(packet);
                all.showPlayer(plugin, invisible);
            });
        }
        return res;
    }

    public static boolean addInvisible(UUID uuid) {
        if (INVISIBLES.contains(uuid) || BasicFunctions.isPlayerOnlineByUUID(uuid)) {
            return false;
        }
        Player invisible = BasicFunctions.getPlayerByUUID(uuid);
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) invisible).getHandle());
        Bukkit.getOnlinePlayers().stream().forEach((all) -> {
            ((CraftPlayer) all).getHandle().playerConnection.sendPacket(packet);
            all.hidePlayer(plugin, invisible);
        });
        return true;
    }
}
