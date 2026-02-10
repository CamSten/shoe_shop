package GUI;

import java.awt.*;

public class Fonts {
    private static Font headerFont = new Font("Inter", Font.BOLD, 32);
    private static Font buttonFont = new Font("Inter", Font.BOLD, 20);
    private static Font textFont = new Font ("Inter", Font.BOLD, 32);
    private static Font inputPromptFont = new Font("Inter", Font.BOLD, 18);

    public static Font getHeaderFont() {
        return headerFont;
    }
    public static Font getButtonFont() {
        return buttonFont;
    }
    public static Font getTextFont() {
        return textFont;
    }
    public static Font getInputPromptFont(){
        return inputPromptFont;
    }
}
