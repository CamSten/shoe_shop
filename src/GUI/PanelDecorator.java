package GUI;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PanelDecorator {
    private int shoePanelSize = 150;
    private int singleShoePanelSize = 300;

    public PanelDecorator(){ }

    public void adjustButton(JButton button){
        button.setFont(Fonts.getButtonFont());
        button.setBackground(Colors.button());
        button.setForeground(Colors.buttonText());
        button.setBorder(BorderFactory.createEmptyBorder(6,14,6,14));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { button.setBackground(Colors.buttonHover()); }
            public void mouseExited(MouseEvent e) { button.setBackground(Colors.button()); }
        });
    }
    public void adjustShoeCardButton(JButton button){
            button.setFont(Fonts.getTinyFont());
            button.setBackground(Colors.button());
            button.setForeground(Colors.buttonText());
//            button.setBorder(BorderFactory.createEmptyBorder(6,14,6,14));
        button.setPreferredSize(new Dimension(100, 30));
        button.setMinimumSize(new Dimension(100, 30));
        button.setMaximumSize(new Dimension(100, 30));
            button.setFocusPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { button.setBackground(Colors.buttonHover()); }
                public void mouseExited(MouseEvent e) { button.setBackground(Colors.button()); }
            });
    }

    public void adjustSmallLabel(JLabel label) {
        label.setForeground(Colors.text());
        label.setBackground(Colors.card()); // card, inte panel
        label.setFont(Fonts.getTinyFont());
        label.setOpaque(true);
    }
    public void adjustLabel(JLabel label){
        label.setFont(Fonts.getLabelFont());
        label.setForeground(Colors.text());
//        label.setOpaque(true);
//        label.setPreferredSize(new Dimension(100, 30));
    }
    public void adjustCardLabel(JLabel label){
        label.setFont(Fonts.getLabelFont());
        label.setForeground(Colors.text());
        label.setBackground(Colors.panel());
        label.setOpaque(true);
    }
    public void adjustBrandLabel(JLabel label){
        label.setFont(Fonts.getLabelFont());
        label.setForeground(Colors.buttonHover());
        label.setBackground(Colors.border());
        label.setOpaque(true);
    }
public void adjustCardText(JTextArea area){
    area.setEditable(false);
    area.setFont(Fonts.getTinyFont());
    area.setForeground(Colors.text());
    area.setBackground(Colors.border());
    area.setOpaque(true);
}
    public void adjustTextField(JTextField field){
        field.setFont(Fonts.getInputFont());
        field.setForeground(Colors.text());
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createLineBorder(Colors.border(), 2, true));
        field.setPreferredSize(new Dimension(250, 50));
        field.setMinimumSize(new Dimension(250, 50));
    }


    public void adjustSubmitButton(JButton submitButton) {
        submitButton.setFont(Fonts.getButtonFont());
        submitButton.setBackground(Colors.button());
        submitButton.setBorder(BorderFactory.createLineBorder(Colors.border(), 5, true));
        submitButton.setForeground(Colors.accent());
        submitButton.setPreferredSize(new Dimension(200, 45) );
        submitButton.setMinimumSize(new Dimension(200, 45));
        submitButton.setMinimumSize(new Dimension(200, 45));
    }


//    public void adjustSmallLabel(JLabel label) {
////        label.setPreferredSize(new Dimension(110, 50));
////        label.setMinimumSize(new Dimension(110, 50));
////        label.setMaximumSize(new Dimension(150, 50));
//        label.setForeground(Colors.text());
//        label.setBackground(Colors.panel());
//        label.setFont(Fonts.getTinyFont());
//        // label.setOpaque(true);
//    }

    public void adjustWrapperPanel(JPanel wrapperPanel){
        wrapperPanel.setBackground(Colors.panel());
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        wrapperPanel.setOpaque(true);
        wrapperPanel.setVisible(true);
        wrapperPanel.setEnabled(true);
//        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    public void adjustInputPanel(JPanel inputPanel){
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setOpaque(false);
        inputPanel.setVisible(true);
        inputPanel.setEnabled(true);
        inputPanel.setBackground(Colors.panel());
        inputPanel.setBorder(BorderFactory.createLineBorder(Colors.border(), 10, true));
    }

    public void editInputField(JTextField inputField) {
        inputField.setBackground(Color.WHITE);
        inputField.setForeground(Colors.text());
        inputField.setFont(Fonts.getInputFont());
        inputField.setEditable(true);
        inputField.setBorder(
                BorderFactory.createLineBorder(Colors.border(), 4, true));
    }

    public void adjustHeader(JTextArea header){
        header.setEditable(false);
        header.setOpaque(false);
        header.setFont(Fonts.getHeaderFont());
        header.setForeground(Colors.accent());
    }

    public void adjustTextArea(JTextArea textArea){
        textArea.setEditable(false);
        textArea.setBorder(
                BorderFactory.createLineBorder(Colors.bg(), 4, true)
        );
        textArea.setFont(Fonts.getButtonFont());
        textArea.setForeground(Colors.text());
        textArea.setBackground(Colors.panel());
        textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    public void adjustSingleShoePanel(JPanel shoePanel){
        shoePanel.setBackground(Colors.card());
        shoePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.border(), 1),
                BorderFactory.createEmptyBorder(12,12,12,12)
        ));
        shoePanel.setPreferredSize(new Dimension(shoePanelSize, shoePanelSize));
        shoePanel.setMinimumSize(new Dimension(shoePanelSize, shoePanelSize));
        shoePanel.setMaximumSize(new Dimension(shoePanelSize, shoePanelSize));
        shoePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    public void adjustShoeInfoPanel(JPanel shoePanel){
        shoePanel.setLayout(new BoxLayout(shoePanel, BoxLayout.Y_AXIS));
        // shoePanel.setBorder(BorderFactory.createLineBorder(Colors.bg(), 5, true));
        shoePanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Colors.border(), 5, true),
                BorderFactory.createEmptyBorder(12,12,12,12)
        ));
        shoePanel.setBackground(Colors.border());
       // shoePanel.setOpaque(false);
        shoePanel.setPreferredSize(new Dimension(singleShoePanelSize, singleShoePanelSize));
        shoePanel.setMinimumSize(new Dimension(singleShoePanelSize, singleShoePanelSize));
        shoePanel.setMaximumSize(new Dimension(singleShoePanelSize, singleShoePanelSize));
        shoePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    public void adjustSingleResultPanel(JPanel singleResultPanel){
        singleResultPanel.setLayout(new BoxLayout(singleResultPanel, BoxLayout.Y_AXIS));
        singleResultPanel.setBorder(BorderFactory.createLineBorder(Colors.border(), 5, true));
        singleResultPanel.setBackground(Colors.panel());
        singleResultPanel.setMinimumSize(new Dimension(300, Integer.MAX_VALUE));
        singleResultPanel.setMaximumSize(new Dimension(450, Integer.MAX_VALUE));
        singleResultPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    public void adjustSingleResultLine(JPanel singleResultPanel){
        singleResultPanel.setLayout(new BoxLayout(singleResultPanel, BoxLayout.X_AXIS));
        singleResultPanel.setBorder(BorderFactory.createLineBorder(Colors.border(), 5, true));
        singleResultPanel.setBackground(Colors.panel());
//        singleResultPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        singleResultPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
    }
}