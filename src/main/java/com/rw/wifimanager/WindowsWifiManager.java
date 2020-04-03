package com.rw.wifimanager;

import static com.rw.wifimanager.internal.CommandUtil.readProcessOutputAsString;
import static com.rw.wifimanager.internal.CommandUtil.runCommand;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WindowsWifiManager implements IWifiManager {
    public static final int REFRESH_WIFI_WAIT_TIME_MS = 3500;
    private static final Logger log = LoggerFactory.getLogger(WindowsWifiManager.class);

    public WindowsWifiManager() {
    }

    @Override
    public List<String> listAvailableSavedNetworks() throws WifiManagerException {
        return wrapFunctionException(() -> {
            Set<String> savedNetworks = findSavedNetworks();

            // This must be run as administrator!
            String nameEqualsWifi = "name=`Wi-Fi`".replace('`', '"');
            Process scanForNetworksProcess = runCommand("cmd", "/c", "netsh", "interface", "set", "interface", nameEqualsWifi, "admin=disabled");
            String scanForNetworksResult = readProcessOutputAsString(scanForNetworksProcess);
            log.info(String.format("Refreshing Wi-Fi: %s", scanForNetworksResult));
            Thread.sleep(REFRESH_WIFI_WAIT_TIME_MS);
            scanForNetworksProcess = runCommand("cmd", "/c", "netsh", "interface", "set", "interface", nameEqualsWifi, "admin=enabled");
            scanForNetworksResult = readProcessOutputAsString(scanForNetworksProcess);
            log.info(String.format("Refreshed Wi-Fi: %s", scanForNetworksResult));
            Thread.sleep(REFRESH_WIFI_WAIT_TIME_MS);

            List<String> availableNetworks = listAvailableNetworks();
            return availableNetworks.stream().filter(savedNetworks::contains).collect(Collectors.toList());
        });
    }

    @Override
    public void connectToNetwork(String network) throws WifiManagerException {
        wrapFunctionException(() -> {
            Process process = runCommand("netsh", "wlan", "connect", "ssid=" + network, "name=" + network);
            String connectionResult = readProcessOutputAsString(process);
            if (!connectionResult.contains("Connection request was completed successfully")) {
                throw new WifiManagerException(String.format("Failed to connect to %s. Error: %s", network, connectionResult));
            }
            return null;
        });
    }

    protected List<String> parseAvailableNetworksString(String availableNetworksString) {
        List<String> availableSavedNetworks = new ArrayList<>();
        Matcher matcher = Pattern.compile("SSID \\d+ : (.*)").matcher(availableNetworksString);
        while (matcher.find()) {
            String availableNetwork = matcher.group(1);
            availableSavedNetworks.add(availableNetwork);
        }
        return availableSavedNetworks;
    }

    protected Set<String> parseSavedNetworksString(String savedNetworksString) {
        Set<String> savedNetworks = new HashSet<>();
        Matcher matcher = Pattern.compile("All User Profile\\s+: (.*)").matcher(savedNetworksString);
        while (matcher.find()) {
            savedNetworks.add(matcher.group(1));
        }
        return savedNetworks;
    }

    private List<String> listAvailableNetworks() throws Exception {
        Process availableNetworksProcess = runCommand("netsh", "wlan", "show", "networks");
        String availableNetworksString = readProcessOutputAsString(availableNetworksProcess);
        return parseAvailableNetworksString(availableNetworksString);
    }

    private Set<String> findSavedNetworks() throws Exception {
        Process savedNetworksProcess = runCommand("netsh", "wlan", "show", "profiles");
        String savedNetworksString = readProcessOutputAsString(savedNetworksProcess);
        return parseSavedNetworksString(savedNetworksString);
    }

    private <T> T wrapFunctionException(Callable<T> callable) throws WifiManagerException {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new WifiManagerException(e.getLocalizedMessage(), e);
        }
    }
}
