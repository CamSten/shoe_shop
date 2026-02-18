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

    public MenuPanel(MainFrame mainFrame, PanelDecorator decorator) {
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
                    Update(Event.select(Event.Subject.CART));
                } catch (SQLException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        JButton optionEditDetails = new JButton("See account details");
        allOptionButtons.add(optionEditDetails);
        optionEditDetails.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Update(new Event(Event.Phase.SELECT, Event.Action.EDIT, Event.Subject.CUSTOMER, Event.Origin.GUI, Event.Outcome.PENDING, null, null));
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
                    Update(new Event(Event.Phase.SELECT, Event.Action.LOG_OUT, Event.Subject.CUSTOMER, Event.Origin.GUI, Event.Outcome.PENDING, null, null));
                } catch (SQLException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }
    public void Update(Event event ) throws SQLException, ClassNotFoundException {
        mainFrame.Update(event);
    }
}