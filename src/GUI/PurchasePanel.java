package GUI;

import Control.Event;
import Model.DataHandling.OrderPost;
import Model.DataHandling.Product;
import Model.DataHandling.ShoeSpecification;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PurchasePanel extends JPanel {
    private MainFrame mainFrame;
    private Event event;
    private PanelDecorator decorator;
    private JPanel cartPanel;
    private JPanel confirmationPanel;
    private JComboBox<String> colorBox;
    private JComboBox<String> sizeBox;
    private JTextArea quantityAvailabilityArea;
    private JTextArea quantityChosenArea;
    private ActionListener sizeListener;
    private ActionListener colorListener;
    private String colorIntro = "Color:";
    private String sizeIntro = "Size:";
    private Set<String> colorSet;
    private Set<Integer> sizeSet;
    private Product p;
    private final String startOfHTML = "<html><div style='text-align: center; padding: 20px;'>";
    private final String endOfHTML = "</div></html>";

    public PurchasePanel(MainFrame mainFrame, Event event, PanelDecorator decorator){
        System.out.println("purchasePanel constructor is reached");
        this.mainFrame = mainFrame;
        this.event = event;
        if (event.getContents()!= null && event.getContents() instanceof Product){
            this.p = (Product) event.getContents();
        }
        System.out.println("in purchasePanel: Action=" + event.getAction() +
                ", Phase=" + event.getPhase() +
                ", Subject=" + event.getSubject() +
                ", Outcome=" + event.getOutcome() +
                ", Origin=" + event.getOrigin());
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
        mainFrame.adjustHeaderAndFooter("Choose color and size:", true, true, true);
        List<Integer> sizes = new ArrayList<>();
        List<String> colors = new ArrayList<>();
        this.colorSet = new LinkedHashSet<>();
        this.sizeSet = new LinkedHashSet<>();
        for (ShoeSpecification sc : p.getShoeSpecifications()){
            sizeSet.add(sc.getSize());
            colorSet.add(sc.getColor());
        }
        sizes.addAll(sizeSet);
        colors.addAll(colorSet);
        JPanel shoePanel = createShoeInfoPanel(p);
        JPanel choicePanel = new JPanel();
        choicePanel.setLayout(new BoxLayout(choicePanel, BoxLayout.Y_AXIS));
        choicePanel.setBackground(Colors.panel());
        JPanel colorPanel = new JPanel(new GridLayout(2, 1));
        decorator.adjustSingleResultPanel(colorPanel);
        JLabel colorLabel = new JLabel("Select color:");
        decorator.adjustLabel(colorLabel);
        colorPanel.add(colorLabel);
        this.colorBox = new JComboBox<>();
        colorBox.addItem(colorIntro);
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
        decorator.adjustSingleResultPanel(quantityPanel);
        JPanel qAvailablePanel = new JPanel(new GridLayout(2, 1));
        decorator.adjustSingleResultPanel(qAvailablePanel);
        JLabel quantityAvailableLabel = new JLabel("In stock:");
        decorator.adjustLabel(quantityAvailableLabel);
        qAvailablePanel.add(quantityAvailableLabel);
        this.quantityAvailabilityArea = new JTextArea();
        decorator.adjustTextArea(quantityAvailabilityArea);
        quantityAvailabilityArea.setBackground(Color.WHITE);
        quantityAvailabilityArea.setEditable(false);
        qAvailablePanel.add(quantityAvailabilityArea);

        JPanel qChosenPanel = new JPanel(new GridLayout(2, 1));
        decorator.adjustSingleResultPanel(qChosenPanel);
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
        choicePanel.add(colorPanel);
        choicePanel.add(sizePanel);
        choicePanel.add(quantityPanel);
        cartPanel.add(shoePanel);
        cartPanel.add(choicePanel);
        return cartPanel;
    }
    public void getConfirmationPanel(Event event){
        System.out.println("getConfirmationPanel is reached in PurchasePanel");
        cartPanel.removeAll();
        this.confirmationPanel = new JPanel(new BorderLayout());
        decorator.adjustWrapperPanel(confirmationPanel);
        Product product = null;
        String confText = "";
        switch  (event.getOutcome()){
            case OK -> {
                confText = "Added to cart: check!\nfor:";
            }
            case FAILURE -> {
                confText = "Add to cart failed for:";
            }
        }
        mainFrame.adjustHeaderAndFooter(confText, true, false, false);
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

        if(event.getContents() instanceof OrderPost p) {
            brandValue = p.getBrand();
            nameValue = p.getName();
                colorValue = p.getColor();
                sizeValue = p.getSize();
                quantityValue = p.getQuantity();
                stringValues.add(brandValue);
                stringValues.add(nameValue);
                stringValues.add(colorValue);
                intValues.add(sizeValue);
                intValues.add(quantityValue);
        }
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
        JPanel infoPanel = new JPanel();
        decorator.adjustWrapperPanel(infoPanel);
        for (int i = 0; i < infoLabels.size(); i++){
            JPanel singleValuePanel = new JPanel(new GridLayout(1, 2));
            decorator.adjustSingleResultPanel(singleValuePanel);
            JLabel descriptionLabel = new JLabel(descriptions.get(i));
            decorator.adjustLabel(descriptionLabel);
            descriptionLabel.setForeground(Colors.textMuted());
            JLabel infoLabel = infoLabels.get(i);
            decorator.adjustLabel(infoLabel);
            singleValuePanel.add(descriptionLabel);
            singleValuePanel.add(infoLabel);
            infoPanel.add(singleValuePanel);
        }
        confirmationPanel.add(infoPanel, BorderLayout.CENTER);
        confirmationPanel.setBorder(BorderFactory.createLineBorder(Colors.bg(), 10, true));
        checkExtraContents(event);
        cartPanel.add(confirmationPanel);
        repaint();
        revalidate();
    }
    private void checkExtraContents(Event event){
        if (event.getExtraContents() != null && event.getExtraContents() instanceof Boolean lastInStock){
            if (lastInStock){
                getEmptyStockMsg();
            }
        }
    }

    private JPanel createShoeInfoPanel(Product p) {
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        decorator.adjustWrapperPanel(wrapperPanel);
//        wrapperPanel.setBorder(BorderFactory.createCompoundBorder(
//                new LineBorder(Colors.border(), 7, true),
//                BorderFactory.createEmptyBorder(12,12,12,12)
//        ));
        JPanel singleShoePanel = new JPanel();
        JPanel shoeInfo = new JPanel();
        shoeInfo.setLayout(new BoxLayout(shoeInfo, BoxLayout.Y_AXIS));
        decorator.adjustWrapperPanel(shoeInfo);
        decorator.adjustWrapperPanel(singleShoePanel);

        JPanel header = new JPanel();
        header.setBackground(Colors.border());
        JLabel brandLabel = new JLabel(p.getBrand());
        JLabel nameLabel = new JLabel(p.getName());
        decorator.adjustBrandLabel(brandLabel);
        decorator.adjustLabel(nameLabel);
        header.add(brandLabel);
        header.add(nameLabel);

        JPanel footer = new JPanel();
        footer.setBackground(Colors.border());
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        JLabel priceLabel = new JLabel(String.valueOf(p.getPrice()) + " SEK");
        priceLabel.setFont(Fonts.getTinyFont());
        priceLabel.setForeground(Color.WHITE);
        priceLabel.setBackground(Colors.buttonHover());
        priceLabel.setOpaque(true);
        priceLabel.setBorder(BorderFactory.createEmptyBorder(4,8,4,8));
        priceLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        JLabel desc = new JLabel();
        desc.setText(startOfHTML+p.getDescription()+endOfHTML);
        decorator.adjustSmallLabel(desc);
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);
        footer.add(desc);
        footer.add(priceLabel);

        JPanel infoPanel = new JPanel(new GridLayout(2, 2));
        decorator.adjustWrapperPanel(infoPanel);
        JPanel colorPanel = new JPanel();
        colorPanel.setLayout(new BoxLayout(colorPanel, BoxLayout.X_AXIS));
        decorator.adjustWrapperPanel(colorPanel);
        infoPanel.setBackground(Colors.border());
        for (String c : colorSet) {
            JLabel color = new JLabel(c);
            color.setBackground(Colors.card());
            color.setForeground(Colors.textMuted());
            color.setFont(Fonts.getTinyFont());
            color.setBorder(BorderFactory.createLineBorder(Colors.border(), 2, true));
            color.setPreferredSize(new Dimension(30,30));
            colorPanel.add(Box.createHorizontalStrut(5));
            colorPanel.add(color);
            colorPanel.add(Box.createHorizontalStrut(5));
        }

        JPanel sizePanel = new JPanel();
        decorator.adjustWrapperPanel(sizePanel);
        sizePanel.setLayout(new BoxLayout(sizePanel, BoxLayout.X_AXIS));
        for (Integer s : sizeSet) {
            JLabel size = new JLabel(String.valueOf(s));
            size.setBackground(Colors.card());
            size.setForeground(Colors.textMuted());
            size.setFont(Fonts.getTinyFont());
            size.setBorder(BorderFactory.createLineBorder(Colors.border(), 2, true));
            size.setPreferredSize(new Dimension(30,30));
            sizePanel.add(Box.createHorizontalStrut(5));
            sizePanel.add(size);
            sizePanel.add(Box.createHorizontalStrut(5));

        }
        JLabel colorLabel = new JLabel("Available colors:");
        JLabel sizeLabel = new JLabel("Available sizes:");
        decorator.adjustCardLabel(colorLabel);
        decorator.adjustCardLabel(sizeLabel);
        infoPanel.add(colorLabel);
        infoPanel.add(colorPanel);
        infoPanel.add(sizeLabel);
        infoPanel.add(sizePanel);
        shoeInfo.add(header, BorderLayout.NORTH);
        shoeInfo.add(infoPanel, BorderLayout.CENTER);
        shoeInfo.add(footer, BorderLayout.SOUTH);
        singleShoePanel.add(shoeInfo);
        wrapperPanel.add(singleShoePanel, BorderLayout.CENTER);
        return wrapperPanel;
    }
    private void getEmptyStockMsg(){
        System.out.println("- - - - getEmptyStockMessage is reached in CartPanel");
        JPanel wrapper = new JPanel(new GridLayout(2, 1));
        JLabel msg = new JLabel("These shoes are flying off the shelves!");
        decorator.adjustBrandLabel(msg);
        JLabel stockInfo = new JLabel("Congratulations, you've secured the last pair!");
        decorator.adjustLabel(stockInfo);
        wrapper.add(msg);
        wrapper.add(stockInfo);
        decorator.adjustWrapperPanel(wrapper);
        JPanel stockMsgPanel = new JPanel();
        stockMsgPanel.add(wrapper);
        stockMsgPanel.setOpaque(false);
        confirmationPanel.add(stockMsgPanel, BorderLayout.SOUTH);
    }
    public void submitActions() throws SQLException, ClassNotFoundException {
        System.out.println("submitActions is reached in CartPanel");
        int buyQuantity = assessQuantityInput();
        if (colorBox.getSelectedItem() != null && sizeBox.getSelectedItem() != null) {
            System.out.println("values != null");
            if (buyQuantity > 0) {
                System.out.println("buyQuantity is: " + buyQuantity);
                String color = (String) colorBox.getSelectedItem();
                try {
                    int size = Integer.parseInt(sizeBox.getSelectedItem().toString());
                    OrderPost orderPost = new OrderPost(-1, p.getProductId(), p.getBrand(), p.getName(), color, size, buyQuantity, p.getPrice(), LocalDateTime.now());
                    mainFrame.Update(new Event(Event.Phase.SUBMIT, Event.Action.PURCHASE, Event.Subject.CART, Event.Origin.GUI, Event.Outcome.OK, orderPost, null));
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
    private void setAvailableQuantity(Product product){
        String selectedColor = (String) colorBox.getSelectedItem();
        String selectedSize = (String) sizeBox.getSelectedItem();
        System.out.println("SET AVAILABLE QUANTITY is reached in PurchasePanel. Color: " + selectedColor + " Size: " + selectedSize);
        if (selectedColor!= null && !selectedColor.equals(colorIntro) && selectedSize!= null && !selectedSize.equals(sizeIntro)) {
            try {
                System.out.println("tryParse is reached in setAvailableQuantity");
                int thisSize = Integer.parseInt(selectedSize);
                int q = getCurrentInventoryFor(selectedColor, thisSize);
                System.out.println("---- \n size is: " + thisSize + "\ncolor is: " + selectedColor +  "\nq is: " + q);
                quantityAvailabilityArea.setText(String.valueOf(q));
            } catch (NumberFormatException e) {
            }
        }
    }
    private void getSizes(Product product, String color) {
        System.out.println("getSizes is reached, value is: " + color);
        String thisSize = (String) sizeBox.getSelectedItem();
        List<Integer> newSizes = new ArrayList<>();
        if (!color.equals(colorIntro)) {
            sizeBox.removeActionListener(sizeListener);
            sizeBox.removeAllItems();
            sizeBox.addItem(sizeIntro);
            System.out.println("sc.size: " + product.getShoeSpecifications().size());
            Set<Integer> sizeSet = new LinkedHashSet<>();
            for (ShoeSpecification sc : product.getShoeSpecifications()) {
                if (sc.getColor().equals(color)) {
                    sizeSet.add(sc.getSize());
                }
            }
            newSizes.addAll(sizeSet);
            for (int i : newSizes) {
                System.out.println("newSize: " + i);
                sizeBox.addItem(String.valueOf(i));
                if (String.valueOf(i).equals(thisSize)){
                    sizeBox.setSelectedItem(String.valueOf(i));
                }
            }
            if (!thisSize.equals(sizeBox.getSelectedItem())){
                sizeBox.setSelectedItem(sizeIntro);
            }
            sizeBox.addActionListener(sizeListener);
            repaint();
            revalidate();
        }
    }
    private void getColors(Product product, int sizeValue) {
        String thisColor = (String) colorBox.getSelectedItem();
        colorBox.removeActionListener(colorListener);
        colorBox.removeAllItems();
        colorBox.addItem(colorIntro);
        List<String> newColors = new ArrayList<>();
        Set<String> colorSet = new LinkedHashSet<>();
        for (ShoeSpecification sc : product.getShoeSpecifications()) {
            if (sc.getSize() == sizeValue) {
                colorSet.add(sc.getColor());
            }
        }
        newColors.addAll(colorSet);
        for (String s : newColors) {
            colorBox.addItem(s);
            if (s.equals(thisColor)){
                colorBox.setSelectedItem(s);
            }
        }
        if (thisColor == null || !thisColor.equals(colorBox.getSelectedItem())){
            colorBox.setSelectedItem(colorIntro);
        }
        colorBox.addActionListener(colorListener);
    }

    public int getCurrentInventoryFor(String color, int size) {
        System.out.println("getInventoryFor is reached. Color: " + color + " size: " + size);
        for (ShoeSpecification sc : p.getShoeSpecifications()) {
            if (sc.getColor().equals(color) && sc.getSize() == size) {
                System.out.println("invQuantity is: " + sc.getInvQuantity());
                return sc.getInvQuantity();
            }
        }
        return -1;
    }
}