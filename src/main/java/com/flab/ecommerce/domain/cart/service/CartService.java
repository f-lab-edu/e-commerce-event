package com.flab.ecommerce.domain.cart.service;

import com.flab.ecommerce.domain.cart.dto.CartItemAddRequestDTO;
import com.flab.ecommerce.domain.cart.dto.CartItemResponseDTO;
import com.flab.ecommerce.domain.cart.dto.CartResponseDTO;
import com.flab.ecommerce.domain.cart.entity.Cart;
import com.flab.ecommerce.domain.cart.entity.CartItem;
import com.flab.ecommerce.domain.cart.repository.CartRepository;
import com.flab.ecommerce.domain.product.entity.Product;
import com.flab.ecommerce.domain.product.exception.ProductNotFoundException;
import com.flab.ecommerce.domain.product.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartResponseDTO getCart(Long memberId) {
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseGet(() -> cartRepository.save(new Cart(memberId)));

        List<CartItemResponseDTO> items = cart.getItems().stream()
                .map(item -> {
                    Product product = productRepository.findById(item.getProduct().getId())
                            .orElseThrow(() -> new ProductNotFoundException(item.getProduct().getId()));

                    return new CartItemResponseDTO(item, product);
                }).toList();

        return new CartResponseDTO(items, cart.getTotalPrice());
    }

    @Transactional
    public CartResponseDTO addItem(Long memberId, CartItemAddRequestDTO requestDTO) {
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseGet(() -> cartRepository.save(new Cart(memberId)));

        Product product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(requestDTO.getProductId()));

        product.checkStock(requestDTO.getQuantity());

        CartItem cartItem = cart.getItem(product.getId())
                .orElseGet(() -> {
                    CartItem newItem = new CartItem(cart, product, 0);
                    cart.getItems().add(newItem);
                    return newItem;
                });

        cartItem.increaseQuantity(requestDTO.getQuantity());
        List<CartItemResponseDTO> items = cart.getItems().stream()
                .map(item -> new CartItemResponseDTO(item, item.getProduct()))
                .toList();

        return new CartResponseDTO(items, cart.getTotalPrice());
    }

}



