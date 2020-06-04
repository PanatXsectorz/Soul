package net.xsapi.panat.event;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import net.xsapi.panat.setting.XS_setting_config;
import net.xsapi.panat.setting.XS_setting_messages;
import net.xsapi.panat.setting.XS_setting_player;

public class XS_Death_Event implements Listener
{

	@EventHandler
	public void onDeath(PlayerDeathEvent e)
	{
		Player p = e.getEntity();
		
		int souls_left = new XS_setting_player().getConfig().getInt("players." + p.getUniqueId() + ".statics.souls") - new XS_setting_config().getConfig().getInt("settings.deaths");
		
		if(souls_left < 0)
		{
			souls_left = 0;
		}
		
		//p.sendMessage("" + p.getInventory().getItemInMainHand());
		
		if(new XS_setting_player().getConfig().getInt("players." + p.getUniqueId() + ".statics.souls") < 1)
		{
			if(new XS_setting_config().getConfig().getBoolean("settings.show_deaths_warning") == true)
			{
				p.sendMessage(new XS_setting_messages().getConfig().getString("messages.warn_deaths").replace('&', '§'));
			}
			
			if(p.getInventory().getItemInMainHand() != null)
			{
				
				if(p.getInventory().getItemInMainHand().getType() != Material.AIR)
				{
					p.getWorld().dropItem(p.getLocation(), p.getInventory().getItemInMainHand());
				}
				
				p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
			}
			
			if(p.getInventory().getHelmet() != null)
			{
				p.getWorld().dropItem(p.getLocation(), p.getInventory().getHelmet());
				p.getInventory().setHelmet(new ItemStack(Material.AIR));
			}
			
			if(p.getInventory().getChestplate() != null)
			{
				p.getWorld().dropItem(p.getLocation(), p.getInventory().getChestplate());
				p.getInventory().setChestplate(new ItemStack(Material.AIR));
			}
			
			if(p.getInventory().getLeggings() != null)
			{
				p.getWorld().dropItem(p.getLocation(), p.getInventory().getLeggings());
				p.getInventory().setLeggings(new ItemStack(Material.AIR));
			}
			
			if(p.getInventory().getBoots() != null)
			{
				p.getWorld().dropItem(p.getLocation(), p.getInventory().getBoots());
				p.getInventory().setBoots(new ItemStack(Material.AIR));
			}
		}
		else
		{
			if(new XS_setting_config().getConfig().getBoolean("settings.show_deaths_messages") == true)
			{
				p.sendMessage(new XS_setting_messages().getConfig().getString("messages.deaths").replace('&', '§').replace("%amount%", ""+new XS_setting_config().getConfig().getInt("settings.deaths")).replace("%left%", ""+souls_left));	
			}
		}
		
		XS_setting_player.customConfig.set("players." + p.getUniqueId() + ".statics.souls", souls_left);

	}
}
