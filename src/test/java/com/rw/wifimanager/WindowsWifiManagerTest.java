package com.rw.wifimanager;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;


class WindowsWifiManagerTest {
    WindowsWifiManager wifiManager = new WindowsWifiManager();

    @Test
    void should_parse_available_networks() {
        List<String> availableNetworks = wifiManager.parseAvailableNetworksString(test_available_networks_string());
        assertEquals(List.of("home sweet home", "repeater"), availableNetworks);
    }

    @Test
    void should_parse_saved_networks() {
        Set<String> savedNetworks = wifiManager.parseSavedNetworksString(test_saved_networks_string());
        assertEquals(Set.of("living room", "the office", "hotspot"), savedNetworks);
    }

    private String test_available_networks_string() {
        return "Interface name : Wi-Fi\n" + //
            "There are 2 networks currently visible.\n\n" + //
            "SSID 1 : home sweet home\n" + //
            "\tNetwork type\t\t\t: Infrastructure\n" + //
            "\tAuthentication\t\t\t: WPA2-Personal\n" + //
            "\tEncryption\t\t\t: CCMP\n\n" + //
            "SSID 2 : repeater\n" + //
            "\tNetwork type\t\t\t: Infrastructure\n" + //
            "\tAuthentication\t\t\t: WPA2-Personal\n" + //
            "\tEncryption\t\t\t: CCMP";
    }

    private String test_saved_networks_string() {
        return "Profiles on interface Wi-Fi:\n" + //
            "User profiles\n" + //
            "-------------\n" + //
            "\tAll User Profile\t: living room\n" + //
            "\tAll User Profile\t: the office\n" + //
            "\tAll User Profile\t: hotspot\n";
    }
}
