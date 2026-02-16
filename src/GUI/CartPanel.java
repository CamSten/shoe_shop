package GUI;

import Control.Event;
import Model.DataHandling.OrderPost;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CartPanel extends JPanel {
    private MainFrame mainFrame;
    private PanelDecorator decorator;
    private Event event;
    private JPanel orderPanel;
    private JLabel totalPriceLabel;
    private List<String> titles;
    private List<JLabel> columnHeaders;
    private List<List<JLabel>> dataEntries;
    private JPanel cartPanel;

    public CartPanel(MainFrame mainFrame, Event event, PanelDecorator decorator) {
        this.mainFrame = mainFrame;
        this.event = event;
        this.decorator = decorator;
        setBackground(Colors.bg());
        add(getCartPanel());
        repaint();
        revalidate();
    }

    private JPanel getCartPanel() {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        decorator.adjustWrapperPanel(wrapper);
        this.orderPanel = new JPanel();
        orderPanel.setBackground(Colors.bg());
        totalPriceLabel = new JLabel("Total price: 0 SEK");
        decorator.adjustBrandLabel(totalPriceLabel);
        totalPriceLabel.setBorder(BorderFactory.createLineBorder(Colors.bg(), 5, true));
        orderPanel.add(wrapper);
        if (event.getContents() instanceof List<?> list && !list.isEmpty() && list.get(0) instanceof OrderPost) {
            wrapper.add(getOrdersPanel((List<OrderPost>) list));
            if (totalPriceLabel != null){
                wrapper.add(totalPriceLabel);
            }
        }
        else {
            JPanel nullDisplay = new JPanel();
            JLabel nullConf = new JLabel("No result was found.");
            nullConf.setFont(Fonts.getSemiHeaderFont());
            nullConf.setForeground(Colors.textMuted());
            nullDisplay.add(nullConf);
            decorator.adjustSingleResultLine(nullDisplay);
            wrapper.add(nullDisplay);
            this.cartPanel = wrapper;
        }
        return wrapper;
    }

    private JPanel getOrdersPanel(List<OrderPost> allPosts){
        this.titles = new ArrayList<>();
        titles.add("Brand");
        titles.add("Name");
        titles.add("Color");
        titles.add("Size");
        titles.add("Quantity");
        titles.add("Added");

        this.dataEntries = new ArrayList<>();
        getColumnHeaders();
        int total = 0;
        for (OrderPost op : allPosts) {
            List<JLabel> row = new ArrayList<>();
            row.add(getCellLabel(op.getBrand()));
            row.add(getCellLabel(op.getName()));
            row.add(getCellLabel(op.getColor()));
            row.add(getCellLabel(String.valueOf(op.getPrice())));
            row.add(getCellLabel(String.valueOf(op.getQuantity())));
            row.add(getCellLabel(op.getFormattedDate()));
            dataEntries.add(row);
            total += op.getPrice() * op.getQuantity();
            totalPriceLabel.setText("Total price: " + total + " SEK");
        }

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
        for (int i = 0; i < columnHeaders.size(); i++){
            JPanel wrapper = new JPanel();
            decorator.adjustWrapperPanel(wrapper);
            JPanel column = new JPanel();
            decorator.adjustInputPanel(column);
            column.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            System.out.println("columnHeaders.get(i): " + columnHeaders.get(i).getText());
            column.add(columnHeaders.get(i));
            for (List<JLabel> labelList : dataEntries){
                System.out.println("dataEntries.get(i).get(j): " + labelList.get(i).getText());
                column.add(labelList.get(i));
            }

            wrapper.add(column);
            infoPanel.add(Box.createHorizontalGlue());
            infoPanel.add(wrapper);
            infoPanel.add(Box.createHorizontalGlue());
        }
        infoPanel.setBorder(BorderFactory.createLineBorder(Colors.border(), 10, true));
        return infoPanel;
    }

    private void getColumnHeaders() {
        this.columnHeaders = new ArrayList<>();
        for (String t : titles) {
            System.out.println("title in getColumnHeaderPanel: " + t);
            JLabel label = new JLabel(t);
            decorator.adjustBrandLabel(label);
            label.setBorder(BorderFactory.createLineBorder(Colors.border(), 5, true));
//            label.setOpaque(true);
            columnHeaders.add(label);
        }
    }
    private JPanel getColumnHeaderPanel() {
        JPanel wrapperPanel = new JPanel();
        JPanel columnPanel = new JPanel();
        columnPanel.setLayout(new BoxLayout(columnPanel, BoxLayout.X_AXIS));
        columnPanel.setBackground(Colors.panel());
        columnPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        String[] titles = {"Brand", "Name", "Color", "Size", "Quantity", "Added"};
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
private void getEmptyStockMsg(){
        JPanel wrapper = new JPanel(new GridLayout(2, 1));
        JLabel msg = new JLabel("You've bought the last pair!");
        decorator.adjustBrandLabel(msg);
        JLabel stockInfo = new JLabel("Stock is now empty for this size and color.");
        decorator.adjustLabel(stockInfo);
        wrapper.add(msg);
        wrapper.add(stockInfo);
        decorator.adjustWrapperPanel(wrapper);
        JPanel stockMsgPanel = new JPanel();
        stockMsgPanel.add(wrapper);
        cartPanel.add(stockMsgPanel);
}
    private void populateOrders(JPanel wrapperPanel, List<OrderPost> orders) {
        orderPanel.removeAll();
       JPanel subWrapper = new JPanel();
       subWrapper.setLayout(new GridLayout(orders.size(), 1));
        int total = 0;
        for (OrderPost op : orders) {
            JPanel rowWrapper = new JPanel();
            JPanel row = getOrderRowPanel(op);
            rowWrapper.add(row);
            subWrapper.add(rowWrapper);
            total += op.getPrice() * op.getQuantity();
        }
        wrapperPanel.add(subWrapper, BorderLayout.CENTER);
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
        decorator.adjustLabel(label);
        return label;
    }
}
