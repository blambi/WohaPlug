package com.chebab.wohaplug;

import java.io.File;
import java.io.IOException;
import java.util.TreeMap;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
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
    private TreeMap<String, CachePixie> naughty_cache; // Caching of bad replies so we don't
                                                       // need to care about them.
    private String url;
    private WohaAPI api;
    private int pingworker_id = -1;

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

        pingworker_id = getServer().getScheduler().scheduleAsyncRepeatingTask(
            this, new Runnable() {
                    public void run() {
                        // Sends pings to the service at a predefined interval and only if we have any one online.
                        Player[] online = getServer().getOnlinePlayers();

                        if( online.length == 0 ) // Nothing to do.
                            return;

                        // Build list
                        String[] names = new String[online.length];
                        for( int x = 0; x < online.length; x++ ) {
                            names[x] = online[x].getName();
                        }

                        api.ping( names );
                    }
                },
            // We wait about 1m before sending and between pings since
            // the server timeout is ~2m this should be fine even if
            // we are having a hard load.
            // 24000ticks / 20 = 1m
            (long)1200, (long)1200 );
    }

    public void onDisable() {
        if( pingworker_id != -1 ) {
            getServer().getScheduler().cancelTask( pingworker_id );
        }

        // Send logout for all players? nah this will problable be
        // used by reload etc so not a werry smart idea. Also if we
        // just let the service call timeout on them its working like
        // wohasock did. Might cause a few ghosts.
    }

    /**
     * Handles the initial check up if a player is allowed to enter or not
     */
    @EventHandler
    public void onPlayerPreLoginEvent( PlayerPreLoginEvent event ) {
        String who = event.getName();
        WohaAPIResponse resp;
        boolean fetch = true;

        if( naughty_cache.containsKey( who ) ) {
            CachePixie subject = naughty_cache.get( who );

            if( subject.isTimedout() ) {
                // Clean up stale stuff and if we are still here remove us,
                naughty_cache.remove( who );
            }
            else {
                fetch = false; // Don't check
                event.disallow( PlayerPreLoginEvent.Result.KICK_WHITELIST, subject.getMessage() );
            }
        }


        if( fetch ) {
            // We have to check with remote then..
            System.out.println( "[WohaPlug] checking with service..." );
            resp = api.auth( who );

            if( resp.getStatus() == WohaAPIResponse.Status.BANNED ) {
                event.disallow( PlayerPreLoginEvent.Result.KICK_BANNED, resp.getMessage() );
            }
            else if( resp.getStatus() == WohaAPIResponse.Status.NOT_WHITELISTED ) {
                event.disallow( PlayerPreLoginEvent.Result.KICK_WHITELIST, "Sorry, not in our whitelist :(" );
            }
            else if( resp.getStatus() == WohaAPIResponse.Status.ERROR ) {
                event.disallow( PlayerPreLoginEvent.Result.KICK_OTHER, "The server is behaving oddly: " + resp.getMessage() );
            }
        }

        // Cache bad ones
        if( event.getResult() != PlayerPreLoginEvent.Result.ALLOWED && !naughty_cache.containsKey( who ) )
            naughty_cache.put( who, new CachePixie( event.getKickMessage() ) );
    }

    // FIXME: add player join so we can handle warnings and jail in a nice manner. or maybe login

    /**
     * Handles when a player leaves both voluntary and a bit more assisted.
     */
    @EventHandler
    public void onPlayerQuitEvent( PlayerQuitEvent event ) {
        String who = event.getPlayer().getName();

        System.out.println( "[WohaPlug] " + who + " left." );
        api.logout( who ); // send update to service.
    }
}
