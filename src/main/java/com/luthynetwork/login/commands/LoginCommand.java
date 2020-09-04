package com.luthynetwork.login.commands;

import com.google.common.collect.Maps;
import com.luthynetwork.core.commands.VoidCommand;
import com.luthynetwork.core.commands.annotation.command.Aliases;
import com.luthynetwork.core.commands.annotation.command.Command;
import com.luthynetwork.core.commands.context.Context;
import com.luthynetwork.core.commands.settings.Executor;
import com.luthynetwork.core.libs.util.title.TitleAPI;
import com.luthynetwork.login.LuthyLogin;
import com.luthynetwork.login.events.PlayerLoginEvent;
import com.luthynetwork.login.utils.Message;
import com.luthynetwork.login.utils.Result;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class LoginCommand extends VoidCommand {

    private final Map<UUID, Integer> tries = Maps.newHashMap();

    @Command(name = "login", executor = Executor.PLAYER_ONLY)
    @Aliases("logar")
    public void command(Context context) {
        val service = LuthyLogin.getService();
        val player = context.player();
        val label = context.label();
        val args = context.args();

        val uuid = player.getUniqueId();

        if (args.length < 1) {
            player.sendMessage(Message.INTERNAL_PREFIX + "§cUse /" + label + " (senha)");
            return;
        }

        if (!service.isRegistered(uuid)) {
            player.sendMessage(Message.INTERNAL_PREFIX + "§cVocê não tem uma conta! Use /registrar (senha) (confirmar senha) para efetuar o cadastro.");
            return;
        }

        if (service.isLogged(uuid)) {
            player.sendMessage(Message.INTERNAL_PREFIX + "§cVocê já está logado!");
            return;
        }

        if (!tries.containsKey(uuid)) tries.put(uuid, 3);

        if (tries.get(player.getUniqueId()) == 0) {
            player.kickPlayer(Message.INTERNAL_PREFIX + "§cVocê excedeu o limite máximo de tentativas.");
            return;
        }

        val password = args[0];

        if (password.length() < 4 || password.length() > 16) {
            player.sendMessage(Message.INTERNAL_PREFIX + "§cA senha deve possuir um mínimo de 4 caracteres e um máximo de 16 caracteres.");
            addTry(player);
            return;
        }

        if (password.chars().filter(Character::isDigit).count() < 2) {
            player.sendMessage(Message.INTERNAL_PREFIX + "§cA senha deve possuir um mínimo de dois números.");
            addTry(player);
            return;
        }

        Result result = service.login(player, password);

        if (result == Result.LOGIN_SUCCESS) {
            tries.remove(uuid);
            service.setLogged(uuid, true);

            player.sendMessage(Message.INTERNAL_PREFIX + "§aAutenticado(a) com sucesso!");

            TitleAPI.clearTitle(player);
            TitleAPI.sendTitle(player, 0, 5 * 20, 0, "§eLuthy Network", "§aAutenticado(a) com sucesso!");

            PlayerLoginEvent event = new PlayerLoginEvent(player);
            Bukkit.getPluginManager().callEvent(event);
        } else if (result == Result.LOGIN_WRONG_PASSWORD) {
            player.sendMessage(Message.INTERNAL_PREFIX + "§cSenha incorreta!");
            addTry(player);
        } else {
            player.sendMessage(Message.INTERNAL_PREFIX + "§cUm erro aconteceu ao se logar. Por favor, tente novamente mais tarde.");
        }
    }

    private void addTry(Player player) {
        val uuid = player.getUniqueId();

        tries.put(uuid, tries.get(uuid) - 1);
        if (tries.get(uuid) != 0) {
            player.sendMessage(Message.INTERNAL_PREFIX + "Você ainda tem §c" + tries.get(uuid) + " §7tentativas.");
        } else {
            tries.remove(uuid);
            player.kickPlayer(Message.INTERNAL_PREFIX + "§cVocê excedeu o limite máximo de tentativas.");
        }
    }
}
