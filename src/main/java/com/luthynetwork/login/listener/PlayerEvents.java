package com.luthynetwork.login.listener;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import com.luthynetwork.core.events.AccountLoadEvent;
import com.luthynetwork.core.libs.util.title.TitleAPI;
import com.luthynetwork.login.LuthyLogin;
import com.luthynetwork.login.utils.Message;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.atomic.AtomicInteger;

public class PlayerEvents implements Listener {

    @EventHandler
    public void onJoin(AccountLoadEvent loadEvent) {
        PlayerJoinEvent event = loadEvent.getJoinEvent();

        val service = LuthyLogin.getService();
        val player = event.getPlayer();
        val uuid = player.getUniqueId();

        player.setGameMode(GameMode.ADVENTURE);
        event.setJoinMessage(null);

        if (!service.isLogged(uuid)) {
            if (service.isRegistered(uuid)) {
                TitleAPI.sendTitle(player, 1, 120 * 20, 1, "§eLuthy Network", "Use /logar (senha).");
            } else {
                TitleAPI.sendTitle(player, 1, 120 * 20, 1, "§eLuthy Network", "Use /registrar (senha) (senha).");
            }
        }

        Bukkit.getOnlinePlayers().forEach(online -> online.hidePlayer(player));
        player.getInventory().clear();

        AtomicInteger i = new AtomicInteger(120);
        new BukkitRunnable() {
            @Override
            public void run() {
                i.decrementAndGet();

                if (player.isOnline()) {
                    if (!service.isLogged(player.getUniqueId())) {
                        ActionBarAPI.sendActionBar(player, "§cRestam apenas " + i + " segundo(s) para se autenticar.");

                        if (i.get() == 0) player.kickPlayer(Message.INTERNAL_PREFIX + "§cVocê excedeu o limite máximo de tempo (120s).");
                    }
                } else cancel();
            }
        }.runTaskTimer(LuthyLogin.getInstance(), 20L, 20L);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onQuit(PlayerQuitEvent event) {
        val service = LuthyLogin.getService();

        service.setLogged(event.getPlayer().getUniqueId(), false);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent event) {
        val service = LuthyLogin.getService();

        if (!service.isLogged(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        val service = LuthyLogin.getService();

        if (!service.isLogged(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

}
