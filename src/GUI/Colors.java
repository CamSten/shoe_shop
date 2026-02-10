package GUI;

import java.awt.*;

public class Colors {
    private static Color buttonBackgroundColor = new Color(145, 157, 153);
    private static Color backgroundColor = new Color(47, 36, 58);


//    private static Color buttonBackgroundColor


    private static Color borderColor = new Color(93, 88, 116);
    //            new Color(107, 99, 93);
//    private static Color backgroundColor = new Color(219, 211, 201);
    private static Color buttonTextColor = new Color(107, 99, 93);
    //        new Color(169, 172, 153);
    private static Color headerColor =new Color(145, 157, 153);
    //    new Color(107, 99, 93);
    private Color textColor = new Color(5, 4, 4);

    private static Color buttonHoverColor = new Color(155, 165, 175);

    public static Color getBackgroundColor() {
        return backgroundColor;
    }

    public static Color getButtonBackgroundColor() {
        return buttonBackgroundColor;
    }

    public static Color getButtonTextColor() {
        return buttonTextColor;
    }

    public static Color getHeaderColor() {
        return headerColor;
    }

    public Color getTextColor() {
        return textColor;
    }
    public static Color getBorderColor(){
        return borderColor;
    }

    public static Color getButtonHoverColor(){
        return buttonHoverColor;
    }
}
