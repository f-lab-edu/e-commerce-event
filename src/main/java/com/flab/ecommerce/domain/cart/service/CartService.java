package com.flab.ecommerce.domain.cart.service;

import com.flab.ecommerce.domain.cart.dto.CartItemAddRequestDTO;
import com.flab.ecommerce.domain.cart.dto.CartItemResponseDTO;
import com.flab.ecommerce.domain.cart.dto.CartResponseDTO;
import com.flab.ecommerce.domain.cart.entity.Cart;
import com.flab.ecommerce.domain.cart.entity.CartItem;
import com.flab.ecommerce.domain.cart.exception.CartItemNotFoundException;
import com.flab.ecommerce.domain.cart.repository.CartRepository;
import com.flab.ecommerce.domain.product.entity.Product;
import com.flab.ecommerce.domain.product.enums.ProductStatus;
import com.flab.ecommerce.domain.product.service.ProductService;
import com.flab.ecommerce.domain.product.service.ProductStatusChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductService productService;
    private final ProductStatusChecker statusChecker;

    @Transactional
    public CartResponseDTO getCart(Long memberId) {
        Cart cart = findOrCreateCart(memberId);
        return new CartResponseDTO(cartToItemResponseList(cart), calculateTotal(cart));
    }

    @Transactional
    public CartResponseDTO addCartItem(Long memberId, CartItemAddRequestDTO request) {
        Cart cart = findOrCreateCart(memberId);
        Product product = productService.getProductOrThrow(request.getProductId());
        product.checkStock(request.getQuantity());

        CartItem cartItem = cart.findItem(product.getId())
                .orElseGet(() -> {
                    CartItem newItem = new CartItem(cart, product, 0);
                    cart.getItems().add(newItem);
                    return newItem;
                });

        cartItem.increaseQuantity(request.getQuantity());
        return new CartResponseDTO(cartToItemResponseList(cart), calculateTotal(cart));
    }

    @Transactional
    public CartResponseDTO updateCartItemQuantity(Long memberId, Long productId, int quantity) {
        Cart cart = findOrCreateCart(memberId);
        CartItem item = cart.findItem(productId)
                .orElseThrow(() -> new CartItemNotFoundException(productId));
        productService.getProductOrThrow(productId).checkStock(quantity);
        item.changeQuantity(quantity);
        return new CartResponseDTO(cartToItemResponseList(cart), calculateTotal(cart));
    }

    @Transactional
    public CartResponseDTO removeCartItem(Long memberId, Long productId) {
        Cart cart = findOrCreateCart(memberId);
        cart.removeItem(productId);
        return new CartResponseDTO(cartToItemResponseList(cart), calculateTotal(cart));
    }

    @Transactional
    public CartResponseDTO clearCart(Long memberId) {
        Cart cart = findOrCreateCart(memberId);
        cart.clearAllItems();
        return new CartResponseDTO(cartToItemResponseList(cart), calculateTotal(cart));
    }

    private Cart findOrCreateCart(Long memberId) {
        return cartRepository.findByMemberId(memberId)
                .orElseGet(() -> cartRepository.save(new Cart(memberId)));
    }


    private List<CartItemResponseDTO> cartToItemResponseList(Cart cart) {
        return cart.getItems().stream()
                .map(item -> CartItemResponseDTO.from(
                        item,
                        statusChecker.getStock(item.getProductId()),
                        statusChecker.check(item.getProductId())
                ))
                .toList();
    }

    private BigDecimal calculateTotal(Cart cart) {
        return cart.getItems().stream()
                .map(item -> item.getProductPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


}
