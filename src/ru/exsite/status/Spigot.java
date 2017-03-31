package ru.exsite.status;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;



public class Spigot extends JavaPlugin implements Listener {
	
	public static int timer;
	public static List<Integer> Groups;
	public static String Token;
	public static int Record;
	public static long TimeRecord;
	public static String Status;
	public static String FormatDate;
	public static Spigot plugin;
	private static int online;
	
	public void onEnable() {
		plugin = this;
		Bukkit.getPluginManager().registerEvents(this, this);
		this.getCommand("exsitestatus").setExecutor(this);
		saveDefaultConfig();
		loadConfig();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				update();
            }
        }, 0L, timer*20L);
	}
	
	public void update() {
		for(int r:Groups) {
			try {
				online = Bukkit.getOnlinePlayers().size();
				if(online > Record) {
					Record = online;
					plugin.getConfig().set("Record.number", online);
					TimeRecord = System.currentTimeMillis();
					plugin.getConfig().set("Record.time", TimeRecord);
					plugin.saveConfig();
				}
				String msg = Status.replace("%online", ""+online).replace("%record", ""+Record).replace("%rectime", Utils.getUnixTime(TimeRecord, FormatDate));
				Utils.setStatus(msg, r, Token);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void loadConfig() {
		timer = this.getConfig().getInt("Timer");
		Groups = this.getConfig().getIntegerList("Groups");
		Token = this.getConfig().getString("Token");
		Record = this.getConfig().getInt("Record.number");
		TimeRecord = this.getConfig().getLong("Record.time");
		Status = this.getConfig().getString("Status");
		FormatDate = this.getConfig().getString("Record.FormatDate");
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			if(!sender.hasPermission("ExsiteStatus.help")) {
				sender.sendMessage("§cExsiteStatus §8» §6У Вас не достаточно прав.");
				return true;
			}
			sender.sendMessage("§cExsiteStatus §8» §6Помощь по командами.");
			sender.sendMessage("");
			sender.sendMessage("§c/"+label+" info §8- §6Информация по онлайну");
			sender.sendMessage("§c/"+label+" update §8- §6Обновить данные");
			sender.sendMessage("§c/"+label+" reload §8- §6Перезапустить конфигурации");
			sender.sendMessage("");
		}
		else {
			switch (args[0].toLowerCase()) {
			case "i":
			case "info":
				if(!sender.hasPermission("ExsiteStatus.info")) {
					sender.sendMessage("§cExsiteStatus §8» §6У Вас не достаточно прав.");
					return true;
				}
				sender.sendMessage("§cExsiteStatus §8» §6Информация.");
				sender.sendMessage("");
				sender.sendMessage("§6Онлайн сервера: §c"+online);
				sender.sendMessage("§6Рекорд онлайна: §c"+Record);
				sender.sendMessage("§6Защитан рекорд: §c"+Utils.getUnixTime(TimeRecord, FormatDate));
				sender.sendMessage("");
				break;
			case "update":
				if(!sender.hasPermission("ExsiteStatus.update")) {
					sender.sendMessage("§cExsiteStatus §8» §6У Вас не достаточно прав.");
					return true;
				}
				update();
				sender.sendMessage("§cExsiteStatus §8» §6Данные успешно обновлены.");
				break;
			case "rl":
			case "reload":
				if(!sender.hasPermission("ExsiteStatus.reload")) {
					sender.sendMessage("§cExsiteStatus §8» §6У Вас не достаточно прав.");
					return true;
				}
				this.reloadConfig();
				loadConfig();
				sender.sendMessage("§cExsiteStatus §8» §6Конфигурации успешно перезагружены.");
				break;
			}
		}
		return false;
	}
}
