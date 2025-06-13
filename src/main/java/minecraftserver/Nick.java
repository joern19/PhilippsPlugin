package minecraftserver;

import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class Nick {
        
    PhilippsPlugin philippsPlugin = null;
    
    HashMap<Player, String> nicks = null;
    
    public Nick(PhilippsPlugin minecraftServer) {
        nicks = new HashMap<>();
        this.philippsPlugin = minecraftServer;
    }
    
    public void changeNick(Player p, String nick) {
        nicks.put(p, nick);
        p.setDisplayName(nick);
    }
    
    public void resetNick(Player p) {
        nicks.put(p, p.getName());
    }
    
    public void onChat(AsyncPlayerChatEvent e) {
        e.getPlayer().setDisplayName(nicks.get(e.getPlayer()));
    }
    
    public void onPlayerMove(PlayerJoinEvent e) {
        nicks.put(e.getPlayer(), e.getPlayer().getName());
    }
}
