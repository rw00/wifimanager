package com.rw.wifimanager;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WifiManagerTest {
    private static final Logger log = LoggerFactory.getLogger(WifiManagerTest.class);


    public WifiManagerTest() {
    }


    public static void main(String[] args) throws Exception {
        WindowsWifiManager wifiManager = new WindowsWifiManager();
        List<String> availableSavedNetworks = wifiManager.getAvailableSavedNetworks();
        log.info("Available Saved Networks: " + availableSavedNetworks);
        if (!availableSavedNetworks.isEmpty()) {
            // wifiManager.connect(availableSavedNetworks.get(0));
        }
    }
}
