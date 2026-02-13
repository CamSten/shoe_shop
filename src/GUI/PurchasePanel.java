package GUI;

import Control.Event;
import Model.Product;
import Model.ShoeSpecification;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PurchasePanel extends JPanel {
    private MainFrame mainFrame;
    private Event event;
    private PanelDecorator decorator;
    private JPanel cartPanel;
    private JComboBox<String> colorBox;
    private JComboBox<String> sizeBox;
    private JLabel quantityAvailabilityLabel;
    private JTextArea quantityChosenArea;
    private ActionListener sizeListener;
    private ActionListener colorListener;
    private String colorIntro = "Color:";
    private String sizeIntro = "Size:";
    private Product p;

    public PurchasePanel(MainFrame mainFrame, Event event, PanelDecorator decorator){
        System.out.println("purchasePanel constructor is reached");
        this.mainFrame = mainFrame;
        this.event = event;
        if (event.getContents()!= null && event.getContents() instanceof Product){
            this.p = (Product) event.getContents();
        }
        this.decorator = decorator;
        setBackground(Colors.panel());
        add(getCartPanel(event));
        repaint();
        revalidate();
    }
    private JPanel getCartPanel(Event event){
        this.cartPanel = new JPanel();
        cartPanel.setLayout(new BoxLayout(cartPanel, BoxLayout.X_AXIS));
        cartPanel.setBackground(Colors.panel());
        List<Integer> sizes = new ArrayList<>();
        List<String> colors = new ArrayList<>();
//        List<Integer> quantities = new ArrayList<>();
        for (ShoeSpecification sc : p.getSizeColors()){
            sizes.add(sc.getSize());
            colors.add(sc.getColor());
        }
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
        choicePanel.setBackground(Colors.panel());
        JPanel colorPanel = new JPanel(new GridLayout(2, 1));
        decorator.adjustSingleResultPanel(colorPanel);
        JLabel colorLabel = new JLabel("Select color:");
        decorator.adjustLabel(colorLabel);
        colorPanel.add(colorLabel);
        this.colorBox = new JComboBox<>();
        colorBox.addItem(colorIntro);
        Set<String> colorSet = new LinkedHashSet<>();
        for (String s: colors){
            colorSet.add(s);
        }
        colors.clear();
        colors.addAll(colorSet);
        for (String s : colors){
            colorBox.addItem(s);
        }
        this.colorListener = e -> {
            String thisColor = (String) colorBox.getSelectedItem();
            if (thisColor!= null && !thisColor.equals(colorIntro)) {
                getSizes(p, thisColor);
                setAvailableQuantity(p);
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
        sizeBox.addItem(sizeIntro);
        Set<Integer> sizeSet = new LinkedHashSet<>();
        for (int s : sizes) {
            sizeSet.add(s);
        }
        sizes.clear();
        sizes.addAll(sizeSet);
        for (int i : sizes){
            sizeBox.addItem(String.valueOf(i));
        }
        this.sizeListener = e -> {
            if (sizeBox.getSelectedItem() != null && !sizeBox.getSelectedItem().equals(sizeIntro)) {
                System.out.println("sizeListener is activated");
                String selected = (String) sizeBox.getSelectedItem();
                int thisSize = 0;
                try { thisSize = Integer.parseInt(selected);
                    getColors(p, thisSize);
                    setAvailableQuantity(p);
                }
                catch (NumberFormatException ex){}
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
        this.quantityAvailabilityLabel= new JLabel();
        decorator.adjustLabel(quantityAvailabilityLabel);
        qAvailablePanel.add(quantityAvailabilityLabel);

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
    public void getConfirmationPanel(Event event){
        System.out.println("getConfirmationPanel is reached in PurchasePanel");
        cartPanel.removeAll();
        JPanel confirmationPanel = new JPanel(new BorderLayout());
        decorator.adjustWrapperPanel(confirmationPanel);
        Product product = null;
        String confText = "";
        JTextArea confirmArea = new JTextArea();
        decorator.adjustHeader(confirmArea);
        confirmationPanel.add(confirmArea, BorderLayout.NORTH);
        String brandValue = "";
        String nameValue = "";
        String colorValue = "";
        int sizeValue = 0;
        int quantityValue = 0;
        List<String> stringValues = new ArrayList<>();
        List<Integer> intValues = new ArrayList<>();
        List<JLabel> infoLabels = new ArrayList<>();
        List<JPanel> valueLabels = new ArrayList<>();
        List<String> descriptions = new ArrayList<>();
        descriptions.add("Brand:");
        descriptions.add("Name:");
        descriptions.add("Color:");
        descriptions.add("Size:");
        descriptions.add("Quantity:");

        if(event.getContents() instanceof Product p){
            product = p;
            brandValue = product.getBrand();
            nameValue = product.getName();
            colorValue = product.getColor();
            sizeValue = product.getSize();
            quantityValue = product.getBuyQuantity();
            stringValues.add(brandValue);
            stringValues.add(nameValue);
            stringValues.add(colorValue);
            intValues.add(sizeValue);
            intValues.add(quantityValue);
        }
        switch  (event.getOutcome()){
            case OK -> {
                confText = "The following product has been added to your cart:";
            }
            case FAILURE -> {
                confText = "The following product could not be added to your cart:";
            }
        }
        confirmArea.setText(confText);
        List<JLabel> intValueLabels = new ArrayList<>();
        for (int i = 0; i < stringValues.size(); i++) {
            if (i < intValues.size()) {
                JLabel intValueLabel = new JLabel(String.valueOf(intValues.get(i)));
                intValueLabels.add(intValueLabel);
            }
            JLabel stringValueLabel = new JLabel(stringValues.get(i));
            infoLabels.add(stringValueLabel);
        }
        infoLabels.addAll(intValueLabels);
        JPanel infoPanel = new JPanel(new GridLayout(infoLabels.size(), 1));
        decorator.adjustInputPanel(infoPanel);
        for (int i = 0; i < infoLabels.size(); i++){
            JPanel singleValuePanel = new JPanel(new GridLayout(1, 2));
            decorator.adjustSingleResultPanel(singleValuePanel);
            JLabel descriptionLabel = new JLabel(descriptions.get(i));
            decorator.adjustLabel(descriptionLabel);
            JLabel infoLabel = infoLabels.get(i);
            decorator.adjustLabel(infoLabel);
            singleValuePanel.add(descriptionLabel);
            singleValuePanel.add(infoLabel);
            infoPanel.add(singleValuePanel);
        }
        confirmationPanel.add(infoPanel, BorderLayout.CENTER);
        cartPanel.add(confirmationPanel);
        repaint();
        revalidate();
    }
    private void submitActions() throws SQLException, ClassNotFoundException {
        System.out.println("submitActions is reached in CartPanel");
        int buyQuantity = assessQuantityInput();
        if (colorBox.getSelectedItem() != null && sizeBox.getSelectedItem() != null) {
            System.out.println("values != null");
            if (buyQuantity > 0) {
                System.out.println("buyQuantity is: " + buyQuantity);
                String color = (String) colorBox.getSelectedItem();
                try {
                    int invQuantity = Integer.parseInt(quantityAvailabilityLabel.getText());
                    int size = Integer.parseInt(sizeBox.getSelectedItem().toString());
                    ShoeSpecification sc = new ShoeSpecification(size, color, invQuantity);
                        sc.setBuyQuantity(buyQuantity);
                        p.setColor(color);
                        p.setSize(size);
                        p.setBuyQuantity(buyQuantity);
                        mainFrame.Update(new Event(Event.Phase.SUBMIT, Event.Action.PURCHASE, Event.Subject.SHOE, Event.Origin.GUI, Event.Outcome.OK, p, sc));
                } catch (NumberFormatException e) {}
            } else {
                JOptionPane.showMessageDialog(this, "You must submit your choices before you can add to cart.");
            }
        }
    }
    private int assessQuantityInput(){
        String qInput = quantityChosenArea.getText();
        System.out.println("qInput: " + qInput);
        int quantity = -1;
        try {
            System.out.println("tryparse in AssessQuantity is reached");
            quantity = Integer.parseInt(qInput);
            System.out.println("quantity is: " + quantity);
            int availableQuantity = Integer.parseInt(quantityAvailabilityLabel.getText());
            if (quantity > availableQuantity){
                System.out.println("quantity > availableQuantity");
                quantity = -1;
                JOptionPane.showMessageDialog(this, "The quantity you have submitted is too large.");
            }
        }
        catch (NumberFormatException e){}
        return quantity;
    }
    private void setAvailableQuantity(Product product){
        String selectedColor = (String) colorBox.getSelectedItem();
        String selectedSize = (String) sizeBox.getSelectedItem();
        if ((selectedColor!= null && !selectedColor.equals(colorIntro)) && (selectedSize!= null && !selectedSize.equals(sizeIntro))) {
            try {
                int thisSize = Integer.parseInt(selectedSize);
                for (ShoeSpecification sc : product.getSizeColors()) {
                    if (sc.getColor().equals(selectedColor) && sc.getSize() == thisSize) {
                        quantityAvailabilityLabel.setText(String.valueOf(sc.getInvQuantity()));
                    }
                }
            } catch (NumberFormatException e) {
            }
        }
    }
    private void getSizes(Product product, String sValue) {
        System.out.println("getSizes is reached, value is: " + sValue);
        List<Integer> newSizes = new ArrayList<>();
        if (!sValue.equals("")) {
            sizeBox.removeActionListener(sizeListener);
            sizeBox.removeAllItems();
            System.out.println("sc.size: " + product.getSizeColors().size());
            Set<Integer> sizeSet = new LinkedHashSet<>();
            for (ShoeSpecification sc : product.getSizeColors()) {
                if (sc.getColor().equals(sValue)) {
                    sizeSet.add(sc.getSize());
                }
            }
            newSizes.addAll(sizeSet);
            for (int i : newSizes) {
                System.out.println("newSize: " + i);
                sizeBox.addItem(String.valueOf(i));
            }
            sizeBox.addActionListener(sizeListener);
            repaint();
            revalidate();
        }
    }
    private void getColors(Product product, int iValue) {
        colorBox.removeActionListener(colorListener);
        colorBox.removeAllItems();
        List<String> newColors = new ArrayList<>();
        Set<String> colorSet = new LinkedHashSet<>();
        for (ShoeSpecification sc : product.getSizeColors()) {
            if (sc.getSize() == iValue) {
                colorSet.add(sc.getColor());
            }
        }
        newColors.addAll(colorSet);
        for (String s : newColors) {
            colorBox.addItem(s);
        }
        colorBox.addActionListener(colorListener);
    }
}
