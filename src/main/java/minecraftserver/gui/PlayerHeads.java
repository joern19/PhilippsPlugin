package minecraftserver.gui;

import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import utilities.EventManager;

public class PlayerHeads {

    private static PlayerHeads instance;

    private PlayerHeads() {
    }

    public static synchronized PlayerHeads getInstance() {
        if (PlayerHeads.instance == null) {
            PlayerHeads.instance = new PlayerHeads();
            EventManager.getInstance().addEvent(PlayerQuitEvent.class, (ll, event) -> {
                instance.onPlayerQuit((PlayerQuitEvent) event);
            });
            EventManager.getInstance().addEvent(PlayerJoinEvent.class, (ll, event) -> {
                instance.onPlayerJoin((PlayerJoinEvent) event);
            });
        }
        return PlayerHeads.instance;
    }

    private HashMap<UUID, ItemStack> heads = new HashMap<>();

    public ItemStack getHead(Player p) {
        if (!heads.containsKey(p.getUniqueId())) {
            System.out.println("New Head recognised");
            ItemStack playerhead = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta playerheadmeta = (SkullMeta) playerhead.getItemMeta();
            if (playerheadmeta != null) {
                playerheadmeta.setOwningPlayer(Bukkit.getOfflinePlayer(p.getUniqueId()));
                playerheadmeta.setDisplayName(p.getName());
            }
            playerhead.setItemMeta(playerheadmeta);

            heads.put(p.getUniqueId(), playerhead);
        }
        return heads.get(p.getUniqueId());
    }

    private void onPlayerQuit(PlayerQuitEvent e) {
        heads.remove(e.getPlayer().getUniqueId());
    }

    private void onPlayerJoin(PlayerJoinEvent e) {
        getHead(e.getPlayer());
    }
}
