package io.github.ntoskrnl4;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.xml.soap.Text;
import java.util.Arrays;

public class TabListTPS implements Runnable {

	// todo: everything in here
    // https://bukkit.gamepedia.com/Scheduler_Programming

    @Override
    public void run() {
        // data[0]: current TPS
        // data[1]: unbounded TPS
        // data[2]: last 10 avg time

        double[] data = TickTimerTask.TPSValues();
        int n_chunks = ChunkCounter.getTotalChunkCount();
        int n_entity = 0;
        for (World world: Bukkit.getWorlds()) {
            n_entity += world.getEntities().size();
        }

        TextComponent[] lines = new TextComponent[4];

        lines[0] = new TextComponent("\nCurrent TPS: ");
        TextComponent tps_val = new TextComponent(String.valueOf(data[0]));
        tps_val.setColor(StatisticsCommand.TPSColor(data[0]));
        lines[0].addExtra(tps_val);
        lines[0].setColor(ChatColor.GOLD);

        lines[1] = new TextComponent("\nLast 10 Avg: ");
        TextComponent avg_val = new TextComponent(String.valueOf(data[2]));
        avg_val.setColor(StatisticsCommand.AvgTickColor(data[2]));
        lines[1].addExtra(avg_val);
        lines[1].setColor(ChatColor.GOLD);

        lines[2] = new TextComponent("\nChunk Count: ");
        TextComponent c_val = new TextComponent(String.valueOf(n_chunks));
        c_val.setColor(ChatColor.GRAY);
        lines[2].addExtra(c_val);
        lines[2].setColor(ChatColor.AQUA);

        lines[3] = new TextComponent("\nEntity Count: ");
        TextComponent e_val = new TextComponent(String.valueOf(n_entity));
        e_val.setColor(StatisticsCommand.AvgTickColor(n_entity/12.0));
        lines[3].addExtra(e_val);
        lines[3].setColor(ChatColor.AQUA);

        for (Player player: Bukkit.getOnlinePlayers()) {
            player.setPlayerListHeaderFooter(
                    new TextComponent[]{
                            new TextComponent("")
                    },
                    lines
            );
        }
    }
}
