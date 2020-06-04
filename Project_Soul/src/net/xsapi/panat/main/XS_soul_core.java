package net.xsapi.panat.main;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import net.xsapi.panat.event.XS_Death_Event;
import net.xsapi.panat.event.XS_Join_Event;
import net.xsapi.panat.setting.XS_load_config;
import net.xsapi.panat.setting.XS_setting_config;
import net.xsapi.panat.setting.XS_setting_messages;
import net.xsapi.panat.setting.XS_setting_player;
import net.xsapi.panat.utiles.ExampleExpansion;

public class XS_soul_core extends JavaPlugin implements Listener
{
	public static XS_soul_core plugin;
	
	public void onEnable()
	{
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) 
        {
        	Bukkit.getLogger().info("PlaceholderAPI Hooked!");
        	new ExampleExpansion().register();
        } 
        else 
        {
        	Bukkit.getLogger().info("PlaceholderAPI Not Hooked!");
            throw new RuntimeException("Could not find PlaceholderAPI!! Plugin can not work without it!");
        }
		
		plugin = this;
		Bukkit.getPluginManager().registerEvents(new XS_Join_Event(), this);
		Bukkit.getPluginManager().registerEvents(new XS_Death_Event(), this);
		new XS_load_config(); //load config
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
		    @Override
		    public void run() 
		    {
		        for(Player p : Bukkit.getOnlinePlayers())
		        {
		        	if((new XS_setting_player().getConfig().getInt("players." + p.getUniqueId() + ".statics.souls") + new XS_setting_config().getConfig().getInt("settings.recover") <= new XS_setting_player().getConfig().getInt("players." + p.getUniqueId() + ".statics.max_souls")))
		        	{
			        	XS_setting_player.customConfig.set("players." + p.getUniqueId() + ".statics.souls", (new XS_setting_player().getConfig().getInt("players." + p.getUniqueId() + ".statics.souls") + new XS_setting_config().getConfig().getInt("settings.recover")));	
		        	}
		        	else 
		        	{
		        		XS_setting_player.customConfig.set("players." + p.getUniqueId() + ".statics.souls", new XS_setting_player().getConfig().getInt("players." + p.getUniqueId() + ".statics.max_souls"));
		        	}
		        	
		        	if(new XS_setting_config().getConfig().getBoolean("settings.show_recover_messages") == true)
		        	{
			        	p.sendMessage(new XS_setting_messages().getConfig().getString("messages.recover").replace('&', '§').replace("%amount%", ""+new XS_setting_config().getConfig().getInt("settings.recover")));
		        	}
		        }
		        
				try 
				{
					XS_setting_player.customConfig.save(XS_setting_player.customConfigFile);
					XS_soul_core.getPlugin().reloadConfig();
				} 
				catch (IOException ex) 
				{
					ex.printStackTrace();
				}
		    }
		}, 0L, new XS_setting_config().getConfig().getInt("settings.time") * 20L);
	}
	
	public void onDisable()
	{
		try 
		{
			XS_setting_player.customConfig.save(XS_setting_player.customConfigFile);
			XS_soul_core.getPlugin().reloadConfig();
		} 
		catch (IOException ex) 
		{
			ex.printStackTrace();
		}
	}
	
	public static XS_soul_core getPlugin()
	{
		return plugin;
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String commandLabel,String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("souls"))
		{
			if(sender instanceof Player)
			{
				Player p = (Player) sender;
				
				if(args.length == 0)
				{
						p.sendMessage("§a§l§m----------------------------");
						p.sendMessage("§c/souls §fเพื่อดูคำสั่งหน้านี้");
						p.sendMessage("§c/souls check §fเพื่อดูยอดวิญญาญคงเหลือ");
						p.sendMessage("§c/souls check <player> §fเช็คยอดวิญญาญคนอื่น");
						p.sendMessage("§c/souls set <player> <amt> §fตั้งค่ายอดวิญญาญ");
						p.sendMessage("§c/souls setmax <player> <amt> §fตั้งค่าวิญญาญสูงสุด");
						p.sendMessage("§c/souls add <player> <amt> §fเพิ่มค่าวิญญาญให้ผู้เล่น");
						p.sendMessage("§c/souls remove <player> <amt> §fลดค่าวิญญาญผู้เล่น");
						p.sendMessage("§c/souls reload §fรีโหลดระบบวิญญาญ");
						p.sendMessage("§a§l§m----------------------------");
				}
				else if(args.length == 1)
				{
					if(args[0].equalsIgnoreCase("check"))
					{
						if(p.hasPermission("souls.check"))
						{
							p.sendMessage(new XS_setting_messages().getConfig().getString("commands.souls_check").replace('&', '§').replace("%amount%", ""+new XS_setting_player().getConfig().getInt("players." + p.getUniqueId() + ".statics.souls")).replace("%max%", ""+new XS_setting_player().getConfig().getInt("players." + p.getUniqueId() + ".statics.max_souls")));
						}
						else
						{
							p.sendMessage(new XS_setting_messages().getConfig().getString("commands.no_permission").replace('&', '§'));
						}
					}
					else if(args[0].equalsIgnoreCase("reload"))
					{
						if(p.hasPermission("souls.reload") || p.hasPermission("souls.admin"))
						{
							XS_setting_messages.customConfigFile = new File(XS_soul_core.getPlugin().getDataFolder(), "messages.yml");
							XS_setting_player.customConfigFile = new File(XS_soul_core.getPlugin().getDataFolder(), "player_data.yml");
							XS_setting_config.customConfigFile = new File(XS_soul_core.getPlugin().getDataFolder(), "config.yml");
							
							if(!XS_setting_messages.customConfigFile.exists() || !XS_setting_player.customConfigFile.exists() || !XS_setting_config.customConfigFile.exists())
							{
								XS_setting_messages.customConfigFile.getParentFile().mkdirs();
								XS_setting_player.customConfigFile.getParentFile().mkdirs();
								XS_setting_config.customConfigFile.getParentFile().mkdirs();
								
								XS_soul_core.getPlugin().saveResource("messages.yml", false);
								XS_soul_core.getPlugin().saveResource("player_data.yml", false);
								XS_soul_core.getPlugin().saveResource("config.yml", false);
							}
							else
							{
								XS_setting_messages.customConfig = YamlConfiguration.loadConfiguration(XS_setting_messages.customConfigFile);
								XS_setting_player.customConfig = YamlConfiguration.loadConfiguration(XS_setting_player.customConfigFile);
								XS_setting_config.customConfig = YamlConfiguration.loadConfiguration(XS_setting_config.customConfigFile);
								
								try
								{
									XS_setting_messages.customConfig.save(XS_setting_messages.customConfigFile);
									XS_setting_player.customConfig.save(XS_setting_player.customConfigFile);
									XS_setting_config.customConfig.save(XS_setting_config.customConfigFile);
									XS_soul_core.getPlugin().reloadConfig();
								}
								catch(IOException e)
								{
									e.printStackTrace();
								}
							}
							
							p.sendMessage(new XS_setting_messages().getConfig().getString("commands.reload_command").replace('&', '§'));
						}
						else
						{
							p.sendMessage(new XS_setting_messages().getConfig().getString("commands.no_permission").replace('&', '§'));
						}
					}
				}
				else if(args.length == 2)
				{
					if(args[0].equalsIgnoreCase("check"))
					{
						if(p.hasPermission("souls.check.other"))
						{
							Player target = Bukkit.getServer().getPlayer(args[1]);
							
							if(target == null)
							{
								p.sendMessage(new XS_setting_messages().getConfig().getString("commands.player_err").replace('&', '§'));
								return false;
							}
								
							p.sendMessage(new XS_setting_messages().getConfig().getString("commands.souls_check_other").replace('&', '§').replace("%amount%", ""+new XS_setting_player().getConfig().getInt("players." + target.getUniqueId() + ".statics.souls")).replace("%player%", target.getName()).replace("%max%", ""+new XS_setting_player().getConfig().getInt("players." + p.getUniqueId() + ".statics.max_souls")));
						
						}
						else
						{
							p.sendMessage(new XS_setting_messages().getConfig().getString("commands.no_permission").replace('&', '§'));
						}
					}
				}
				else if(args.length == 3)
				{
					if(args[0].equalsIgnoreCase("set"))
					{
						if(p.hasPermission("souls.set") || p.hasPermission("souls.admin"))
						{
							int souls = 0;
							
							try
							{
								souls = Integer.parseInt(args[2]);
							}
							catch(NumberFormatException ex)
							{
								p.sendMessage("" + new XS_setting_messages().getConfig().getString("commands.not_a_number").replace('&', '§'));
								return false;
							}
							
							Player target = Bukkit.getServer().getPlayer(args[1]);
							
							if(target == null)
							{
								p.sendMessage(new XS_setting_messages().getConfig().getString("commands.player_err").replace('&', '§'));
								return false;
							}
							
							if(souls <= 0)
							{
								p.sendMessage(new XS_setting_messages().getConfig().getString("commands.negative_number").replace('&', '§'));
								return false;
							}
							
							XS_setting_player.customConfig.set("players." + target.getUniqueId() + ".statics.souls", souls);
							p.sendMessage(new XS_setting_messages().getConfig().getString("commands.souls_set_complete").replace('&', '§').replace("%player%", target.getName()).replace("%amount%", ""+new XS_setting_player().getConfig().getInt("players." + target.getUniqueId() + ".statics.souls")));
							try 
							{
								XS_setting_player.customConfig.save(XS_setting_player.customConfigFile);
								XS_soul_core.getPlugin().reloadConfig();
							} 
							catch (IOException ex) 
							{
								ex.printStackTrace();
							}
						}
						else
						{
							p.sendMessage(new XS_setting_messages().getConfig().getString("commands.no_permission").replace('&', '§'));
						}
					}
					else if(args[0].equalsIgnoreCase("add"))
					{
						if(p.hasPermission("souls.add") || p.hasPermission("souls.admin"))
						{
							int souls = 0;
							
							try
							{
								souls = Integer.parseInt(args[2]);
							}
							catch(NumberFormatException ex)
							{
								p.sendMessage("" + new XS_setting_messages().getConfig().getString("commands.not_a_number").replace('&', '§'));
								return false;
							}
							
							Player target = Bukkit.getServer().getPlayer(args[1]);
							
							if(target == null)
							{
								p.sendMessage(new XS_setting_messages().getConfig().getString("commands.player_err").replace('&', '§'));
								return false;
							}
							
							if(souls <= 0)
							{
								p.sendMessage(new XS_setting_messages().getConfig().getString("commands.negative_number").replace('&', '§'));
								return false;
							}
							
							XS_setting_player.customConfig.set("players." + target.getUniqueId() + ".statics.souls", (new XS_setting_player().getConfig().getInt("players." + target.getUniqueId() + ".statics.souls")+souls));

							
							p.sendMessage(new XS_setting_messages().getConfig().getString("commands.souls_add_complete").replace('&', '§').replace("%player%", target.getName()).replace("%amount%", ""+souls));
							try 
							{
								XS_setting_player.customConfig.save(XS_setting_player.customConfigFile);
								XS_soul_core.getPlugin().reloadConfig();
							} 
							catch (IOException ex) 
							{
								ex.printStackTrace();
							}
						}
						else
						{
							p.sendMessage(new XS_setting_messages().getConfig().getString("commands.no_permission").replace('&', '§'));
						}
					}
					else if(args[0].equalsIgnoreCase("remove"))
					{
						if(p.hasPermission("souls.remove") || p.hasPermission("souls.admin"))
						{
							int souls = 0;
							
							try
							{
								souls = Integer.parseInt(args[2]);
							}
							catch(NumberFormatException ex)
							{
								p.sendMessage("" + new XS_setting_messages().getConfig().getString("commands.not_a_number").replace('&', '§'));
								return false;
							}
							
							Player target = Bukkit.getServer().getPlayer(args[1]);
							
							if(target == null)
							{
								p.sendMessage(new XS_setting_messages().getConfig().getString("commands.player_err").replace('&', '§'));
								return false;
							}
							
							if(souls <= 0)
							{
								p.sendMessage(new XS_setting_messages().getConfig().getString("commands.negative_number").replace('&', '§'));
								return false;
							}
							
							XS_setting_player.customConfig.set("players." + target.getUniqueId() + ".statics.souls", (new XS_setting_player().getConfig().getInt("players." + target.getUniqueId() + ".statics.souls")-souls));

							
							p.sendMessage(new XS_setting_messages().getConfig().getString("commands.souls_remove_complete").replace('&', '§').replace("%player%", target.getName()).replace("%amount%", ""+souls));
							try 
							{
								XS_setting_player.customConfig.save(XS_setting_player.customConfigFile);
								XS_soul_core.getPlugin().reloadConfig();
							} 
							catch (IOException ex) 
							{
								ex.printStackTrace();
							}
						}
						else
						{
							p.sendMessage(new XS_setting_messages().getConfig().getString("commands.no_permission").replace('&', '§'));
						}
					}
					else if(args[0].equalsIgnoreCase("setmax"))
					{
						if(p.hasPermission("souls.setmax") || p.hasPermission("souls.setmax"))
						{
							int souls = 0;
							
							try
							{
								souls = Integer.parseInt(args[2]);
							}
							catch(NumberFormatException ex)
							{
								p.sendMessage("" + new XS_setting_messages().getConfig().getString("commands.not_a_number").replace('&', '§'));
								return false;
							}
							
							Player target = Bukkit.getServer().getPlayer(args[1]);
							
							if(target == null)
							{
								p.sendMessage(new XS_setting_messages().getConfig().getString("commands.player_err").replace('&', '§'));
								return false;
							}
							
							if(souls <= 0)
							{
								p.sendMessage(new XS_setting_messages().getConfig().getString("commands.negative_number").replace('&', '§'));
								return false;
							}
							
							if(new XS_setting_player().getConfig().getInt("players." + target.getUniqueId() + ".statics.souls") >= souls)
							{
								XS_setting_player.customConfig.set("players." + target.getUniqueId() + ".statics.souls", souls);
								XS_setting_player.customConfig.set("players." + target.getUniqueId() + ".statics.max_souls", souls);
							}
							else
							{
								XS_setting_player.customConfig.set("players." + target.getUniqueId() + ".statics.max_souls", souls);
							}
							
							p.sendMessage(new XS_setting_messages().getConfig().getString("commands.souls_setmax_complete").replace('&', '§').replace("%player%", target.getName()).replace("%amount%", ""+souls));
							
							try 
							{
								XS_setting_player.customConfig.save(XS_setting_player.customConfigFile);
								XS_soul_core.getPlugin().reloadConfig();
							} 
							catch (IOException ex) 
							{
								ex.printStackTrace();
							}
						}
						else
						{
							p.sendMessage(new XS_setting_messages().getConfig().getString("commands.no_permission").replace('&', '§'));
						}
					}
				}
			}
			else
			{
				if(args.length == 3)
				{
					if(args[0].equalsIgnoreCase("set"))
					{

							int souls = 0;
							souls = Integer.parseInt(args[2]);
							
							Player target = Bukkit.getServer().getPlayer(args[1]);
							
							if(target == null)
							{
								return false;
							}
							
							if(souls <= 0)
							{
								return false;
							}
							
							XS_setting_player.customConfig.set("players." + target.getUniqueId() + ".statics.souls", souls);
							
							try 
							{
								XS_setting_player.customConfig.save(XS_setting_player.customConfigFile);
								XS_soul_core.getPlugin().reloadConfig();
							} 
							catch (IOException ex) 
							{
								ex.printStackTrace();
							}
					}
					else if(args[0].equalsIgnoreCase("add"))
					{
							int souls = 0;
							
							try
							{
								souls = Integer.parseInt(args[2]);
							}
							catch(NumberFormatException ex)
							{
								return false;
							}
							
							Player target = Bukkit.getServer().getPlayer(args[1]);
							
							if(target == null)
							{
								return false;
							}
							
							
							XS_setting_player.customConfig.set("players." + target.getUniqueId() + ".statics.souls", (new XS_setting_player().getConfig().getInt("players." + target.getUniqueId() + ".statics.souls")+souls));

							try 
							{
								XS_setting_player.customConfig.save(XS_setting_player.customConfigFile);
								XS_soul_core.getPlugin().reloadConfig();
							} 
							catch (IOException ex) 
							{
								ex.printStackTrace();
							}
					}
					else if(args[0].equalsIgnoreCase("remove"))
					{
							int souls = 0;
							
							try
							{
								souls = Integer.parseInt(args[2]);
							}
							catch(NumberFormatException ex)
							{
								return false;
							}
							
							Player target = Bukkit.getServer().getPlayer(args[1]);
							
							if(target == null)
							{
								return false;
							}
							
							
							XS_setting_player.customConfig.set("players." + target.getUniqueId() + ".statics.souls", (new XS_setting_player().getConfig().getInt("players." + target.getUniqueId() + ".statics.souls")-souls));

							
							try 
							{
								XS_setting_player.customConfig.save(XS_setting_player.customConfigFile);
								XS_soul_core.getPlugin().reloadConfig();
							} 
							catch (IOException ex) 
							{
								ex.printStackTrace();
							}
					}
					else if(args[0].equalsIgnoreCase("setmax"))
					{
							int souls = 0;
							
							try
							{
								souls = Integer.parseInt(args[2]);
							}
							catch(NumberFormatException ex)
							{
								return false;
							}
							
							Player target = Bukkit.getServer().getPlayer(args[1]);
							
							if(target == null)
							{
								return false;
							}
							
							if(souls <= 0)
							{
								return false;
							}
							
							if(new XS_setting_player().getConfig().getInt("players." + target.getUniqueId() + ".statics.souls") >= souls)
							{
								XS_setting_player.customConfig.set("players." + target.getUniqueId() + ".statics.souls", souls);
								XS_setting_player.customConfig.set("players." + target.getUniqueId() + ".statics.max_souls", souls);
							}
							else
							{
								XS_setting_player.customConfig.set("players." + target.getUniqueId() + ".statics.max_souls", souls);
							}
							
							try 
							{
								XS_setting_player.customConfig.save(XS_setting_player.customConfigFile);
								XS_soul_core.getPlugin().reloadConfig();
							} 
							catch (IOException ex) 
							{
								ex.printStackTrace();
							}
					}
				}
			}
		}
		
		return true;
	}
}
