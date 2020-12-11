package io.github.ntoskrnl4;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TickTimerTask implements Listener {

	// The plugin's class is never actually created, so everything runs in a static context.
	// Throw static on everything!
	private static long tickStartTime = 0;
	private static long tickFinishTime = 0;
	private static double[] latest_ticks = {0.0, 0.0, 0.0, 0.0, 0.0};

	@EventHandler
	public static void startTickTiming(ServerTickStartEvent event) {
		// Get the EXACT time, to the nanosecond, we started processing this tick
		tickStartTime = System.nanoTime();
	}

	@EventHandler
	public static void finishTickTiming(ServerTickEndEvent event) {
		// Get the EXACT time, to the nanosecond, we finished processing this tick
		// Then figure out how long it took/.,>?,/,
		tickFinishTime = System.nanoTime();
		calculateTick();
	}

	public static void calculateTick() {
		double tick_time = (tickFinishTime-tickStartTime)/1e9;
		latest_ticks[4] = latest_ticks[3];
		latest_ticks[3] = latest_ticks[2];  // this is faster than using an ArrayList and continually
		latest_ticks[2] = latest_ticks[1];  // adding and removing elements on it to keep it of size 5
		latest_ticks[1] = latest_ticks[0];  // if anyone has a faster solution let me know
		latest_ticks[0] = tick_time;
	}

	public static double avgTickTime() {
		// Averaging algorithm (of the last couple of ticks)
		// Returns in the scale of seconds
		double sum = 0.0;
		for (double i: latest_ticks) {
			sum += i;
		}
		return sum / 5;
	}

	public static double[] TPSValues() {
		// One tick is 50ms, so Ticks Per Second is just the inverse of the time per tick.
		// The game never runs faster than 20 TPS so cap it at 20
		double avgTickTime = avgTickTime();
		double[] results = new double[3];

		results[0] = Math.round(100*Math.min(20.0, 1/avgTickTime))/100.0;  // Actual TPS
		results[1] = Math.round(100*(1/avgTickTime()))/100.0;  // Uncapped TPS
		results[2] = Math.round(100*(avgTickTime()*1000))/100.0;
		return results;
	}

	public static double[] latestTickTimes(boolean asMilliseconds) {
		double[] values = new double[5];
		for (int i=0; i<latest_ticks.length; i++) {
			if (asMilliseconds) {
				values[i] = (int)(latest_ticks[i]*1e6)/1e3;
				// Cast from seconds to microseconds, then divide to milliseconds (which is rounded to microsecond level)
			} else {
				values[i] = (int)(latest_ticks[i]*1e6)/1e6;
				// Cast from seconds to microseconds, then divide back to seconds (which is rounded to microsecond level)
			}
		}
		return values;
	}
}
