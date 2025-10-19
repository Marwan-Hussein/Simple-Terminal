import java.util.Scanner;
import java.util.zip.*;
import java.io.*;

import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

class Parser {
    String commandName;
    String[] args;

    public boolean parse(String input) {
        // getting command name
        int space_index = input.indexOf(' ', 0);
        this.commandName = input.substring(0, space_index).toLowerCase();

        // getting args[]
        String arguments = input.substring(space_index + 1, input.length());
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
    // 1. pwd - Takes no arguments and prints the current path
    public String pwd() {
        // Implementation
        return currentDirectory;
    }
    
    // 2. cd - Change directory with multiple cases
  
    
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
        //Check if the target is a relative path

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
    
    // 3. ls - Lists contents of current directory sorted alphabetically
    public void ls() {
        // Implementation
       
        File[] files = new File(currentDirectory).listFiles();
        Arrays.sort(files);
        for (File file : files) {
            System.out.println(file.getName());
        }

    }
    
    // 4. mkdir - Creates directories (1 or more arguments)
    public void mkdir(String[] args) {

        // Implementation for directory names and paths
        if (args.length == 0) {
            System.out.println("Error: No directory name provided");
            return;
        }
        //loop through args and create directories for each arg if args is path then create directories for each arg in the path
            //Create directory in this path
            for (String arg : args) {
                if (arg.contains("\\")) {
                     String [] FullPath = arg.split("\\\\");
                     String path = String.join("\\\\", Arrays.copyOf(FullPath, FullPath.length - 1));
                     Path TruePath=Paths.get(path);
                     if(!TruePath.isAbsolute()){
                        TruePath=Paths.get(currentDirectory).resolve(path);
                     }
                     String directory = FullPath[FullPath.length - 1];
                     if (new File(TruePath.toString()).exists()) {
                     if (!new File(TruePath + "\\" + directory).exists()) {
                             File file = new File(TruePath + "\\" + directory);
                             file.mkdirs();
                     }
                }
            }
                else{
                    if (!new File(arg ).exists()) {
                        File file = new File(currentDirectory+"\\"+arg);
                        file.mkdirs();
                
                       
                }
                     
            }
          
        }


                
            
           
         
    }
    
    // 5. rmdir - Remove directories with multiple cases
    public void rmdir(String[] args) {
        // Cases: "*", full path, relative path (only if empty)
        if (args.length == 0) {
            System.out.println("Error: No directory name provided");
            return;
        }
        if (args[0].equals("*")) {
            //Delete only empty files in the current directory
            File[] files = new File(currentDirectory).listFiles();
            for (File file : files) {
                if (file.isDirectory() && file.list().length == 0) {
                    file.delete();
                }
            }
           
        }
        if (!args[0].contains("\\")) {
              //Delete the target if empty
              
            String path = args[0];
            if (new File(path).isDirectory() && new File(path).list().length == 0) {
                File file = new File(path);
                file.delete();
            }
          
        }
        else  {
            //Delete the target if empty

            Path candidate = Paths.get(args[0]);
            if (!candidate.isAbsolute()) {
                candidate = Paths.get(currentDirectory).resolve(args[0]);
            }
            candidate = candidate.normalize();
            
            if (Files.isDirectory(candidate)){
               if(new File(candidate.toString()).list().length == 0) {
                File file = new File(candidate.toString());
                file.delete();
               }
               
            }  else {
                System.out.println("No such directory: " + candidate);
            }
           
        }

    }
    
    public void cp_r(String[] args) throws IOException {
        File fromDir = new File(args[0]);
        File toDir = new File(args[1]);

        // creating one if dest is not exists
        if (!toDir.exists()) {
            boolean created = toDir.mkdirs();
        }

        File[] from_files = fromDir.listFiles();
        if (from_files == null || from_files.length == 0)
            System.out.println(args[0] + " is empty!");

        // coping ==>
        for (File f : from_files) {
            Path sourcePath = f.toPath();
            Path destPath = new File(toDir, f.getName()).toPath();
            try {
                Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void chooseCommandAction() {
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String input;
        do {
            input = in.nextLine();
            input = input.trim();

            parser.parse(input);
        } while (parser.getCommandName() != "exit");
    }
}
