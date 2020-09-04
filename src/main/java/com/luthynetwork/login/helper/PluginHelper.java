package com.luthynetwork.login.helper;

import com.luthynetwork.core.commands.VoidCommand;
import com.luthynetwork.core.commands.registration.VoidRegister;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.Optional;

public abstract class PluginHelper extends JavaPlugin {

    private final VoidRegister voidRegister = new VoidRegister(this);

    public abstract void load();
    public abstract void enable();
    public abstract void disable();

    @Override
    public void onLoad() {
        load();
    }

    @Override
    public void onEnable() {
        enable();
    }

    @Override
    public void onDisable() {
        disable();
    }

    public <T> T getService(Class<T> service) {
        Objects.requireNonNull(service, "clazz");
        return Optional.ofNullable(Bukkit.getServicesManager().getRegistration(service)).map(RegisteredServiceProvider::getProvider).orElseThrow(() -> {
            return new IllegalStateException("No registration present for service '" + service.getName() + "'");
        });
    }

    public <T> T provideService(Class<T> clazz, T instance, ServicePriority priority) {
        Objects.requireNonNull(clazz, "clazz");
        Objects.requireNonNull(instance, "instance");
        Objects.requireNonNull(priority, "priority");
        Bukkit.getServicesManager().register(clazz, instance, this, priority);
        return instance;
    }

    public <T> T provideService(Class<T> clazz, T instance) {
        this.provideService(clazz, instance, ServicePriority.Normal);
        return instance;
    }

    public void register(VoidCommand... commands) {
        voidRegister.add(commands);
    }

    public void listener(Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, this);
        }
    }

}
