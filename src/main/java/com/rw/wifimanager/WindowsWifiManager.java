package com.rw.wifimanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WindowsWifiManager implements IWifiManager {
    private static final Logger log = LoggerFactory.getLogger(WindowsWifiManager.class);


    public WindowsWifiManager() {
    }


    @Override
    public List<String> getAvailableSavedNetworks() throws Exception {
        Process savedNetworksProcess = runCommand("netsh", "wlan", "show", "profiles");
        String savedNetworksString = readProcessOutput(savedNetworksProcess);

        Set<String> savedNetworks = new HashSet<>();
        Pattern pattern = Pattern.compile("All User Profile\\s+: (.*)");
        Matcher matcher = pattern.matcher(savedNetworksString);
        while (matcher.find()) {
            savedNetworks.add(matcher.group(1));
        }

        // This must be run as administrator!
        Process scanForNetworksProcess = runCommand("cmd", "/c", "netsh", "interface", "set", "interface", "name=\"Wi-Fi\"", "admin=disabled");
        String scanForNetworksResult = readProcessOutput(scanForNetworksProcess);
        log.info("Refreshing Wi-Fi: " + scanForNetworksResult);
        Thread.sleep(5000);
        scanForNetworksProcess = runCommand("cmd", "/c", "netsh", "interface", "set", "interface", "name=\"Wi-Fi\"", "admin=enabled");
        scanForNetworksResult = readProcessOutput(scanForNetworksProcess);
        log.info("Refreshed Wi-Fi: " + scanForNetworksResult);
        Thread.sleep(5000);
        // Adjust the wait time

        Process availableNetworksProcess = runCommand("netsh", "wlan", "show", "networks");
        String availableNetworksString = readProcessOutput(availableNetworksProcess);

        List<String> availableSavedNetworks = new ArrayList<>();
        pattern = Pattern.compile("SSID \\d+ : (.*)");
        matcher = pattern.matcher(availableNetworksString);
        while (matcher.find()) {
            String availableNetwork = matcher.group(1);
            if (savedNetworks.contains(availableNetwork)) {
                availableSavedNetworks.add(availableNetwork);
            }
        }
        return availableSavedNetworks;
    }

    @Override
    public void connect(String network) throws Exception {
        Process process = runCommand("netsh", "wlan", "connect", "ssid=" + network, "name=" + network);
        String connectionResult = readProcessOutput(process);
        if (!connectionResult.contains("Connection request was completed successfully")) {
            throw new Exception("Failed to connect. Error: " + connectionResult);
        }
    }

    private Process runCommand(String... command) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(new File("."));
        return processBuilder.start();
    }

    private String readProcessOutput(Process process) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }
}
