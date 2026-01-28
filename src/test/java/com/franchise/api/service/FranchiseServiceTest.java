package com.franchise.api.service;

import com.franchise.api.dto.RequestDTOs.*;
import com.franchise.api.exception.ResourceNotFoundException;
import com.franchise.api.model.Branch;
import com.franchise.api.model.Franchise;
import com.franchise.api.model.Product;
import com.franchise.api.repository.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FranchiseServiceTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    @InjectMocks
    private FranchiseService franchiseService;

    private Franchise testFranchise;

    @BeforeEach
    void setUp() {
        Product testProduct = Product.builder()
                .id("prod-1")
                .name("Test Product")
                .stock(100)
                .build();

        List<Product> products = new ArrayList<>();
        products.add(testProduct);

        Branch testBranch = Branch.builder()
                .id("branch-1")
                .name("Test Branch")
                .products(products)
                .build();

        List<Branch> branches = new ArrayList<>();
        branches.add(testBranch);

        testFranchise = Franchise.builder()
                .id("franchise-1")
                .name("Test Franchise")
                .branches(branches)
                .build();
    }

    @Test
    void createFranchise_ShouldReturnCreatedFranchise() {
        CreateFranchiseRequest request = CreateFranchiseRequest.builder()
                .name("New Franchise")
                .build();

        Franchise expectedFranchise = Franchise.builder()
                .id("new-franchise-1")
                .name("New Franchise")
                .build();

        when(franchiseRepository.save(any(Franchise.class)))
                .thenReturn(Mono.just(expectedFranchise));

        StepVerifier.create(franchiseService.createFranchise(request))
                .expectNextMatches(franchise -> 
                    franchise.getName().equals("New Franchise") &&
                    franchise.getId().equals("new-franchise-1")
                )
                .verifyComplete();

        verify(franchiseRepository, times(1)).save(any(Franchise.class));
    }

    @Test
    void getAllFranchises_ShouldReturnAllFranchises() {
        when(franchiseRepository.findAll())
                .thenReturn(Flux.just(testFranchise));

        StepVerifier.create(franchiseService.getAllFranchises())
                .expectNext(testFranchise)
                .verifyComplete();

        verify(franchiseRepository, times(1)).findAll();
    }

    @Test
    void getFranchiseById_WhenExists_ShouldReturnFranchise() {
        when(franchiseRepository.findById("franchise-1"))
                .thenReturn(Mono.just(testFranchise));

        StepVerifier.create(franchiseService.getFranchiseById("franchise-1"))
                .expectNext(testFranchise)
                .verifyComplete();

        verify(franchiseRepository, times(1)).findById("franchise-1");
    }

    @Test
    void getFranchiseById_WhenNotExists_ShouldThrowException() {
        when(franchiseRepository.findById("non-existent"))
                .thenReturn(Mono.empty());

        StepVerifier.create(franchiseService.getFranchiseById("non-existent"))
                .expectError(ResourceNotFoundException.class)
                .verify();

        verify(franchiseRepository, times(1)).findById("non-existent");
    }

    @Test
    void updateFranchiseName_ShouldUpdateAndReturnFranchise() {
        UpdateNameRequest request = UpdateNameRequest.builder()
                .name("Updated Name")
                .build();

        Franchise updatedFranchise = Franchise.builder()
                .id("franchise-1")
                .name("Updated Name")
                .branches(testFranchise.getBranches())
                .build();

        when(franchiseRepository.findById("franchise-1"))
                .thenReturn(Mono.just(testFranchise));
        when(franchiseRepository.save(any(Franchise.class)))
                .thenReturn(Mono.just(updatedFranchise));

        StepVerifier.create(franchiseService.updateFranchiseName("franchise-1", request))
                .expectNextMatches(franchise -> franchise.getName().equals("Updated Name"))
                .verifyComplete();

        verify(franchiseRepository, times(1)).findById("franchise-1");
        verify(franchiseRepository, times(1)).save(any(Franchise.class));
    }

    @Test
    void addBranchToFranchise_ShouldAddBranchAndReturnFranchise() {
        CreateBranchRequest request = CreateBranchRequest.builder()
                .name("New Branch")
                .build();

        when(franchiseRepository.findById("franchise-1"))
                .thenReturn(Mono.just(testFranchise));
        when(franchiseRepository.save(any(Franchise.class)))
                .thenReturn(Mono.just(testFranchise));

        StepVerifier.create(franchiseService.addBranchToFranchise("franchise-1", request))
                .expectNextMatches(franchise -> 
                    franchise.getBranches().size() == 2 &&
                    franchise.getBranches().get(1).getName().equals("New Branch")
                )
                .verifyComplete();

        verify(franchiseRepository, times(1)).findById("franchise-1");
        verify(franchiseRepository, times(1)).save(any(Franchise.class));
    }

    @Test
    void addProductToBranch_ShouldAddProductAndReturnFranchise() {
        CreateProductRequest request = CreateProductRequest.builder()
                .name("New Product")
                .stock(50)
                .build();

        when(franchiseRepository.findById("franchise-1"))
                .thenReturn(Mono.just(testFranchise));
        when(franchiseRepository.save(any(Franchise.class)))
                .thenReturn(Mono.just(testFranchise));

        StepVerifier.create(franchiseService.addProductToBranch("franchise-1", "branch-1", request))
                .expectNextMatches(franchise -> {
                    Branch branch = franchise.getBranches().get(0);
                    return branch.getProducts().size() == 2 &&
                           branch.getProducts().get(1).getName().equals("New Product") &&
                           branch.getProducts().get(1).getStock() == 50;
                })
                .verifyComplete();

        verify(franchiseRepository, times(1)).findById("franchise-1");
        verify(franchiseRepository, times(1)).save(any(Franchise.class));
    }

    @Test
    void deleteProductFromBranch_ShouldRemoveProductAndReturnFranchise() {
        when(franchiseRepository.findById("franchise-1"))
                .thenReturn(Mono.just(testFranchise));
        when(franchiseRepository.save(any(Franchise.class)))
                .thenReturn(Mono.just(testFranchise));

        StepVerifier.create(franchiseService.deleteProductFromBranch("franchise-1", "branch-1", "prod-1"))
                .expectNextMatches(franchise -> {
                    Branch branch = franchise.getBranches().get(0);
                    return branch.getProducts().isEmpty();
                })
                .verifyComplete();

        verify(franchiseRepository, times(1)).findById("franchise-1");
        verify(franchiseRepository, times(1)).save(any(Franchise.class));
    }

    @Test
    void updateProductStock_ShouldUpdateStockAndReturnFranchise() {
        UpdateStockRequest request = UpdateStockRequest.builder()
                .stock(200)
                .build();

        when(franchiseRepository.findById("franchise-1"))
                .thenReturn(Mono.just(testFranchise));
        when(franchiseRepository.save(any(Franchise.class)))
                .thenReturn(Mono.just(testFranchise));

        StepVerifier.create(franchiseService.updateProductStock("franchise-1", "branch-1", "prod-1", request))
                .expectNextMatches(franchise -> {
                    Product product = franchise.getBranches().get(0).getProducts().get(0);
                    return product.getStock() == 200;
                })
                .verifyComplete();

        verify(franchiseRepository, times(1)).findById("franchise-1");
        verify(franchiseRepository, times(1)).save(any(Franchise.class));
    }

    @Test
    void getTopProductsByBranch_ShouldReturnTopProductsPerBranch() {
        when(franchiseRepository.findById("franchise-1"))
                .thenReturn(Mono.just(testFranchise));

        StepVerifier.create(franchiseService.getTopProductsByBranch("franchise-1"))
                .expectNextMatches(response ->
                    response.getProductName().equals("Test Product") &&
                    response.getStock() == 100 &&
                    response.getBranchName().equals("Test Branch")
                )
                .verifyComplete();

        verify(franchiseRepository, times(1)).findById("franchise-1");
    }
}
