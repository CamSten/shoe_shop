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
    private PanelDecorator decorator;
    JPanel centerPanel;
    private JTextField firstnameField;
    private JTextField surnameField;
    private JPasswordField passwordField;
    private JTextField streetField;
    private JTextField cityField;
    private JTextField emailField;
    private List<JTextField> allInputFields;
    private JLabel firstNameLabel;
    private JLabel surnameLabel;
    private JLabel passwordLabel;
    private JLabel streetAddressLabel;
    private JLabel cityLabel;
    private JLabel emailLabel;
    private final Color backgroundColor = Color.darkGray;

    public LoginPanel(ApplicationManager manager, PanelDecorator decorator){
        this.manager = manager;
        this.decorator = decorator;
        this.centerPanel = new JPanel();
        centerPanel.setBackground(backgroundColor);
        setBackground(backgroundColor);
        showLoginPanel();
    }
    private void showLoginPanel(){
        JLabel welcomeLabel = new JLabel("Welcome!");
        welcomeLabel.setFont(Fonts.getHeaderFont());
        welcomeLabel.setForeground(Colors.getHeaderColor());
        this.emailLabel = new JLabel("Enter email:");
        emailLabel.setForeground(Colors.getHeaderColor());
        emailLabel.setFont(Fonts.getInputPromptFont());
        this.emailField = new JTextField();
        emailField.setForeground(Colors.getHeaderColor());
        emailField.setFont(Fonts.getInputPromptFont());
        this.passwordLabel = new JLabel("Enter password:");
        passwordLabel.setForeground(Colors.getHeaderColor());
        passwordLabel.setFont(Fonts.getInputPromptFont());
        this.passwordField = new JPasswordField(20);
        passwordField.setForeground(Colors.getHeaderColor());

        JPanel inputFields = new JPanel(new GridLayout(2, 2));
        inputFields.setBackground(backgroundColor);
        inputFields.setBorder(
                BorderFactory.createLineBorder(Colors.getHeaderColor(), 4, true));
        inputFields.setPreferredSize(new Dimension(400, 80));
        inputFields.setMinimumSize(new Dimension(400, 80));
        inputFields.setMaximumSize(new Dimension(400, 80));
        inputFields.add(emailLabel);
        inputFields.add(emailField);
        inputFields.add(passwordLabel);
        inputFields.add(passwordField);

        JButton loginButton = new JButton("Log in");
        loginButton.setForeground(Colors.getHeaderColor());
        loginButton.setFont(Fonts.getTextFont());
        loginButton.addActionListener(_ -> {
            String emailInput = emailField.getText().trim();
            String passwordInput = passwordField.getText().trim();
            List<String> allInput = new ArrayList<>();
            allInput.add(emailInput);
            allInput.add(passwordInput);
            if (isInputComplete(allInput)) {
                try {
                    manager.validateCustomer(emailInput, passwordInput);
                } catch (SQLException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBackground(backgroundColor);
        inputPanel.add(inputFields);
        inputPanel.add(loginButton);
        JButton newUserButton = new JButton("Create account");
        newUserButton.setForeground(Colors.getHeaderColor());
        newUserButton.setFont(Fonts.getTextFont());
        newUserButton.addActionListener(_ -> createNewAccount());
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(welcomeLabel, BorderLayout.NORTH);
        centerPanel.add(inputPanel, BorderLayout.CENTER);
        centerPanel.add(newUserButton, BorderLayout.SOUTH);
        setLayout(new BorderLayout());
        setOpaque(true);
        add(centerPanel, BorderLayout.CENTER);
        repaint();
        revalidate();
    }
    private boolean isInputComplete(List<String> input){
        boolean complete = true;
        for (String s : input) {
            if (s.equals("")){
                complete = false;
                promptCompleteLogin();
            }
        }
        return complete;
    }
    public void promptCompleteLogin(){
        JOptionPane.showMessageDialog(this, "All fields must be filled in, try again.");
    }
    public void promptDifferentName(){
        for (JTextField f : allInputFields){
            f.setText("");
        }
        JOptionPane.showMessageDialog(this, "The username is already taken. Try another name.");
    }
    public void promptNoSuchUser() throws SQLException, ClassNotFoundException {
        int choice = JOptionPane.showOptionDialog(null, "The username doesn't exist. Would you like to create a new account?", "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Yes", "No"}, "Yes");
        passwordField.setText("");
        manager.assessCreateAccount(choice);
    }
    public void promptWrongPassword(){
        JOptionPane.showMessageDialog(this, "The password is incorrect.");
    }
    public void createNewAccount(){
        JLabel prompt = new JLabel("Create a new account: ");
        prompt.setFont(Fonts.getTextFont());
        prompt.setForeground(Colors.getHeaderColor());
        String email = emailField.getText();
        centerPanel.removeAll();
        JPanel inputPanel = getInputPanel();
        emailField.setText(email);
        JButton createAccount = new JButton("Save");
        createAccount.setFont(Fonts.getTextFont());
        createAccount.setForeground(Colors.getHeaderColor());
        createAccount.addActionListener(_ -> {
            String firstNameInput = firstnameField.getText().trim();
            String surnameInput = surnameField.getText().trim();
            String passwordInput = passwordField.getText().trim();
            String streetInput = streetField.getText();
            String cityInput = cityField.getText().trim();
            String emailInput = emailField.getText().toLowerCase().trim();
            List<String> userInput = new ArrayList<>();
            userInput.add(firstNameInput);
            userInput.add(surnameInput);
            userInput.add(passwordInput);
            userInput.add(streetInput);
            userInput.add(cityInput);
            userInput.add(emailInput);
            try {
                manager.Update(new Event(Event.Phase.SUBMIT, Event.Action.CREATE_ACCOUNT, Event.Subject.CUSTOMER, Event.Origin.GUI, Event.Outcome.PENDING, userInput, null));
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        inputPanel.add(createAccount);
        JButton returnButton = new JButton("Cancel");
        returnButton.setForeground(Colors.getHeaderColor());
        returnButton.setFont(Fonts.getTextFont());
        returnButton.addActionListener(_ -> {
            centerPanel.removeAll();
            showLoginPanel();
        });
        centerPanel.add(prompt, BorderLayout.NORTH);
        centerPanel.add(inputPanel, BorderLayout.CENTER);
        centerPanel.add(returnButton, BorderLayout.SOUTH);
        setEnabled(true);
        repaint();
        revalidate();
    }
    private JPanel getInputPanel(){
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        decorator.adjustInputPanel(inputPanel);
        inputPanel.setBackground(backgroundColor);
        inputPanel.setBorder(BorderFactory.createLineBorder(Colors.getBorderColor(), 5, true));
        List<JTextField> allInputFields = getAllInputFields();
        List<JLabel> allLabels = getAllInputLabels();
        for (int i = 0; i < allLabels.size(); i++){
            JPanel singleInput = new JPanel(new GridLayout(1, 2));
            singleInput.add(allLabels.get(i));
            singleInput.add(allInputFields.get(i));
            singleInput.setBorder(BorderFactory.createLineBorder(Colors.getBorderColor(), 5, true));
            inputPanel.add(singleInput);
        }
        return inputPanel;
    }
    private List<JTextField> getAllInputFields(){
        this.allInputFields = new ArrayList<>();
        this.firstnameField = new JTextField();
        this.surnameField = new JTextField();
        allInputFields.add(firstnameField);
        allInputFields.add(surnameField);
        this.streetField = new JTextField();
        allInputFields.add(streetField);
        this.cityField = new JTextField();
        allInputFields.add(cityField);
        allInputFields.add(emailField);
        allInputFields.add(passwordField);
        for (JTextField t : allInputFields){
            decorator.editInputField(t);
        }
        return allInputFields;
    }
    private List<JLabel> getAllInputLabels(){
        List<JLabel> allLabels = new ArrayList<>();
        JLabel firstNameLabel = new JLabel("Enter first name: ");
        JLabel surnameLabel = new JLabel("Enter surname: ");
        allLabels.add(firstNameLabel);
        allLabels.add(surnameLabel);
        this.streetAddressLabel = new JLabel("Enter street address: ");
        allLabels.add(streetAddressLabel);
        this.cityLabel = new JLabel("Enter city: ");
        allLabels.add(cityLabel);
        this.emailLabel = new JLabel("Enter email address: ");
        allLabels.add(emailLabel);
//        this.passwordLabel = new JLabel("Enter password: ");
        allLabels.add(passwordLabel);

        for (JLabel l : allLabels){
            decorator.adjustLabel(l);
        }
        return allLabels;
    }
}