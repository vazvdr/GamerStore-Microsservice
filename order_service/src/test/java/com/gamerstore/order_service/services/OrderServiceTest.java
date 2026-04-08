package com.gamerstore.order_service.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import com.gamerstore.order_service.models.Order;
import com.gamerstore.order_service.models.OrderItem;
import com.gamerstore.order_service.repositories.OrderRepository;
import com.gamerstore.shared.messaging.dto.PaymentConfirmedEvent;
import com.gamerstore.shared.messaging.dto.ItemDTO;

class OrderServiceTest {

    @Mock
    private OrderRepository repository;

    @InjectMocks
    private OrderService orderService;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ===============================
    // ✅ CREATE ORDER
    // ===============================
    @Test
    void shouldCreateOrderSuccessfully() {

        PaymentConfirmedEvent event = mockEventSingleItem();

        orderService.createOrder(event);

        verify(repository).save(orderCaptor.capture());

        Order saved = orderCaptor.getValue();

        assertEquals(1L, saved.getUserId());
        assertEquals("pi_123", saved.getPaymentIntentId());
        assertEquals(BigDecimal.valueOf(150), saved.getTotal());
        assertEquals(BigDecimal.valueOf(130), saved.getSubtotal());
        assertEquals(BigDecimal.valueOf(20), saved.getShipping());

        assertEquals(1, saved.getItems().size());

        OrderItem item = saved.getItems().get(0);

        assertEquals(10L, item.getProductId());
        assertEquals(2, item.getQuantity());
        assertEquals("Mouse Gamer", item.getName());
        assertEquals("img.png", item.getImageUrl());
        assertEquals(BigDecimal.valueOf(75), item.getPrice());
    }

    // ===============================
    // ✅ MULTIPLE ITEMS
    // ===============================
    @Test
    void shouldCreateOrderWithMultipleItems() {

        PaymentConfirmedEvent event = mockEventMultipleItems();

        orderService.createOrder(event);

        verify(repository).save(orderCaptor.capture());

        Order saved = orderCaptor.getValue();

        assertEquals(2, saved.getItems().size());
    }

    // ===============================
    // ⚠️ EMPTY ITEMS
    // ===============================
    @Test
    void shouldCreateOrderWithEmptyItemsList() {

        PaymentConfirmedEvent event = mock(PaymentConfirmedEvent.class);

        when(event.getUserId()).thenReturn(1L);
        when(event.getPaymentIntentId()).thenReturn("pi_empty");
        when(event.getAmount()).thenReturn(BigDecimal.ZERO);
        when(event.getSubtotal()).thenReturn(BigDecimal.ZERO);
        when(event.getShipping()).thenReturn(BigDecimal.ZERO);
        when(event.getItems()).thenReturn(List.of());

        orderService.createOrder(event);

        verify(repository).save(orderCaptor.capture());

        Order saved = orderCaptor.getValue();

        assertNotNull(saved.getItems());
        assertTrue(saved.getItems().isEmpty());
    }

    // ===============================
    // ❌ IDEMPOTÊNCIA ATUAL
    // ===============================
    @Test
    void shouldCreateDuplicateOrdersWhenSameEventIsProcessedTwice() {

        PaymentConfirmedEvent event = mockEventSingleItem();

        orderService.createOrder(event);
        orderService.createOrder(event);

        verify(repository, times(2)).save(any(Order.class));
    }

    // ===============================
    // 💥 DATABASE FAILURE
    // ===============================
    @Test
    void shouldThrowExceptionWhenDatabaseFails() {

        PaymentConfirmedEvent event = mockEventSingleItem();

        when(repository.save(any()))
                .thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(event);
        });

        verify(repository).save(any());
    }

    // ===============================
    // 🔧 HELPERS
    // ===============================
    private PaymentConfirmedEvent mockEventSingleItem() {

        PaymentConfirmedEvent event = mock(PaymentConfirmedEvent.class);

        when(event.getUserId()).thenReturn(1L);
        when(event.getPaymentIntentId()).thenReturn("pi_123");
        when(event.getAmount()).thenReturn(BigDecimal.valueOf(150));
        when(event.getSubtotal()).thenReturn(BigDecimal.valueOf(130));
        when(event.getShipping()).thenReturn(BigDecimal.valueOf(20));

        ItemDTO item = mock(ItemDTO.class);

        when(item.getProductId()).thenReturn(10L);
        when(item.getQuantity()).thenReturn(2);
        when(item.getName()).thenReturn("Mouse Gamer");
        when(item.getImageUrl()).thenReturn("img.png");
        when(item.getPrice()).thenReturn(BigDecimal.valueOf(75));

        when(event.getItems()).thenReturn(List.of(item));

        return event;
    }

    private PaymentConfirmedEvent mockEventMultipleItems() {

        PaymentConfirmedEvent event = mock(PaymentConfirmedEvent.class);

        when(event.getUserId()).thenReturn(1L);
        when(event.getPaymentIntentId()).thenReturn("pi_multi");
        when(event.getAmount()).thenReturn(BigDecimal.valueOf(300));
        when(event.getSubtotal()).thenReturn(BigDecimal.valueOf(250));
        when(event.getShipping()).thenReturn(BigDecimal.valueOf(50));

        ItemDTO item1 = mock(ItemDTO.class);
        when(item1.getProductId()).thenReturn(1L);
        when(item1.getQuantity()).thenReturn(1);
        when(item1.getName()).thenReturn("Teclado");
        when(item1.getImageUrl()).thenReturn("teclado.png");
        when(item1.getPrice()).thenReturn(BigDecimal.valueOf(100));

        ItemDTO item2 = mock(ItemDTO.class);
        when(item2.getProductId()).thenReturn(2L);
        when(item2.getQuantity()).thenReturn(2);
        when(item2.getName()).thenReturn("Mouse");
        when(item2.getImageUrl()).thenReturn("mouse.png");
        when(item2.getPrice()).thenReturn(BigDecimal.valueOf(75));

        when(event.getItems()).thenReturn(List.of(item1, item2));

        return event;
    }
}