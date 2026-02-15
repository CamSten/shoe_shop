package GUI;
import Control.ApplicationManager;
import Control.Event;
import Control.Subscriber;
import GUI.AdminGUI.AdminInfoPanel;
import GUI.AdminGUI.AdminMenuPanel;
import Model.DataHandling.ProductTerm;
import Model.DataHandling.Product;

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
    private SingleProductPanel singlePanel;
    private PurchasePanel purchasePanel;
    private AdminMenuPanel adminMenu;
    private AdminInfoPanel adminInfoPanel;
    private JButton backToMenu;
    private final Color backgroundColor = Colors.bg();
    private JButton addToCartButton;
    private JButton returnButton;
    private Product currentProduct;
    private Event currentEvent;
    private JButton getAddToCartButton;

    public MainFrame(ApplicationManager manager) {
        this.manager = manager;
        this.decorator = new PanelDecorator();
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(800, 750));
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
        showLoginPanel();
        setVisible(true);
        setEnabled(true);
        repaint();
        revalidate();
        pack();
    }

    private void showSingleProductPanel(Event event) {
        if (event.getContents() instanceof Product) {
            Product product = (Product) event.getContents();
            adjustHeaderAndFooter("Available options:", true, false ,true);

            this.currentProduct = (Product) event.getContents();
            centerPanel.removeAll();
            this.singlePanel = new SingleProductPanel(this, currentProduct, decorator);
            centerPanel.add(singlePanel, BorderLayout.CENTER);
            revalidate();
            repaint();
        }
    }

    private void showLoginPanel() {
            removeHeader();
//            headerPanel = new HeaderPanel(decorator, "ShoeShop");
        adjustHeaderAndFooter("ShoeShop", false, false, false);
            add(headerPanel, BorderLayout.NORTH);
            centerPanel.removeAll();
            this.loginPanel = new LoginPanel(manager, this, decorator);
            centerPanel.add(loginPanel, BorderLayout.CENTER);
//            bottomPanel.removeAll();
            revalidate();
            repaint();
        }

    private void showMenuPanel() {
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
        if(showBackToMenu) {
                JPanel buttonPanel = new JPanel();
                backToMenu = new JButton("Return to menu");
                decorator.adjustWrapperPanel(buttonPanel);
                buttonPanel.add(backToMenu);
                decorator.adjustButton(backToMenu);
                backToMenu.setBorder(BorderFactory.createLineBorder(Colors.bg(), 10, true));
                backToMenu.addActionListener(_ -> showMenuPanel());
                buttonPanel.add(backToMenu);
                bottomPanel.add(buttonPanel, BorderLayout.WEST);
                bottomPanel.setVisible(true);
        }

        if (showReturn){
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
               JPanel buttonPanel = new JPanel();
               decorator.adjustWrapperPanel(buttonPanel);
               if (addToCartButton != null){
                   System.out.println("in adjustHeaderAndFooter, addToCartButton != null");
                   currentEvent.setAction(Event.Action.PURCHASE);
                   addToCartButton.setText("Submit");
                   addToCartButton.setBackground(Color.WHITE);
                   addToCartButton.setBorder(BorderFactory.createLineBorder(Colors.buttonHover(), 10, true));
               }
               else {
                   this.addToCartButton = new JButton("Add to Cart");
                   addToCartButton.setBorder(BorderFactory.createLineBorder(Colors.bg(), 10, true));
                   decorator.adjustButton(addToCartButton);
                   addToCartButton.setBorder(BorderFactory.createLineBorder(Colors.bg(), 10, true));
               }

               addToCartButton.addActionListener(e -> {
                   if (currentEvent.getAction() == Event.Action.CHOOSE_TYPE) {
                       showPurchasePanel(currentEvent);
                   }
                   else{
                       try {
                           purchasePanel.submitActions();
                       } catch (Exception ex) {
                           throw new RuntimeException(ex);
                       }
                   }
               });
               buttonPanel.add(addToCartButton);
               bottomPanel.add(buttonPanel, BorderLayout.CENTER);
           }
            bottomPanel.setVisible(true);
            revalidate();
            repaint();
            pack();
    }

    private void showAdminMenu(){
        System.out.println("showAdminMenu is reached in MainFrame");
        adjustHeaderAndFooter("Choose what you would like to do: ", false, false, false);
        centerPanel.removeAll();
        this.adminMenu = new AdminMenuPanel(this, decorator);
        centerPanel.add(adminMenu);
        revalidate();
        repaint();
    }
    private void showAdminInfoPanel(){
        adjustHeaderAndFooter("Choose what you would like to do: ", true, false, false);
        centerPanel.removeAll();
        this.adminInfoPanel = new AdminInfoPanel(this, decorator, currentEvent);
        centerPanel.add(adminInfoPanel);
        revalidate();
        repaint();
    }

    public void Update(Event event) throws SQLException, ClassNotFoundException {
        this.currentEvent = event;
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
                        if (event.getSubject() == Event.Subject.ADMIN && event.getOutcome() == Event.Outcome.OK){
                            showAdminMenu();
                        }
                        else if (event.getPhase() == Event.Phase.AWAIT_INPUT && event.getSubject() == Event.Subject.NONE){
                            showLoginPanel();
                            break;
                        }
                        else {
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
                        }
                        else if (event.getPhase() == Event.Phase.AWAIT_INPUT){
                            loginPanel.showCreateAccountPanel();
                        }
                    }
                    case VIEW -> {
                        System.out.println("case VIEW is reached");
                        if (event.getExtraContents() != null && event.getExtraContents() instanceof Event.Subject) {
                            showAdminInfoPanel();
                        }
                        else if (event.getPhase() == Event.Phase.DISPLAY && event.getSubject() == Event.Subject.SHOE) {
                            showOptionsPanel(event);
                        } else if (event.getSubject() == Event.Subject.CART) {
                            showCartPanel(event);
                        }
                    }
                    case PURCHASE -> {
                        if (event.getPhase() == Event.Phase.COMPLETE) {
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
