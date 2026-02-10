package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PanelDecorator {

    public PanelDecorator(){

    }
    public void adjustSubmitButton(JButton submitButton) {
        submitButton.setFont(Fonts.getButtonFont());
        submitButton.setBackground(Colors.getButtonBackgroundColor());
        submitButton.setBorder(BorderFactory.createLineBorder(Colors.getBorderColor(), 5, true));
        submitButton.setForeground(Colors.getButtonTextColor());
        submitButton.setPreferredSize(new Dimension(200, 45) );
        submitButton.setMinimumSize(new Dimension(200, 45));
        submitButton.setMinimumSize(new Dimension(200, 45));
    }
    public void adjustButton(JButton button){
        button.setFont(Fonts.getButtonFont());
        button.setBackground(Colors.getButtonBackgroundColor());
        button.setForeground(Colors.getButtonTextColor());
        button.setBorder(
                BorderFactory.createLineBorder(Colors.getBorderColor(), 4, true)
        );
        button.setPreferredSize(new Dimension(200, 40));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(Colors.getButtonHoverColor());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Colors.getButtonBackgroundColor());
            }
        });
    }
    public void adjustNameButton(JButton button){
        button.setFont(Fonts.getButtonFont());
        button.setForeground(Colors.getHeaderColor());
        button.setBackground(Colors.getButtonTextColor());
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(
                BorderFactory.createLineBorder(Colors.getBorderColor(), 4, true)
        );
    }
    public void adjustPanel(int size, JPanel panel) {
        panel.setVisible(true);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(250, size * 60));
        panel.setMaximumSize(new Dimension(250, Integer.MAX_VALUE));
    }
    public void adjustWrapperPanel(JPanel wrapperPanel){
        wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.Y_AXIS));
        wrapperPanel.setBackground(Colors.getBackgroundColor());
        wrapperPanel.setOpaque(true);
        wrapperPanel.setVisible(true);
        wrapperPanel.setEnabled(true);
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }
    public void adjustInputPanel(JPanel inputPanel){
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setOpaque(true);
        inputPanel.setVisible(true);
        inputPanel.setEnabled(true);
        inputPanel.setBackground(Colors.getHeaderColor());
        inputPanel.setBorder(BorderFactory.createLineBorder(Colors.getHeaderColor(), 10, true));
    }
    public void adjustLabel(JLabel label){
        label.setForeground(Colors.getButtonTextColor());
        label.setBackground(Colors.getBorderColor());
        label.setPreferredSize(new Dimension(250, 45));
        label.setMinimumSize(new Dimension(250, 45));
        label.setMinimumSize(new Dimension(250, 45));
        label.setFont(Fonts.getInputPromptFont());
    }
    public void editInputField(JTextField inputField) {
        inputField.setBackground(Color.WHITE);
        inputField.setForeground(Colors.getButtonTextColor());
        inputField.setPreferredSize(new Dimension(150, 45));
        inputField.setMinimumSize(new Dimension(150, 45));
        inputField.setMinimumSize(new Dimension(150, 45));
        inputField.setFont(Fonts.getInputPromptFont());
        inputField.setBorder(
                BorderFactory.createLineBorder(Colors.getBorderColor(), 4, true));
    }
    public void adjustHeader(JTextArea header){
        header.setEditable(false);
        header.setOpaque(false);
        header.setFont(Fonts.getButtonFont());
        header.setForeground(Colors.getHeaderColor());
        header.setBackground(Colors.getBackgroundColor());
        header.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    public void adjustTextArea(JTextArea textArea){
        textArea.setEditable(false);
        textArea.setBorder(
                BorderFactory.createLineBorder(Colors.getBorderColor(), 4, true)
        );
        textArea.setFont(Fonts.getButtonFont());
        textArea.setForeground(Colors.getBackgroundColor());
        textArea.setBackground(Colors.getButtonBackgroundColor());
        textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }
    public void adjustTextField(JTextField textArea){
        textArea.setEditable(false);
        textArea.setFont(Fonts.getButtonFont());
        textArea.setForeground(Colors.getHeaderColor());
        textArea.setBackground(Colors.getBackgroundColor());
        textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    public void adjustSingleResultPanel(JPanel singleResultPanel){
        singleResultPanel.setLayout(new BoxLayout(singleResultPanel, BoxLayout.Y_AXIS));
        singleResultPanel.setBorder(BorderFactory.createLineBorder(Colors.getBorderColor(), 5, true));
        singleResultPanel.setBackground(Colors.getButtonBackgroundColor());
        singleResultPanel.setMaximumSize(new Dimension(450, Integer.MAX_VALUE));
        singleResultPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
    }
    public void adjustSingleResultLine(JPanel singleResultPanel){
        singleResultPanel.setLayout(new BoxLayout(singleResultPanel, BoxLayout.X_AXIS));
        singleResultPanel.setBorder(BorderFactory.createLineBorder(Colors.getBorderColor(), 5, true));
        singleResultPanel.setBackground(Colors.getButtonBackgroundColor());
        singleResultPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        singleResultPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
    }
}
