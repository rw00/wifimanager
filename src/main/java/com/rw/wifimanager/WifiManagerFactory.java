package com.rw.wifimanager;

public class WifiManagerFactory {
    private WifiManagerFactory() {
    }


    public static IWifiManager createWifiManager() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return new WindowsWifiManager();
        }
        throw new RuntimeException("Unsupported system");
    }
}
