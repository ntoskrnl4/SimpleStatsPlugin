package io.github.ntoskrnl4;

import org.bukkit.Bukkit;

public class TickTimerTask {

	public static double avgTickTime() {
		// Averaging algorithm (of the last couple of ticks)
		// Returns in the scale of seconds
		long[] tickTimes = Bukkit.getTickTimes();
		long sum = 0;

		for (int i=0; i < 10; i++) {
			sum += tickTimes[i];
		}
		return (sum / 10.0) / 1e9;
	}

	public static double[] TPSValues() {
		// One tick is 50ms, so Ticks Per Second is just the inverse of the time per tick.
		// The game never runs faster than 20 TPS so cap it at 20
		double avgTickTime = avgTickTime();
		double[] results = new double[3];

		results[0] = Math.round(100*Math.min(20.0, 1/avgTickTime))/100.0;  // Actual TPS
		results[1] = Math.round(100*(1/avgTickTime))/100.0;  // Uncapped TPS
		results[2] = Math.round(100*(avgTickTime*1000))/100.0;
		return results;
	}

	public static double[] latestTickTimes(boolean asMilliseconds, int length) {
		long[] latest_ticks = Bukkit.getTickTimes();
		double[] values = new double[length];
		for (int i=0; i < length; i++) {
			if (asMilliseconds) {
				values[i] = (int)(latest_ticks[i]/1000) / 1e3;
				// Divide from nanoseconds to microseconds, then divide to milliseconds (which is rounded to microsecond level)
			} else {
				values[i] = (int)(latest_ticks[i]/1000) / 1e6;
				// Divide from nanoseconds to microseconds, then divide to seconds (which is rounded to microsecond level)
			}
		}
		return values;
	}
}
