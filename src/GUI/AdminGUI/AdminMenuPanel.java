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
        setOpaque(true);
        getButtons();
        JPanel menuButtons = new JPanel();
        menuButtons.setBackground(Colors.panel());
        menuButtons.setOpaque(true);
        menuButtons.setLayout(new BoxLayout(menuButtons, BoxLayout.Y_AXIS));
        menuButtons.setAlignmentX(Component.CENTER_ALIGNMENT);
        for (JButton button : allOptionButtons) {
            JPanel singleButtonPanel = new JPanel();
            decorator.adjustInputPanel(singleButtonPanel);
            decorator.adjustButton(button);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            singleButtonPanel.add(button);
            menuButtons.add(Box.createVerticalStrut(15));
            menuButtons.add(singleButtonPanel);
            menuButtons.add(Box.createVerticalStrut(15));
            menuButtons.setBorder(BorderFactory.createLineBorder(Colors.bg(), 10, true));
        }
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Colors.panel());
        buttonPanel.setOpaque(true);
        buttonPanel.setPreferredSize(menuButtons.getPreferredSize());
        buttonPanel.setMaximumSize(menuButtons.getMaximumSize());
        buttonPanel.add(menuButtons);
        add(buttonPanel, BorderLayout.CENTER);
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
                    Update(new Event(Event.Phase.SELECT, Event.Action.LOG_OUT, Event.Subject.ADMIN, Event.Origin.GUI, Event.Outcome.PENDING, null, null));
                } catch (SQLException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }
    public void Update(Event event ) throws SQLException, ClassNotFoundException {
        event.setExtraContents(Event.Subject.ADMIN);
        mainFrame.Update(event);
    }
}