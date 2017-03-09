package com.github.anno4j.rdfs_parser.generation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Utility class for starting system commands.
 */
class SystemCommand {

    /**
     * Spawns a new process for the (native) command given.
     * @param command The command to execute with its arguments appended.
     * @return Returns the output of the command if it ran successful.
     * @throws IOException Thrown if an error occured while spawning the process
     * or the execution finished with non-zero exit status.
     */
    static String runCommand(String command) throws IOException {
        BufferedReader reader = null;

        try {
            // Launch the process:
            Process process = Runtime.getRuntime().exec(command);

            // Get stream of the console running the command:
            InputStreamReader streamReader = new InputStreamReader(process.getInputStream());
            reader = new BufferedReader(streamReader);

            // Read the output of the process:
            String currentLine;
            StringBuilder commandOutput = new StringBuilder();
            while ((currentLine = reader.readLine()) != null) {
                commandOutput.append(currentLine);
            }

            // Get the exit code of the process:
            int returnCode = process.waitFor();
            if (returnCode == 0) {
                return commandOutput.toString();
            } else {
                throw new IOException("Exited with code " + returnCode + ". Output: " + commandOutput.toString());
            }

        } catch (IOException | InterruptedException e) {
            throw new IOException(e.getMessage());

        } finally {

            // Close the stream from the process:
            if(reader != null) {
                reader.close();
            }
        }
    }
}
