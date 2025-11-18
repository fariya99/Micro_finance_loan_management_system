// MainAppGUI.java
// Complete Swing GUI — advanced dashboard, CSV persistence. PDF functionality removed.

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.io.*;
import java.nio.file.*;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.util.Map;
import java.util.HashMap;


public class MainAppGUI {

    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel contentPanel;

    private DataStore store;
    private ReportGenerator reports;


    // Table models
    private DefaultTableModel customerModel;
    private DefaultTableModel loanModel;
    private DefaultTableModel paymentModel;
//    Admin
private final Map<String, String> ADMIN_CREDENTIALS = new HashMap<>() {{
    put("Maryam", "M33228");
    put("Falah", "F33258");
    put("Soha", "S33163");
    put("Fariya", "F32699");
}};


    private boolean showLoginDialog() {
        // --- UI CONSTANTS for Dark Theme ---
        final Color BACKGROUND_DARK_BLUE = new Color(20, 25, 40); // Very dark, professional background
        final Color CARD_BACKGROUND = new Color(30, 40, 60);       // Slightly lighter card background
        final Color PRIMARY_BLUE = new Color(40, 150, 255);        // Vibrant Blue for accents
        final Color TEXT_COLOR = Color.WHITE;
        final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
        final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
        final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 18);

        // Create dialog
        JDialog dialog = new JDialog(frame, "Login", true);
        dialog.setUndecorated(true);
        dialog.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        dialog.setLocationRelativeTo(null);

        // Main panel with dark background
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND_DARK_BLUE);
        dialog.setContentPane(mainPanel);

        // --- Login Card Panel ---
        JPanel card = new JPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(450, 400)); // Slightly wider and taller
        card.setBackground(CARD_BACKGROUND);
        // Minimalist Border: No hard lines, just a slight shadow effect (using EmptyBorder for padding)
        card.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        card.setOpaque(true);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 0, 10, 0); // Spacing between components
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 1;

        // 1. Title (Large, Center-Aligned, High Contrast)
        JLabel title = new JLabel("Welcome Admin", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(PRIMARY_BLUE);
        gc.insets = new Insets(0, 0, 30, 0); // Extra space below the title
        card.add(title, gc);

        // Separator Line (Optional, but adds a clean touch)
        gc.gridy++;
        gc.insets = new Insets(0, 0, 20, 0);
        card.add(new JSeparator(SwingConstants.HORIZONTAL), gc);

        gc.insets = new Insets(10, 0, 5, 0); // Reset insets for form elements

        // 2. Username Field
        gc.gridy++;
        JLabel nameLabel = new JLabel("USERNAME:");
        nameLabel.setFont(LABEL_FONT);
        nameLabel.setForeground(TEXT_COLOR);
        card.add(nameLabel, gc);

        gc.gridy++;
        JTextField nameField = new JTextField(20);
        nameField.setFont(INPUT_FONT);
        // Flat, dark input field style
        nameField.setBackground(BACKGROUND_DARK_BLUE);
        nameField.setForeground(TEXT_COLOR);
        nameField.setCaretColor(TEXT_COLOR); // Cursor color
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_BLUE, 1), // Blue thin border
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.add(nameField, gc);

        // 3. Admin ID Field (Password)
        gc.gridy++;
        gc.insets = new Insets(20, 0, 5, 0); // Extra space before password label
        JLabel idLabel = new JLabel("ADMIN ID:");
        idLabel.setFont(LABEL_FONT);
        idLabel.setForeground(TEXT_COLOR);
        card.add(idLabel, gc);

        gc.gridy++;
        JPasswordField idField = new JPasswordField(20);
        idField.setFont(INPUT_FONT);
        idField.setBackground(BACKGROUND_DARK_BLUE);
        idField.setForeground(TEXT_COLOR);
        idField.setCaretColor(TEXT_COLOR);
        idField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_BLUE, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.add(idField, gc);

        // 4. Buttons
        gc.gridy++;
        gc.insets = new Insets(40, 0, 0, 0); // Large space before buttons

        JPanel buttons = new JPanel(new GridLayout(1, 2, 15, 0)); // Equal size buttons with space
        buttons.setBackground(CARD_BACKGROUND);

        JButton loginBtn = new JButton("SIGN IN");
        loginBtn.setBackground(PRIMARY_BLUE);
        loginBtn.setForeground(TEXT_COLOR);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginBtn.setFocusPainted(false);
        loginBtn.setPreferredSize(new Dimension(150, 45)); // Larger button for focus

        JButton cancelBtn = new JButton("EXIT");
        cancelBtn.setBackground(new Color(100, 100, 100)); // Darker gray for Exit
        cancelBtn.setForeground(TEXT_COLOR);
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cancelBtn.setFocusPainted(false);
        cancelBtn.setPreferredSize(new Dimension(150, 45));

        buttons.add(loginBtn);
        buttons.add(cancelBtn);
        card.add(buttons, gc);

        // Add card to center of main panel
        mainPanel.add(card, new GridBagConstraints());

        final boolean[] success = {false};

        // Login action
        loginBtn.addActionListener(e -> {
            String enteredName = nameField.getText().trim(); // User input (e.g., "maryam")
            String enteredId = new String(idField.getPassword()).trim(); // User ID/Password

            String storedId = null;

            // --- NEW LOGIC: Search the map for a case-insensitive match ---
            for (Map.Entry<String, String> entry : ADMIN_CREDENTIALS.entrySet()) {
                // Check if the key (e.g., "Maryam") matches the entered name (e.g., "maryam")
                if (entry.getKey().equalsIgnoreCase(enteredName)) {
                    storedId = entry.getValue(); // Found the correct stored ID ("M33228")
                    break; // Stop searching once found
                }
            }

            // Check if we found a username AND if the entered ID matches the stored ID
            if (storedId != null && storedId.equals(enteredId)) {
                success[0] = true;
                dialog.dispose();
            }
            // --- END NEW LOGIC ---

            else {
                JOptionPane.showMessageDialog(dialog, "Invalid Username or Admin ID.", "Authentication Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelBtn.addActionListener(e -> System.exit(0));

        dialog.setVisible(true);
        return success[0];
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {} //for window
            //try { UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); } catch (Exception ignored) {} //for mac

            MainAppGUI app = new MainAppGUI();
            if (app.showLoginDialog()) { // show login first
                app.init(); // only initialize main GUI if login successful
            }
        });
    }


    private void init() {
        store = new DataStore();
        reports = new ReportGenerator(store);

        frame = new JFrame("Microfinance Loan Management - Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 760);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        frame.add(createTopBar(), BorderLayout.NORTH);
        frame.add(createSideBar(), BorderLayout.WEST);
        frame.add(createContentPanel(), BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private JPanel createTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(20, 90, 160));
        top.setPreferredSize(new Dimension(100, 70));
        JLabel title = new JLabel("  Microfinance Loan Management");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        top.add(title, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        JLabel user = new JLabel("\uD83D\uDC64 Admin");
        user.setForeground(Color.WHITE);
        right.add(user);
        top.add(right, BorderLayout.EAST);
        return top;
    }

    private JPanel createSideBar() {
        JPanel side = new JPanel();
        side.setPreferredSize(new Dimension(260, 0));
        side.setLayout(new BorderLayout());
        side.setBackground(new Color(240, 245, 250));
        side.setBorder(new EmptyBorder(10,10,10,10));

        JPanel profile = new JPanel(new BorderLayout());
        profile.setBackground(new Color(40, 120, 200));
        profile.setPreferredSize(new Dimension(240, 120));
        profile.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        JLabel pName = new JLabel("<html><span style='color:white;font-weight:bold'>Microfinance</span><br><span style='color:#e0f2ff'>Loan System</span></html>");
        profile.add(pName, BorderLayout.CENTER);
        side.add(profile, BorderLayout.NORTH);

        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(0,1,6,6));
        buttons.setOpaque(false);

        String[] names = {"Dashboard", "Customers", "Loans", "Payments", "Reports", "Export CSV (zip)", "Exit"};
        for (String n : names) {
            JButton b = new JButton(n);
            b.setFocusPainted(false);
            b.setBackground(Color.WHITE);
            b.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220,220,220)), new EmptyBorder(8,12,8,12)));
            b.setHorizontalAlignment(SwingConstants.LEFT);
            b.addActionListener(e -> handleSidebarAction(n));
            buttons.add(b);
        }
        side.add(buttons, BorderLayout.CENTER);
        return side;
    }

    private void handleSidebarAction(String action) {
        switch (action) {
            case "Dashboard": showCard("dashboard"); break;
            case "Customers": showCard("customers"); break;
            case "Loans": showCard("loans"); break;
            case "Payments": showCard("payments"); break;
            case "Reports": showCard("reports"); break;
            case "Exit": System.exit(0); break;
        }
    }

    private JPanel createContentPanel() {
        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);

        contentPanel.add(buildDashboardPanel(), "dashboard");
        contentPanel.add(buildCustomersPanel(), "customers");
        contentPanel.add(buildLoansPanel(), "loans");
        contentPanel.add(buildPaymentsPanel(), "payments");
        contentPanel.add(buildReportsPanel(), "reports");

        cardLayout.show(contentPanel, "dashboard");
        return contentPanel;
    }

    private void showCard(String id) {
        if ("customers".equals(id)) refreshCustomerTable();
        if ("loans".equals(id)) refreshLoanTable();
        if ("payments".equals(id)) refreshPaymentTable();
        cardLayout.show(contentPanel, id);
    }

    // Dashboard panel (unchanged)
    private JPanel buildDashboardPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel heading = new JLabel("Dashboard");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 24));
        p.add(heading, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1,2,20,20));
        center.setBorder(new EmptyBorder(20,0,0,0));

        JPanel left = new JPanel(new BorderLayout());

        JPanel stats = new JPanel(new GridLayout(1,3,12,12));
        stats.add(makeStatCard("Customers", String.valueOf(store.getCustomers().size()), new Color(80,160,200)));
        stats.add(makeStatCard("Loans", String.valueOf(store.getLoans().size()), new Color(90,180,120)));
        double outstanding = store.getLoans().stream().mapToDouble(Loan::getBalance).sum();
        stats.add(makeStatCard("Outstanding", String.format("%.2f", outstanding), new Color(200,140,200)));
        left.add(stats, BorderLayout.NORTH);

        left.add(new GraphPanel(store), BorderLayout.CENTER);

        JPanel right = new JPanel(new BorderLayout());
        right.setBorder(new EmptyBorder(0,10,0,0));
        JLabel recent = new JLabel("Recent Loans");
        recent.setFont(new Font("Segoe UI", Font.BOLD, 16));
        right.add(recent, BorderLayout.NORTH);

        DefaultListModel<String> lm = new DefaultListModel<>();
        List<Loan> loans = store.getLoans();
        for (int i = Math.max(0, loans.size()-8); i < loans.size(); i++) lm.addElement(loans.get(i).toString());
        JList<String> list = new JList<>(lm);
        right.add(new JScrollPane(list), BorderLayout.CENTER);

        center.add(left);
        center.add(right);
        p.add(center, BorderLayout.CENTER);
        return p;
    }

    private JPanel makeStatCard(String label, String value, Color bg) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(bg);
        p.setBorder(new EmptyBorder(12,12,12,12));
        JLabel l = new JLabel(label);
        l.setForeground(Color.WHITE);
        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.BOLD, 18));
        v.setForeground(Color.WHITE);
        p.add(l, BorderLayout.NORTH);
        p.add(v, BorderLayout.CENTER);
        return p;
    }

    private static class GraphPanel extends JPanel {
        private DataStore store;
        public GraphPanel(DataStore s) {
            this.store = s;
            setPreferredSize(new Dimension(0,260));
            setBackground(Color.WHITE);
            setBorder(new EmptyBorder(12,12,12,12));
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int w = getWidth(), h = getHeight();
            g.setColor(new Color(245,245,245));
            g.fillRect(0,0,w,h);
            int p=0,b=0,e=0;
            for (Loan l : store.getLoans()) {
                switch (l.getLoanType()) {
                    case "PERSONAL": p++; break;
                    case "BUSINESS": b++; break;
                    case "EDUCATION": e++; break;
                }
            }
            int total = Math.max(1, p+b+e);
            int barW = Math.max(20, w/12);
            int baseline = h - 50;
            int gap = (w - 3*barW) / 6;
            int x = gap;

            int ph = (int)((double)p/total*(h-120));
            g.setColor(new Color(80,160,200));
            g.fillRect(x, baseline-ph, barW, ph);
            g.setColor(Color.BLACK); g.drawString("Personal ("+p+")", x, baseline+20);
            x += barW + gap;

            int bh = (int)((double)b/total*(h-120));
            g.setColor(new Color(90,180,120));
            g.fillRect(x, baseline-bh, barW, bh);
            g.setColor(Color.BLACK); g.drawString("Business ("+b+")", x, baseline+20);
            x += barW + gap;

            int eh = (int)((double)e/total*(h-120));
            g.setColor(new Color(200,140,200));
            g.fillRect(x, baseline-eh, barW, eh);
            g.setColor(Color.BLACK); g.drawString("Education ("+e+")", x, baseline+20);
        }
    }
    // -------- Customers Panel --------
    private JPanel buildCustomersPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(12,15,12,15));
        JLabel heading = new JLabel("Customers");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        p.add(heading, BorderLayout.NORTH);

        // Table setup
        String[] cols = {"ID", "Name", "CNIC", "Phone", "Email", "Address", "Actions"};
        customerModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only Actions column editable
            }
        };
        JTable table = new JTable(customerModel);
        table.setRowHeight(30); // Ensure buttons fit
        table.getColumn("Actions").setPreferredWidth(180);

        // Actions column renderer/editor
        table.getColumn("Actions").setCellRenderer(new ActionCellRenderer());
        table.getColumn("Actions").setCellEditor(new ActionCellEditor(table));

        p.add(new JScrollPane(table), BorderLayout.CENTER);

        // Form to add customer (unchanged)
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(0,12,0,0));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6,6,6,6);
        gc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameF = new JTextField(15);
        JTextField cnicF = new JTextField(15);
        JTextField phoneF = new JTextField(15);
        JTextField emailF = new JTextField(15);
        JTextField addrF = new JTextField(15);
        JButton addBtn = new JButton("Add Customer");
        addBtn.setBackground(new Color(70,160,120));
        addBtn.setForeground(Color.WHITE);

        gc.gridx=0; gc.gridy=0; form.add(new JLabel("Name:"), gc); gc.gridx=1; form.add(nameF, gc);
        gc.gridx=0; gc.gridy=1; form.add(new JLabel("CNIC:"), gc); gc.gridx=1; form.add(cnicF, gc);
        gc.gridx=0; gc.gridy=2; form.add(new JLabel("Phone:"), gc); gc.gridx=1; form.add(phoneF, gc);
        gc.gridx=0; gc.gridy=3; form.add(new JLabel("Email:"), gc); gc.gridx=1; form.add(emailF, gc);
        gc.gridx=0; gc.gridy=4; form.add(new JLabel("Address:"), gc); gc.gridx=1; form.add(addrF, gc);
        gc.gridx=1; gc.gridy=5; form.add(addBtn, gc);

        addBtn.addActionListener(e -> {
            String name = nameF.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Name required", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String id = generateId("C");
            Customer cst = new Customer(id, name, cnicF.getText().trim(), emailF.getText().trim(), addrF.getText().trim(), phoneF.getText().trim());
            if(store.addCustomer(cst)) {
                refreshCustomerTable();
                nameF.setText(""); cnicF.setText(""); phoneF.setText(""); emailF.setText(""); addrF.setText("");
                JOptionPane.showMessageDialog(frame, "Customer added: " + id, "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid data! Check CNIC, Phone, or Email.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        p.add(form, BorderLayout.EAST);
        refreshCustomerTable();
        return p;
    }

// -------------------- Renderer & Editor --------------------
class ActionCellRenderer extends JPanel implements TableCellRenderer {
    private JButton editBtn = new JButton("Edit");
    private JButton delBtn = new JButton("Delete");

    public ActionCellRenderer() {
        setLayout(new GridLayout(1, 2, 5, 0)); // Grid ensures both buttons are visible
        setOpaque(true);

        editBtn.setMargin(new Insets(2, 2, 2, 2));
        delBtn.setMargin(new Insets(2, 2, 2, 2));

        editBtn.setBackground(new Color(107, 158, 188));
        editBtn.setForeground(Color.WHITE);


        delBtn.setBackground(new Color(232, 76, 76));
        delBtn.setForeground(Color.WHITE);


        add(editBtn);
        add(delBtn);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        if(isSelected) setBackground(table.getSelectionBackground());
        else setBackground(table.getBackground());
        return this;
    }
}

    class ActionCellEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel = new JPanel();
        private JButton editBtn = new JButton("Edit");
        private JButton delBtn = new JButton("Delete");
        private int currentRow;

        public ActionCellEditor(JTable table) {
            panel.setLayout(new GridLayout(1, 2, 5, 0));
            panel.setOpaque(true);

            editBtn.setMargin(new Insets(2, 2, 2, 2));
            delBtn.setMargin(new Insets(2, 2, 2, 2));

            editBtn.setBackground(new Color(107, 158, 188));
            editBtn.setForeground(Color.WHITE);

            delBtn.setBackground(new Color(232, 76, 76));
            delBtn.setForeground(Color.WHITE);

            panel.add(editBtn);
            panel.add(delBtn);

            editBtn.addActionListener(e -> {
                editCustomerRow(currentRow);
                fireEditingStopped();
            });
            delBtn.addActionListener(e -> {
                deleteCustomerRow(currentRow);
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = row;
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }


    private void refreshCustomerTable() {
        customerModel.setRowCount(0);
        for(Customer c : store.getCustomers()) {
            customerModel.addRow(new Object[]{
                    c.getCustomerId(), c.getName(), c.getCnic(), c.getPhoneNumber(), c.getEmail(), c.getAddress(), ""
            });
        }
    }

    private void editCustomerRow(int row) {
        if(row<0 || row>=customerModel.getRowCount()) return;
        String id = (String) customerModel.getValueAt(row, 0);
        Customer c = store.findCustomerById(id);
        if(c==null) return;

        JTextField nameF = new JTextField(c.getName());
        JTextField cnicF = new JTextField(c.getCnic());
        JTextField phoneF = new JTextField(c.getPhoneNumber());
        JTextField emailF = new JTextField(c.getEmail());
        JTextField addrF = new JTextField(c.getAddress());

        JPanel panel = new JPanel(new GridLayout(0,2));
        panel.add(new JLabel("Name:")); panel.add(nameF);
        panel.add(new JLabel("CNIC:")); panel.add(cnicF);
        panel.add(new JLabel("Phone:")); panel.add(phoneF);
        panel.add(new JLabel("Email:")); panel.add(emailF);
        panel.add(new JLabel("Address:")); panel.add(addrF);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Edit Customer " + id, JOptionPane.OK_CANCEL_OPTION);
        if(result == JOptionPane.OK_OPTION) {
            if(store.editCustomer(id, nameF.getText(), cnicF.getText(), emailF.getText(), addrF.getText(), phoneF.getText())) {
                refreshCustomerTable();
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid data! Check CNIC, Phone, or Email.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteCustomerRow(int row) {
        if(row<0 || row>=customerModel.getRowCount()) return;
        String id = (String) customerModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(frame, "Delete customer " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if(confirm == JOptionPane.YES_OPTION) {
            if(store.deleteCustomer(id)) {
                refreshCustomerTable();
            } else {
                JOptionPane.showMessageDialog(frame, "Could not delete customer!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }



    private JPanel buildLoansPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(12,12,12,12));
        JLabel heading = new JLabel("Loans");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        p.add(heading, BorderLayout.NORTH);

        String[] cols = {"Loan ID","Type","Customer ID","Principal","Interest","Duration(m)","Balance","Issue Date","Due Date","Status"};
        loanModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(loanModel);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(0,12,0,0));
        GridBagConstraints gc = new GridBagConstraints(); gc.insets = new Insets(6,6,6,6); gc.fill = GridBagConstraints.HORIZONTAL;

        JTextField custIdF = new JTextField(12);
        JTextField amountF = new JTextField(12);
        JTextField durF = new JTextField(12);
        JComboBox<String> typeBox = new JComboBox<>(new String[] {"PERSONAL","BUSINESS","EDUCATION"});
        JButton issueBtn = new JButton("Issue Loan");
        issueBtn.setBackground(new Color(70,130,180)); issueBtn.setForeground(Color.WHITE);

        gc.gridx=0; gc.gridy=0; form.add(new JLabel("Customer ID:"), gc); gc.gridx=1; form.add(custIdF, gc);
        gc.gridx=0; gc.gridy=1; form.add(new JLabel("Amount:"), gc); gc.gridx=1; form.add(amountF, gc);
        gc.gridx=0; gc.gridy=2; form.add(new JLabel("Duration (months):"), gc); gc.gridx=1; form.add(durF, gc);
        gc.gridx=0; gc.gridy=3; form.add(new JLabel("Type:"), gc); gc.gridx=1; form.add(typeBox, gc);
        gc.gridx=1; gc.gridy=4; form.add(issueBtn, gc);

        issueBtn.addActionListener(e -> {
            String cid = custIdF.getText().trim();
            Customer c = store.findCustomerById(cid);
            if (c == null) { JOptionPane.showMessageDialog(frame, "Customer not found", "Validation", JOptionPane.WARNING_MESSAGE); return; }
            double amt; int months;
            try { amt = Double.parseDouble(amountF.getText().trim()); } catch (Exception ex) { JOptionPane.showMessageDialog(frame, "Invalid amount", "Validation", JOptionPane.WARNING_MESSAGE); return; }
            try { months = Integer.parseInt(durF.getText().trim()); } catch (Exception ex) { JOptionPane.showMessageDialog(frame, "Invalid months", "Validation", JOptionPane.WARNING_MESSAGE); return; }
            String type = (String) typeBox.getSelectedItem();
            String id = generateId("L");
            Loan loan;
            if ("PERSONAL".equals(type)) loan = new PersonalLoan(id, cid, amt, months, LocalDate.now());
            else if ("BUSINESS".equals(type)) loan = new BusinessLoan(id, cid, amt, months, LocalDate.now());
            else loan = new EducationLoan(id, cid, amt, months, LocalDate.now());
            store.addLoan(loan);
            refreshLoanTable();
            JOptionPane.showMessageDialog(frame, "Loan issued: " + id + "\nTotal payable (approx): " + String.format("%.2f", loan.getBalance()), "Success", JOptionPane.INFORMATION_MESSAGE);
            custIdF.setText(""); amountF.setText(""); durF.setText("");
        });

        p.add(form, BorderLayout.EAST);
        refreshLoanTable();
        return p;
    }

    private void refreshLoanTable() {
        loanModel.setRowCount(0);
        for (Loan l : store.getLoans()) {
            double interest = l.calculateTotalPayable() - l.getPrincipal();
            loanModel.addRow(new Object[] {
                    l.getLoanId(), l.getLoanType(), l.getCustomerId(),
                    String.format("%.2f", l.getPrincipal()),
                    String.format("%.2f", interest),
                    l.getDurationMonths(),
                    String.format("%.2f", l.getBalance()),
                    l.getIssueDate(),
                    l.getDueDate(),
                    l.getStatus()
            });
        }
    }

    private JPanel buildPaymentsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(12,12,12,12));
        JLabel heading = new JLabel("Payments");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        p.add(heading, BorderLayout.NORTH);

        String[] cols = {"Payment ID","Loan ID","Amount","Date"};
        paymentModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(paymentModel);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(0,12,0,0));
        GridBagConstraints gc = new GridBagConstraints(); gc.insets = new Insets(6,6,6,6); gc.fill = GridBagConstraints.HORIZONTAL;

        JTextField loanField = new JTextField(12);
        JTextField amtField = new JTextField(12);
        JButton recBtn = new JButton("Record Payment");
        recBtn.setBackground(new Color(255,140,0)); recBtn.setForeground(Color.WHITE);

        gc.gridx=0; gc.gridy=0; form.add(new JLabel("Loan ID:"), gc); gc.gridx=1; form.add(loanField, gc);
        gc.gridx=0; gc.gridy=1; form.add(new JLabel("Amount:"), gc); gc.gridx=1; form.add(amtField, gc);
        gc.gridx=1; gc.gridy=2; form.add(recBtn, gc);

        recBtn.addActionListener(e -> {
            String loanId = loanField.getText().trim();
            Loan loan = store.findLoanById(loanId);
            if (loan == null) { JOptionPane.showMessageDialog(frame, "Loan not found.", "Validation", JOptionPane.WARNING_MESSAGE); return; }
            double amt;
            try { amt = Double.parseDouble(amtField.getText().trim()); } catch (Exception ex) { JOptionPane.showMessageDialog(frame, "Invalid amount.", "Validation", JOptionPane.WARNING_MESSAGE); return; }
            if (amt <= 0) { JOptionPane.showMessageDialog(frame, "Amount must be positive.", "Validation", JOptionPane.WARNING_MESSAGE); return; }
            String pid = generateId("P");
            Payment pm = new Payment(pid, loanId, amt, LocalDate.now());
            store.recordPaymentAndUpdateLoan(pm);
            refreshPaymentTable();
            refreshLoanTable();
            JOptionPane.showMessageDialog(frame, "Payment recorded: " + pid, "Success", JOptionPane.INFORMATION_MESSAGE);
            loanField.setText(""); amtField.setText("");
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField viewLoan = new JTextField(12);
        viewLoan.setToolTipText("Loan ID to view payments");
        JButton viewBtn = new JButton("View Payments");
        JButton refreshBtn = new JButton("Refresh All");

        viewBtn.addActionListener(e -> {
            String lid = viewLoan.getText().trim();
            if (lid.isEmpty()) { JOptionPane.showMessageDialog(frame, "Enter loan ID.", "Validation", JOptionPane.WARNING_MESSAGE); return; }
            List<Payment> list = store.getPaymentsForLoan(lid);
            paymentModel.setRowCount(0);
            for (Payment pay : list) paymentModel.addRow(new Object[] { pay.getPaymentId(), pay.getLoanId(), String.format("%.2f", pay.getAmountPaid()), pay.getDate() });
        });
        refreshBtn.addActionListener(e -> { refreshPaymentTable(); refreshLoanTable(); });

        bottom.add(viewLoan); bottom.add(viewBtn); bottom.add(refreshBtn);

        p.add(form, BorderLayout.EAST);
        p.add(bottom, BorderLayout.SOUTH);
        refreshPaymentTable();
        return p;
    }

    private void refreshPaymentTable() {
        paymentModel.setRowCount(0);
        for (Payment p : store.getPayments()) {
            paymentModel.addRow(new Object[] { p.getPaymentId(), p.getLoanId(), String.format("%.2f", p.getAmountPaid()), p.getDate() });
        }
    }

    private JPanel buildReportsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(12,12,12,12));
        JLabel heading = new JLabel("Reports");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        p.add(heading, BorderLayout.NORTH);

        JTextArea out = new JTextArea();
        out.setEditable(false);
        out.setLineWrap(true);
        out.setWrapStyleWord(true);
        JScrollPane sp = new JScrollPane(out);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton loanSummaryBtn = new JButton("Loan Summary (Preview)");
        JButton overdueBtn = new JButton("Overdue Loans (Preview)");
        JTextField custField = new JTextField(12);
        JButton custBtn = new JButton("Customer Report (Preview)");

        loanSummaryBtn.addActionListener(e -> out.setText(reports.loanSummaryText()));
        overdueBtn.addActionListener(e -> out.setText(reports.overdueText()));
        custBtn.addActionListener(e -> {
            String id = custField.getText().trim();
            if (id.isEmpty()) { JOptionPane.showMessageDialog(frame, "Customer ID required.", "Validation", JOptionPane.WARNING_MESSAGE); return; }
            out.setText(reports.customerReportText(id));
        });

        controls.add(loanSummaryBtn);
        controls.add(overdueBtn);
        controls.add(new JLabel("Customer ID:"));
        controls.add(custField);
        controls.add(custBtn);

        p.add(controls, BorderLayout.NORTH);
        p.add(sp, BorderLayout.CENTER);
        return p;
    }


    private String generateId(String prefix) {
        return prefix + UUID.randomUUID().toString().replace("-", "").substring(0,8).toUpperCase();
    }


}
