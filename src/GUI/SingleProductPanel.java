package GUI;

import Model.Product;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SingleProductPanel extends JPanel {
    private PanelDecorator decorator;
    private Product product;
    private MainFrame mainFrame;

    public SingleProductPanel(MainFrame mainFrame, Product product, PanelDecorator decorator) {
        this.mainFrame = mainFrame;
        this.product = product;
        this.decorator = decorator;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Colors.panel());
        add(createShoeInfoPanel(product));
    }

    private JPanel createShoeInfoPanel(Product p) {
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        JPanel singleShoePanel = new JPanel();
        JPanel shoeInfo = new JPanel();
        shoeInfo.setLayout(new BoxLayout(shoeInfo, BoxLayout.Y_AXIS));
        shoeInfo.setBackground(Colors.card());
        shoeInfo.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        decorator.adjustWrapperPanel(singleShoePanel);

        JPanel header = new JPanel();
        JLabel brandLabel = new JLabel(p.getBrand());
        JLabel nameLabel = new JLabel(p.getName());
        decorator.adjustLabel(brandLabel);
        decorator.adjustLabel(nameLabel);
        header.add(brandLabel);
        header.add(nameLabel);

        JPanel footer = new JPanel();
        JLabel priceLabel = new JLabel(String.valueOf(p.getPrice()));
        JTextArea desc = new JTextArea(p.getDescription());
        desc.setEditable(false);
        decorator.adjustTextArea(desc);
        footer.add(priceLabel);
        footer.add(desc);

        shoeInfo.add(header, BorderLayout.NORTH);
        shoeInfo.add(footer, BorderLayout.SOUTH);

        JPanel colorPanel = new JPanel();
        colorPanel.setBackground(Colors.card());
        JLabel colorLabel = new JLabel("Available colors:");
        decorator.adjustLabel(colorLabel);
        colorPanel.add(colorLabel);
        for (String color : p.getColors()) {
            JButton colorBtn = new JButton(color);
            colorPanel.add(colorBtn);
        }

        JPanel sizePanel = new JPanel();
        JLabel sizeLabel = new JLabel("Available sizes:");
        decorator.adjustLabel(sizeLabel);
        sizePanel.add(sizeLabel);
        for (Integer size : p.getSizes()) {
            JTextField sizeField = new JTextField(String.valueOf(size));
            sizeField.setPreferredSize(new Dimension(30,30));
            sizeField.setEditable(false);
            sizePanel.add(sizeField);
        }

        shoeInfo.add(colorPanel);
        shoeInfo.add(sizePanel);

        singleShoePanel.add(shoeInfo);
        wrapperPanel.add(singleShoePanel, BorderLayout.CENTER);
        return wrapperPanel;
    }
}
