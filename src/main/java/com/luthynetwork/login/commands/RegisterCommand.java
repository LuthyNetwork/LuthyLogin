package com.luthynetwork.login.commands;

import com.luthynetwork.core.commands.VoidCommand;
import com.luthynetwork.core.commands.annotation.command.Aliases;
import com.luthynetwork.core.commands.annotation.command.Command;
import com.luthynetwork.core.commands.context.Context;
import com.luthynetwork.core.commands.settings.Executor;
import com.luthynetwork.core.libs.util.title.TitleAPI;
import com.luthynetwork.login.LuthyLogin;
import com.luthynetwork.login.utils.Message;
import com.luthynetwork.login.utils.Result;
import lombok.val;
import org.bukkit.scheduler.BukkitRunnable;

public class RegisterCommand extends VoidCommand {

    @Command(name = "register", executor = Executor.PLAYER_ONLY)
    @Aliases("registrar")
    public void command(Context context) {
        val service = LuthyLogin.getService();
        val player = context.player();
        val label = context.label();
        val args = context.args();

        if (args.length < 2) {
            player.sendMessage(Message.INTERNAL_PREFIX + "§cUse /" + label + " (senha) (senha) para se registrar.");
            return;
        }

        if (service.isRegistered(player.getUniqueId())) {
            player.sendMessage(Message.INTERNAL_PREFIX + "§cVocê já possui uma conta! Use /logar (senha) para efetuar a autenticação.");
            return;
        }

        if (service.isLogged(player.getUniqueId())) {
            player.sendMessage(Message.INTERNAL_PREFIX + "§cVocê já está logado!");
            return;
        }

        if (!args[0].equals(args[1])) {
            player.sendMessage(Message.INTERNAL_PREFIX + "§cAs duas senhas devem ser iguais.");
            return;
        }
        val password = args[0];

        if (password.length() < 4 || password.length() > 16) {
            player.sendMessage(Message.INTERNAL_PREFIX + "§cA senha deve possuir um mínimo de 4 caracteres e um máximo de 16 caracteres.");
            return;
        }

        if (password.chars().filter(Character::isDigit).count() < 2) {
            player.sendMessage(Message.INTERNAL_PREFIX + "§cA senha deve possuir um mínimo de dois números.");
            return;
        }

        Result result = service.register(player, password);

        if (result == Result.REGISTER_SUCCESS) {
            player.sendMessage(Message.INTERNAL_PREFIX + "§aAutenticado(a) com sucesso! Agora use /logar (senha) para entrar no servidor.");

            TitleAPI.clearTitle(player);
            TitleAPI.sendTitle(player, 0, 20 * 5, 0, "§eLuthy Network", "§aRegistrado(a) com sucesso!");

            new BukkitRunnable() {
                @Override
                public void run() {
                    TitleAPI.clearTitle(player);
                    TitleAPI.sendTitle(player, 0, 120, 0, "§eLuthy Network", "Use /logar (senha).");
                }
            }.runTaskLater(LuthyLogin.getInstance(), 20L * 5);
        } else {
            player.sendMessage(Message.INTERNAL_PREFIX + "§cUm erro aconteceu ao se registrar. Por favor, tente novamente mais tarde.");
        }
    }

}
