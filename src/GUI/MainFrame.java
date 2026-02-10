package GUI;
import Control.ApplicationManager;
import Control.Event;
import Control.Subscriber;
import Model.Customer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

public class MainFrame extends JFrame implements Subscriber {
    private static JPanel centerPanel;
    private final JPanel topPanel;
    private  LoginPanel loginPanel;
    private MenuPanel menuPanel;
    private PanelDecorator decorator;
    private OptionsPanel optionsPanel;
    private CartPanel cartPanel;
    private JPanel bottomPanel;
    private final Color backgroundColor = Color.darkGray;
    private Customer user;
    private final ApplicationManager manager;
    JButton backToMenu;

    public MainFrame(ApplicationManager manager) {
        if (bottomPanel != null ){
            bottomPanel.removeAll();
        }
        this.manager = manager;
        this.decorator = new PanelDecorator();
        setVisible(true);
        setLayout(new BorderLayout());
        setEnabled(true);
        setMinimumSize(new Dimension(500, 600));
        setBackground(backgroundColor);
        centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(backgroundColor);
        add(centerPanel, BorderLayout.CENTER);
        showLoginPanel();
        JTextArea points = new JTextArea();
        points.setVisible(true);
        points.setOpaque(false);
        topPanel = new JPanel();
        topPanel.add(points);
        topPanel.setBackground(backgroundColor);
        topPanel.setVisible(true);
        add(topPanel, BorderLayout.NORTH);
        bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(backgroundColor);
        add(bottomPanel, BorderLayout.SOUTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    manager.assessQuit(0);
                } catch (SQLException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });
        repaint();
        revalidate();
        pack();
    }
    private void showLoginPanel(){
        System.out.println();
        centerPanel.removeAll();
        this.loginPanel = new LoginPanel(manager, decorator);
        centerPanel.add(loginPanel, BorderLayout.CENTER);
        repaint();
        revalidate();
        pack();
    }
    public void showMenuPanel(){
        System.out.println("showMenuPanel in MainFrame is reached");
        if (bottomPanel != null){
            bottomPanel.removeAll();
        }
        centerPanel.removeAll();
        this.menuPanel = new MenuPanel(this);
        centerPanel.add(menuPanel);
        repaint();
        revalidate();
        pack();
    }
    private void addReturnButton() {
        bottomPanel.removeAll();
        if (backToMenu == null) {
            this.backToMenu = new JButton("Return to menu");
            backToMenu.setBackground(backgroundColor);
            backToMenu.setForeground(Colors.getBackgroundColor());
            backToMenu.setFont(Fonts.getTextFont());
            backToMenu.setBorder(
                    BorderFactory.createLineBorder(Colors.getHeaderColor(), 4, true));
            backToMenu.addActionListener(_ -> {
                try {
                    manager.Update(new Event(null, null, null, null, null, null, null));
                } catch (SQLException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        bottomPanel.add(backToMenu, BorderLayout.WEST);
        bottomPanel.setVisible(true);
        bottomPanel.setBackground(backgroundColor);
        repaint();
        revalidate();
        pack();
    }

    @Override
    public void Update(Event event) throws SQLException, ClassNotFoundException {
        System.out.println("In MAINFRAME update, action is: " + event.getAction() + " phase: " + event.getPhase() + " outcome is: " + event.getOutcome() + "subject is: " + event.getSubject() + " origin is: " + event.getOrigin());
        if (event.getContents() != null){
            System.out.println("event.getContents instance of: " + event.getContents().getClass());
        }
        if (event.getExtraContents() != null){
            System.out.println("event.getExtraContents instance of: " + event.getExtraContents().getClass());
        }
        switch (event.getOrigin()){
            case GUI -> {
                if (event.getAction() == Event.Action.VIEW){
                    manager.Update(event);
                }

            }
            case LOGIC -> {
                if (event.getAction() == Event.Action.CREATE_ACCOUNT && event.getOutcome() == Event.Outcome.ALREADY_EXISTS){
                    System.out.println("CASE ALREADY EXISTS IS REACHED");
                    loginPanel.promptDifferentName();
                }
                else if (event.getAction() == Event.Action.CREATE_ACCOUNT && event.getOutcome() != Event.Outcome.OK){
                    loginPanel.createNewAccount();
                }
                else if (event.getAction() == Event.Action.VALIDATE && event.getOutcome() == Event.Outcome.NOT_FOUND){
                    loginPanel.promptNoSuchUser();
                }
                else if (event.getAction() == Event.Action.VALIDATE && event.getOutcome() == Event.Outcome.FAILURE){
                    loginPanel.promptWrongPassword();
                }
                else if (event.getAction() == Event.Action.VALIDATE || event.getAction() == Event.Action.CREATE_ACCOUNT && event.getOutcome() == Event.Outcome.OK){
                    showMenuPanel();
                }
                else if (event.getAction() == Event.Action.VIEW && event.getPhase() == Event.Phase.DISPLAY && event.getOutcome() == Event.Outcome.OK && event.getSubject() != Event.Subject.NONE){
                    showOptionsPanel(event);
                }
                else if (event.getAction() == Event.Action.VIEW && event.getOutcome() == Event.Outcome.OK){
                    showMenuPanel();
                }
                else if (event.getAction() == Event.Action.VIEW){
                    panelActions(event);
                }
                else if (event.getOutcome() == Event.Outcome.OK && event.getSubject() == Event.Subject.CUSTOMER){
                    showMenuPanel();
                }
            }
        }
    }
    private void showOptionsPanel(Event event){
        System.out.println("show options panel in mainFrame is reached");
        if (bottomPanel != null){
            bottomPanel.removeAll();
        }
        addReturnButton();
        centerPanel.removeAll();
        this.optionsPanel = new OptionsPanel(this, decorator, event);
        centerPanel.add(optionsPanel);
        repaint();
        revalidate();
        pack();
    }
    private void showCartPanel(){
        this.cartPanel = new CartPanel(this);
    }

    private void panelActions(Event event){
        if (event.getSubject() == Event.Subject.SHOE){
            //showOptionsPanel(event);
        }
        else if (event.getSubject() == Event.Subject.CART){
            //showCartPanel();
        }
    }
}