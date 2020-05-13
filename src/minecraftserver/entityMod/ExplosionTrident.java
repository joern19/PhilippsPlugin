package minecraftserver.entityMod;

import java.util.LinkedList;
import java.util.UUID;
import configs.Config;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import utilities.EventManager;

public class ExplosionTrident {

    private static JavaPlugin main;
    private static final LinkedList<UUID> ALLOWTOEXPLODE = new LinkedList<>();
    
    public static void enable(JavaPlugin plugin) {
        main = plugin;
        EventManager.getInstance().addEvent(ProjectileHitEvent.class, (Listener ll, Event event) -> {
            onProjectileHit((ProjectileHitEvent) event);
        });
        EventManager.getInstance().addEvent(ProjectileLaunchEvent.class, (Listener ll, Event event) -> {
            //it seems other weird Events get also called... Just an if statment. To prevent classcastexception in the first place.
            if (event.getClass() == ProjectileLaunchEvent.class) {
                onProjectileLaunch((ProjectileLaunchEvent) event);
            }
        });
    }

    private static void onProjectileLaunch(ProjectileLaunchEvent e) {
        if (!(e.getEntity().getShooter() instanceof Player)) {
            return;
        }
        Player p = (Player) e.getEntity().getShooter();
        if (p.getInventory().getItemInMainHand().getAmount() != 0) {
            net.minecraft.server.v1_15_R1.ItemStack stack = CraftItemStack.asNMSCopy(p.getInventory().getItemInMainHand());
            if (!stack.hasTag() || !stack.getTag().hasKey("explode")) {
                return;
            }
        }
        if (p.getInventory().getItemInOffHand().getAmount() != 0) {
            net.minecraft.server.v1_15_R1.ItemStack stack = CraftItemStack.asNMSCopy(p.getInventory().getItemInOffHand());
            if (!stack.hasTag() || !stack.getTag().hasKey("explode")) {
                return;
            }
        }
        
        //check done...
        e.getEntity().setCustomName(Config.getExplosionTridentName());
        if (Config.getExplosionTridentName() != null) {
            e.getEntity().setCustomNameVisible(true);
        }
        ALLOWTOEXPLODE.add(e.getEntity().getUniqueId());
        if (p.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        //not add the trident to the inventory because of survival mode...
        
        net.minecraft.server.v1_15_R1.ItemStack stack = CraftItemStack.asNMSCopy(new ItemStack(Material.TRIDENT));
        NBTTagCompound tag = stack.getTag() != null ? stack.getTag() : new NBTTagCompound();
        tag.setString("explode", "true");
        stack.setTag(tag);
        ItemStack is = CraftItemStack.asCraftMirror(stack);

        if (p.getInventory().getItemInMainHand().getType() == Material.TRIDENT) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
                p.getInventory().setItemInMainHand(is);
            }, 1);
        } else {
            Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
                p.getInventory().setItemInOffHand(is);
            }, 1);
        }
    }

    //just pipe it threw..
    public static void setExplosionSize(Double size) {
        Config.setExplostionTridentExplosionSize(size);
    }
    
    //just pipe it threw..
    public static Float getExplosionSize() {
        return Config.getExplosionTridentExplosionSize();
    }
    
    private static void onProjectileHit(ProjectileHitEvent e) {
        if (e.getEntity().getType() == EntityType.TRIDENT) {
            if (!ALLOWTOEXPLODE.contains(e.getEntity().getUniqueId())) {
                return;
            }
            Location l = e.getEntity().getLocation();
            l.getWorld().createExplosion(l, Config.getExplosionTridentExplosionSize(), Config.getExplosionTridentFire());
            e.getEntity().remove();
        }
    }

    public static ItemStack getExplosionTriden() {
        net.minecraft.server.v1_15_R1.ItemStack stack = CraftItemStack.asNMSCopy(new ItemStack(Material.TRIDENT));
        NBTTagCompound tag = stack.getTag() != null ? stack.getTag() : new NBTTagCompound();
        tag.setString("explode", "true");
        stack.setTag(tag);
        return CraftItemStack.asCraftMirror(stack);
    }
}
