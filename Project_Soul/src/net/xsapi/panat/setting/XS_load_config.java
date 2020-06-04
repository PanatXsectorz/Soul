package net.xsapi.panat.setting;

public class XS_load_config 
{
	public XS_load_config()
	{
		new XS_setting_player().loadConfigu();
		new XS_setting_messages().loadConfigu();
		new XS_setting_config().loadConfigu();
	}
}
