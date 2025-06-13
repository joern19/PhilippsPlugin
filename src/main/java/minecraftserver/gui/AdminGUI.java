package minecraftserver.gui;

import configs.Config;
import configs.Homes;
import experimental.SimulateBlockInventorys;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import minecraftserver.PhilippsPlugin;
import minecraftserver.entityMod.ExplosionTrident;
import net.minecraft.server.v1_15_R1.ChatMessage;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.PacketPlayOutOpenWindow;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import utilities.BasicFunctions;
import utilities.ParticleManager;

public class AdminGUI {

    public static final String NAME_ADMIN_GUI = "Admin GUI";
    public static final String NAME_TRAILS = "Trials";
    public static final String NAME_SETTINGS = "Settings";
    public static final String NAME_MAGIC_ITEMS = "Magic Items";
    public static final String NAME_WORLD_OPTIONS = "World Options";
    public static final String NAME_GAMERULES = "Gamerules";
    public static final String NAME_POSION = "Posion GUI";
    public static final String NAME_PLAYER_OPTIONS = "Loading...";
    public static final String NAME_HOMES = "homes";

    private static AdminGUI instance = null;

    private AdminGUI() {
    }

    public static synchronized void reload() { // here we should create the Pages
        if (instance == null) {
            instance = new AdminGUI();

            new Page(NAME_ADMIN_GUI, instance.getPlayers());
            new Page(NAME_TRAILS, instance.getTrialItems());
            new Page(NAME_SETTINGS, instance.getSettingsItems());
            new Page(NAME_WORLD_OPTIONS, instance.getWorldItems());
            new Page(NAME_MAGIC_ITEMS, instance.getMagicItems());
            new Page(NAME_GAMERULES, instance.getGameruleItems());
            new Page(NAME_POSION, instance.getPosionItems());
            new Page(NAME_PLAYER_OPTIONS, instance.createPlayerOptions());
            new Page(NAME_HOMES, instance.getHomeItems());

            //do stuff that not have to be reloaded
        }
    }

    private ClickableItem[] getMagicItems() {
        LinkedList<ClickableItem> ll = new LinkedList<>();
        ll.add(new ClickableItem(Material.FEATHER, "Eine Feder die dich schneller macht") {
            @Override
            public void click(Player p, Boolean shift) {
                ItemStack is = new ItemStack(Material.FEATHER);
                ItemMeta im = is.getItemMeta();

                im.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, new AttributeModifier("test", 2, AttributeModifier.Operation.ADD_NUMBER));
                im.addAttributeModifier(Attribute.GENERIC_FLYING_SPEED, new AttributeModifier("test", 3, AttributeModifier.Operation.ADD_NUMBER));

                is.setItemMeta(im);
                p.getInventory().addItem(is);
            }
        });
        ll.add(new ClickableItem(Material.APPLE, "Ein paar Herzen mehr?") {
            @Override
            public void click(Player p, Boolean shift) {
                ItemStack is = new ItemStack(Material.LEATHER_HELMET);
                ItemMeta im = is.getItemMeta();
                im.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, new AttributeModifier("test", 120, AttributeModifier.Operation.ADD_NUMBER));
                im.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier("test", 10000, AttributeModifier.Operation.ADD_NUMBER));
                im.setUnbreakable(true);
                is.setItemMeta(im);
                p.getInventory().addItem(is);
            }
        });
        ll.add(new ClickableItem(Material.LEATHER_BOOTS, "Sieht nicht so aus als ob es Hilft oder?") {
            @Override
            public void click(Player p, Boolean shift) {
                ItemStack is = new ItemStack(Material.LEATHER_BOOTS);
                ItemMeta im = is.getItemMeta();
                im.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier("test", 10000, AttributeModifier.Operation.ADD_NUMBER));
                im.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier("test", 10000, AttributeModifier.Operation.ADD_NUMBER));
                im.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier("test", 10000, AttributeModifier.Operation.ADD_NUMBER));
                im.setUnbreakable(true);
                is.setItemMeta(im);
                p.getInventory().addItem(is);
            }
        });
        ll.add(new ClickableItem(Material.WOODEN_SWORD, "Und tot.") {
            @Override
            public void click(Player p, Boolean shift) {
                ItemStack is = new ItemStack(Material.STICK);
                ItemMeta im = is.getItemMeta();
                im.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier("test", 600, AttributeModifier.Operation.ADD_NUMBER));
                im.addAttributeModifier(Attribute.GENERIC_LUCK, new AttributeModifier("test", 1000, AttributeModifier.Operation.ADD_NUMBER));
                is.setItemMeta(im);
                p.getInventory().addItem(is);
            }
        });
        ll.add(new ClickableItem(Material.TRIDENT, "Need Explosions? Take This.") {
            @Override
            public void click(Player p, Boolean shift) {
                p.getInventory().addItem(ExplosionTrident.getExplosionTriden());
            }
        });
        return ll.toArray(new ClickableItem[ll.size()]);
    }

    private ClickableItem[] getGameruleItems() {
        LinkedList<ClickableItem> ll = new LinkedList<>();
        for (GameRule gr : GameRule.values()) {
            if (!gr.getType().equals(Boolean.class)) {
                continue;
            }
            ll.add(new ClickableItem(Material.STONE, "Edit: " + gr.getName(), "loading...") {
                @Override
                public void click(Player p, Boolean shift) {
                    World w = p.getWorld();
                    w.setGameRule(gr, !(Boolean) w.getGameRuleValue(gr));
                    BasicFunctions.setLore(p.getOpenInventory().getItem(this.getSlot()), "It is set to: " + w.getGameRuleValue(gr).toString());
                }

                @Override
                void onLoad(Player p) {
                    BasicFunctions.setLore(p.getOpenInventory().getItem(this.getSlot()), "It is set to: " + p.getWorld().getGameRuleValue(gr).toString());
                }
            });
        }
        return ll.toArray(new ClickableItem[ll.size()]);
    }

    private ClickableItem[] getWorldItems() {
        LinkedList<ClickableItem> list = new LinkedList<>();
        list.add(new ClickableItem(Material.WATER_BUCKET, "Set Weather to Rain for all Player") {
            @Override
            public void click(Player p, Boolean shift) {
                Bukkit.getOnlinePlayers().forEach((all) -> {
                    all.setPlayerWeather​(WeatherType.DOWNFALL);
                });
            }
        });
        list.add(new ClickableItem(Material.BUCKET, "Clear Weather for all Player") {
            @Override
            public void click(Player p, Boolean shift) {
                Bukkit.getOnlinePlayers().forEach((all) -> {
                    all.setPlayerWeather​(WeatherType.CLEAR);
                });
            }
        });
        list.add(new ClickableItem(Material.CLOCK, "Set Time To Day in your World") {
            @Override
            public void click(Player p, Boolean shift) {
                p.getLocation().getWorld().setTime(1000);
            }
        });
        list.add(new ClickableItem(Material.CLOCK, "Set Time To Night in your World") {
            @Override
            public void click(Player p, Boolean shift) {
                p.getLocation().getWorld().setTime(13000);
            }
        });
        list.add(new ClickableItem(Material.DIAMOND_SWORD, "CREATE some Magic Items") {
            @Override
            public void click(Player p, Boolean shift) {
                Page.getInstance(NAME_MAGIC_ITEMS).openPage(p);
            }
        });
        list.add(new ClickableItem(Material.CRAFTING_TABLE, "Edit Gamerules") {
            @Override
            public void click(Player p, Boolean shift) {
                Page.getInstance(NAME_GAMERULES).openPage(p);
            }
        });
        list.add(new ClickableItem(Material.LAVA_BUCKET, "Trash") {
            @Override
            public void click(Player p, Boolean shift) {
                Inventory inv = Bukkit.createInventory(p, 9 * 6, "Trash");
                inv.addItem(new ItemStack(Material.DIAMOND_BLOCK, 64));
                inv.addItem(new ItemStack(Material.IRON_INGOT, 64));
                inv.addItem(new ItemStack(Material.OAK_LOG, 64));
                inv.addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 64));
                inv.addItem(new ItemStack(Material.TOTEM_OF_UNDYING, 64));
                p.openInventory(inv);
            }
        });
        return list.toArray(new ClickableItem[list.size()]);
    }

    private ClickableItem[] getTrialItems() { //ok, the size for the Inventory is limited... So we could use categorys but i will now just filter some out for selection..
        final LinkedList<ClickableItem> list = new LinkedList<>();
        Particle[] avaibleParticle = new Particle[]{Particle.BARRIER, Particle.VILLAGER_HAPPY, Particle.VILLAGER_ANGRY, Particle.DRAGON_BREATH, Particle.EXPLOSION_NORMAL, Particle.FIREWORKS_SPARK, Particle.HEART, Particle.NOTE, Particle.CLOUD, Particle.SMOKE_LARGE};
        list.add(new ClickableItem(Material.BARRIER, "Remove all his Effects") {
            @Override
            void click(Player p, Boolean shift) {
                p.sendMessage("Removed " + ParticleManager.removeTrials(p) + " Trails.");
            }
        });
        for (Particle particle : avaibleParticle) {//add it onEnable...
            list.add(new ClickableItem(Material.FIREWORK_ROCKET, particle.name()) {
                @Override
                void click(Player p, Boolean shift) {
                    ParticleManager.addTrail(p, particle, 3);
                }
            });
        }
        return list.toArray(new ClickableItem[list.size()]);
    }

    private ClickableItem[] getSettingsItems() {
        LinkedList<ClickableItem> ll = new LinkedList<>();

        ll.add(new ClickableItem(Material.TNT, "Set the Size of the Explosion Trident(For everyone)", new String[]{"LEFT: increase", "RIGHT: decrease", "SHIFT: 5 step"}) {
            @Override
            void onLoad(Player p) {
                p.getOpenInventory().getItem(this.getSlot()).setAmount(Math.round(ExplosionTrident.getExplosionSize()));
            }

            @Override
            void leftClick(Player p, Boolean shift) {
                Integer step = 1;
                if (shift) {
                    step = 5;
                }
                Integer newSize = Math.round(ExplosionTrident.getExplosionSize()) + step;
                if (newSize > 64) {
                    return;
                }
                p.getOpenInventory().getItem(this.getSlot()).setAmount(newSize);
                ExplosionTrident.setExplosionSize((double) newSize);
            }

            @Override
            void rightClick(Player p, Boolean shift) {
                Integer step = 1;
                if (shift) {
                    step = 5;
                }
                Integer newSize = Math.round(ExplosionTrident.getExplosionSize()) - step;
                if (newSize < 1) {
                    return;
                }
                p.getOpenInventory().getItem(this.getSlot()).setAmount(newSize);
                ExplosionTrident.setExplosionSize((double) newSize);
            }
        });
        ll.add(new ClickableItem(Material.FEATHER, "Turns Falldamage on/off") {
            @Override
            void onLoad(Player p) {
                if (Config.isFallDamageActivated()) {
                    BasicFunctions.setLore(p.getOpenInventory().getItem(this.getSlot()), "Turns Falldamage off (it is on)");
                } else {
                    BasicFunctions.setLore(p.getOpenInventory().getItem(this.getSlot()), "Turns Falldamage on (it is off)");
                }
            }

            @Override
            void click(Player p, Boolean shift) {
                Config.setFallDamage(!Config.isFallDamageActivated());
                Config.save();
                onLoad(p);
            }
        });
        ll.add(new ClickableItem(Material.ENDER_PEARL, "Increases Flyspeed on/off") {
            @Override
            void onLoad(Player p) {
                if (Config.isFlySpeedIncreased()) {
                    BasicFunctions.setLore(p.getOpenInventory().getItem(this.getSlot()), "Turns Increased Flyspeed off (it is on)");
                } else {
                    BasicFunctions.setLore(p.getOpenInventory().getItem(this.getSlot()), "Turns Increased Flyspeed on (it is off)");
                }
            }

            @Override
            void click(Player p, Boolean shift) {
                Config.setIncreasedFlySpeed(!Config.isFlySpeedIncreased());
                Config.save();
                onLoad(p);
            }
        });

        return ll.toArray(new ClickableItem[ll.size()]);
    }

    public ClickableItem[] createPlayerOptions() {
        final LinkedList<ClickableItem> list = new LinkedList<>();
        list.add(new ClickableItem(Material.JUNGLE_DOOR, "Kick Player", "Kick him/her from the Server!") {
            @Override
            void click(Player p, Boolean shift, Object[] infos) {
                if (infos.length >= 1 && infos[0] instanceof Player) {
                    Player clicked = (Player) infos[0];
                    clicked.kickPlayer("");
                } else {
                    p.sendMessage("Message Info currupted...");
                }
            }

            @Override
            void onLoad(Player p, Object[] infos) {
                if (infos.length >= 1 && infos[0] instanceof Player) {
                    Player clicked = (Player) infos[0];
                    EntityPlayer ep = ((CraftPlayer) p).getHandle();

                    PacketPlayOutOpenWindow packet = new PacketPlayOutOpenWindow(ep.activeContainer.windowId, BasicFunctions.getContainerType(p.getOpenInventory().getTopInventory().getSize()), new ChatMessage(clicked.getName()));
                    ep.playerConnection.sendPacket(packet);
                    ep.updateInventory(ep.activeContainer);
                } else {
                    p.sendMessage("Message Info currupted...");
                }
            }
        });
        list.add(new ClickableItem(Material.BARRIER, "Bann Player", "Bann him/her forever! Warning: this will not kick him. Kick him manually...") {
            @Override
            void click(Player p, Boolean shift, Object[] infos) {
                if (infos.length >= 1 && infos[0] instanceof Player) {
                    Player clicked = (Player) infos[0];
                    Bukkit.getBanList(BanList.Type.NAME).addBan(clicked.getName(), "", null, "somebody");
                } else {
                    p.sendMessage("Message Info currupted...");
                }
            }
        });
        list.add(new ClickableItem(Material.ENDER_PEARL, "Teleport to him/her", "Teleport you to this Player") {
            @Override
            void click(Player p, Boolean shift, Object[] infos) {
                if (infos.length >= 1 && infos[0] instanceof Player) {
                    Player clicked = (Player) infos[0];
                    p.teleport(clicked);
                } else {
                    p.sendMessage("Message Info currupted...");
                }
            }
        });
        list.add(new ClickableItem(Material.STICKY_PISTON, "Teleport", "Teleportiere sie/ihn zu dir.") {
            @Override
            void click(Player p, Boolean shift, Object[] infos) {
                if (infos.length >= 1 && infos[0] instanceof Player) {
                    Player clicked = (Player) infos[0];
                    clicked.teleport(p);
                } else {
                    p.sendMessage("Message Info currupted...");
                }
            }
        });
        list.add(new ClickableItem(Material.CHEST, "Open his Inventory") {
            @Override
            void click(Player p, Boolean shift, Object[] infos) {
                if (infos.length >= 1 && infos[0] instanceof Player) {
                    Player clicked = (Player) infos[0];
                    p.openInventory(clicked.getInventory());
                } else {
                    p.sendMessage("Message Info currupted...");
                }
            }
        });
        list.add(new ClickableItem(Material.ENDER_CHEST, "Open his Ender Chest") {
            @Override
            void click(Player p, Boolean shift, Object[] infos) {
                if (infos.length >= 1 && infos[0] instanceof Player) {
                    Player clicked = (Player) infos[0];
                    p.openInventory(clicked.getEnderChest());
                } else {
                    p.sendMessage("Message Info currupted...");
                }
            }
        });
        list.add(new ClickableItem(Material.ICE, "Freeze", "Freeze or un-freeze the Player") {
            @Override
            void click(Player p, Boolean shift, Object[] infos) {
                if (infos.length >= 1 && infos[0] instanceof Player) {
                    Player clicked = (Player) infos[0];
                    if (PhilippsPlugin.instance.freeze(clicked)) {
                        p.sendMessage("Freezed");
                    } else {
                        p.sendMessage("ent-Freezed");
                    }
                } else {
                    p.sendMessage("Message Info currupted...");
                }
            }
        });
        list.add(new ClickableItem(Material.WOODEN_SWORD, "HIT", "Füge dem Spieler ein halbes Herz schaden zu.") {
            @Override
            void click(Player p, Boolean shift, Object[] infos) {
                if (infos.length >= 1 && infos[0] instanceof Player) {
                    Player clicked = (Player) infos[0];
                    clicked.damage(1);
                } else {
                    p.sendMessage("Message Info currupted...");
                }
            }
        });
        list.add(new ClickableItem(Material.DIAMOND_SWORD, "HIT", "Füge dem Spieler so viel schaden zu bis er nur noch ein halbes Herz hat.") {
            @Override
            void click(Player p, Boolean shift, Object[] infos) {
                if (infos.length >= 1 && infos[0] instanceof Player) {
                    Player clicked = (Player) infos[0];
                    clicked.damage(clicked.getHealth() - 1);
                } else {
                    p.sendMessage("Message Info currupted...");
                }
            }
        });
        list.add(new ClickableItem(Material.SKELETON_SKULL, "Kill", "Töte ihn.") {
            @Override
            void click(Player p, Boolean shift, Object[] infos) {
                if (infos.length >= 1 && infos[0] instanceof Player) {
                    Player clicked = (Player) infos[0];
                    clicked.setHealth(0);
                } else {
                    p.sendMessage("Message Info currupted...");
                }
            }
        });
        list.add(new ClickableItem(Material.DIRT, "SURVIVAL", "Setze ihn in den Survival mode.") {
            @Override
            void click(Player p, Boolean shift, Object[] infos) {
                if (infos.length >= 1 && infos[0] instanceof Player) {
                    Player clicked = (Player) infos[0];
                    clicked.setGameMode(GameMode.SURVIVAL);
                } else {
                    p.sendMessage("Message Info currupted...");
                }
            }
        });
        list.add(new ClickableItem(Material.DIAMOND_BLOCK, "CRATIVE", "Setze ihn in den Creative mode.") {
            @Override
            void click(Player p, Boolean shift, Object[] infos) {
                if (infos.length >= 1 && infos[0] instanceof Player) {
                    Player clicked = (Player) infos[0];
                    clicked.setGameMode(GameMode.CREATIVE);
                } else {
                    p.sendMessage("Message Info currupted...");
                }
            }
        });
        list.add(new ClickableItem(Material.COMMAND_BLOCK, "OP", "Ernenne ihn zu Operator.") {
            @Override
            void click(Player p, Boolean shift, Object[] infos) {
                if (infos.length >= 1 && infos[0] instanceof Player) {
                    Player clicked = (Player) infos[0];
                    clicked.setOp(true);
                } else {
                    p.sendMessage("Message Info currupted...");
                }
            }
        });
        list.add(new ClickableItem(Material.HEART_OF_THE_SEA, "Entferne OP", "Fange die Operator Rechte wieder ein.") {
            @Override
            void click(Player p, Boolean shift, Object[] infos) {
                if (infos.length >= 1 && infos[0] instanceof Player) {
                    Player clicked = (Player) infos[0];
                    clicked.setOp(false);
                } else {
                    p.sendMessage("Message Info currupted...");
                }
            }
        });
        list.add(new ClickableItem(Material.GOLDEN_APPLE, "Heile ihn") {
            @Override
            void click(Player p, Boolean shift, Object[] infos) {
                if (infos.length >= 1 && infos[0] instanceof Player) {
                    Player clicked = (Player) infos[0];
                    clicked.setHealth(clicked.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                } else {
                    p.sendMessage("Message Info currupted...");
                }
            }
        });
        list.add(new ClickableItem(Material.APPLE, "Stelle sein Essen wieder her.") {
            @Override
            void click(Player p, Boolean shift, Object[] infos) {
                if (infos.length >= 1 && infos[0] instanceof Player) {
                    Player clicked = (Player) infos[0];
                    clicked.setFoodLevel(20);
                } else {
                    p.sendMessage("Message Info currupted...");
                }
            }
        });
        list.add(new ClickableItem(Material.ENCHANTED_GOLDEN_APPLE, "Heile und füttere ihn") {
            @Override
            void click(Player p, Boolean shift, Object[] infos) {
                if (infos.length >= 1 && infos[0] instanceof Player) {
                    Player clicked = (Player) infos[0];
                    clicked.setHealth(clicked.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                    clicked.setFoodLevel(20);
                } else {
                    p.sendMessage("Message Info currupted...");
                }
            }
        });
        list.add(new ClickableItem(Material.SADDLE, "REITEN", "Setze dich auf ihn.") {
            @Override
            void click(Player p, Boolean shift, Object[] infos) {
                if (infos.length >= 1 && infos[0] instanceof Player) {
                    Player clicked = (Player) infos[0];
                    if (clicked != p) {
                        clicked.addPassenger(p);
                    } else {
                        p.sendMessage("§cThis does not work...");
                    }
                } else {
                    p.sendMessage("Message Info currupted...");
                }
            }
        });
        list.add(new ClickableItem(Material.FIRE_CHARGE, "BLITZ", "Lasse ein Blitz bei ihm Einschlagen.") {
            @Override
            void click(Player p, Boolean shift, Object[] infos) {
                if (infos.length >= 1 && infos[0] instanceof Player) {
                    Player clicked = (Player) infos[0];
                    clicked.getWorld().strikeLightning(clicked.getLocation());
                } else {
                    p.sendMessage("Message Info currupted...");
                }
            }
        });
        list.add(new ClickableItem(Material.BOOK, "INFO", "Get Infos about this Player.") {
            @Override
            void click(Player p, Boolean shift, Object[] infos) {
                if (infos.length >= 1 && infos[0] instanceof Player) {
                    Player clicked = (Player) infos[0];
                    p.sendMessage("Leben: " + clicked.getHealthScale());
                    p.sendMessage("Essen: " + clicked.getFoodLevel());
                    p.sendMessage("IP: " + clicked.getAddress().getAddress().getHostAddress() + ":" + clicked.getAddress().getPort());
                    p.sendMessage("World: " + clicked.getLocation().getWorld().getName());
                    p.sendMessage("Gamemode: " + clicked.getGameMode().name());
                    p.sendMessage("Display Name: " + clicked.getDisplayName());
                    p.sendMessage("Eingestellte Sprache: " + clicked.getLocale());
                    p.sendMessage("Darf Fliegen: " + (clicked.getAllowFlight() ? "Ja" : "Nein"));
                    p.sendMessage("Ist Op: " + (clicked.isOp() ? "Ja" : "Nein"));
                    String position = "";
                    for (Iterator<PotionEffect> it = clicked.getActivePotionEffects().iterator(); it.hasNext();) {
                        PotionEffect pe = it.next();
                        position += (pe.getType().getName());
                        if (it.hasNext()) {
                            position += ", ";
                        }
                    }
                    p.sendMessage("Effecte: " + position);
                } else {
                    p.sendMessage("Message Info currupted...");
                }
            }
        });
        list.add(new ClickableItem(Material.POTION, "Edit his Effects") {
            @Override
            void click(Player p, Boolean shift, Object[] infos) {
                if (infos.length >= 1 && infos[0] instanceof Player) {
                    Player clicked = (Player) infos[0];
                    Page.getInstance(NAME_POSION).openPage(p, new Object[]{clicked});
                } else {
                    p.sendMessage("Message Info currupted...");
                }
            }
        });
        list.add(new ClickableItem(Material.FEATHER, "Erlaube/Verbiete Fliegen") {
            @Override
            void click(Player p, Boolean shift, Object[] infos) {
                if (infos.length >= 1 && infos[0] instanceof Player) {
                    Player clicked = (Player) infos[0];
                    if (clicked.getAllowFlight()) {
                        clicked.setAllowFlight(false);
                        p.sendMessage("Disallowed Player to Fly.");
                    } else {
                        clicked.setAllowFlight(true);
                        p.sendMessage("Allowed Player to Fly.");
                    }
                } else {
                    p.sendMessage("Message Info currupted...");
                }
            }
        });
        if (Config.getHomesEnabled()) {
            list.add(new ClickableItem(Material.ACACIA_DOOR, "Teleportiere dich zu einem seinen HOMEs") {
                @Override
                void click(Player p, Boolean shift, Object[] infos) {
                    if (infos.length >= 1 && infos[0] instanceof Player) {
                        Player clicked = (Player) infos[0];
                        Page.getInstance(NAME_HOMES).openPage(p, new Object[]{clicked});
                    } else {
                        p.sendMessage("Message Info currupted...");
                    }
                }
            });
        }
        list.add(new ClickableItem(Material.TNT, "Spawn an explosion at his position.", "SHIFT: set Breaks Blocks") {
            @Override
            void click(Player p, Boolean shift, Object[] infos) {
                if (infos.length >= 1 && infos[0] instanceof Player) {
                    Player clicked = (Player) infos[0];
                    Location l = clicked.getLocation();
                    clicked.getWorld().createExplosion(l.getX(), l.getY(), l.getZ(), 2.0F, false, shift);
                } else {
                    p.sendMessage("Message Info currupted...");
                }
            }
        });
        list.add(new ClickableItem(Material.EXPERIENCE_BOTTLE, "XP", "Verpasse ihm 100 Level XP") {
            @Override
            void click(Player p, Boolean shift, Object[] infos) {
                if (infos.length >= 1 && infos[0] instanceof Player) {
                    Player clicked = (Player) infos[0];
                    clicked.setLevel(clicked.getLevel() + 100);
                } else {
                    p.sendMessage("Message Info currupted...");
                }
            }
        });
        list.add(new ClickableItem(Material.ENCHANTING_TABLE, "Öffne ein Enchanting Table") {
            @Override
            void click(Player p, Boolean shift, Object[] infos) {
                if (infos.length >= 1 && infos[0] instanceof Player) {
                    Player clicked = (Player) infos[0];
                    clicked.closeInventory();
                    SimulateBlockInventorys.openEnchantingTable(clicked);
                } else {
                    p.sendMessage("Message Info currupted...");
                }
            }
        });
        list.add(new ClickableItem(Material.SKELETON_SKULL, "Kill his Minecraft", new String[]{"The Player will need to Restart it..", "SHIFT: if you are sure"}) {
            @Override
            void click(Player p, Boolean shift) {
                if (shift) {
                    p.spawnParticle(Particle.HEART, p.getLocation(), Integer.MAX_VALUE);
                }
            }
        });
        list.add(new ClickableItem(Material.FIREWORK_ROCKET, "Add a trial to him..") {
            @Override
            void click(Player p, Boolean shift, Object[] infos) {
                Page.getInstance(NAME_TRAILS).openPage(p, infos);
            }
        });
        System.gc();

        return list.toArray(new ClickableItem[list.size()]);
    }

    private ClickableItem[] getHomeItems() {
        LinkedList<ClickableItem> list = new LinkedList<>();
        list.add(new ClickableItem(Material.CLOCK, "Loading...") {
            @Override
            void onLoad(Player p, Object[] infos) {
                if (infos.length >= 1 && infos[0] instanceof Player) {
                    Player clicked = (Player) infos[0];
                    HashMap<Integer, Location> map = Homes.getHomes(clicked.getName());
                    int counter = 0;
                    for (Integer i : map.keySet()) {
                        Location loc = map.get(i);
                        String[] lore = new String[]{"Teleport you to home Number " + i, "X: " + loc.getBlockX() + " Y: " + loc.getBlockY() + " Z: " + loc.getBlockZ(), "World: " + loc.getWorld().getName()};
                        ClickableItem ci = new ClickableItem(Material.BIRCH_DOOR, "Home " + i, lore) {
                            @Override
                            public void click(Player p, Boolean shift) {
                                p.teleport(loc);
                                p.sendMessage("You were Teleportet to Home from " + clicked.getName());
                            }
                        };
                        list.addLast(ci);
                        ci.setSlot(counter);
                        p.getOpenInventory().setItem(counter, ci.getItem());

                        Page page = Page.getInstance(NAME_HOMES);
                        page.addClickListener(ci);

                        counter += 1;
                    }
                } else {
                    p.sendMessage("Message Info currupted...");
                }
            }
        });
        return list.toArray(new ClickableItem[list.size()]);
    }

    private ClickableItem[] getPosionItems() {
        final LinkedList<ClickableItem> list = new LinkedList<>();
        list.add(new ClickableItem(Material.CLOCK, "duration", new String[]{"set the Duration", "leftClick increase", "rightClick decrease", "64 refers to maximum", "1 equals to 10 secounds"}) {
            @Override
            void rightClick(Player p, Boolean shift) {
                Integer step = 1;
                if (shift) {
                    step = 10;
                }
                ItemStack is = p.getOpenInventory().getItem(this.getSlot());
                Integer newSize = is.getAmount() - step;
                if (newSize < 1) {
                    newSize = 1;
                }
                is.setAmount(newSize);
            }

            @Override
            void leftClick(Player p, Boolean shift) {
                Integer step = 1;
                if (shift) {
                    step = 10;
                }
                ItemStack is = p.getOpenInventory().getItem(this.getSlot());
                Integer newSize = is.getAmount() + step;
                if (newSize > 64) {
                    newSize = 64;
                }
                is.setAmount(newSize);
            }

            @Override
            void onLoad(Player p) {
                p.getOpenInventory().getItem(this.getSlot()).setAmount(12); //default is 2 minutes.
            }
        });
        list.add(new ClickableItem(Material.GLOWSTONE_DUST, "level", new String[]{"set the level of the enchantment", "leftClick increase", "rightClick decrease"}) {
            @Override
            void rightClick(Player p, Boolean shift) {
                Integer step = 1;
                if (shift) {
                    step = 10;
                }
                ItemStack is = p.getOpenInventory().getItem(this.getSlot());
                Integer newSize = is.getAmount() - step;
                if (newSize < 1) {
                    newSize = 1;
                }
                is.setAmount(newSize);
            }

            @Override
            void leftClick(Player p, Boolean shift) {
                Integer step = 1;
                if (shift) {
                    step = 5;
                }
                ItemStack is = p.getOpenInventory().getItem(this.getSlot());
                Integer newSize = is.getAmount() + step;
                if (newSize > 30) {
                    newSize = 30;
                }
                is.setAmount(newSize);
            }

            @Override
            void onLoad(Player p) {
                p.getOpenInventory().getItem(this.getSlot()).setAmount(3);
            }
        });
        list.add(new ClickableItem(Material.MILK_BUCKET, "Clear all Effects", "entfernt alle Effecte") {
            @Override
            public void click(Player p, Boolean shift, Object[] infos) {
                if (infos.length >= 1 && infos[0] instanceof Player) {
                    Player clicked = (Player) infos[0];
                    clicked.getActivePotionEffects().forEach((pe) -> {
                        clicked.removePotionEffect(pe.getType());
                    });
                } else {
                    p.sendMessage("Message Info currupted...");
                }
            }
        });
        for (PotionEffectType pet : PotionEffectType.values()) {
            ItemStack potion = new ItemStack(Material.POTION);  //if i move this
            PotionMeta meta = (PotionMeta) potion.getItemMeta(); //and this above the loop the first effect gets added to the Player. WHY???
            meta.setDisplayName(pet.getName());
            meta.setColor(pet.getColor());
            meta.addCustomEffect(pet.createEffect(0, 0), true);
            list.add(new ClickableItem(Material.POTION, meta) {
                @Override
                void click(Player p, Boolean shift, Object[] infos) {
                    if (infos.length >= 1 && infos[0] instanceof Player) {
                        Player clicked = (Player) infos[0];
                        int duration = 100000;
                        int blockAmount = p.getOpenInventory().getItem(0).getAmount();
                        if (blockAmount < 64) {
                            duration = blockAmount * 20 * 10;
                        }
                        int level = p.getOpenInventory().getItem(1).getAmount();
                        clicked.addPotionEffect(pet.createEffect(duration, level));
                    } else {
                        p.sendMessage("Message Info currupted...");
                    }
                }
            });
        }
        return list.toArray(new ClickableItem[list.size()]);
    }

    private ClickableItem[] getPlayers() {
        final LinkedList<ClickableItem> list = new LinkedList<>();

        list.add(new ClickableItem(Material.GRASS_BLOCK, "Allgemeine Optionen") {
            @Override
            public void click(Player p, Boolean shift) {
                Page.getInstance(NAME_WORLD_OPTIONS).openPage(p);
            }
        });
        list.add(new ClickableItem(Material.COMPARATOR, "Settings") {
            @Override
            void click(Player p, Boolean shift) {
                Page.getInstance(NAME_SETTINGS).openPage(p);
            }
        });
        list.add(new ClickableItem(Material.CLOCK, "LOADING...") {
            @Override
            void onLoad(Player p) {
                EntityPlayer ep = ((CraftPlayer) p).getHandle();

                PacketPlayOutOpenWindow packet = new PacketPlayOutOpenWindow(ep.activeContainer.windowId, BasicFunctions.getContainerType(BasicFunctions.getNeededRows(Bukkit.getOnlinePlayers().size() + 2) * 9), new ChatMessage(p.getOpenInventory().getTitle()));
                ep.playerConnection.sendPacket(packet);
                ep.updateInventory(ep.activeContainer);

                p.getOpenInventory().getItem(this.getSlot()).setType(Material.VOID_AIR);
                int counter = this.getSlot();
                for (Player all : Bukkit.getOnlinePlayers()) {
                    ClickableItem ci = new ClickableItem(PlayerHeads.getInstance().getHead(all)) {
                        @Override
                        void click(Player p, Boolean shift) {
                            Player clicked = Bukkit.getPlayer(getItem().getItemMeta().getDisplayName());
                            Page.getInstance(NAME_PLAYER_OPTIONS).openPage(p, new Object[]{clicked});
                        }
                    };
                    ci.setSlot(counter);
                    p.getOpenInventory().setItem(counter, ci.getItem());
                    ci.onLoad(p); //theoretisch unnötig. Mache es trotzdem der vollständigkeit halber..

                    Page page = Page.getInstance(NAME_ADMIN_GUI);
                    page.addClickListener(ci);

                    counter += 1;
                }
            }
        });
        return list.toArray(new ClickableItem[list.size()]);
    }
}
