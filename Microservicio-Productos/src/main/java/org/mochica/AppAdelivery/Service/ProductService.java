package org.mochica.AppAdelivery.Service;

import org.mochica.AppAdelivery.Entity.Product;
import org.mochica.AppAdelivery.Repository.ProductRepository;
import org.mochica.AppAdelivery.Entity.Categori;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public String saveProduct(Product product) throws InterruptedException, ExecutionException {
        return productRepository.saveProduct(product);
    }

    public Product getProduct(Long productId) throws InterruptedException, ExecutionException {
        return productRepository.getProduct(productId);
    }

    public String deleteProduct(Long productId) throws InterruptedException, ExecutionException {
        return productRepository.deleteProduct(productId);
    }

    // Actualizar stock
    public void updateStock(Long productId, int cantidad) throws InterruptedException, ExecutionException {
        Product product = getProduct(productId);
        if (product != null) {
            product.actualizarStock(cantidad);
            saveProduct(product);
        }
    }

    // Obtener disponibilidad
    public int getDisponibility(Long productId) throws InterruptedException, ExecutionException {
        Product product = getProduct(productId);
        if (product != null) {
            return product.obtenerDisponibilidad();
        }
        return 0;  // Si no existe el producto, retorna 0
    }

    // Agregar categoría
    public void addCategory(Long productId, String category) throws InterruptedException, ExecutionException {
        Product product = getProduct(productId);
        if (product != null) {
            product.agregarCategoria(Enum.valueOf(Categori.class, category));
            saveProduct(product);
        }
    }

    // Buscar producto por nombre
    public Product findProductByName(String name) throws InterruptedException, ExecutionException {
        List<Product> products = productRepository.getAllProducts();  // Implementa getAllProducts en ProductRepository

        for (Product product : products) {
            if (product.buscarProductoPorNombre(name)) {
                return product;
            }
        }
        return null;  // Retorna null si no se encontró el producto
    }
}
