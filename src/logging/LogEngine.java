package logging;

import main.Configuration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A logging instance writing all received messages to the
 * standard output channel and to a log file specified in
 * the {@link Configuration} class.
 */
public enum LogEngine {
    instance;

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private BufferedWriter writer;

    /**
     * Builds the logger with the log file path specified in the {@link Configuration}.
     */
    public void init() {
        try {
            writer = new BufferedWriter(new FileWriter(new File(Configuration.instance.logFilePath)));
        }
        catch (FileNotFoundException exc) {
            System.err.println("Error: Log File '" + Configuration.instance.logFilePath +
                               "' could not be created!");
            exc.printStackTrace();
        }
        catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    /**
     * Writes a message to the log file preceding all new line characters
     * with the current date prefix.
     *
     * @param message the message
     */
    public void write(String message, boolean appendDatePrefix) {
        try {
            if (appendDatePrefix) {
                String datePrefix = "[" + dateFormat.format(new Date()) + "]:  ";
                writer.write(datePrefix + message);
            }
            else {
                writer.write(message);
            }

            writer.flush();
        }
        catch (IOException ioe) {
            ioe.getMessage();
            ioe.printStackTrace();
        }
    }

    public void writeLine(String message, boolean appendDatePrefix) {
        try {
            if (appendDatePrefix) {
                String datePrefix = "[" + dateFormat.format(new Date()) + "]:  ";
                writer.write(datePrefix + message + Configuration.instance.lineSeparator);
            }
            else {
                writer.write(message + Configuration.instance.lineSeparator);
            }

            writer.flush();
        }
        catch (IOException ioe) {
            ioe.getMessage();
            ioe.printStackTrace();
        }
    }

    /**
     * Logs a message to the log file and the standard output.
     *
     * @param message          the message
     * @param appendDatePrefix true if date prefix should be appended,
     *                         false otherwise
     */
    public void log(String message, boolean appendDatePrefix) {
        write(message, appendDatePrefix);
        System.out.print(message);
    }

    public void log(String message) {
        log(message, true);
    }

    public void logln(String message, boolean appendDatePrefix) {
        writeLine(message, appendDatePrefix);
        System.out.println(message);
    }

    public void logln(String message) {
        logln(message, true);
    }

    /**
     * Writes a new line in the log file and on standard output.
     */
    public void newLine(boolean appendDatePrefix) {
        write("\n", appendDatePrefix);
        System.out.println();
    }

    /**
     * Closes the logging instance.
     */
    public void close() {
        try {
            writer.close();
        }
        catch (IOException ioe) {
            System.err.println("Fatal Error occurred during closing: IO Error (see the stacktrace)");
            ioe.printStackTrace();
        }
    }
}
