package com.rw.wifimanager;

import java.util.List;


public interface IWifiManager {
    List<String> listAvailableSavedNetworks() throws WifiManagerException;

    void connectToNetwork(String network) throws WifiManagerException;
}
