package experimental;

import net.minecraft.server.v1_15_R1.ChatComponentText;
import net.minecraft.server.v1_15_R1.ContainerEnchantTable;
import net.minecraft.server.v1_15_R1.EntityHuman;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.PacketPlayOutOpenWindow;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SimulateBlockInventorys {

    private static EntityPlayer entityPlayer;
    private static EnchantmentContainer container;
    
    public static void openEnchantingTable(Player p) {
        /*entityPlayer = ((CraftPlayer)p).getHandle();
        container = new EnchantmentContainer(entityPlayer);
        container.addSlotListener(entityPlayer);
        ItemStack is = new ItemStack(Items.LAPIS_LAZULI);
        for (Slot s : container.slots) {
            if (s.getItem().isEmpty()) {
                container.setItem(s.rawSlotIndex, is);
            }
        }
        System.out.println("Going to update...");
        entityPlayer.updateInventory(entityPlayer.defaultContainer);
        entityPlayer.updateInventory(container);
        System.out.println("Updated");
        System.out.println(container.getSlot(0).toString());*/
        p.closeInventory();
        Inventory inv = Bukkit.createInventory(p, InventoryType.ENCHANTING, "test");
        System.out.println(inv.getSize());
        inv.addItem(new ItemStack(Material.DIAMOND_SWORD));
        //inv.addItem(new ItemStack(Material.LAPIS_LAZULI, 64));
        p.openInventory(inv);
        //p.getOpenInventory().getTopInventory().addItem(new ItemStack(Material.LAPIS_LAZULI, 64));
    }
    
    public static void open() {
        int c = entityPlayer.nextContainerCounter();
        entityPlayer.playerConnection.sendPacket(new PacketPlayOutOpenWindow(c, container.getType(), new ChatComponentText("Enchanting")));
        entityPlayer.activeContainer = container;
        entityPlayer.activeContainer.addSlotListener(entityPlayer);
        entityPlayer.activeContainer.checkReachable = false;
    }
    
    private static class EnchantmentContainer extends ContainerEnchantTable {
        public EnchantmentContainer(EntityHuman entity) {
            //super(entity.inventory, entity.world, new BlockPosition(0,0,0));
            super(entityPlayer.nextContainerCounter(),entity.inventory);
        }
        @Override
        public boolean c(EntityHuman human) {
            return true;
        }
    }
    
}
