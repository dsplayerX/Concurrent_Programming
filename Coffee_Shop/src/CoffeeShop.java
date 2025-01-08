import java.util.LinkedList;
import java.util.Queue;

class CoffeeShop {
    private final Queue<String> orderQueue;
    private final int capacity;

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
        System.out.println(customerName + ": Placed order [" + order + "]");
        notifyAll();
    }

    // Barista prepares the orders (Consumer)
    public synchronized String prepareOrder(String baristaName) throws InterruptedException {
        while (orderQueue.isEmpty()) {
            System.out.println(baristaName + ": Order queue is empty. Waiting for orders...");
            wait();
        }
        String order = orderQueue.poll();
        System.out.println(baristaName + ": Preparing order [" + order + "]");
        notifyAll();
        return order;
    }
}