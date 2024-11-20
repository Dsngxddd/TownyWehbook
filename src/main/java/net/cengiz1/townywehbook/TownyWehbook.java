package net.cengiz1.townywehbook;

import net.cengiz1.townywehbook.listener.NewDayListener;
import net.cengiz1.townywehbook.listener.NewTownListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class TownyWehbook extends JavaPlugin {

    private static TownyWehbook instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        getServer().getPluginManager().registerEvents(new NewDayListener(config, this), this);
        getServer().getPluginManager().registerEvents(new NewTownListener(config), this);
        enable();

    }

    @Override
    public void onDisable() {
    }

    public static TownyWehbook getInstance() {
        return instance;
    }

    public void enable(){
        Bukkit.getLogger().info("TownyWehbook Aktif!");
        Bukkit.getLogger().info("github.com/dsngxddd");
        Bukkit.getLogger().info("By DSNG");
        Bukkit.getLogger().info("Version 0.1");
    }
}
