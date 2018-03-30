package main;

public enum Configuration {
    instance;

    public final String projectName = "Satogaeri";

    public final String userDirectory = System.getProperty("user.dir");
    public final String fileSeparator = System.getProperty("file.separator");
    public final String lineSeparator = System.getProperty("line.separator");

    public final String logFilePath = userDirectory + fileSeparator + "log" + fileSeparator +
            projectName + ".log";
}
