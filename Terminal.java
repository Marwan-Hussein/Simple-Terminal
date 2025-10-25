import java.util.*;
import java.util.zip.*;
import java.io.*;
import java.nio.file.*;

// person1: marwan
class Parser {
    String commandName;
    String[] args;
    private String redirectOperator; // ">" or ">>"
    private String redirectFile;     // File name for redirection

    public boolean parse(String input) {
        // Reset redirection fields
        redirectOperator = null;
        redirectFile = null;
        
        // Check for redirection operators
        if (input.contains(" > ") || input.contains(" >> ")) {
            // Handle redirection
            String[] parts;
            if (input.contains(" >> ")) {
                parts = input.split(" >> ", 2);
                redirectOperator = ">>";
            } else {
                parts = input.split(" > ", 2);
                redirectOperator = ">";
            }
            
            if (parts.length == 2) {
                String commandPart = parts[0].trim();
                redirectFile = parts[1].trim();
                
                // Parse the command part
                return parseCommandPart(commandPart);
            }
        }
        
        // No redirection, parse normally
        return parseCommandPart(input);
    }
    
    private boolean parseCommandPart(String input) {
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
        this.args = arguments.split("\\s+");
        return true;
    }

    public String getCommandName() {
        return this.commandName;
    }

    public String[] getArgs() {
        return this.args;
    }
    
    public boolean hasRedirection() {
        return redirectOperator != null && redirectFile != null;
    }
    
    public String getRedirectOperator() {
        return redirectOperator;
    }
    
    public String getRedirectFile() {
        return redirectFile;
    }
}

public class Terminal {
    static Parser parser = new Parser();
    static String currentDirectory = System.getProperty("user.dir");

    //Abdullah
    // 1. pwd - print the current directory
    public String pwd() {
        return currentDirectory;
    }

    // 2. cd - supports no args, "..", relative and absolute paths
    public void cd(String[] args) {
        if (args.length == 0) {
            // cd with no args - go to home directory
            currentDirectory = System.getProperty("user.home");
            return;
        }

        String target = args[0];
        Path candidate;

        if ("..".equals(target)) {
            // Handle cd ..
            Path parent = Paths.get(currentDirectory).getParent();
            if (parent == null) {
                // Already at root
                System.out.println("Already at root directory.");
            } else {
                currentDirectory = parent.normalize().toString();
            }
        } else {
            // Handle cd with path
            candidate = Paths.get(target);
            if (!candidate.isAbsolute()) {
                // Convert relative path to absolute
                candidate = Paths.get(currentDirectory).resolve(candidate);
            }
            candidate = candidate.normalize();
            
            if (Files.exists(candidate) && Files.isDirectory(candidate)) {
                currentDirectory = candidate.toString();
            } else {
                System.out.println("No such directory: " + candidate);
            }
        }
    }

    // 3. ls - list contents sorted alphabetically
    public void ls() {
        File curr = new File(currentDirectory);
        File[] files = curr.listFiles();
        if (files == null) {
            System.out.println("Cannot access directory: " + currentDirectory);
            return;
        }
        
        // Sort files alphabetically
        Arrays.sort(files, Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER));
        
        for (File file : files) {
            System.out.println(file.getName());
        }
    }

    // 4. mkdir - create one or more directories
    public void mkdir(String[] args) {
        if (args.length == 0) {
            System.out.println("Error: No directory name provided");
            return;
        }

        for (String arg : args) {
            Path candidate = Paths.get(arg);
            if (!candidate.isAbsolute()) {
                candidate = Paths.get(currentDirectory).resolve(candidate);
            }
            candidate = candidate.normalize();

            try {
                Files.createDirectories(candidate);
                System.out.println("Directory created: " + candidate.toString());
            } catch (IOException e) {
                System.out.println("Failed to create directory " + candidate.toString() + ": " + e.getMessage());
            }
        }
    }

    // 5. rmdir - remove empty directories
    public void rmdir(String[] args) {
        if (args.length == 0) {
            System.out.println("Error: No directory name provided");
            return;
        }

        String target = args[0];

        if ("*".equals(target)) {
            // Remove all empty directories in current directory
            File currentDir = new File(currentDirectory);
            File[] files = currentDir.listFiles();
            if (files == null) {
                System.out.println("Cannot access current directory: " + currentDirectory);
                return;
            }
            
            int removedCount = 0;
            for (File file : files) {
                if (file.isDirectory()) {
                    File[] contents = file.listFiles();
                    if (contents != null && contents.length == 0) {
                        if (file.delete()) {
                            removedCount++;
                            System.out.println("Removed empty directory: " + file.getName());
                        } else {
                            System.out.println("Failed to remove directory: " + file.getPath());
                        }
                    }
                }
            }
            if (removedCount == 0) {
                System.out.println("No empty directories found to remove.");
            }
            return;
        }

        // Remove specific directory
        Path candidate = Paths.get(target);
        if (!candidate.isAbsolute()) {
            candidate = Paths.get(currentDirectory).resolve(candidate);
        }
        candidate = candidate.normalize();

        File dir = candidate.toFile();
        if (!dir.exists()) {
            System.out.println("No such directory: " + candidate);
            return;
        }

        if (!dir.isDirectory()) {
            System.out.println("Path is not a directory: " + candidate);
            return;
        }

        String[] contents = dir.list();
        if (contents == null) {
            System.out.println("Cannot access directory: " + candidate);
            return;
        }

        if (contents.length == 0) {
            if (dir.delete()) {
                System.out.println("Removed directory: " + candidate);
            } else {
                System.out.println("Failed to remove directory: " + candidate);
            }
        } else {
            System.out.println("Directory not empty: " + candidate);
        }
    }
    // Person1: marwan
    public void cp_r(String[] args) throws IOException {
        if (args.length < 2) {
        System.out.println("Error: Usage: cp -r source_dir target_dir");
        return;
        }

        File fromDir = new File(args[0]);
        File toDir = new File(args[1]);

        // creating one if dest is not exists
        if (!toDir.exists() || !fromDir.exists()){System.out.println("the folder you entered doesn't exists!");return;}

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

    // marwan
    public void zip(String[] args) {
        if (args.length < 2) {
            System.out.println("Error: Usage: zip archive.zip file1 file2 ... OR zip -r archive.zip directory/");
            return;
        }

        try {
            boolean recursive = false;
            String zipFileName;
            List<String> sources = new ArrayList<>();

            // Check for -r flag
            if (args[0].equals("-r") && args.length >= 3) {
                recursive = true;
                zipFileName = args[1];
                sources = Arrays.asList(Arrays.copyOfRange(args, 2, args.length));
            } else {
                zipFileName = args[0];
                sources = Arrays.asList(Arrays.copyOfRange(args, 1, args.length));
            }

            // Ensure .zip extension
            if (!zipFileName.endsWith(".zip")) {
                zipFileName += ".zip";
            }

            Path zipPath = Paths.get(zipFileName);
            if (!zipPath.isAbsolute()) {
                zipPath = Paths.get(currentDirectory).resolve(zipPath);
            }

            try (FileOutputStream fos = new FileOutputStream(zipPath.toFile());
                 ZipOutputStream zos = new ZipOutputStream(fos)) {

                for (String source : sources) {
                    Path sourcePath = Paths.get(source);
                    if (!sourcePath.isAbsolute()) {
                        sourcePath = Paths.get(currentDirectory).resolve(sourcePath);
                    }

                    File sourceFile = sourcePath.toFile();

                    if (recursive && sourceFile.isDirectory()) {
                        addDirectoryToZip(sourceFile, sourceFile.getName(), zos); // private method
                    } else if (sourceFile.isFile()) {
                        addFileToZip(sourceFile, sourceFile.getName(), zos); // private method
                    } else if (sourceFile.isDirectory()) {
                        // Add all files in directory (non-recursive)
                        File[] files = sourceFile.listFiles();
                        if (files != null) {
                            for (File file : files) {
                                if (file.isFile()) {
                                    addFileToZip(file, sourceFile.getName() + "/" + file.getName(), zos);
                                }
                            }
                        }
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error creating zip: " + e.getMessage());
        }
    }

    private void addFileToZip(File file, String entryName, ZipOutputStream zos) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            ZipEntry zipEntry = new ZipEntry(entryName);
            zos.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zos.write(bytes, 0, length);
            }
            zos.closeEntry();
        }
    }

    private void addDirectoryToZip(File directory, String basePath, ZipOutputStream zos) throws IOException {
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isFile()) {
                addFileToZip(file, basePath + "/" + file.getName(), zos);
            } else if (file.isDirectory()) {
                addDirectoryToZip(file, basePath + "/" + file.getName(), zos);
            }
        }
    }


    // marwan
    public void unzip(String[] args) {
        if (args.length == 0) {
            System.out.println("Error: Usage: unzip archive.zip OR unzip archive.zip -d /path/to/destination/");
            return;
        }

        try {
            String zipFileName = args[0];
            Path destPath = Paths.get(currentDirectory); // default to current directory

            // Check for -d option
            if (args.length >= 3 && args[1].equals("-d")) {
                destPath = Paths.get(args[2]);
                if (!destPath.isAbsolute()) {
                    destPath = Paths.get(currentDirectory).resolve(destPath);
                }
            }

            Path zipPath = Paths.get(zipFileName);
            if (!zipPath.isAbsolute()) {
                zipPath = Paths.get(currentDirectory).resolve(zipPath);
            }

            if (!Files.exists(zipPath)) {
                System.out.println("Zip file not found: " + zipPath);
                return;
            }

            // Create destination directory if it doesn't exist
            Files.createDirectories(destPath);

            try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipPath.toFile()))) {
                ZipEntry zipEntry;
                while ((zipEntry = zis.getNextEntry()) != null) {
                    Path newFile = destPath.resolve(zipEntry.getName());

                    // Create parent directories if needed
                    Files.createDirectories(newFile.getParent());

                    if (!zipEntry.isDirectory()) {
                        try (FileOutputStream fos = new FileOutputStream(newFile.toFile())) {
                            byte[] bytes = new byte[1024];
                            int length;
                            while ((length = zis.read(bytes)) >= 0) {
                                fos.write(bytes, 0, length);
                            }
                        }
                    } else {
                        Files.createDirectories(newFile);
                    }
                    zis.closeEntry();
                }
            }

        } catch (IOException e) {
            System.out.println("Error extracting zip: " + e.getMessage());
        }
    }
    
    
    
    // Execute command with output redirection
    private void executeCommandWithRedirection(String command, String[] args) {
        String redirectFile = parser.getRedirectFile();
        boolean append = parser.getRedirectOperator().equals(">>");
        
        try {
            // Resolve file path (handle relative and absolute paths)
            Path filePath = Paths.get(redirectFile);
            if (!filePath.isAbsolute()) {
                filePath = Paths.get(currentDirectory).resolve(filePath);
            }
            filePath = filePath.normalize();
            
            // Create parent directories if they don't exist
            Files.createDirectories(filePath.getParent());
            
            // Capture command output
            String commandOutput = captureCommandOutput(command, args);
            
            // Write to file
            try (FileWriter writer = new FileWriter(filePath.toFile(), append)) {
                writer.write(commandOutput);
                if (!commandOutput.endsWith("\n") && !commandOutput.isEmpty()) {
                    writer.write("\n");
                }
                System.out.println("Output redirected to: " + filePath);
            }
            
        } catch (IOException e) {
            System.out.println("Error redirecting output to " + redirectFile + ": " + e.getMessage());
        }
    }

    // Capture the output of a command as a string
    private String captureCommandOutput(String commandName, String[] args) throws IOException {
        StringBuilder output = new StringBuilder();
        
        switch (commandName) {
            case "pwd":
                output.append(pwd());
                break;
            case "ls":
                output.append(captureLsOutput());
                break;
            case "wc":
                output.append(captureWcOutput(args));
                break;
            case "cat":
                output.append(captureCatOutput(args));
                break;
            default:
                // For other commands, show warning and execute normally
                System.out.println("Warning: Redirection not supported for command: " + commandName);
                executeNormalCommand(commandName, args);
                return "";
        }
        
        return output.toString();
    }
    
    // Add this method to capture cat output
    private String captureCatOutput(String[] args) {
        if (args.length == 0) {
            return "Error: No file specified";
        }
        
        StringBuilder content = new StringBuilder();
        try {
            if (args.length == 1) {
                Path file = Paths.get(args[0]);
                if (!file.isAbsolute()) file = Paths.get(currentDirectory).resolve(file);
                if (Files.exists(file)) {
                    content.append(new String(Files.readAllBytes(file)));
                } else {
                    return "No such file: " + file;
                }
            } else if (args.length == 2) {
                // Concatenate two files
                Path file1 = Paths.get(args[0]);
                Path file2 = Paths.get(args[1]);
                if (!file1.isAbsolute()) file1 = Paths.get(currentDirectory).resolve(file1);
                if (!file2.isAbsolute()) file2 = Paths.get(currentDirectory).resolve(file2);
                
                if (Files.exists(file1)) {
                    content.append(new String(Files.readAllBytes(file1)));
                } else {
                    content.append("No such file: ").append(file1).append("\n");
                }
                
                if (Files.exists(file2)) {
                    content.append(new String(Files.readAllBytes(file2)));
                } else {
                    content.append("No such file: ").append(file2).append("\n");
                }
            } else {
                return "Too many arguments for cat command";
            }
        } catch (IOException e) {
            return "Error reading file: " + e.getMessage();
        }
        
        return content.toString();
    }

    // Capture ls output as string
    private String captureLsOutput() {
        StringBuilder output = new StringBuilder();
        File curr = new File(currentDirectory);
        File[] files = curr.listFiles();
        
        if (files == null) {
            return "Cannot access directory: " + currentDirectory;
        }
        
        Arrays.sort(files, Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER));
        for (File file : files) {
            output.append(file.getName()).append("\n");
        }
        
        // Remove trailing newline if present
        if (output.length() > 0 && output.charAt(output.length() - 1) == '\n') {
            output.setLength(output.length() - 1);
        }
        
        return output.toString();
    }

   
    // Capture wc output as string
    private String captureWcOutput(String[] args) {
        if (args.length == 0) {
            return "Error: No file specified";
        }
        
        try {
            Path file = Paths.get(args[0]);
            if (!file.isAbsolute()) file = Paths.get(currentDirectory).resolve(file);
            if (!Files.exists(file)) {
                return "No such file: " + file;
            }
            
            long lines = 0, words = 0, chars = 0;
            for (String line : Files.readAllLines(file)) {
                lines++;
                chars += line.length() + 1;
                if (!line.trim().isEmpty()) words += line.trim().split("\\s+").length;
            }
            return lines + " " + words + " " + chars + " " + file.getFileName();
        } catch (IOException e) {
            return "Error reading file: " + e.getMessage();
        }
    }

    public void chooseCommandAction(String command, String[] args) {
        try {
            // Check if we have redirection
            if (parser.hasRedirection()) {
                executeCommandWithRedirection(command, args);
            } else {
                executeNormalCommand(command, args);
            }
        } catch (Exception e) {
            System.out.println("Error executing command: " + e.getMessage()+"\n");
        }
    }
    
    private void executeNormalCommand(String command, String[] args) throws IOException {
        switch (command) {
            case "pwd": System.out.println(pwd()); break; // the current path
            case "cd": cd(args); break;                   // cd: home | cd ..: prev | cd path: goto path
            case "ls": ls(); break;                       // list files in the dir
            case "mkdir": mkdir(args); break;             // make directory
            case "rmdir": rmdir(args); break;             // remove empty direcories
            case "cp -r": cp_r(args); break;              // copy dir's 1 content and copy it to dir2
            case "touch": touch(args); break;             // creates a file
            case "rm": rm(args); break;                   // remove a file
            case "cp": cp(args); break;                   // copy file's 1 content and copy it to file2 [replacement]
            case "cat": cat(args); break;                 // print the content of a file
            case "wc": wc(args); break;                   // #lines | #words | #chars | file_extension 
            case "zip": zip(args); break;
            case "unzip": unzip(args); break;
            
            default: System.out.println("Unknown command: " + command+"\n");
        }
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Terminal terminal = new Terminal();

        while (true) {
            System.out.print("> ");
            String input = in.nextLine().trim();

            if(input.isEmpty()) continue;
            if (input.equalsIgnoreCase("exit")) break;
            if (!parser.parse(input)) {
                System.out.println("Error: Invalid command format");
                continue;
            }

            terminal.chooseCommandAction(parser.getCommandName(), parser.getArgs());
        }
        in.close();
    }
}