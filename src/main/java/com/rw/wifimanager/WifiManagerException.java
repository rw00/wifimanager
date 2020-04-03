package com.rw.wifimanager;

public class WifiManagerException extends Exception {
    public WifiManagerException(String message) {
        super(message);
    }

    public WifiManagerException(String message, Exception cause) {
        super(message, cause);
    }
}
