package ru.exsite.status;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;


public class Bungee extends Plugin implements Listener {

	public static int timer;
	public static List<Integer> Groups;
	public static String Token;
	public static int Record;
	public static long TimeRecord;
	public static String Status;
	public static String RecordFormatDate;
	public static Bungee plugin;
	public static int online;
	public Configuration config;
	public static int TempRecord;
	public static long TempRecordTime;
	public static String TempFormatDate;
	public static long lastupdate;

	public void onEnable() {
		plugin = this;
		getProxy().getPluginManager().registerListener(this, this);
        getProxy().getPluginManager().registerCommand(this, new Command("exsitestatus", null, new String[] { "status", "exstatus" }) {

			@Override
            public void execute(CommandSender sender, String[] args) {
            	if (args.length == 0) {
        			if(!sender.hasPermission("ExsiteStatus.help")) {
        				sender.sendMessage("§cExsiteStatus §8» §6У Вас не достаточно прав.");
        				return;
        			}
        			sender.sendMessage("§cExsiteStatus §8» §6Помощь по командами.");
        			sender.sendMessage("");
        			sender.sendMessage("§c/exstatus info §8- §6Информация по онлайну");
        			sender.sendMessage("§c/exstatus update §8- §6Обновить данные");
        			sender.sendMessage("§c/exstatus reload §8- §6Перезапустить конфигурации");
        			sender.sendMessage("");
        		}
        		else {
        			switch (args[0].toLowerCase()) {
        			case "i":
        			case "info":
        				if(!sender.hasPermission("ExsiteStatus.info")) {
        					sender.sendMessage("§cExsiteStatus §8» §6У Вас не достаточно прав.");
        					return;
        				}
        				sender.sendMessage("§cExsiteStatus §8» §6Информация.");
        				sender.sendMessage("");
        				sender.sendMessage("§6Онлайн сервера: §c"+online);
        				sender.sendMessage("");
        				sender.sendMessage("§6Рекорд онлайна: §c"+Record);
        				sender.sendMessage("§6Зафиксирован рекорд: §c"+Utils.getUnixTime(TimeRecord, RecordFormatDate));
        				sender.sendMessage("");
        				sender.sendMessage("§6Суточный рекорд: §c"+TempRecord);
        				sender.sendMessage("§6Зафиксирован рекорд: §c"+Utils.getUnixTime(TempRecordTime, TempFormatDate));
        				sender.sendMessage("");
        				break;
        			case "update":
        				if(!sender.hasPermission("ExsiteStatus.update")) {
        					sender.sendMessage("§cExsiteStatus §8» §6У Вас не достаточно прав.");
        					return;
        				}
        				update();
        				sender.sendMessage("§cExsiteStatus §8» §6Данные успешно обновлены.");
        				break;
        			case "rl":
        			case "reload":
        				if(!sender.hasPermission("ExsiteStatus.reload")) {
        					sender.sendMessage("§cExsiteStatus §8» §6У Вас не достаточно прав.");
        					return;
        				}
        				saveDefaultConfig();
        				loadConfig();
        				sender.sendMessage("§cExsiteStatus §8» §6Конфигурации успешно перезагружены.");
        				break;
        			}
        		}
            }
        });
		saveDefaultConfig();
		loadConfig();
		getProxy().getScheduler().schedule(this, new Runnable() {
			public void run() {
				update();
            }
		}, 0L, timer, TimeUnit.SECONDS);
	}

	private void saveDefaultConfig() {
        try {
            if(!this.getDataFolder().exists()) {
               this.getDataFolder().mkdir();
            }

            File file = new File(this.getDataFolder(), "config.yml");
            ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
            if(!file.exists()) {
               provider.save(provider.load(this.getResourceAsStream("config.yml")), file);
            }

            this.config = provider.load(file);
            provider.save(this.config, file);
         } catch (Exception e) {
            e.printStackTrace();
         }
	}

	public void update() {
		try {
			online = plugin.getProxy().getOnlineCount();
			if(online > Record) {
				Record = online;
				config.set("Record.number", online);
				TimeRecord = System.currentTimeMillis();
				config.set("Record.time", TimeRecord);
				ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(this.getDataFolder(), "config.yml"));
			}
			if(!Utils.getUnixTime(System.currentTimeMillis(), TempFormatDate).equals(Utils.getUnixTime(lastupdate, TempFormatDate))) {
				TempRecord = online;
				config.set("TempRecord.number", online);
				TempRecordTime = System.currentTimeMillis();
				config.set("TempRecord.time", TempRecordTime);
				lastupdate = System.currentTimeMillis();
				config.set("TempRecord.lastupdate", lastupdate);
				ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(this.getDataFolder(), "config.yml"));
			} else {
				if(online > TempRecord) {
					TempRecord = online;
					config.set("TempRecord.number", online);
					TempRecordTime = System.currentTimeMillis();
					config.set("TempRecord.time", TempRecordTime);
					ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(this.getDataFolder(), "config.yml"));
				}
			}
			String msg = Status.replace("%online", ""+online).replace("%temprecord", ""+TempRecord).replace("%temprectime", ""+Utils.getUnixTime(TempRecordTime, TempFormatDate)).replace("%record", ""+Record).replace("%rectime", Utils.getUnixTime(TimeRecord, RecordFormatDate));
			for(int r:Groups) {
				Utils.setStatus(msg, r, Token);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadConfig() {
		timer = config.getInt("Timer");
		Groups = config.getIntList("Groups");
		Token = config.getString("Token");
		Record = config.getInt("Record.number");
		TimeRecord = config.getLong("Record.time");
		Status = config.getString("Status");
		RecordFormatDate = config.getString("Record.FormatDate");
		TempRecord = config.getInt("TempRecord.number");
		TempRecordTime = config.getLong("TempRecord.time");
		TempFormatDate = config.getString("TempRecord.FormatDate");
		lastupdate = config.getLong("TempRecord.lastupdate");
	}
}