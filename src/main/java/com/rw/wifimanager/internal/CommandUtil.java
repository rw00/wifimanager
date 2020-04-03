package com.rw.wifimanager.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


public class CommandUtil {
    private static final ExecutorService COMMAND_EXECUTOR = Executors.newSingleThreadExecutor(runnable -> {
        var thread = new Thread(runnable);
        thread.setName("command-executor");
        thread.setDaemon(true);
        return thread;
    });

    private CommandUtil() {
    }

    public static Process runCommand(String... command) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(new File("."));
        Process process = processBuilder.start();
        COMMAND_EXECUTOR.submit(() -> {
            process.waitFor();
            return null;
        });
        return process;
    }

    public static String readProcessOutputAsString(Process process) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            return bufferedReader.lines().collect(Collectors.joining("\n"));
        }
    }

    public static List<String> readProcessOutputAsLines(Process process) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            return bufferedReader.lines().collect(Collectors.toList());
        }
    }
}
