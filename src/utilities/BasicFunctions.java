package utilities;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.UUID;
import minecraftserver.gui.ClickableItem;
import net.minecraft.server.v1_15_R1.Containers;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class BasicFunctions {

    public static boolean isPlayerOnline(Player p) {
        return Bukkit.getOnlinePlayers().contains(p);
    }

    public static boolean isPlayerOnlineByName(String name) {
        return Bukkit.getOnlinePlayers().stream().anyMatch(player -> name.equals(player.getName()));
    }

    public static boolean isPlayerOnlineByDisplayName(String displayName) {
        return Bukkit.getOnlinePlayers().stream().anyMatch(player -> player.getDisplayName().equals(displayName));
    }

    public static boolean isPlayerOnlineByUUID(UUID uuid) {
        return Bukkit.getOnlinePlayers().stream().anyMatch(player -> player.getUniqueId().equals(uuid));
    }

    public static Player getPlayerByUUID(UUID uuid) {
        return Bukkit.getPlayer(uuid);
    }

    public static Player getPlayerByName(String name) {
        return Bukkit.getPlayer(name);
    }

    public static World getWorldbyName(String worldName) {
        return Bukkit.getWorld(worldName);
    }

    public static ItemStack setLore(ItemStack is, String lore) {
        ItemMeta im = is.getItemMeta();
        ArrayList<String> newLore = new ArrayList<>();
        newLore.add(lore);
        im.setLore(newLore);
        is.setItemMeta(im);
        return is;
    }

    /**
     * Null check if ItemMeta is included...
     *
     * @param e
     */
    public static boolean compareKey(ItemStack is1, ItemStack is2) {
        if (is1 == null || is2 == null) {
            return false;
        }
        if (!is1.hasItemMeta() || !is2.hasItemMeta()) { //nullcheck
            return false;
        }

        PersistentDataContainer pdc1 = is1.getItemMeta().getPersistentDataContainer();
        PersistentDataContainer pdc2 = is2.getItemMeta().getPersistentDataContainer();

        if (!pdc1.has(ClickableItem.getIdKey(), PersistentDataType.INTEGER) || !pdc2.has(ClickableItem.getIdKey(), PersistentDataType.INTEGER)) {
            return false;
        }
        Integer id = pdc1.get(ClickableItem.getIdKey(), PersistentDataType.INTEGER);
        return pdc2.get(ClickableItem.getIdKey(), PersistentDataType.INTEGER).equals(id);
    }

    /**
     *
     * @param s
     * @return null if string is not an integer
     */
    public static Integer tryParse(String s) {
        Integer i;
        try {
            i = Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            return null;
        }
        return i;
    }

    public static Integer getNeededRows(Integer itemCount) {
        int neededRows = itemCount / 9;
        if (itemCount % 9 != 0) {
            neededRows = ((int) itemCount / 9) + 1;
        }
        return neededRows;
    }

    private static Object getPrivateField(Object object, String field) throws SecurityException,
            NoSuchFieldException, IllegalArgumentException, IllegalAccessException {

        Class<?> clazz = object.getClass();
        Field objectField = clazz.getDeclaredField(field);
        objectField.setAccessible(true);
        Object result = objectField.get(object);
        objectField.setAccessible(false);
        return result;
    }

    public static Containers<?> getContainerType(int slots) {
        switch (getNeededRows(slots)) {
            case 1:
                return Containers.GENERIC_9X1;
            case 2:
                return Containers.GENERIC_9X2;
            case 3:
                return Containers.GENERIC_9X3;
            case 4:
                return Containers.GENERIC_9X4;
            case 5:
                return Containers.GENERIC_9X5;
            case 6:
                return Containers.GENERIC_9X6;
            default:
                System.err.println("Failed to get Containers Type for " + getNeededRows(slots) + " rows.");
                return null;
        }
    }

    public static void unRegisterBukkitCommand(String command) {
        /*PluginCommand cmd = PhilippsPlugin.instance.getCommand(command);
        try {
            Object result = getPrivateField(Bukkit.getServer().getPluginManager(), "commandMap");
            SimpleCommandMap commandMap = (SimpleCommandMap) result;
            Object map = getPrivateField(commandMap, "knownCommands");
            @SuppressWarnings("unchecked")
            HashMap<String, Command> knownCommands = (HashMap<String, Command>) map;
            knownCommands.remove(cmd.getName());
            cmd.getAliases().stream().filter((alias) -> (knownCommands.containsKey(alias))).filter((alias) -> (knownCommands.get(alias).toString().contains(PhilippsPlugin.instance.getName()))).forEachOrdered((alias) -> {
                knownCommands.remove(alias);
            });
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }*/
        //Currently doesnt work...
    }
}
