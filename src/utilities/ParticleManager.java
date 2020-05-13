/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import minecraftserver.PhilippsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

/**
 *
 * @author joern
 */
public class ParticleManager {

    private static final HashMap<Player, List<Integer>> TRAILS = new HashMap<>();

    public static void addTrail(Player p, Particle particle, int waitUntilSpawnNext) {
        int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(PhilippsPlugin.instance, () -> {
            Location loc = p.getLocation();
            loc.setY(loc.getY() + 0.5);
            p.getWorld().spawnParticle(particle, loc, 1); //1 the count
        }, 0, waitUntilSpawnNext);

        List list;
        if (!TRAILS.containsKey(p)) {
            list = new ArrayList<>();
        } else {
            list = TRAILS.get(p);
        }
        list.add(id);
        TRAILS.put(p, list);
    }

    public static int removeTrials(Player p) {
        List<Integer> ids = TRAILS.remove(p);
        if (ids == null) {
            return 0;
        }
        int counter = 0;
        for (Integer i : ids) {
            counter++;
            Bukkit.getScheduler().cancelTask(i);
        }
        return counter;
    }

    /*public static void sendParticleOnlyToOnePlayer(Player p, Location loc, Particle particleType) {
        ParticleParam pp = Particle.REGISTRY.get();
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(pp, true, (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), (float) 0,(float) 0,(float) 0, (float) 1, 0);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
    }*/
    
}
