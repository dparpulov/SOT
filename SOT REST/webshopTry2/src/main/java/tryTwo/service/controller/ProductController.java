package tryTwo.service.controller;

import tryTwo.service.model.Product;

import javax.inject.Singleton;
import javax.validation.constraints.Null;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("product")
@Singleton
public class ProductController {
    private List<Product> productList = new ArrayList<>();

    public ProductController() {
        productList.add(new Product(1, "Banana", 1));
        productList.add(new Product(2, "Ham", 2));
        productList.add(new Product(3, "Eggs", 1.50));
        productList.add(new Product(4, "Peanut butter", 3));

    }



    //Gets the product with the given id using a path parameter
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Product getProduct(@PathParam("id") int id){
        for (Product product: productList){
            if (product.getId() == id){
                return product; //Response.ok(product).build();
            }
        }
            //return Response.serverError().entity("Cannot find product with id " + id + "!").build();
        throw new RuntimeException("Cannot find product with id " + id + "!");

    }

    //Gets the product with the given id using a query parameter
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Product getProductQuery(@QueryParam("id") int id){
        for (Product product: productList){
            if (product.getId() == id){
                return product;
            }
        }
        throw new RuntimeException("Cannot find product with id " + id + "!");
    }



    //Deletes the product with the given id using a path parameter
    @DELETE
    @Path("{id}")
    public void deleteProduct(@PathParam("id") int id){
        Product productToRemove = null;
        for (Product product: productList){
            if (product.getId() == id){
                productToRemove = product;
                break;
            }
        }
        if (productToRemove != null) {
            productList.remove(productToRemove);
        }else {
            throw new RuntimeException("Cannot find product with id " + id + "!");
        }
    }

    //Creates a product
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void createProduct(Product product){
        if (productExists(product)){
            throw new RuntimeException("There is already a product with id " + product.getId() + "!");
        }else {
            productList.add(product);
        }
    }

    //Updates the product with the given id using a path parameter
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateProduct(Product product){
        Product existingProduct = this.getProduct(product.getId());
        if (existingProduct == null) {
            throw new RuntimeException("Cannot find product with id " + product.getId() + "!");
        }
        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
    }

    //Extras

    //Checks if the products exists
    private boolean productExists(Product product){
        for (Product p: productList){
            if (product.getId() == p.getId()){
                return true;
            }
        }
        return false;
    }

    //Gets all the available products
    @GET
    @Path("all")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Product> getAllProducts(){

        return productList;
    }

    //Gets the total amount of products
    @GET
    @Path("count")
    @Produces({MediaType.TEXT_PLAIN})
    public Integer getCountProducts(){

        return productList.size();
    }
}
