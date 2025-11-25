// MainAppGUI.java
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.io.*;
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

    private DefaultTableModel customerModel;
    private DefaultTableModel loanModel;
    private DefaultTableModel paymentModel;

    private final Map<String, String> ADMIN_CREDENTIALS = new HashMap<>() {{
        put("Maryam", "M33228");
        put("Falah", "F33258");
        put("Soha", "S33163");
        put("Fariya", "F32699");
    }};


    private final Color GRADIENT_PURPLE = new Color(147, 51, 234);
    private final Color GRADIENT_BLUE = new Color(59, 130, 246);
    private final Color GRADIENT_CYAN = new Color(34, 211, 238);
    private final Color CARD_PURPLE = new Color(168, 85, 247);

    private boolean showLoginDialog() {
        JDialog dialog = new JDialog(frame, "Login", true);
        dialog.setUndecorated(true);
        dialog.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        dialog.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel() {
            private float offset = 0;
            private Timer animationTimer;

            {
                animationTimer = new Timer(50, e -> {
                    offset += 0.005f;
                    if (offset > 1.0f) offset = 0;
                    repaint();
                });
                animationTimer.start();
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();


                float[] fractions = {0.0f, 0.5f, 1.0f};
                Color[] colors = {
                        new Color(59, 130, 246),   // Blue
                        new Color(99, 102, 241),   // Indigo
                        new Color(114, 223, 248)    // Purple
                };

                LinearGradientPaint lgp = new LinearGradientPaint(
                        0, 0, w, h, fractions, colors
                );
                g2d.setPaint(lgp);
                g2d.fillRect(0, 0, w, h);

                // Animated circles
                g2d.setColor(new Color(255, 255, 255, 20));
                int numCircles = 5;
                for (int i = 0; i < numCircles; i++) {
                    float phase = offset + (i * 0.2f);
                    int x = (int)(w * 0.2f + Math.sin(phase * Math.PI * 2) * w * 0.3f);
                    int y = (int)(h * 0.3f + Math.cos(phase * Math.PI * 2) * h * 0.2f);
                    int size = 100 + i * 50;
                    g2d.fillOval(x - size/2, y - size/2, size, size);
                }
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        dialog.setContentPane(mainPanel);


        JPanel card = new JPanel(new GridBagLayout()) {
            private float scale = 0.8f;
            private Timer scaleTimer;

            {
                scaleTimer = new Timer(20, e -> {
                    if (scale < 1.0f) {
                        scale += 0.02f;
                        if (scale >= 1.0f) {
                            scale = 1.0f;
                            scaleTimer.stop();
                        }
                        revalidate();
                        repaint();
                    }
                });
                scaleTimer.start();
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(0, 0, w, h, 60, 60);

                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.fill(roundedRectangle);

                g2d.setColor(new Color(255, 255, 255, 80));
                g2d.setStroke(new BasicStroke(2));
                g2d.draw(roundedRectangle);
            }
        };
        card.setPreferredSize(new Dimension(500, 600));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 0, 10, 0);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridx = 0;
        gc.gridy = 0;

        JPanel iconPanel = new JPanel() {
            private float pulsePhase = 0;
            private Timer pulseTimer;

            {
                pulseTimer = new Timer(50, e -> {
                    pulsePhase += 0.1f;
                    repaint();
                });
                pulseTimer.start();
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int baseSize = 100;
                float pulse = (float)(Math.sin(pulsePhase) * 5 + baseSize);
                int size = (int)pulse;
                int x = (getWidth() - size) / 2;
                int y = 10;

                GradientPaint gp = new GradientPaint(x, y, new Color(59, 130, 246),
                        x + size, y + size, new Color(49, 127, 168));
                g2d.setPaint(gp);
                g2d.fillOval(x, y, size, size);

                g2d.setColor(Color.WHITE);
                int innerSize = size - 20;
                g2d.fillOval(x + 10, y + 10, innerSize, innerSize);

                g2d.setPaint(gp);
                g2d.fillOval(x + 35, y + 25, 30, 30);

                GeneralPath path = new GeneralPath();
                path.moveTo(x + 25, y + 75);
                path.quadTo(x + 50, y + 55, x + 75, y + 75);
                g2d.fill(path);
            }
        };
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(400, 120));
        gc.insets = new Insets(0, 0, 20, 0);
        card.add(iconPanel, gc);

        gc.gridy++;
        JLabel title = new JLabel("Sign In", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 48));
        title.setForeground(Color.WHITE);
        gc.insets = new Insets(0, 0, 40, 0);
        card.add(title, gc);

        gc.gridy++;
        gc.insets = new Insets(10, 0, 10, 0);
        JPanel userPanel = createInputPanel("👤", "Enter Username");
        card.add(userPanel, gc);

        // Get the text field from userPanel
        JTextField nameField = null;
        for (Component comp : userPanel.getComponents()) {
            if (comp instanceof JTextField) {
                nameField = (JTextField) comp;
                break;
            }
        }

        gc.gridy++;
        JPanel passPanel = createPasswordPanel("🔒", "Enter Password");
        card.add(passPanel, gc);

        JPasswordField idField = null;
        for (Component comp : passPanel.getComponents()) {
            if (comp instanceof JPasswordField) {
                idField = (JPasswordField) comp;
                break;
            }
        }

        final JTextField finalNameField = nameField;
        final JPasswordField finalIdField = idField;

        gc.gridy++;
        JPanel optionsPanel = new JPanel(new BorderLayout());
        optionsPanel.setOpaque(false);


        gc.gridy++;
        JButton loginBtn = new JButton("LOGIN") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(0, 0, new Color(59, 130, 246),
                        getWidth(), 0, new Color(99, 102, 241));
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(getText(), x, y);
            }
        };
        loginBtn.setPreferredSize(new Dimension(400, 50));
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setContentAreaFilled(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setFocusPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        loginBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 17));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            }
        });

        gc.insets = new Insets(10, 0, 20, 0);
        card.add(loginBtn, gc);

        

        mainPanel.add(card, new GridBagConstraints());

        final boolean[] success = {false};

        loginBtn.addActionListener(e -> {
            String enteredName = finalNameField.getText().trim();
            String enteredId = new String(finalIdField.getPassword()).trim();

            // Check if placeholder text is still there
            if (enteredName.equals("Enter Username") || enteredName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter a username.", "Authentication Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (enteredId.equals("Enter Password") || enteredId.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter a password.", "Authentication Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String storedId = null;

            for (Map.Entry<String, String> entry : ADMIN_CREDENTIALS.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(enteredName)) {
                    storedId = entry.getValue();
                    break;
                }
            }

            if (storedId != null && storedId.equals(enteredId)) {
                success[0] = true;
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Invalid Username or Admin ID.", "Authentication Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
        return success[0];
    }

    private JPanel createInputPanel(String icon, String placeholder) {
        JPanel panel = new JPanel(new BorderLayout(10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(255, 255, 255, 40));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
            }
        };
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(400, 50));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        JTextField field = new JTextField();
        field.setName("inputField"); // Add name for retrieval
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(new Color(30, 30, 30));
        field.setCaretColor(new Color(30, 30, 30));
        field.setOpaque(true);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Add placeholder
        field.setText(placeholder);
        field.setForeground(new Color(150, 150, 150));
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(new Color(30, 30, 30));
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(new Color(150, 150, 150));
                }
            }
        });

        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPasswordPanel(String icon, String placeholder) {
        JPanel panel = new JPanel(new BorderLayout(10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(255, 255, 255, 40));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
            }
        };
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(400, 50));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        JPasswordField field = new JPasswordField();
        field.setName("passwordField"); // Add name for retrieval
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(new Color(30, 30, 30));
        field.setCaretColor(new Color(30, 30, 30));
        field.setOpaque(true);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        field.setEchoChar('•');

        // Add placeholder for password field
        field.setText(placeholder);
        field.setForeground(new Color(150, 150, 150));
        field.setEchoChar((char) 0); // Show placeholder text

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                String currentText = new String(field.getPassword());
                if (currentText.equals(placeholder)) {
                    field.setText("");
                    field.setForeground(new Color(30, 30, 30));
                    field.setEchoChar('•');
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getPassword().length == 0) {
                    field.setText(placeholder);
                    field.setForeground(new Color(150, 150, 150));
                    field.setEchoChar((char) 0);
                }
            }
        });

        panel.add(field, BorderLayout.CENTER);

        // Eye icon with theme color
        JLabel eyeIcon = new JLabel("👁");
        eyeIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        eyeIcon.setForeground(new Color(59, 130, 246)); // Theme blue color
        eyeIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));

        eyeIcon.addMouseListener(new MouseAdapter() {
            boolean visible = false;
            @Override
            public void mouseClicked(MouseEvent e) {
                String currentText = new String(field.getPassword());
                if (!currentText.equals(placeholder) && !currentText.isEmpty()) {
                    visible = !visible;
                    if (visible) {
                        field.setEchoChar((char) 0);
                        eyeIcon.setForeground(new Color(99, 102, 241)); // Lighter blue when active
                    } else {
                        field.setEchoChar('•');
                        eyeIcon.setForeground(new Color(59, 130, 246)); // Original blue
                    }
                }
            }
        });

        panel.add(eyeIcon, BorderLayout.EAST);

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
            try { UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); } catch (Exception ignored) {}

            MainAppGUI app = new MainAppGUI();
            if (app.showLoginDialog()) {
                app.init();
            }
        });
    }

    private void init() {
        store = new DataStore();
        reports = new ReportGenerator(store);

        frame = new JFrame("Community Fund Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 1200);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        frame.add(createTopBar(), BorderLayout.NORTH);
        frame.add(createSideBar(), BorderLayout.WEST);
        frame.add(createContentPanel(), BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private JPanel createTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(25, 118, 210));
        top.setPreferredSize(new Dimension(100, 70));  // Changed back to 70 from 100

        // Left panel for title (aligned to start)
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        leftPanel.setOpaque(false);

        JLabel title = new JLabel("Community Fund Management");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));  // Changed back to 20 from 28

        leftPanel.add(title);
        top.add(leftPanel, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        right.setOpaque(false);

        JLabel dateTime = new JLabel(java.time.LocalDate.now().toString());
        dateTime.setForeground(Color.WHITE);
        dateTime.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel user = new JLabel("Admin  ");
        user.setForeground(Color.WHITE);
        user.setFont(new Font("Segoe UI", Font.BOLD, 14));

        right.add(dateTime);
        right.add(user);
        top.add(right, BorderLayout.EAST);
        return top;
    }

    private JPanel createSideBar() {
        JPanel side = new JPanel();
        side.setPreferredSize(new Dimension(240, 0));
        side.setLayout(new BorderLayout());
        side.setBackground(new Color(248, 249, 250));
        side.setBorder(new EmptyBorder(10,10,10,10));

        JPanel profile = new JPanel(new BorderLayout());
        profile.setBackground(new Color(25, 118, 210));
        profile.setPreferredSize(new Dimension(220, 85));
        profile.setBorder(BorderFactory.createEmptyBorder(10,8,10,8));

        JLabel pName = new JLabel("<html><div style='text-align:center'><span style='font-size:13px;color:white;font-weight:bold'>Community Fund<br>Management</span></div></html>");
        pName.setHorizontalAlignment(SwingConstants.CENTER);
        pName.setVerticalAlignment(SwingConstants.CENTER);

        profile.add(pName, BorderLayout.CENTER);
        side.add(profile, BorderLayout.NORTH);

        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(0,1,6,6));
        buttons.setOpaque(false);
        buttons.setBorder(new EmptyBorder(12,0,0,0));

        String[] names = {"Dashboard", "Customers", "Loans", "Payments", "Reports", "Exit"};
        for (String n : names) {
            JButton b = new JButton(n);
            b.setFocusPainted(false);
            b.setBackground(Color.WHITE);
            b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            b.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220,220,220), 1),
                    new EmptyBorder(10,12,10,12)
            ));
            b.setHorizontalAlignment(SwingConstants.LEFT);
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));

            b.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    b.setBackground(new Color(227, 242, 253));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    b.setBackground(Color.WHITE);
                }
            });

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

    private JPanel buildDashboardPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        p.setBackground(Color.WHITE);

        JLabel heading = new JLabel("Dashboard");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setForeground(new Color(33, 33, 33));
        p.add(heading, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 15));
        center.setBorder(new EmptyBorder(20,0,0,0));
        center.setOpaque(false);

        // Stats panel with fixed height
        JPanel statsWrapper = new JPanel(new BorderLayout());
        statsWrapper.setOpaque(false);
        statsWrapper.setPreferredSize(new Dimension(0, 65));

        JPanel stats = new JPanel(new GridLayout(1,3,15,0));
        stats.setOpaque(false);
        stats.add(makeStatCard("Total Customers", String.valueOf(store.getCustomers().size()), new Color(33, 150, 243)));
        stats.add(makeStatCard("Active Loans", String.valueOf(store.getLoans().size()), new Color(76, 175, 80)));
        double outstanding = store.getLoans().stream().mapToDouble(Loan::getBalance).sum();
        stats.add(makeStatCard("Outstanding Amount", String.format("PKR %.2f", outstanding), new Color(156, 39, 176)));

        statsWrapper.add(stats, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new GridLayout(1,2,15,15));
        bottom.setOpaque(false);

        JPanel graphPanel = new JPanel(new BorderLayout());
        graphPanel.setBackground(Color.WHITE);
        graphPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                "Loan Distribution",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14)
        ));
        graphPanel.add(new GraphPanel(store), BorderLayout.CENTER);

        JPanel recentPanel = new JPanel(new BorderLayout());
        recentPanel.setBackground(Color.WHITE);
        recentPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                "Recent Loans",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14)
        ));

        DefaultListModel<String> lm = new DefaultListModel<>();
        List<Loan> loans = store.getLoans();
        for (int i = Math.max(0, loans.size()-8); i < loans.size(); i++) lm.addElement(loans.get(i).toString());
        JList<String> list = new JList<>(lm);
        list.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        list.setBorder(new EmptyBorder(8,8,8,8));
        recentPanel.add(new JScrollPane(list), BorderLayout.CENTER);

        bottom.add(graphPanel);
        bottom.add(recentPanel);

        center.add(statsWrapper, BorderLayout.NORTH);
        center.add(bottom, BorderLayout.CENTER);
        p.add(center, BorderLayout.CENTER);
        return p;
    }

    private JPanel makeStatCard(String label, String value, Color bg) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(bg);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.darker(), 1),
                new EmptyBorder(8,10,8,10)
        ));
        p.setPreferredSize(new Dimension(0, 65));

        JLabel l = new JLabel(label, SwingConstants.CENTER);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel v = new JLabel(value, SwingConstants.CENTER);
        v.setFont(new Font("Segoe UI", Font.BOLD, 16));
        v.setForeground(Color.WHITE);
        v.setHorizontalAlignment(SwingConstants.CENTER);

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

    private JPanel buildCustomersPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(15,15,15,15));
        p.setBackground(Color.WHITE);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel heading = new JLabel("Customers Management");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setForeground(new Color(33, 33, 33));
        topPanel.add(heading, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(new Color(25, 118, 210));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchBtn.setBorder(new EmptyBorder(8, 15, 8, 15));
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        topPanel.add(searchPanel, BorderLayout.EAST);

        p.add(topPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "Name", "CNIC", "Phone", "Email", "Address", "Actions"};
        customerModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };
        JTable table = new JTable(customerModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(25, 118, 210));
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setPreferredSize(new Dimension(0, 35));
        table.setRowHeight(30);
        table.setGridColor(new Color(224, 224, 224));
        table.getColumn("Actions").setPreferredWidth(180);

        table.getColumn("Actions").setCellRenderer(new ActionCellRenderer());
        table.getColumn("Actions").setCellEditor(new ActionCellEditor(table));

        searchBtn.addActionListener(e -> {
            String searchText = searchField.getText().trim().toLowerCase();
            if (searchText.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a name to search", "Search", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean found = false;
            for (int i = 0; i < table.getRowCount(); i++) {
                String name = table.getValueAt(i, 1).toString().toLowerCase();
                if (name.contains(searchText)) {
                    table.setRowSelectionInterval(i, i);
                    table.scrollRectToVisible(table.getCellRect(i, 0, true));
                    found = true;
                    break;
                }
            }

            if (!found) {
                JOptionPane.showMessageDialog(frame, "No records found for: " + searchField.getText(), "Search Result", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        p.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                        "Add New Customer",
                        0, 0,
                        new Font("Segoe UI", Font.BOLD, 14)
                ),
                new EmptyBorder(12, 12, 12, 12)
        ));
        form.setBackground(Color.WHITE);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6,6,6,6);
        gc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameF = new JTextField(16);
        nameF.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        nameF.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(6, 8, 6, 8)
        ));

        JTextField cnicF = new JTextField(16);
        cnicF.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cnicF.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(6, 8, 6, 8)
        ));

        JTextField phoneF = new JTextField(16);
        phoneF.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        phoneF.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(6, 8, 6, 8)
        ));

        JTextField emailF = new JTextField(16);
        emailF.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        emailF.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(6, 8, 6, 8)
        ));

        JTextField addrF = new JTextField(16);
        addrF.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        addrF.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(6, 8, 6, 8)
        ));

        JButton addBtn = new JButton("Add Customer");
        addBtn.setBackground(new Color(76, 175, 80));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addBtn.setBorder(new EmptyBorder(10, 20, 10, 20));
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JLabel cnicLabel = new JLabel("CNIC:");
        cnicLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JLabel addrLabel = new JLabel("Address:");
        addrLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        gc.gridx=0; gc.gridy=0; form.add(nameLabel, gc); gc.gridx=1; form.add(nameF, gc);
        gc.gridx=0; gc.gridy=1; form.add(cnicLabel, gc); gc.gridx=1; form.add(cnicF, gc);
        gc.gridx=0; gc.gridy=2; form.add(phoneLabel, gc); gc.gridx=1; form.add(phoneF, gc);
        gc.gridx=0; gc.gridy=3; form.add(emailLabel, gc); gc.gridx=1; form.add(emailF, gc);
        gc.gridx=0; gc.gridy=4; form.add(addrLabel, gc); gc.gridx=1; form.add(addrF, gc);
        gc.gridx=1; gc.gridy=5; gc.insets = new Insets(12,6,6,6); form.add(addBtn, gc);

        addBtn.addActionListener(e -> {
            String name = nameF.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Name required", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String id = generateId("C");
            Customer cst = new Customer(id, name, cnicF.getText().trim(), emailF.getText().trim(), addrF.getText().trim(), phoneF.getText().trim());
            if (store.addCustomer(cst)) {
                refreshCustomerTable();
                nameF.setText("");
                cnicF.setText("");
                phoneF.setText("");
                emailF.setText("");
                addrF.setText("");
                JOptionPane.showMessageDialog(frame, "Customer added: " + cst.getCustomerId(), "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid data! Check CNIC, Phone, or Email.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        p.add(form, BorderLayout.EAST);
        refreshCustomerTable();
        return p;
    }

    class ActionCellRenderer extends JPanel implements TableCellRenderer {
        private JButton editBtn = new JButton("Edit");
        private JButton delBtn = new JButton("Delete");

        public ActionCellRenderer() {
            setLayout(new GridLayout(1, 2, 5, 0));
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
            editBtn.setBackground(new Color(23,162,184));
            editBtn.setForeground(Color.WHITE);
            editBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            delBtn.setBackground(new Color(231,76,60));
            delBtn.setForeground(Color.WHITE);
            delBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
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
        p.setBorder(new EmptyBorder(15,15,15,15));
        p.setBackground(Color.WHITE);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel heading = new JLabel("Loans Management");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setForeground(new Color(33, 33, 33));
        topPanel.add(heading, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        JLabel searchLabel = new JLabel("Search Customer:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(new Color(25, 118, 210));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchBtn.setBorder(new EmptyBorder(8, 15, 8, 15));
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        topPanel.add(searchPanel, BorderLayout.EAST);

        p.add(topPanel, BorderLayout.NORTH);

        String[] loanCols = {
                "Loan ID","Type","Customer ID","Principal","Interest",
                "Duration(m)","Remaining Balance","Paid Amount","Last Payment Date",
                "Installments","EMI","Issue Date","Due Date","Status"
        };
        loanModel = new DefaultTableModel(loanCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(loanModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(25, 118, 210));
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setPreferredSize(new Dimension(0, 35));
        table.setRowHeight(28);
        table.setGridColor(new Color(224, 224, 224));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(110);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(90);
        table.getColumnModel().getColumn(6).setPreferredWidth(130);
        table.getColumnModel().getColumn(7).setPreferredWidth(100);
        table.getColumnModel().getColumn(8).setPreferredWidth(140);
        table.getColumnModel().getColumn(9).setPreferredWidth(100);
        table.getColumnModel().getColumn(10).setPreferredWidth(80);
        table.getColumnModel().getColumn(11).setPreferredWidth(100);
        table.getColumnModel().getColumn(12).setPreferredWidth(100);
        table.getColumnModel().getColumn(13).setPreferredWidth(80);

        searchBtn.addActionListener(e -> {
            String searchText = searchField.getText().trim();
            if (searchText.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a Customer ID or Name to search", "Search", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean found = false;
            for (int i = 0; i < table.getRowCount(); i++) {
                String customerId = table.getValueAt(i, 2).toString();
                Customer cust = store.findCustomerById(customerId);

                if (customerId.toLowerCase().contains(searchText.toLowerCase()) ||
                        (cust != null && cust.getName().toLowerCase().contains(searchText.toLowerCase()))) {
                    table.setRowSelectionInterval(i, i);
                    table.scrollRectToVisible(table.getCellRect(i, 0, true));
                    found = true;
                    break;
                }
            }

            if (!found) {
                JOptionPane.showMessageDialog(frame, "No records found for: " + searchField.getText(), "Search Result", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.7);
        split.setLeftComponent(new JScrollPane(table));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                        "Issue New Loan",
                        0, 0,
                        new Font("Segoe UI", Font.BOLD, 14)
                ),
                new EmptyBorder(12, 12, 12, 12)
        ));
        form.setBackground(Color.WHITE);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6,6,6,6);
        gc.fill = GridBagConstraints.HORIZONTAL;

        JTextField custIdF = new JTextField(14);
        custIdF.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        custIdF.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(6, 8, 6, 8)
        ));

        JTextField amountF = new JTextField(14);
        amountF.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        amountF.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(6, 8, 6, 8)
        ));

        JTextField durF = new JTextField(14);
        durF.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        durF.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(6, 8, 6, 8)
        ));

        JComboBox<String> typeBox = new JComboBox<>(new String[] {"PERSONAL","BUSINESS","EDUCATION"});
        typeBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JCheckBox installmentBox = new JCheckBox("Installment Loan?");
        installmentBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        installmentBox.setOpaque(false);

        JButton issueBtn = new JButton("Issue Loan");
        issueBtn.setBackground(new Color(33, 150, 243));
        issueBtn.setForeground(Color.WHITE);
        issueBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        issueBtn.setBorder(new EmptyBorder(10, 20, 10, 20));
        issueBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel custLabel = new JLabel("Customer ID:");
        custLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JLabel amtLabel = new JLabel("Amount:");
        amtLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JLabel durLabel = new JLabel("Duration (months):");
        durLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        gc.gridx=0; gc.gridy=0; form.add(custLabel, gc); gc.gridx=1; form.add(custIdF, gc);
        gc.gridx=0; gc.gridy=1; form.add(amtLabel, gc); gc.gridx=1; form.add(amountF, gc);
        gc.gridx=0; gc.gridy=2; form.add(durLabel, gc); gc.gridx=1; form.add(durF, gc);
        gc.gridx=0; gc.gridy=3; form.add(typeLabel, gc); gc.gridx=1; form.add(typeBox, gc);
        gc.gridx=1; gc.gridy=4; form.add(installmentBox, gc);
        gc.gridx=1; gc.gridy=5; gc.insets = new Insets(12,6,6,6); form.add(issueBtn, gc);

        issueBtn.addActionListener(e -> {
            String cid = custIdF.getText().trim();
            Customer c = store.findCustomerById(cid);
            if (c == null) {
                JOptionPane.showMessageDialog(frame, "Customer not found", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double amt;
            int months;
            try { amt = Double.parseDouble(amountF.getText().trim()); }
            catch (NumberFormatException ex) { JOptionPane.showMessageDialog(frame, "Invalid amount", "Validation", JOptionPane.WARNING_MESSAGE); return; }
            if (amt <= 0) { JOptionPane.showMessageDialog(frame, "Amount must be positive", "Validation", JOptionPane.WARNING_MESSAGE); return; }

            try { months = Integer.parseInt(durF.getText().trim()); }
            catch (NumberFormatException ex) { JOptionPane.showMessageDialog(frame, "Invalid months", "Validation", JOptionPane.WARNING_MESSAGE); return; }
            if (months <= 0) { JOptionPane.showMessageDialog(frame, "Duration must be positive", "Validation", JOptionPane.WARNING_MESSAGE); return; }

            boolean installment = installmentBox.isSelected();
            String type = (String) typeBox.getSelectedItem();
            String id = generateId("L");
            Loan loan;
            if ("PERSONAL".equals(type))
                loan = new PersonalLoan(id, cid, amt, months, LocalDate.now(), installment);
            else if ("BUSINESS".equals(type))
                loan = new BusinessLoan(id, cid, amt, months, LocalDate.now(), installment);
            else
                loan = new EducationLoan(id, cid, amt, months, LocalDate.now(), installment);

            store.addLoan(loan);
            refreshLoanTable();
            JOptionPane.showMessageDialog(frame, "Loan issued: " + id + "\nTotal payable (approx): "
                    + String.format("%.2f", loan.getBalance()), "Success", JOptionPane.INFORMATION_MESSAGE);

            custIdF.setText(""); amountF.setText(""); durF.setText(""); installmentBox.setSelected(false);
        });

        split.setRightComponent(form);
        p.add(split, BorderLayout.CENTER);

        refreshLoanTable();
        return p;
    }

    private void refreshLoanTable() {
        loanModel.setRowCount(0);
        for (Loan l : store.getLoans()) {
            double interest = l.calculateTotalPayable() - l.getPrincipal();
            double paid = store.getTotalPaidForLoan(l.getLoanId());
            LocalDate lastPaid = store.getLastPaymentDateForLoan(l.getLoanId());
            String lastPaidStr = lastPaid != null ? lastPaid.toString() : "-";

            String installments = "No";
            String emiStr = "-";

            if (l instanceof PersonalLoan pl && pl.isInstallment()) {
                installments = String.valueOf(pl.getDurationMonths());
                emiStr = String.format("%.2f", pl.getEmi());
            } else if (l instanceof BusinessLoan bl && bl.isInstallment()) {
                installments = String.valueOf(bl.getDurationMonths());
                emiStr = String.format("%.2f", bl.getEmi());
            } else if (l instanceof EducationLoan el && el.isInstallment()) {
                installments = String.valueOf(el.getDurationMonths());
                emiStr = String.format("%.2f", el.getEmi());
            }

            loanModel.addRow(new Object[] {
                    l.getLoanId(), l.getLoanType(), l.getCustomerId(),
                    String.format("%.2f", l.getPrincipal()),
                    String.format("%.2f", interest),
                    l.getDurationMonths(),
                    String.format("%.2f", l.getBalance()),
                    String.format("%.2f", paid),
                    lastPaidStr,
                    installments,
                    emiStr,
                    l.getIssueDate(),
                    l.getDueDate(),
                    l.getStatus()
            });
        }
    }

    private JPanel buildPaymentsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(15,15,15,15));
        p.setBackground(Color.WHITE);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel heading = new JLabel("Payments");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setForeground(new Color(33, 33, 33));
        topPanel.add(heading, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        JLabel searchLabel = new JLabel("Search Loan ID:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(new Color(25, 118, 210));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchBtn.setBorder(new EmptyBorder(8, 15, 8, 15));
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        topPanel.add(searchPanel, BorderLayout.EAST);

        p.add(topPanel, BorderLayout.NORTH);

        String[] cols = {"Payment ID","Loan ID","Amount","Date"};
        paymentModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(paymentModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(25, 118, 210));
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setPreferredSize(new Dimension(0, 35));
        table.setRowHeight(30);
        table.setGridColor(new Color(224, 224, 224));

        searchBtn.addActionListener(e -> {
            String searchText = searchField.getText().trim().toLowerCase();
            if (searchText.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a Loan ID to search", "Search", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean found = false;
            for (int i = 0; i < table.getRowCount(); i++) {
                String loanId = table.getValueAt(i, 1).toString().toLowerCase();
                if (loanId.contains(searchText)) {
                    table.setRowSelectionInterval(i, i);
                    table.scrollRectToVisible(table.getCellRect(i, 0, true));
                    found = true;
                    break;
                }
            }

            if (!found) {
                JOptionPane.showMessageDialog(frame, "No records found for: " + searchField.getText(), "Search Result", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        p.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                        "Record Payment",
                        0, 0,
                        new Font("Segoe UI", Font.BOLD, 14)
                ),
                new EmptyBorder(12, 12, 12, 12)
        ));
        form.setBackground(Color.WHITE);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6,6,6,6);
        gc.fill = GridBagConstraints.HORIZONTAL;

        JTextField loanField = new JTextField(14);
        loanField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        loanField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(6, 8, 6, 8)
        ));

        JTextField amtField = new JTextField(14);
        amtField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        amtField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(6, 8, 6, 8)
        ));

        JButton recBtn = new JButton("Record Payment");
        recBtn.setBackground(new Color(255, 152, 0));
        recBtn.setForeground(Color.WHITE);
        recBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        recBtn.setBorder(new EmptyBorder(10, 20, 10, 20));
        recBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel loanLabel = new JLabel("Loan ID:");
        loanLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JLabel amtLabel = new JLabel("Amount:");
        amtLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        gc.gridx=0; gc.gridy=0; form.add(loanLabel, gc); gc.gridx=1; form.add(loanField, gc);
        gc.gridx=0; gc.gridy=1; form.add(amtLabel, gc); gc.gridx=1; form.add(amtField, gc);
        gc.gridx=1; gc.gridy=2; gc.insets = new Insets(12,6,6,6); form.add(recBtn, gc);

        recBtn.addActionListener(e -> {
            String loanId = loanField.getText().trim();
            Loan loan = store.findLoanById(loanId);
            if (loan == null) {
                JOptionPane.showMessageDialog(frame, "Loan not found.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double amt;
            try { amt = Double.parseDouble(amtField.getText().trim()); }
            catch (Exception ex) { JOptionPane.showMessageDialog(frame, "Invalid amount.", "Validation", JOptionPane.WARNING_MESSAGE); return; }
            if (amt <= 0) {
                JOptionPane.showMessageDialog(frame, "Amount must be positive.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String pid = generateId("P");
            Payment pm = new Payment(pid, loanId, amt, LocalDate.now());
            store.recordPaymentAndUpdateLoan(pm);

            refreshPaymentTable();
            refreshLoanTable();

            JOptionPane.showMessageDialog(frame, "Payment recorded: " + pid, "Success", JOptionPane.INFORMATION_MESSAGE);

            loanField.setText(""); amtField.setText("");
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.setOpaque(false);
        JTextField viewLoan = new JTextField(12);
        viewLoan.setToolTipText("Loan ID to view payments");
        JButton viewBtn = new JButton("View Payments");
        JButton refreshBtn = new JButton("Refresh All");

        viewBtn.addActionListener(e -> {
            String lid = viewLoan.getText().trim();
            if (lid.isEmpty()) { JOptionPane.showMessageDialog(frame, "Enter loan ID.", "Validation", JOptionPane.WARNING_MESSAGE); return; }
            List<Payment> list = store.getPaymentsForLoan(lid);
            paymentModel.setRowCount(0);
            for (Payment pay : list)
                paymentModel.addRow(new Object[]{pay.getPaymentId(), pay.getLoanId(), String.format("%.2f", pay.getAmountPaid()), pay.getDate()});
        });

        refreshBtn.addActionListener(e -> {
            refreshPaymentTable();
            refreshLoanTable();
        });

        bottom.add(viewLoan); bottom.add(viewBtn); bottom.add(refreshBtn);

        p.add(form, BorderLayout.EAST);
        p.add(bottom, BorderLayout.SOUTH);

        refreshPaymentTable();
        return p;
    }

    private void refreshPaymentTable() {
        paymentModel.setRowCount(0);
        for (Payment p : store.getPayments()) {
            paymentModel.addRow(new Object[]{
                    p.getPaymentId(),
                    p.getLoanId(),
                    String.format("%.2f", p.getAmountPaid()),
                    p.getDate()
            });
        }
    }

    private JPanel buildReportsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(15,15,15,15));
        p.setBackground(Color.WHITE);

        JLabel heading = new JLabel("Reports & Analytics");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setForeground(new Color(33, 33, 33));
        p.add(heading, BorderLayout.NORTH);

        JTextArea out = new JTextArea();
        out.setEditable(false);
        out.setLineWrap(true);
        out.setWrapStyleWord(true);
        out.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        out.setBorder(new EmptyBorder(12, 12, 12, 12));
        JScrollPane sp = new JScrollPane(out);
        sp.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        controls.setOpaque(false);
        controls.setBorder(new EmptyBorder(12, 0, 8, 0));

        JButton loanSummaryBtn = new JButton("Loan Summary");
        loanSummaryBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        loanSummaryBtn.setBackground(new Color(33, 150, 243));
        loanSummaryBtn.setForeground(Color.WHITE);
        loanSummaryBtn.setBorder(new EmptyBorder(8, 15, 8, 15));
        loanSummaryBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton overdueBtn = new JButton("Overdue Loans");
        overdueBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        overdueBtn.setBackground(new Color(244, 67, 54));
        overdueBtn.setForeground(Color.WHITE);
        overdueBtn.setBorder(new EmptyBorder(8, 15, 8, 15));
        overdueBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JTextField custField = new JTextField(14);
        custField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        custField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(6, 8, 6, 8)
        ));

        JButton custBtn = new JButton("Customer Report");
        custBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        custBtn.setBackground(new Color(39, 123, 176));
        custBtn.setForeground(Color.WHITE);
        custBtn.setBorder(new EmptyBorder(8, 15, 8, 15));
        custBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        loanSummaryBtn.addActionListener(e -> out.setText(reports.loanSummaryText()));
        overdueBtn.addActionListener(e -> out.setText(reports.overdueText()));
        custBtn.addActionListener(e -> {
            String id = custField.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Customer ID required.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            out.setText(reports.customerReportText(id));
        });

        JButton exportLoansCsvBtn = new JButton("📥 Export Loans CSV");
        exportLoansCsvBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        exportLoansCsvBtn.setBackground(new Color(76, 175, 80));
        exportLoansCsvBtn.setForeground(Color.WHITE);
        exportLoansCsvBtn.setBorder(new EmptyBorder(10, 20, 10, 20));
        exportLoansCsvBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton exportOverdueCsvBtn = new JButton("📥 Export Overdue CSV");
        exportOverdueCsvBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        exportOverdueCsvBtn.setBackground(new Color(76, 175, 80));
        exportOverdueCsvBtn.setForeground(Color.WHITE);
        exportOverdueCsvBtn.setBorder(new EmptyBorder(10, 20, 10, 20));
        exportOverdueCsvBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton exportCustomerCsvBtn = new JButton("📥 Export Customer CSV");
        exportCustomerCsvBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        exportCustomerCsvBtn.setBackground(new Color(76, 175, 80));
        exportCustomerCsvBtn.setForeground(Color.WHITE);
        exportCustomerCsvBtn.setBorder(new EmptyBorder(10, 20, 10, 20));
        exportCustomerCsvBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        exportLoansCsvBtn.addActionListener(e -> {
            File file = promptAndChooseFile("loans_summary.csv");
            if (file == null) return;
            boolean ok = reports.exportLoanSummaryCSV(file.getAbsolutePath());
            if (ok) {
                JOptionPane.showMessageDialog(frame, "Saved: " + file.getAbsolutePath(), "Success", JOptionPane.INFORMATION_MESSAGE);
                tryOpenFile(file);
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to save file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        exportOverdueCsvBtn.addActionListener(e -> {
            File file = promptAndChooseFile("overdue_loans.csv");
            if (file == null) return;
            boolean ok = reports.exportOverdueCSV(file.getAbsolutePath());
            if (ok) {
                JOptionPane.showMessageDialog(frame, "Saved: " + file.getAbsolutePath(), "Success", JOptionPane.INFORMATION_MESSAGE);
                tryOpenFile(file);
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to save file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        exportCustomerCsvBtn.addActionListener(e -> {
            String id = custField.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Customer ID required to export customer CSV.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            File file = promptAndChooseFile("customer_" + id + ".csv");
            if (file == null) return;
            boolean ok = reports.exportCustomerCSV(id, file.getAbsolutePath());
            if (ok) {
                JOptionPane.showMessageDialog(frame, "Saved: " + file.getAbsolutePath(), "Success", JOptionPane.INFORMATION_MESSAGE);
                tryOpenFile(file);
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to save file. (Customer may not exist or IO error)", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        controls.add(loanSummaryBtn);
        controls.add(overdueBtn);
        controls.add(new JLabel("  Customer ID:"));
        controls.add(custField);
        controls.add(custBtn);

        JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        exportPanel.setOpaque(false);
        exportPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                "Export Options",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14)
        ));
        exportPanel.add(exportLoansCsvBtn);
        exportPanel.add(exportOverdueCsvBtn);
        exportPanel.add(exportCustomerCsvBtn);

        JPanel north = new JPanel(new BorderLayout());
        north.setOpaque(false);
        north.add(controls, BorderLayout.NORTH);
        north.add(exportPanel, BorderLayout.SOUTH);

        p.add(north, BorderLayout.NORTH);
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private File promptAndChooseFile(String suggestedName) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save CSV");
        chooser.setSelectedFile(new File(suggestedName));

        int choice = chooser.showSaveDialog(frame);
        if (choice == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getParentFile(), file.getName() + ".csv");
            }
            return file;
        }
        return null;
    }

    private void tryOpenFile(File file) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }
        } catch (Exception ex) {
            System.err.println("Could not open file: " + ex.getMessage());
        }
    }

    private String generateId(String prefix) {
        return prefix + UUID.randomUUID().toString().replace("-", "").substring(0,8).toUpperCase();
    }
}

