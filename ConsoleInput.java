import java.util.Scanner;

public class ConsoleInput {
    // 2. SCANNER: Scanner-based input for entering inventory data
    public void runConsole(InventoryManager manager) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n=== CONSOLE INVENTORY INPUT ===");
        System.out.print("How many products would you like to register? ");
        int num = 0;
        try {
            num = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number. Returning to main menu.");
            return;
        }

        for (int i = 0; i < num; i++) {
            System.out.println("\nProduct " + (i + 1) + " of " + num);
            System.out.print("Enter product name: ");
            String name = scanner.nextLine();
            
            System.out.print("Enter minimum stock level for " + name + ": ");
            int minLevel = 0;
            try {
                minLevel = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number, defaulting minimum level to 10.");
                minLevel = 10;
            }

            boolean added = manager.registerProduct(name, minLevel);
            if (added) {
                System.out.println("Success: " + name + " registered.");
            } else {
                System.out.println("Failed: " + name + " could not be registered (duplicate or array full).");
            }
        }
        
        System.out.println("\n=== Console input completed ===");
    }
}
