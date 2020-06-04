package net.xsapi.panat.setting;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import net.xsapi.panat.main.XS_soul_core;


public class XS_setting_messages 
{
	public static File customConfigFile;
	public static FileConfiguration customConfig;

	@SuppressWarnings("static-access")
	public FileConfiguration getConfig() 
	{
		return this.customConfig;
	}

	public void loadConfigu() 
	{
		customConfigFile = new File(XS_soul_core.getPlugin().getDataFolder(), "messages.yml");
		if (!customConfigFile.exists()) {
			customConfigFile.getParentFile().mkdirs();
			XS_soul_core.getPlugin().saveResource("messages.yml", false);
		} else {
		}

		customConfig = new YamlConfiguration();
		try {
			customConfig.load(customConfigFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}

	}
	public void save()
	{
		customConfigFile = new File(XS_soul_core.getPlugin().getDataFolder(), "messages.yml");
		try {
			customConfig.options().copyDefaults(true);
			customConfig.save(customConfigFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
