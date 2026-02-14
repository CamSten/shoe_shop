package GUI;

import Control.Event;
import Model.DataHandling.OrderPost;

import javax.swing.*;
import java.util.List;

public class CartPanel extends JPanel {
    private MainFrame mainFrame;
    private PanelDecorator decorator;
    private Event event;
    private JPanel orderPanel;
    private JLabel totalPriceLabel;

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
            populateOrders((List<OrderPost>) list);
        }
        return wrapper;
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
}
