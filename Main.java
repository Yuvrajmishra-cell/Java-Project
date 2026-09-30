import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Initialize the Inventory Manager
        InventoryManager manager = new InventoryManager();

        // Pre-load 5 sample products so demo works immediately
        // Product 1
        manager.registerProduct("Laptop", 10);
        manager.enterStock(0, 15); // SUFFICIENT
        
        // Product 2
        manager.registerProduct("Mouse", 20);
        manager.enterStock(1, 5);  // LOW STOCK
        
        // Product 3
        manager.registerProduct("Keyboard", 15);
        manager.enterStock(2, 0);  // OUT OF STOCK
        
        // Product 4
        manager.registerProduct("Monitor", 5);
        manager.enterStock(3, 8);  // SUFFICIENT
        
        // Product 5
        manager.registerProduct("Printer", 3);
        manager.enterStock(4, 0);  // OUT OF STOCK

        // Run analysis on startup to populate statuses
        manager.analyzeStock();
        
        // Print the initial report to console just to show it works
        System.out.println(manager.generateReport());
        System.out.println("Starting GUI...");

        // Launch GUI safely on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            InventoryGUI gui = new InventoryGUI(manager);
            gui.setVisible(true);
        });
    }
}
