# Restaurant Order Management System

This is a Java-based GUI application that simulates a simple restaurant order management system. Users can add menu items to their order, specify quantities, view order summaries, and place orders which are saved to a MySQL database.

## Features

- Java AWT GUI for user interaction
- Add predefined menu items (e.g., Burger, Pizza) to order
- Specify quantity for each item
- Generate and display order receipt
- Save order details to a MySQL database

## Technologies Used

- Java AWT
- JDBC (Java Database Connectivity)
- MySQL

## Database Setup

1. Create a MySQL database named `restaurant_order`.
2. Use the following SQL to create the `orders` table:

```sql
CREATE DATABASE restaurant_order;

USE restaurant_order;

CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    item_name VARCHAR(255),
    quantity INT,
    total_price DOUBLE
);
