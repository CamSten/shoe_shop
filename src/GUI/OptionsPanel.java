package GUI;

import javax.lang.model.util.SimpleElementVisitor7;
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
    private Control.Event event;

    private List<Model.Product> shoes;

    private JPanel displayingResult;
    private JPanel inputPanel;

    public OptionsPanel (MainFrame mainFrame, PanelDecorator decorator, Control.Event event){
        System.out.println("OptionsPanel constructor is reached");
        if (event.getContents() != null){
            System.out.println("optionsPanel contents instance of: " + event.getContents().getClass());
            List<Object> list = (List<Object>) event.getContents();
            System.out.println("list.size is: " + list.size());
            if (list.size()> 0) {
                System.out.println("list.getFirst instance of: " + list.getFirst().getClass());
            }
        }
        this.mainFrame = mainFrame;
        this.event = event;
        this.decorator = decorator;
        setLayout(new BorderLayout());
        setBackground(Colors.getBackgroundColor());
        JPanel headerPanel = new HeaderPanel(decorator, event);
        add(headerPanel, BorderLayout.NORTH);
        add(getOptionsPanel(event), BorderLayout.CENTER);
    }

    public JPanel getOptionsPanel(Event event){
        this.displayingResult = new JPanel();
        displayingResult.setLayout(new BoxLayout(displayingResult, BoxLayout.Y_AXIS));
        displayingResult.setBackground(Colors.getBackgroundColor());
        for (JPanel results : getResult(event)){
            displayingResult.add(results);
            displayingResult.add(Box.createVerticalStrut(5));
        }
        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setBackground(Colors.getBackgroundColor());
        wrapperPanel.setBorder(
                BorderFactory.createEmptyBorder(10, 40, 10, 40)
        );
        decorator.adjustWrapperPanel(wrapperPanel);
        this.inputPanel = wrapperPanel;
        addScrollBar(inputPanel, displayingResult);
        return wrapperPanel;
    }

    private List<JPanel> getResult(Event event){
        System.out.println("GET RESULT IN OPTIONSPANEL IS REACHED");

        if (event.getContents() != null){
            System.out.println("optionsPanel GET RESULT contents instance of: " + event.getContents().getClass());
            List<Object> list = (List<Object>) event.getContents();
            System.out.println("list.size is: " + list.size());
        }
        Object data = event.getContents();
        List <JPanel> allSingleResultPanels = new ArrayList<>();
        if (data instanceof List list && list.getFirst() instanceof Product) {
            System.out.println("in OPTIONSPANEL GET RESULT, DATA INSTANCE OF PRODUCT-LIST");
            List<Product> shoes = (List<Product>) data;
            List<String> displayTerms = new ArrayList<>();
            for (Product p : shoes) {
                String s = getTerm(p, event);
                JButton button = new JButton(s);
                button.addActionListener(_ ->
                {
                    System.out.println("mainFrame.update is reached in optionsPanel.get result");
                    try {
                        sendUserChoice(p, event);
                    } catch (SQLException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                });
                JPanel buttonPanel = new JPanel();
                buttonPanel.add(button);
                allSingleResultPanels.add(buttonPanel);
            }
        }
        return allSingleResultPanels;
    }
    public JPanel getAdjustedInputPanel (JLabel label, Object inputArea){
        JPanel inputPanel = new JPanel();
        JPanel queryPanel = new JPanel();
        queryPanel.setLayout(new GridLayout(1, 2));
//        queryPanel.setBorder(BorderFactory.createLineBorder(Colors.getBorderColor(), 5, true));
        queryPanel.setBackground(Colors.getHeaderColor());
        queryPanel.add(label);
        if(inputArea instanceof JTextField) {
            JTextField textField = (JTextField) inputArea;
            queryPanel.add(textField);
        }
        else if (inputArea instanceof JButton){
            JButton button = (JButton) inputArea;
            queryPanel.add(button);
        }
        inputPanel.add(Box.createVerticalStrut(5));
        inputPanel.add(queryPanel);
        inputPanel.add(Box.createVerticalStrut(5));
        return inputPanel;
    }


    public void addScrollBar(JPanel inputPanel, JPanel panel){
        inputPanel.removeAll();
        JScrollPane scrollResults = new JScrollPane(panel);
        scrollResults.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollResults.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollResults.setPreferredSize(new Dimension(550, 400));
        SwingUtilities.invokeLater(() ->
                scrollResults.getVerticalScrollBar().setValue(0)
        );
        inputPanel.add(scrollResults);
    }
    private ProductTerm getNextProductTerm(ProductTerm p){
        System.out.println("in OPTIONS PANEL GET NEXT PRODUCT TERM p is: " + p);
        ProductTerm nextProductTerm = null;
        switch (p){
            case None -> {
                nextProductTerm = ProductTerm.Brand;
            }
            case Name -> {
                nextProductTerm = ProductTerm.Color;
            }
            case Brand -> {
                nextProductTerm = ProductTerm.Name;
            }
            case Color -> {
                nextProductTerm = ProductTerm.Size;
            }
        }
        return nextProductTerm;
    }

    private String getTerm(Product p, Event event){
        System.out.println("----GET TERM IN OPTIONSPANEL IS REACHED");
        String term = "";
        ProductTerm pt = null;
        if (event.getContents()!= null){
            System.out.println("contents: " + event.getContents().getClass());
        }
        if (event.getExtraContents() == null){
            pt = ProductTerm.Brand;
        }
        else {
            System.out.println("extra contents is: " + event.getExtraContents().getClass());
            pt = (ProductTerm) event.getExtraContents();
        }
        switch (pt) {
            case Brand-> {
                term = p.getBrand();
            }
            case Color -> {
                term = p.getColor();
            }
            case Size -> {
                term = p.getSize();
            }
            case Name -> {
                term = p.getName();
            }
        }

        System.out.println("TERM IS: " + term);
        return term;
    }
    private void sendUserChoice(Product p, Event event) throws SQLException, ClassNotFoundException {
        System.out.println("SEND USER CHOICE IN OPTIONS PANEL IS REACHED");
        ProductTerm thisProductTerm = ProductTerm.Brand;

        if (event.getExtraContents() != null){
            System.out.println("optionsPanel GET RESULT EXTRA contents instance of: " + event.getExtraContents().getClass());
            if (event.getExtraContents() instanceof ProductTerm){
                thisProductTerm = (ProductTerm) event.getExtraContents();
                System.out.println("extracontent productTerm is: " + thisProductTerm);
            }
        }
        if (event.getContents() != null ) {
            System.out.println("event.getContents is: " + event.getContents().getClass());
            if (event.getContents() instanceof ProductTerm) {
                thisProductTerm = (ProductTerm) event.getContents();
                System.out.println("content productTerm is: " + thisProductTerm);
            }
        }
        ProductTerm nextProductTerm = getNextProductTerm(thisProductTerm);
        List<String> forQuery = getSpecificationList(p, nextProductTerm);
        System.out.println("in OPTIONSPANEL SEND USER CHOICE, thispt is: " + thisProductTerm + " NEXT PT:  " + nextProductTerm);
        mainFrame.Update(new Event(Event.Phase.SELECT, Event.Action.VIEW, Event.Subject.SHOE, Event.Origin.GUI, null, forQuery, nextProductTerm));
    }
    private List<String> getSpecificationList(Product product, ProductTerm pt){
        List<String> specificationList = new ArrayList<>();
        switch (pt){
            case Name -> {
                specificationList.add(product.getBrand());
            }
            case Color -> {
                specificationList.add(product.getBrand());
                specificationList.add(product.getName());
            }
            case Size -> {
                specificationList.add(product.getBrand());
                specificationList.add(product.getName());
                specificationList.add(product.getColor());
            }
        }
        return specificationList;
    }
}
