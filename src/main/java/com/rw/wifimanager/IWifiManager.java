package com.rw.wifimanager;

import java.util.List;


public interface IWifiManager {
    List<String> getAvailableSavedNetworks() throws Exception;

    void connect(String network) throws Exception;
}
