package com.rw.wifimanager;

import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;


class WifiManagerFactoryTest {

    @Test
    void factory_should_create_mac_wifi_manager() throws Exception {
        var macWifiManager = WifiManagerFactory.doCreateWifiManager("mac os x");
        assertSame(MacWifiManager.class, macWifiManager.getClass());
    }

    @Test
    void factory_should_create_windows_wifi_manager() throws Exception {
        var windowsWifiManager = WifiManagerFactory.doCreateWifiManager("windows 10");
        assertSame(WindowsWifiManager.class, windowsWifiManager.getClass());
    }
}
