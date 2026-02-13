package GUI;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import Control.Event;
import Model.Product;
import Model.ProductTerm;

public class OptionsPanel extends JPanel {
    private MainFrame mainFrame;
    private PanelDecorator decorator;
    private HeaderPanel headerPanel;
    private Control.Event event;
    private JButton categoryButton;
    private JButton brandButton;
    private JButton colorButton;
    private List<Model.Product> shoes;
    private JPanel displayingResult;
    private JPanel inputPanel;
    private ProductTerm currentProductTerm;

    public OptionsPanel(MainFrame mainFrame, PanelDecorator decorator, Control.Event event) {
        System.out.println("OptionsPanel constructor is reached");

        if (event.getContents() != null && event.getContents() instanceof List) {
            System.out.println("optionsPanel contents instance of: " + event.getContents().getClass());
            List<Object> list = (List<Object>) event.getContents();
            System.out.println("list.size is: " + list.size());
            if (list.size() > 0) {
                System.out.println("list.getFirst instance of: " + list.getFirst().getClass());
            }
        }

        this.mainFrame = mainFrame;
        this.event = event;
        this.decorator = decorator;
        setLayout(new BorderLayout());
        setBackground(Colors.panel());
        add(getOptionsPanel(event), BorderLayout.CENTER);
    }

    private JPanel getCategoryPanel() {
        System.out.println("getCategoryPanel is reached");
        List<JButton> catPanelButtons = new ArrayList<>();

        this.categoryButton = new JButton("Category");
        categoryButton.addActionListener(_ -> {
            try {
                mainFrame.Update(Event.chooseType(Event.Subject.SHOE, ProductTerm.Category));
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        catPanelButtons.add(categoryButton);

        this.brandButton = new JButton("Brand");
        brandButton.addActionListener(_ -> {
            try {
                mainFrame.Update(Event.chooseType(Event.Subject.SHOE, ProductTerm.Brand));
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        catPanelButtons.add(brandButton);

        this.colorButton = new JButton("Color");
        colorButton.addActionListener(_ -> {
            try {
                mainFrame.Update(Event.chooseType(Event.Subject.SHOE, ProductTerm.Color));
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        catPanelButtons.add(colorButton);

        JPanel categoryPanel = new JPanel();
//        decorator.adjustWrapperPanel(categoryPanel);
        decorator.adjustInputPanel(categoryPanel);
        categoryPanel.setLayout(new BoxLayout(categoryPanel, BoxLayout.X_AXIS));

        JLabel filterLabel = new JLabel("Filter on:");
        decorator.adjustLabel(filterLabel);
        categoryPanel.add(filterLabel);
        for (JButton b : catPanelButtons) {
            decorator.adjustButton(b);
            Box.createHorizontalStrut(10);
            categoryPanel.add(b);
            Box.createHorizontalStrut(10);
        }
        JPanel wrapperPanel = new JPanel();
//        wrapperPanel.setLayout(new GridLayout(2, 1, 0, 0));
        decorator.adjustWrapperPanel(wrapperPanel);
//        wrapperPanel.add(filterLabel);
        wrapperPanel.add(categoryPanel);
//        wrapperPanel.setBorder(BorderFactory.createLineBorder(Colors.buttonHover(), 10, true));
        return wrapperPanel;
    }

    public JPanel getOptionsPanel(Event event) {
        System.out.println("getOptionsPanel is reached");
        JPanel optionsPanel = new JPanel(new BorderLayout());
        JPanel categoryPanel = getCategoryPanel();
        optionsPanel.add(categoryPanel, BorderLayout.NORTH);
        this.displayingResult = new JPanel();
//        displayingResult.setLayout(new BoxLayout(displayingResult, BoxLayout.Y_AXIS));
        displayingResult.setLayout(new BorderLayout());
        decorator.adjustWrapperPanel(displayingResult);
        displayingResult.setBorder(BorderFactory.createLineBorder(Colors.bg(), 10, true));


//        displayingResult.add(Box.createVerticalStrut(5));
        displayingResult.add(getResultPanel(event),BorderLayout.CENTER);
//        displayingResult.add(Box.createVerticalStrut(5));

        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setBackground(Colors.panel());
//        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        decorator.adjustWrapperPanel(wrapperPanel);

        this.inputPanel = wrapperPanel;
//        inputPanel.setBackground(Colors.bg());
        addScrollBar(inputPanel, displayingResult);
        optionsPanel.add(inputPanel);
        return optionsPanel;
    }

    private JPanel getResultPanel(Event event) {
        System.out.println("GET RESULTPANEL IN OPTIONSPANEL IS REACHED");
        this.currentProductTerm = ProductTerm.Category;

        if (event.getContents() != null && event.getContents() instanceof ProductTerm p) {
            currentProductTerm = p;
        }
        if (event.getExtraContents() != null && event.getExtraContents() instanceof ProductTerm p) {
            currentProductTerm = p;
        }
        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        decorator.adjustWrapperPanel(resultPanel);

        if (event.getContents() != null && event.getContents() instanceof List) {
            System.out.println("optionsPanel GET RESULT contents instance of: " + event.getContents().getClass());
            List<Object> list = (List<Object>) event.getContents();
            System.out.println("list.size is: " + list.size());
        }

        Object data = event.getContents();

        if (data instanceof List list && list.getFirst() instanceof String) {
            System.out.println("data instance of String list");
            List<String> output = (List<String>) data;
            for (String s : output) {
                System.out.println("in GET RESULT PANEL, STRING IS: " + s);
                JPanel singleResultPanel = new JPanel();
                decorator.adjustSingleResultPanel(singleResultPanel);
                JButton button = new JButton(s);
                decorator.adjustButton(button);
                singleResultPanel.add(button);
                button.addActionListener(_ -> {
                    try {
                        sendUserChoice(s);
                    } catch (SQLException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
                resultPanel.add(Box.createVerticalGlue());
                resultPanel.add(singleResultPanel);
                resultPanel.add(Box.createVerticalGlue());
            }
        }

        if (data instanceof List list && list.getFirst() instanceof Product) {
            System.out.println("in OPTIONSPANEL GET RESULT, DATA INSTANCE OF PRODUCT-LIST");
            List<Product> shoes = (List<Product>) data;
            resultPanel.add(createAllShoePanels(shoes));
        } else if (data instanceof Product product) {
            System.out.println("data instance of product");
            resultPanel.removeAll();
//            mainFrame.getAddButtonPanel(product);

            JPanel singleShoe = new JPanel();
            singleShoe.setBackground(Colors.panel());
            singleShoe.setLayout(new BoxLayout(singleShoe, BoxLayout.Y_AXIS));
            singleShoe.add(getShoeInfoPanel(product));
            resultPanel.add(singleShoe);

            repaint();
            revalidate();
        }

        return resultPanel;
    }

    private JPanel createAllShoePanels(List<Product> products) {
        System.out.println("CreateAllShoePanels in OptionsPanel is reached");
        JPanel allShoesPanel = new JPanel(new GridLayout(0, 3, 20, 20));
//        allShoesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        allShoesPanel.setBackground(Colors.panel());

        for (Product p : products) {
            JPanel shoePanel = new JPanel();
            shoePanel.setLayout(new BoxLayout(shoePanel, BoxLayout.Y_AXIS));
            decorator.adjustShoeInfoPanel(shoePanel);

            JLabel shoeBrand = new JLabel(p.getBrand());
            decorator.adjustBrandLabel(shoeBrand);
            JLabel shoeName = new JLabel(p.getName());
            decorator.adjustCardLabel(shoeName);
            JPanel cardTop = new JPanel(new GridLayout(1, 2));
//            decorator.adjustInputPanel(cardTop);
            cardTop.add(shoeBrand);
            cardTop.add(shoeName);
            shoePanel.add(cardTop);
//            shoePanel.add(Box.createVerticalStrut(10));


            JButton shoeButton = new JButton("See details");
            decorator.adjustShoeCardButton(shoeButton);
            shoeButton.addActionListener(_ -> {
                try {
                    mainFrame.Update(new Event(Event.Phase.SUBMIT, Event.Action.CHOOSE_TYPE, Event.Subject.SHOE, Event.Origin.GUI, Event.Outcome.PENDING, p, null
                    ));
                } catch (SQLException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
            shoeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            shoePanel.add(shoeButton);
            decorator.adjustSingleShoePanel(shoePanel);


            shoePanel.add(Box.createVerticalGlue());

            JTextArea description = new JTextArea(p.getDescription());
            decorator.adjustCardText(description);
            shoePanel.add(description);
            String thisPrice = String.valueOf(p.getPrice());
            JLabel priceLabel = new JLabel(thisPrice + " SEK");
            priceLabel.setFont(Fonts.getTinyFont());
            priceLabel.setForeground(Colors.text());
            priceLabel.setBackground(Colors.border());
            priceLabel.setOpaque(true);
//            decorator.adjustCardLabel(priceLabel);
            shoePanel.add(priceLabel);
            allShoesPanel.add(shoePanel);
        }

        return allShoesPanel;
    }

    private JPanel getShoeInfoPanel(Product p) {
        System.out.println("getSingleShoePanel is reached");
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(Colors.panel());
        JPanel singleShoePanel = new JPanel();
        JPanel shoeInfo = new JPanel(new BorderLayout());
//        shoeInfo.setLayout(new BoxLayout(shoeInfo, BoxLayout.Y_AXIS));
        shoeInfo.setBackground(Colors.card());
        shoeInfo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        decorator.adjustWrapperPanel(singleShoePanel);

        JPanel header = new JPanel();
        decorator.adjustSingleResultLine(header);
        JLabel shoeBrand = new JLabel(p.getBrand());
        decorator.adjustBrandLabel(shoeBrand);
        JLabel shoeName = new JLabel(p.getName());
        decorator.adjustLabel(shoeName);
        shoeName.setForeground(Colors.buttonHover());
        header.add(shoeBrand);
        header.add(shoeName);

        JPanel shoeFooter = new JPanel();
        JLabel priceLabel = new JLabel(String.valueOf(p.getPrice()));
        JTextArea description = new JTextArea(p.getDescription());
        decorator.adjustTextArea(description);
        description.setEditable(false);
        shoeFooter.add(description);
        shoeFooter.add(priceLabel);

        shoeInfo.add(header, BorderLayout.NORTH);
        shoeInfo.add(shoeFooter, BorderLayout.SOUTH);

        JPanel colorInfoPanel = new JPanel();
        colorInfoPanel.setBackground(Colors.card());
        JLabel colorLabel = new JLabel("Available colors:");
        decorator.adjustCardLabel(colorLabel);

        JPanel colorPanel = new JPanel();
        colorPanel.setBackground(Colors.button());
        colorLabel.setLayout(new BoxLayout(colorPanel, BoxLayout.X_AXIS));
        colorInfoPanel.add(colorLabel);
        for (String s : p.getColors()) {
            JLabel color = new JLabel();
            color.setPreferredSize(new Dimension(30, 30));
            colorPanel.add(color);
        }
        colorInfoPanel.add(colorPanel);
        JPanel sizeInfoPanel = new JPanel();
        JPanel sizeFields = new JPanel();
        sizeFields.setLayout(new BoxLayout(sizeFields, BoxLayout.X_AXIS));
        JLabel sizeLabel = new JLabel("Available sizes");
        decorator.adjustCardLabel(sizeLabel);

        for (int i : p.getSizes()){
            JTextField sizeField = new JTextField(String.valueOf(i));
            sizeField.setPreferredSize(new Dimension(30, 30));
            sizeField.setMaximumSize(new Dimension(30, 30));
            sizeField.setMinimumSize(new Dimension(30, 30));
            sizeFields.add(sizeField);
        }
        sizeInfoPanel.add(sizeLabel);
        sizeInfoPanel.add(sizeFields);
        JPanel specificsPanel = new JPanel(new GridLayout(2, 1));
        decorator.adjustSingleResultPanel(specificsPanel);
        specificsPanel.add(specificsPanel);
        specificsPanel.add(colorInfoPanel);

        shoeInfo.add(specificsPanel);

        singleShoePanel.add(shoeInfo, BorderLayout.CENTER);
        wrapperPanel.add(singleShoePanel, BorderLayout.CENTER);
        return wrapperPanel;
    }

    public void addScrollBar(JPanel inputPanel, JPanel panel) {
        inputPanel.removeAll();
        JScrollPane scrollResults = new JScrollPane(panel);
        scrollResults.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollResults.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollResults.setPreferredSize(new Dimension(750, 480));
        SwingUtilities.invokeLater(() ->
                scrollResults.getVerticalScrollBar().setValue(0)
        );
        inputPanel.add(scrollResults);
    }

    private void sendUserChoice(String choice) throws SQLException, ClassNotFoundException {
        System.out.println("SEND USER CHOICE IN OPTIONS PANEL IS REACHED, choice is: " + choice);
        mainFrame.Update(new Event(
                Event.Phase.SELECT,
                Event.Action.VIEW,
                Event.Subject.SHOE,
                Event.Origin.GUI,
                Event.Outcome.PENDING,
                currentProductTerm,
                choice
        ));
    }

    private List<String> getSpecificationList(Product product, ProductTerm pt) {
        List<String> specificationList = new ArrayList<>();
        switch (pt) {
            case Name -> specificationList.add(product.getBrand());
            case Color -> {
                specificationList.add(product.getBrand());
                specificationList.add(product.getName());
            }
            case Size -> {
                specificationList.add(product.getBrand());
                specificationList.add(product.getName());
                // specificationList.add(product.getColor());
            }
        }
        return specificationList;
    }
}



//package GUI;
//
//import javax.swing.*;
//import java.awt.*;
//import java.sql.SQLException;
//import java.util.List;
//import Control.Event;
//import Model.Product;
//import Model.ProductTerm;
//
//public class OptionsPanel extends JPanel {
//    private MainFrame mainFrame;
//    private PanelDecorator decorator;
//    private Event event;
//
//    private JButton categoryButton;
//    private JButton brandButton;
//    private JButton colorButton;
//
//    private JPanel resultPanel;
//    private ProductTerm currentProductTerm;
//
//    public OptionsPanel(MainFrame mainFrame, PanelDecorator decorator, Event event) {
//        this.mainFrame = mainFrame;
//        this.decorator = decorator;
//        this.event = event;
//
//        setLayout(new BorderLayout());
//        setBackground(Colors.panel());
//
//        JPanel categoryPanel = getCategoryPanel();
//        add(categoryPanel, BorderLayout.NORTH);
//
//
//        resultPanel = new JPanel();
//        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
//        resultPanel.setBackground(Colors.panel());
//        decorator.adjustWrapperPanel(resultPanel);
//
//        JScrollPane scrollResults = new JScrollPane(resultPanel);
//        scrollResults.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//        scrollResults.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//
//        add(scrollResults, BorderLayout.CENTER);
//
//        updateResults(event.getContents());
//    }
//
//    private JPanel getCategoryPanel() {
//        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        decorator.adjustWrapperPanel(categoryPanel);
//
//        JLabel filterLabel = new JLabel("Filter on:");
//        decorator.adjustLabel(filterLabel);
//        categoryPanel.add(filterLabel);
//
//        categoryButton = new JButton("Category");
//        categoryButton.addActionListener(_ -> triggerChoice(ProductTerm.Category));
//
//        brandButton = new JButton("Brand");
//        brandButton.addActionListener(_ -> triggerChoice(ProductTerm.Brand));
//
//        colorButton = new JButton("Color");
//        colorButton.addActionListener(_ -> triggerChoice(ProductTerm.Color));
//
//        JButton[] buttons = {categoryButton, brandButton, colorButton};
//        for (JButton b : buttons) {
//            decorator.adjustButton(b);
//            categoryPanel.add(Box.createHorizontalStrut(5));
//            categoryPanel.add(b);
//        }
//
//        return categoryPanel;
//    }
//
//    private void triggerChoice(ProductTerm term) {
//        try {
//            mainFrame.Update(Event.chooseType(Event.Subject.SHOE, term));
//        } catch (SQLException | ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public void updateResults(Object data) {
//        resultPanel.removeAll();
//        currentProductTerm = ProductTerm.Category;
//
//        if (data instanceof ProductTerm pt) {
//            currentProductTerm = pt;
//        }
//
//        if (data instanceof List<?> list && !list.isEmpty()) {
//            if (list.get(0) instanceof Product) {
//                List<Product> products = (List<Product>) list;
//                for (Product p : products) {
//                    resultPanel.add(createShoePanel(p));
//                    resultPanel.add(Box.createVerticalStrut(15));
//                }
//            } else if (list.get(0) instanceof String) {
//                List<String> output = (List<String>) list;
//                for (String s : output) {
//                    JPanel singleResultPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//                    decorator.adjustSingleResultPanel(singleResultPanel);
//
//                    JButton button = new JButton(s);
//                    decorator.adjustButton(button);
//                    button.addActionListener(_ -> sendUserChoice(s));
//
//                    singleResultPanel.add(button);
//                    resultPanel.add(singleResultPanel);
//                    resultPanel.add(Box.createVerticalStrut(5));
//                }
//            }
//        }
//
//        resultPanel.revalidate();
//        resultPanel.repaint();
//    }
//
//    private JPanel createShoePanel(Product p) {
//        JPanel shoePanel = new JPanel();
//        shoePanel.setLayout(new BoxLayout(shoePanel, BoxLayout.Y_AXIS));
//        shoePanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
//        shoePanel.setBackground(Colors.card());
//        decorator.adjustShoeInfoPanel(shoePanel);
//
//        JLabel shoeBrand = new JLabel(p.getBrand());
//        JLabel shoeName = new JLabel(p.getName());
//        JLabel priceLabel = new JLabel("$" + p.getPrice());
//
//        shoePanel.add(shoeBrand);
//        shoePanel.add(shoeName);
//        shoePanel.add(priceLabel);
//        shoePanel.add(Box.createVerticalStrut(10));
//
//        JTextArea description = new JTextArea(p.getDescription());
//        description.setEditable(false);
//        description.setLineWrap(true);
//        description.setWrapStyleWord(true);
//        description.setBackground(Colors.card());
//        shoePanel.add(description);
//
//        JButton shoeButton = new JButton("See details");
//        decorator.adjustButton(shoeButton);
//        shoeButton.addActionListener(_ -> {
//            try {
//                mainFrame.Update(new Event(
//                        Event.Phase.SELECT,
//                        Event.Action.CHOOSE_TYPE,
//                        Event.Subject.SHOE,
//                        Event.Origin.GUI,
//                        Event.Outcome.PENDING,
//                        p,
//                        null
//                ));
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        });
//        shoePanel.add(shoeButton);
//
//        // Färger
//        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        colorPanel.setBackground(Colors.card());
//        for (String c : p.getColors()) {
//            JLabel cLabel = new JLabel(c);
//            decorator.adjustLabel(cLabel);
//            cLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
//            colorPanel.add(cLabel);
//        }
//        shoePanel.add(colorPanel);
//
//        // Storlekar
//        JPanel sizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        sizePanel.setBackground(Colors.card());
//        for (Integer size : p.getSizes()) {
//            JLabel sizeLabel = new JLabel(String.valueOf(size));
//            decorator.adjustLabel(sizeLabel);
//            sizeLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
//            sizePanel.add(sizeLabel);
//        }
//        shoePanel.add(sizePanel);
//
//        return shoePanel;
//    }
//
//    private void sendUserChoice(String choice) {
//        try {
//            mainFrame.Update(new Event(
//                    Event.Phase.SELECT,
//                    Event.Action.VIEW,
//                    Event.Subject.SHOE,
//                    Event.Origin.GUI,
//                    Event.Outcome.PENDING,
//                    currentProductTerm,
//                    choice
//            ));
//        } catch (SQLException | ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
