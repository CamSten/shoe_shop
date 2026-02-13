package GUI;
import Control.ApplicationManager;
import Control.Event;
import Control.Subscriber;
import Model.Customer;
import Model.Product;
import Model.ProductTerm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

public class MainFrame extends JFrame implements Subscriber {
    private final ApplicationManager manager;
    private final PanelDecorator decorator;
    private JPanel centerPanel;
    private JPanel topPanel;
    private JPanel bottomPanel;
    private HeaderPanel headerPanel;
    private LoginPanel loginPanel;
    private MenuPanel menuPanel;
    private OptionsPanel optionsPanel;
    private CartPanel cartPanel;
    private PurchasePanel purchasePanel;
    private JButton backToMenu;
    private final Color backgroundColor = Colors.bg();
    private JButton addToCartButton;
    private JButton returnButton;
    private Product currentProduct;

    public MainFrame(ApplicationManager manager) {
        this.manager = manager;
        this.decorator = new PanelDecorator();
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(800, 600));
        setBackground(backgroundColor);
        centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(backgroundColor);
        add(centerPanel, BorderLayout.CENTER);
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
        showLoginPanel();
        setVisible(true);
        setEnabled(true);
        repaint();
        revalidate();
        pack();
    }

    private void showSingleProductPanel(Event event) {
        System.out.println("showSingleProductPanel is reached");
        if (event.getContents() instanceof Product) {
            this.currentProduct = (Product) event.getContents();

            centerPanel.removeAll();
            SingleProductPanel singlePanel = new SingleProductPanel(currentProduct, decorator);
            centerPanel.add(singlePanel, BorderLayout.CENTER);
            bottomPanel.removeAll();
            if (returnButton == null) {
                this.returnButton = new JButton("Return");
                returnButton.setBackground(backgroundColor);
                returnButton.setForeground(Colors.accent());
                returnButton.setFont(Fonts.getButtonFont());
                returnButton.setBorder(BorderFactory.createLineBorder(Colors.accent(), 4, true));
                returnButton.addActionListener(e -> {
                    if (optionsPanel != null && event != null) {
                        showOptionsPanel(event); // återgå till gamla optionspanel
                    }
                });
            }
            bottomPanel.add(returnButton, BorderLayout.WEST);

            addToCartButton = new JButton("Add to Cart");
            addToCartButton.setBackground(backgroundColor);
            addToCartButton.setForeground(Colors.accent());
            addToCartButton.setFont(Fonts.getButtonFont());
            addToCartButton.setBorder(BorderFactory.createLineBorder(Colors.accent(), 4, true));
            addToCartButton.addActionListener(e -> {
                showPurchasePanel(event);
            });
            bottomPanel.add(addToCartButton, BorderLayout.EAST);

            bottomPanel.setVisible(true);
            revalidate();
            repaint();
            pack();
        }
    }

    private void showLoginPanel() {
            removeHeader();
            headerPanel = new HeaderPanel(decorator, "ShoeShop");
            add(headerPanel, BorderLayout.NORTH);
            centerPanel.removeAll();
            this.loginPanel = new LoginPanel(manager, this, decorator);
            centerPanel.add(loginPanel, BorderLayout.CENTER);
            bottomPanel.removeAll();
            revalidate();
            repaint();
        }

    private void showMenuPanel() {
        adjustHeaderAndFooter("Choose what you would like to do: ");
        centerPanel.removeAll();
        this.menuPanel = new MenuPanel(this, decorator);
        centerPanel.add(menuPanel);
        revalidate();
        repaint();
    }
    private void showOptionsPanel(Event event) {
        System.out.println("showOptionsPanel is reached");
        adjustHeaderAndFooter("Browse shoes");
        centerPanel.removeAll();
        this.optionsPanel = new OptionsPanel(this, decorator, event);
        centerPanel.add(optionsPanel);
        revalidate();
        repaint();
    }
    private void showCartPanel(Event event) {
        adjustHeaderAndFooter("Your orders:");
        centerPanel.removeAll();
        this.cartPanel = new CartPanel(this, event, decorator);
        centerPanel.add(cartPanel);
        revalidate();
        repaint();
    }
    private void showPurchasePanel(Event event) {
        removeHeader();
        centerPanel.removeAll();
        this.purchasePanel = new PurchasePanel(this, event, decorator);
        centerPanel.add(purchasePanel);
        revalidate();
        repaint();
    }
    private void removeHeader() {
        if (headerPanel != null) {
            remove(headerPanel);
            headerPanel = null;
        }
    }
    public void adjustHeaderAndFooter(String headerText) {
        removeHeader();
        if (headerText != null && !headerText.isEmpty()) {
            this.headerPanel = new HeaderPanel(decorator, headerText);
            add(headerPanel, BorderLayout.NORTH);
        }
        bottomPanel.removeAll();
        if (backToMenu == null) {
            backToMenu = new JButton("Return to menu");
            backToMenu.setBackground(backgroundColor);
            backToMenu.setForeground(Colors.accent());
            backToMenu.setFont(Fonts.getButtonFont());
            backToMenu.setBorder(BorderFactory.createLineBorder(Colors.accent(), 4, true));
            backToMenu.addActionListener(_ -> showMenuPanel());
        }
        bottomPanel.add(backToMenu, BorderLayout.WEST);
        bottomPanel.setVisible(true);
    }

    public void Update(Event event) throws SQLException, ClassNotFoundException {
        System.out.println("in MainFrame.UPDATE: Action=" + event.getAction() +
                ", Phase=" + event.getPhase() +
                ", Subject=" + event.getSubject() +
                ", Outcome=" + event.getOutcome() +
                ", Origin=" + event.getOrigin());

        if (event.getContents() != null) {
            System.out.println("Contents instance of: " + event.getContents().getClass());
        }
        if (event.getExtraContents() != null) {
            System.out.println("ExtraContents instance of: " + event.getExtraContents().getClass());
            if (event.getExtraContents() instanceof ProductTerm pt) {
                System.out.println("ProductTerm: " + pt);
            }
        }

        Event.Origin origin = event.getOrigin();

        switch (origin) {
            case GUI -> {
                //(Event.Phase.SUBMIT, Event.Action.CHOOSE_TYPE, Event.Subject.SHOE, Event.Origin.GUI, Event.Outcome.PENDING, p, null
                switch (event.getAction()) {
                    case CREATE_ACCOUNT -> showLoginPanel();
                    case VIEW -> {
                        if (event.getOutcome() == Event.Outcome.PENDING && event.getPhase() == Event.Phase.SELECT || event.getSubject() == Event.Subject.CART) {
                            manager.Update(event);
                        } else {
                            showOptionsPanel(event);
                        }
                    }
                    case CHOOSE_TYPE -> {
                        if (event.getContents() instanceof Product) {
                            showSingleProductPanel(event);
                        }
                        else if (event.getContents() instanceof ProductTerm){
                            manager.Update(event);
                        }
                    }
                    case PURCHASE -> {
                        manager.Update(event);
                        //showPurchasePanel(event);
                        //if (event.getOutcome == Event.Outcome.PENDING){
                        //                            showSingleProductPanel(event);
                        //                        }
                    }
                }
            }
            case LOGIC -> {
                switch (event.getAction()) {
                    //        mainFrame.Update(new Event(Event.Phase.AWAIT_INPUT, Event.Action.VALIDATE, Event.Subject.CUSTOMER, Event.Origin.LOGIC, Event.Outcome.NOT_FOUND, null, null));
                    case VALIDATE -> {
                        if (event.getPhase() == Event.Phase.AWAIT_INPUT && event.getSubject() == Event.Subject.NONE){
                            showLoginPanel();
                            break;
                        }
                        switch (event.getOutcome()) {
                            case NOT_FOUND -> loginPanel.promptNoSuchUser();
                            case INVALID_INPUT -> loginPanel.promptWrongPassword();
                            case OK -> showMenuPanel();
                        }
                    }
                    case CREATE_ACCOUNT -> {
                        if (event.getOutcome() == Event.Outcome.OK) {
                            showMenuPanel();
                        }
                        else if (event.getPhase() == Event.Phase.AWAIT_INPUT){
                            loginPanel.showCreateAccountPanel();
                        }
                    }
                    case VIEW -> {
                        System.out.println("case VIEW is reached");
                        if (event.getPhase() == Event.Phase.DISPLAY && event.getSubject() == Event.Subject.SHOE) {
                            showOptionsPanel(event);
                        } else if (event.getSubject() == Event.Subject.CART) {
                            showCartPanel(event);
                        }
                    }
                    case PURCHASE -> {
                        if (event.getPhase() == Event.Phase.COMPLETE) {
                            purchasePanel.getConfirmationPanel(event);
                        } else {
                            showPurchasePanel(event);
                        }
                    }
                }
            }
        }
    }
}
