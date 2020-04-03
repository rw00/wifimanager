package com.rw.wifimanager;

public class WifiManagerFactory {
    private WifiManagerFactory() {
    }

    public static IWifiManager createWifiManager() throws WifiManagerException {
        String osName = System.getProperty("os.name");
        return doCreateWifiManager(osName);
    }

    protected static IWifiManager doCreateWifiManager(String osName) throws WifiManagerException {
        osName = osName.toLowerCase();
        if (osName.contains("win")) {
            return new WindowsWifiManager();
        } else if (osName.contains("mac")) {
            return new MacWifiManager();
        }
        throw new WifiManagerException("Unsupported system");
    }
}
