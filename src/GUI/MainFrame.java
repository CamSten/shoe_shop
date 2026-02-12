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
                returnButton.setFont(Fonts.getTextFont());
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
            addToCartButton.setFont(Fonts.getTextFont());
            addToCartButton.setBorder(BorderFactory.createLineBorder(Colors.accent(), 4, true));
            addToCartButton.addActionListener(e -> {
                try {
                    manager.Update(new Event(Event.Phase.SELECT, Event.Action.PURCHASE, Event.Subject.SHOE, Event.Origin.GUI, Event.Outcome.PENDING, currentProduct, null));
                } catch (SQLException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            });
            bottomPanel.add(addToCartButton, BorderLayout.EAST);

            bottomPanel.setVisible(true);
            revalidate();
            repaint();
            pack();
        }
    }

    private void showLoginPanel() {
        centerPanel.removeAll();
        loginPanel = new LoginPanel(manager, decorator);
        centerPanel.add(loginPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
        pack();
    }

    private void showMenuPanel(Event event) {
        adjustHeaderAndFooter("Choose what would you like to do");
        centerPanel.removeAll();
        menuPanel = new MenuPanel(this, decorator);
        centerPanel.add(menuPanel);
        revalidate();
        repaint();
    }
    private void showOptionsPanel(Event event) {
        adjustHeaderAndFooter("Browse shoes");
        centerPanel.removeAll();
        optionsPanel = new OptionsPanel(this, decorator, event);
        centerPanel.add(optionsPanel);
        revalidate();
        repaint();
    }
    private void showCartPanel(Event event) {
        adjustHeaderAndFooter("Your orders:");
        centerPanel.removeAll();
        cartPanel = new CartPanel(this, event, decorator);
        centerPanel.add(cartPanel);
        revalidate();
        repaint();
    }
    private void showPurchasePanel(Event event) {
        removeHeader();
        centerPanel.removeAll();
        purchasePanel = new PurchasePanel(this, event, decorator);
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
    private void adjustHeaderAndFooter(String headerText) {
        removeHeader();
        if (headerText != null && !headerText.isEmpty()) {
            headerPanel = new HeaderPanel(decorator, headerText);
            add(headerPanel, BorderLayout.NORTH);
        }
        bottomPanel.removeAll();
        if (backToMenu == null) {
            backToMenu = new JButton("Return to menu");
            backToMenu.setBackground(backgroundColor);
            backToMenu.setForeground(Colors.accent());
            backToMenu.setFont(Fonts.getTextFont());
            backToMenu.setBorder(BorderFactory.createLineBorder(Colors.accent(), 4, true));
            backToMenu.addActionListener(_ -> showMenuPanel(null));
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
                switch (event.getAction()) {
                    case CREATE_ACCOUNT -> showLoginPanel();
                    case VIEW -> showOptionsPanel(event);
                    case CHOOSE_TYPE -> {
                        if (event.getContents() instanceof Product) {
                            showSingleProductPanel(event);
                        }
                    }
                    case PURCHASE -> showPurchasePanel(event);
                }
            }
            case LOGIC -> {
                switch (event.getAction()) {
                    case VALIDATE -> {
                        switch (event.getOutcome()) {
                            case NOT_FOUND -> showLoginPanel(); // prompt create new account
                            case INVALID_INPUT -> showLoginPanel(); // prompt wrong password
                            case OK -> showMenuPanel(event);
                        }
                    }
                    case CREATE_ACCOUNT -> {
                        if (event.getOutcome() == Event.Outcome.OK) {
                            showMenuPanel(event);
                        }
                    }
                    case VIEW -> {
                        if (event.getPhase() == Event.Phase.DISPLAY && event.getSubject() == Event.Subject.SHOE) {
                            showOptionsPanel(event);
                        } else if (event.getPhase() == Event.Phase.COMPLETE && event.getSubject() == Event.Subject.CART) {
                            showCartPanel(event);
                        }
                    }
                    case PURCHASE -> showPurchasePanel(event);
                }
            }
        }
    }
}
