package org.mochica.AppAdelivery.Controller;

import org.mochica.AppAdelivery.Entity.Product;
import org.mochica.AppAdelivery.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/add")
    public String addProduct(@RequestBody Product product) throws InterruptedException, ExecutionException {
        return productService.saveProduct(product);
    }

    @GetMapping("/get/{id}")
    public Product getProduct(@PathVariable Long id) throws InterruptedException, ExecutionException {
        return productService.getProduct(id);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) throws InterruptedException, ExecutionException {
        return productService.deleteProduct(id);
    }

    @PutMapping("/updateStock/{id}/{cantidad}")
    public void updateStock(@PathVariable Long id, @PathVariable int cantidad) throws InterruptedException, ExecutionException {
        productService.updateStock(id, cantidad);
    }
}
