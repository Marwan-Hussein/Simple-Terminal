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
