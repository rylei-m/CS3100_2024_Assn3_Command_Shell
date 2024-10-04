package com.example;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Shell {
    private static final List<String> history = new ArrayList<>();
    private static long totalExecutionTime = 0;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String currentDir = System.getProperty("user.dir");

        while (true) {
            System.out.print("[" + currentDir + "]: ");
            String commandLine = scanner.nextLine();
            history.add(commandLine);

            String[] commandArgs = splitCommand(commandLine);

            if (commandArgs.length == 0) continue;

            String command = commandArgs[0];

            if (command.equals("exit")) {
                System.out.println("Exiting shell.");
                break;
            }

            switch (command) {
                case "ptime":
                    printPtime();
                    break;
                case "history":
                    printHistory();
                    break;
                case "^":
                    executeHistoryCommand(commandArgs);
                    break;
                case "list":
                    listFiles(currentDir);
                    break;
                case "cd":
                    currentDir = changeDirectory(currentDir, commandArgs);
                    break;
                case "mdir":
                    makeDirectory(currentDir, commandArgs);
                    break;
                case "rdir":
                    removeDirectory(currentDir, commandArgs);
                    break;
                default:
                    executeExternalCommand(commandArgs, commandLine.endsWith("&"));
            }
        }
        scanner.close();
    }

    private static String[] splitCommand(String command) {
        List<String> matchList = new ArrayList<>();
        Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
        Matcher regexMatcher = regex.matcher(command);
        while (regexMatcher.find()) {
            if (regexMatcher.group(1) != null) {
                matchList.add(regexMatcher.group(1));
            } else if (regexMatcher.group(2) != null) {
                matchList.add(regexMatcher.group(2));
            } else {
                matchList.add(regexMatcher.group());
            }
        }
        return matchList.toArray(new String[0]);
    }

    private static void printPtime() {
        System.out.printf("Total time in child processes: %.4f seconds\n", totalExecutionTime / 1000.0);
    }

    private static void printHistory() {
        System.out.println("-- Command History --");
        for (int i = 0; i < history.size(); i++) {
            System.out.println((i + 1) + " : " + history.get(i));
        }
    }

    private static void executeHistoryCommand(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: ^ <number>");
            return;
        }
        try {
            int index = Integer.parseInt(args[1]) - 1;
            if (index < 0 || index >= history.size()) {
                System.out.println("Invalid history command number.");
                return;
            }
            String command = history.get(index);
            System.out.println("Executing: " + command);
            String[] commandArgs = splitCommand(command);

            // Execute the command
            if (commandArgs.length > 0) {
                String firstCommand = commandArgs[0];
                switch (firstCommand) {
                    case "ptime":
                        printPtime();
                        break;
                    case "history":
                        printHistory();
                        break;
                    case "list":
                        listFiles(System.getProperty("user.dir"));
                        break;
                    case "cd":
                        System.setProperty("user.dir", changeDirectory(System.getProperty("user.dir"), commandArgs));
                        break;
                    case "mdir":
                        makeDirectory(System.getProperty("user.dir"), commandArgs);
                        break;
                    case "rdir":
                        removeDirectory(System.getProperty("user.dir"), commandArgs);
                        break;
                    default:
                        executeExternalCommand(commandArgs, command.endsWith("&"));
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        }
    }


    private static void listFiles(String currentDir) {
        try {
            Files.list(Paths.get(currentDir)).forEach(path -> {
                File file = path.toFile();
                String permissions = (file.isDirectory() ? "d" : "-")
                        + (file.canRead() ? "r" : "-")
                        + (file.canWrite() ? "w" : "-")
                        + (file.canExecute() ? "x" : "-");
                String size = String.format("%10d", file.length());
                String lastModified = new java.text.SimpleDateFormat("MMM dd, yyyy HH:mm").format(file.lastModified());
                System.out.println(permissions + " " + size + " " + lastModified + " " + file.getName());
            });
        } catch (IOException e) {
            System.out.println("Error listing files.");
        }
    }

    private static String changeDirectory(String currentDir, String[] args) {
        Path newPath;
        if (args.length < 2) {
            newPath = Paths.get(System.getProperty("user.home"));
        } else {
            newPath = Paths.get(currentDir, args[1]);
        }

        if (Files.exists(newPath) && Files.isDirectory(newPath)) {
            return newPath.toAbsolutePath().toString();
        } else {
            System.out.println("Directory not found: " + newPath);
            return currentDir;
        }
    }

    private static void makeDirectory(String currentDir, String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: mdir <directory_name>");
            return;
        }
        Path newPath = Paths.get(currentDir, args[1]);
        try {
            Files.createDirectory(newPath);
            System.out.println("Directory created: " + newPath);
        } catch (IOException e) {
            System.out.println("Error creating directory: " + newPath);
        }
    }

    private static void removeDirectory(String currentDir, String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: rdir <directory_name>");
            return;
        }
        Path dirPath = Paths.get(currentDir, args[1]);
        try {
            Files.deleteIfExists(dirPath);
            System.out.println("Directory removed: " + dirPath);
        } catch (IOException e) {
            System.out.println("Error removing directory: " + dirPath);
        }
    }

    private static void executeExternalCommand(String[] commandArgs, boolean isBackground) {
        // Remove '&' from the last argument if it exists
        if (isBackground) {
            commandArgs[commandArgs.length - 1] = commandArgs[commandArgs.length - 1].replace("&", "").trim();
        }

        ProcessBuilder processBuilder = new ProcessBuilder(commandArgs);
        processBuilder.inheritIO();
        try {
            long startTime = System.currentTimeMillis();
            Process process = processBuilder.start();
            if (!isBackground) {
                process.waitFor();
                long endTime = System.currentTimeMillis();
                totalExecutionTime += (endTime - startTime);
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Invalid command or error during execution.");
        }
    }

    private static void executePipedCommands(String[] commandArgs1, String[] commandArgs2) {
        try {
            ProcessBuilder pb1 = new ProcessBuilder(commandArgs1);
            ProcessBuilder pb2 = new ProcessBuilder(commandArgs2);

            Process process1 = pb1.start();
            InputStream inputStream = process1.getInputStream();
            Process process2 = pb2.start();

            OutputStream outputStream = process2.getOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            process1.waitFor();
            process2.waitFor();

        } catch (IOException | InterruptedException e) {
            System.out.println("Error during piped command execution.");
        }
    }

}
