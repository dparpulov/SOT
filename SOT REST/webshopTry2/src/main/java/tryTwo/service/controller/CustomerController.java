package tryTwo.service.controller;

import tryTwo.service.model.Customer;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("customer")
@Singleton
public class CustomerController {
    private List<Customer> customerList = new ArrayList<>();

    public CustomerController() {
        customerList.add(new Customer(1, "Bob", "bob@hello.nl", 20));
        customerList.add(new Customer(2, "Tim", "tim@hello.nl", 5));

    }

    //Gets the client with the given id using a path parameter
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Customer getClient(@PathParam("id") int id){
        for (Customer customer : customerList){
            if (customer.getId() == id){
                return customer; //Response.ok(customer).build();
            }
        }
        //return Response.serverError().entity("Cannot find customer with id " + id + "!").build();
        throw new RuntimeException("Cannot find client with id " + id + "!");

    }

    //Gets the client with the given id using a query parameter
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Customer getClientQuery(@QueryParam("id") int id){
        for (Customer customer : customerList){
            if (customer.getId() == id){
                return customer;
            }
        }
        throw new RuntimeException("Cannot find customer with id " + id + "!");
    }

    //Deletes the client with the given id using a path parameter
    @DELETE
    @Path("{id}")
    public void deleteClient(@PathParam("id") int id){
        Customer customerToRemove = null;
        for (Customer customer : customerList){
            if (customer.getId() == id){
                customerToRemove = customer;
                break;
            }
        }
        if (customerToRemove != null) {
            customerList.remove(customerToRemove);
        }else {
            throw new RuntimeException("Cannot find customer with id " + id + "!");
        }
    }

    //Creates a customer
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void createClientJson(Customer customer){
        if (clientExists(customer)){
            throw new RuntimeException("There is already a customer with id " + customer.getId() + "!");
        }else {
            customerList.add(customer);
        }
    }

    //Creates a customer
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response createClientForm(@FormParam("name") String name, @FormParam("email") String email, @FormParam("budget") String budget){
        if (clientExistsEmail(email) != null){
            return Response.serverError().entity("Could not create new customer with email: " + email +
                    "\nReason: This email has been used!").build();
        }

        Customer customerToBeAdded = new Customer(customerList.size()+1,name, email, Double.parseDouble(budget));
        customerList.add(customerToBeAdded);
        return Response.noContent().build();
    }

    //Updates the customer with the given id using a path parameter
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateClient(Customer customer){
        Customer existingCustomer = this.getClient(customer.getId());
        if (existingCustomer == null) {
            throw new RuntimeException("Cannot find customer with id " + customer.getId() + "!");
        }
        existingCustomer.setName(customer.getName());
        existingCustomer.setEmail(customer.getEmail());
        existingCustomer.setBudget(customer.getBudget());
        existingCustomer.setOrder(customer.getOrder());
    }

    //Extras

    //Checks if the customer exists
    private boolean clientExists(Customer customer){
        for (Customer c: customerList){
            if (customer.getId() == c.getId()){
                return true;
            }
        }
        return false;
    }

    //Checks if the customer exists
    private Customer clientExistsEmail(String email){
        for (Customer c: customerList){
            if (c.getEmail().equals(email)){
                return c;
            }
        }
        return null;
    }

    //Gets all the available clients
    @GET
    @Path("all")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Customer> getAllCustomers(){

        return customerList;
    }

    //Gets the total amount of clients
    @GET
    @Path("count")
    @Produces({MediaType.TEXT_PLAIN})
    public Integer getCountCustomer(){
        return customerList.size();
    }
}
