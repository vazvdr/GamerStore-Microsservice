package com.gamerstore.cart_service.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.gamerstore.cart_service.clients.ProductClient;
import com.gamerstore.cart_service.clients.ShippingClient;
import com.gamerstore.cart_service.dto.CartItemDTO;
import com.gamerstore.cart_service.dto.CartResponseDTO;
import com.gamerstore.cart_service.dto.CartSyncItemDTO;
import com.gamerstore.shared.product.dto.ProductResponseDTO;
import com.gamerstore.shared.shipping.dto.ApplyShippingRequestDTO;
import com.gamerstore.shared.shipping.dto.ShippingOptionDTO;
import com.gamerstore.shared.shipping.dto.ShippingRequestDTO;
import com.gamerstore.shared.shipping.dto.ShippingResponseDTO;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CartService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ProductClient productClient;
    private final ShippingClient shippingClient;

    public CartService(
            RedisTemplate<String, Object> redisTemplate,
            ProductClient productClient,
            ShippingClient shippingClient) {

        this.redisTemplate = redisTemplate;
        this.productClient = productClient;
        this.shippingClient = shippingClient;
    }

    private String getKey(String userId) {
        return "cart:" + userId;
    }

    private HashOperations<String, Object, Object> hash() {
        return redisTemplate.opsForHash();
    }

    public CartResponseDTO syncCart(String userId, List<CartSyncItemDTO> items) {

        String key = getKey(userId);

        Map<Object, Object> existingItems = hash().entries(key);

        ObjectMapper mapper = new ObjectMapper();

        for (CartSyncItemDTO syncItem : items) {

            Long productId = syncItem.getProductId();
            String field = productId.toString();
            Integer quantityToAdd = syncItem.getQuantity();

            CartItemDTO existingItem = null;

            Object obj = existingItems.get(field);

            if (obj instanceof CartItemDTO) {
                existingItem = (CartItemDTO) obj;
            } else if (obj instanceof Map) {
                existingItem = mapper.convertValue(obj, CartItemDTO.class);
            }

            ProductResponseDTO product = productClient.getProductById(productId);

            if (existingItem != null) {

                // 🔥 MERGE (soma quantidades)
                int newQuantity = existingItem.getQuantidade() + quantityToAdd;

                // 🔴 Respeitar estoque
                int stock = product.stock();
                if (newQuantity > stock) {
                    newQuantity = stock;
                }

                existingItem.setQuantidade(newQuantity);
                existingItem.setEstoque(stock);

                hash().put(key, field, existingItem);

            } else {

                // 🔴 Produto novo no carrinho
                int stock = product.stock();
                int finalQuantity = Math.min(quantityToAdd, stock);

                CartItemDTO newItem = new CartItemDTO(
                        product.id(),
                        product.name(),
                        product.description(),
                        product.price(),
                        finalQuantity,
                        product.imageUrl(),
                        stock);

                hash().put(key, field, newItem);
            }
        }

        return getCart(userId);
    }

    public CartResponseDTO addItem(String userId, Long productId) {

        String key = getKey(userId);
        String field = productId.toString();

        CartItemDTO item = (CartItemDTO) hash().get(key, field);

        if (item == null) {

            ProductResponseDTO product = productClient.getProductById(productId);

            item = new CartItemDTO(
                    product.id(),
                    product.name(),
                    product.description(),
                    product.price(),
                    1,
                    product.imageUrl(),
                    product.stock());

        } else {

            item.setQuantidade(item.getQuantidade() + 1);
        }

        hash().put(key, field, item);

        return getCart(userId);
    }

    public CartResponseDTO increaseQuantity(String userId, Long productId) {

        String key = getKey(userId);
        String field = productId.toString();
        CartItemDTO item = (CartItemDTO) hash().get(key, field);

        if (item == null) {
            throw new RuntimeException("Produto não encontrado no carrinho");
        }
        item.setQuantidade(item.getQuantidade() + 1);
        hash().put(key, field, item);
        return getCart(userId);
    }

    public CartResponseDTO decreaseQuantity(String userId, Long productId) {

        String key = getKey(userId);
        String field = productId.toString();
        CartItemDTO item = (CartItemDTO) hash().get(key, field);

        if (item == null) {
            throw new RuntimeException("Produto não encontrado no carrinho");
        }

        if (item.getQuantidade() <= 1) {
            hash().delete(key, field);
        } else {
            item.setQuantidade(item.getQuantidade() - 1);
            hash().put(key, field, item);
        }

        return getCart(userId);
    }

    public CartResponseDTO removeItem(String userId, Long productId) {
        String key = getKey(userId);
        hash().delete(key, productId.toString());
        return getCart(userId);
    }

    public CartResponseDTO getCart(String userId) {

        String key = getKey(userId);
        Map<Object, Object> entries = hash().entries(key);

        List<CartItemDTO> items = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        ObjectMapper mapper = new ObjectMapper();

        // 1️⃣ Converter objetos do Redis para CartItemDTO
        List<CartItemDTO> cartItemsFromRedis = new ArrayList<>();
        List<Long> productIds = new ArrayList<>();

        for (Object obj : entries.values()) {
            CartItemDTO cartItem = null;

            if (obj instanceof CartItemDTO) {
                cartItem = (CartItemDTO) obj;
            } else if (obj instanceof Map) {
                cartItem = mapper.convertValue(obj, CartItemDTO.class);
            }

            if (cartItem != null) {
                cartItemsFromRedis.add(cartItem);
                productIds.add(cartItem.getProductId());
            }
        }

        // 2️⃣ Buscar produtos no ProductService usando batch
        List<ProductResponseDTO> products = new ArrayList<>();

        if (!productIds.isEmpty()) {
            products = productClient.getProductsByIds(productIds);
        }

        // 3️⃣ Atualizar estoque e calcular subtotal
        for (CartItemDTO cartItem : cartItemsFromRedis) {

            for (ProductResponseDTO product : products) {
                if (product.id().equals(cartItem.getProductId())) {
                    cartItem.setEstoque(product.stock());
                    break;
                }
            }

            items.add(cartItem);

            subtotal = subtotal.add(
                    cartItem.getPreco().multiply(
                            BigDecimal.valueOf(cartItem.getQuantidade())));
        }

        // 4️⃣ Recuperar shipping e total do Redis
        BigDecimal shippingValue = BigDecimal.ZERO;
        BigDecimal total = subtotal;

        Object shippingObj = entries.get("shippingValue");
        Object totalObj = entries.get("total");

        if (shippingObj instanceof BigDecimal) {
            shippingValue = (BigDecimal) shippingObj;
        }

        if (totalObj instanceof BigDecimal) {
            total = (BigDecimal) totalObj;
        }

        return new CartResponseDTO(
                items,
                subtotal,
                shippingValue,
                total);
    }

    public void clearCart(String userId) {
        redisTemplate.delete(getKey(userId));
    }

    public CartResponseDTO applyShipping(String userId, ApplyShippingRequestDTO request) {
        String key = getKey(userId);
        Map<Object, Object> entries = hash().entries(key);
        if (entries.isEmpty()) {
            throw new RuntimeException("Carrinho está vazio");
        }

        List<CartItemDTO> items = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        for (Object obj : entries.values()) {
            if (!(obj instanceof CartItemDTO)) {
                continue;
            }

            CartItemDTO item = (CartItemDTO) obj;
            items.add(item);
            subtotal = subtotal.add(
                    item.getPreco().multiply(
                            BigDecimal.valueOf(item.getQuantidade())));
        }

        ShippingResponseDTO response = shippingClient.calcularFrete(
                new ShippingRequestDTO(request.cep()));
        ShippingOptionDTO selected = response.opcoes()
                .stream()
                .filter(op -> op.tipo().equalsIgnoreCase(request.tipoFrete()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Tipo de frete inválido"));

        BigDecimal shippingValue = selected.valor();
        BigDecimal total = subtotal.add(shippingValue);

        // 🔴 SALVANDO FRETE NO REDIS
        hash().put(key, "shippingValue", shippingValue);
        hash().put(key, "total", total);

        return new CartResponseDTO(
                items,
                subtotal,
                shippingValue,
                total);
    }
}