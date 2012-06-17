package com.chebab.wohaapi;

/**
 * Contains a simple way to access responses from wohaapi
 */
public class WohaAPIResponse {
    Status status; // Move to enum later on...
    String message;
    boolean flag_jailed = false;
    String flag_warning = "";

    public WohaAPIResponse( Status status, String message ) {
        this.status = status;
        this.message = message; // Parse out flags
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public boolean getFlagJailed() {
        return flag_jailed;
    }

    public String getFlagWarning() {
        return flag_warning;
    }

    public static enum Status {
        NOT_WHITELISTED,
        BANNED,
        ERROR,
        OK
    }
}