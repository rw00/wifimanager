package com.rw.wifimanager;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;


class MacWifiManagerTest {
    MacWifiManager wifiManager = new MacWifiManager();

    @Test
    void should_parse_network_setup() {
        String wifiDeviceId = wifiManager.parseNetworkSetupString(test_network_setup_string());
        assertEquals("en0", wifiDeviceId);
    }

    @Test
    void should_parse_available_networks() {
        List<String> availableNetworks = wifiManager.parseAvailableNetworks(test_available_networks_list());
        assertEquals(List.of("home sweet home", "internet-box", "neighbor's wifi", "Link"), availableNetworks);
    }

    @Test
    void should_parse_saved_networks() {
        Set<String> savedNetworks = wifiManager.parseSavedNetworksString(test_saved_networks_string());
        assertEquals(Set.of("home sweet home", "room1", "room2", "room3", "hackmeifyoucan", "repeater"), savedNetworks);
    }

    private String test_network_setup_string() {
        return "Hardware Port: Thunderbolt 1\n" + //
            "Device: en1\n" + //
            "Ethernet Address: 82:7f:8f:e8:1c:01\n\n" + //
            "Hardware Port: Wi-Fi\n" + //
            "Device: en0\n" + //
            "Ethernet Address: 88:66:5a:14:d3:39\n\n" + //
            "Hardware Port: Thunderbolt 2\n" + //
            "Device: en2\n" + //
            "Ethernet Address: 82:7f:8f:e8:1c:00";
    }

    private List<String> test_available_networks_list() {
        return List.of(//
            "                            SSID BSSID             RSSI CHANNEL HT CC SECURITY (auth/unicast/group)", //
            "                 home sweet home                   -84  8,+1    Y  -- WPA2(PSK,FT-PSK/AES/AES)", //
            "                    internet-box                   -81  52      Y  -- WPA2(PSK/AES/AES)", //
            "                 neighbor's wifi                   -77  4       Y  -- WPA2(PSK/AES/AES)", //
            "                            Link                   -77  3,+1    Y  -- WPA2(PSK/AES/AES)" //
        );
    }

    private String test_saved_networks_string() {
        return "Preferred networks on en0:\n" + //
            "\thome sweet home\n" + //
            "\troom1\n" + //
            "\troom2\n" + //
            "\troom3\n" + //
            "\thackmeifyoucan\n" + //
            "\trepeater";
    }
}
