package GUI.AdminGUI;

import GUI.Colors;
import GUI.PanelDecorator;
import GUI.MainFrame;
import Control.Event;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminMenuPanel extends JPanel{
    private MainFrame mainFrame;
    private PanelDecorator decorator;
    private List<JButton> allOptionButtons = new ArrayList<>();

    public AdminMenuPanel( MainFrame mainFrame, PanelDecorator decorator) {
        this.mainFrame = mainFrame;
        this.decorator = decorator;
        setLayout(new BorderLayout());
        setBackground(Colors.bg());
//        setMinimumSize(new Dimension(550, 500));
        setOpaque(true);

        getButtons();

        JPanel menuButtons = new JPanel();
        menuButtons.setBackground(Colors.panel());
        menuButtons.setOpaque(true);
        menuButtons.setLayout(new BoxLayout(menuButtons, BoxLayout.Y_AXIS));
//        menuButtons.setPreferredSize(new Dimension(300, allOptionButtons.size() * 60));
//        menuButtons.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));
        menuButtons.setAlignmentX(Component.CENTER_ALIGNMENT);

        for (JButton button : allOptionButtons) {
            JPanel singleButtonPanel = new JPanel();
            decorator.adjustInputPanel(singleButtonPanel);
            decorator.adjustButton(button);
//            button.setFont(Fonts.getButtonFont());
//            button.setBackground(Colors.button());
//            button.setForeground(Colors.buttonText());
//            button.setPreferredSize(new Dimension(300, 45));
//            button.setMinimumSize(new Dimension(300, 45));
//            button.setMinimumSize(new Dimension(300, 45));
//            button.add(Box.createHorizontalStrut(300));
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
//            button.setBorder(BorderFactory.createLineBorder(Colors.border(), 5, true));
//
            singleButtonPanel.add(button);
            menuButtons.add(Box.createVerticalStrut(15));
            menuButtons.add(singleButtonPanel);
            menuButtons.add(Box.createVerticalStrut(15));
            menuButtons.setBorder(BorderFactory.createLineBorder(Colors.bg(), 10, true));
        }
//        JPanel buttonPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel();
//        buttonPanel.setBorder(BorderFactory.createLineBorder(Colors.buttonHover(), 4, true));
        buttonPanel.setBackground(Colors.panel());
        buttonPanel.setOpaque(true);
        buttonPanel.setPreferredSize(menuButtons.getPreferredSize());
        buttonPanel.setMaximumSize(menuButtons.getMaximumSize());
//        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
//        buttonPanel.add(menuButtons, BorderLayout.CENTER);
        buttonPanel.add(menuButtons);
//        add(Box.createHorizontalGlue());
        add(buttonPanel, BorderLayout.CENTER);
//        add(Box.createHorizontalGlue());
        repaint();
        revalidate();
    }

    private void getButtons() {
        JButton optionSeeOrders = new JButton("See order history");
        allOptionButtons.add(optionSeeOrders);
        optionSeeOrders.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    System.out.println("");
                    Update(Event.select(Event.Subject.SHOE));
                } catch (SQLException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        JButton optionSeeSales = new JButton("See sales overview");
        allOptionButtons.add(optionSeeSales);
        optionSeeSales.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    System.out.println("--- IN MENU PANEL, SEE CART IS CHOSEN");
                    Update(Event.select(Event.Subject.CART));
                } catch (SQLException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        JButton optionSeeInventory = new JButton("See current inventory");
        allOptionButtons.add(optionSeeInventory);
        optionSeeInventory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    System.out.println("--- IN MENU PANEL, ACCOUNT DETAILS IS CHOSEN");
                    Update(new Event(Event.Phase.SELECT, Event.Action.VIEW, Event.Subject.CUSTOMER, Event.Origin.GUI, Event.Outcome.PENDING, null, null));
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