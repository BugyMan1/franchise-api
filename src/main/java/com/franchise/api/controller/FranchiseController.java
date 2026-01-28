package com.franchise.api.controller;

import com.franchise.api.dto.RequestDTOs.*;
import com.franchise.api.dto.TopProductResponse;
import com.franchise.api.model.Franchise;
import com.franchise.api.service.FranchiseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/franchises")
@RequiredArgsConstructor
@Tag(name = "Franchise Management", description = "API to manage franchises, branches and products")
public class FranchiseController {

    private final FranchiseService franchiseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new franchise")
    public Mono<Franchise> createFranchise(@Valid @RequestBody CreateFranchiseRequest request) {
        return franchiseService.createFranchise(request);
    }

    @GetMapping
    @Operation(summary = "Get all franchises")
    public Flux<Franchise> getAllFranchises() {
        return franchiseService.getAllFranchises();
    }

    @GetMapping("/{franchiseId}")
    @Operation(summary = "Get a franchise by ID")
    public Mono<Franchise> getFranchiseById(@PathVariable String franchiseId) {
        return franchiseService.getFranchiseById(franchiseId);
    }

    @PutMapping("/{franchiseId}/name")
    @Operation(summary = "Update a franchise name")
    public Mono<Franchise> updateFranchiseName(
            @PathVariable String franchiseId,
            @Valid @RequestBody UpdateNameRequest request) {
        return franchiseService.updateFranchiseName(franchiseId, request);
    }

    @PostMapping("/{franchiseId}/branches")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a new branch to the franchise")
    public Mono<Franchise> addBranchToFranchise(
            @PathVariable String franchiseId,
            @Valid @RequestBody CreateBranchRequest request) {
        return franchiseService.addBranchToFranchise(franchiseId, request);
    }

    @PutMapping("/{franchiseId}/branches/{branchId}/name")
    @Operation(summary = "Update a branch name")
    public Mono<Franchise> updateBranchName(
            @PathVariable String franchiseId,
            @PathVariable String branchId,
            @Valid @RequestBody UpdateNameRequest request) {
        return franchiseService.updateBranchName(franchiseId, branchId, request);
    }

    @PostMapping("/{franchiseId}/branches/{branchId}/products")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a new product to a branch")
    public Mono<Franchise> addProductToBranch(
            @PathVariable String franchiseId,
            @PathVariable String branchId,
            @Valid @RequestBody CreateProductRequest request) {
        return franchiseService.addProductToBranch(franchiseId, branchId, request);
    }

    @DeleteMapping("/{franchiseId}/branches/{branchId}/products/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a product from a branch")
    public Mono<Franchise> deleteProductFromBranch(
            @PathVariable String franchiseId,
            @PathVariable String branchId,
            @PathVariable String productId) {
        return franchiseService.deleteProductFromBranch(franchiseId, branchId, productId);
    }

    @PutMapping("/{franchiseId}/branches/{branchId}/products/{productId}/stock")
    @Operation(summary = "Update the stock of a product")
    public Mono<Franchise> updateProductStock(
            @PathVariable String franchiseId,
            @PathVariable String branchId,
            @PathVariable String productId,
            @Valid @RequestBody UpdateStockRequest request) {
        return franchiseService.updateProductStock(franchiseId, branchId, productId, request);
    }

    @PutMapping("/{franchiseId}/branches/{branchId}/products/{productId}/name")
    @Operation(summary = "Update a product name")
    public Mono<Franchise> updateProductName(
            @PathVariable String franchiseId,
            @PathVariable String branchId,
            @PathVariable String productId,
            @Valid @RequestBody UpdateNameRequest request) {
        return franchiseService.updateProductName(franchiseId, branchId, productId, request);
    }

    @GetMapping("/{franchiseId}/top-products")
    @Operation(summary = "Obtain the product with the largest stock for each branch of a franchise")
    public Flux<TopProductResponse> getTopProductsByBranch(@PathVariable String franchiseId) {
        return franchiseService.getTopProductsByBranch(franchiseId);
    }
}
