package tryTwo.service.model;

import java.util.Objects;

public class Customer {
    private int id;
    private String name;
    private String email;
    private double budget;
    private Order order;

    public Customer() {
    }

    public Customer(int id, String name, String email, double budget) {
        super();
        this.id = id;
        this.name = name;
        this.email = email;
        this.budget = budget;
        this.order = null;
    }

    public Customer(int id, String name, String email, double budget, Order order) {
        super();
        this.id = id;
        this.name = name;
        this.email = email;
        this.budget = budget;
        this.order = order;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return id == customer.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
