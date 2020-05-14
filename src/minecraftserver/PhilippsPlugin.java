package minecraftserver;

import utilities.Update;
import configs.Homes;
import configs.Config;
import configs.LangFile;
import minecraftserver.adminTools.MaineVorteile;
import minecraftserver.entityMod.AnimalNet;
import java.util.ArrayList;
import java.util.HashMap;
import minecraftserver.adminTools.Admins;
import minecraftserver.adminTools.CommandManager;
import minecraftserver.entityMod.ExplosionTrident;
import minecraftserver.gui.AdminGUI;
import minecraftserver.gui.PlayerHeads;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import utilities.EventManager;

public class PhilippsPlugin extends JavaPlugin implements Listener {

    public final ConsoleCommandSender console = getServer().getConsoleSender();

    private static final String PLUGIN_TITLE = "[Philipps Plugin]";

    MaineVorteile maineVorteile = null;
    //Tablist tablist = null;
    Update update = null;
    Nick nick = null;
    AnimalNet animalNet = null;
    public static PhilippsPlugin instance = null;
    NamespacedKey animalNetKey = null;
    CommandManager cm = null;

    ArrayList<String> blcklist = null;
    ArrayList<String> freezes = null;
    HashMap<Player, Inventory> invs = null;

    @EventHandler
    public static void FurnaceSmeltEvent(FurnaceSmeltEvent e) {
        if (e.getSource().getType() == Material.DRIED_KELP) {
            if (e.getSource().getAmount() < 9) {
                e.setCancelled(true);
            } else {
                e.getSource().setAmount(e.getSource().getAmount() - 8);
            }
        }
        if (e.getSource().getType() == Material.WHEAT) {
            if (e.getSource().getAmount() < 3) {
                e.setCancelled(true);
            } else {
                e.getSource().setAmount(e.getSource().getAmount() - 2);
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        if (e.isCancelled()) {
            return;
        }
        //maineVorteile.onInventoryOpen(e);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (Config.isHardcore()) {
            Bukkit.getOnlinePlayers().forEach((p) -> {
                p.kickPlayer(((Player) e.getEntity​()).getName() + " died...");
            });
            Bukkit.getServer().shutdown();
        }
    }

    @Override
    public void onEnable() {
        instance = this;
        new EventManager(this);

        getCommand("hi").setTabCompleter(new CustomTabCompleter());
        getServer().getPluginManager().registerEvents(this, this);

        invs = new HashMap<>();

        nick = new Nick(this);

        animalNet = new AnimalNet();
        ItemStack item = new ItemStack(Material.SPAWNER);
        ItemMeta im = item.getItemMeta();
        im.setDisplayName("Animal Net");
        item.setItemMeta(im);
        animalNetKey = new NamespacedKey(this, "tier_netz");
        ShapedRecipe recipe = new ShapedRecipe(animalNetKey, item);
        recipe.shape(" AA", " BA", "B  ");
        recipe.setIngredient('A', Material.STRING);
        recipe.setIngredient('B', Material.STICK);

        Bukkit.addRecipe(recipe);

        freezes = new ArrayList();
        freezes.add("");

        blcklist = new ArrayList<>();
        blcklist.add("donaldtrump");
        blcklist.add("muschi");
        blcklist.add("dumm");
        blcklist.add("hidler");
        blcklist.add("jörn");
        blcklist.add("joern");
        blcklist.add("craftphil");

        update = new Update(this);
        maineVorteile = new MaineVorteile(this, update);
        //tablist = new Tablist(this);
        new Config();
        new LangFile();

        Admins.loadAdmins();
        
        getConfig().options().copyDefaults(true);
        AdminGUI.reload();
        //InvisableToSomeone.makeServerInvisibleTo(new String[]{"joern19"});

        cm = new CommandManager(maineVorteile, this);

        PlayerHeads.getInstance(); //so the events get registerd...
        ExplosionTrident.enable(this);
        Homes.init(); //IMPORTANT: also call init if it is disbled. It will unregister some commands.

        Bukkit.getServer().getOnlinePlayers().stream().filter((p) -> (p.getName().equals("joern19"))).forEach((p) -> {
            maineVorteile.Ich = p;
            p.sendMessage("hi joern");
            //ParticleManager.addTrail(p, Particle.VILLAGER_ANGRY, 2);
        });
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        boolean res = cm.onCommand(sender, cmd, args);
        if (!Homes.onCommand(sender, cmd, args)) {
            res = false;
        }
        return res;
        /*boolean result = false;
        if (!maineVorteile.onCommand(sender, cmd, args)) {
            if (!Config.onCommand(sender, cmd, label, args)) {
                //todo commands!
                result = true;
                if (cmd.getName().equalsIgnoreCase("workbench")) {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        p.openWorkbench(p.getLocation(), true);
                    } else {
                        sender.sendMessage("Das kann nur ein Spieler");
                    }
                } else if (cmd.getName().equals("hit")) {
                    if (sender.isOp()) {
                        if (args.length == 1) {
                            if (BasicFunctions.isPlayerOnlineByName(args[0])) {
                                HitPlayer(BasicFunctions.getPlayerByName(args[0]));
                            } else {
                                sender.sendMessage("diesen Spieler gibt es nicht!");
                            }
                        } else if (args.length == 2) {
                            if (BasicFunctions.isPlayerOnlineByName(args[0])) {
                                if (args[1].equalsIgnoreCase("true")) {
                                    double Leben = BasicFunctions.getPlayerByName(args[0]).getHealth();
                                    Leben--;
                                    BasicFunctions.getPlayerByName(args[0]).damage(Leben);
                                } else if (args[1].equalsIgnoreCase("false")) {
                                    BasicFunctions.getPlayerByName(args[0]).damage(1);
                                }
                            } else {
                                sender.sendMessage("diesen Spieler gibt es nicht!");
                            }
                        } else {
                            sender.sendMessage("§c/hit <Spieler> <fast töten(true false)>");
                        }
                    } else {
                        sender.sendMessage("§cDas darfst du nicht!");
                    }
                } else if (cmd.getName().equalsIgnoreCase("inv")) {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        if (p.hasPermission("lol")) {
                            if (args.length == 1) {
                                if (BasicFunctions.isPlayerOnlineByName(args[0])) {
                                    p.openInventory(BasicFunctions.getPlayerByName(args[0]).getInventory());
                                } else {
                                    sender.sendMessage("§cdieser spieler existiert nicht!");
                                }
                            } else {
                                sender.sendMessage("/inv <Player>");
                            }
                        }
                    }
                } else if (cmd.getName().equalsIgnoreCase("enderinv")) {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        if (p.hasPermission("lol")) {
                            if (args.length == 1) {
                                if (BasicFunctions.isPlayerOnlineByName(args[0])) {
                                    p.openInventory(BasicFunctions.getPlayerByName(args[0]).getEnderChest());
                                } else {
                                    sender.sendMessage("§cdieser spieler existiert nicht!");
                                }
                            } else {
                                sender.sendMessage("/enderinv <Player>");
                            }
                        }
                    }
                } else if (cmd.getName().equalsIgnoreCase("chestinv")) {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        if (p.hasPermission("lol")) {
                            if (args.length == 1) {
                                if (BasicFunctions.isPlayerOnlineByName(args[0])) {
                                    Player p2 = BasicFunctions.getPlayerByName(args[0]);
                                    if (p2.getOpenInventory() != null) {
                                        p.openInventory(p.getOpenInventory().getTopInventory());
                                    } else {
                                        sender.sendMessage("Dieser Spieler hat kein Inventar geöffnet");
                                    }
                                } else {
                                    sender.sendMessage("§cdieser spieler existiert nicht!");
                                }
                            } else {
                                sender.sendMessage("/chestinv <Player>");
                            }
                        }
                    }
                } else if (cmd.getName().equalsIgnoreCase("changeName")) {
                    if (sender.hasPermission("lol")) {
                        if (args.length == 2) {
                            if (BasicFunctions.isPlayerOnlineByName(args[0])) {
                                Player p = BasicFunctions.getPlayerByName(args[0]);
                                p.setDisplayName(args[1]);
                                sender.sendMessage("changed");
                            } else {
                                sender.sendMessage("Dieser Spieler ist nicht Online");
                            }
                        } else {
                            sender.sendMessage("/changeName <SplielerName> <NeuerName>");
                        }
                    }
                } else if (cmd.getName().equalsIgnoreCase("rucksack")) {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        if (invs.containsKey(p)) {
                            p.openInventory(invs.get(p));
                        } else {
                            invs.put(p, Bukkit.createInventory(p, InventoryType.CHEST));
                            p.openInventory(invs.get(p));
                        }
                    }
                } else if (cmd.getName().equalsIgnoreCase("enderchest")) {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        p.openInventory(p.getEnderChest());
                    }
                } else if (cmd.getName().equalsIgnoreCase("nick")) {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        if (args.length == 0) {
                            nick.resetNick(p);
                        } else if (args.length == 1) {
                            if (args[0].length() < 11) {
                                if (!contains(args[0])) {
                                    nick.changeNick(p, args[0]);
                                } else {
                                    p.kickPlayer("Unangebrachter Name!!!");
                                }
                            } else {
                                sender.sendMessage("Name zu Lang");
                            }
                        } else {
                            sender.sendMessage("resetName: /nick");
                            sender.sendMessage("setName: /nick <Name>");
                        }
                    }
                } else if (cmd.getName().equalsIgnoreCase("freeze")) {
                    if (args.length == 1) {
                        if (args[0].equalsIgnoreCase("list")) {
                            for (String p : freezes) {
                                sender.sendMessage(p);
                            }
                        } else if (BasicFunctions.isPlayerOnlineByName(args[0])) {
                            Player p = BasicFunctions.getPlayerByName(args[0]);
                            if (freeze(p)) {
                                sender.sendMessage("frezzed");
                            } else {
                                sender.sendMessage("ent-frezzed");
                            }
                        } else {
                            sender.sendMessage("§cPlayer not found");
                        }
                    } else {
                        sender.sendMessage("/freeze <PlayerName>");
                    }
                } else if (cmd.getName().equalsIgnoreCase("msg")) {
                    if (args.length >= 2) {
                        if (BasicFunctions.isPlayerOnlineByName(args[1])) {
                            Player p = BasicFunctions.getPlayerByName(args[1]);
                            p.sendMessage(sender.getName() + " --> Dir: " + stringListToString(args));
                        } else {
                            sender.sendMessage("Dieser Spieler existiert nicht");
                        }
                    } else {
                        sender.sendMessage("/msg <Player> <Message>");
                    }
                }
            }
        } else {
            result = true;
        }
        return result;*/
    }

    public boolean freeze(Player p) { //true if freezed, false if unfreezed
        if (!freezes.contains(p.getName())) {
            freezes.add(p.getName());
            return true;
        } else {
            freezes.remove(p.getName());
            return false;
        }
    }

    private String stringListToString(String[] list) {
        String result = "";
        int i = 0;
        for (String s : list) {
            if (i >= 2) {
                result = result + s;
            }
            i++;
        }
        return result;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        //maineVorteile.onPlayerJoin(e);
        nick.onPlayerMove(e);
        //tablist.onPlayerJoin(e);
        Player p = e.getPlayer();
        p.discoverRecipe(animalNetKey);
        p.sendTitle("§kkfs§r§6Herzlichwillkommen ", "§6in der Hauptstadt§r§kgrd", 0, 10, 1);
        e.setJoinMessage("");
        //InvisableToSomeone.onJoin(e);
    }
    
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        maineVorteile.onEntityDamageByEntity(e);
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (freezes.contains(e.getPlayer().getName())) {
            e.setCancelled(true);
        }
        maineVorteile.onPlayerMove(e);
    }

    @EventHandler
    public void onBlockBreake(BlockBreakEvent e) {
        if (freezes.contains(e.getPlayer().getName())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        maineVorteile.onBlockClick(e);
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            //animalNet.rightClickBlock(e);
        }
    }

    @EventHandler
    public void onPlayerClick(PlayerInteractEntityEvent e) {
        animalNet.rightClickAnimal(e);
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (freezes.contains(e.getPlayer().getName())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        //tablist.onPlayerQuit(e);
        if (e.getPlayer().getName().equals(MaineVorteile.MY_NAME)) {
            e.setQuitMessage("");
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        maineVorteile.onPlayerLogin(e);
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
        maineVorteile.onPlayerCommandPreprocess(e);
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent e) {
        maineVorteile.onServerCommand(e);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        nick.onChat(e);
        if (e.getPlayer().hasPermission("colorCode")) {
            e.setMessage(e.getMessage().replace("&", "§"));
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            if (e.getCause() == DamageCause.FALL && !Config.isFallDamageActivated()) {
                e.setCancelled(true);
            }
        }
    }

    public boolean contains(String name) {
        boolean result = false;
        for (String s : blcklist) {
            if (name.toLowerCase().contains(s)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public ArrayList<Character> convertStringToArraylist(String str) {
        ArrayList<Character> charList = new ArrayList<>();
        for (int i = 0; i < str.length(); i++) {
            charList.add(str.charAt(i));
        }
        return charList;
    }
}
