package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Optional;

// DEPENDENCIES
import controller.CustomerController;
import model.Customer;

// NOTE: This file must be saved as 'CustomerGUI.java' in a 'view' directory.
public class CustomerGUI extends JFrame {

    // --- Controller Instance ---
    private CustomerController controller = new CustomerController();

    // --- GUI Fields ---
    private JTextField idField, nameField, emailField, phoneField, cnicField, addressField, searchIdField;
    private JTextArea displayArea;

    // --- Premium Theme Colors ---
    private static final Color PRIMARY_BG = new Color(10, 10, 10); // Very Deep Black
    private static final Color SECONDARY_BG = new Color(30, 30, 30); // Dark Gray for input/display fields
    private static final Color TEXT_COLOR = Color.WHITE; // Pure White text
    private static final Color ACCENT_COLOR = new Color(255, 215, 0); // Gold/Amber Yellow
    private static final Color BORDER_COLOR = new Color(80, 80, 80); // Soft border color

    // --- Fonts ---
    private static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 18);
    private static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font DATA_FONT = new Font("Monospaced", Font.PLAIN, 12);

    public CustomerGUI() {
        // --- Frame Setup ---
        setTitle("🏆 Premium Customer Dashboard");
        setSize(750, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(PRIMARY_BG);
        setLayout(new BorderLayout(20, 20)); 
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(15, 15, 15, 15));

        customizeUIDefaults();

        // ---------------------- HEADER ----------------------
        JLabel headerLabel = new JLabel("PREMIUM CUSTOMER REGISTRY", SwingConstants.CENTER);
        headerLabel.setFont(HEADER_FONT);
        headerLabel.setForeground(ACCENT_COLOR);
        add(headerLabel, BorderLayout.NORTH);

        // ---------------------- CONTENT PANEL (INPUT & ACTIONS) ----------------------
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(PRIMARY_BG);

        // 1. Input Panel
        JPanel inputPanel = createSectionPanel();
        inputPanel.setLayout(new GridLayout(6, 2, 20, 10)); // Wider gaps
        setupInputRow(inputPanel, "Customer ID", idField = new JTextField());
        setupInputRow(inputPanel, "Name", nameField = new JTextField());
        setupInputRow(inputPanel, "Email", emailField = new JTextField());
        setupInputRow(inputPanel, "Phone", phoneField = new JTextField());
        setupInputRow(inputPanel, "CNIC", cnicField = new JTextField());
        setupInputRow(inputPanel, "Address", addressField = new JTextField());
        
        contentPanel.add(inputPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer

        // 2. Action and Search Panel
        JPanel utilityPanel = new JPanel(new BorderLayout(30, 0));
        utilityPanel.setBackground(PRIMARY_BG);
        
        // Action Buttons Group
        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        actionButtonPanel.setBackground(PRIMARY_BG);
        JButton addBtn = createModernButton("ADD CUSTOMER");
        JButton showBtn = createModernButton("SHOW ALL");
        actionButtonPanel.add(addBtn);
        actionButtonPanel.add(showBtn);
        utilityPanel.add(actionButtonPanel, BorderLayout.WEST);

        // Search Group
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setBackground(PRIMARY_BG);
        searchIdField = createModernTextField();
        searchIdField.setPreferredSize(new Dimension(150, 30));
        searchPanel.add(createModernLabel("SEARCH ID:"));
        searchPanel.add(searchIdField);
        JButton searchBtn = createModernButton("SEARCH");
        searchPanel.add(searchBtn);
        utilityPanel.add(searchPanel, BorderLayout.EAST);
        
        contentPanel.add(utilityPanel);

        add(contentPanel, BorderLayout.CENTER);

        // ---------------------- DISPLAY AREA (SOUTH) ----------------------
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setBackground(SECONDARY_BG);
        displayArea.setForeground(ACCENT_COLOR); // Gold text in display area
        displayArea.setCaretColor(TEXT_COLOR);
        displayArea.setFont(DATA_FONT);
        displayArea.setBorder(new EmptyBorder(10, 10, 10, 10)); 

        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 1)); 
        scrollPane.setPreferredSize(new Dimension(700, 250));
        add(scrollPane, BorderLayout.SOUTH);

        // ---------------------- BUTTON ACTIONS ----------------------
        addBtn.addActionListener(e -> handleAddCustomer());
        showBtn.addActionListener(e -> handleShowAllCustomers());
        searchBtn.addActionListener(e -> handleSearchCustomer());
        searchIdField.addActionListener(e -> handleSearchCustomer()); 

        // Initial setup prompt
        displayArea.append(">> SYSTEM READY. Use the fields above to manage customers.");
    }
    
    // --- Action Handlers ---
    private void handleAddCustomer() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();

        if (id.isEmpty() || name.isEmpty()) {
             JOptionPane.showMessageDialog(this, "Customer ID and Name are required fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
             return;
        }

        Customer c = new Customer(
            id, 
            name, 
            emailField.getText().trim(), 
            phoneField.getText().trim(), 
            cnicField.getText().trim(), 
            addressField.getText().trim()
        );
        controller.addCustomer(c);

        JOptionPane.showMessageDialog(this, "Customer **" + name + "** successfully added!", "Success", JOptionPane.INFORMATION_MESSAGE);
        clearFields();
        handleShowAllCustomers();
    }

    private void handleShowAllCustomers() {
        displayArea.setText("");
        List<Customer> customers = controller.getCustomers();
        if (customers.isEmpty()) {
            displayArea.append(">> NO CUSTOMER RECORDS FOUND.");
        } else {
            displayArea.append(">> DISPLAYING ALL CUSTOMERS (" + customers.size() + ")\n\n");
            for (Customer c : customers) {
                displayArea.append(">> " + c.toString() + "\n");
                displayArea.append("--------------------------------------------------------------------------------------------------\n");
            }
        }
    }
    
    private void handleSearchCustomer() {
        String searchId = searchIdField.getText().trim();
        displayArea.setText("");
        
        if (searchId.isEmpty()) {
             displayArea.append(">> ERROR: Please enter a Customer ID to search.");
             return;
        }
        
        Optional<Customer> result = controller.findCustomerById(searchId);
        
        if (result.isPresent()) {
            displayArea.append(">> SEARCH RESULT FOR ID: " + searchId + "\n\n");
            displayArea.append(">> FOUND: " + result.get().toString() + "\n");
        } else {
            displayArea.append(">> ERROR: Customer with ID '" + searchId + "' not found.");
        }
        searchIdField.setText("");
    }


    // --- Styling Helpers (No changes from previous response, kept for context) ---
    private void customizeUIDefaults() {
        UIManager.put("Label.foreground", TEXT_COLOR);
        UIManager.put("Button.font", new Font("SansSerif", Font.BOLD, 12));
        UIManager.put("OptionPane.background", SECONDARY_BG);
        UIManager.put("OptionPane.messageForeground", TEXT_COLOR);
        UIManager.put("Panel.background", PRIMARY_BG);
    }
    
    private JPanel createSectionPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(PRIMARY_BG);
        return panel;
    }
    
    private JLabel createModernLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(ACCENT_COLOR); 
        label.setFont(LABEL_FONT);
        return label;
    }
    
    private JTextField createModernTextField() {
        JTextField field = new JTextField();
        field.setBackground(SECONDARY_BG); 
        field.setForeground(TEXT_COLOR); 
        field.setCaretColor(ACCENT_COLOR); 
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(5, 5, 5, 5) 
        ));
        return field;
    }

    private void setupInputRow(JPanel panel, String labelText, JTextField field) {
        JLabel label = createModernLabel(labelText);
        
        field.setBackground(SECONDARY_BG); 
        field.setForeground(TEXT_COLOR); 
        field.setCaretColor(ACCENT_COLOR); 
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(5, 5, 5, 5) 
        ));

        panel.add(label);
        panel.add(field);
    }

    private JButton createModernButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(ACCENT_COLOR); 
        btn.setForeground(PRIMARY_BG); 
        btn.setFocusPainted(false);
        btn.setBorderPainted(false); 
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); 
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(ACCENT_COLOR.darker()); 
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(ACCENT_COLOR);
            }
        });
        return btn;
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        cnicField.setText("");
        addressField.setText("");
    }

    // Main function to run the GUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CustomerGUI().setVisible(true);
        });
    }
}