import java.util.*;
import java.util.zip.*;
import java.io.*;
import java.nio.file.*;

// person1: marwan
class Parser {
    String commandName;
    String[] args;

    public boolean parse(String input) {
        // getting command name
        int space_index = input.indexOf(' ', 0);

        // in case the command needs no args
        if (space_index == -1) {
            this.commandName = input.toLowerCase();
            this.args = new String[0];
            return true;
        }

        this.commandName = input.substring(0, space_index).toLowerCase();
        int strtArgs = space_index+1;
      
        // in case cp -r
        if(input.substring(0, space_index+3).toLowerCase().equals("cp -r")){
            this.commandName = "cp -r";
            strtArgs = space_index+3;
        }

        String arguments = input.substring(strtArgs).trim();
        this.args = arguments.split(" ");
        return true;
    }

    public String getCommandName() {
        return this.commandName;
    }

    public String[] getArgs() {
        return this.args;
    }
}

public class Terminal {
    static Parser parser = new Parser();
    static String currentDirectory = System.getProperty("user.dir");

    public String pwd() {
        return currentDirectory;
    }

    public void cd(String[] args) {
        if (args.length == 0) {
            currentDirectory = System.getProperty("user.dir");
            return;
        }

        String target = args[0];
        if ("..".equals(target)) {
            Path parent = Paths.get(currentDirectory).getParent();
            if (parent != null) {
                currentDirectory = parent.toString();
            }
            return;
        }

        Path candidate = Paths.get(target);
        if (!candidate.isAbsolute()) {
            candidate = Paths.get(currentDirectory).resolve(target);
        }
        candidate = candidate.normalize();

        if (Files.exists(candidate) && Files.isDirectory(candidate)) {
            currentDirectory = candidate.toString();
        } else {
            System.out.println("No such directory: " + candidate);
        }
    }

    public void ls() {
        File[] files = new File(currentDirectory).listFiles();
        if (files == null) return;
        Arrays.sort(files);
        for (File file : files) {
            System.out.println(file.getName());
        }
    }

    public void mkdir(String[] args) {
        if (args.length == 0) {
            System.out.println("Error: No directory name provided");
            return;
        }
        for (String arg : args) {
            Path dirPath = Paths.get(arg);
            if (!dirPath.isAbsolute()) {
                dirPath = Paths.get(currentDirectory).resolve(dirPath);
            }
            try {
                Files.createDirectories(dirPath);
            } catch (IOException e) {
                System.out.println("Error creating directory: " + e.getMessage());
            }
        }
    }

    public void rmdir(String[] args) {
        if (args.length == 0) {
            System.out.println("Error: No directory name provided");
            return;
        }
        if (args[0].equals("*")) {
            File[] files = new File(currentDirectory).listFiles();
            if (files == null) return;
            for (File file : files) {
                if (file.isDirectory() && file.list().length == 0) {
                    file.delete();
                }
            }
            return;
        }

        Path dirPath = Paths.get(args[0]);
        if (!dirPath.isAbsolute()) {
            dirPath = Paths.get(currentDirectory).resolve(dirPath);
        }

        File dir = dirPath.toFile();
        if (dir.exists() && dir.isDirectory() && dir.list().length == 0) {
            dir.delete();
        } else {
            System.out.println("No such empty directory: " + dirPath);
        }
    }

    // Person1: marwan
    public void cp_r(String[] args) throws IOException {
        File fromDir = new File(args[0]);
        File toDir = new File(args[1]);

        // creating one if dest is not exists
        if (!toDir.exists())
            toDir.mkdirs();

        File[] from_files = fromDir.listFiles();
        if (from_files == null || from_files.length == 0)
            return;

        // coping
        for (File f : from_files) {
            Path sourcePath = f.toPath();
            Path destPath = new File(toDir, f.getName()).toPath();
            Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    // ----------My Commands ----------

    public void touch(String[] args) {
        if (args.length == 0) {
            System.out.println("Error: No file name provided");
            return;
        }
        try {
            Path filePath = Paths.get(args[0]);
            if (!filePath.isAbsolute()) filePath = Paths.get(currentDirectory).resolve(filePath);
            if (Files.exists(filePath)) {
                System.out.println("File already exists: " + filePath);
            } else {
                Files.createFile(filePath);
                System.out.println("File created: " + filePath);
            }
        } catch (IOException e) {
            System.out.println("Error creating file: " + e.getMessage());
        }
    }

    public void rm(String[] args) {
        if (args.length == 0) {
            System.out.println("Error: No file name provided");
            return;
        }
        try {
            Path filePath = Paths.get(args[0]);
            if (!filePath.isAbsolute()) filePath = Paths.get(currentDirectory).resolve(filePath);
            if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
                Files.delete(filePath);
                System.out.println("File deleted: " + filePath);
            } else {
                System.out.println("No such file: " + filePath);
            }
        } catch (IOException e) {
            System.out.println("Error deleting file: " + e.getMessage());
        }
    }

    public void cp(String[] args) {
        if (args.length < 2) {
            System.out.println("Error: Missing arguments (usage: cp source target)");
            return;
        }
        try {
            Path source = Paths.get(args[0]);
            Path target = Paths.get(args[1]);
            if (!source.isAbsolute()) source = Paths.get(currentDirectory).resolve(source);
            if (!target.isAbsolute()) target = Paths.get(currentDirectory).resolve(target);
            if (!Files.exists(source)) {
                System.out.println("Source file does not exist: " + source);
                return;
            }
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File copied successfully to: " + target);
        } catch (IOException e) {
            System.out.println("Error copying file: " + e.getMessage());
        }
    }

    public void cat(String[] args) {
        if (args.length == 0) {
            System.out.println("Error: No file specified");
            return;
        }
        try {
            if (args.length == 1) {
                Path file = Paths.get(args[0]);
                if (!file.isAbsolute()) file = Paths.get(currentDirectory).resolve(file);
                if (Files.exists(file)) {
                    Files.lines(file).forEach(System.out::println);
                } else {
                    System.out.println("No such file: " + file);
                }
            } else if (args.length == 2) {
                Path file1 = Paths.get(args[0]);
                Path file2 = Paths.get(args[1]);
                if (!file1.isAbsolute()) file1 = Paths.get(currentDirectory).resolve(file1);
                if (!file2.isAbsolute()) file2 = Paths.get(currentDirectory).resolve(file2);
                if (Files.exists(file1)) Files.lines(file1).forEach(System.out::println);
                if (Files.exists(file2)) Files.lines(file2).forEach(System.out::println);
            } else {
                System.out.println("Too many arguments for cat command");
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    public void wc(String[] args) {
        if (args.length == 0) {
            System.out.println("Error: No file specified");
            return;
        }
        try {
            Path file = Paths.get(args[0]);
            if (!file.isAbsolute()) file = Paths.get(currentDirectory).resolve(file);
            if (!Files.exists(file)) {
                System.out.println("No such file: " + file);
                return;
            }
            long lines = 0, words = 0, chars = 0;
            for (String line : Files.readAllLines(file)) {
                lines++;
                chars += line.length() + 1;
                if (!line.trim().isEmpty()) words += line.trim().split("\\s+").length;
            }
            System.out.println(lines + " " + words + " " + chars + " " + file.getFileName());
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    public void chooseCommandAction(String command, String[] args) {
        try {
            switch (command) {
                case "pwd": System.out.println(pwd()); break;
                case "cd": cd(args); break;
                case "ls": ls(); break;
                case "mkdir": mkdir(args); break;
                case "rmdir": rmdir(args); break;
                case "cp -r": cp_r(args); break;
                case "touch": touch(args); break;
                case "rm": rm(args); break;
                case "cp": cp(args); break;
                case "cat": cat(args); break;
                case "wc": wc(args); break;
                default: System.out.println("Unknown command: " + command);
            }
        } catch (Exception e) {
            System.out.println("Error executing command: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Terminal terminal = new Terminal();
        while (true) {
            String input = in.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) break;
            if (!parser.parse(input)) continue;
            terminal.chooseCommandAction(parser.getCommandName(), parser.getArgs());
        }
        in.close();
    }
}