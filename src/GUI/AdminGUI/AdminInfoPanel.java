package GUI.AdminGUI;

import Control.Event;
import Control.Subscriber;
import GUI.Colors;
import GUI.Fonts;
import GUI.PanelDecorator;
import GUI.MainFrame;
import Model.DataHandling.InventoryPost;
import Model.DataHandling.OrderPost;
import Model.DataHandling.SalesPost;

import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminInfoPanel extends JPanel implements Subscriber {
    private MainFrame mainframe;
    private PanelDecorator decorator;
    private Event event;
    private JPanel centerPanel;
    private JPanel infoPanel;
//    private JLabel totalPriceLabel;
    private List<String> titles;
    private List<JLabel> columnHeaders;
    private List<List<JLabel>> dataEntries;

    public AdminInfoPanel(MainFrame mainFrame, PanelDecorator decorator, Event event) {
        System.out.println("adminInfoPanel is reached");
        this.mainframe = mainFrame;
        this.decorator = decorator;
        this.event = event;
        if (event.getContents() instanceof List list && list.isEmpty()) {
            showNoResult();
            adjustPanel();
        }
        else {
            switch (event.getSubject()) {
                case SALES -> {
                    System.out.println("in adminInfoPanel, case SALES is reached");
                    if (event.getContents() instanceof List list && list.getFirst() instanceof SalesPost){
                        List<SalesPost> allPosts = (List<SalesPost>) list;
                        getSalesPanel(allPosts);
                    }
                }
                case STOCK -> {
                    System.out.println("in adminInfoPanel, case STOCK is reached");
                    if (event.getContents() instanceof List list && list.getFirst() instanceof InventoryPost ) {
                        List<InventoryPost> allStock = (List<InventoryPost>) list;
                        getInventoryPanel(allStock);
                    }
                }
                case CART -> {
                    System.out.println("in adminInfoPanel, case CART is reached");
                    if (event.getContents() instanceof List list && list.getFirst() instanceof OrderPost) {
                        List<OrderPost> allPosts = (List<OrderPost>) list;
                        getOrdersPanel(allPosts);
                    }
                }
                case NON_STOCK ->{
                    if (event.getContents() instanceof List list && list.getFirst() instanceof InventoryPost ) {
                        List<InventoryPost> allStock = (List<InventoryPost>) list;
                        getOutOfStockpanel(allStock);
                    }
                }
            }
            getColumnHeaders();
            getInfoPanel();
            adjustPanel();
        }
    }
    private void showNoResult(){
         this.infoPanel = new JPanel();
         JLabel nullConf = new JLabel("No result was found.");
         nullConf.setFont(Fonts.getSemiHeaderFont());
         nullConf.setForeground(Colors.textMuted());
         infoPanel.add(nullConf);
         decorator.adjustSingleResultLine(infoPanel);
    }
    private void getInfoPanel() {
        this.infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
        for (int i = 0; i < columnHeaders.size(); i++) {
            JPanel wrapper = new JPanel();
            decorator.adjustWrapperPanel(wrapper);
            JPanel column = new JPanel();
            decorator.adjustInputPanel(column);
            column.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            System.out.println("columnHeaders.get(i): " + columnHeaders.get(i).getText());
            column.add(columnHeaders.get(i));
            for (List<JLabel> labelList : dataEntries) {
                System.out.println("dataEntries.get(i).get(j): " + labelList.get(i).getText());
                column.add(labelList.get(i));
            }

            wrapper.add(column);
            infoPanel.add(Box.createHorizontalGlue());
            infoPanel.add(wrapper);
            infoPanel.add(Box.createHorizontalGlue());
        }
    }
    private void getSalesPanel(List<SalesPost> allPosts){
        System.out.println("getSalesPanel is reached in adminInfoPanel");
        this.titles = new ArrayList<>();
        titles.add("Brand:");
        titles.add("Name:");
        titles.add("Quantity sold:");

        this.dataEntries = new ArrayList<>();
        for (SalesPost post : allPosts){
            List<JLabel> row = new ArrayList<>();
            row.add(getCellLabel(post.getBrand()));
            row.add(getCellLabel(post.getName()));
            row.add(getCellLabel(String.valueOf(post.getSoldQuantity())));
            dataEntries.add(row);
        }
    }
    private void getOutOfStockpanel(List<InventoryPost> allStock){
        this.titles = new ArrayList<>();
        titles.add("Brand:");
        titles.add("Name:");
        titles.add("Color:");
        titles.add("Size:");
        titles.add("Price:");

        this.dataEntries = new ArrayList<>();
        for (InventoryPost post : allStock) {
            List<JLabel> row = new ArrayList<>();
            row.add(getCellLabel(post.getProductBrand()));
            row.add(getCellLabel(post.getProductName()));
            row.add(getCellLabel(post.getProductColor()));
            row.add(getCellLabel(String.valueOf(post.getProductSize())));
            row.add(getCellLabel(String.valueOf(post.getPrice())));
            dataEntries.add(row);
        }

    }
    private void getInventoryPanel(List<InventoryPost> allStock){
        System.out.println("getInventoryPanel is reached in AdminInfoPanel");
        this.titles = new ArrayList<>();
//        titles.add("Category:");
        titles.add("Brand:");
        titles.add("Name:");
        titles.add("Color:");
        titles.add("Size:");
        titles.add("Price:");
        titles.add("Available quantity:");

        this.dataEntries = new ArrayList<>();
        for (InventoryPost post : allStock){
            List<JLabel> row = new ArrayList<>();
//            row.add(getCellLabel(post.getCategory()));
            row.add(getCellLabel(post.getProductBrand()));
            row.add(getCellLabel(post.getProductName()));
            row.add(getCellLabel(post.getProductColor()));
            row.add(getCellLabel(String.valueOf(post.getProductSize())));
            row.add(getCellLabel(String.valueOf(post.getPrice())));
            row.add(getCellLabel(String.valueOf(post.getStockQuantity())));
            dataEntries.add(row);
        }
    }
    private void getOrdersPanel(List<OrderPost> allPosts){
        System.out.println("getOrdersPanel is reached in AdminInfoPanel");
        this.titles = new ArrayList<>();
        titles.add("Customer ID:");
        titles.add("Brand");
        titles.add("Name");
        titles.add("Color");
        titles.add("Size");
        titles.add("Quantity");
        titles.add("Added");

        this.dataEntries = new ArrayList<>();
        int total = 0;
        for (OrderPost op : allPosts) {
            List<JLabel> row = new ArrayList<>();
            row.add(getCellLabel(String.valueOf(op.getCustomerId())));
            row.add(getCellLabel(op.getBrand()));
            row.add(getCellLabel(op.getName()));
            row.add(getCellLabel(op.getColor()));
            row.add(getCellLabel(String.valueOf(op.getPrice())));
            row.add(getCellLabel(String.valueOf(op.getQuantity())));
            row.add(getCellLabel(op.getFormattedDate()));
            dataEntries.add(row);
//            total += op.getPrice() * op.getQuantity();
//            totalPriceLabel.setText("Total price: " + total + " SEK");
        }
    }

//    private void getInfoPanel() {
//        JPanel wrapper = new JPanel();
//        decorator.adjustWrapperPanel(wrapper);
//        wrapper.add(getColumnHeaders());
//        this.centerPanel = new JPanel();
//        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
//        centerPanel.setBackground(Colors.bg());
//        totalPriceLabel = new JLabel("Total price: 0 SEK");
//        decorator.adjustLabel(totalPriceLabel);
//        wrapper.add(totalPriceLabel);
//        centerPanel.add(wrapper);
//        if (event.getContents() instanceof List<?> list && !list.isEmpty() && list.get(0) instanceof OrderPost) {
//
//        }
//    }
    private void getColumnHeaders() {
        this.columnHeaders = new ArrayList<>();
        for (String t : titles) {
            System.out.println("title in getColumnHeaderPanel: " + t);
            JLabel label = new JLabel(t);
            decorator.adjustLabel(label);
            label.setOpaque(true);
            columnHeaders.add(label);
        }
    }

//    private void populateOrders() {
//        this.infoPanel = new JPanel();
//        decorator.adjustWrapperPanel(infoPanel);
//        if (event.getContents() instanceof List list && !list.isEmpty()) {
//            if (list.getFirst() instanceof OrderPost) {
//                List<OrderPost> orders = (List<OrderPost>) event.getContents();
//                this.dataEntries = new ArrayList<>();
//                int total = 0;
//                for (OrderPost op : orders) {
//                    List<JLabel> row = new ArrayList<>();
//                    row.add(getCellLabel(op.getBrand()));
//                    row.add(getCellLabel(op.getName()));
//                    row.add(getCellLabel(op.getColor()));
//                    row.add(getCellLabel(String.valueOf(op.getPrice())));
//                    row.add(getCellLabel(String.valueOf(op.getQuantity())));
//                    row.add(getCellLabel(op.getFormattedDate()));
//                    dataEntries.add(row);
//                    total += op.getPrice() * op.getQuantity();
//                    totalPriceLabel.setText("Total price: " + total + " SEK");
//                }
//            } else if (list.getFirst() instanceof InventoryPost){
//                List<InventoryPost> allPosts = (List<InventoryPost>) event.getContents();
//                this.dataEntries = new ArrayList<>();
//                for (InventoryPost post : allPosts){
//                    List<JLabel> row = new ArrayList<>();
//                    row.add(getCellLabel(post.getCategory()));
//                    row.add(getCellLabel(post.getProductBrand()));
//                    row.add(getCellLabel(post.getProductName()));
//                    row.add(getCellLabel(String.valueOf(post.getProductSize())));
//                    row.add(getCellLabel(String.valueOf(post.getPrice())));
//                    row.add(getCellLabel(String.valueOf(post.getStockQuantity())));
//                    dataEntries.add(row);
//                }
//            }
//            else if (list.getFirst() instanceof SalesPost){
//                List<SalesPost> allPosts = (List<SalesPost>) event.getContents();
//                this.dataEntries = new ArrayList<>();
//                for (SalesPost post : allPosts){
//                    List<JLabel> row = new ArrayList<>();
////                    row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
////                    decorator.adjustWrapperPanel(row);
////                    row.setBorder(BorderFactory.createLineBorder(Colors.border(), 2, true));
//                    row.add(getCellLabel(post.getBrand()));
//                    row.add(getCellLabel(post.getName()));
//                    row.add(getCellLabel(String.valueOf(post.getSoldQuantity())));
//                    dataEntries.add(row);
//                }
//            }
//        }
//    }

//    private JPanel getOrderRowPanel(OrderPost order) {
//        JPanel wrapperPanel = new JPanel();
//        JPanel row = new JPanel();
//        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
//        decorator.adjustWrapperPanel(row);
//        row.setBorder(BorderFactory.createLineBorder(Colors.border(), 2, true));
//        row.add(getCellLabel(order.getBrand()));
//        row.add(getCellLabel(order.getName()));
//        row.add(getCellLabel(order.getColor()));
//        row.add(getCellLabel(String.valueOf(order.getSize())));
//        row.add(getCellLabel(String.valueOf(order.getQuantity())));
//        row.add(getCellLabel(order.getFormattedDate()));
//        wrapperPanel.add(row);
//        return wrapperPanel;
//    }

    private JLabel getCellLabel(String text) {
        JLabel label = new JLabel(text);
        decorator.adjustSmallLabel(label);
        label.setOpaque(true);
        return label;
    }

    private void adjustPanel(){
        if (centerPanel == null){
            this.centerPanel = new JPanel();
            centerPanel.setBackground(Colors.bg());
            add(centerPanel);
        }
        else {
            centerPanel.removeAll();
        }
//        this.infoPanel = new JPanel();
//        for (int i = 0; i < columnHeaders.size(); i++){
//            infoPanel.add(columnHeaders.get(i));
//            for (int j = 0; j < dataEntries.size(); j++){
//                infoPanel.add(dataEntries.get(i).get(j));
//            }
//        }
//        if (totalPriceLabel != null){
//            infoPanel.add(totalPriceLabel);
//        }
        centerPanel.add(infoPanel);
        repaint();
        revalidate();
    }
    @Override
    public void Update(Event event) throws SQLException, ClassNotFoundException {

        }
    }

