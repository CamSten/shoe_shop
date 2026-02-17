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
    private PanelDecorator decorator;
    private List<JButton> allOptionButtons = new ArrayList<>();

    public MenuPanel(MainFrame mainFrame, PanelDecorator decorator) {
        System.out.println("showMenuPanel constructor is reached");
        this.mainFrame = mainFrame;
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
        JButton optionBrowseSHoes = new JButton("Browse shoes");
        allOptionButtons.add(optionBrowseSHoes);
        optionBrowseSHoes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    System.out.println("--- IN MENU PANEL, SEE SHOES IS CHOSEN");
                    Update(Event.select(Event.Subject.SHOE));
                } catch (SQLException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        JButton optionSeeCart = new JButton("See items in cart");
        allOptionButtons.add(optionSeeCart);
        optionSeeCart.addActionListener(new ActionListener() {
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
        JButton optionEditDetails = new JButton("See account details");
        allOptionButtons.add(optionEditDetails);
        optionSeeCart.addActionListener(new ActionListener() {
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
        JButton optionLogOut = new JButton("Log out");
        allOptionButtons.add(optionLogOut);
        optionLogOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    System.out.println("--- IN MENU PANEL, LOG OUT IS CHOSEN");
                    Update(new Event(Event.Phase.SELECT, Event.Action.LOG_OUT, Event.Subject.CUSTOMER, Event.Origin.GUI, Event.Outcome.PENDING, null, null));
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
