package minecraftserver;

import java.lang.reflect.Field;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.IChatBaseComponent;
import net.minecraft.server.v1_15_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_15_R1.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_15_R1.PlayerConnection;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class MyTablist {

    public static void set(Player p, String abovelist, String underlist) {
        EntityPlayer pl = (((CraftPlayer) p).getHandle());
        PlayerConnection c = pl.playerConnection;
        IChatBaseComponent header = ChatSerializer.a("{'text': '" + abovelist + "'}");
        IChatBaseComponent msg = ChatSerializer.a("{'text': '" + underlist + "'}");
        PacketPlayOutPlayerListHeaderFooter l = new PacketPlayOutPlayerListHeaderFooter();
        l.header = header;
        c.sendPacket(l);

        try {
            Field field = l.getClass().getDeclaredField("b");
            field.setAccessible(true);
            field.set(l, msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            c.sendPacket(l);
        }
    }
}
