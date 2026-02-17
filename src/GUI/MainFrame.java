package GUI;
import Control.ApplicationManager;
import Control.Event;
import Control.Subscriber;
import GUI.AdminGUI.AdminInfoPanel;
import GUI.AdminGUI.AdminMenuPanel;
import Model.DataHandling.ProductTerm;
import Model.DataHandling.Product;
import Model.DataHandling.Customer;

import javax.lang.model.util.SimpleElementVisitor6;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

public class MainFrame extends JFrame implements Subscriber {
    private final ApplicationManager manager;
    private final PanelDecorator decorator;
    private int customerId;
    private JPanel centerPanel;
    private JPanel bottomPanel;
    private HeaderPanel headerPanel;
    private LoginPanel loginPanel;
    private MenuPanel menuPanel;
    private EditCustomerPanel editPanel;
    private OptionsPanel optionsPanel;
    private CartPanel cartPanel;
    private PurchasePanel purchasePanel;
    private AdminMenuPanel adminMenu;
    private AdminInfoPanel adminInfoPanel;
    private JButton backToMenu;
    private final Color backgroundColor = Colors.bg();
    private final JButton addToCartButton;
    private JButton returnButton;
    private Product currentProduct;
    private Event currentEvent;
    private Customer currentCustomer;
    private JButton getAddToCartButton;
    int maxHeight = 700;

    public MainFrame(ApplicationManager manager) {
        this.manager = manager;
        this.decorator = new PanelDecorator();
        this.addToCartButton = new JButton("Add to Cart");
        decorator.adjustButton(addToCartButton);
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(800, 750));
    //   setMaximumSize(new Dimension(1500, 1200));
        setBackground(backgroundColor);
        centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(backgroundColor);
        add(centerPanel, BorderLayout.CENTER);
        bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Colors.panel());
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
        addToCartButton.setBorder(BorderFactory.createLineBorder(Colors.bg(), 10, true));
        addToCartButton.addActionListener(e -> {
            if (currentEvent != null) {
                if (purchasePanel != null) {
                    try {
                        purchasePanel.submitActions();
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                } else if (currentEvent.getAction() == Event.Action.CHOOSE_TYPE) {
                    showPurchasePanel(currentEvent);
                }
            }
        });
        showLoginPanel();
        setVisible(true);
        setEnabled(true);
        repaint();
        revalidate();
       // pack();
    }
    void showLoginPanel() {
        removeHeader();
        adjustHeaderAndFooter("ShoeShop", false, false, false);
        add(headerPanel, BorderLayout.NORTH);
        centerPanel.removeAll();
        this.loginPanel = new LoginPanel(manager, this, decorator);
        centerPanel.add(loginPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
    void showMenuPanel() {
        adjustHeaderAndFooter("Choose what you would like to do: ", false, false, false);
        centerPanel.removeAll();
        this.menuPanel = new MenuPanel(this, decorator);
        centerPanel.add(menuPanel);
        revalidate();
        repaint();
    }
    private void showOptionsPanel(Event event) {
        System.out.println("showOptionsPanel is reached");
        adjustHeaderAndFooter("Browse shoes", true, false, false);
        centerPanel.removeAll();
        this.optionsPanel = new OptionsPanel(this, decorator, event);
        centerPanel.add(optionsPanel);
        revalidate();
        repaint();
    }
    private void showCartPanel(Event event) {
        adjustHeaderAndFooter("Your orders:", true, false, false);
        centerPanel.removeAll();
        this.cartPanel = new CartPanel(this, event, decorator);
        centerPanel.add(cartPanel);
        revalidate();
        repaint();
    }
    private void showPurchasePanel(Event event) {
        System.out.println("showPurchasePanel is reached");
        adjustHeaderAndFooter("Choose color and size", true, true, true);
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
    public void adjustHeaderAndFooter(String headerText, boolean showBackToMenu, boolean showReturn, boolean showAdd) {
        System.out.println("headerText: " + headerText + " showBackToMenu: " + showBackToMenu + " showReturn: " + showReturn + " showAdd: " + showAdd);
        bottomPanel.removeAll();
        removeHeader();
        this.headerPanel = new HeaderPanel(decorator, headerText);
        add(headerPanel, BorderLayout.NORTH);
        if (showBackToMenu) {
            JPanel buttonPanel = new JPanel();
            backToMenu = new JButton("Return to menu");
            decorator.adjustWrapperPanel(buttonPanel);
            buttonPanel.add(backToMenu);
            decorator.adjustButton(backToMenu);
            backToMenu.setBorder(BorderFactory.createLineBorder(Colors.bg(), 10, true));
            backToMenu.addActionListener(_ -> showMenu());
            buttonPanel.add(backToMenu);
            bottomPanel.add(buttonPanel, BorderLayout.WEST);
            bottomPanel.setVisible(true);
        }
        if (showReturn) {
            JPanel buttonPanel = new JPanel();
            decorator.adjustWrapperPanel(buttonPanel);
            this.returnButton = new JButton("Return");
            decorator.adjustButton(returnButton);
            buttonPanel.add(returnButton);
            returnButton.setBorder(BorderFactory.createLineBorder(Colors.bg(), 10, true));
            returnButton.addActionListener(e -> {
                if (optionsPanel != null && currentEvent != null) {
                    showOptionsPanel(currentEvent);
                }
            });
            buttonPanel.add(returnButton);
            bottomPanel.add(buttonPanel, BorderLayout.EAST);
        }
        if (showAdd) {
            if (showAdd) {
                JPanel buttonPanel = new JPanel();
                decorator.adjustWrapperPanel(buttonPanel);
                buttonPanel.add(addToCartButton);
                bottomPanel.add(buttonPanel, BorderLayout.CENTER);
            }
        }
        bottomPanel.setVisible(true);
        revalidate();
        repaint();
        //pack();
    }
    private void showMenu() {
        if (currentEvent.getExtraContents() != null && currentEvent.getExtraContents() instanceof Event.Subject subject && subject == Event.Subject.ADMIN) {
            showAdminMenu();
        } else {
            showMenuPanel();
        }
    }

    private void showEditPanel(){
        System.out.println("showEditPanel is reached");
//        if (currentEvent.getContents() != null && currentEvent.getContents() instanceof Customer customer){
            centerPanel.removeAll();
            this.editPanel = new EditCustomerPanel(this, decorator, currentCustomer);
            centerPanel.add(editPanel);
            revalidate();
            repaint();
    }
    private void showAdminMenu() {
        System.out.println("showAdminMenu is reached in MainFrame");
        adjustHeaderAndFooter("Choose what you would like to do: ", false, false, false);
        centerPanel.removeAll();
        this.adminMenu = new AdminMenuPanel(this, decorator);
        centerPanel.add(adminMenu);
        revalidate();
        repaint();
    }
    private void showAdminInfoPanel() {
        String headerText = "";
        switch (currentEvent.getSubject()) {
            case SALES -> {
                headerText = "Top 5 most sold products:";
            }
            case CART -> {
                headerText = "Active orders:";
            }
            case STOCK -> {
                headerText = "Current inventory:";
            }
            case NON_STOCK -> {
                headerText = "Current products out of stock:";
            }
        }
        adjustHeaderAndFooter(headerText, true, false, false);
        centerPanel.removeAll();
        this.adminInfoPanel = new AdminInfoPanel(this, decorator, currentEvent);
        centerPanel.add(adminInfoPanel);
        revalidate();
        repaint();
        pack();
    }
    private void adminActions(Event event) throws SQLException, ClassNotFoundException {
        System.out.println("adminActions in MainFrame is reached");
        switch (event.getOrigin()) {
            case GUI -> {
                manager.Update(event);
                if (event.getAction() == Event.Action.LOG_OUT){
                    showLoginPanel();
                }
            }
            case LOGIC -> {
                if (event.getSubject() == Event.Subject.ADMIN && event.getOutcome() == Event.Outcome.OK) {
                    showAdminMenu();
                } else {
                    showAdminInfoPanel();
                }
            }
        }
    }
    public Customer getCurrentCustomer(){
        return currentCustomer;
    }
    public void setCustomerId(int id){
        this.customerId = id;
    }
    public void setCurrentCustomer(Customer c){
        System.out.println("setCurrentCustomer is reached in MainFrame");
        this.currentCustomer = c;
    }
    public int getCustomerId(){
        return customerId;
    }
    public int getMaxHeight(){
        return maxHeight;
    }
    public void Update(Event event) throws SQLException, ClassNotFoundException {
        this.currentEvent = event;
        boolean admin = false;
        System.out.println("in MainFrame.UPDATE: Action=" + event.getAction() + ", Phase=" + event.getPhase() + ", Subject=" + event.getSubject() + ", Outcome=" + event.getOutcome() + ", Origin=" + event.getOrigin());
        if (event.getContents() != null) {
            System.out.println("Contents instance of: " + event.getContents().getClass());
        }
        if (event.getExtraContents() != null) {
            System.out.println("ExtraContents instance of: " + event.getExtraContents().getClass());
            if (event.getExtraContents() instanceof ProductTerm pt) {
                System.out.println("ProductTerm: " + pt);
            }
            if (event.getExtraContents() instanceof Event.Subject subject && subject == Event.Subject.ADMIN) {
                admin = true;
                adminActions(event);
            }
        }
        Event.Origin origin = event.getOrigin();
        if (!admin) {
            switch (origin) {
                case GUI -> {
                    switch (event.getAction()) {
                        case CREATE_ACCOUNT -> {
                            showLoginPanel();
                        }
                        case LOG_OUT -> {
                            showLoginPanel();
                            manager.Update(event);
                        }
                        case VIEW -> {
                            if (event.getOutcome() == Event.Outcome.PENDING && event.getPhase() == Event.Phase.SELECT || event.getSubject() == Event.Subject.CART) {
                                manager.Update(event);
                            } else {
                                showOptionsPanel(event);
                            }
                        }
                        case CHOOSE_TYPE -> {
                            if (event.getContents() instanceof Product) {
                                showPurchasePanel(event);
                            } else if (event.getContents() instanceof ProductTerm) {
                                manager.Update(event);
                            }
                        }
                        case EDIT -> {
                            //showEditPanel();
                            if (event.getOutcome() == Event.Outcome.PENDING && event.getPhase() == Event.Phase.SELECT) {
                                System.out.println("in mainFrame, case EDIT is reached");
                                if (currentCustomer != null) {
                                    showEditPanel();
                                }
                            }
                            else {
                                manager.Update(event);
                            }
                        }
                        case PURCHASE -> {
                            manager.Update(event);
                        }
                    }
                }
                    case LOGIC -> {
                        switch (event.getAction()) {
                            case EDIT -> {
                                if (event.getPhase() == Event.Phase.COMPLETE && event.getContents() instanceof Customer customer) {

                                    if (event.getOutcome() == Event.Outcome.OK) {
                                        setCurrentCustomer(customer);
                                        editPanel.showConfirmation();
                                    }
                                }
                            }
                            case VALIDATE -> {
                                if (event.getExtraContents() instanceof Integer id){
                                    this.customerId = id;
                                }
                                if (event.getPhase() == Event.Phase.AWAIT_INPUT && event.getSubject() == Event.Subject.NONE) {
                                    showLoginPanel();
                                } else {
                                    switch (event.getOutcome()) {
                                        case NOT_FOUND -> loginPanel.promptNoSuchUser();
                                        case INVALID_INPUT -> loginPanel.promptWrongPassword();
                                        case OK -> showMenuPanel();
                                    }
                                }
                            }
                            case CREATE_ACCOUNT -> {
                                if (event.getOutcome() == Event.Outcome.OK) {
                                    showMenuPanel();
                                } else if (event.getPhase() == Event.Phase.AWAIT_INPUT) {
                                    loginPanel.showCreateAccountPanel();
                                }
                            }
                            case VIEW -> {
                                System.out.println("case VIEW is reached");
                                if (event.getExtraContents() != null && event.getExtraContents() instanceof Event.Subject) {
                                    showAdminInfoPanel();
                                } else if (event.getPhase() == Event.Phase.DISPLAY && event.getSubject() == Event.Subject.SHOE) {
                                    showOptionsPanel(event);
                                } else if (event.getSubject() == Event.Subject.CART) {
                                    showCartPanel(event);
                                }
                            }
                            case PURCHASE -> {
                                if (event.getPhase() == Event.Phase.COMPLETE || event.getOutcome() == Event.Outcome.FAILURE || event.getOutcome() == Event.Outcome.OK) {
                                    purchasePanel.getConfirmationPanel(event);
                                    remove(addToCartButton);
                                } else {
                                    showPurchasePanel(event);
                                }
                            }
                        }
                    }
                }
            }
        }
    }