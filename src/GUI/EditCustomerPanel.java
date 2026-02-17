package GUI;

import javax.swing.*;
import Control.Event;
import Model.DataHandling.Customer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class EditCustomerPanel extends JPanel {
    private MainFrame mainFrame;
    private PanelDecorator decorator;
    private Customer customer;
    private JPanel centerPanel;
    private JPanel buttonPanel;
    private JTextField firstnameField;
    private JTextField surnameField;
    private JTextField streetField;
    private JTextField cityField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton saveButton;
    private ActionListener saver = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            submitActions();
        }
    };
    private ActionListener returner = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            mainFrame.showMenuPanel();
        }
    };

    public EditCustomerPanel(MainFrame mainFrame, PanelDecorator decorator, Customer customer) {
        this.mainFrame = mainFrame;
        this.decorator = decorator;
        this.customer = customer;
        this.centerPanel = new JPanel(new BorderLayout());
        setBackground(Colors.panel());
        centerPanel.setBackground(Colors.panel());
        getEditPanel();
        add(centerPanel);
        revalidate();
        repaint();
    }
    private void getEditPanel(){
        System.out.println("getEditPanel is reached in EditCustomerPanel");
        getPanelContents();
        List<JTextField> fields = List.of(firstnameField, surnameField, streetField, cityField, emailField, passwordField);
        for (JTextField f : fields) {
            decorator.adjustTextField(f);
        }
        List<String> labelsText = List.of("First name:", "Surname:", "Street:", "City:", "Email:", "Password:");
        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(Colors.bg());
        JPanel labelCol = new JPanel(new GridLayout(labelsText.size(), 1, 5, 5));
        JPanel inputCol = new JPanel(new GridLayout(fields.size(), 1, 5, 5));
        labelCol.setBackground(Colors.panel());
        inputCol.setBackground(Colors.panel());
        for (int i = 0; i < labelsText.size(); i++){
            JLabel label = new JLabel(labelsText.get(i));
            decorator.adjustLabel(label);
            labelCol.add(label);
            inputCol.add(fields.get(i));
        }
        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.X_AXIS));
        decorator.adjustWrapperPanel(wrapperPanel);
        wrapperPanel.add(labelCol);
        wrapperPanel.add(inputCol);
        inputPanel.add(wrapperPanel);
        inputPanel.add(wrapperPanel);
        centerPanel.add(inputPanel, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void getPanelContents(){
        this.firstnameField = new JTextField(customer.getFirstName());
        this.surnameField = new JTextField(customer.getSurname());
        this.streetField = new JTextField(customer.getStreetAddress());
        this.cityField = new JTextField(customer.getCity());
        this.emailField = new JTextField(customer.getEmail());
        this.passwordField = new JPasswordField(customer.getPassword());
        mainFrame.adjustHeaderAndFooter("Account details:", false, false, false);

        this.buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Colors.bg());
        this.saveButton = new JButton("Submit changes");
        decorator.adjustButton(saveButton);
        saveButton.addActionListener(saver);
        JButton cancelButton = new JButton("Cancel");
        decorator.adjustButton(cancelButton);
        cancelButton.addActionListener(_ -> mainFrame.showLoginPanel());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);
        revalidate();
        repaint();
    }
    private void submitActions(){
        System.out.println("submitActions is reached in EditCustomerPanel");
        String firstName = firstnameField.getText().trim();
        String surname = surnameField.getText().trim();
        String street = streetField.getText().trim();
        String city = cityField.getText().trim();
        String email = emailField.getText().trim();
        String pass = String.valueOf(passwordField.getPassword()).trim();
        Customer newCustomerInfo = new Customer(mainFrame.getCustomerId(),firstName, surname, street, city, email, pass);
        try {
            mainFrame.Update(new Event(Event.Phase.SUBMIT, Event.Action.EDIT, Event.Subject.CUSTOMER, Event.Origin.GUI, Event.Outcome.PENDING, newCustomerInfo, null));
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
    void showConfirmation(){
        saveButton.removeActionListener(saver);
        saveButton.addActionListener(returner);
        saveButton.setText("Return to menu");
        System.out.println("- - - - getEmptyStockMessage is reached in CartPanel");
        JPanel wrapper = new JPanel(new GridLayout(2, 1));
        JLabel msg = new JLabel("Your details have been updated.");
        decorator.adjustBrandLabel(msg);
        wrapper.add(msg);
        decorator.adjustWrapperPanel(wrapper);
        JPanel stockMsgPanel = new JPanel();
        stockMsgPanel.add(wrapper);
        stockMsgPanel.setOpaque(false);
        centerPanel.add(stockMsgPanel, BorderLayout.NORTH);
        repaint();
        revalidate();
    }
}
