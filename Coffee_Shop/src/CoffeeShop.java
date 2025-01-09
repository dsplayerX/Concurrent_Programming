import java.util.LinkedList;
import java.util.Queue;

/**
 * A coffee shop with a queue for orders.
 */
class CoffeeShop {
    private final Queue<String> orderQueue;
    private final int capacity;
    private boolean closingShop = false; // flag to indicate the shop is closing

    public CoffeeShop(int capacity) {
        this.orderQueue = new LinkedList<>();
        this.capacity = capacity;
    }

    // Customer places orders (Producer)
    public synchronized void placeOrder(String order, String customerName) throws InterruptedException {
        while (orderQueue.size() == capacity) {
            System.out.println(customerName + ": Order queue is full. Waiting to place order...");
            wait();
        }
        orderQueue.add(order);
        System.out.println(customerName + ": Placed an order for " + order + ".");
        notifyAll();
    }

    // Barista prepares the orders (Consumer)
    public synchronized String prepareOrder(String baristaName) throws InterruptedException {
        while (orderQueue.isEmpty() && !closingShop) {
            System.out.println(baristaName + ": Order queue is empty. Waiting for orders...");
            wait();
        }

        if (orderQueue.isEmpty() && closingShop) {
            // Shop is closing, and no more orders are left to process.
            // Returning null to signal the barista thread to terminate.
            return null;
        }
        String order = orderQueue.poll();
        System.out.println(baristaName + ": Preparing order " + order + ".");
        notifyAll();
        return order;
    }

    public synchronized void closeShop() {
        closingShop = true; // Set the flag to indicate the shop is closing
        notifyAll(); // Wake up all waiting threads
    }
}