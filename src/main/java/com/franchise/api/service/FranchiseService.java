package com.franchise.api.service;

import com.franchise.api.dto.RequestDTOs.*;
import com.franchise.api.dto.TopProductResponse;
import com.franchise.api.exception.ResourceNotFoundException;
import com.franchise.api.model.Branch;
import com.franchise.api.model.Franchise;
import com.franchise.api.model.Product;
import com.franchise.api.repository.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FranchiseService {

    private final FranchiseRepository franchiseRepository;

    public Mono<Franchise> createFranchise(CreateFranchiseRequest request) {
        Franchise franchise = Franchise.builder()
                .name(request.getName())
                .build();
        return franchiseRepository.save(franchise);
    }

    public Flux<Franchise> getAllFranchises() {
        return franchiseRepository.findAll();
    }

    public Mono<Franchise> getFranchiseById(String id) {
        return franchiseRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franchise not found with id: " + id)));
    }

    public Mono<Franchise> updateFranchiseName(String id, UpdateNameRequest request) {
        return franchiseRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franchise not found with id: " + id)))
                .flatMap(franchise -> {
                    franchise.setName(request.getName());
                    return franchiseRepository.save(franchise);
                });
    }

    public Mono<Franchise> addBranchToFranchise(String franchiseId, CreateBranchRequest request) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franchise not found with id: " + franchiseId)))
                .flatMap(franchise -> {
                    Branch branch = Branch.builder()
                            .id(UUID.randomUUID().toString())
                            .name(request.getName())
                            .build();
                    franchise.getBranches().add(branch);
                    return franchiseRepository.save(franchise);
                });
    }

    public Mono<Franchise> updateBranchName(String franchiseId, String branchId, UpdateNameRequest request) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franchise not found with id: " + franchiseId)))
                .flatMap(franchise -> {
                    Branch branch = franchise.getBranches().stream()
                            .filter(b -> b.getId().equals(branchId))
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + branchId));
                    
                    branch.setName(request.getName());
                    return franchiseRepository.save(franchise);
                });
    }

    public Mono<Franchise> addProductToBranch(String franchiseId, String branchId, CreateProductRequest request) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franchise not found with id: " + franchiseId)))
                .flatMap(franchise -> {
                    Branch branch = franchise.getBranches().stream()
                            .filter(b -> b.getId().equals(branchId))
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + branchId));
                    
                    Product product = Product.builder()
                            .id(UUID.randomUUID().toString())
                            .name(request.getName())
                            .stock(request.getStock())
                            .build();
                    
                    branch.getProducts().add(product);
                    return franchiseRepository.save(franchise);
                });
    }

    public Mono<Franchise> deleteProductFromBranch(String franchiseId, String branchId, String productId) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franchise not found with id: " + franchiseId)))
                .flatMap(franchise -> {
                    Branch branch = franchise.getBranches().stream()
                            .filter(b -> b.getId().equals(branchId))
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + branchId));
                    
                    boolean removed = branch.getProducts().removeIf(p -> p.getId().equals(productId));
                    
                    if (!removed) {
                        return Mono.error(new ResourceNotFoundException("Product not found with id: " + productId));
                    }
                    
                    return franchiseRepository.save(franchise);
                });
    }

    public Mono<Franchise> updateProductStock(String franchiseId, String branchId, String productId, UpdateStockRequest request) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franchise not found with id: " + franchiseId)))
                .flatMap(franchise -> {
                    Branch branch = franchise.getBranches().stream()
                            .filter(b -> b.getId().equals(branchId))
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + branchId));
                    
                    Product product = branch.getProducts().stream()
                            .filter(p -> p.getId().equals(productId))
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
                    
                    product.setStock(request.getStock());
                    return franchiseRepository.save(franchise);
                });
    }

    public Mono<Franchise> updateProductName(String franchiseId, String branchId, String productId, UpdateNameRequest request) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franchise not found with id: " + franchiseId)))
                .flatMap(franchise -> {
                    Branch branch = franchise.getBranches().stream()
                            .filter(b -> b.getId().equals(branchId))
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id: " + branchId));
                    
                    Product product = branch.getProducts().stream()
                            .filter(p -> p.getId().equals(productId))
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
                    
                    product.setName(request.getName());
                    return franchiseRepository.save(franchise);
                });
    }

    public Flux<TopProductResponse> getTopProductsByBranch(String franchiseId) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Franchise not found with id: " + franchiseId)))
                .flatMapMany(franchise -> Flux.fromIterable(franchise.getBranches())
                        .filter(branch -> !branch.getProducts().isEmpty())
                        .map(branch -> {
                            Product topProduct = branch.getProducts().stream()
                                    .max(Comparator.comparing(Product::getStock))
                                    .orElse(null);
                            
                            if (topProduct == null) {
                                return null;
                            }
                            
                            return TopProductResponse.builder()
                                    .productId(topProduct.getId())
                                    .productName(topProduct.getName())
                                    .stock(topProduct.getStock())
                                    .branchId(branch.getId())
                                    .branchName(branch.getName())
                                    .build();
                        })
                        .filter(Objects::nonNull)
                );
    }
}
