package GUI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import Control.Event;

public class MenuPanel extends JPanel {
    private MainFrame mainFrame;
    private List<JButton> allOptionButtons = new ArrayList<>();
    public MenuPanel(MainFrame mainFrame){
        this.mainFrame = mainFrame;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Colors.getBackgroundColor());
        setMinimumSize(new Dimension(550, 500));
        JLabel prompt = new JLabel("Choose what you would like to do:");
        prompt.setHorizontalAlignment(SwingConstants.CENTER);
        prompt.setFont(Fonts.getHeaderFont());
        prompt.setForeground(Colors.getHeaderColor());
        JPanel header = new JPanel(new GridLayout(2,1));
        header.add(prompt);
        header.setBackground(Colors.getBackgroundColor());
        add(header);
        setOpaque(true);

        getButtons();

        JPanel menuButtons = new JPanel();
        menuButtons.setBackground(Colors.getButtonBackgroundColor());
        menuButtons.setOpaque(true);
        menuButtons.setLayout(new BoxLayout(menuButtons, BoxLayout.Y_AXIS));
        menuButtons.setPreferredSize(new Dimension(300, allOptionButtons.size() * 60));
        menuButtons.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));
        menuButtons.setAlignmentX(Component.CENTER_ALIGNMENT);

        for (JButton button : allOptionButtons){
            button.setFont(Fonts.getButtonFont());
            button.setBackground(Colors.getButtonBackgroundColor());
            button.setForeground(Colors.getButtonTextColor());
            button.setPreferredSize(new Dimension(300, 45) );
            button.setMinimumSize(new Dimension(300, 45));
            button.setMinimumSize(new Dimension(300, 45));
            button.add(Box.createHorizontalStrut(300));
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setBorder(BorderFactory.createLineBorder(Colors.getBorderColor(), 5, true));
//
            menuButtons.add(Box.createVerticalStrut(5));
            menuButtons.add(button);
            menuButtons.add(Box.createVerticalStrut(5));
            menuButtons.setBorder(BorderFactory.createLineBorder(Colors.getButtonBackgroundColor(), 10, true));
        }
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Colors.getBackgroundColor());
        buttonPanel.setOpaque(true);
        buttonPanel.setPreferredSize(menuButtons.getPreferredSize());
        buttonPanel.setMaximumSize(menuButtons.getMaximumSize());
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonPanel.add(menuButtons);
        add(Box.createHorizontalGlue());
        add(buttonPanel);
        add(Box.createHorizontalGlue());
        repaint();
        revalidate();
    }

    private void getButtons() {
            JButton optionHandleSeeker = new JButton("Browse shoes");
            allOptionButtons.add(optionHandleSeeker);
            optionHandleSeeker.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        System.out.println( "--- IN MENU PANEL, SEE SHOES IS CHOSEN");
                        Update(Event.select(Event.Subject.SHOE));
                    } catch (SQLException | ClassNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
            JButton optionHandleOpening = new JButton("See items in cart");
            allOptionButtons.add(optionHandleOpening);
            optionHandleOpening.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        System.out.println( "--- IN MENU PANEL, SEE CART IS CHOSEN");
                        Update(Event.select(Event.Subject.CART));
                    } catch (SQLException | ClassNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
        }
    public void Update(Event event ) throws SQLException, ClassNotFoundException {
        System.out.println("UPDATE IN MENUPANEL IS REACHED, event.Subject is: " + event.getSubject());
        mainFrame.Update(event);
    }
}
