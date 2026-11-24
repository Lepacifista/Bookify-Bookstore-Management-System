# Bookify – Bookstore Management System

A Java Swing-based Bookstore Management System developed using Java and MySQL. The application helps manage bookstore operations, including books, customers, suppliers, sales and reports.

## Project Overview

Bookify is a desktop application designed to simplify bookstore management through a graphical user interface. It provides tools for managing inventory, processing customer purchases and monitoring sales activities.

## Features

- **User Authentication:** Login system for administrators.
- **Dashboard:** Displays bookstore statistics, including books, customers, sales and revenue.
- **Book Management:** Add, update, delete, search, sort and restock books.
- **Customer Management:** Manage customer information.
- **Supplier Management:** Manage supplier records.
- **Sales Management:** Process sales, manage shopping carts and update stock quantities.
- **Receipt Generation:** Generate and print sales receipts.
- **Reports:** View sales history, filter sales by date and monitor low-stock books.

## Technologies Used

- Java
- Java Swing
- MySQL
- JDBC (Java Database Connectivity)
- IntelliJ IDEA

## Project Structure

```text
Bookify/
├── src/
│   └── bookstore/
│       ├── dao/
│       ├── database/
│       ├── gui/
│       ├── main/
│       └── model/
├── bookstore_db.sql
├── .gitignore
└── README.md
```

## Requirements

Before running the application, ensure you have:

- Java Development Kit (JDK).
- MySQL Server.
- MySQL Connector/J.
- IntelliJ IDEA or another Java IDE.

## Database Setup

1. Start your MySQL server.
2. Open MySQL Workbench.
3. Open the `bookstore_db.sql` file.
4. Execute the SQL script to create the database and its tables.
5. Verify that the `bookstore_db` database has been created successfully.

**Note:** The provided SQL file contains the database structure only. It does not include the original administrator account or sample records.

## Database Connection

Open:

`src/bookstore/database/DatabaseConnection.java`

Configure the MySQL connection details on your local computer. Replace the placeholder database password with your own MySQL password.

Do not commit personal credentials to the public repository.

## Administrator Account

After creating the database, create an administrator account using your own credentials. The application uses the `users` table for authentication.

Example SQL (assuming the user ID is auto-incremented):

```sql
INSERT INTO users (username, password, role)
VALUES ('admin', 'your_admin_password', 'Administrator');
```

Use a password appropriate for your local coursework setup. This example reflects the project's current authentication implementation and is not intended for production security.

## Running the Application

1. Clone or download this repository.
2. Open the project in IntelliJ IDEA.
3. Add MySQL Connector/J to the project libraries if it is not already configured.
4. Set up the MySQL database.
5. Configure the database connection.
6. Run `Main.java` from `src/bookstore/main/`.

## Testing

The application has undergone functional testing of its major modules, including authentication, book management, customer management, supplier management, sales processing, receipt generation, dashboard statistics and reports.

## Project Purpose

This project was developed for academic purposes to demonstrate the application of Java programming, graphical user interface development, database connectivity and database management in solving practical bookstore management challenges.