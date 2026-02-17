package GUI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class InfoPanel extends JPanel{
    private MainFrame mainFrame;
    private PanelDecorator decorator;
    private List<String> titles;
    List<List<JLabel>> dataEntries;

    public InfoPanel(List<String> titles, List<List<JLabel>> dataEntries, PanelDecorator decorator) {
        this.titles = titles;
        this.dataEntries = dataEntries;
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBackground(Colors.panel());
        List<JLabel> headers = new ArrayList<>();
        for (String t : titles) {
            JLabel label = new JLabel(t);
            decorator.adjustBrandLabel(label);
            label.setBorder(BorderFactory.createLineBorder(Colors.border(), 5, true));
            headers.add(label);
        }
        for (int i = 0; i < headers.size(); i++) {
            JPanel wrapper = new JPanel();
            decorator.adjustWrapperPanel(wrapper);
            JPanel column = new JPanel();
            decorator.adjustInputPanel(column);
            column.setBorder(BorderFactory.createEmptyBorder());
            column.add(headers.get(i));
            for (List<JLabel> row : dataEntries) {
                column.add(row.get(i));
            }
            wrapper.add(column);
            add(Box.createHorizontalGlue());
            add(wrapper);
            add(Box.createHorizontalGlue());
            wrapper.setBorder(BorderFactory.createLineBorder(Colors.border(), 10, true));
        }
    }
}
