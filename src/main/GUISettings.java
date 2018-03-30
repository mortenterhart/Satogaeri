package main;

import javafx.scene.paint.Color;

public class GUISettings {

    public static final String fxmlPackagePath = "/gui/model/Satogaeri_Surface.fxml";
    public static final double circleRadius = 15;
    public static final double gridCellWidth = 45;
    public static final double gridCellHeight = 45;
    public static final int distanceLabelFontSize = 20;

    private static final Color defaultCircleOutlineColor = Color.YELLOW;
    private static final Color defaultInvariantRegionColor = Color.GREENYELLOW;

    public static Color circleOutlineColor = defaultCircleOutlineColor;
    public static Color invariantRegionColor = defaultInvariantRegionColor;

    private static final long defaultSleepInterval = 1000L;
    private static final long defaultCircleHighlightingTime = 500L;

    public static long sleepInterval = defaultSleepInterval;
    public static long circleHighlightingTime = defaultCircleHighlightingTime;

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
        circleHighlightingTime = defaultCircleHighlightingTime;
    }

    public static void setCircleOutlineColor(Color outlineColor) {
        circleOutlineColor = outlineColor;
    }

    public static void resetCircleOutlineColor() {
        circleOutlineColor = defaultCircleOutlineColor;
    }

    public static void setInvariantRegionColor(Color regionColor) {
        invariantRegionColor = regionColor;
    }

    public static void resetInvariantRegionColor() {
        invariantRegionColor = defaultInvariantRegionColor;
    }
}
