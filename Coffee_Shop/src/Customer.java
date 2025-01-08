class Customer implements Runnable {
    private final CoffeeShop coffeeShop;
    private final String order;

    public Customer(CoffeeShop coffeeShop, String order) {
        this.coffeeShop = coffeeShop;
        this.order = order;
    }

    @Override
    public void run() {
        try {
            String customerName = Thread.currentThread().getName();
            coffeeShop.placeOrder(order, customerName);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}