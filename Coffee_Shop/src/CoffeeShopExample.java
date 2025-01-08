public class CoffeeShopExample {
    public static void main(String[] args) {
        CoffeeShop coffeeShop = new CoffeeShop(2); // Queue capacity is 2

        // Number of baristas and customers
        int numBaristas = 3;
        int numCustomers = 10;

        Thread[] baristasNCustomers = new Thread[numBaristas + numCustomers]; // Array to hold all threads

        // Create barista threads
        for (int i = 0; i < numBaristas; i++) {
            baristasNCustomers[i] = new Thread(new Barista(coffeeShop), "Barista " + (i + 1));
        }

        // Create customer threads
        for (int i = 0; i < numCustomers; i++) {
            baristasNCustomers[numBaristas + i] = new Thread(new Customer(coffeeShop, "Order " + (i + 1)), "Customer " + (i + 1));
        }

        // Start all threads
        for (Thread thread : baristasNCustomers) {
            thread.start();
        }
    }
}