import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * InventoryGUI - Light Warehouse Dashboard Interface ("StockSentinel")
 * Uses pure Java Swing/AWT with CardLayout, custom-styled components,
 * clean white cards with teal & amber accents, and rounded pill status badges.
 */
public class InventoryGUI extends JFrame {

    // ==========================================
    // COLOR PALETTE CONSTANTS (Warehouse Light Theme)
    // ==========================================
    private static final Color COLOR_BG              = new Color(0xF4, 0xF6, 0xF8); // #F4F6F8 App background
    private static final Color COLOR_SIDEBAR         = new Color(0xFF, 0xFF, 0xFF); // #FFFFFF Sidebar background
    private static final Color COLOR_BORDER          = new Color(0xE2, 0xE8, 0xF0); // #E2E8F0 Subtle borders
    private static final Color COLOR_CARD            = new Color(0xFF, 0xFF, 0xFF); // #FFFFFF Card panels
    private static final Color COLOR_INPUT_BG        = new Color(0xF8, 0xFA, 0xFC); // #F8FAFC Input fields
    private static final Color COLOR_INPUT_BORDER    = new Color(0xCB, 0xD5, 0xE1); // #CBD5E1 Input border
    private static final Color COLOR_TEXT_PRIMARY    = new Color(0x0F, 0x17, 0x2A); // #0F172A Primary dark text
    private static final Color COLOR_TEXT_SECONDARY  = new Color(0x64, 0x74, 0x8B); // #64748B Secondary grey text

    // Teals & Accents
    private static final Color COLOR_HEADER_START    = new Color(0x0F, 0x76, 0x6E); // #0F766E Header gradient start
    private static final Color COLOR_HEADER_END      = new Color(0x13, 0x4E, 0x4A); // #134E4A Header gradient end
    private static final Color COLOR_HEADER_SUBTITLE = new Color(0xCC, 0xFB, 0xF1); // #CCFBF1 Light teal subtitle
    private static final Color COLOR_TEAL_PRIMARY    = new Color(0x0D, 0x94, 0x88); // #0D9488 Primary teal
    private static final Color COLOR_TEAL_DARK       = new Color(0x0F, 0x76, 0x6E); // #0F766E Dark teal text
    private static final Color COLOR_TEAL_LIGHT      = new Color(0xCC, 0xFB, 0xF1); // #CCFBF1 Light teal selection

    // Navigation & Status bar
    private static final Color COLOR_NAV_TEXT        = new Color(0x33, 0x41, 0x55); // #334155 Sidebar normal text
    private static final Color COLOR_NAV_HOVER       = new Color(0xF1, 0xF5, 0xF9); // #F1F5F9 Sidebar hover bg
    private static final Color COLOR_STATUS_BG       = new Color(0xE2, 0xE8, 0xF0); // #E2E8F0 Status bar bg
    private static final Color COLOR_STATUS_TEXT     = new Color(0x47, 0x55, 0x69); // #475569 Status bar text

    // Buttons
    private static final Color COLOR_BTN_TEAL        = new Color(0x0D, 0x94, 0x88); // #0D9488 Primary teal
    private static final Color COLOR_BTN_GREEN       = new Color(0x16, 0xA3, 0x4A); // #16A34A Success green
    private static final Color COLOR_BTN_RED         = new Color(0xDC, 0x26, 0x26); // #DC2626 Danger red
    private static final Color COLOR_BTN_AMBER       = new Color(0xF5, 0x9E, 0x0B); // #F59E0B Warning amber
    private static final Color COLOR_BTN_SLATE       = new Color(0x64, 0x74, 0x8B); // #64748B Neutral slate
    private static final Color COLOR_AMBER_TEXT      = new Color(0x1F, 0x29, 0x37); // #1F2937 Dark text on amber

    // Status Pill Badges
    private static final Color COLOR_PILL_SUFFICIENT_BG   = new Color(0xDC, 0xFC, 0xE7); // #DCFCE7
    private static final Color COLOR_PILL_SUFFICIENT_TEXT = new Color(0x16, 0x65, 0x34); // #166534
    private static final Color COLOR_PILL_LOW_BG          = new Color(0xFE, 0xF3, 0xC7); // #FEF3C7
    private static final Color COLOR_PILL_LOW_TEXT        = new Color(0x92, 0x40, 0x0E); // #92400E
    private static final Color COLOR_PILL_OUT_BG          = new Color(0xFE, 0xE2, 0xE2); // #FEE2E2
    private static final Color COLOR_PILL_OUT_TEXT        = new Color(0x99, 0x1B, 0x1B); // #991B1B

    // Table
    private static final Color COLOR_TABLE_ROW_ALT   = new Color(0xF8, 0xFA, 0xFC); // #F8FAFC
    private static final Color COLOR_TABLE_HEADER_BG = new Color(0xF1, 0xF5, 0xF9); // #F1F5F9
    private static final Color COLOR_TABLE_HEADER_TXT= new Color(0x47, 0x55, 0x69); // #475569

    // ==========================================
    // BACKEND REFERENCE & DATA COMPONENTS
    // ==========================================
    private final InventoryManager manager;

    // Navigation and Cards
    private CardLayout cardLayout;
    private JPanel cardsPanel;
    private SidebarButton[] navButtons;
    private JLabel statusLeftLbl;

    private static final String[] CARD_NAMES = {
        "REGISTER", "STOCK_ENTRY", "SEARCH", "ALERTS", "REPORT"
    };
    private static final String[] PAGE_TITLES = {
        "Register Product", "Stock Entry", "Search", "Low Stock Alerts", "Stock Report"
    };

    // Components requiring dynamic updates
    private JComboBox<String> productCombo;
    private JTextField stockQtyField;
    private JTextField stockMinField;

    // Stock Report Components
    private JTable reportTable;
    private DefaultTableModel reportTableModel;
    private SummaryCard cardTotal;
    private SummaryCard cardLow;
    private SummaryCard cardOut;

    // Alerts Table Components
    private JTable alertsTable;
    private DefaultTableModel alertsTableModel;
    private JLabel alertsSummaryLbl;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================
    public InventoryGUI(InventoryManager manager) {
        this.manager = manager;

        // Force cross-platform Look and Feel and set dialog defaults
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.put("OptionPane.background", Color.WHITE);
            UIManager.put("Panel.background", Color.WHITE);
            UIManager.put("OptionPane.messageForeground", COLOR_TEXT_PRIMARY);
        } catch (Exception ignored) {}

        setTitle("StockSentinel - Warehouse Inventory Alert System");
        setSize(1100, 700);
        setMinimumSize(new Dimension(980, 620));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        initUI();

        // Initial data sync
        refreshProductCombo();
        refreshReport();
    }

    // ==========================================
    // MAIN LAYOUT INITIALIZATION
    // ==========================================
    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(COLOR_BG);

        // 1. Top Header (NORTH)
        root.add(createHeader(), BorderLayout.NORTH);

        // 2. Left Sidebar (WEST)
        root.add(createSidebar(), BorderLayout.WEST);

        // 3. Center Cards (CENTER)
        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);
        cardsPanel.setBackground(COLOR_BG);

        cardsPanel.add(createRegisterPage(), CARD_NAMES[0]);
        cardsPanel.add(createStockEntryPage(), CARD_NAMES[1]);
        cardsPanel.add(createSearchPage(), CARD_NAMES[2]);
        cardsPanel.add(createAlertsPage(), CARD_NAMES[3]);
        cardsPanel.add(createReportPage(), CARD_NAMES[4]);

        root.add(cardsPanel, BorderLayout.CENTER);

        // 4. Bottom Status Bar (SOUTH)
        root.add(createStatusBar(), BorderLayout.SOUTH);

        setContentPane(root);
    }

    // ==========================================
    // 1. TOP HEADER (NORTH)
    // ==========================================
    private JPanel createHeader() {
        // Gradient background panel (Teal gradient)
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, COLOR_HEADER_START, getWidth(), 0, COLOR_HEADER_END));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));

        // Left Branding: Emoji + Title + Subtitle
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        brandPanel.setOpaque(false);

        JLabel iconLbl = new JLabel("🏭");
        iconLbl.setFont(getAppFont(Font.PLAIN, 28));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel titleLbl = new JLabel("StockSentinel");
        titleLbl.setFont(getAppFont(Font.BOLD, 22));
        titleLbl.setForeground(Color.WHITE);

        JLabel subLbl = new JLabel("Warehouse Inventory Alert System");
        subLbl.setFont(getAppFont(Font.PLAIN, 12));
        subLbl.setForeground(COLOR_HEADER_SUBTITLE);

        textPanel.add(titleLbl);
        textPanel.add(subLbl);

        brandPanel.add(iconLbl);
        brandPanel.add(textPanel);

        // Right Pill: System Active indicator
        JPanel pillPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 6));
        pillPanel.setOpaque(false);

        JPanel pill = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_PILL_SUFFICIENT_BG);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.setColor(new Color(0xBB, 0xF7, 0xD0));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pill.setOpaque(false);
        pill.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));

        JLabel pillText = new JLabel("● SYSTEM ACTIVE");
        pillText.setFont(getAppFont(Font.BOLD, 11));
        pillText.setForeground(COLOR_PILL_SUFFICIENT_TEXT);
        pill.add(pillText);

        pillPanel.add(pill);

        header.add(brandPanel, BorderLayout.WEST);
        header.add(pillPanel, BorderLayout.EAST);
        return header;
    }

    // ==========================================
    // 2. LEFT SIDEBAR (WEST)
    // ==========================================
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(COLOR_SIDEBAR);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, COLOR_BORDER));

        // Top Navigation Label
        JPanel navTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 16));
        navTop.setOpaque(false);
        JLabel navHeaderLbl = new JLabel("NAVIGATION");
        navHeaderLbl.setFont(getAppFont(Font.BOLD, 11));
        navHeaderLbl.setForeground(COLOR_TEXT_SECONDARY);
        navTop.add(navHeaderLbl);

        // Vertical Button Container
        JPanel navList = new JPanel();
        navList.setOpaque(false);
        navList.setLayout(new BoxLayout(navList, BoxLayout.Y_AXIS));

        String[][] navItems = {
            {"📝", "Register Product"},
            {"📥", "Stock Entry"},
            {"🔍", "Search"},
            {"⚠️", "Low Stock Alerts"},
            {"📊", "Stock Report"}
        };

        navButtons = new SidebarButton[navItems.length];
        for (int i = 0; i < navItems.length; i++) {
            final int pageIndex = i;
            SidebarButton btn = new SidebarButton(navItems[i][0], navItems[i][1]);
            btn.addActionListener(e -> selectPage(pageIndex));
            navButtons[i] = btn;
            navList.add(btn);
            navList.add(Box.createVerticalStrut(4));
        }

        navButtons[0].setActive(true); // First item selected by default

        // Bottom Footer Label
        JPanel navBottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 14));
        navBottom.setOpaque(false);
        JLabel versionLbl = new JLabel("v1.0 · Java Swing");
        versionLbl.setFont(getAppFont(Font.PLAIN, 11));
        versionLbl.setForeground(COLOR_TEXT_SECONDARY);
        navBottom.add(versionLbl);

        sidebar.add(navTop, BorderLayout.NORTH);
        sidebar.add(navList, BorderLayout.CENTER);
        sidebar.add(navBottom, BorderLayout.SOUTH);
        return sidebar;
    }

    // Switch card page and update button highlight & status bar
    private void selectPage(int index) {
        for (int i = 0; i < navButtons.length; i++) {
            navButtons[i].setActive(i == index);
        }
        cardLayout.show(cardsPanel, CARD_NAMES[index]);
        statusLeftLbl.setText("● Viewing: " + PAGE_TITLES[index]);

        if (index == 1) { // Stock Entry
            refreshProductCombo();
        } else if (index == 4) { // Stock Report
            refreshReport();
        }
    }

    // ==========================================
    // 3. BOTTOM STATUS BAR (SOUTH)
    // ==========================================
    private JPanel createStatusBar() {
        JPanel status = new JPanel(new BorderLayout());
        status.setBackground(COLOR_STATUS_BG);
        status.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDER),
            BorderFactory.createEmptyBorder(6, 18, 6, 18)
        ));

        statusLeftLbl = new JLabel("● Viewing: Register Product");
        statusLeftLbl.setFont(getAppFont(Font.PLAIN, 12));
        statusLeftLbl.setForeground(COLOR_STATUS_TEXT);

        JLabel statusRightLbl = new JLabel("Java Swing · Inventory Alert System");
        statusRightLbl.setFont(getAppFont(Font.PLAIN, 12));
        statusRightLbl.setForeground(COLOR_STATUS_TEXT);

        status.add(statusLeftLbl, BorderLayout.WEST);
        status.add(statusRightLbl, BorderLayout.EAST);
        return status;
    }

    // ==========================================
    // PAGE 1: REGISTER PRODUCT
    // ==========================================
    private JPanel createRegisterPage() {
        JPanel page = new JPanel(new BorderLayout(0, 20));
        page.setOpaque(false);
        page.setBorder(BorderFactory.createEmptyBorder(24, 30, 24, 30));

        page.add(createPageHeader("📝 Register Product", "Add new products and specify minimum stock thresholds"), BorderLayout.NORTH);

        CardPanel card = new CardPanel();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel nameLbl = createFieldLabel("PRODUCT NAME");
        JTextField nameField = new JTextField(24);
        styleField(nameField);

        JLabel minLbl = createFieldLabel("MINIMUM STOCK LEVEL");
        JTextField minField = new JTextField(24);
        styleField(minField);

        gbc.gridx = 0; gbc.gridy = 0; card.add(nameLbl, gbc);
        gbc.gridx = 0; gbc.gridy = 1; card.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; card.add(minLbl, gbc);
        gbc.gridx = 0; gbc.gridy = 3; card.add(minField, gbc);

        // Buttons row
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        btnRow.setOpaque(false);

        RoundedButton addBtn = new RoundedButton("+ Add Product", COLOR_BTN_GREEN);
        RoundedButton consoleBtn = new RoundedButton("Use Console Scanner Input", COLOR_BTN_SLATE);
        RoundedButton clearBtn = new RoundedButton("Clear", COLOR_BTN_SLATE);

        btnRow.add(addBtn);
        btnRow.add(consoleBtn);
        btnRow.add(clearBtn);

        gbc.gridx = 0; gbc.gridy = 4;
        card.add(btnRow, gbc);

        // Action Listeners
        addBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Product name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                int minLvl = Integer.parseInt(minField.getText().trim());
                if (minLvl < 0) {
                    JOptionPane.showMessageDialog(this, "Minimum level cannot be negative.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                boolean success = manager.registerProduct(name, minLvl);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Product '" + name + "' registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    nameField.setText("");
                    minField.setText("");
                    refreshProductCombo();
                    refreshReport();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to register. Duplicate name or warehouse at full capacity (max 100).", "Registration Error", JOptionPane.WARNING_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid integer for minimum level.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        consoleBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Please check the terminal to enter data via Scanner.\nGUI will be active after you finish in terminal.", "Console Scanner", JOptionPane.INFORMATION_MESSAGE);
            new Thread(() -> {
                ConsoleInput console = new ConsoleInput();
                console.runConsole(manager);
                SwingUtilities.invokeLater(() -> {
                    refreshProductCombo();
                    refreshReport();
                    JOptionPane.showMessageDialog(this, "Console input finished. Records updated.", "Scanner Completed", JOptionPane.INFORMATION_MESSAGE);
                });
            }).start();
        });

        clearBtn.addActionListener(e -> {
            nameField.setText("");
            minField.setText("");
            nameField.requestFocus();
        });

        // Wrapper to keep card aligned cleanly at the top
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(card, BorderLayout.NORTH);

        page.add(wrapper, BorderLayout.CENTER);
        return page;
    }

    // ==========================================
    // PAGE 2: STOCK ENTRY
    // ==========================================
    private JPanel createStockEntryPage() {
        JPanel page = new JPanel(new BorderLayout(0, 20));
        page.setOpaque(false);
        page.setBorder(BorderFactory.createEmptyBorder(24, 30, 24, 30));

        page.add(createPageHeader("📥 Stock Entry", "Update quantities, adjust threshold levels, or remove items"), BorderLayout.NORTH);

        CardPanel card = new CardPanel();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel productLbl = createFieldLabel("SELECT PRODUCT");
        productCombo = new JComboBox<>();
        styleComboBox(productCombo);

        JLabel qtyLbl = createFieldLabel("NEW QUANTITY");
        stockQtyField = new JTextField(24);
        styleField(stockQtyField);

        JLabel minLbl = createFieldLabel("NEW MIN LEVEL");
        stockMinField = new JTextField(24);
        styleField(stockMinField);

        gbc.gridx = 0; gbc.gridy = 0; card.add(productLbl, gbc);
        gbc.gridx = 0; gbc.gridy = 1; card.add(productCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2; card.add(qtyLbl, gbc);
        gbc.gridx = 0; gbc.gridy = 3; card.add(stockQtyField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; card.add(minLbl, gbc);
        gbc.gridx = 0; gbc.gridy = 5; card.add(stockMinField, gbc);

        // Buttons row
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        btnRow.setOpaque(false);

        RoundedButton updateBtn = new RoundedButton("✎ Update Stock", COLOR_BTN_TEAL);
        RoundedButton deleteBtn = new RoundedButton("✕ Delete Product", COLOR_BTN_RED);

        btnRow.add(updateBtn);
        btnRow.add(deleteBtn);

        gbc.gridx = 0; gbc.gridy = 6;
        card.add(btnRow, gbc);

        // Auto-fill values on selection change
        productCombo.addActionListener(e -> {
            String selected = (String) productCombo.getSelectedItem();
            if (selected != null) {
                int idx = manager.searchProduct(selected);
                if (idx != -1) {
                    stockQtyField.setText(String.valueOf(manager.getQuantity(idx)));
                    stockMinField.setText(String.valueOf(manager.getMinLevel(idx)));
                }
            }
        });

        // Update Button Action
        updateBtn.addActionListener(e -> {
            String selected = (String) productCombo.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "No product selected.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                int qty = Integer.parseInt(stockQtyField.getText().trim());
                int minLvl = Integer.parseInt(stockMinField.getText().trim());
                if (qty < 0 || minLvl < 0) {
                    JOptionPane.showMessageDialog(this, "Values cannot be negative.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int idx = manager.searchProduct(selected);
                if (idx != -1) {
                    manager.enterStock(idx, qty);
                    manager.updateMinLevel(idx, minLvl);
                    manager.analyzeStock();
                    JOptionPane.showMessageDialog(this, "Stock updated for " + selected
                            + "\nQty: " + qty + " | Min Level: " + minLvl
                            + " | Status: " + manager.getStatus(idx), "Update Successful", JOptionPane.INFORMATION_MESSAGE);
                    refreshReport();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid integers for quantity and min level.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Delete Button Action
        deleteBtn.addActionListener(e -> {
            String selected = (String) productCombo.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "No product selected.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete \"" + selected + "\"?\nThis cannot be undone.",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                int idx = manager.searchProduct(selected);
                boolean deleted = manager.deleteProduct(idx);
                if (deleted) {
                    JOptionPane.showMessageDialog(this, "\"" + selected + "\" has been deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                    stockQtyField.setText("");
                    stockMinField.setText("");
                    refreshProductCombo();
                    refreshReport();
                } else {
                    JOptionPane.showMessageDialog(this, "Could not delete product.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(card, BorderLayout.NORTH);

        page.add(wrapper, BorderLayout.CENTER);
        return page;
    }

    // ==========================================
    // PAGE 3: SEARCH
    // ==========================================
    private JPanel createSearchPage() {
        JPanel page = new JPanel(new BorderLayout(0, 20));
        page.setOpaque(false);
        page.setBorder(BorderFactory.createEmptyBorder(24, 30, 24, 30));

        page.add(createPageHeader("🔍 Search", "Quickly lookup product stock levels and current threshold status"), BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // Search Bar Card
        CardPanel searchCard = new CardPanel();
        searchCard.setLayout(new FlowLayout(FlowLayout.LEFT, 14, 10));

        JLabel searchLbl = createFieldLabel("PRODUCT NAME:");
        JTextField searchField = new JTextField(22);
        styleField(searchField);
        RoundedButton searchBtn = new RoundedButton("Search Product", COLOR_BTN_TEAL);

        searchCard.add(searchLbl);
        searchCard.add(searchField);
        searchCard.add(searchBtn);

        // Result Card
        CardPanel resultCard = new CardPanel();
        resultCard.setLayout(new BorderLayout());
        resultCard.setPreferredSize(new Dimension(650, 220));
        resultCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        JLabel placeholderLbl = new JLabel("Enter a product name above and click Search.", SwingConstants.CENTER);
        placeholderLbl.setFont(getAppFont(Font.ITALIC, 14));
        placeholderLbl.setForeground(COLOR_TEXT_SECONDARY);
        resultCard.add(placeholderLbl, BorderLayout.CENTER);

        // Search Action logic
        Runnable doSearch = () -> {
            String query = searchField.getText().trim();
            if (query.isEmpty()) return;

            manager.analyzeStock();
            int idx = manager.searchProduct(query);
            resultCard.removeAll();

            if (idx != -1) {
                JPanel details = new JPanel(new GridLayout(4, 2, 14, 12));
                details.setOpaque(false);
                details.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

                JLabel nameTitle = createFieldLabel("PRODUCT NAME:");
                JLabel nameVal = new JLabel(manager.getProductName(idx));
                nameVal.setFont(getAppFont(Font.BOLD, 17));
                nameVal.setForeground(COLOR_TEXT_PRIMARY);

                JLabel qtyTitle = createFieldLabel("CURRENT QUANTITY:");
                JLabel qtyVal = new JLabel(manager.getQuantity(idx) + " units");
                qtyVal.setFont(getAppFont(Font.BOLD, 15));
                qtyVal.setForeground(COLOR_TEXT_PRIMARY);

                JLabel minTitle = createFieldLabel("MINIMUM THRESHOLD:");
                JLabel minVal = new JLabel(manager.getMinLevel(idx) + " units");
                minVal.setFont(getAppFont(Font.BOLD, 15));
                minVal.setForeground(COLOR_TEXT_PRIMARY);

                JLabel statusTitle = createFieldLabel("CURRENT STATUS:");
                String status = manager.getStatus(idx);
                JLabel statusVal = new JLabel(status);
                statusVal.setFont(getAppFont(Font.BOLD, 14));

                if ("OUT OF STOCK".equalsIgnoreCase(status)) {
                    statusVal.setForeground(COLOR_PILL_OUT_TEXT);
                    statusVal.setText("●  OUT OF STOCK");
                } else if ("LOW STOCK".equalsIgnoreCase(status)) {
                    statusVal.setForeground(COLOR_PILL_LOW_TEXT);
                    statusVal.setText("▲  LOW STOCK");
                } else {
                    statusVal.setForeground(COLOR_PILL_SUFFICIENT_TEXT);
                    statusVal.setText("✔  SUFFICIENT");
                }

                details.add(nameTitle);   details.add(nameVal);
                details.add(qtyTitle);    details.add(qtyVal);
                details.add(minTitle);    details.add(minVal);
                details.add(statusTitle); details.add(statusVal);

                resultCard.add(details, BorderLayout.CENTER);
            } else {
                JPanel errPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 40));
                errPanel.setOpaque(false);
                JLabel errLbl = new JLabel("✕  Product \"" + query + "\" not found in inventory.");
                errLbl.setFont(getAppFont(Font.BOLD, 15));
                errLbl.setForeground(COLOR_BTN_RED);
                errPanel.add(errLbl);
                resultCard.add(errPanel, BorderLayout.CENTER);
            }
            resultCard.revalidate();
            resultCard.repaint();
        };

        searchBtn.addActionListener(e -> doSearch.run());
        searchField.addActionListener(e -> doSearch.run());

        content.add(searchCard);
        content.add(Box.createVerticalStrut(16));
        content.add(resultCard);

        page.add(content, BorderLayout.CENTER);
        return page;
    }

    // ==========================================
    // PAGE 4: LOW STOCK ALERTS
    // ==========================================
    private JPanel createAlertsPage() {
        JPanel page = new JPanel(new BorderLayout(0, 16));
        page.setOpaque(false);
        page.setBorder(BorderFactory.createEmptyBorder(24, 30, 24, 30));

        page.add(createPageHeader("⚠️ Low Stock Alerts", "Items requiring immediate restocking based on minimum thresholds"), BorderLayout.NORTH);

        // Top Action Row
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        RoundedButton checkBtn = new RoundedButton("⚠️ Check Alerts", COLOR_BTN_RED);
        alertsSummaryLbl = new JLabel("Click 'Check Alerts' to scan for critical stock");
        alertsSummaryLbl.setFont(getAppFont(Font.PLAIN, 13));
        alertsSummaryLbl.setForeground(COLOR_TEXT_SECONDARY);

        topBar.add(checkBtn, BorderLayout.WEST);
        topBar.add(alertsSummaryLbl, BorderLayout.EAST);

        // Alerts Table
        String[] cols = {"Product Name", "Current Qty", "Status", "Deficit"};
        alertsTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        alertsTable = new JTable(alertsTableModel);
        JScrollPane scrollPane = styleTable(alertsTable);

        // Status column renderer
        alertsTable.getColumnModel().getColumn(2).setCellRenderer(new StatusBadgeRenderer());

        checkBtn.addActionListener(e -> {
            manager.analyzeStock();
            String[][] alerts = manager.detectLowStock();
            alertsTableModel.setRowCount(0);

            if (alerts.length > 0) {
                for (String[] row : alerts) {
                    alertsTableModel.addRow(row);
                }
                alertsSummaryLbl.setText("Found " + alerts.length + " item(s) requiring immediate attention!");
                alertsSummaryLbl.setForeground(COLOR_BTN_RED);
                JOptionPane.showMessageDialog(this, "WARNING: " + alerts.length + " items are low or out of stock!", "Low Stock Alert", JOptionPane.WARNING_MESSAGE);
            } else {
                alertsSummaryLbl.setText("All warehouse products are sufficiently stocked.");
                alertsSummaryLbl.setForeground(COLOR_PILL_SUFFICIENT_TEXT);
                JOptionPane.showMessageDialog(this, "All products are sufficiently stocked.", "Inventory Safe", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        page.add(topBar, BorderLayout.NORTH);
        page.add(scrollPane, BorderLayout.CENTER);
        return page;
    }

    // ==========================================
    // PAGE 5: STOCK REPORT
    // ==========================================
    private JPanel createReportPage() {
        JPanel page = new JPanel(new BorderLayout(0, 16));
        page.setOpaque(false);
        page.setBorder(BorderFactory.createEmptyBorder(24, 30, 24, 30));

        // Header Panel + 3 Summary Cards
        JPanel topContainer = new JPanel();
        topContainer.setOpaque(false);
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));

        topContainer.add(createPageHeader("📊 Stock Report", "Complete overview of all warehouse products and status summary"));

        JPanel cardsGrid = new JPanel(new GridLayout(1, 3, 16, 0));
        cardsGrid.setOpaque(false);
        cardsGrid.setPreferredSize(new Dimension(0, 85));

        // Summary cards with colored left strip
        cardTotal = new SummaryCard("Total Products", "0", COLOR_TEAL_PRIMARY);
        cardLow   = new SummaryCard("Low Stock Items", "0", COLOR_BTN_AMBER);
        cardOut   = new SummaryCard("Out of Stock Items", "0", COLOR_BTN_RED);

        cardsGrid.add(cardTotal);
        cardsGrid.add(cardLow);
        cardsGrid.add(cardOut);

        topContainer.add(cardsGrid);
        topContainer.add(Box.createVerticalStrut(16));

        // Report Table
        String[] cols = {"Name", "Quantity", "Min Level", "Status"};
        reportTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        reportTable = new JTable(reportTableModel);
        JScrollPane scrollPane = styleTable(reportTable);

        // Status column renderer
        reportTable.getColumnModel().getColumn(3).setCellRenderer(new StatusBadgeRenderer());

        // Bottom Action Bar
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        bottomBar.setOpaque(false);

        RoundedButton refreshBtn = new RoundedButton("↻ Refresh Report", COLOR_BTN_SLATE);
        refreshBtn.addActionListener(e -> refreshReport());
        bottomBar.add(refreshBtn);

        page.add(topContainer, BorderLayout.NORTH);
        page.add(scrollPane, BorderLayout.CENTER);
        page.add(bottomBar, BorderLayout.SOUTH);
        return page;
    }

    // ==========================================
    // DATA SYNC & HELPER METHODS
    // ==========================================
    private void refreshProductCombo() {
        if (productCombo == null) return;
        productCombo.removeAllItems();
        for (int i = 0; i < manager.getCount(); i++) {
            productCombo.addItem(manager.getProductName(i));
        }
        if (manager.getCount() > 0 && stockQtyField != null && stockMinField != null) {
            stockQtyField.setText(String.valueOf(manager.getQuantity(0)));
            stockMinField.setText(String.valueOf(manager.getMinLevel(0)));
        } else if (stockQtyField != null && stockMinField != null) {
            stockQtyField.setText("");
            stockMinField.setText("");
        }
    }

    private void refreshReport() {
        if (reportTableModel == null) return;
        manager.analyzeStock();
        reportTableModel.setRowCount(0);

        int total = manager.getCount();
        int low = 0;
        int out = 0;

        for (int i = 0; i < total; i++) {
            String name = manager.getProductName(i);
            int qty = manager.getQuantity(i);
            int min = manager.getMinLevel(i);
            String status = manager.getStatus(i);

            if ("LOW STOCK".equals(status)) low++;
            if ("OUT OF STOCK".equals(status)) out++;

            reportTableModel.addRow(new Object[]{name, qty, min, status});
        }

        if (cardTotal != null) cardTotal.setValue(String.valueOf(total));
        if (cardLow != null)   cardLow.setValue(String.valueOf(low));
        if (cardOut != null)   cardOut.setValue(String.valueOf(out));
    }

    // ==========================================
    // UI COMPONENT FACTORY & STYLING HELPERS
    // ==========================================
    private JPanel createPageHeader(String titleWithEmoji, String subtitle) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        JLabel titleLbl = new JLabel(titleWithEmoji);
        titleLbl.setFont(getAppFont(Font.BOLD, 22));
        titleLbl.setForeground(COLOR_TEXT_PRIMARY);

        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(getAppFont(Font.PLAIN, 12));
        subLbl.setForeground(COLOR_TEXT_SECONDARY);

        // Short 60px teal underline
        JPanel underline = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(COLOR_TEAL_PRIMARY);
                g2.fillRect(0, 0, 60, 3);
                g2.dispose();
            }
        };
        underline.setOpaque(false);
        underline.setPreferredSize(new Dimension(60, 3));
        underline.setMaximumSize(new Dimension(60, 3));
        underline.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        subLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(titleLbl);
        panel.add(Box.createVerticalStrut(4));
        panel.add(subLbl);
        panel.add(Box.createVerticalStrut(8));
        panel.add(underline);
        return panel;
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text.toUpperCase());
        label.setFont(getAppFont(Font.BOLD, 11));
        label.setForeground(COLOR_TEXT_SECONDARY);
        return label;
    }

    private void styleField(JTextField field) {
        field.setBackground(COLOR_INPUT_BG);
        field.setForeground(COLOR_TEXT_PRIMARY);
        field.setCaretColor(COLOR_TEAL_PRIMARY);
        field.setFont(getAppFont(Font.PLAIN, 13));

        Border normalBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_INPUT_BORDER, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        );
        Border focusedBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_TEAL_PRIMARY, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        );
        field.setBorder(normalBorder);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(focusedBorder);
            }
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(normalBorder);
            }
        });
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setBackground(Color.WHITE);
        combo.setForeground(COLOR_TEXT_PRIMARY);
        combo.setFont(getAppFont(Font.PLAIN, 13));
        combo.setBorder(BorderFactory.createLineBorder(COLOR_INPUT_BORDER, 1));
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? COLOR_TEAL_LIGHT : Color.WHITE);
                setForeground(isSelected ? COLOR_TEAL_DARK : COLOR_TEXT_PRIMARY);
                setFont(getAppFont(isSelected ? Font.BOLD : Font.PLAIN, 13));
                setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12));
                return this;
            }
        });
    }

    private JScrollPane styleTable(JTable table) {
        table.setBackground(Color.WHITE);
        table.setForeground(COLOR_TEXT_PRIMARY);
        table.setFont(getAppFont(Font.PLAIN, 13));
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(COLOR_TEAL_LIGHT);
        table.setSelectionForeground(COLOR_TEXT_PRIMARY);

        // Header Styling with 2px teal bottom border
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 38));
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSel, boolean hasFoc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(tbl, value, isSel, hasFoc, row, col);
                lbl.setBackground(COLOR_TABLE_HEADER_BG);
                lbl.setForeground(COLOR_TABLE_HEADER_TXT);
                lbl.setFont(getAppFont(Font.BOLD, 12));
                lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_TEAL_PRIMARY),
                    BorderFactory.createEmptyBorder(0, 14, 0, 14)
                ));
                return lbl;
            }
        });

        // Alternating row renderer for general cells
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSel, boolean hasFoc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(tbl, value, isSel, hasFoc, row, col);
                Color bg = isSel ? COLOR_TEAL_LIGHT : (row % 2 == 0 ? Color.WHITE : COLOR_TABLE_ROW_ALT);
                lbl.setBackground(bg);
                lbl.setForeground(COLOR_TEXT_PRIMARY);
                lbl.setFont(getAppFont(Font.PLAIN, 13));
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
                return lbl;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        return scrollPane;
    }

    private static Font getAppFont(int style, int size) {
        Font font = new Font("Segoe UI", style, size);
        if (!font.getFamily().equalsIgnoreCase("Segoe UI")) {
            font = new Font("Arial", style, size);
        }
        return font;
    }

    // ==========================================
    // CUSTOM REUSABLE INNER COMPONENTS
    // ==========================================

    /**
     * RoundedButton - Custom JButton with rounded rectangle painting,
     * smooth hover brightness transition, hand cursor, and support for amber dark text.
     */
    static class RoundedButton extends JButton {
        private final Color normalColor;
        private final Color hoverColor;
        private boolean hovered = false;
        private final int arc = 10;

        public RoundedButton(String text, Color bg) {
            super(text);
            this.normalColor = bg;
            this.hoverColor = new Color(
                Math.max(0, Math.min(255, bg.getRed() + (bg.getRed() > 200 ? -15 : 20))),
                Math.max(0, Math.min(255, bg.getGreen() + (bg.getGreen() > 200 ? -15 : 20))),
                Math.max(0, Math.min(255, bg.getBlue() + (bg.getBlue() > 200 ? -15 : 20)))
            );
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);

            // Dark text on amber button, white on all others
            if (bg.equals(COLOR_BTN_AMBER)) {
                setForeground(COLOR_AMBER_TEXT);
            } else {
                setForeground(Color.WHITE);
            }

            setFont(getAppFont(Font.BOLD, 13));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(hovered ? hoverColor : normalColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

            FontMetrics fm = g2.getFontMetrics(getFont());
            int textX = (getWidth() - fm.stringWidth(getText())) / 2;
            int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.setFont(getFont());
            g2.setColor(getForeground());
            g2.drawString(getText(), textX, textY);
            g2.dispose();
        }
    }

    /**
     * SidebarButton - Left sidebar navigation button with active teal background (#CCFBF1),
     * teal text (#0F766E), hover effects (#F1F5F9), and a 4px teal (#0D9488) accent indicator.
     */
    static class SidebarButton extends JButton {
        private boolean active = false;
        private boolean hovered = false;
        private final String iconText;
        private final String titleText;

        public SidebarButton(String icon, String title) {
            this.iconText = icon;
            this.titleText = title;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(200, 42));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
            });
        }

        public void setActive(boolean active) {
            this.active = active;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            if (active) {
                // Light teal active background
                g2.setColor(COLOR_TEAL_LIGHT);
                g2.fillRoundRect(8, 2, w - 16, h - 4, 8, 8);
                // 4px teal accent indicator bar on left
                g2.setColor(COLOR_TEAL_PRIMARY);
                g2.fillRoundRect(8, 4, 4, h - 8, 3, 3);
            } else if (hovered) {
                g2.setColor(COLOR_NAV_HOVER);
                g2.fillRoundRect(8, 2, w - 16, h - 4, 8, 8);
            }

            // Draw icon + title text
            g2.setFont(getAppFont(Font.PLAIN, 15));
            g2.setColor(active ? COLOR_TEAL_DARK : COLOR_NAV_TEXT);
            g2.drawString(iconText, 22, h / 2 + 5);

            g2.setFont(getAppFont(active ? Font.BOLD : Font.PLAIN, 13));
            g2.setColor(active ? COLOR_TEAL_DARK : COLOR_NAV_TEXT);
            g2.drawString(titleText, 48, h / 2 + 5);

            g2.dispose();
        }
    }

    /**
     * CardPanel - White container panel with 1px border (#E2E8F0) and rounded corners (arc 14).
     */
    static class CardPanel extends JPanel {
        public CardPanel() {
            setOpaque(false);
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(COLOR_CARD);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            g2.setColor(COLOR_BORDER);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * SummaryCard - White metric card with 5px colored strip on LEFT edge,
     * big colored number, and small grey label.
     */
    static class SummaryCard extends JPanel {
        private final JLabel valueLabel;
        private final Color accentColor;

        public SummaryCard(String title, String initialValue, Color accentColor) {
            this.accentColor = accentColor;
            setOpaque(false);
            setLayout(new BorderLayout(0, 6));
            setBorder(BorderFactory.createEmptyBorder(16, 22, 16, 20));

            JLabel titleLbl = new JLabel(title.toUpperCase());
            titleLbl.setFont(getAppFont(Font.BOLD, 11));
            titleLbl.setForeground(COLOR_TEXT_SECONDARY);

            valueLabel = new JLabel(initialValue);
            valueLabel.setFont(getAppFont(Font.BOLD, 30));
            valueLabel.setForeground(accentColor);

            add(titleLbl, BorderLayout.NORTH);
            add(valueLabel, BorderLayout.CENTER);
        }

        public void setValue(String val) {
            valueLabel.setText(val);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // White card background
            g2.setColor(COLOR_CARD);
            g2.fillRoundRect(0, 0, w - 1, h - 1, 12, 12);
            // 1px border
            g2.setColor(COLOR_BORDER);
            g2.drawRoundRect(0, 0, w - 1, h - 1, 12, 12);

            // 5px colored strip on the LEFT edge
            g2.setColor(accentColor);
            g2.fillRoundRect(0, 0, 6, h - 1, 12, 12);
            g2.fillRect(3, 0, 3, h - 1); // square off right side of the strip

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * StatusBadgeRenderer - Table cell renderer that displays soft rounded pill badges
     * with custom background and text color for each stock status.
     */
    static class StatusBadgeRenderer extends DefaultTableCellRenderer {
        private String currentStatus = "";
        private boolean isRowSelected = false;
        private int currentRow = 0;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            this.currentStatus = value == null ? "" : value.toString();
            this.isRowSelected = isSelected;
            this.currentRow = row;
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(getAppFont(Font.BOLD, 12));
            setOpaque(false);
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Background of cell
            Color cellBg = isRowSelected ? COLOR_TEAL_LIGHT : (currentRow % 2 == 0 ? Color.WHITE : COLOR_TABLE_ROW_ALT);
            g2.setColor(cellBg);
            g2.fillRect(0, 0, w, h);

            Color pillBg;
            Color pillText;
            String textToDraw;

            if ("OUT OF STOCK".equalsIgnoreCase(currentStatus)) {
                pillBg = COLOR_PILL_OUT_BG;
                pillText = COLOR_PILL_OUT_TEXT;
                textToDraw = "● OUT OF STOCK";
            } else if ("LOW STOCK".equalsIgnoreCase(currentStatus)) {
                pillBg = COLOR_PILL_LOW_BG;
                pillText = COLOR_PILL_LOW_TEXT;
                textToDraw = "▲ LOW STOCK";
            } else if ("SUFFICIENT".equalsIgnoreCase(currentStatus)) {
                pillBg = COLOR_PILL_SUFFICIENT_BG;
                pillText = COLOR_PILL_SUFFICIENT_TEXT;
                textToDraw = "✔ SUFFICIENT";
            } else {
                pillBg = COLOR_BORDER;
                pillText = COLOR_TEXT_PRIMARY;
                textToDraw = currentStatus;
            }

            // Draw pill badge centered in cell
            FontMetrics fm = g2.getFontMetrics(getFont());
            int pillWidth = fm.stringWidth(textToDraw) + 24;
            int pillHeight = 24;
            int pillX = (w - pillWidth) / 2;
            int pillY = (h - pillHeight) / 2;

            g2.setColor(pillBg);
            g2.fillRoundRect(pillX, pillY, pillWidth, pillHeight, 12, 12);

            g2.setColor(pillText);
            g2.setFont(getFont());
            int textX = pillX + 12;
            int textY = pillY + ((pillHeight - fm.getHeight()) / 2) + fm.getAscent();
            g2.drawString(textToDraw, textX, textY);

            g2.dispose();
        }
    }
}
