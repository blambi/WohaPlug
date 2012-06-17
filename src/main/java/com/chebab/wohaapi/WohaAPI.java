package com.chebab.wohaapi;

import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;

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
            ret = call_url.getContent().toString();
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
        String command = "auth/" + username + "/";

        String resp = this.call( command );

        System.out.println( resp );

        return new WohaAPIResponse( WohaAPIResponse.Status.BANNED, resp );
    }

}
