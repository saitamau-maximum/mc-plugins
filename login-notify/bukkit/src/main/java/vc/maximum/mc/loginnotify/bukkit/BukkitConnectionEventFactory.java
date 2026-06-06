package vc.maximum.mc.loginnotify.bukkit;

import java.net.InetSocketAddress;
import java.time.Instant;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import vc.maximum.mc.loginnotify.core.ConnectionEvent;
import vc.maximum.mc.loginnotify.core.ConnectionEventType;

public final class BukkitConnectionEventFactory {

    private BukkitConnectionEventFactory() {}

    public static ConnectionEvent fromJoin(String serverName, Player player) {
        return new ConnectionEvent(
                ConnectionEventType.JOIN,
                player.getName(),
                player.getUniqueId(),
                resolveAddress(player),
                Bukkit.getOnlinePlayers().size(),
                serverName,
                Instant.now());
    }

    public static ConnectionEvent fromQuit(String serverName, Player player) {
        return new ConnectionEvent(
                ConnectionEventType.QUIT,
                player.getName(),
                player.getUniqueId(),
                resolveAddress(player),
                Math.max(0, Bukkit.getOnlinePlayers().size() - 1),
                serverName,
                Instant.now());
    }

    private static String resolveAddress(Player player) {
        InetSocketAddress address = player.getAddress();
        if (address == null || address.getAddress() == null) {
            return "(unknown)";
        }
        return address.getAddress().getHostAddress();
    }
}
