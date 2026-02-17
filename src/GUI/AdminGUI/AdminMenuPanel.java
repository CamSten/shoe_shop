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
        System.out.println("AdminMenuPanel constructor is reached");
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
        System.out.println("in AMP, GETBUTTONS IS REACHED");
        JButton optionSeeOrders = new JButton("See order history");
        allOptionButtons.add(optionSeeOrders);
        optionSeeOrders.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    System.out.println("--- IN ADMIN MENU PANEL, SEE ORDER HISTORY IS CHOSEN");
                    Update(Event.requestAdminInfo(Event.Subject.CART));
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
                    System.out.println("--- IN ADMIN MENU PANEL, SEE SALES IS CHOSEN");
                    Update(Event.requestAdminInfo(Event.Subject.SALES));
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
                    System.out.println("--- IN ADMIN MENU PANEL, SEE INV IS CHOSEN");
                    Update(Event.requestAdminInfo(Event.Subject.STOCK));
                } catch (SQLException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        JButton optionSeeOutOfStock = new JButton("See products out of stock: ");
        allOptionButtons.add(optionSeeOutOfStock);
        optionSeeOutOfStock.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    System.out.println("--- IN ADMIN MENU PANEL, SEE OUT OF STOCK IS CHOSEN");
                    Update(Event.requestAdminInfo(Event.Subject.NON_STOCK));
                } catch (SQLException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        JButton optionLogOut = new JButton("Log out");
        allOptionButtons.add(optionLogOut);
        optionLogOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    System.out.println("--- IN MENU PANEL, LOG OUT IS CHOSEN");
                    Update(new Event(Event.Phase.SELECT, Event.Action.LOG_OUT, Event.Subject.ADMIN, Event.Origin.GUI, Event.Outcome.PENDING, null, null));
                } catch (SQLException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }
    public void Update(Event event ) throws SQLException, ClassNotFoundException {
        System.out.println("UPDATE IN MENUPANEL IS REACHED, event.Subject is: " + event.getSubject());
        event.setExtraContents(Event.Subject.ADMIN);
        mainFrame.Update(event);
    }
}