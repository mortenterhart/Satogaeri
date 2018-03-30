package main;

import javafx.scene.paint.Color;

public class GUISettings {

    public static final String fxmlPackagePath = "/gui/model/Satogaeri_Surface.fxml";
    public static final double circleRadius = 15;
    public static final double gridCellWidth = 45;
    public static final double gridCellHeight = 45;
    public static final int distanceLabelFontSize = 20;

    public static Color circleOutlineColor = Color.YELLOW;
    public static Color invariantRegionColor = Color.GREENYELLOW;

    private static final long defaultSleepInterval = 1000L;
    private static final long defaultCircleHighligtingTime = 500L;

    public static long sleepInterval = defaultSleepInterval;
    public static long circleHighlightingTime = defaultCircleHighligtingTime;

    public static void setSleepInterval(long interval) {
        sleepInterval = interval;
    }

    public static void resetSleepInterval() {
        sleepInterval = defaultSleepInterval;
    }

    public static void setCircleHighlightingTime(long highlightingTime) {
        circleHighlightingTime = highlightingTime;
    }

    public static void resetCircleHighlightingTime() {
        circleHighlightingTime = defaultCircleHighligtingTime;
    }
}
