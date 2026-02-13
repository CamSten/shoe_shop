package GUI;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import Control.Event;
import Model.Product;
import Model.ProductTerm;

public class OptionsPanel extends JPanel {
    private MainFrame mainFrame;
    private PanelDecorator decorator;
    private Control.Event event;

    private JButton categoryButton;
    private JButton brandButton;
    private JButton colorButton;

    private JPanel displayingResult;
    private JPanel inputPanel;
    private JPanel resultPanel;

    private ProductTerm currentProductTerm;

    public OptionsPanel(MainFrame mainFrame, PanelDecorator decorator, Control.Event event) {
        System.out.println("optionsPanel constructor is reached");
        this.mainFrame = mainFrame;
        this.event = event;
        this.decorator = decorator;
        if (event.getExtraContents() != null){
            System.out.println("extra contents: " + event.getExtraContents().getClass());
        }
        setLayout(new BorderLayout());
        setBackground(Colors.panel());
        this.displayingResult = new JPanel();
        displayingResult.setLayout(new BoxLayout(displayingResult, BoxLayout.Y_AXIS));
        decorator.adjustWrapperPanel(displayingResult);

        JPanel categoryPanel = getCategoryPanel();
        displayingResult.add(categoryPanel);
        displayingResult.add(Box.createVerticalStrut(5));

        this.resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        decorator.adjustWrapperPanel(resultPanel);
        displayingResult.add(resultPanel);

        displayingResult.add(Box.createVerticalStrut(5));

        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setBackground(Colors.panel());
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        decorator.adjustWrapperPanel(wrapperPanel);
        this.inputPanel = wrapperPanel;

        JScrollPane scrollResults = new JScrollPane(displayingResult);
        scrollResults.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollResults.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollResults.setPreferredSize(new Dimension(550, 400));

        inputPanel.add(scrollResults);
        add(inputPanel, BorderLayout.CENTER);

        updateResults(event.getContents());
    }

    private JPanel getCategoryPanel() {
        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.X_AXIS));
        decorator.adjustWrapperPanel(categoryPanel);
        decorator.adjustWrapperPanel(wrapperPanel);

        JLabel filterLabel = new JLabel("Filter on:");
        decorator.adjustLabel(filterLabel);

        categoryButton = new JButton("Category");
        categoryButton.addActionListener(_ -> triggerChoice(ProductTerm.Category));

        brandButton = new JButton("Brand");
        brandButton.addActionListener(_ -> triggerChoice(ProductTerm.Brand));

        colorButton = new JButton("Color");
        colorButton.addActionListener(_ -> triggerChoice(ProductTerm.Color));

        JButton[] buttons = {categoryButton, brandButton, colorButton};
        for (JButton b : buttons) {
            decorator.adjustButton(b);
            categoryPanel.add(b);
            categoryPanel.add(Box.createHorizontalStrut(10));
        }

        wrapperPanel.add(filterLabel);
        wrapperPanel.add(categoryPanel);
        return wrapperPanel;
    }

    private void triggerChoice(ProductTerm term) {
        try {
            mainFrame.Update(Event.chooseType(Event.Subject.SHOE, term));
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateResults(Object data) {
        System.out.println("update result is reached");
        if (data != null){
            System.out.println("data instance of: " + data.getClass());
            if (data instanceof List list) {
                System.out.println("data is list");
                if (!list.isEmpty()) {
                    System.out.println("list is: " + list.getFirst().getClass());
                }
                else {
                    System.out.println("list is empty");
                }
            }
        }
        resultPanel.removeAll();
        currentProductTerm = ProductTerm.Category;

        if (data instanceof ProductTerm pt) {
            currentProductTerm = pt;
        }

        if (data instanceof List list && !list.isEmpty()) {
            if (list.getFirst() instanceof Product) {

                List<Product> shoes = (List<Product>) list;

                JPanel wrapper = new JPanel(new BorderLayout());
                wrapper.add(createAllShoePanels(shoes), BorderLayout.NORTH);
                resultPanel.add(wrapper);
                repaint();
                revalidate();
            } else if (list.getFirst() instanceof String) {
                List<String> output = (List<String>) list;
                for (String s : output) {
                    JPanel singleResultPanel = new JPanel();
                    decorator.adjustSingleResultPanel(singleResultPanel);
                    JButton button = new JButton(s);
                    decorator.adjustButton(button);
                    button.addActionListener(_ -> sendUserChoice(s));
                    singleResultPanel.add(button);
                    resultPanel.add(singleResultPanel);
                }
            }
        }

        resultPanel.revalidate();
        resultPanel.repaint();
    }
    private JPanel createAllShoePanels(List<Product> products) {
        System.out.println("createAllShoePanels is reached");
        JPanel allShoesPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        allShoesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        allShoesPanel.setBackground(Colors.panel());

        for (Product p : products) {
            System.out.println("product is: " + p.getName());
            JPanel shoePanel = new JPanel();
            shoePanel.setLayout(new BoxLayout(shoePanel, BoxLayout.Y_AXIS));
            decorator.adjustShoeInfoPanel(shoePanel);

            JLabel shoeBrand = new JLabel(p.getBrand());
            JLabel shoeName = new JLabel(p.getName());
            JLabel priceLabel = new JLabel(String.valueOf(p.getPrice()));

            shoePanel.add(shoeBrand);
            shoePanel.add(shoeName);
            shoePanel.add(Box.createVerticalStrut(10));
            shoePanel.add(priceLabel);
            shoePanel.add(Box.createVerticalGlue());

            JTextArea description = new JTextArea(p.getDescription());
            description.setEditable(false);
            shoePanel.add(description);

            JButton shoeButton = new JButton("See details");
            decorator.adjustButton(shoeButton);
            shoeButton.addActionListener(_ -> {
                try {
                    mainFrame.Update(new Event(
                            Event.Phase.SELECT,
                            Event.Action.CHOOSE_TYPE,
                            Event.Subject.SHOE,
                            Event.Origin.GUI,
                            Event.Outcome.PENDING,
                            p,
                            null
                    ));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            shoePanel.add(shoeButton);

            JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            colorPanel.setBackground(Colors.card());
            for (String c : p.getColors()) {
                JLabel cLabel = new JLabel(c);
                decorator.adjustLabel(cLabel);
                cLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                colorPanel.add(cLabel);
            }
            shoePanel.add(colorPanel);

            JPanel sizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            sizePanel.setBackground(Colors.card());
            for (Integer size : p.getSizes()) {
                JLabel sizeLabel = new JLabel(String.valueOf(size));
                decorator.adjustLabel(sizeLabel);
                sizeLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                sizePanel.add(sizeLabel);
            }
            shoePanel.add(sizePanel);
            allShoesPanel.add(shoePanel);
            allShoesPanel.setPreferredSize(new Dimension(500, 400));

        }

        return allShoesPanel;
    }

    private void sendUserChoice(String choice) {
        try {
            mainFrame.Update(new Event(
                    Event.Phase.SELECT,
                    Event.Action.VIEW,
                    Event.Subject.SHOE,
                    Event.Origin.GUI,
                    Event.Outcome.PENDING,
                    currentProductTerm,
                    choice
            ));
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
