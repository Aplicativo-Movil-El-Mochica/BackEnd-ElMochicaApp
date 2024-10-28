package org.mochica.AppAdelivery.Controller;

import org.mochica.AppAdelivery.DTO.ProductCategoryDTO;
import org.mochica.AppAdelivery.DTO.ProductDispositionDTO;
import org.mochica.AppAdelivery.DTO.ProductStockDTO;
import org.mochica.AppAdelivery.Entity.Categori;
import org.mochica.AppAdelivery.Entity.Product;
import org.mochica.AppAdelivery.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.mochica.AppAdelivery.DTO.ProductNameDTO;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/add")
    public ResponseEntity<String> addProduct(@RequestBody Product product) throws InterruptedException, ExecutionException {
        String productId = productService.saveProduct(product);
        return new ResponseEntity<>(productId, HttpStatus.CREATED);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) throws InterruptedException, ExecutionException {
        Product product = productService.getProduct(id);
        if (product != null) {
            return new ResponseEntity<>(product, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) throws InterruptedException, ExecutionException {
        String response = productService.deleteProduct(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/updateStock/{id}/{cantidad}")
    public ResponseEntity<Void> updateStock(@PathVariable Long id, @PathVariable int cantidad) throws InterruptedException, ExecutionException {
        ProductStockDTO productStockDTO = new ProductStockDTO();
        productStockDTO.setProductId(id);
        productStockDTO.setCantidad(cantidad);
        productService.updateStock(productStockDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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

    @PostMapping("/getDisposition")
    public ResponseEntity<Integer> getDisposition(@RequestBody ProductDispositionDTO productDispositionDTO) throws InterruptedException, ExecutionException {
        int disposition = productService.getDisposition(productDispositionDTO);
        return new ResponseEntity<>(disposition, HttpStatus.OK);
    }

    @PostMapping("/addCategory")
    public ResponseEntity<Void> addCategory(@RequestBody ProductCategoryDTO productCategoryDTO) throws InterruptedException, ExecutionException {
        productService.addCategory(productCategoryDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/filterByCategory/{category}")
    public ResponseEntity<List<Product>> filterByCategory(@PathVariable Categori category) throws InterruptedException, ExecutionException {
        List<Product> products = productService.findProductsByCategory(category);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
}