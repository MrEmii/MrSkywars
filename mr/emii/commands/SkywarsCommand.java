package mr.emii.commands;

import mr.emii.Main;
import mr.emii.configurations.Messages;
import mr.emii.game.Game;
import mr.emii.models.KitModel;
import mr.emii.models.PlayerModel;
import mr.emii.utils.GameFileUtils;
import mr.emii.utils.KitFileUtils;
import mr.emii.utils.FileUtils;
import mr.emii.utils.TextUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SkywarsCommand implements CommandExecutor {

    private Messages config = Main.getInstance().messages;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player p = (Player) commandSender;

        if (p.hasPermission("mrskywars.admin")) {
            if (strings.length == 0) {
                p.sendMessage(getHelpMessage().toString());
            } else if (strings.length == 1) {
                String cmd = strings[0];
                if (cmd.equalsIgnoreCase("reload")) {
                    Main.getInstance().reloadConfig();
                    p.sendMessage("§a[MrSkywars] §7Updated config.yml");
                    Main.getInstance().readKits();
                    p.sendMessage("§a[MrSkywars] §7Updated kits configurations");
                    if (Main.getInstance().getGameManager().getGameList().size() > 0) {
                        Main.getInstance().getGameManager().getGameList().forEach(gg -> {
                            gg.getGameConfig().reload();
                            p.sendMessage("§a[MrSkywars] §7Updated " + gg + ".yml file");
                        });
                    }
                } else if (cmd.equalsIgnoreCase("setmainlobby")) {
                    Main.getInstance().getConfig().set("lobbyLocation", TextUtils.LocationToString(p.getLocation()));
                    p.sendMessage("§a[MrSkywars] §7Main lobby updated!");
                    Main.getInstance().saveConfig();
                    Main.getInstance().reloadConfig();
                } else if (cmd.equalsIgnoreCase("kits")) {
                    p.sendMessage("§a§m---------------------------------------------------\n");
                    p.sendMessage("§aTotal Kits: §7" + Main.getInstance().getKitsManager().getKitList().size());
                    Main.getInstance().getKitsManager().getKitList().forEach(a -> {
                        p.sendMessage("§b- " + a.getName());
                    });
                    p.sendMessage("§a§m---------------------------------------------------\n");
                } else if (cmd.equalsIgnoreCase("games")) {
                    p.sendMessage("§a§m---------------------------------------------------\n");
                    p.sendMessage("§aTotal Games: §7" + Main.getInstance().getGameManager().getGameList().size());
                    if (Main.getInstance().getGameManager().getGameList().size() > 0) {
                        Main.getInstance().getGameManager().getGameList().forEach(a -> {
                            p.sendMessage("§b-" + a.getName() + "§7-" + a.getGameState());
                        });
                    } else {
                        p.sendMessage("§7Not found games");
                    }
                    p.sendMessage("§a§m---------------------------------------------------\n");
                } else {
                    p.sendMessage(getHelpMessage().toString());
                }
            } else if (strings.length == 2) {
                String cmd = strings[0];
                String arg = strings[1];

                if (cmd.equalsIgnoreCase("get")) {
                    if (arg.equalsIgnoreCase("items")) {
                        ArrayList<String> materials = new ArrayList<>();
                        for (Material ss : Material.values()) {
                            materials.add(ss.name());
                        }
                        Main.getInstance().getConfig().set("items", materials);
                        Main.getInstance().saveConfig();
                    }
                }

            } else if (strings.length == 3) {
                String cmd = strings[0];
                String arg = strings[1];
                String args = strings[2];
                if (cmd.equalsIgnoreCase("kit")) {
                    if (arg.equalsIgnoreCase("remove")) {
                        if (Main.getInstance().getKitsManager().getByString(args) != null) {
                            KitModel m_kit = (KitModel) Main.getInstance().getKitsManager().getByString(args);
                            m_kit.getFile().getFile().delete();
                            Main.getInstance().getKitsManager().removeKitByName(m_kit);
                            p.sendMessage("§a[MrSkywars] §7" + args + " successfully removed.");
                        } else {
                            p.sendMessage(config.getString("messages.errormessage").replace("{error}", args + " doesn't exists"));
                        }
                    }
                    if (arg.equalsIgnoreCase("edit")) {
                        if (Main.getInstance().getKitsManager().getByString(args) != null) {
                            List<ItemStack> itemLs = new ArrayList<>();
                            for (int i = 0; i < p.getInventory().getContents().length; i++) {
                                itemLs.add(p.getInventory().getContents()[i]);
                            }

                            KitModel m_kit = (KitModel) Main.getInstance().getKitsManager().getByString(args);
                            m_kit.setItems(itemLs);
                            FileUtils fl = KitFileUtils.updateKitFile(m_kit);
                            fl.reload();
                            p.sendMessage("§a[MrSkywars] §7" + args + " edit successfully.");
                        } else {
                            p.sendMessage(config.getString("messages.errormessage").replace("{error}", args + " doesn't exists"));
                        }
                    }
                }
                if (cmd.equalsIgnoreCase("game")) {
                    if (arg.equalsIgnoreCase("remove")) {
                        if (Main.getInstance().getGameManager().getGameByName(args) != null) {
                            Game gameobj = Main.getInstance().getGameManager().getGameByName(args);
                            gameobj.getGameConfig().getFile().delete();
                            Main.getInstance().getGameManager().removeGame(gameobj);
                            p.sendMessage("§a[MrSkywars] §7" + args + " successfully removed.");
                        } else {
                            p.sendMessage(config.getString("messages.errormessage").replace("&", "§").replace("{error}", args + " doesn't exist"));
                        }
                    }
                    if (arg.equalsIgnoreCase("read")) {
                        if (Main.getInstance().getGameManager().getGameByName(args) == null) {
                            File theGame = new File(Main.getInstance().getDataFolder().getPath() + "/games/" + args + ".yml");
                            if (theGame.exists()) {
                                FileUtils gamefile = new FileUtils(theGame);
                                gamefile.reload();
                                Main.getInstance().getGameManager().addGame(
                                        GameFileUtils.FileToGame(gamefile)
                                );
                                p.sendMessage("§a[MrSkywars] §7" + args + " successfully created from file.");
                            } else {
                                p.sendMessage("§a[MrSkywars] §7" + args + ".yml doesn't exists");
                            }
                        } else {
                            p.sendMessage(config.getString("messages.errormessage").replace("&", "§").replace("{error}", args + " already exist"));
                        }
                    }
                    if (arg.equalsIgnoreCase("forcestart")) {
                        if (Main.getInstance().getGameManager().getGameByName(args) != null) {
                            Game gameobj = Main.getInstance().getGameManager().getGameByName(args);
                            if (gameobj.forceStart()) {
                                p.sendMessage("§a[MrSkywars] §7" + args + " successfully started.");
                            } else {
                                p.sendMessage("§a[MrSkywars] §7" + args + " could force start.");
                            }
                        } else {
                            p.sendMessage(config.getString("messages.errormessage").replace("&", "§").replace("{error}", args + " doesn't exist"));
                        }
                    }
                    if (arg.equalsIgnoreCase("reload")) {
                        if (Main.getInstance().getGameManager().getGameByName(args) != null) {
                            Game gameobj = Main.getInstance().getGameManager().getGameByName(args);
                            if (gameobj.getGameConfig() != null) {
                                gameobj.getGameConfig().reload();
                                p.sendMessage("§a[MrSkywars] §7" + args + ".yml successfully recharged.");
                            } else {
                                p.sendMessage(config.getString("messages.errormessage").replace("&", "§").replace("{error}", args + ".yml doesn't exist"));
                            }
                        } else {
                            p.sendMessage(config.getString("messages.errormessage").replace("&", "§").replace("{error}", args + " doesn't exist"));
                        }
                    }
                    if (arg.equalsIgnoreCase("bound")) {
                        if (Main.getInstance().getGameManager().getGameByName(args) != null) {
                            Game gameobj = Main.getInstance().getGameManager().getGameByName(args);
                            if (gameobj.getGameConfig() != null) {
                                gameobj.getGameConfig().reload();
                                p.sendMessage("§a[MrSkywars] §7" + args + ".yml successfully recharged.");
                            } else {
                                p.sendMessage(config.getString("messages.errormessage").replace("&", "§").replace("{error}", args + ".yml doesn't exist"));
                            }
                        } else {
                            p.sendMessage(config.getString("messages.errormessage").replace("&", "§").replace("{error}", args + " doesn't exist"));
                        }
                    }
                    if (arg.equalsIgnoreCase("forcestop")) {
                        if (Main.getInstance().getGameManager().getGameByName(args) != null) {
                            Game gameobj = Main.getInstance().getGameManager().getGameByName(args);
                            if (gameobj.forceStop()) {
                                p.sendMessage("§a[MrSkywars] §7" + args + " successfully stopped.");
                            } else {
                                p.sendMessage("§a[MrSkywars] §7" + args + " could force stop.");
                            }
                        } else {
                            p.sendMessage(config.getString("messages.errormessage").replace("&", "§").replace("{error}", args + " doesn't exist"));
                        }
                    }
                    if (arg.equalsIgnoreCase("setlobby")) {
                        if (Main.getInstance().getGameManager().getGameByName(args) != null) {
                            Game gameobj = Main.getInstance().getGameManager().getGameByName(args);
                            gameobj.setLobby(p.getLocation());
                            String locc = TextUtils.LocationToString(p.getLocation());
                            gameobj.getGameConfig().set("prelobby", locc);
                            p.sendMessage("§a[MrSkywars] §7[" + locc + "] added as pre lobby for §f" + args);
                        } else {
                            p.sendMessage(config.getString("messages.errormessage").replace("&", "§").replace("{error}", args + " doesn't exist"));
                        }
                    }
                    if (arg.equalsIgnoreCase("setspecslobby")) {
                        if (Main.getInstance().getGameManager().getGameByName(args) != null) {
                            Game gameobj = Main.getInstance().getGameManager().getGameByName(args);
                            gameobj.setSpecLobby(p.getLocation());
                            String locc = TextUtils.LocationToString(p.getLocation());
                            gameobj.getGameConfig().set("specslobby", locc);
                            p.sendMessage("§a[MrSkywars] §7[" + locc + "] added as spectators lobby for §f" + args);
                        } else {
                            p.sendMessage(config.getString("messages.errormessage").replace("&", "§").replace("{error}", args + " doesn't exist"));
                        }
                    }
                    if (arg.equalsIgnoreCase("join")) {
                        if (Main.getInstance().getGameManager().getGameByName(args) != null) {
                            Game gameobj = Main.getInstance().getGameManager().getGameByName(args);
                            PlayerModel mp = (PlayerModel) Main.getInstance().getPlayerManager().getByString(p.getDisplayName());
                            gameobj.playerJoin(mp);
                            p.sendMessage("§a[MrSkywars] §7" + args + " successfully removed.");
                        } else {
                            p.sendMessage(config.getString("messages.errormessage").replace("&", "§").replace("{error}", args + " doesn't exist"));
                        }
                    }

                }
            } else if (strings.length == 4) {
                String cmd = strings[0];
                String arg = strings[1];
                String args = strings[2];
                String sargs = strings[3];
                if (cmd.equalsIgnoreCase("kit")) {
                    if (arg.equalsIgnoreCase("create")) {
                        if (Main.getInstance().getKitsManager().getByString(args) == null) {
                            List<ItemStack> itemLs = new ArrayList<>();
                            for (int i = 0; i < p.getInventory().getContents().length; i++) {
                                itemLs.add(p.getInventory().getContents()[i]);
                            }

                            KitModel m_kit = new KitModel(args, itemLs, Integer.valueOf(sargs));
                            FileUtils file = KitFileUtils.createKitModelFile(m_kit);
                            m_kit.setFile(file);
                            Main.getInstance().getKitsManager().addToList(m_kit);

                            p.sendMessage("§a[MrSkywars] §7" + args + " added to kit list.");
                        } else {
                            p.sendMessage(config.getString("messages.errormessage").replace("{error}", args + " already exists"));
                        }
                    }
                    if (arg.equalsIgnoreCase("edit")) {
                        if (Main.getInstance().getKitsManager().getByString(args) != null) {
                            KitModel m_kit = (KitModel) Main.getInstance().getKitsManager().getByString(args);
                            m_kit.setPrice(Integer.valueOf(sargs));
                            FileUtils fl = KitFileUtils.updateKitFile(m_kit);
                            fl.reload();
                            p.sendMessage("§a[MrSkywars] §7" + args + " edit successfully.");
                        } else {
                            p.sendMessage(config.getString("messages.errormessage").replace("{error}", args + " doesn't exists"));
                        }
                    }

                }
                if (cmd.equalsIgnoreCase("game")) {
                    if (arg.equalsIgnoreCase("setmaxplayers")) {
                        if (Main.getInstance().getGameManager().getGameByName(args) != null) {
                            Game gameobj = Main.getInstance().getGameManager().getGameByName(args);
                            gameobj.setMaxPlayers(Integer.valueOf(sargs));
                            gameobj.getGameConfig().set("maxplayers", Integer.valueOf(sargs));
                            p.sendMessage("§a[MrSkywars] §7" + Integer.valueOf(sargs) + " used for max players in " + args);
                        } else {
                            p.sendMessage(config.getString("messages.errormessage").replace("&", "§").replace("{error}", args + " doesn't exist"));
                        }
                    }
                    if (arg.equalsIgnoreCase("minplayers")) {
                        if (Main.getInstance().getGameManager().getGameByName(args) != null) {
                            Game gameobj = Main.getInstance().getGameManager().getGameByName(args);
                            gameobj.setMinPlayers(Integer.valueOf(sargs));
                            gameobj.getGameConfig().set("minplayers", Integer.valueOf(args));
                            p.sendMessage("§a[MrSkywars] §7" + Integer.valueOf(sargs) + " used for min players in " + args);
                        } else {
                            p.sendMessage(config.getString("messages.errormessage").replace("&", "§").replace("{error}", args + " doesn't exist"));
                        }
                    }
                    if (arg.equalsIgnoreCase("rollback")) {
                        if (args.equalsIgnoreCase("read")) {
                            if (Main.getInstance().getGameManager().getGameByName(args) != null) {
                                Game gameobj = Main.getInstance().getGameManager().getGameByName(args);
                                if (gameobj.readBlocks()) {
                                    p.sendMessage("§a[MrSkywars] §7" + args + " blocks successfully reading.");
                                } else {
                                    p.sendMessage("§a[MrSkywars] §7" + args + " could read blocks on map.");
                                }
                            } else {
                                p.sendMessage(config.getString("messages.errormessage").replace("&", "§").replace("{error}", args + " doesn't exist"));
                            }
                        } else if (args.equalsIgnoreCase("start")) {
                            if (Main.getInstance().getGameManager().getGameByName(args) != null) {
                                Game gameobj = Main.getInstance().getGameManager().getGameByName(args);
                                if (gameobj.rollback()) {
                                    p.sendMessage("§a[MrSkywars] §7" + args + " successfully rollback.");
                                } else {
                                    p.sendMessage("§a[MrSkywars] §7" + args + " could be rollback map.");
                                }
                            } else {
                                p.sendMessage(config.getString("messages.errormessage").replace("&", "§").replace("{error}", args + " doesn't exist"));
                            }
                        }
                    }
                    if (arg.equalsIgnoreCase("spawn")) {
                        if (args.equalsIgnoreCase("add")) {
                            if (Main.getInstance().getGameManager().getGameByName(sargs) != null) {
                                Game gameobj = Main.getInstance().getGameManager().getGameByName(sargs);
                                if (gameobj.getSpawns().size() < gameobj.getMaxPlayers()) {
                                    ArrayList<Location> loc = new ArrayList<>();
                                    loc.add(p.getLocation());
                                    gameobj.setSpawns(loc);
                                    List<String> spawnsList = gameobj.getGameConfig().getStringList("spawns");
                                    String locc = TextUtils.LocationToString(p.getLocation());
                                    spawnsList.add(locc);
                                    gameobj.getGameConfig().set("spawns", spawnsList);
                                    p.sendMessage("§a[MrSkywars] §7[" + locc + "] added as spawn §f" + spawnsList.size() + "/" + gameobj.getMaxPlayers());
                                } else {
                                    p.sendMessage(config.getString("messages.errormessage").replace("&", "§").replace("{error}", "No more slots for spawns"));
                                }
                            } else {
                                p.sendMessage(config.getString("messages.errormessage").replace("&", "§").replace("{error}", sargs + " doesn't exist"));
                            }
                        }
                        if (args.equalsIgnoreCase("remove")) {
                            if (Main.getInstance().getGameManager().getGameByName(sargs) != null) {
                                Game gameobj = Main.getInstance().getGameManager().getGameByName(sargs);
                                if (gameobj.getSpawns().size() < gameobj.getMaxPlayers()) {
                                    List<Location> loc = gameobj.getSpawns();
                                    loc.remove(loc.lastIndexOf(loc));
                                    gameobj.setSpawns((ArrayList<Location>) loc);
                                    List<String> spawnsList = gameobj.getGameConfig().getStringList("spawns");
                                    spawnsList.remove(spawnsList.lastIndexOf(spawnsList));
                                    gameobj.getGameConfig().set("spawns", spawnsList);
                                    p.sendMessage("§a[MrSkywars] §7Removed last spawn added §f" + spawnsList.size() + "/" + gameobj.getMaxPlayers());
                                } else {
                                    p.sendMessage(config.getString("messages.errormessage").replace("&", "§").replace("{error}", "No more slots for spawns"));
                                }
                            } else {
                                p.sendMessage(config.getString("messages.errormessage").replace("&", "§").replace("{error}", sargs + " doesn't exist"));
                            }
                        }
                    }
                    if (arg.equalsIgnoreCase("bound")) {
                        if (args.equalsIgnoreCase("add")) {
                            if (Main.getInstance().getGameManager().getGameByName(sargs) != null) {
                                Game gameobj = Main.getInstance().getGameManager().getGameByName(sargs);
                                if (gameobj.getBounds().size() < 2) {
                                    Location loc = p.getLocation();
                                    gameobj.getBounds().add(loc);
                                    List<String> spawnsList = gameobj.getGameConfig().getStringList("bounds");
                                    String locc = TextUtils.LocationToString(p.getLocation());
                                    spawnsList.add(locc);
                                    gameobj.getGameConfig().set("bounds", spawnsList);
                                    p.sendMessage("§a[MrSkywars] §7[" + locc + "] added bound §f" + spawnsList.size() + "/2");
                                } else {
                                    p.sendMessage(config.getString("messages.errormessage").replace("&", "§").replace("{error}", "No more slots for bound"));
                                }
                            } else {
                                p.sendMessage(config.getString("messages.errormessage").replace("&", "§").replace("{error}", sargs + " doesn't exist"));
                            }
                        }
                        if (args.equalsIgnoreCase("remove")) {
                            if (Main.getInstance().getGameManager().getGameByName(sargs) != null) {
                                Game gameobj = Main.getInstance().getGameManager().getGameByName(sargs);
                                if (gameobj.getSpawns().size() < gameobj.getMaxPlayers()) {
                                    List<Location> loc = gameobj.getBounds();
                                    loc.remove(loc.lastIndexOf(loc));
                                    gameobj.setBounds(loc);
                                    List<String> spawnsList = gameobj.getGameConfig().getStringList("bounds");
                                    spawnsList.remove(spawnsList.lastIndexOf(spawnsList));
                                    gameobj.getGameConfig().set("bounds", spawnsList);
                                    p.sendMessage("§a[MrSkywars] §7Remove last bound §f" + spawnsList.size() + "/2");
                                } else {
                                    p.sendMessage(config.getString("messages.errormessage").replace("&", "§").replace("{error}", "No more slots for bounds"));
                                }
                            } else {
                                p.sendMessage(config.getString("messages.errormessage").replace("&", "§").replace("{error}", sargs + " doesn't exist"));
                            }
                        }
                    }
                    if (arg.equalsIgnoreCase("clone")) {
                        if (Main.getInstance().getGameManager().getGameByName(args) != null) {
                            if (Main.getInstance().getGameManager().getGameByName(sargs) == null) {
                                Game newGame = GameFileUtils.cloneGame(Main.getInstance().getGameManager().getGameByName(args).getGameConfig(), sargs);
                                if (newGame != null) {
                                    p.sendMessage("§a[MrSkywars] §7" + args + " successfully cloned to" + sargs);
                                } else {
                                    p.sendMessage("§a[MrSkywars] §7Could not clone the file " + arg + ".yml");
                                }
                            } else {
                                p.sendMessage(config.getString("messages.errormessage").replace("&", "§").replace("{error}", sargs + " already exist"));
                            }
                        } else {
                            p.sendMessage(config.getString("messages.errormessage").replace("&", "§").replace("{error}", args + " doesn't exist"));
                        }
                    }
                }
                if (cmd.equalsIgnoreCase("player")) {

                }
            } else if (strings.length == 5) {
                String cmd = strings[0];
                String action = strings[1];
                String gn = strings[2];
                String arg = strings[3];
                String args = strings[3];
                if (cmd.equalsIgnoreCase("game")) {
                    if (action.equalsIgnoreCase("create")) {
                        if (Main.getInstance().getGameManager().getGameByName(gn) == null) {
                            int maxplayers = Integer.valueOf(arg);
                            int minplayers = Integer.valueOf(args);
                            Game m_game = new Game(gn, maxplayers, minplayers, 0, 0, 0, new ArrayList<>(), null, null, null, null, new ArrayList<>());
                            FileUtils ft = GameFileUtils.GameFileUtils(m_game);
                            if (ft != null) m_game.setGameConfig(ft);
                            Main.getInstance().getGameManager().addGame(m_game);

                            p.sendMessage("§a[MrSkywars] §7" + gn + " successfully game created.");
                        } else {
                            p.sendMessage(config.getString("messages.errormessage").replace("&", "§").replace("{error}", gn + " already exist"));
                        }
                    }
                }
            }
        }

        return true;
    }

    public StringBuilder getHelpMessage() {

        StringBuilder msg = new StringBuilder("§6MrSkywars §aCommands\n");
        msg.append("§a§m---------------------------------------------------\n");
        msg.append("§a[]-Optionals <>-Required\n");
        msg.append("§6General §aCommands\n");
        msg.append("\n");
        msg.append("§b/mrsk reload - §7Reload data without players files\n");
        msg.append("§b/mrsk kits - §7Show all kits\n");
        msg.append("§b/mrsk games - §7Show all games\n");
        msg.append("§b/mrsk setmainlobby - §7Set main lobby\n");
        msg.append("§6Game §aCommands\n");
        msg.append("§b/mrsk game reload <game name> - §7Reload game config file\n");
        msg.append("§b/mrsk game read <game name> - §7Read game from configuration\n");
        msg.append("§b/mrsk game bound set <game name> - §7Add a bound, max two\n");
        msg.append("§b/mrsk game bound set <game name> [index] - §7Remove bound. by default is the lasted\n");
        msg.append("§b/mrsk game create <game name> <maxplayers> <minplayers> - §7Create with name and players properties\n");
        msg.append("§b/mrsk game world <game name> - §7Set the world of the game for rollback\n");
        msg.append("§b/mrsk game remove <game name> - §7Delete arena forever\n");
        msg.append("§b/mrsk game spawn add <game name> - §7Add spawn for the game\n");
        msg.append("§b/mrsk game spawn remove <game name> [index] - §7Remove spawn. by default is the lasted\n");
        msg.append("§b/mrsk game clone <game to copy> <new game name> - §7Clone a existent game to new game\n");
        msg.append("§b/mrsk game forcestart <game name> - §7Force start game\n");
        msg.append("§b/mrsk game forcestop <game name> - §7Force stop game\n");
        msg.append("§b/mrsk game rollback read <game name> - §7Force rollback map\n");
        msg.append("§b/mrsk game rollback start <game name> - §7Force rollback map\n");
        msg.append("§b/mrsk game setmaxplayers <game name> <maxplayers> - §7Set max players\n");
        msg.append("§b/mrsk game setspecs <game name> <true|false> - §7Set available join as spectator, by default is true\n");
        msg.append("§b/mrsk game setminplayers <game name> <maxplayers> - §7Set min players\n");
        msg.append("§b/mrsk game setspecslobby <game name> - §7Set spectators lobby\n");
        msg.append("§b/mrsk game setlobby <game name> - §7Set pre lobby of the arena\n");
        msg.append("§6Kit §aCommands\n");
        msg.append("§b/mrsk kit create <kit name> <price> - §7Create kit, copy your current inventory\n");
        msg.append("§b/mrsk kit remove <kit name> - §7Remove kit\n");
        msg.append("§b/mrsk kit edit <kit name> [price] - §7Update kit with your current inventory if price is undefined\n");
        msg.append("§6Player §aCommands\n");
        msg.append("§b/mrsk player <player> addkit <kit name> - §7Add kit to player\n");
        msg.append("§b/mrsk player <player> removekit <kit name> - §7Remove kit to player\n");
        msg.append("§b/mrsk player <player> addcoins <mount> - §7Add coins to player\n");
        msg.append("§b/mrsk player <player> removecoins <mount> - §7Remove coins to player\n");
        msg.append("§b/mrsk player <player> setcoins <mount> - §7Set coins to player since 0\n");
        msg.append("§a§m---------------------------------------------------\n");

        return msg;
    }
}
