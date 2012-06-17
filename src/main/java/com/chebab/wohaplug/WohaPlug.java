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
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import org.bukkit.entity.Player;

import com.chebab.wohaapi.*;

public class WohaPlug extends JavaPlugin implements Listener
{
    private TreeMap<String, CachePixie> naughty_cache; // Caching of bad replies so we don't need to care about them
    private String url;
    private WohaAPI api;

    public void onLoad() {
        this.getConfig().options().copyDefaults( true );
        this.saveConfig();
    }

    public void onEnable() {
        naughty_cache = new TreeMap<String, CachePixie>();

        url = this.getConfig().getString( "url" );
        api = new WohaAPI( url );

        getServer().getPluginManager().registerEvents( this, this );

        System.out.println( "[WohaPlug] loaded, will connect to " + url );
        // FIXME: add a peroidic job that pushes update every X sec
    }

    /**
     * Handles the initial check up if a player is allowed to enter or not
     */
    @EventHandler
    public void onPlayerPreLoginEvent( PlayerPreLoginEvent event ) {
        String who = event.getName();
        WohaAPIResponse resp;

        // FIXME: refactor so we don't do auth in two places!
        if( naughty_cache.containsKey( who ) ) {
            CachePixie subject = naughty_cache.get( who );

            if( subject.isTimedout() ) {
                // Clean up stale stuff and if we are still here remove us,
                naughty_cache.remove( who );
                System.out.println( "[WohaPlug] checking with service..." );
                resp = api.auth( who );

                if( resp.getStatus() == WohaAPIResponse.Status.BANNED ) {
                    event.disallow( PlayerPreLoginEvent.Result.KICK_BANNED, resp.getMessage() );
                }

                // FIXME: add handling of other responses.
            }
            else {
                event.disallow( PlayerPreLoginEvent.Result.KICK_WHITELIST, subject.getMessage() );
            }
        }
        else {
            // We have to check with remote then..
            System.out.println( "[WohaPlug] checking with service..." );
            resp = api.auth( who );

            if( resp.getStatus() == WohaAPIResponse.Status.BANNED ) {
                event.disallow( PlayerPreLoginEvent.Result.KICK_BANNED, resp.getMessage() );
            }
        }

        // Cache bad ones
        if( event.getResult() != PlayerPreLoginEvent.Result.ALLOWED && !naughty_cache.containsKey( who ) )
            naughty_cache.put( who, new CachePixie( event.getKickMessage() ) );
    }

    /**
     * Handles when a player leaves both voluntary and a bit more assisted.
     */
    @EventHandler
    public void onPlayerQuitEvent( PlayerQuitEvent event ) {
        String who = event.getPlayer().getName();

        System.out.println( "[WohaPlug] " + who + " left." );
        // send update to service.
    }
}
