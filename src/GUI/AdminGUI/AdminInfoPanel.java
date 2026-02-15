package GUI.AdminGUI;

import Control.Event;
import Control.Subscriber;
import GUI.Colors;
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
    private JPanel orderPanel;
    private JLabel totalPriceLabel;
    private List<String> titles;

    public AdminInfoPanel(MainFrame mainFrame, PanelDecorator decorator, Event event) {
        this.mainframe = mainFrame;
        this.decorator = decorator;
        this.event = event;
        if (event.getContents() instanceof List list) {
            switch (event.getSubject()) {
                case SALES -> {
                    if (list.getFirst() instanceof SalesPost){
                        List<SalesPost> allPosts = (List<SalesPost>) list;
                        getSalesPanel(allPosts);
                    }
                }
                case STOCK -> {
                    if (list.getFirst() instanceof InventoryPost ) {
                        List<InventoryPost> allStock = (List<InventoryPost>) list;
                        getInventoryPanel(allStock);
                    }
                }
                case CART -> {
                    if (list.getFirst() instanceof OrderPost) {
                        List<OrderPost> allPosts = (List<OrderPost>) list;
                        getOrdersPanel(allPosts);
                    }
                }
            }
        }
    }
    private void getSalesPanel(List<SalesPost> allPosts){
        JPanel salesPanel = new JPanel();
        this.titles = new ArrayList<>();
        titles.add("Brand:");
        titles.add("Name:");
        titles.add("Quantity sold:");
        JPanel titlePanel = getColumnHeaderPanel();
        adjustPanel(salesPanel);

    }
    private void getInventoryPanel(List<InventoryPost> allStock){
        JPanel inventoryPanel = new JPanel();
        this.titles = new ArrayList<>();
        titles.add("Category:");
        titles.add("Brand:");
        titles.add("Name:");
        titles.add("Color:");
        titles.add("Size:");
        titles.add("Price:");
        titles.add("Available quantity:");
        JPanel titlePanel = getColumnHeaderPanel();
        adjustPanel(inventoryPanel);
    }
    private void getOrdersPanel(List<OrderPost> allPosts){
        JPanel ordersPanel = new JPanel();

        this.titles = new ArrayList<>();
        titles.add("Brand");
        titles.add("Name");
        titles.add("Color");
        titles.add("Size");
        titles.add("Quantity");
        titles.add("Added");
        JPanel titlePanel = getColumnHeaderPanel();
        populateOrders(allPosts);
        adjustPanel(ordersPanel);
    }

    private void getInfoPanel() {
        JPanel wrapper = new JPanel();
        decorator.adjustWrapperPanel(wrapper);
        wrapper.add(getColumnHeaderPanel());
        this.orderPanel = new JPanel();
        orderPanel.setLayout(new BoxLayout(orderPanel, BoxLayout.Y_AXIS));
        orderPanel.setBackground(Colors.bg());
        totalPriceLabel = new JLabel("Total price: 0 SEK");
        decorator.adjustLabel(totalPriceLabel);
        wrapper.add(totalPriceLabel);
        orderPanel.add(wrapper);
        if (event.getContents() instanceof List<?> list && !list.isEmpty() && list.get(0) instanceof OrderPost) {

        }
    }
    private JPanel getColumnHeaderPanel() {
        JPanel wrapperPanel = new JPanel();
        JPanel columnPanel = new JPanel();
        columnPanel.setLayout(new BoxLayout(columnPanel, BoxLayout.X_AXIS));
        columnPanel.setBackground(Colors.panel());
        columnPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        for (String t : titles) {
            System.out.println("title in getColumnHeaderPanel: " + t);
            JLabel label = new JLabel(t);
            decorator.adjustLabel(label);
            label.setOpaque(true);
            columnPanel.add(label);
        }
        wrapperPanel.add(columnPanel);
        return wrapperPanel;
    }

    private void populateOrders(List<OrderPost> orders) {
        orderPanel.removeAll();
        JPanel wrapperPanel = new JPanel();
        int total = 0;
        for (OrderPost op : orders) {
            JPanel row = getOrderRowPanel(op);
            wrapperPanel.add(row);
            total += op.getPrice() * op.getQuantity();
        }
        orderPanel.add(wrapperPanel);
        totalPriceLabel.setText("Total price: " + total + " SEK");

        orderPanel.repaint();
        orderPanel.revalidate();
    }

    private JPanel getOrderRowPanel(OrderPost order) {
        JPanel wrapperPanel = new JPanel();
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        decorator.adjustWrapperPanel(row);
        row.setBorder(BorderFactory.createLineBorder(Colors.border(), 2, true));

        row.add(getCellLabel(order.getBrand()));
        row.add(getCellLabel(order.getName()));
        row.add(getCellLabel(order.getColor()));
        row.add(getCellLabel(String.valueOf(order.getSize())));
        row.add(getCellLabel(String.valueOf(order.getQuantity())));
        row.add(getCellLabel(order.getFormattedDate()));
        wrapperPanel.add(row);
        return wrapperPanel;
    }

    private JLabel getCellLabel(String text) {
        JLabel label = new JLabel(text);
        decorator.adjustSmallLabel(label);
        label.setOpaque(true);
        return label;
    }

    private void adjustPanel(JPanel panel){
        removeAll();
        setBackground(Colors.bg());
        add(panel);
        repaint();
        revalidate();
    }
    @Override
    public void Update(Event event) throws SQLException, ClassNotFoundException {

        }
    }

