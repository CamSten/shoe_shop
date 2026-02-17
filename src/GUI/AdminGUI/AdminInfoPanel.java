package GUI.AdminGUI;

import Control.Event;
import Control.Subscriber;
import GUI.Colors;
import GUI.Fonts;
import GUI.PanelDecorator;
import GUI.MainFrame;
import GUI.InfoPanel;
import Model.DataHandling.InventoryPost;
import Model.DataHandling.OrderPost;
import Model.DataHandling.SalesPost;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminInfoPanel extends JPanel {
    private MainFrame mainframe;
    private PanelDecorator decorator;
    private Event event;
    private JPanel centerPanel;
    private JPanel dataPanel;
    private InfoPanel infoPanel;
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
                        getSalesData(allPosts);
                    }
                }
                case STOCK -> {
                    System.out.println("in adminInfoPanel, case STOCK is reached");
                    if (event.getContents() instanceof List list && list.getFirst() instanceof InventoryPost ) {
                        List<InventoryPost> allStock = (List<InventoryPost>) list;
                        getInventoryData(allStock);
                    }
                }
                case CART -> {
                    System.out.println("in adminInfoPanel, case CART is reached");
                    if (event.getContents() instanceof List list && list.getFirst() instanceof OrderPost) {
                        List<OrderPost> allPosts = (List<OrderPost>) list;
                        getOrdersData(allPosts);
                    }
                }
                case NON_STOCK ->{
                    if (event.getContents() instanceof List list && list.getFirst() instanceof InventoryPost ) {
                        List<InventoryPost> allStock = (List<InventoryPost>) list;
                        getOutOfStockData(allStock);
                    }
                }
            }

            this.dataPanel = new JPanel();
            decorator.adjustWrapperPanel(dataPanel);
            dataPanel.add(new InfoPanel(titles, dataEntries, decorator));
            adjustPanel();
        }
    }
    private void showNoResult(){
        this.dataPanel = new JPanel();
        JLabel nullConf = new JLabel("No result was found.");
        nullConf.setFont(Fonts.getSemiHeaderFont());
        nullConf.setForeground(Colors.textMuted());
        dataPanel.add(nullConf);
        decorator.adjustSingleResultLine(dataPanel);
    }

    private void getSalesData(List<SalesPost> allPosts){
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
    private void getOutOfStockData(List<InventoryPost> allStock){
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
    private void getInventoryData(List<InventoryPost> allStock){
        System.out.println("getInventoryPanel is reached in AdminInfoPanel");
        this.titles = new ArrayList<>();
        titles.add("Brand:");
        titles.add("Name:");
        titles.add("Color:");
        titles.add("Size:");
        titles.add("Price:");
        titles.add("Available quantity:");

        this.dataEntries = new ArrayList<>();
        for (InventoryPost post : allStock){
            List<JLabel> row = new ArrayList<>();
            row.add(getCellLabel(post.getProductBrand()));
            row.add(getCellLabel(post.getProductName()));
            row.add(getCellLabel(post.getProductColor()));
            row.add(getCellLabel(String.valueOf(post.getProductSize())));
            row.add(getCellLabel(String.valueOf(post.getPrice())));
            row.add(getCellLabel(String.valueOf(post.getStockQuantity())));
            dataEntries.add(row);
        }
    }
    private void getOrdersData(List<OrderPost> allPosts){
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
        }
    }

    private JLabel getCellLabel(String text) {
        JLabel label = new JLabel(text);
        decorator.adjustLabel(label);
        label.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        return label;
    }
    private void adjustPanel() {
        if (centerPanel == null) {
            this.centerPanel = new JPanel();
            centerPanel.setBackground(Colors.panel());
            add(centerPanel);
            setBackground(Colors.border());
        } else {
            centerPanel.removeAll();
        }
        addScrollBar(centerPanel, dataPanel);
        repaint();
        revalidate();
    }
    @Override
    public Dimension getPreferredSize() {
        int width = titles.size() * 150;
        int height = mainframe.getMaxHeight()- 50;
       // int height = 50 + dataEntries.size() * 30;
        return new Dimension(width, height);
    }

    public void addScrollBar(JPanel inputPanel, JPanel panel) {
   inputPanel.removeAll();
   inputPanel.setLayout(new BorderLayout());
        JScrollPane scrollResults = new JScrollPane(panel);
        scrollResults.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollResults.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollResults.setPreferredSize(getPreferredSize());
        SwingUtilities.invokeLater(() ->
                scrollResults.getVerticalScrollBar().setValue(0)
        );
        inputPanel.add(scrollResults, BorderLayout.CENTER);
    }
}