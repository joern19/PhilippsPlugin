package experimental;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.logging.Level;
import minecraftserver.PhilippsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignInput {

    private static SignInput instance = null;

    //private final HashMap<Block, Interface> signsWaiting = new HashMap<>();

    private SignInput() {
    }

    public static synchronized SignInput getInstance() {
        if (instance == null) {
            instance = new SignInput();
            System.out.println("Event Registerd...");
            Bukkit.getPluginManager().registerEvent(SignChangeEvent.class, PhilippsPlugin.instance, EventPriority.LOW, (Listener ll, Event event) -> {
                instance.onSignChange((SignChangeEvent) event);
            }, PhilippsPlugin.instance, true);
            /*EventManager.getInstance().addEvent(SignChangeEvent.class, (Listener ll, Event event) -> {
                inctance.onSignChange((SignChangeEvent) event);
            });*/// BlockDamageEvent dont destroy my sign...
        }
        return instance;
    }

    private void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server."
                    + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void onSignChange(SignChangeEvent e) {
        Bukkit.getLogger().log(Level.INFO, "Event called");
        //if (!signsWaiting.containsKey(e.getBlock())) {
        //    return;
        //}
        //new Thread(() -> signsWaiting.get(e.getBlock()).afterSignChanged(e.getPlayer(), e.getBlock())).start();
        //signsWaiting.remove(e.getBlock());
        //e.getBlock().setType(Material.AIR); //what was it before?
        //Location tmp = e.getBlock().getLocation();
        //tmp.setY(e.getBlock().getLocation().getBlockY() - 1);
        //tmp.getBlock().setType(Material.AIR);

        Bukkit.getLogger().log(Level.INFO, "SignChangeEvent called. Found following Lines:");
        for (String lines : e.getLines()) {
            Bukkit.getLogger().log(Level.INFO, lines);
        }
    }

    public void openSign(Player p, String firstLine, Interface afterChange) {
        /*Location signLoc = new Location(p.getWorld(), 0, 1, 0);
        Location underSign = signLoc.clone();
        underSign.setY(0);
        underSign.getBlock().setType(Material.BEDROCK);
        signLoc.getBlock().setType(Material.OAK_SIGN);
        Sign s = (Sign) signLoc.getBlock().getState();
        s.setEditable(true);
        s.setLine(0, firstLine);
        s.setLine(1, "");
        s.setLine(2, "");
        s.setLine(3, "");
        s.update();
        Bukkit.getScheduler().scheduleSyncDelayedTask(PhilippsPlugin.instance, new Runnable() {
            @Override
            public void run() {
                try {
                    Class<?> packetClass = getNMSClass("PacketPlayOutOpenSignEditor");
                    Class<?> blockPositionClass = getNMSClass("BlockPosition");
                    Constructor<?> blockPosCon = blockPositionClass.getConstructor(new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE});

                    Object blockPosition = blockPosCon.newInstance(new Object[]{signLoc.getBlockX(), signLoc.getBlockY(), signLoc.getBlockZ()});
                    Constructor<?> packetCon = packetClass.getConstructor(new Class[]{blockPositionClass});

                    Object packet = packetCon.newInstance(new Object[]{blockPosition});
                    sendPacket(p, packet);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, 1);*/
        //signsWaiting.put(signLoc.getBlock(), afterChange);
    }

    public interface Interface {

        void afterSignChanged(Player p, Block b);
    }

    public static Interface getSampleFunction() {
        Interface myInterface = ((p, b) -> {
            Sign s = (Sign) b.getState();
            p.sendMessage("line1: " + s.getLine(0));
            p.sendMessage("line2: " + s.getLine(1));
            p.sendMessage("line3: " + s.getLine(2));
            p.sendMessage("line4: " + s.getLine(3));
        });
        return myInterface;
    }
}
