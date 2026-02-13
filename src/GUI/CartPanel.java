package GUI;

import Control.Event;
import Model.OrderPost;

import javax.swing.*;
import java.awt.*;
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
        setLayout(new BorderLayout());
        setBackground(Colors.bg());
        add(getCartPanel(), BorderLayout.CENTER);
    }

    private JPanel getCartPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        decorator.adjustWrapperPanel(wrapper);
        wrapper.add(getColumnHeaderPanel(), BorderLayout.CENTER);
        orderPanel = new JPanel();
        orderPanel.setLayout(new BoxLayout(orderPanel, BoxLayout.Y_AXIS));
        orderPanel.setBackground(Colors.bg());
        wrapper.add(orderPanel, BorderLayout.CENTER);
        totalPriceLabel = new JLabel("Total price: 0 SEK");
        decorator.adjustLabel(totalPriceLabel);
        wrapper.add(totalPriceLabel, BorderLayout.SOUTH);
        if (event.getContents() instanceof List<?> list && !list.isEmpty() && list.get(0) instanceof OrderPost) {
            populateOrders((List<OrderPost>) list);
        }
        return wrapper;
    }
    private JPanel getColumnHeaderPanel() {
        JPanel columnPanel = new JPanel(new GridLayout(1, 6));
        columnPanel.setBackground(Colors.panel());
        columnPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        String[] titles = {"Brand", "Name", "Color", "Size", "Quantity", "Added"};
        for (String t : titles) {
            JLabel label = new JLabel(t);
            label.setFont(Fonts.getHeaderFont());
            label.setForeground(Colors.bg());
            columnPanel.add(label);
        }
        return columnPanel;
    }

    private void populateOrders(List<OrderPost> orders) {
        orderPanel.removeAll();
        int total = 0;
        for (OrderPost op : orders) {
            JPanel row = getOrderRowPanel(op);
            orderPanel.add(row);
            total += op.getPrice() * op.getQuantity();
        }
        totalPriceLabel.setText("Total price: " + total + " SEK");
        orderPanel.revalidate();
        orderPanel.repaint();
    }

    private JPanel getOrderRowPanel(OrderPost order) {
        JPanel row = new JPanel(new GridLayout(1, 6));
        row.setBackground(Colors.card());
        row.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        row.add(getCellLabel(order.getBrand()));
        row.add(getCellLabel(order.getName()));
        row.add(getCellLabel(order.getColor()));
        row.add(getCellLabel(String.valueOf(order.getSize())));
        row.add(getCellLabel(String.valueOf(order.getQuantity())));
        row.add(getCellLabel(order.getFormattedDate()));
        return row;
    }

    private JLabel getCellLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Fonts.getLabelFont());
        label.setForeground(Colors.text());
        return label;
    }
}
