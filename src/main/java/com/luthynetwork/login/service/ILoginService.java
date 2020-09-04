package com.luthynetwork.login.service;

import com.luthynetwork.login.utils.Result;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface ILoginService {

    Result register(Player player, String password);
    Result login(Player player, String password);

    void setLogged(UUID uuid, boolean log);

    boolean isRegistered(UUID uuid);
    boolean isLogged(UUID uuid);

}
