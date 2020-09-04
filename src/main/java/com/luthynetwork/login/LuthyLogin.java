package com.luthynetwork.login;

import com.luthynetwork.core.LuthyCore;
import com.luthynetwork.core.libs.database.CoreHikariImplement;
import com.luthynetwork.login.commands.LoginCommand;
import com.luthynetwork.login.commands.RegisterCommand;
import com.luthynetwork.login.helper.PluginHelper;
import com.luthynetwork.login.listener.PlayerEvents;
import com.luthynetwork.login.service.ILoginService;
import com.luthynetwork.login.service.impl.LoginServiceImpl;
import lombok.Getter;

public final class LuthyLogin extends PluginHelper {

    @Getter private static LuthyLogin instance;
    @Getter private static CoreHikariImplement hikari;
    @Getter private static ILoginService service;

    @Override
    public void load() {
        this.provideService(ILoginService.class, new LoginServiceImpl());
    }

    @Override
    public void enable() {
        instance = this;

        hikari = LuthyCore.getApi().getHikariConnection();
        service = this.getService(ILoginService.class);

        listener(new PlayerEvents());
        register(new LoginCommand(), new RegisterCommand());
    }

    @Override
    public void disable() {

    }
}
