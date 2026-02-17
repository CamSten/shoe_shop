package GUI;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import Control.Event;
import Model.DataHandling.Product;
import Model.DataHandling.ProductTerm;
import Model.DataHandling.ShoeSpecification;

public class OptionsPanel extends JPanel {
    private MainFrame mainFrame;
    private PanelDecorator decorator;
    private Control.Event event;
    private JButton categoryButton;
    private JButton brandButton;
    private JButton colorButton;
    private List<Product> shoes;
    private JPanel displayingResult;
    private JPanel inputPanel;
    private ProductTerm currentProductTerm;
    private final String startOfHTML = "<html><div style='text-align: center; padding: 20px;'>";
    private final String endOfHTML = "</div></html>";

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
        decorator.adjustWrapperPanel(wrapperPanel);
        wrapperPanel.add(categoryPanel);
        return wrapperPanel;
    }

    public JPanel getOptionsPanel(Event event) {
        System.out.println("getOptionsPanel is reached");
        JPanel optionsPanel = new JPanel(new BorderLayout());
        JPanel categoryPanel = getCategoryPanel();
        optionsPanel.add(categoryPanel, BorderLayout.NORTH);
        this.displayingResult = new JPanel();
        displayingResult.setLayout(new BorderLayout());
        decorator.adjustWrapperPanel(displayingResult);
        displayingResult.setBorder(BorderFactory.createLineBorder(Colors.bg(), 10, true));
        displayingResult.add(getResultPanel(event),BorderLayout.CENTER);
        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setBackground(Colors.panel());
        decorator.adjustWrapperPanel(wrapperPanel);
        this.inputPanel = wrapperPanel;
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
            JPanel singleShoe = new JPanel();
            singleShoe.setBackground(Colors.panel());
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
        allShoesPanel.setBackground(Colors.panel());
        for (Product p : products) {
            System.out.println("SHOE IN CREATE ALL SHOE PANELS: " + p.getName());
            JPanel shoePanel = new JPanel();
            decorator.adjustShoeInfoPanel(shoePanel);
            JLabel shoeBrand = new JLabel(p.getBrand());
            shoeBrand.setAlignmentX(Component.CENTER_ALIGNMENT);
            decorator.adjustBrandLabel(shoeBrand);
            shoeBrand.setOpaque(false);
            JLabel shoeName = new JLabel(p.getName());
            shoeName.setAlignmentX(Component.CENTER_ALIGNMENT);
            decorator.adjustCardLabel(shoeName);
            shoeName.setOpaque(false);
            JPanel cardTop = new JPanel();
            cardTop.setLayout(new BoxLayout(cardTop, BoxLayout.Y_AXIS));
            cardTop.setBackground(Colors.panel());
//            shoeBrand.setAlignmentX(Component.CENTER_ALIGNMENT);
//            shoeName.setAlignmentX(Component.CENTER_ALIGNMENT);

            cardTop.add(shoeBrand);
            cardTop.add(Box.createVerticalStrut(2));
            cardTop.add(shoeName);

            shoePanel.add(cardTop);
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
            shoePanel.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(Colors.bg(), 5, true),
                    BorderFactory.createEmptyBorder(12,12,12,12)
            ));
            shoePanel.add(Box.createVerticalGlue());
            JLabel description = new JLabel();
            description.setText(startOfHTML+p.getDescription()+endOfHTML);
            decorator.adjustSmallLabel(description);
            description.setAlignmentX(Component.CENTER_ALIGNMENT);
            shoePanel.add(description);
            String thisPrice = String.valueOf(p.getPrice());
            JLabel priceLabel = new JLabel(thisPrice + " SEK");
            priceLabel.setFont(Fonts.getTinyFont());
            priceLabel.setForeground(Color.WHITE);
            priceLabel.setBackground(Colors.buttonHover());
            priceLabel.setOpaque(true);
            priceLabel.setBorder(BorderFactory.createEmptyBorder(4,8,4,8));
            shoePanel.add(priceLabel);
            allShoesPanel.add(shoePanel);
            shoePanel.setBackground(Colors.border());
        }

        return allShoesPanel;
    }
    private JPanel getShoeInfoPanel(Product p) {
        System.out.println("getSingleShoePanel is reached");
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(Colors.panel());
        JPanel singleShoePanel = new JPanel();
        JPanel shoeInfo = new JPanel(new BorderLayout());
        shoeInfo.setBackground(Colors.card());
        shoeInfo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        decorator.adjustWrapperPanel(singleShoePanel);
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
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
        priceLabel.setFont(Fonts.getTinyFont());
        priceLabel.setForeground(Color.WHITE);
        priceLabel.setBackground(Colors.buttonHover());
        priceLabel.setOpaque(true);
        priceLabel.setBorder(BorderFactory.createEmptyBorder(4,8,4,8));
        JLabel description = new JLabel();
        description.setText(startOfHTML+p.getDescription()+endOfHTML);
        decorator.adjustSmallLabel(description);
        description.setAlignmentX(Component.CENTER_ALIGNMENT);
        shoeFooter.add(description);
        shoeFooter.add(priceLabel);
        shoeInfo.add(header, BorderLayout.NORTH);
        shoeInfo.add(shoeFooter, BorderLayout.SOUTH);
        JPanel colorInfoPanel = new JPanel(new BorderLayout());
        colorInfoPanel.setBackground(Colors.card());
        JLabel colorLabel = new JLabel("Available colors:");
        decorator.adjustCardLabel(colorLabel);
        JPanel colorPanel = new JPanel();
        colorPanel.setBackground(Colors.button());
        colorInfoPanel.add(colorLabel, BorderLayout.NORTH);
        Set<String> colorSet = new LinkedHashSet<>();
        Set<Integer> sizeSet = new LinkedHashSet<>();
        for (ShoeSpecification sp : p.getShoeSpecifications()) {
            colorSet.add(sp.getColor());
            sizeSet.add(sp.getSize());
        }
        for (String s : colorSet) {
            System.out.println("color in OptionsPanel: " + s);
            JLabel color = new JLabel(s);
            color.setPreferredSize(new Dimension(50, 30));
            colorPanel.add(color);
        }
        colorInfoPanel.add(colorPanel, BorderLayout.CENTER);
        JPanel sizeInfoPanel = new JPanel(new BorderLayout());
        JPanel sizePanel = new JPanel();
        JLabel sizeLabel = new JLabel("Available sizes");
        decorator.adjustCardLabel(sizeLabel);

        for (int i : sizeSet){
            JLabel thisSize = new JLabel(String.valueOf(i));
            thisSize.setPreferredSize(new Dimension(30, 30));
                thisSize.setMaximumSize(new Dimension(30, 30));
            thisSize.setMinimumSize(new Dimension(30, 30));
            sizePanel.add(thisSize);
        }
        sizeInfoPanel.add(sizeLabel, BorderLayout.NORTH);
        sizeInfoPanel.add(sizePanel, BorderLayout.CENTER);
        JPanel specificsPanel = new JPanel(new GridLayout(2, 1));
        decorator.adjustSingleResultPanel(specificsPanel);
        specificsPanel.add(sizeInfoPanel);
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
                Event.Phase.SELECT, Event.Action.VIEW, Event.Subject.SHOE, Event.Origin.GUI, Event.Outcome.PENDING, currentProductTerm, choice
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
            }
        }
        return specificationList;
    }
}