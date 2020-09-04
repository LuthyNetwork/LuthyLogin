package com.luthynetwork.login.service.impl;

import com.google.common.collect.Lists;
import com.luthynetwork.core.LuthyCore;
import com.luthynetwork.core.data.Account;
import com.luthynetwork.core.libs.util.security.SecurityUtil;
import com.luthynetwork.core.service.IAccount;
import com.luthynetwork.login.service.ILoginService;
import com.luthynetwork.login.utils.Result;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class LoginServiceImpl implements ILoginService {

    private final List<UUID> logged = Lists.newArrayList();

    @Override
    public Result register(Player player, String password) {
        try {
            IAccount service = LuthyCore.getApi().getAccountService();
            Account account = service.create(player.getUniqueId());

            account.setPassword(SecurityUtil.md5(password));

            return Result.REGISTER_SUCCESS;
        } catch (Exception ignored) {
            return Result.REGISTER_ERROR;
        }
    }

    @Override
    public Result login(Player player, String password) {
        try {
            IAccount service = LuthyCore.getApi().getAccountService();
            Account account = service.create(player.getUniqueId());

            String input = SecurityUtil.md5(password);
            String encrypted = account.getPassword();

            return input.equals(encrypted) ? Result.LOGIN_SUCCESS : Result.LOGIN_WRONG_PASSWORD;
        } catch (Exception ignored) {
            return Result.LOGIN_ERROR;
        }
    }

    @Override
    public void setLogged(UUID uuid, boolean log) {
        if (log) {
            logged.add(uuid);
        } else {
            logged.remove(uuid);
        }
    }

    @Override
    public boolean isRegistered(UUID uuid) {
        IAccount service = LuthyCore.getApi().getAccountService();
        Account account = service.create(uuid);

        return !account.getPassword().equals("");
    }

    @Override
    public boolean isLogged(UUID uuid) {
        return logged.contains(uuid);
    }

}
