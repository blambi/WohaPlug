package com.chebab.wohaplug;

import java.io.File;
import java.io.IOException;
import java.util.TreeMap;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.PluginManager;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
//import org.bukkit.event.entity.EntityDamageByEntityEvent;

import org.bukkit.entity.EntityType;

import org.bukkit.entity.Player;

public class WohaPlug extends JavaPlugin implements Listener
{
    private TreeMap<String,String> naughty_cache; // Caching of bad replies so we don't need to care about them
    private String host;
    private int port;
    
    public void onLoad() {
        this.getConfig().options().copyDefaults( true );
        this.saveConfig();
    }

    public void onEnable() {
        host = this.getConfig().getString( "host" );
        port = this.getConfig().getInt( "port" );

        getServer().getPluginManager().registerEvents( this, this );

        System.out.println( "[WohaPlug] loaded, will connect to " + host + ":" + port );
    }

    @EventHandler
    public void onPlayerJoinEvent( PlayerJoinEvent event ) {
        system.out.println( "[WohaPlug] JOIN" );
    }

    @EventHandler
    public void onPlayerLoginEvent( PlayerLoginEvent event ) {
        system.out.println( "[WohaPlug] LOGIN" );
    }
}
