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
    private Event event;

    private JButton categoryButton;
    private JButton brandButton;
    private JButton colorButton;

    private JPanel resultPanel;
    private ProductTerm currentProductTerm;

    public OptionsPanel(MainFrame mainFrame, PanelDecorator decorator, Event event) {
        this.mainFrame = mainFrame;
        this.decorator = decorator;
        this.event = event;

        setLayout(new BorderLayout());
        setBackground(Colors.panel());

        JPanel categoryPanel = getCategoryPanel();
        add(categoryPanel, BorderLayout.NORTH);


        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBackground(Colors.panel());
        decorator.adjustWrapperPanel(resultPanel);

        JScrollPane scrollResults = new JScrollPane(resultPanel);
        scrollResults.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollResults.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollResults, BorderLayout.CENTER);

        updateResults(event.getContents());
    }

    private JPanel getCategoryPanel() {
        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        decorator.adjustWrapperPanel(categoryPanel);

        JLabel filterLabel = new JLabel("Filter on:");
        decorator.adjustLabel(filterLabel);
        categoryPanel.add(filterLabel);

        categoryButton = new JButton("Category");
        categoryButton.addActionListener(_ -> triggerChoice(ProductTerm.Category));

        brandButton = new JButton("Brand");
        brandButton.addActionListener(_ -> triggerChoice(ProductTerm.Brand));

        colorButton = new JButton("Color");
        colorButton.addActionListener(_ -> triggerChoice(ProductTerm.Color));

        JButton[] buttons = {categoryButton, brandButton, colorButton};
        for (JButton b : buttons) {
            decorator.adjustButton(b);
            categoryPanel.add(Box.createHorizontalStrut(5));
            categoryPanel.add(b);
        }

        return categoryPanel;
    }

    private void triggerChoice(ProductTerm term) {
        try {
            mainFrame.Update(Event.chooseType(Event.Subject.SHOE, term));
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateResults(Object data) {
        resultPanel.removeAll();
        currentProductTerm = ProductTerm.Category;

        if (data instanceof ProductTerm pt) {
            currentProductTerm = pt;
        }

        if (data instanceof List<?> list && !list.isEmpty()) {
            if (list.get(0) instanceof Product) {
                List<Product> products = (List<Product>) list;
                for (Product p : products) {
                    resultPanel.add(createShoePanel(p));
                    resultPanel.add(Box.createVerticalStrut(15));
                }
            } else if (list.get(0) instanceof String) {
                List<String> output = (List<String>) list;
                for (String s : output) {
                    JPanel singleResultPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    decorator.adjustSingleResultPanel(singleResultPanel);

                    JButton button = new JButton(s);
                    decorator.adjustButton(button);
                    button.addActionListener(_ -> sendUserChoice(s));

                    singleResultPanel.add(button);
                    resultPanel.add(singleResultPanel);
                    resultPanel.add(Box.createVerticalStrut(5));
                }
            }
        }

        resultPanel.revalidate();
        resultPanel.repaint();
    }

    private JPanel createShoePanel(Product p) {
        JPanel shoePanel = new JPanel();
        shoePanel.setLayout(new BoxLayout(shoePanel, BoxLayout.Y_AXIS));
        shoePanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        shoePanel.setBackground(Colors.card());
        decorator.adjustShoeInfoPanel(shoePanel);

        JLabel shoeBrand = new JLabel(p.getBrand());
        JLabel shoeName = new JLabel(p.getName());
        JLabel priceLabel = new JLabel("$" + p.getPrice());

        shoePanel.add(shoeBrand);
        shoePanel.add(shoeName);
        shoePanel.add(priceLabel);
        shoePanel.add(Box.createVerticalStrut(10));

        JTextArea description = new JTextArea(p.getDescription());
        description.setEditable(false);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setBackground(Colors.card());
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

        // Färger
        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        colorPanel.setBackground(Colors.card());
        for (String c : p.getColors()) {
            JLabel cLabel = new JLabel(c);
            decorator.adjustLabel(cLabel);
            cLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
            colorPanel.add(cLabel);
        }
        shoePanel.add(colorPanel);

        // Storlekar
        JPanel sizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sizePanel.setBackground(Colors.card());
        for (Integer size : p.getSizes()) {
            JLabel sizeLabel = new JLabel(String.valueOf(size));
            decorator.adjustLabel(sizeLabel);
            sizeLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
            sizePanel.add(sizeLabel);
        }
        shoePanel.add(sizePanel);

        return shoePanel;
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
