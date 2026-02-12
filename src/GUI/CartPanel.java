package GUI;

import Control.Event;
import Model.Product;
import Model.ShoeSpecification;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CartPanel extends JPanel {
    private MainFrame mainFrame;
    private Event event;
    private PanelDecorator decorator;
    private HeaderPanel headerPanel;
    private JComboBox<String> colorBox;
    private JComboBox<Integer> sizeBox;
    private JTextArea quantityAvailabilityArea;
    private JTextArea quantityChosenArea;
    private ActionListener sizeListener;
    private ActionListener colorListener;
    private Product p;


    public CartPanel(MainFrame mainFrame, Event event, PanelDecorator decorator){
        System.out.println("cartPanel constructor is reached");
        this.mainFrame = mainFrame;
        this.event = event;
        if (event.getContents()!= null && event.getContents() instanceof Product){
            this.p = (Product) event.getContents();
        }
        this.decorator = decorator;
        setBackground(Colors.getBackgroundColor());
        add(getCartPanel(event));
        repaint();
        revalidate();
    }
    private JPanel getCartPanel(Event event){
        JPanel cartPanel = new JPanel();
        cartPanel.setLayout(new BoxLayout(cartPanel, BoxLayout.X_AXIS));
        cartPanel.setBackground(Colors.bg());
        List<Integer> sizes = p.getSizes();
        List<String> colors = p.getColors();
        List<Integer> quantities = p.getQuantities();

        JPanel shoePanel = new JPanel(new BorderLayout());
        JPanel shoeHeader = new JPanel();
        JLabel shoeBrand = new JLabel(p.getBrand());
        JLabel shoeName = new JLabel(p.getName());
        shoeHeader.add(shoeBrand);
        shoeHeader.add(shoeName);
        JPanel shoeFooter = new JPanel();
        JLabel priceLabel = new JLabel(String.valueOf(p.getPrice()));
        JTextArea description = new JTextArea(p.getDescription());
        description.setEditable(false);
        shoeFooter.add(description);
        shoeFooter.add(priceLabel);
        shoePanel.add(shoeHeader, BorderLayout.NORTH);
        shoePanel.add(shoeFooter, BorderLayout.SOUTH);
        decorator.adjustShoeInfoPanel(shoePanel);

        JPanel choicePanel = new JPanel();
//        choicePanel.setPreferredSize(new Dimension(300, 400));
//        choicePanel.setMinimumSize(new Dimension(300, 400));
//        choicePanel.setMinimumSize(new Dimension(300, 400));
        choicePanel.setLayout(new BoxLayout(choicePanel, BoxLayout.Y_AXIS));
        choicePanel.setBackground(Colors.bg());
        JPanel colorPanel = new JPanel(new GridLayout(2, 1));
        decorator.adjustSingleResultPanel(colorPanel);
        JLabel colorLabel = new JLabel("Select color:");
        decorator.adjustLabel(colorLabel);
        colorPanel.add(colorLabel);
        this.colorBox = new JComboBox<>();
        for (String s: colors){
            colorBox.addItem(s);
        }
        this.colorListener = e -> {
            String thisColor = (String) colorBox.getSelectedItem();
            if (thisColor!= null) {
                getSizes(p, thisColor);
            }
        };
        colorBox.addActionListener(colorListener);
        colorPanel.add(colorBox);
        JPanel sizePanel = new JPanel(new GridLayout(2, 1));
        decorator.adjustSingleResultPanel(sizePanel);
        JLabel sizeLabel = new JLabel("Select size:");
        decorator.adjustLabel(sizeLabel);
        sizePanel.add(sizeLabel);
        this.sizeBox = new JComboBox<>();
        for (int s : sizes){
            sizeBox.addItem(s);
        }
        this.sizeListener = e -> {
            if (sizeBox.getSelectedItem() != null) {
                System.out.println("sizeListener is activated");
                int thisSize = (Integer) sizeBox.getSelectedItem();
                getColors(p, thisSize);
            }
        };
        sizeBox.addActionListener(sizeListener);
        sizePanel.add(sizeBox);

        JPanel quantityPanel = new JPanel();
        quantityPanel.setLayout(new BoxLayout(quantityPanel, BoxLayout.Y_AXIS));
        decorator.adjustWrapperPanel(quantityPanel);

        JPanel qAvailablePanel = new JPanel(new GridLayout(2, 1));
        //decorator.adjustSingleResultPanel(qAvailablePanel);
        JLabel quantityAvailableLabel = new JLabel("In stock:");
        decorator.adjustLabel(quantityAvailableLabel);
        qAvailablePanel.add(quantityAvailableLabel);
        this.quantityAvailabilityArea = new JTextArea();
        quantityAvailabilityArea.setEditable(false);
        decorator.adjustTextArea(quantityAvailabilityArea);
        qAvailablePanel.add(quantityAvailabilityArea);

        JPanel qChosenPanel = new JPanel(new GridLayout(2, 1));
        //decorator.adjustSingleResultPanel(qChosenPanel);
        JLabel quantityChosenLabel = new JLabel("Select quantity:");
        decorator.adjustLabel(quantityChosenLabel);
        this.quantityChosenArea = new JTextArea();
        decorator.adjustTextArea(quantityChosenArea);
        quantityChosenArea.setBackground(Color.WHITE);
        quantityChosenArea.setEditable(true);
        qChosenPanel.add(quantityChosenLabel);
        qChosenPanel.add(quantityChosenArea);
        quantityPanel.add(qAvailablePanel);
        quantityPanel.add(qChosenPanel);

        JPanel submitPanel = new JPanel();
        decorator.adjustSingleResultPanel(submitPanel);
        JButton submitButton = new JButton("Add to cart");
        submitButton.addActionListener(e -> {
            try {
                submitActions();
            } catch (SQLException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });
        submitPanel.add(submitButton);
        choicePanel.add(colorPanel);
        choicePanel.add(sizePanel);
        choicePanel.add(quantityPanel);
        choicePanel.add(submitPanel);

        cartPanel.add(shoePanel);
        cartPanel.add(choicePanel);
        return cartPanel;
    }
    private void submitActions() throws SQLException, ClassNotFoundException {
        System.out.println("submitActions is reached in CartPanel");
        int quantity = assessQuantity();
        if (colorBox.getSelectedItem() != null && sizeBox.getSelectedItem() != null) {
            System.out.println("values != null");
            if (quantity > -1) {
                System.out.println("quantity is: " + quantity);
                String color = (String) colorBox.getSelectedItem();
                int size = (Integer) sizeBox.getSelectedItem();
                ShoeSpecification sc = new ShoeSpecification(size, color);
                sc.setQuantity((quantity));
                mainFrame.Update(new Event(Event.Phase.SUBMIT, Event.Action.PURCHASE, Event.Subject.SHOE, Event.Origin.GUI, Event.Outcome.OK, p, sc));
            }
        }
        else {
            JOptionPane.showMessageDialog(this, "You must submit your choices before you can add to cart.");

        }
    }
    private int assessQuantity(){
        String qInput = quantityChosenArea.getText();
        System.out.println("qInput: " + qInput);
        int quantity = -1;
        try {
            System.out.println("tryparse in AssessQuantity is reached");
            quantity = Integer.parseInt(qInput);
            System.out.println("quantity is: " + quantity);
            int availableQuantity = Integer.parseInt(quantityAvailabilityArea.getText());
            if (quantity > availableQuantity){
                System.out.println("quantity > availableQuantity");
                quantity = -1;
                JOptionPane.showMessageDialog(this, "The quantity you have submitted is too large.");
            }
        }
        catch (NumberFormatException e){}

        return quantity;
    }
    private void getQuantity(Product product){
        List<Integer> newQuantities = new ArrayList<>();
        if (colorBox.getSelectedItem()!= null && sizeBox.getSelectedItem() != null) {
            String thisColor = (String) colorBox.getSelectedItem();
            int thisSize = (Integer) sizeBox.getSelectedItem();
            for (ShoeSpecification sc : product.getSizeColors()) {
                if (sc.getColor().equals(thisColor) && sc.getSize() == thisSize) {
                    quantityAvailabilityArea.setText(String.valueOf(sc.getQuantity()));
                }
            }
        }
    }
    private void getSizes(Product product, String sValue) {
        System.out.println("getSizes is reached");
        List<Integer> newSizes = new ArrayList<>();
        if (!sValue.equals("")) {
            sizeBox.removeActionListener(sizeListener);
            sizeBox.removeAllItems();
            for (ShoeSpecification sc : product.getSizeColors()) {
                if (sc.getColor().equals(sValue)) {
                    newSizes.add(sc.getSize());
                }
            }
            for (int i : newSizes) {
                System.out.println("newSize: " + i);
                sizeBox.addItem(i);
            }
            sizeBox.addActionListener(sizeListener);
            repaint();
            revalidate();
            getQuantity(product);
        }
    }
    private void getColors(Product product, int iValue) {
        if (colorBox.getSelectedItem() == null) {
            List<String> newColors = new ArrayList<>();
            for (ShoeSpecification sc : product.getSizeColors()) {
                if (sc.getSize() == iValue) {
                    newColors.add(sc.getColor());
                }
            }
            for (String s : newColors) {
                colorBox.addItem(s);
            }
            getQuantity(product);
        }
    }
}
