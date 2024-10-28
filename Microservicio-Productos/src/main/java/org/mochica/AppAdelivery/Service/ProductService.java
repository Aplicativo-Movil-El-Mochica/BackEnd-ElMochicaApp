package org.mochica.AppAdelivery.Service;

import org.mochica.AppAdelivery.DTO.*;
import org.mochica.AppAdelivery.Entity.Categori;
import org.mochica.AppAdelivery.Entity.Product;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface ProductService {

    Boolean saveProduct(SaveProductDTO saveProductDTO) throws InterruptedException, ExecutionException;
    List<Product> getProduct(String productName) throws InterruptedException, ExecutionException;
    String deleteProduct(String productId) throws InterruptedException, ExecutionException;
    Boolean updateStock(ProductStockDTO productStockDTO) throws InterruptedException, ExecutionException;
    int getDisposition(String productName) throws InterruptedException, ExecutionException;
    Boolean updateCategory(ProductCategoryDTO productCategoryDTO) throws InterruptedException, ExecutionException;
    Product findProductByName(ProductNameDTO productNameDTO) throws InterruptedException, ExecutionException;
    List<Product> findProductsByCategory(Categori category) throws InterruptedException, ExecutionException;
}
