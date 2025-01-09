import java.util.Random;

/**
 * Represents a barista who prepares orders in a coffee shop.
 */
class Barista implements Runnable {
    private final CoffeeShop coffeeShop;

    public Barista(CoffeeShop coffeeShop) {
        this.coffeeShop = coffeeShop;
    }

    @Override
    public void run() {
        try {
            String baristaName = Thread.currentThread().getName();
            Random rand = new Random();
            while (true) { // Barista are always ready to prepare orders
                String order = coffeeShop.prepareOrder(baristaName);
                if (order == null) {
                    System.out.println(baristaName + ": No more orders to prepare. Shop is closing...");
                    break;
                }
                System.out.println(baristaName + ": Order ready " + order + ".");
                Thread.sleep(rand.nextInt(100,1000)); // Simulating time taken to prepare an order
            }
        } catch (InterruptedException e) {
            System.err.println(Thread.currentThread().getName() + " was interrupted.");
            e.printStackTrace();
        }
    }
}