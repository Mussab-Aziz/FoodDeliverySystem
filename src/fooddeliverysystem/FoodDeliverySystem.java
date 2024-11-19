// Name: Mussab Bin Aziz
// Roll no: 47527 

package fooddeliverysystem;

import java.io.*;
import java.util.*;

// Account class
class Account {
    protected String username;
    protected String password;

    public Account(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public boolean login(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    public String getUsername() {
        return username;
    }
}

// Customer class
class Customer extends Account {
    private List<Order> orders;

    public Customer(String username, String password) {
        super(username, password);
        this.orders = new ArrayList<>();
    }

    public void browseMenu(Restaurant restaurant) {
        System.out.println("Menu for " + restaurant.getName() + ":");
        if (restaurant.getMenu().isEmpty()) {
            System.out.println("No menu items available.");
            return;
        }
        for (MenuItem item : restaurant.getMenu()) {
            System.out.println(item.getName() + " - Pkr " + item.getPrice());
        }
    }

    public Order placeOrder(Restaurant restaurant, List<MenuItem> items, String deliveryOption) {
        Order order = new Order(this, restaurant, items, deliveryOption);
        orders.add(order);
        restaurant.receiveOrder(order);
        saveOrderToFile(order); // Save order to file
        System.out.println("Order placed successfully!");
        return order;
    }

    public void trackOrders() {
        if (orders.isEmpty()) {
            System.out.println("No active orders.");
            return;
        }

        System.out.println("Active Orders:");
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            System.out.println((i + 1) + ". Order from " + order.getRestaurant().getName() + " - Status: " + order.getStatus());
        }
    }

    public void leaveReview(Restaurant restaurant, int rating, String comment) {
        Review review = new Review(this, rating, comment);
        restaurant.receiveReview(review);
        System.out.println("Review submitted!");
    }

    private void saveOrderToFile(Order order) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("orders.txt", true))) {
            writer.write(order.toString());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving order to file: " + e.getMessage());
        }
    }
}

// Restaurant class
class Restaurant extends Account {
    private String name;
    private List<MenuItem> menu;
    private List<Order> orders;
    private List<Review> reviews;

    public Restaurant(String name, String username, String password) {
        super(username, password);
        this.name = name;
        this.menu = new ArrayList<>();
        this.orders = new ArrayList<>();
        this.reviews = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<MenuItem> getMenu() {
        return menu;
    }

    public void addMenuItem(MenuItem item) {
        menu.add(item);
        System.out.println(item.getName() + " added to the menu.");
    }

    public void receiveOrder(Order order) {
        orders.add(order);
        System.out.println("New order received.");
    }

    public void receiveReview(Review review) {
        reviews.add(review);
        System.out.println("New review received.");
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void viewOrders() {
        if (orders.isEmpty()) {
            System.out.println("No new orders.");
            return;
        }

        System.out.println("Current Orders:");
        for (Order order : orders) {
            System.out.println("Order from " + order.getCustomer().getUsername() + " - Status: " + order.getStatus());
        }
    }

    public void updateOrderStatus(Order order, String status) {
        if (!orders.contains(order)) {
            System.out.println("Order does not exist.");
            return;
        }
        order.setStatus(status);
        System.out.println("Order status updated to: " + status);
    }
}

// Delivery Rider class
class DeliveryRider extends Account {
    private List<Order> assignedOrders;

    public DeliveryRider(String username, String password) {
        super(username, password);
        this.assignedOrders = new ArrayList<>();
    }

    public void assignOrder(Order order) {
        assignedOrders.add(order);
        System.out.println("New order assigned for delivery.");
    }

    public void viewActiveOrders() {
        if (assignedOrders.isEmpty()) {
            System.out.println("No active orders.");
            return;
        }

        System.out.println("Active Orders:");
        for (Order order : assignedOrders) {
            System.out.println("Order from " + order.getCustomer().getUsername() + " at " + order.getRestaurant().getName() + " - Status: " + order.getStatus());
        }
    }

    public void selectOrderToDeliver(List<Order> orders) {
        System.out.println("Available Orders:");
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            System.out.println((i + 1) + ". Order from " + order.getCustomer().getUsername() + " at " + order.getRestaurant().getName() + " - Status: " + order.getStatus());
        }

        System.out.print("Select an order number to deliver: ");
        Scanner scanner = new Scanner(System.in);
        int orderIndex = scanner.nextInt() - 1;

        if (orderIndex >= 0 && orderIndex < orders.size()) {
            Order selectedOrder = orders.get(orderIndex);
            assignOrder(selectedOrder);
            selectedOrder.setStatus("Out for Delivery");
        } else {
            System.out.println("Invalid order selection.");
        }
    }
}

// Order class
class Order {
    private Customer customer;
    private Restaurant restaurant;
    private List<MenuItem> items;
    private String deliveryOption;
    private String status;

    public Order(Customer customer, Restaurant restaurant, List<MenuItem> items, String deliveryOption) {
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item.");
        }
        this.customer = customer;
        this.restaurant = restaurant;
        this.items = items;
        this.deliveryOption = deliveryOption;
        this.status = "Pending";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public Customer getCustomer() {
        return customer;
    }

    @Override
    public String toString() {
        return "Order{" +
                "customer=" + customer.getUsername() +
                ", restaurant=" + restaurant.getName() +
                ", items=" + items +
                ", deliveryOption='" + deliveryOption + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

// MenuItem class
class MenuItem {
    private String name;
    private double price;

    public MenuItem(String name, double price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be positive.");
        }
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return name + " (pkr" + price + ")";
    }
}

// Review class
class Review {
    private Customer customer;
    private int rating;
    private String comment;

    public Review(Customer customer, int rating, String comment) {
        this.customer = customer;
        this.rating = rating;
        this.comment = comment;
    }

    public String toString() {
        return "Rating: " + rating + "/5, Comment: " + comment;
    }
}

// Main system class
public class FoodDeliverySystem {
    private static Map<String, Customer> customers = new HashMap<>();
    private static Map<String, Restaurant> restaurants = new HashMap<>();
    private static Map<String, DeliveryRider> riders = new HashMap<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        loadAccounts();
        while (true) {
            System.out.println("Welcome to the Food Delivery System");
            System.out.println("1. Login as Customer");
            System.out.println("2. Login as Restaurant");
            System.out.println("3. Login as Delivery Rider");
            System.out.println("4. Create Customer Account");
            System.out.println("5. Create Restaurant Account");
            System.out.println("6. Create Delivery Rider Account");
            System.out.println("7. Exit");
            int choice = -1;

            // Error handling for choice input
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // clear the invalid input
                continue; // skip to the next iteration of the loop
            }

            switch (choice) {
                case 1 -> customerLogin();
                case 2 -> restaurantLogin();
                case 3 -> riderLogin();
                case 4 -> createCustomerAccount();
                case 5 -> createRestaurantAccount();
                case 6 -> createRiderAccount();
                case 7 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // Load accounts from file
    private static void loadAccounts() {
        try (BufferedReader reader = new BufferedReader(new FileReader("accounts.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String type = data[0].trim();
                String username = data[1].trim();
                String password = data[2].trim();

                if (type.equals("Customer")) {
                    customers.put(username, new Customer(username, password));
                } else if (type.equals("Restaurant")) {
                    String restaurantName = data[3].trim();
                    restaurants.put(username, new Restaurant(restaurantName, username, password));
                } else if (type.equals("Rider")) {
                    riders.put(username, new DeliveryRider(username, password));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading accounts: " + e.getMessage());
        }
    }

    // Save account to file
    private static void saveAccountToFile(Account account) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("accounts.txt", true))) {
            if (account instanceof Customer) {
                writer.write("Customer," + account.getUsername() + "," + account.password);
            } else if (account instanceof Restaurant) {
                Restaurant restaurant = (Restaurant) account;
                writer.write("Restaurant," + restaurant.getUsername() + "," + restaurant.password + "," + restaurant.getName());
            } else if (account instanceof DeliveryRider) {
                writer.write("Rider," + account.getUsername() + "," + account.password);
            }
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving account to file: " + e.getMessage());
        }
    }

    // Customer login
    private static void customerLogin() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        Customer customer = customers.get(username);
        if (customer != null && customer.login(username, password)) {
            customerMenu(customer);
        } else {
            System.out.println("Invalid username or password.");
        }
    }

    // Restaurant login
    private static void restaurantLogin() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        Restaurant restaurant = restaurants.get(username);
        if (restaurant != null && restaurant.login(username, password)) {
            restaurantMenu(restaurant);
        } else {
            System.out.println("Invalid username or password.");
        }
    }

    // Rider login
    private static void riderLogin() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        DeliveryRider rider = riders.get(username);
        if (rider != null && rider.login(username, password)) {
            riderMenu(rider);
        } else {
            System.out.println("Invalid username or password.");
        }
    }

    // Create a new customer account
    private static void createCustomerAccount() {
        System.out.print("Enter new username: ");
        String username = scanner.nextLine();
        if (customers.containsKey(username)) {
            System.out.println("Username already exists. Try again.");
            return;
        }

        System.out.print("Enter new password: ");
        String password = scanner.nextLine();
        customers.put(username, new Customer(username, password));
        saveAccountToFile(new Customer(username, password)); // Save account to file
        System.out.println("Customer account created successfully!");
    }

    // Create a new restaurant account
    private static void createRestaurantAccount() {
        System.out.print("Enter restaurant name: ");
        String name = scanner.nextLine();
        System.out.print("Enter new username: ");
        String username = scanner.nextLine();
        if (restaurants.containsKey(username)) {
            System.out.println("Username already exists. Try again.");
            return;
        }

        System.out.print("Enter new password: ");
        String password = scanner.nextLine();
        restaurants.put(username, new Restaurant(name, username, password));
        saveAccountToFile(new Restaurant(name, username, password)); // Save account to file
        System.out.println("Restaurant account created successfully!");
    }

    // Create a new delivery rider account
    private static void createRiderAccount() {
        System.out.print("Enter new username: ");
        String username = scanner.nextLine();
        if (riders.containsKey(username)) {
            System.out.println("Username already exists. Try again.");
            return;
        }

        System.out.print("Enter new password: ");
        String password = scanner.nextLine();
        riders.put(username, new DeliveryRider(username, password));
        saveAccountToFile(new DeliveryRider(username, password)); // Save account to file
        System.out.println("Delivery rider account created successfully!");
    }

    // Customer menu
    private static void customerMenu(Customer customer) {
        while (true) {
            System.out.println("\nCustomer Menu");
            System.out.println("1. Browse Restaurant Menu");
            System.out.println("2. Place Order");
            System.out.println("3. Track Orders");
            System.out.println("4. Leave Review");
            System.out.println("5. Logout");
            int choice = -1;

            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // clear the invalid input
                continue; // skip to the next iteration of the loop
            }

            switch (choice) {
                case 1 -> browseRestaurants(customer);
                case 2 -> placeOrder(customer);
                case 3 -> customer.trackOrders();
                case 4 -> leaveReview(customer);
                case 5 -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // Browse restaurants
    private static void browseRestaurants(Customer customer) {
        System.out.println("Available Restaurants:");
        for (Restaurant restaurant : restaurants.values()) {
            System.out.println(restaurant.getName());
        }
        System.out.print("Enter restaurant name to view menu: ");
        String restaurantName = scanner.nextLine();
        Restaurant restaurant = restaurants.get(restaurantName);

        if (restaurant == null) {
            System.out.println("Restaurant not found.");
            return;
        }

        customer.browseMenu(restaurant);
    }

    // Place order
    private static void placeOrder(Customer customer) {
        System.out.print("Enter restaurant name: ");
        String restaurantName = scanner.nextLine();
        Restaurant restaurant = restaurants.get(restaurantName);

        if (restaurant == null) {
            System.out.println("Restaurant not found.");
            return;
        }

        List<MenuItem> items = new ArrayList<>();
        while (true) {
            System.out.print("Enter menu item name (or type 'done' to finish): ");
            String itemName = scanner.nextLine();
            if (itemName.equalsIgnoreCase("done")) break;

            MenuItem item = null;
            for (MenuItem m : restaurant.getMenu()) {
                if (m.getName().equalsIgnoreCase(itemName)) {
                    item = m;
                    break;
                }
            }

            if (item == null) {
                System.out.println("Item not found in the menu.");
            } else {
                items.add(item);
                System.out.println(item.getName() + " added to your order.");
            }
        }

        System.out.print("Enter delivery option: ");
        String deliveryOption = scanner.nextLine();
        customer.placeOrder(restaurant, items, deliveryOption);
    }

    // Leave a review
    private static void leaveReview(Customer customer) {
        System.out.print("Enter restaurant name to review: ");
        String restaurantName = scanner.nextLine();
        Restaurant restaurant = restaurants.get(restaurantName);

        if (restaurant == null) {
            System.out.println("Restaurant not found.");
            return;
        }

        System.out.print("Enter rating (1-5): ");
        int rating = scanner.nextInt();
        scanner.nextLine(); // consume newline
        System.out.print("Enter your comment: ");
        String comment = scanner.nextLine();
        customer.leaveReview(restaurant, rating, comment);
    }

    // Restaurant menu
    private static void restaurantMenu(Restaurant restaurant) {
        while (true) {
            System.out.println("\nRestaurant Menu");
            System.out.println("1. View Orders");
            System.out.println("2. Add Menu Item");
            System.out.println("3. Update Order Status");
            System.out.println("4. Logout");
            int choice = -1;

            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // clear the invalid input
                continue; // skip to the next iteration of the loop
            }

            switch (choice) {
                case 1 -> restaurant.viewOrders();
                case 2 -> addMenuItem(restaurant);
                case 3 -> updateOrderStatus(restaurant);
                case 4 -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // Add menu item
    private static void addMenuItem(Restaurant restaurant) {
        System.out.print("Enter item name: ");
        String itemName = scanner.nextLine();
        System.out.print("Enter item price: ");
        double price = scanner.nextDouble();
        scanner.nextLine(); // consume newline

        MenuItem item = new MenuItem(itemName, price);
        restaurant.addMenuItem(item);
    }

    // Update order status
    private static void updateOrderStatus(Restaurant restaurant) {
        restaurant.viewOrders();
        System.out.print("Enter order number to update status: ");
        int orderNumber = scanner.nextInt() - 1;

        if (orderNumber < 0 || orderNumber >= restaurant.getOrders().size()) {
            System.out.println("Invalid order number.");
            return;
        }

        System.out.print("Enter new status: ");
        String newStatus = scanner.next();
        restaurant.updateOrderStatus(restaurant.getOrders().get(orderNumber), newStatus);
    }

    // Delivery rider menu
    private static void riderMenu(DeliveryRider rider) {
        while (true) {
            System.out.println("\nDelivery Rider Menu");
            System.out.println("1. View Active Orders");
            System.out.println("2. Select Order to Deliver");
            System.out.println("3. Logout");
            int choice = -1;

            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // clear the invalid input
                continue; // skip to the next iteration of the loop
            }

            switch (choice) {
                case 1 -> rider.viewActiveOrders();
                case 2 -> {
                    // Combine all restaurant orders for the rider to select from
                    List<Order> allOrders = new ArrayList<>();
                    for (Restaurant restaurant : restaurants.values()) {
                        allOrders.addAll(restaurant.getOrders());
                    }
                    rider.selectOrderToDeliver(allOrders);
                }
                case 3 -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }
}