That's a great idea for a project\! A **README** file is essential for any GitHub repository, as it gives users and contributors an overview of the project.

Here is a comprehensive README file for your Command Line Interpreter (CLI) project, incorporating the assignment details and the team's contributions.

---

# 💻 Command Line Interpreter (CLI) Project

## ✨ Overview

This project implements a custom **Command Line Interpreter (CLI)** for an operating system, designed to mimic the functionality of common Unix/Linux shell commands. The CLI allows users to input commands via the keyboard, parses the input, and executes the specified command actions. It is written in **Java** as part of the Operating Systems 1 Course at Cairo University, Faculty of Computers & Artificial Intelligence.

[cite\_start]The CLI remains active, accepting commands until the user enters the special termination command: `exit`[cite: 7, 10, 42, 37].

## 🛠️ Implementation Details

### Program Structure

[cite\_start]The project is built around two core classes: `Parser` and `Terminal`[cite: 13].

| Class Name     | Description                                                                                                            | Key Methods                                                                                                                              |
| :------------- | :--------------------------------------------------------------------------------------------------------------------- | :--------------------------------------------------------------------------------------------------------------------------------------- |
| **`Parser`**   | Responsible for taking the user's input string, dividing it into the command name, and an array of arguments (`args`). | [cite\_start]`parse(String input)`, `getCommandName()`, `getArgs()` [cite: 14, 15, 16, 17, 18, 19, 20, 21]                               |
| **`Terminal`** | Contains the logic for executing each command and managing the overall CLI session.                                    | [cite\_start]`pwd()`, `cd(String[] args)`, `chooseCommandAction()`, `main(String[] args)` [cite: 22, 23, 24, 25, 26, 27, 28, 29, 30, 31] |

### Required Commands Implemented

The following standard and advanced shell commands have been implemented:

| Category              | Command | Description                                                                                                                                            |
| :-------------------- | :------ | :----------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Basic File System** | `pwd`   | [cite\_start]Prints the current path (current working directory)[cite: 33].                                                                            |
|                       | `cd`    | [cite\_start]Changes the current directory, supporting: home directory (no args), parent directory (`..`), and specific full/relative paths[cite: 33]. |
|                       | `ls`    | [cite\_start]Lists the contents of the current directory, sorted alphabetically[cite: 33].                                                             |
|                       | `mkdir` | [cite\_start]Creates one or more directories, supporting directory names or full/relative paths[cite: 33].                                             |
|                       | `rmdir` | [cite\_start]Removes directories, supporting: removing all empty directories (`*`) or removing a specific empty directory by path[cite: 33].           |
|                       | `touch` | [cite\_start]Creates a new file at a specified full or relative path[cite: 33].                                                                        |
| **File Manipulation** | `rm`    | [cite\_start]Removes a specified file in the current directory[cite: 34].                                                                              |
|                       | `cp`    | [cite\_start]Copies the content of the first file onto the second file[cite: 33].                                                                      |
|                       | `cp -r` | [cite\_start]Recursively copies the first directory (with all its content) into the second directory[cite: 33].                                        |
| **File Content**      | `cat`   | [cite\_start]Prints the content of one file or concatenates and prints the content of two files[cite: 34].                                             |
|                       | `wc`    | [cite\_start]Prints the line count, word count, and character count of a specified file[cite: 34].                                                     |
| **Advanced**          | `>`     | [cite\_start]**Output Redirection:** Redirects the output of a command to a file, replacing its existing content[cite: 34].                            |
|                       | `>>`    | [cite\_start]**Output Appending:** Redirects the output of a command to a file, appending to its existing content[cite: 34].                           |
|                       | `zip`   | [cite\_start]Compresses one or more files/directories (with the `-r` option) into a new `.zip` archive[cite: 34].                                      |
|                       | `unzip` | [cite\_start]Extracts all files from a `.zip` archive, with an optional destination directory (`-d`)[cite: 34].                                        |
| **Control**           | `exit`  | [cite\_start]Terminates the Command Line Interpreter[cite: 10, 37].                                                                                    |

### 🚨 Error Handling

The CLI includes robust error handling. [cite\_start]If a user enters a wrong command or provides bad parameters (e.g., an invalid path, using a file instead of a directory), the program prints a helpful error message without terminating[cite: 38].

---

## 👨‍💻 Team Contribution

This project was a collaborative effort. The following team members were responsible for the specified components:

| Team Member           | Contribution                                                                                                                                                                                                                                                |
| :-------------------- | :----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| [**Marwan Hussein**](www.linkedin.com/in/marawan-hussein-568373314)    | Implemented the **`Parser`** class. Developed the `Terminal`'s main execution loop and `chooseCommandAction()`. Implemented the advanced **`cp -r`**, **`zip`**, and **`unzip`** commands. Handled the comprehensive **error handling** throughout the code. |
| [**Abdullah**](https://www.linkedin.com/in/sumaya-yousif-07b2b6376?utm_source=share&utm_campaign=share_via&utm_content=profile&utm_medium=android_app)          | Implemented the core file system commands: **`pwd`**, **`cd`**, **`ls`**, **`mkdir`**, and **`rmdir`**.                                                                                                                                                      |
| [**Mahmoud Abdelaziz**](https://www.linkedin.com/in/mahmoud-abdelaziz-240012347?utm_source=share&utm_campaign=share_via&utm_content=profile&utm_medium=android_app) | Implemented file manipulation commands: **`touch`**, **`rm`**, **`cp`**, **`cat`**, and **`wc`**.                                                                                                                                                            |
| [**Sumaya Yousef**](https://www.linkedin.com/in/sumaya-yousif-07b2b6376?utm_source=share&utm_campaign=share_via&utm_content=profile&utm_medium=android_app)     | Implemented the output redirection commands: **`>`** and **`>>`**.                                                                                                                                                                                           |

---

## 🚀 How to Run

1.  **Clone the repository:**
    ```bash
    git clone [Your Repository URL]
    ```
2.  **Compile the Java code** (ensure you have the Java Development Kit installed):
    ```bash
    javac Terminal.java # Assuming Terminal.java is your main file
    ```
3.  **Run the CLI:**
    ```bash
    java Terminal
    ```
4.  The CLI will start, and you can begin entering commands:
    ```
    >pwd
    C:\current\path
    >mkdir test_dir
    >ls
    test_dir
    ...
    >exit
    ```

