package io.github.ntoskrnl4;

import java.util.Arrays;

import org.bukkit.ChatColor;
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

		if(args.length == 0) {
			// When in doubt,
			// RETURN EVERYTHING

			String[] systemInfo = systemInfo();
			String[] tickInfo = tickInfo();
			String[] chunkInfo = chunkInfo();
			// Unfortunately there's no easy way to concatenate two arrays together, so, do this instead
			String[] out = Arrays.copyOf(systemInfo, systemInfo.length + tickInfo.length + chunkInfo.length);
			System.arraycopy(tickInfo, 0, out, systemInfo.length, tickInfo.length);
			System.arraycopy(chunkInfo, 0, out, systemInfo.length + tickInfo.length, chunkInfo.length);

			sender.sendMessage(out);

		} else if (contains(args[0], systemInfoTriggers)) {
			sender.sendMessage(systemInfo());

		} else if (contains(args[0], tickInfoTriggers)) {
			sender.sendMessage(tickInfo());

		} else if (contains(args[0], chunkInfoTriggers)) {
			sender.sendMessage(chunkInfo());

		} else {
			// If we didn't recognize what they typed, return an error.
			sender.sendMessage(ChatColor.RED+"Unknown subcommand: valid options are \"system\", \"tick\", \"chunks\", or null (no argument)");
		}
		return true;
	}

	public String[] systemInfo() {
		// Get various information about the Java Runtime Environment, such as
		// the current memory usage, the amount of allocated memory (memory set
		// aside to be used), and the peak amount of memory it can use.
		// Also get the number of CPU cores we have
		long process_memory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/(1000*1000);
		long allocated_memory = Runtime.getRuntime().totalMemory()/(1000*1000);
		long available_memory = Runtime.getRuntime().maxMemory()/(1000*1000);
		int available_cpus = Runtime.getRuntime().availableProcessors();
		return new String[]{
				"Memory Usage: "+ process_memory +" MB / "+ allocated_memory +" MB ("+ available_memory +" MB max)",
				"Number of CPUs: "+ available_cpus
		};
	}

	public String[] tickInfo() {
		// Get the current server Ticks per Second to see how well it's running,
		// along with the exact time length of the last few ticks to see how
		// well we're doing (lower is shorter=better).
		return new String[]{
				"Current TPS: "+ TickTimerTask.avgTPS(),
				"Last 5 tick times (ms): "+ Arrays.toString(TickTimerTask.latestTickTimes(true))
		};
	}

	public String[] chunkInfo() {
		Object[][] values = ChunkCounter.getChunkDimensions();
		String[] ret = new String[values[0].length+1];
		ret[0] = "Loaded Chunks: "+ ChunkCounter.getTotalChunkCount();
		for(int i=0; i < values[0].length; i++) {
			ret[i+1] = values[0][i] + ": " + values[1][i];
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

}
