package com.chebab.wohaplug;

/**
 * A simple class for just handling kick reason, timestamp and check if timestamp is stale and old
 */
public class CachePixie {
    /**
     * No need to specify timestamp since we create them on the fly
     */
    private String message;
    private long timeout;
    
    public CachePixie( String message ) {
        this.message = message;
        this.timeout = System.currentTimeMillis() + 30 * 1000; // 30s timeout
    }

    public String getMessage() {
        return message;
    }

    public boolean isTimedout() {
        return System.currentTimeMillis() > timeout;
    }
}
