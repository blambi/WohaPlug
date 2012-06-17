package com.chebab.wohaapi;

import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

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
            // FIXME: Add flag support here? maybe a single in the top?
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

}
