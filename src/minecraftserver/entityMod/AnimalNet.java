package minecraftserver.entityMod;

import java.util.ArrayList;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AnimalNet {

    //private HashMap<Integer, MyAnimal> animals;
    public AnimalNet() {
        //animals = new HashMap<>();
    }

    public void addNet(ItemStack is) {
        //nets.put(is, null);
    }

    /*private Integer createKey(HashMap<Integer, MyAnimal> map) {
        Integer res = 0;
        while (map.containsKey(res)) {
            res += 1;
        }
        return res;
    }*/
    public void rightClickAnimal(PlayerInteractEntityEvent e) {
        //System.out.println(animals.toString());
        /*if (e.getRightClicked() instanceof Player) {
            return;
        }
        ItemStack curItem = e.getPlayer().getInventory().getItemInMainHand();
        if (curItem.getType() == Material.SPAWNER) {
            if ((curItem.hasItemMeta() && (!curItem.getItemMeta().hasLore() || curItem.getItemMeta().getLore().isEmpty())) || !curItem.hasItemMeta()) {
                /*Integer key = createKey(animals);
                animals.put(key, new MyAnimal((e.getRightClicked())));*/

 /*ItemMeta im = curItem.getItemMeta();
                ArrayList<String> list = new ArrayList();
                list.add(e.getRightClicked().getType().toString());
                //list.add(key.toString());
                im.setLore(list);
                curItem.setItemMeta(im);

                e.getRightClicked().eject();
                e.getRightClicked().remove();
                e.setCancelled(true);
            }
        }*/
        
        //EntityModifier eM = new EntityModifier(entity, PhilippsPlugin.instance);
        //eM.modify().setCanDespawn(false).setCanPickUpLoot(false).setInvulnerable(true);
    }

    public void rightClickBlock(PlayerInteractEvent e) {
        ItemStack is = e.getPlayer().getInventory().getItemInMainHand();
        if (is.getType() == Material.SPAWNER) {
            e.setCancelled(true);
            if ((is.hasItemMeta() && (is.getItemMeta().hasLore() && !is.getItemMeta().getLore().isEmpty()))) {
                if (is.getItemMeta().getLore().size() == 1) {
                    /*Object lore = is.getItemMeta().getLore().get(0);
                    try {
                        EntityType entity = null;
                        for (EntityType et : EntityType.values()) {
                            if (et.name().equals(lore)) {
                                entity = et;
                            }
                        }
                        if (entity != null) {
                            Location loc = e.getClickedBlock().getLocation();
                            loc.setY(loc.getY() + 1);
                            loc.setX(loc.getBlockX() + 0.5);
                            loc.setZ(loc.getBlockZ() + 0.5);
                            e.getPlayer().getWorld().spawnEntity(loc, entity);
                            ItemMeta im = is.getItemMeta();
                            im.setLore(new ArrayList<>());
                            is.setItemMeta(im);
                        }
                    } catch (ClassCastException ex) {
                        e.getPlayer().sendMessage("Lore Error.");
                    }*/
 /*String sKey = is.getItemMeta().getLore().get(1);
                    Integer key;
                    try {
                        key = Integer.parseInt(sKey);
                    } catch (NumberFormatException ex) {
                        ex.printStackTrace();
                        return;
                    }
                    if (!animals.containsKey(key)) {
                        System.out.println(key + " not found..");
                        return;
                    } else {*/
                    Location loc = e.getClickedBlock().getLocation();

                    loc.setX(loc.getX() + 0.5); //just center it to the middle of the block..
                    loc.setZ(loc.getZ() + 0.5);

                    switch (e.getBlockFace().name()) {
                        case "DOWN":
                            loc.setY(loc.getY() - 2);
                            break;
                        case "UP":
                            loc.setY(loc.getY() + 1);
                            break;
                        case "EAST":
                            loc.setX(loc.getX() + 1);
                            break;
                        case "WEST":
                            loc.setX(loc.getX() - 1);
                            break;
                        case "NORTH":
                            loc.setZ(loc.getZ() - 1);
                            break;
                        case "SOUTH":
                            loc.setZ(loc.getZ() + 1);
                            break;
                    }

                    Object lore = is.getItemMeta().getLore().get(0);
                    try {
                        EntityType entity = null;
                        for (EntityType et : EntityType.values()) {
                            if (et.name().equals(lore)) {
                                entity = et;
                            }
                        }
                        if (entity != null) {
                            //Location loc = e.getClickedBlock().getLocation();
                            //loc.setY(loc.getY() + 1);
                            //loc.setX(loc.getBlockX() + 0.5);
                            //loc.setZ(loc.getBlockZ() + 0.5);
                            e.getPlayer().getWorld().spawnEntity(loc, entity);
                            ItemMeta im = is.getItemMeta();
                            im.setLore(new ArrayList<>());
                            is.setItemMeta(im);
                        }
                    } catch (ClassCastException ex) {
                        e.getPlayer().sendMessage("Lore Error.");
                    }

                    /*System.out.println("now spawn the animal..");
                        //animals.get(key).teleport(loc);
                        animals.get(key).spawn(loc);
                        animals.remove(key);
                        ItemMeta im = is.getItemMeta();
                        im.setLore(new ArrayList<>());
                        is.setItemMeta(im);*/
                    //}
                }
            }
        }
    }

    private void spawnEntity(Entity e) {
        net.minecraft.server.v1_15_R1.Entity nmsE = ((CraftEntity) e).getHandle();
        NBTTagCompound comp = new NBTTagCompound();
        nmsE.c(comp);
    }
}
