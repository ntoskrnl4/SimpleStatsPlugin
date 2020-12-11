package io.github.ntoskrnl4;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChunkCounter implements Listener {

	private static int totalChunkCount = 0;
	private static final Map<String, Integer> dimensionList = new HashMap<>(8);
	/*
	TLDR: Explanation of HashMap and the loadFactor argument
	A HashMap is equivalent to a dictionary in Python, where key-value pairs are
	stored such that one key maps to one value. Keys and values can be any object
	in Java, such as Strings, Doubles, even custom classes like minecraft.World,
	and each key has one value (which could be any other object).

	The first argument defines its initial capacity of 8 objects.

	In order to make it easier for computers to process, keys are "hashed", so
	that it's all one easy type to deal with. Hashes represent one, single, unique
	object in the entire (Java program's) world. Internally, key-value pairs are
	stored in "buckets" depending on the key's hash. This makes it quick when
	there are lots of objects in the HashMap - it doesn't have to check the key
	against every single one, but now just against ones that are in the same hash
	bucket. However, at some point, those buckets will get a bit full. To
	optimize efficiency, Java will then recalculate ALL hashes and redistribute
	them into more buckets than before (so that it's still only checking against
	a few objects in one bucket, rather than against 50 objects in the entire
	table). The point that it will expand the table and redistribute buckets is
	called the loadFactor, and is a factor of the table's current vs. max capacity.
	For example, with 8 items and a load factor of 0.5, after 4 items are put in
	it will expand the table and reorganize the table internally to keep it
	efficient when doing table operations (getting keys, adding keys, etc).
	*/

	@EventHandler
	public static void addLoadedChunk(ChunkLoadEvent event) {
		totalChunkCount += 1;

		String worldName = event.getWorld().getName();
		Integer current = dimensionList.get(worldName);
		if (current == null) {
			current = 0;
		}
		dimensionList.put(worldName, current+1);
		// Add one to this dimension's chunk count
	}

	@EventHandler
	public static void removeUnloadedChunk(ChunkUnloadEvent event) {
		totalChunkCount -= 1;

		String worldName = event.getWorld().getName();
		dimensionList.put(worldName, dimensionList.get(worldName)-1);
		// Subtract one from this dimension's chunk count
	}

	public static int getTotalChunkCount() {
		return totalChunkCount;
	}

	/**
	Get the number of chunks in each dimension. The result will be a
	rectangular two-dimensional array that looks something like this:

	type: { String[] , int[] }

	{{"world", "world_nether", "world_the_end"},
	 {  621,           16,            256     }}

	where world names are in the first array and chunk counts are in the second.
	*/
	public static Object[][] getChunkDimensions() {
		ArrayList<String> names = new ArrayList<>();
		ArrayList<Integer> counts = new ArrayList<>();
		for (Map.Entry<String, Integer> entry: dimensionList.entrySet()) {
			names.add(entry.getKey());
			counts.add(entry.getValue());
		}
		return new Object[][]{names.toArray(), counts.toArray()};
	}
}
