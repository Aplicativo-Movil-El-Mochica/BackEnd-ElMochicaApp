package org.mochica.AppAdelivery.Controller;

import org.mochica.AppAdelivery.DTO.*;
import org.mochica.AppAdelivery.Entity.Categori;
import org.mochica.AppAdelivery.Entity.Product;
import org.mochica.AppAdelivery.Service.Impl.ProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductServiceImpl productService;

    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@RequestBody SaveProductDTO saveProductDTO) throws InterruptedException, ExecutionException {
        Boolean success = productService.saveProduct(saveProductDTO);
        if (success) {
            return new ResponseEntity<>("Dish added successfully", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/getProduct")
    public ResponseEntity<?> getProduct(@RequestParam String productName) throws InterruptedException, ExecutionException {
        List<Product> product = productService.getProduct(productName);
        if (product != null) {
            return new ResponseEntity<>(product, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable String id) throws InterruptedException, ExecutionException {
        String response = productService.deleteProduct(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/getDisposition")
    public ResponseEntity<Integer> getDisposition(@RequestBody ProductNameDTO productNameDTO) throws InterruptedException, ExecutionException {
        int disposition = productService.getDisposition(productNameDTO.getProductName());
        return new ResponseEntity<>(disposition, HttpStatus.OK);
    }

    @PutMapping("/updateCategory")
    public ResponseEntity<?> updateCategory(@RequestBody ProductCategoryDTO productCategoryDTO) {
        try {
            Boolean success = productService.updateCategory(productCategoryDTO);
            if (success) {
                return new ResponseEntity<>("Category updated successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/updateStock")
    public ResponseEntity<?> updateStock(@RequestBody ProductStockDTO productStockDTO) throws InterruptedException, ExecutionException {
        Boolean success = productService.updateStock(productStockDTO);
        if (success) {
            return new ResponseEntity<>("Stock updated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/findByName")
    public ResponseEntity<Product> findProductByName(@RequestBody ProductNameDTO productNameDTO) throws InterruptedException, ExecutionException {
        Product product = productService.findProductByName(productNameDTO);
        if (product != null) {
            return new ResponseEntity<>(product, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }



    @GetMapping("/filterByCategory/{category}")
    public ResponseEntity<List<Product>> filterByCategory(@PathVariable Categori category) throws InterruptedException, ExecutionException {
        List<Product> products = productService.findProductsByCategory(category);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
}