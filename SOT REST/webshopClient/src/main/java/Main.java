import org.glassfish.jersey.client.ClientConfig;
import tryTwo.service.model.*;

import javax.ws.rs.client.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static ClientConfig config = new ClientConfig();
    private static Client client = ClientBuilder.newClient(config);
    private static URI baseUri = UriBuilder.fromUri("http://localhost:25565/webshop").build();
    private static WebTarget serviceTarget = client.target(baseUri);
    private static Invocation.Builder requestBuilder;
    private static Response response;

    private static String customerHost, orderHost, productHost;
    private static boolean mainExit, customerExit, orderExit, productExit;
    private static Scanner s = new Scanner(System.in);

    private static void setupConfig(String serviceHost){
        config = new ClientConfig();
        client = ClientBuilder.newClient(config);
        baseUri = UriBuilder.fromUri(serviceHost).build();
        serviceTarget = client.target(baseUri);
    }

    public static void main(String[] args){
        mainExit = false;
        customerHost = "http://localhost:25565/webshop/customer";
        orderHost = "http://localhost:25565/webshop/order";
        productHost = "http://localhost:25565/webshop/product";

        mainInstructions();

        while (!mainExit){
            System.out.print("Input: \n");
            int userSelection = s.nextInt();
            s.nextLine();

            switch (userSelection){
                case 0:
                    System.out.println("Good bye! Hope to see you soon. :)");
                    mainExit = true;
                    break;
                case 1:
                    System.out.println("Client management");
                    clientManagement();
                    break;
                case 2:
                    System.out.println("Order management");
                    break;
                case 3:
                    System.out.println("Product management");
                    break;
                case 4:
                    System.out.println("Enter shop");
                    break;
                default:
                    System.out.println("Default case");
                    break;
            }
        }
    }

    private static void clientManagement(){
        customerExit = false;
        customerHost = "http://localhost:25565/webshop/customer";

        customerInstructions();

        while (!customerExit){
            System.out.print("Input: \n");
            int userSelection = s.nextInt();
            s.nextLine();

            switch (userSelection){
                case 0:
                    System.out.println("Back to main menu"); //go back to main
                    mainInstructions();
                    customerExit = true;
                    break;
                case 1:
                    System.out.println("See all Customers:");
                    viewAllCustomers();
                    break;
                case 2:
                    System.out.println("Add customer:");
                    addCustomer();
                    break;
                case 3:
                    System.out.println("Remove customer:");
                    removeCustomer();
                    break;
                case 4:
                    System.out.println("Update customer:");
                    updateCustomer();
                    break;
            }
        }
    }

    private static void viewAllCustomers(){
        setupConfig(customerHost);
        requestBuilder = serviceTarget.path("all").request().accept(MediaType.APPLICATION_JSON);
        response = requestBuilder.get();

        try {
            if (response.getStatus() == Response.Status.OK.getStatusCode()){
                GenericType<ArrayList<Customer>> customerList = new GenericType<>() {};
                ArrayList<Customer> object = response.readEntity(customerList);

                System.out.println("All available customers: \n");
                for (Customer c : object){
                    System.out.format("Id: %10s Name: %10s Email: %10s Budget: %10s", c.getId(), c.getName(), c.getEmail(), Double.toString(c.getBudget()));
                    System.out.println();
                }
                System.out.println();

            }else {
                System.out.println("Cannot get the generic collection" + response);
                GenericType<ArrayList<Customer>> genericType = new GenericType<>() {};
                ArrayList<Customer> entity = response.readEntity(genericType);
                System.out.println(entity);
            }
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    private static void addCustomer(){
        setupConfig(customerHost);

        System.out.print("Enter the new customer name: ");
        String name = s.nextLine();

        System.out.print("Enter the new customer email: ");
        String email = s.nextLine();

        System.out.print("Enter the new customer budget: ");
        double budget = s.nextDouble();

        Form createCus = new Form();
        createCus.param("name", name);
        createCus.param("email", email);
        createCus.param("budget", String.valueOf(budget));

        Entity<Form> entity = Entity.entity(createCus, MediaType.APPLICATION_FORM_URLENCODED);
        response = serviceTarget.request().accept(MediaType.TEXT_PLAIN).post(entity);

        if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()){
            System.out.println("Successfully created new customer with email " + email);
            System.out.println();
        } else {
            System.out.println("Error: " + response.readEntity(String.class));
        }
    }

    private static void removeCustomer(){
        viewAllCustomers();

        System.out.print("Enter the id of the customer you want to remove: ");
        String id = s.nextLine();

        requestBuilder = serviceTarget.path(id).request().accept(MediaType.APPLICATION_JSON);
        response = requestBuilder.delete();

        if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()){
            System.out.println("Successfully removed customer with id " + id);
        } else {
            System.out.println("Error: " + response.readEntity(String.class));
        }

    }

    private static void updateCustomer() {
        viewAllCustomers();

        System.out.print("Enter the id of the customer you want to edit: ");
        int id = s.nextInt();
        s.nextLine();

        requestBuilder = serviceTarget.queryParam("id", id).request().accept(MediaType.APPLICATION_JSON);
        response = requestBuilder.get();

        if (response.getStatus() == Response.Status.OK.getStatusCode()){
            Customer toBeUpdated = response.readEntity(Customer.class);

            System.out.print("Enter the new name for the customer: ");
            String newName = s.nextLine();

            System.out.print("Enter the new email for the customer: ");
            String newEmail = s.nextLine();

            System.out.print("Enter the new budget for the customer: ");
            Double newBudget = s.nextDouble();

            toBeUpdated.setName(newName);
            toBeUpdated.setEmail(newEmail);
            toBeUpdated.setBudget(newBudget);

            Entity<Customer> object = Entity.entity(toBeUpdated, MediaType.APPLICATION_JSON);
            response = serviceTarget.request().accept(MediaType.TEXT_PLAIN).put(object);

            if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()){
                System.out.println("Successfully updated the customer with id " + id + ", name " + newName + ", email " + newEmail);
            } else {
                System.out.println("Error: " + response.readEntity(String.class));
            }
        } else {
            System.out.println("Error: " + response.readEntity(String.class));
        }
    }


    //region Instructions
    private static void mainInstructions(){
        System.out.println("Press 1: Client Management:");
        System.out.println("Press 2: Order Management:");
        System.out.println("Press 3: Product Management:");
        System.out.println("Press 4: Shop Management:");
        System.out.println("Press 0: Exit:");
    }

    private static void customerInstructions(){
        System.out.println("Press 1: See all customers:");
        System.out.println("Press 2: Add customer:");
        System.out.println("Press 3: Delete customer:");
        System.out.println("Press 4: Update customer:");
        System.out.println("Press 0: Exit:");
    }

    private static void orderInstructions(){
        System.out.println("Press 1: See all orders:");
        System.out.println("Press 2: Add an order:");
        System.out.println("Press 3: Delete an order:");
        System.out.println("Press 4: Update an order:");
        System.out.println("Press 0: Exit:");
    }

    private static void productInstructions(){
        System.out.println("Press 1: See all products:");
        System.out.println("Press 2: Add a product:");
        System.out.println("Press 3: Delete a product:");
        System.out.println("Press 4: Update a product:");
        System.out.println("Press 0: Exit:");
    }
    //endregions
}
