class Barista implements Runnable {
    private final CoffeeShop coffeeShop;

    public Barista(CoffeeShop coffeeShop) {
        this.coffeeShop = coffeeShop;
    }

    @Override
    public void run() {
        try {
            String baristaName = Thread.currentThread().getName();
            while (true) { // Simulating continuous work for the barista
                String order = coffeeShop.prepareOrder(baristaName);
                Thread.sleep(2000); // Simulating time taken to prepare an order
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}