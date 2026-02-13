
package GUI;
import Control.ApplicationManager;
import Control.Event;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoginPanel extends JPanel {
    private ApplicationManager manager;
    private MainFrame mainFrame;
    private PanelDecorator decorator;
    private JPanel centerPanel;

    private JTextField firstnameField;
    private JTextField surnameField;
    private JTextField streetField;
    private JTextField cityField;
    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginPanel(ApplicationManager manager, MainFrame mainFrame, PanelDecorator decorator){
        this.manager = manager;
        this.mainFrame = mainFrame;
        this.decorator = decorator;
        this.centerPanel = new JPanel(new BorderLayout());
        setLayout(new BorderLayout());
        setBackground(Colors.bg());
        add(centerPanel, BorderLayout.CENTER);
        showLoginPanel();
    }

    public void showLoginPanel() {
        centerPanel.removeAll();
//        JPanel panel = new JPanel(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Colors.panel());

        JPanel inputCol = new JPanel(new GridLayout(2, 1));
//        decorator.adjustInputPanel(inputCol);
        inputCol.setBackground(Colors.panel());
//        JPanel labelWrapperMail = new JPanel();
        JLabel emailLabel = new JLabel("  Enter email:");
        decorator.adjustLabel(emailLabel);
        this.emailField = new JTextField();
        decorator.adjustTextField(emailField);
        inputCol.add(emailField);
        //inputCol.setBorder(BorderFactory.createLineBorder(Colors.buttonHover(), 4, true));


        JPanel labelCol = new JPanel(new GridLayout(2,1));
//        decorator.adjustInputPanel(labelCol);
        labelCol.setBackground(Colors.bg());
//        JPanel labelWrapperPass = new JPanel();
        JLabel passwordLabel = new JLabel("  Enter password:");
//        labelWrapperPass.add(passwordLabel);
        decorator.adjustLabel(passwordLabel);
        passwordField = new JPasswordField();
        decorator.adjustTextField(passwordField);

//        labelCol.add(Box.createVerticalGlue());
        labelCol.add(emailLabel);
        labelCol.add(Box.createVerticalGlue());
        labelCol.add(passwordLabel);
//        labelCol.add(Box.createVerticalGlue());

//        inputCol.add(Box.createVerticalGlue());
        inputCol.add(emailField);
        inputCol.add(Box.createVerticalGlue());
        inputCol.add(passwordField);
//        inputCol.add(Box.createVerticalGlue());
        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.X_AXIS));
        decorator.adjustWrapperPanel(wrapperPanel);
        wrapperPanel.add(labelCol);
        wrapperPanel.add(Box.createHorizontalGlue());
        wrapperPanel.add(inputCol);


        JPanel buttonWrapperPanel = new JPanel();
        decorator.adjustWrapperPanel(buttonWrapperPanel);
//        JPanel buttonPanel = new JPanel();
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1));
        decorator.adjustWrapperPanel(buttonPanel);
//        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(Colors.bg());
        JButton loginButton = new JButton("Log in");
        decorator.adjustButton(loginButton);
        loginButton.addActionListener(_ -> handleLogin());
        JButton newUserButton = new JButton("Create account");
        decorator.adjustButton(newUserButton);
        newUserButton.addActionListener(_ -> showCreateAccountPanel());
        buttonPanel.add(loginButton);
        buttonPanel.add(newUserButton);
        buttonWrapperPanel.add(buttonPanel);

        panel.add(wrapperPanel, BorderLayout.CENTER);
        panel.add(buttonWrapperPanel, BorderLayout.SOUTH);

        centerPanel.add(panel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void handleLogin() {
        String emailInput = emailField.getText().trim();
        String passwordInput = new String(passwordField.getPassword()).trim();
        if (emailInput.isEmpty() || passwordInput.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled in.");
            return;
        }
        try {
            manager.validateCustomer(emailInput, passwordInput);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void showCreateAccountPanel() {
        centerPanel.removeAll();
        firstnameField = new JTextField();
        surnameField = new JTextField();
        streetField = new JTextField();
        cityField = new JTextField();
        passwordField = new JPasswordField();
        mainFrame.adjustHeaderAndFooter("Fill in your details:", false, false, false);

        List<JTextField> fields = List.of(firstnameField, surnameField, streetField, cityField, emailField, passwordField);
        for (JTextField f : fields) decorator.adjustTextField(f);

        List<String> labelsText = List.of("First name:", "Surname:", "Street:", "City:", "Email:", "Password:");
        JPanel inputPanel = new JPanel();
//        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBackground(Colors.bg());
        JPanel labelCol = new JPanel(new GridLayout(labelsText.size(), 1, 5, 5));
//        labelCol.setLayout(new BoxLayout(labelCol, BoxLayout.Y_AXIS));
        JPanel inputCol = new JPanel(new GridLayout(fields.size(), 1, 5, 5));
//        inputCol.setLayout(new BoxLayout(labelCol, BoxLayout.Y_AXIS));
        labelCol.setBackground(Colors.panel());
        inputCol.setBackground(Colors.panel());

        for (int i = 0; i < labelsText.size(); i++){
//            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
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

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Colors.bg());
        JButton saveButton = new JButton("Save");
        decorator.adjustButton(saveButton);
        saveButton.addActionListener(_ -> handleCreateAccount());
        JButton cancelButton = new JButton("Cancel");
        decorator.adjustButton(cancelButton);
        cancelButton.addActionListener(_ -> showLoginPanel());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        centerPanel.add(inputPanel, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);
        revalidate();
        repaint();
    }

    private void handleCreateAccount(){
        List<String> userInput = new ArrayList<>();
        userInput.add(firstnameField.getText().trim());
        userInput.add(surnameField.getText().trim());
        userInput.add(new String(passwordField.getPassword()).trim());
        userInput.add(streetField.getText().trim());
        userInput.add(cityField.getText().trim());
        userInput.add(emailField.getText().trim().toLowerCase());

        try {
            manager.Update(new Event(
                    Event.Phase.SUBMIT,
                    Event.Action.CREATE_ACCOUNT,
                    Event.Subject.CUSTOMER,
                    Event.Origin.GUI,
                    Event.Outcome.PENDING,
                    userInput,
                    null
            ));
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void promptCompleteLogin() {
        JOptionPane.showMessageDialog(this, "All fields must be filled in.");
    }

    public void promptNoSuchUser() throws SQLException, ClassNotFoundException {
        int choice = JOptionPane.showOptionDialog(
                this,
                "The user name does not exist. Would you like to create a new account?",
                "Create account",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new Object[]{"Yes", "No"},
                "Yes"
        );
        passwordField.setText("");
        manager.assessCreateAccount(choice);
    }
    public void promptWrongPassword() {
        JOptionPane.showMessageDialog(this, "The password that you have entered is incorrect");
    }
}