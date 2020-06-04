package net.xsapi.panat.event;

import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.xsapi.panat.main.XS_soul_core;
import net.xsapi.panat.setting.XS_setting_config;
import net.xsapi.panat.setting.XS_setting_player;

public class XS_Join_Event implements Listener 
{
	@EventHandler
	public void InsertPlayer(PlayerJoinEvent e)
	{
		Player p = e.getPlayer();
		if(XS_setting_player.customConfig.getConfigurationSection("players." + p.getUniqueId()) == null)
		{
			XS_setting_player.customConfig.set("players." + p.getUniqueId() + ".information.name", p.getName());
			XS_setting_player.customConfig.set("players." + p.getUniqueId() + ".statics.souls", 5);
			XS_setting_player.customConfig.set("players." + p.getUniqueId() + ".statics.max_souls", new XS_setting_config().getConfig().getInt("settings.default_max_souls"));
			
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
			if(XS_setting_player.customConfig.getConfigurationSection("players." + p.getUniqueId() + ".statics.max_souls") == null)
			{
				XS_setting_player.customConfig.set("players." + p.getUniqueId() + ".statics.max_souls", new XS_setting_config().getConfig().getInt("settings.default_max_souls"));
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
