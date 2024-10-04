# CS3100_2024_Assn3_Command_Shell

## Steps to Run:
Ensure the Structure:
/src
-/main
--/java
---/com
----/example
-----Shell.java
-----Help.java
-----Fib.java
-----Fac.java
-----E.java
-----Assn2.java
build.gradle

### Navigate to the project root directory
cd /path/to/project

### Clean the previous builds (optional but recommended)
gradle clean

### Build the project and create the JAR file
gradle build


### Navigate to the build/libs directory
cd build/libs

### Run the JAR file using Java
java -jar Assn2.jar

## You can type commands such as:

list: Lists the contents of the current directory. \
cd folder: Changes to a subdirectory. \
mkdir newfolder: Creates a new directory. \
rmdir newfolder: Removes an existing directory. \
history: Shows command history. \
ptime: Displays the total time spent on child processes. \
exit: Exits the shell. \