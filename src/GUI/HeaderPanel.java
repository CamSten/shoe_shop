package GUI;
import javax.swing.*;
import java.awt.*;

public class HeaderPanel extends JPanel {
    private PanelDecorator decorator;
    private String headerText;

    public HeaderPanel(PanelDecorator decorator, String headerText) {
        this.decorator = decorator;
        this.headerText = headerText;
        setBackground(Colors.bg());
        setLayout(new BorderLayout());
        add(getHeaderPanel(), BorderLayout.CENTER);
    }
    private JPanel getHeaderPanel() {
        JPanel headerPanel = new JPanel(new GridLayout(1,1));
        headerPanel.setBackground(Colors.bg());
        JTextArea header = new JTextArea(headerText);
        header.setEditable(false);
        header.setOpaque(false);
        header.setBorder(null);
        decorator.adjustHeader(header);
        headerPanel.add(header);
        return headerPanel;
    }
}