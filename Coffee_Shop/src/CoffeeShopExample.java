import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * A coffee shop simulation with baristas and customers.
 */
public class CoffeeShopExample {
    public static void main(String[] args) {
        CoffeeShop coffeeShop = new CoffeeShop(3); // Queue capacity is 2

        // Drinks menu
        List<String> drinks = Arrays.asList("Latte", "Cappuccino", "Espresso", "Americano", "Macchiato", "Mocha", "Flat White", "Cold Brew");

        int numBaristas = 3; // Number of baristas
        int numCustomers = 5; // Number of customers
        int ordersPerCustomer = 5; // Number of orders each customer places

        Thread[] baristasNCustomers = new Thread[numBaristas + numCustomers]; // Array to hold all threads

        // Create barista threads
        for (int i = 0; i < numBaristas; i++) {
            baristasNCustomers[i] = new Thread(new Barista(coffeeShop), "Barista " + (i + 1));
        }

        // Create customer threads
        Random random = new Random();
        for (int i = 0; i < numCustomers; i++) {
            List<String> randomOrders = getRandomOrders(drinks, ordersPerCustomer, random);

            baristasNCustomers[numBaristas + i] = new Thread(new Customer(coffeeShop, randomOrders), "Customer " + (i + 1));
        }

        // Start all threads
        for (Thread thread : baristasNCustomers) {
            thread.start();
        }

        // Wait for all customer threads to finish
        for (int i = numBaristas; i < baristasNCustomers.length; i++) {
            try {
                baristasNCustomers[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Close the shop
        coffeeShop.closeShop();

        // Wait for all barista threads to finish
        for (int i = 0; i < numBaristas; i++) {
            try {
                baristasNCustomers[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Coffee shop was closed for the day.");
    }

    /**
     * Returns a list of random orders from the drinks menu.
     */
    private static List<String> getRandomOrders(List<String> drinks, int numberOfOrders, Random random) {
        List<String> randomOrders = new ArrayList<>();
        for (int i = 0; i < numberOfOrders; i++) {
            int randomIndex = random.nextInt(drinks.size());
            randomOrders.add(drinks.get(randomIndex));
        }
        return randomOrders;
    }
}