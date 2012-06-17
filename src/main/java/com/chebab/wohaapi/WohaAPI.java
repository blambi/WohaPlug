package com.chebab.wohaapi;

import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;

/**
 * This class implements the quite simple protocol WohaAPI
 */
public class WohaAPI {
    String url;

    public WohaAPI( String URL ) {
        this.url = URL;
    }

    private String call( String command, String arguments ) {
        try {

        }
        catch (MalformedURLException e) {
            
        }
        catch (IOException e) {

        }

    }

    public WohaAPIResponse auth( String username ) {

    }

}
