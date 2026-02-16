package GUI;

import java.awt.*;

public class Fonts {
    private static Font headerFont = new Font("Inter", Font.BOLD, 46);
    private static Font semiHeaderFont = new Font ("Inter", Font.BOLD, 30);
    private static Font buttonFont = new Font("Inter", Font.BOLD, 22);
    private static Font labelFont = new Font("Inter", Font.BOLD, 22);
    private static Font inputFont = new Font("Inter", Font.PLAIN, 22);
    private static Font tinyFont = new Font("Inter", Font.BOLD, 12);

    public static Font getHeaderFont() {
        return headerFont;
    }
    public static Font getSemiHeaderFont(){return semiHeaderFont;}
    public static Font getButtonFont() {
        return buttonFont;
    }
    public static Font getLabelFont() {
        return labelFont;
    }
    public static Font getInputFont() {
        return inputFont;
    }

    public static Font getTinyFont() {
        return tinyFont;
    }
}