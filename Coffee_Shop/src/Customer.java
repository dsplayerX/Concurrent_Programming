import java.util.List;

/**
 * A customer who places orders at a coffee shop.
 */
class Customer implements Runnable {
    private final CoffeeShop coffeeShop;
    private final List<String> orders; // List of orders for the customer

    public Customer(CoffeeShop coffeeShop, List<String> orders) {
        this.coffeeShop = coffeeShop;
        this.orders = orders;
    }

    @Override
    public void run() {
        try {
            String customerName = Thread.currentThread().getName();
            System.out.println(customerName + "'s order list : " + String.join(", ", orders));
            for (String order : orders) {
                String orderString = order + " for " + customerName;
                coffeeShop.placeOrder(orderString, customerName); // Place each order
                Thread.sleep(1000); // Simulate some delay between orders
            }
        } catch (InterruptedException e) {
            System.err.println(Thread.currentThread().getName() + " was interrupted.");
            e.printStackTrace();
        }
    }
}