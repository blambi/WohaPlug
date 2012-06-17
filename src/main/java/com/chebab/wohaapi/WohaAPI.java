package com.chebab.wohaapi;

import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.commons.lang.StringUtils;

/**
 * This class implements the quite simple protocol WohaAPI
 */
public class WohaAPI {
    String url;

    public WohaAPI( String url ) {
        if( url.endsWith( "/" ) )
            this.url = url;
        else
            this.url = url + '/';
    }

    private String call( String command ) {
        String ret = "";

        try {
            URL call_url = new URL( this.url + command );

            InputStream in = call_url.openStream();
            BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
            ret = reader.readLine();
        }
        catch (MalformedURLException e) {
            ret = "ERROR: Malformed URL";
        }
        catch (IOException e) {
            ret = "ERROR: IOException";
        }

        return ret;
    }

    public WohaAPIResponse auth( String username ) {
        WohaAPIResponse.Status status = WohaAPIResponse.Status.OK;
        String command = "auth/" + username + "/";
        String message = "";
        String resp = this.call( command );

        if( resp.startsWith( "OK" ) ) {
            // FIXME: Add flag support here!
            status = WohaAPIResponse.Status.OK;
        }
        else if( resp.startsWith( "NOT_WHITELISTED" ) ) {
            status = WohaAPIResponse.Status.NOT_WHITELISTED;
        }
        else if( resp.startsWith( "BANNED" ) ) {
            status = WohaAPIResponse.Status.BANNED;
            message = resp.substring( resp.indexOf( ':' ) +1);
        }
        else {
            status = WohaAPIResponse.Status.ERROR;
            if( resp.length() > 4 )
                message = resp.substring( resp.indexOf( ':' ) +1);
            else
                message = "empty reply";
        }

        return new WohaAPIResponse( status, message );
    }

    /**
     * We take the freedom to ignore all diffrent responses since they
     * are of no real value.
     * @return true OK was recived
     * @return false something else was recived might be treated as true...
     */
     public boolean logout( String username ) {
         String resp = this.call( "logout/" + username + "/" );
         return resp.equals( "OK" );
     }

    /**
     * Updates the service regarding who is still connected.
     *
     * @argument users array of users still connected
     * @return true if all users was online.
     */
    public boolean ping( String[] usernames ) {
        String args = StringUtils.join( usernames, '|' );
        String resp = this.call( "ping/" + args );

        if( resp.equals( "PONG" ) )
            return true;

        return false;
    }
}
