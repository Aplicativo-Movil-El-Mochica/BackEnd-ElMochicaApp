package org.mochica.AppAdelivery.Controller;

import org.mochica.AppAdelivery.DTO.ProductCategoryDTO;
import org.mochica.AppAdelivery.DTO.ProductDispositionDTO;
import org.mochica.AppAdelivery.DTO.ProductStockDTO;
import org.mochica.AppAdelivery.Entity.Product;
import org.mochica.AppAdelivery.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.mochica.AppAdelivery.DTO.ProductNameDTO;

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
        ProductStockDTO productStockDTO = new ProductStockDTO();
        productStockDTO.setProductId(id);
        productStockDTO.setCantidad(cantidad);
        productService.updateStock(productStockDTO);
    }

    @PostMapping("/findByName")
    public Product findProductByName(@RequestBody ProductNameDTO productNameDTO) throws InterruptedException, ExecutionException {
    return productService.findProductByName(productNameDTO);
    }

    @PostMapping("/getDisponibility")
    public int getDisponibility(@RequestBody ProductDispositionDTO productDisponibilityDTO) throws InterruptedException, ExecutionException {
        return productService.getDisponibility(productDisponibilityDTO);
    }

    @PostMapping("/addCategory")
    public void addCategory(@RequestBody ProductCategoryDTO productCategoryDTO) throws InterruptedException, ExecutionException {
        productService.addCategory(productCategoryDTO);
    }
}
