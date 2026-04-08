package com.gamerstore.payment_service.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;

import com.gamerstore.payment_service.clients.ProductClient;
import com.gamerstore.payment_service.clients.ProductResponse;
import com.gamerstore.payment_service.dto.PaymentRequestDTO;
import com.gamerstore.payment_service.messaging.PaymentEventPublisher;
import com.gamerstore.shared.messaging.dto.*;

import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import org.junit.jupiter.api.*;
import org.mockito.*;

class PaymentServiceTest {

    @Mock
    private PaymentEventPublisher publisher;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private PaymentService paymentService;

    @Captor
    private ArgumentCaptor<PaymentConfirmedEvent> confirmedCaptor;

    @Captor
    private ArgumentCaptor<PaymentFailedEvent> failedCaptor;

    @Captor
    private ArgumentCaptor<StockReserveEvent> stockCaptor;

    private MockedStatic<PaymentIntent> paymentIntentMock;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        paymentIntentMock = mockStatic(PaymentIntent.class);
    }

    @AfterEach
    void tearDown() {
        paymentIntentMock.close();
    }

    // ===============================
    // ✅ SUCCESS PAYMENT
    // ===============================
    @Test
    void shouldProcessPaymentSuccessfully() throws Exception {

        PaymentRequestDTO request = mockRequest();

        ProductResponse product = mockProduct();
        when(productClient.getProductById(1L)).thenReturn(product);

        PaymentIntent intent = mock(PaymentIntent.class);
        when(intent.getStatus()).thenReturn("succeeded");
        when(intent.getId()).thenReturn("pi_123");

        paymentIntentMock.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class))).thenReturn(intent);

        paymentService.processPayment(request);

        verify(publisher).publishStockReserve(stockCaptor.capture());
        verify(publisher).publishPaymentConfirmed(confirmedCaptor.capture());

        PaymentConfirmedEvent event = confirmedCaptor.getValue();

        assertEquals(1L, event.getUserId());
        assertEquals("pi_123", event.getPaymentIntentId());
        assertEquals(BigDecimal.valueOf(100), event.getAmount()); // ✅ CORRIGIDO
        assertEquals(BigDecimal.valueOf(100), event.getSubtotal());
        assertEquals(BigDecimal.ZERO, event.getShipping());

        assertEquals(1, event.getItems().size());

        ItemDTO item = event.getItems().get(0);
        assertEquals("Produto Teste", item.getName());
        assertEquals("img.png", item.getImageUrl());
    }

    // ===============================
    // ❌ PAYMENT FAILED
    // ===============================
    @Test
    void shouldPublishFailedEventWhenPaymentFails() throws Exception {

        PaymentRequestDTO request = mockRequest();

        ProductResponse product = mockProduct();
        when(productClient.getProductById(1L)).thenReturn(product);

        PaymentIntent intent = mock(PaymentIntent.class);
        when(intent.getStatus()).thenReturn("failed");

        paymentIntentMock.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class))).thenReturn(intent);

        paymentService.processPayment(request);

        verify(publisher).publishPaymentFailed(failedCaptor.capture());

        PaymentFailedEvent event = failedCaptor.getValue();

        assertEquals(1L, event.getUserId());
        assertEquals(1, event.getItems().size());
    }

    // ===============================
    // 💥 STRIPE ERROR
    // ===============================
    @Test
    void shouldThrowExceptionWhenStripeFails() throws Exception {

        PaymentRequestDTO request = mockRequest();

        ProductResponse product = mockProduct();
        when(productClient.getProductById(1L)).thenReturn(product);

        paymentIntentMock.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class)))
                .thenThrow(new RuntimeException("Stripe error"));

        assertThrows(RuntimeException.class, () -> {
            paymentService.processPayment(request);
        });
    }

    // ===============================
    // 🔁 MULTIPLE ITEMS
    // ===============================
    @Test
    void shouldProcessMultipleItemsCorrectly() throws Exception {

        PaymentRequestDTO request = mock(PaymentRequestDTO.class);

        when(request.getUserId()).thenReturn(1L);
        when(request.getAmount()).thenReturn(BigDecimal.valueOf(200));
        when(request.getStripeCustomerId()).thenReturn("cus_123");
        when(request.getPaymentMethodId()).thenReturn("pm_123");

        ItemDTO item1 = mockItem(1L, 1);
        ItemDTO item2 = mockItem(2L, 2);

        when(request.getItems()).thenReturn(List.of(item1, item2));

        when(productClient.getProductById(1L)).thenReturn(mockProduct(1L, 50));
        when(productClient.getProductById(2L)).thenReturn(mockProduct(2L, 50));

        PaymentIntent intent = mock(PaymentIntent.class);
        when(intent.getStatus()).thenReturn("succeeded");
        when(intent.getId()).thenReturn("pi_multi");

        paymentIntentMock.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class))).thenReturn(intent);

        paymentService.processPayment(request);

        verify(publisher).publishPaymentConfirmed(confirmedCaptor.capture());

        PaymentConfirmedEvent event = confirmedCaptor.getValue();

        assertEquals(2, event.getItems().size());

        // subtotal = 50*1 + 50*2 = 150
        assertEquals(BigDecimal.valueOf(150), event.getSubtotal());
    }

    // ===============================
    // 🔧 HELPERS
    // ===============================
    private PaymentRequestDTO mockRequest() {

        PaymentRequestDTO request = mock(PaymentRequestDTO.class);

        when(request.getUserId()).thenReturn(1L);
        when(request.getAmount()).thenReturn(BigDecimal.valueOf(100));
        when(request.getStripeCustomerId()).thenReturn("cus_123");
        when(request.getPaymentMethodId()).thenReturn("pm_123");

        ItemDTO item = mockItem(1L, 1);

        when(request.getItems()).thenReturn(List.of(item));

        return request;
    }

    private ItemDTO mockItem(Long productId, int quantity) {

        ItemDTO item = mock(ItemDTO.class);

        when(item.getProductId()).thenReturn(productId);
        when(item.getQuantity()).thenReturn(quantity);

        return item;
    }

    private ProductResponse mockProduct() {
        return mockProduct(1L, 100);
    }

    private ProductResponse mockProduct(Long id, int price) {

        ProductResponse product = new ProductResponse();

        product.setId(id);
        product.setName("Produto Teste");
        product.setImageUrl("img.png");
        product.setPrice(BigDecimal.valueOf(price));

        return product;
    }
}