package sample.cafekiosk.api.service.order;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import sample.cafekiosk.api.controller.order.request.OrderCreateRequest;
import sample.cafekiosk.domain.order.OrderRepository;
import sample.cafekiosk.domain.orderproduct.OrderProductRepository;
import sample.cafekiosk.domain.product.Product;
import sample.cafekiosk.domain.product.ProductRepository;
import sample.cafekiosk.domain.product.ProductType;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static sample.cafekiosk.domain.product.ProductSellingStatus.SELLING;
import static sample.cafekiosk.domain.product.ProductType.HANDMADE;

@ActiveProfiles("test")
@SpringBootTest
class OrderServiceTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private OrderService orderService;

    @AfterEach
    void tearDown() {
        orderProductRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
    }

    @DisplayName("주문번호 리스트를 받아 주문을 생성한다.")
    @Test
    void createOrder() {
        //given
        LocalDateTime registerdDateTime = LocalDateTime.now();

        Product product1 = createProduct(HANDMADE,"001",1000);
        Product product2 = createProduct(HANDMADE,"002",3000);
        Product product3 = createProduct(HANDMADE,"003",5000);
        productRepository.saveAll(List.of(product1,product2,product3));

        OrderCreateRequest request = OrderCreateRequest.builder()
                .productNumbers(List.of("001", "002"))
                .build();
        //when
        OrderResponse orderResponse = orderService.createOrder(request, registerdDateTime);

        //then
        assertThat(orderResponse.getId()).isNotNull();
        assertThat(orderResponse)
                .extracting("registeredDateTime","totalPrice")
                .contains(registerdDateTime, 4000);

        assertThat(orderResponse.getProducts()).hasSize(2)
                .extracting("productNumber","price")
                .containsExactlyInAnyOrder(
                        tuple("001",1000),
                        tuple("002",3000)
                );

    }

    @DisplayName("중복되는 상품번호 리스트로 주문을 생성할 수 있다.")
    @Test
    void createOrderWithDuplicateProductNumbers() {
        //given
        LocalDateTime registerdDateTime = LocalDateTime.now();

        Product product1 = createProduct(HANDMADE,"001",1000);
        Product product2 = createProduct(HANDMADE,"002",3000);
        Product product3 = createProduct(HANDMADE,"003",5000);
        productRepository.saveAll(List.of(product1,product2,product3));

        OrderCreateRequest request = OrderCreateRequest.builder()
                .productNumbers(List.of("001", "001"))
                .build();
        //when
        OrderResponse orderResponse = orderService.createOrder(request, registerdDateTime);

        //then
        assertThat(orderResponse.getId()).isNotNull();
        assertThat(orderResponse)
                .extracting("registeredDateTime","totalPrice")
                .contains(registerdDateTime, 2000);

        assertThat(orderResponse.getProducts()).hasSize(2)
                .extracting("productNumber","price")
                .containsExactlyInAnyOrder(
                        tuple("001",1000),
                        tuple("001",1000)
                );
    }

    private Product createProduct(ProductType type, String productNumber, int price) {
        return Product.builder()
                .type(type)
                .productNumber(productNumber)
                .price(price)
                .sellingStatus(SELLING)
                .name("메뉴 이름")
                .build();
    }
}