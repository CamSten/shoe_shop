package GUI;
import java.awt.*;
public class Colors {
    private static final Color BACKGROUND = new Color(143, 247, 167);
    private static final Color PANEL = new Color(186, 173, 194);
    private static final Color CARD = Color.WHITE;
    private static final Color ACCENT = new Color(186, 173, 194);
    //= new Color(255, 153, 51);
    private static final Color TEXT_PRIMARY = new Color(40, 40, 40);
    private static final Color TEXT_SECONDARY = new Color(90, 90, 90);
    private static final Color BORDER = new Color(200, 200, 200);
    private static final Color BUTTON_BG = ACCENT;
    private static final Color BUTTON_TEXT = Color.WHITE;
    private static final Color BUTTON_HOVER = new Color(255, 170, 85);
    public static Color bg() { return BACKGROUND; }
    public static Color panel() { return PANEL; }
    public static Color card() { return CARD; }
    public static Color accent() { return ACCENT; }
    public static Color text() { return TEXT_SECONDARY;}
    //return TEXT_PRIMARY; }
    public static Color textMuted() { return TEXT_SECONDARY; }
    public static Color border() { return BORDER; }
    public static Color button() { return BUTTON_BG; }
    public static Color buttonHover() { return BUTTON_HOVER; }
    public static Color buttonText() { return BUTTON_TEXT; } }
