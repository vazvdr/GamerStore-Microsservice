package com.gamerstore.cart_service.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import com.gamerstore.cart_service.clients.ProductClient;
import com.gamerstore.cart_service.clients.ShippingClient;
import com.gamerstore.cart_service.dto.CartItemDTO;
import com.gamerstore.cart_service.dto.CartResponseDTO;
import com.gamerstore.shared.product.dto.ProductResponseDTO;
import com.gamerstore.shared.shipping.dto.*;

class CartServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @Mock
    private ProductClient productClient;

    @Mock
    private ShippingClient shippingClient;

    @InjectMocks
    private CartService cartService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
    }

    // ===============================
    // ✅ ADD ITEM
    // ===============================
    @Test
    void shouldAddNewItemToCart() {

        String userId = "1";
        Long productId = 10L;

        when(hashOperations.get(any(), any())).thenReturn(null);

        ProductResponseDTO product = new ProductResponseDTO(
                productId,
                "Mouse Gamer",
                "Desc",
                BigDecimal.valueOf(100),
                10,
                "img");

        when(productClient.getProductById(productId)).thenReturn(product);
        when(hashOperations.entries(any())).thenReturn(new HashMap<>());

        CartResponseDTO response = cartService.addItem(userId, productId);

        verify(hashOperations).put(any(), eq(productId.toString()), any());
        assertNotNull(response);
    }

    @Test
    void shouldIncreaseQuantityIfItemAlreadyExists() {

        Long productId = 10L;

        CartItemDTO item = new CartItemDTO(
                productId, "Mouse", "Desc",
                BigDecimal.valueOf(100), 1,
                "img", 10);

        when(hashOperations.get(any(), any())).thenReturn(item);
        when(hashOperations.entries(any())).thenReturn(Map.of(productId.toString(), item));

        cartService.addItem("1", productId);

        assertEquals(2, item.getQuantidade());
        verify(hashOperations).put(any(), eq(productId.toString()), eq(item));
    }

    // ===============================
    // ➕ INCREASE
    // ===============================
    @Test
    void shouldIncreaseQuantity() {

        CartItemDTO item = new CartItemDTO(1L, "Prod", "Desc",
                BigDecimal.TEN, 1, "img", 10);

        when(hashOperations.get(any(), any())).thenReturn(item);
        when(hashOperations.entries(any())).thenReturn(Map.of("1", item));

        CartResponseDTO response = cartService.increaseQuantity("1", 1L);

        assertEquals(2, item.getQuantidade());
        assertNotNull(response);
    }

    @Test
    void shouldThrowWhenIncreasingNonExistingItem() {

        when(hashOperations.get(any(), any())).thenReturn(null);

        assertThrows(RuntimeException.class, () -> cartService.increaseQuantity("1", 1L));
    }

    // ===============================
    // ➖ DECREASE
    // ===============================
    @Test
    void shouldDecreaseQuantity() {

        CartItemDTO item = new CartItemDTO(1L, "Prod", "Desc",
                BigDecimal.TEN, 2, "img", 10);

        when(hashOperations.get(any(), any())).thenReturn(item);
        when(hashOperations.entries(any())).thenReturn(Map.of("1", item));

        cartService.decreaseQuantity("1", 1L);

        assertEquals(1, item.getQuantidade());
    }

    @Test
    void shouldRemoveItemWhenQuantityIsOne() {

        CartItemDTO item = new CartItemDTO(1L, "Prod", "Desc",
                BigDecimal.TEN, 1, "img", 10);

        when(hashOperations.get(any(), any())).thenReturn(item);
        when(hashOperations.entries(any())).thenReturn(new HashMap<>());

        cartService.decreaseQuantity("1", 1L);

        verify(hashOperations).delete(any(), eq("1"));
    }

    @Test
    void shouldThrowWhenDecreasingNonExistingItem() {

        when(hashOperations.get(any(), any())).thenReturn(null);

        assertThrows(RuntimeException.class, () -> cartService.decreaseQuantity("1", 1L));
    }

    // ===============================
    // ❌ REMOVE
    // ===============================
    @Test
    void shouldRemoveItem() {

        when(hashOperations.entries(any())).thenReturn(new HashMap<>());

        CartResponseDTO response = cartService.removeItem("1", 1L);

        verify(hashOperations).delete(any(), eq("1"));
        assertNotNull(response);
    }

    @Test
    void shouldReturnCartWithSubtotal() {

        CartItemDTO item = new CartItemDTO(
                1L, "Prod", "Desc",
                BigDecimal.valueOf(50), 2,
                "img", 10);

        Map<Object, Object> entries = new HashMap<>();
        entries.put("1", item);

        when(hashOperations.entries(any())).thenReturn(entries);

        ProductResponseDTO product = new ProductResponseDTO(
                1L, "Prod", "Desc",
                BigDecimal.valueOf(50),
                10,
                "img");

        when(productClient.getProductsByIds(any()))
                .thenReturn(Collections.singletonList(product));

        CartResponseDTO response = cartService.getCart("1");

        assertEquals(BigDecimal.valueOf(100), response.subTotal());
    }

    // ===============================
    // 🚚 APPLY SHIPPING
    // ===============================
    @Test
    void shouldApplyShipping() {

        CartItemDTO item = new CartItemDTO(
                1L, "Prod", "Desc",
                BigDecimal.valueOf(100), 1,
                "img", 10);

        Map<Object, Object> entries = new HashMap<>();
        entries.put("1", item);

        when(hashOperations.entries(any())).thenReturn(entries);

        // 🔥 MOCKANDO OPTION (sem construtor)
        ShippingOptionDTO option = mock(ShippingOptionDTO.class);
        when(option.tipo()).thenReturn("PAC");
        when(option.valor()).thenReturn(BigDecimal.valueOf(20));

        ShippingResponseDTO shippingResponse = new ShippingResponseDTO("12345-000", Collections.singletonList(option));

        when(shippingClient.calcularFrete(any()))
                .thenReturn(shippingResponse);

        ApplyShippingRequestDTO request = new ApplyShippingRequestDTO("12345-000", "PAC");

        CartResponseDTO response = cartService.applyShipping("1", request);

        assertEquals(BigDecimal.valueOf(20), response.shippingValue());
        assertEquals(BigDecimal.valueOf(120), response.total());

        verify(hashOperations).put(any(), eq("shippingValue"), eq(BigDecimal.valueOf(20)));
    }

    @Test
    void shouldThrowWhenCartIsEmpty() {

        when(hashOperations.entries(any())).thenReturn(new HashMap<>());

        ApplyShippingRequestDTO request = new ApplyShippingRequestDTO("12345-000", "PAC");

        assertThrows(RuntimeException.class, () -> cartService.applyShipping("1", request));
    }

    @Test
    void shouldThrowWhenShippingTypeInvalid() {

        CartItemDTO item = new CartItemDTO(
                1L, "Prod", "Desc",
                BigDecimal.valueOf(100), 1,
                "img", 10);

        Map<Object, Object> entries = new HashMap<>();
        entries.put("1", item);

        when(hashOperations.entries(any())).thenReturn(entries);

        ShippingResponseDTO response = new ShippingResponseDTO("12345-000", Collections.emptyList());

        when(shippingClient.calcularFrete(any()))
                .thenReturn(response);

        ApplyShippingRequestDTO request = new ApplyShippingRequestDTO("12345-000", "PAC");

        assertThrows(RuntimeException.class, () -> cartService.applyShipping("1", request));
    }
}