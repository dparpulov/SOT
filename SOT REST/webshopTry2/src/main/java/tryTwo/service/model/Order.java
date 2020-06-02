package tryTwo.service.model;

import java.util.List;
import java.util.Objects;

public class Order {
    private int id;
    private Customer customer;
    private List<Product> products;
    private double totalPrice;

    public Order(){

    }

    public Order(int id, Customer customer, List<Product> products, double totalPrice){
        super();
        this.id = id;
        this.customer = customer;
        this.products = products;
        this.totalPrice = totalPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(List<Product> products) {
        for (Product product : products) {
            this.totalPrice = product.getPrice();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id == order.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
