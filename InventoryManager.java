public class InventoryManager {
    // 1. ARRAYS: store product names, quantities, and minimum stock levels
    private String[] productNames;
    private int[] quantities;
    private int[] minLevels;
    private String[] statuses; // Additional parallel array for status
    private int count;
    private static final int MAX_SIZE = 100;

    public InventoryManager() {
        productNames = new String[MAX_SIZE];
        quantities = new int[MAX_SIZE];
        minLevels = new int[MAX_SIZE];
        statuses = new String[MAX_SIZE];
        count = 0;
    }

    // Module 1: Product Registration
    // Registers a new product if array is not full and name is not duplicate
    public boolean registerProduct(String name, int minLevel) {
        if (count < MAX_SIZE) {
            // Check for duplicate names
            if (searchProduct(name) != -1) {
                return false; // duplicate found
            }
            productNames[count] = name;
            minLevels[count] = minLevel;
            quantities[count] = 0; // Default quantity
            statuses[count] = "UNKNOWN";
            count++;
            return true;
        }
        return false;
    }

    // Module 2: Stock Entry
    // Updates the stock quantity for a specific product index
    public void enterStock(int index, int quantity) {
        if (index >= 0 && index < count) {
            quantities[index] = quantity;
        }
    }

    // Module 2b: Update Min Level
    // Updates the minimum stock level for a specific product index
    public void updateMinLevel(int index, int minLevel) {
        if (index >= 0 && index < count) {
            minLevels[index] = minLevel;
        }
    }

    // Module 2c: Delete Product
    // Removes a product at the given index by shifting all subsequent elements
    // one position to the left in all three parallel arrays, then decrementing count.
    public boolean deleteProduct(int index) {
        if (index < 0 || index >= count) {
            return false; // invalid index
        }
        // Shift every element after 'index' one slot to the left
        for (int i = index; i < count - 1; i++) {
            productNames[i] = productNames[i + 1];
            quantities[i]   = quantities[i + 1];
            minLevels[i]    = minLevels[i + 1];
            statuses[i]     = statuses[i + 1];
        }
        // Clear the last slot and shrink count
        productNames[count - 1] = null;
        quantities[count - 1]   = 0;
        minLevels[count - 1]    = 0;
        statuses[count - 1]     = null;
        count--;
        return true;
    }

    // Module 3: Stock Analysis
    // Compares each product's quantity with its minimum level using if-else
    // and assigns a status: "OUT OF STOCK", "LOW STOCK", "SUFFICIENT"
    public void analyzeStock() {
        // 3. LOOPS: use for loops to process all products
        for (int i = 0; i < count; i++) {
            // 4. IF-ELSE: compare quantity against minimum level
            if (quantities[i] == 0) {
                statuses[i] = "OUT OF STOCK";
            } else if (quantities[i] < minLevels[i]) {
                statuses[i] = "LOW STOCK";
            } else {
                statuses[i] = "SUFFICIENT";
            }
        }
    }

    // Module 4: Low-Stock Detection
    // Loops through products and collects only low and out-of-stock items, 
    // including how many units short each one is.
    public String[][] detectLowStock() {
        // Count how many items match the criteria first
        int lowCount = 0;
        for (int i = 0; i < count; i++) {
            if (statuses[i].equals("OUT OF STOCK") || statuses[i].equals("LOW STOCK")) {
                lowCount++;
            }
        }

        // Collect the low stock data in a 2D array
        String[][] alerts = new String[lowCount][4];
        int alertIndex = 0;
        for (int i = 0; i < count; i++) {
            if (statuses[i].equals("OUT OF STOCK") || statuses[i].equals("LOW STOCK")) {
                alerts[alertIndex][0] = productNames[i];
                alerts[alertIndex][1] = String.valueOf(quantities[i]);
                alerts[alertIndex][2] = statuses[i];
                
                int shortUnits = minLevels[i] - quantities[i];
                alerts[alertIndex][3] = String.valueOf(shortUnits) + " units short";
                alertIndex++;
            }
        }
        return alerts;
    }

    // Module 5: Stock Report
    // Produces a full formatted report of all products with name, quantity, minimum, status, 
    // plus a summary (total products, low-stock count, out-of-stock count)
    public String generateReport() {
        analyzeStock(); // ensure statuses are fresh
        
        StringBuilder report = new StringBuilder();
        report.append("--- INVENTORY STOCK REPORT ---\n");
        int outOfStockCount = 0;
        int lowStockCount = 0;

        for (int i = 0; i < count; i++) {
            report.append("Name: ").append(productNames[i])
                  .append(" | Qty: ").append(quantities[i])
                  .append(" | Min: ").append(minLevels[i])
                  .append(" | Status: ").append(statuses[i]).append("\n");

            if (statuses[i].equals("OUT OF STOCK")) {
                outOfStockCount++;
            } else if (statuses[i].equals("LOW STOCK")) {
                lowStockCount++;
            }
        }
        
        report.append("\n--- SUMMARY ---\n");
        report.append("Total Products: ").append(count).append("\n");
        report.append("Low Stock Items: ").append(lowStockCount).append("\n");
        report.append("Out of Stock Items: ").append(outOfStockCount).append("\n");

        return report.toString();
    }

    // Module 6: SEARCHING
    // Linear search by product name (case-insensitive) that returns the index or -1
    public int searchProduct(String name) {
        for (int i = 0; i < count; i++) {
            if (productNames[i].equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

    // Basic Getters
    public int getCount() { return count; }
    public String getProductName(int i) { return productNames[i]; }
    public int getQuantity(int i) { return quantities[i]; }
    public int getMinLevel(int i) { return minLevels[i]; }
    public String getStatus(int i) { return statuses[i]; }
}
