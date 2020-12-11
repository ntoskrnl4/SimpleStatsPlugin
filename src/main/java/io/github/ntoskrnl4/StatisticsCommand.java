package io.github.ntoskrnl4;

import java.util.Arrays;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StatisticsCommand implements CommandExecutor {

	String[] systemInfoTriggers = {"system", "cpu", "memory"};
	String[] tickInfoTriggers = {"tick", "ticks", "physics", "engine", "game", "lag"};
	String[] chunkInfoTriggers = {"chunk", "chunks", "world", "worlds", "loaded"};

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// sender: Object that you can .sendMessage() and it will be sent back to whoever ran the command
		// command: Command object that was ran by the user
		// label: The name the player used for the command, in case it was actually an alias
		// args: String[] of arguments the player used on the command (eg. various subcommands)

		if (args.length == 0) {
			// When in doubt,
			// RETURN EVERYTHING

			TextComponent[] systemInfo = systemInfo(false);
			TextComponent[] tickInfo = tickInfo(true);
			TextComponent[] chunkInfo = chunkInfo(true);

			TextComponent[] msg = Arrays.copyOf(systemInfo, systemInfo.length + tickInfo.length + chunkInfo.length);
			System.arraycopy(tickInfo, 0, msg, systemInfo.length, tickInfo.length);
			System.arraycopy(chunkInfo, 0, msg, systemInfo.length + tickInfo.length, chunkInfo.length);

			sender.sendMessage(msg);

		} else if (contains(args[0], systemInfoTriggers)) {
			sender.sendMessage(systemInfo(false));

		} else if (contains(args[0], tickInfoTriggers)) {
			sender.sendMessage(tickInfo(false));

		} else if (contains(args[0], chunkInfoTriggers)) {
			sender.sendMessage(chunkInfo(false));

		} else {
			// If we didn't recognize what they typed, return an error.
			sender.sendMessage(ChatColor.RED+"Unknown subcommand: valid options are \"system\", \"tick\", \"chunks\", or null (no argument)");
		}
		return true;
	}

	public TextComponent[] systemInfo(boolean leadingNewline) {
		// Get various information about the Java Runtime Environment, such as
		// the current memory usage, the amount of allocated memory (memory set
		// aside to be used), and the peak amount of memory it can use.
		// Also get the number of CPU cores we have
		long process_memory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/(1000*1000);
		long allocated_memory = Runtime.getRuntime().totalMemory()/(1000*1000);
		long available_memory = Runtime.getRuntime().maxMemory()/(1000*1000);
		int available_cpus = Runtime.getRuntime().availableProcessors();

		String lead = leadingNewline ? "\n" : "";

		TextComponent msg = new TextComponent(lead + "Memory Usage: ");
		msg.setColor(ChatColor.GOLD);
		TextComponent process_string = new TextComponent(process_memory +" MB / "+ allocated_memory +" MB ("+ available_memory +" MB max)");
		process_string.setColor(ChatColor.GRAY);
		msg.addExtra(process_string);

		TextComponent cpus = new TextComponent("\nNumber of CPUs: ");
		cpus.setColor(ChatColor.GOLD);
		TextComponent count = new TextComponent(String.valueOf(available_cpus));
		count.setColor(ChatColor.GRAY);
		cpus.addExtra(count);

		return new TextComponent[]{msg, cpus};
	}

	public TextComponent[] tickInfo(boolean leadingNewline) {
		// Get the current server Ticks per Second to see how well it's running,
		// along with the exact time length of the last few ticks to see how
		// well we're doing (lower is shorter=better).
		double[] data = TickTimerTask.TPSValues();
		String hover_text = detailedTPS(data);

		String lead = leadingNewline ? "\n" : "";

		TextComponent msg = new TextComponent(lead + "Current TPS: ");
		msg.setColor(ChatColor.GOLD);
		TextComponent l1p2 = new TextComponent(String.valueOf(data[0]));
		l1p2.setColor(TPSColor(data[0]));
		msg.addExtra(l1p2);
		msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hover_text)));

		TextComponent line2 = new TextComponent("\nAverage Tick (last 5): ");
		line2.setColor(ChatColor.GOLD);
		TextComponent l2p2 = new TextComponent(data[2] + " ms");
		l2p2.setColor(AvgTickColor(data[2]));
		line2.addExtra(l2p2);
		line2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hover_text)));

		return new TextComponent[]{msg, line2};
	}

	public TextComponent[] chunkInfo(boolean leadingNewline) {
		Object[][] values = ChunkCounter.getChunkDimensions();
		TextComponent[] ret = new TextComponent[values[0].length+1];

		String lead = leadingNewline ? "\n" : "";

		TextComponent chunkText = new TextComponent(lead + "Loaded Chunks: ");
		chunkText.setColor(ChatColor.AQUA);
		TextComponent chunkCount = new TextComponent(String.valueOf(ChunkCounter.getTotalChunkCount()));
		chunkCount.setColor(ChatColor.LIGHT_PURPLE);
		chunkText.addExtra(chunkCount);
		ret[0] = chunkText;

		for(int i=0; i < values[0].length; i++) {
			TextComponent world = new TextComponent("\n" + values[0][i] + ": ");
			world.setColor(ChatColor.DARK_AQUA);
			TextComponent value = new TextComponent(String.valueOf(values[1][i]));
			value.setColor(ChatColor.DARK_PURPLE);
			world.addExtra(value);
			ret[i+1] = world;
		}
		return ret;
	}

	private boolean contains(String test, String[] list) {
		for (String item: list) {
			if (item.equals(test))
				return true;
		}
		return false;
	}

	private String detailedTPS(double[] data) {
		String text = "Current Server TPS: " + data[0] +
			 "\nLast 5 Ticks Average: " + data[2] + " ms" +
			 "\nEffective Uncapped TPS: " + data[1] +
		   "\n\nLast 5 ticks, most recent first:";
		for (double t: TickTimerTask.latestTickTimes(true)) {
			text += "\n"+t+" ms";
		}
		return text;
	}

	private ChatColor TPSColor(double tps) {
		if (tps > 19.75) return ChatColor.GREEN;
		if (tps > 18.0) return ChatColor.AQUA;
		if (tps > 16.0) return ChatColor.YELLOW;
		if (tps > 13.0) return ChatColor.GOLD;
		if (tps > 10.0) return ChatColor.RED;
		return ChatColor.DARK_RED;
	}

	private ChatColor AvgTickColor(double tick) {
		if (tick < 30.00) return ChatColor.GREEN;
		if (tick < 50.00) return ChatColor.YELLOW;
		if (tick < 75.00) return ChatColor.GOLD;
		if (tick < 100.00) return ChatColor.RED;
		return ChatColor.DARK_RED;
	}

}
