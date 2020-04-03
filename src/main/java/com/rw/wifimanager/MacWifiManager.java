package com.rw.wifimanager;

import static com.rw.wifimanager.internal.CommandUtil.readProcessOutputAsLines;
import static com.rw.wifimanager.internal.CommandUtil.readProcessOutputAsString;
import static com.rw.wifimanager.internal.CommandUtil.runCommand;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MacWifiManager implements IWifiManager {
    private static final Logger log = LoggerFactory.getLogger(MacWifiManager.class);

    public MacWifiManager() {
    }

    @Override
    public List<String> listAvailableSavedNetworks() throws WifiManagerException {
        try {
            log.info("Listing available saved networks...");
            String wifiDeviceId = findWifiDeviceId();
            List<String> availableNetworks = listAvailableNetworks();
            Set<String> savedNetworks = findSavedNetworks(wifiDeviceId);
            return availableNetworks.stream().filter(savedNetworks::contains).distinct().collect(Collectors.toList());
        } catch (Exception e) {
            throw new WifiManagerException("Failed to list available saved networks", e);
        }
    }

    @Override
    public void connectToNetwork(String network) throws WifiManagerException {
        try {
            log.info("Connecting to Wi-Fi network {}...", network);
            String wifiDeviceId = findWifiDeviceId();
            Process process = runCommand("networksetup", "-setairportnetwork", wifiDeviceId, network);
            readProcessOutputAsString(process); // ignore
        } catch (Exception e) {
            throw new WifiManagerException(String.format("Failed to connect to network %s", network), e);
        }
    }

    protected String parseNetworkSetupString(String networkSetupString) {
        Matcher matcher = Pattern.compile("Hardware Port: Wi-Fi\nDevice: (.+)\n").matcher(networkSetupString);
        return matcher.find() ? matcher.group(1) : null;
    }

    protected List<String> parseAvailableNetworks(List<String> availableNetworksProcessOutput) {
        int bssidSpacesNumber = 19;
        int[] bssidSpacesCounter = new int[] { bssidSpacesNumber };
        return availableNetworksProcessOutput.stream().skip(1).map(line -> line.chars().dropWhile(c -> ((char) c == ' ')).takeWhile(c -> {
            if (c == ' ') {
                bssidSpacesCounter[0]--;
            } else {
                bssidSpacesCounter[0] = bssidSpacesNumber;
            }
            return bssidSpacesCounter[0] > 0;
        }).collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString().trim()).collect(Collectors.toList());
    }

    protected Set<String> parseSavedNetworksString(String savedNetworksString) {
        Set<String> savedNetworks = new HashSet<>();
        Matcher matcher = Pattern.compile("\t(.+)").matcher(savedNetworksString);
        while (matcher.find()) {
            savedNetworks.add(matcher.group(1));
        }
        return savedNetworks;
    }

    private String findWifiDeviceId() throws Exception {
        log.debug("Finding the Wi-Fi Device ID");
        Process networkSetupProcess = runCommand("networksetup", "-listallhardwareports");
        String networkSetupString = readProcessOutputAsString(networkSetupProcess);
        String wifiDeviceId = parseNetworkSetupString(networkSetupString);
        if (wifiDeviceId == null) {
            throw new WifiManagerException("Failed to find the Wi-Fi Device ID");
        }
        log.debug("Wi-Fi Device ID={}", wifiDeviceId);
        return wifiDeviceId;
    }

    private List<String> listAvailableNetworks() throws Exception {
        log.info("Listing available Wi-Fi networks...");
        Process availableNetworksProcess = runCommand("/System/Library/PrivateFrameworks/Apple80211.framework/Versions/A/Resources/airport", "scan");
        List<String> availableNetworksProcessOutput = readProcessOutputAsLines(availableNetworksProcess);
        return parseAvailableNetworks(availableNetworksProcessOutput);
    }

    private Set<String> findSavedNetworks(String wifiDeviceId) throws Exception {
        log.info("Listing saved Wi-Fi networks...");
        Process savedNetworksProcess = runCommand("networksetup", "-listpreferredwirelessnetworks", wifiDeviceId);
        String savedNetworksString = readProcessOutputAsString(savedNetworksProcess);
        return parseSavedNetworksString(savedNetworksString);
    }
}
