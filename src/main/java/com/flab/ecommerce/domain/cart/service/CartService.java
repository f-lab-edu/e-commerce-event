package com.flab.ecommerce.domain.cart.service;

import com.flab.ecommerce.domain.cart.dto.CartItemAddRequestDTO;
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

import java.util.Optional;

@Service
@AllArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartResponseDTO getCart(Long memberId) {
            Cart cart = cartRepository.findByMemberId(memberId)
                    .orElseGet(() -> cartRepository.save(new Cart(memberId)));

            return new CartResponseDTO(cart);
        }

    @Transactional
    public CartResponseDTO addItem(Long memberId, CartItemAddRequestDTO requestDTO) {
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseGet(() -> cartRepository.save(new Cart(memberId)));

        Product product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(requestDTO.getProductId()));

        Optional<CartItem> existingItem = cart.getItem(product.getId());
        if (existingItem.isPresent()) {
            existingItem.get().increaseQuantity(requestDTO.getQuantity());
        } else {
            CartItem newItem = new CartItem(cart, product, requestDTO.getQuantity());
            cart.getItems().add(newItem);
        }

        return new CartResponseDTO(cart);
    }

}
