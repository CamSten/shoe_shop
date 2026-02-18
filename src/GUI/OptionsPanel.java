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
    private ProductTerm currentProductTerm;
    private final String startOfHTML = "<html><div style='text-align: center; padding: 20px;'>";
    private final String endOfHTML = "</div></html>";

    public OptionsPanel(MainFrame mainFrame, PanelDecorator decorator, Control.Event event) {
        this.mainFrame = mainFrame;
        this.event = event;
        this.decorator = decorator;
        setLayout(new BorderLayout());
        setBackground(Colors.panel());
        add(getOptionsPanel(event), BorderLayout.CENTER);
    }
    private JPanel getCategoryPanel() {
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
        JPanel optionsPanel = new JPanel(new BorderLayout());
        optionsPanel.setBackground(Colors.panel());
        JPanel categoryPanel = getCategoryPanel();
        optionsPanel.add(categoryPanel, BorderLayout.NORTH);
        JPanel results = getResultPanel(event);
        optionsPanel.add(results, BorderLayout.CENTER);
        return optionsPanel;
    }
    private JPanel getResultPanel(Event event) {
        this.currentProductTerm = ProductTerm.Category;
        if (event.getContents() != null && event.getContents() instanceof ProductTerm p) {
            currentProductTerm = p;
        }
        if (event.getExtraContents() != null && event.getExtraContents() instanceof ProductTerm p) {
            currentProductTerm = p;
        }
        JPanel resultPanel = new JPanel();
        decorator.adjustWrapperPanel(resultPanel);
        resultPanel.setBorder(BorderFactory.createLineBorder(Colors.bg(), 10, true));
        Object data = event.getContents();
        if (data instanceof List list && list.getFirst() instanceof String) {
            resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
            List<String> output = (List<String>) data;
            for (String s : output) {
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
                resultPanel.add(singleResultPanel);
            }
        }
        if (data instanceof List list && list.getFirst() instanceof Product) {
            List<Product> shoes = (List<Product>) data;
            resultPanel.setLayout(new BorderLayout());
            resultPanel.add(createAllShoePanels(shoes), BorderLayout.CENTER);
        } else if (data instanceof Product product) {
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
        JPanel allShoesPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        allShoesPanel.setBackground(Colors.panel());
        for (Product p : products) {
            JPanel shoePanel = new JPanel();
            decorator.adjustShoeInfoPanel(shoePanel);
            shoePanel.setMinimumSize(new Dimension(260, 380));
            shoePanel.setPreferredSize(new Dimension(260, 380));
            shoePanel.setMaximumSize(new Dimension(260, 380));
            shoePanel.setLayout(new BoxLayout(shoePanel, BoxLayout.Y_AXIS));
            JLabel shoeBrand = new JLabel(p.getBrand());
            shoeBrand.setAlignmentX(Component.CENTER_ALIGNMENT);
            decorator.adjustBrandLabel(shoeBrand);
            shoeBrand.setBackground(Colors.panel());
            shoeBrand.setOpaque(false);
            JLabel shoeName = new JLabel(p.getName());
            shoeName.setAlignmentX(Component.CENTER_ALIGNMENT);
            decorator.adjustCardLabel(shoeName);
            shoeName.setBackground(Colors.panel());
            shoeName.setOpaque(false);
            JPanel cardTop = new JPanel();
            cardTop.setLayout(new BoxLayout(cardTop, BoxLayout.Y_AXIS));
            cardTop.setBackground(Colors.border());
            cardTop.add(shoeBrand);
            cardTop.add(shoeName);
            shoePanel.add(cardTop);
            cardTop.setAlignmentX(Component.CENTER_ALIGNMENT);
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
            shoePanel.setBackground(Colors.border());
            allShoesPanel.add(shoePanel);
        }
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(Colors.panel());
        JScrollPane scrollPane = new JScrollPane(allShoesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        wrapperPanel.add(scrollPane, BorderLayout.CENTER);
        return wrapperPanel;
    }
    private JPanel getShoeInfoPanel(Product p) {
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
    private void sendUserChoice(String choice) throws SQLException, ClassNotFoundException {
        mainFrame.Update(new Event(
                Event.Phase.SELECT, Event.Action.VIEW, Event.Subject.SHOE, Event.Origin.GUI, Event.Outcome.PENDING, currentProductTerm, choice
        ));
    }
    @Override
    public Dimension getPreferredSize() {
        int width = 800;
        int height = mainFrame.getMaxHeight()- 50;
        return new Dimension(width, height);
    }
}