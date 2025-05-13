import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

public class RestaurantOrderFrame extends Frame {
    private ArrayList<MenuItem> menu = new ArrayList<>();
    private Order currentOrder = new Order();

    private TextArea orderTextArea;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/restaurant_order";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public RestaurantOrderFrame() {
        setTitle("Restaurant Order Management System");
        setSize(400, 400);
        setLayout(new BorderLayout());

        initMenu();
        initComponents();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                dispose();
            }
        });
    }

    private void initMenu() {
        // Sample menu items
        menu.add(new MenuItem("Burger", 8.99));
        menu.add(new MenuItem("Pizza", 12.99));
        // Add more menu items as needed
    }

    private void initComponents() {
        orderTextArea = new TextArea();
        orderTextArea.setEditable(false);
        add(orderTextArea, BorderLayout.CENTER);

        Button addToOrderButton = new Button("Add to Order");
        addToOrderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAddToOrderDialog();
            }
        });

        Button placeOrderButton = new Button("Place Order");
        placeOrderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                placeOrder();
            }
        });

        Panel buttonPanel = new Panel();
        buttonPanel.add(addToOrderButton);
        buttonPanel.add(placeOrderButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void showAddToOrderDialog() {
        Frame dialog = new Frame("Add to Order");
        dialog.setSize(300, 150);
        dialog.setLayout(new FlowLayout());
        dialog.setLocationRelativeTo(this);

        Label menuItemLabel = new Label("Select Menu Item:");
        Choice menuItemsChoice = new Choice();
        for (MenuItem menuItem : menu) {
            menuItemsChoice.add(menuItem.getName());
        }

        Label quantityLabel = new Label("Enter Quantity:");
        TextField quantityTextField = new TextField(10);

        Button addButton = new Button("Add");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedMenuItem = menuItemsChoice.getSelectedItem();
                if (selectedMenuItem != null) {
                    MenuItem menuItem = getMenuByName(selectedMenuItem);
                    try {
                        int quantity = Integer.parseInt(quantityTextField.getText());
                        currentOrder.addItem(new OrderItem(menuItem, quantity));
                        updateOrderTextArea();
                        dialog.dispose();
                    } catch (NumberFormatException ex) {
                        showErrorDialog("Invalid quantity. Please enter a number.");
                    }
                }
            }
        });

        dialog.add(menuItemLabel);
        dialog.add(menuItemsChoice);
        dialog.add(quantityLabel);
        dialog.add(quantityTextField);
        dialog.add(addButton);

        dialog.setVisible(true);
    }

    private MenuItem getMenuByName(String name) {
        for (MenuItem menuItem : menu) {
            if (menuItem.getName().equals(name)) {
                return menuItem;
            }
        }
        return null;
    }

    private void placeOrder() {
        saveOrderToDatabase();
        updateOrderTextArea();
        generateReceipt();

        // Additional code for displaying order confirmation, etc.
    }

    private void saveOrderToDatabase() {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            String insertQuery = "INSERT INTO orders (item_name, quantity, total_price) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                for (OrderItem item : currentOrder.getOrderItems()) {
                    preparedStatement.setString(1, item.getMenuItem().getName());
                    preparedStatement.setInt(2, item.getQuantity());
                    preparedStatement.setDouble(3, item.getTotal());
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorDialog("Error saving order to the database.");
        }
    }

    private void generateReceipt() {
        StringBuilder receipt = new StringBuilder("Receipt:\n");
        for (OrderItem item : currentOrder.getOrderItems()) {
            receipt.append(item.getQuantity()).append(" x ").append(item.getMenuItem().getName())
                    .append(" - $").append(item.getTotal()).append("\n");
        }
        receipt.append("Total: $").append(currentOrder.getTotal());

        // Display receipt in a dialog box
        showReceiptDialog(receipt.toString());

        // Print receipt on the console
        System.out.println(receipt.toString());
    }

    private void showReceiptDialog(String receipt) {
        Frame dialog = new Frame("Receipt");
        dialog.setSize(300, 150);
        dialog.setLayout(new FlowLayout());
        dialog.setLocationRelativeTo(this);

        Label receiptLabel = new Label(receipt);

        Button closeButton = new Button("Close");
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.add(receiptLabel);
        dialog.add(closeButton);

        dialog.setVisible(true);
    }

    private void updateOrderTextArea() {
        orderTextArea.setText(currentOrder.getOrderDetails());
    }

    private void showErrorDialog(String message) {
        Frame dialog = new Frame("Error");
        dialog.setSize(200, 100);
        dialog.setLayout(new FlowLayout());
        dialog.setLocationRelativeTo(this);

        Label errorLabel = new Label(message);

        Button closeButton = new Button("Close");
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.add(errorLabel);
        dialog.add(closeButton);

        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RestaurantOrderFrame().setVisible(true);
            }
        });
    }

    private static class MenuItem {
        private String name;
        private double price;

        public MenuItem(String name, double price) {
            this.name = name;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }
    }

    private static class OrderItem {
        private MenuItem menuItem;
        private int quantity;

        public OrderItem(MenuItem menuItem, int quantity) {
            this.menuItem = menuItem;
            this.quantity = quantity;
        }

        public MenuItem getMenuItem() {
            return menuItem;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getTotal() {
            return menuItem.getPrice() * quantity;
        }
    }

    private static class Order {
        private ArrayList<OrderItem> orderItems = new ArrayList<>();

        public void addItem(OrderItem item) {
            orderItems.add(item);
        }

        public ArrayList<OrderItem> getOrderItems() {
            return orderItems;
        }

        public double getTotal() {
            double total = 0;
            for (OrderItem item : orderItems) {
                total += item.getTotal();
            }
            return total;
        }

        public String getOrderDetails() {
            StringBuilder details = new StringBuilder("Order Details:\n");
            for (OrderItem item : orderItems) {
                details.append(item.getQuantity()).append(" x ").append(item.getMenuItem().getName())
                        .append(" - $").append(item.getTotal()).append("\n");
            }
            details.append("Total: $").append(getTotal());
            return details.toString();
        }
    }
}