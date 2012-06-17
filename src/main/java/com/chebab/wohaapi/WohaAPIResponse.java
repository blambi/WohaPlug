package com.chebab.wohaapi;

/**
 * Contains a simple way to access responses from wohaapi
 */
public class WohaAPIResponse {
    String status; // Move to enum later on...
    String message;
    
    public WohaAPIResponse( String status, String message ) {
        this.status = status;
        this.message = message; // Parse out flags
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public boolean getFlagJailed() {
        return false;
    }

    public String getFlagWarning() {
        return "";
    }
}