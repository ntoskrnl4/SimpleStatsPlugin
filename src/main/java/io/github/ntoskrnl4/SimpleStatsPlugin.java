package io.github.ntoskrnl4;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class SimpleStatsPlugin extends JavaPlugin {

	@Override
	public void onEnable() {
		//getLogger().info("Inside the onEnable function");

		this.getCommand("stats").setExecutor(new StatisticsCommand());

		getServer().getPluginManager().registerEvents(new TickTimerTask(), this);
		getServer().getPluginManager().registerEvents(new ChunkCounter(), this);

		getServer().getScheduler().runTaskTimer(this, new TabListTPS(), 100, 5);
	}

	@Override
	public void onDisable() {
		//getLogger().info("Inside the onDisable function");
	}
}
