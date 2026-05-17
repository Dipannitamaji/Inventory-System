import java.sql.*;
import java.util.Scanner;

public class InventorySystem {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/InventoryDB";
    private static final String USER = "root";
    private static final String PASS = "MYSQL02"; 

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Scanner scanner = new Scanner(System.in)) {
             
            boolean running = true;
            while (running) {
                System.out.println("\n=== WAREHOUSE INVENTORY SYSTEM ===");
                System.out.println("1. Add Product");
                System.out.println("2. Update Stock Level");
                System.out.println("3. Run Low Stock Report");
                System.out.println("4. Exit");
                System.out.print("Action: ");
                
                int choice = scanner.nextInt();
                scanner.nextLine();
                
                switch (choice) {
                    case 1:
                        addProduct(conn, scanner);
                        break;
                    case 2:
                        updateStock(conn, scanner);
                        break;
                    case 3:
                        checkLowStock(conn);
                        break;
                    case 4:
                        System.out.println("Shutting down...");
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid input.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database Connection Failed.");
        }
    }

    private static void addProduct(Connection conn, Scanner scanner) {
        System.out.print("Product Name: ");
        String name = scanner.nextLine();
        System.out.print("Initial Quantity: ");
        int qty = scanner.nextInt();
        System.out.print("Unit Price: ");
        double price = scanner.nextDouble();

        String sql = "INSERT INTO Inventory (ProductName, Quantity, Price) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, qty);
            pstmt.setDouble(3, price);
            pstmt.executeUpdate();
            System.out.println("SUCCESS: Product added to catalog.");
        } catch (SQLException e) {
            System.out.println("ERROR: Failed to add product.");
        }
    }

    private static void updateStock(Connection conn, Scanner scanner) {
        System.out.print("Enter Product ID: ");
        int id = scanner.nextInt();
        System.out.print("Enter New Quantity: ");
        int qty = scanner.nextInt();

        String sql = "UPDATE Inventory SET Quantity = ? WHERE ProductID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, qty);
            pstmt.setInt(2, id);
            int rows = pstmt.executeUpdate();
            if (rows > 0) System.out.println("SUCCESS: Stock updated.");
            else System.out.println("ERROR: Product ID not found.");
        } catch (SQLException e) {
            System.out.println("ERROR: Failed to update stock.");
        }
    }

    private static void checkLowStock(Connection conn) {
        String sql = "SELECT ProductName, Quantity FROM Inventory WHERE Quantity < 5";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
             
            System.out.println("\n--- CRITICAL LOW STOCK REPORT ---");
            boolean alert = false;
            while (rs.next()) {
                alert = true;
                System.out.println("WARNING: " + rs.getString("ProductName") + " only has " + rs.getInt("Quantity") + " units left.");
            }
            if (!alert) System.out.println("All inventory levels are stable.");
        } catch (SQLException e) {
            System.out.println("ERROR: Could not run report.");
        }
    }
}