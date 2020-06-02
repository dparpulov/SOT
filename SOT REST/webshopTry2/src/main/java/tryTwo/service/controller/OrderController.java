package tryTwo.service.controller;

import tryTwo.service.model.Order;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("order")
@Singleton
public class OrderController {
    private List<Order> orderList = new ArrayList<>();

    public OrderController() {

    }

    //Gets the order with the given id using a path parameter
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Order getOrder(@PathParam("id") int id){
        for (Order order: orderList){
            if (order.getId() == id){
                return order; //Response.ok(order).build();
            }
        }
        //return Response.serverError().entity("Cannot find order with id " + id + "!").build();
        throw new RuntimeException("Cannot find order with id " + id + "!");

    }

    //Gets the order with the given id using a query parameter
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Order getOrderQuery(@QueryParam("id") int id){
        for (Order order: orderList){
            if (order.getId() == id){
                return order;
            }
        }
        throw new RuntimeException("Cannot find order with id " + id + "!");
    }



    //Deletes the order with the given id using a path parameter
    @DELETE
    @Path("{id}")
    public void deleteOrder(@PathParam("id") int id){
        Order orderToRemove = null;
        for (Order order: orderList){
            if (order.getId() == id){
                orderToRemove = order;
                break;
            }
        }
        if (orderToRemove != null) {
            orderList.remove(orderToRemove);
        }else {
            throw new RuntimeException("Cannot find order with id " + id + "!");
        }
    }

    //Creates a order
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void createOrder(Order order){
        if (orderExists(order)){
            throw new RuntimeException("There is already a order with id " + order.getId() + "!");
        }else {
            orderList.add(order);
        }
    }

    //Updates the order with the given id using a path parameter
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateOrder(Order order){
        Order existingOrder = this.getOrder(order.getId());
        if (existingOrder == null) {
            throw new RuntimeException("Cannot find order with id " + order.getId() + "!");
        }
        existingOrder.setCustomer(order.getCustomer());
        existingOrder.setProducts(order.getProducts());
        existingOrder.setTotalPrice(order.getProducts());

    }

    //Extras

    //Checks if the order exists
    private boolean orderExists(Order order){
        for (Order c: orderList){
            if (order.getId() == c.getId()){
                return true;
            }
        }
        return false;
    }

    //Gets all the available orders
    @GET
    @Path("all")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Order> getAllOrders(){

        return orderList;
    }

    //Gets the total amount of orders
    @GET
    @Path("count")
    @Produces({MediaType.TEXT_PLAIN})
    public Integer getCountOrders(){

        return orderList.size();
    }
}
