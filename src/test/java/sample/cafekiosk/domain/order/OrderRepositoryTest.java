package sample.cafekiosk.domain.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import sample.cafekiosk.IntegrationTestSupport;
import sample.cafekiosk.domain.product.Product;
import sample.cafekiosk.domain.product.ProductRepository;
import sample.cafekiosk.domain.product.ProductSellingStatus;
import sample.cafekiosk.domain.product.ProductType;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static sample.cafekiosk.domain.order.OrderStatus.INIT;
import static sample.cafekiosk.domain.product.ProductSellingStatus.*;
import static sample.cafekiosk.domain.product.ProductType.HANDMADE;

@Transactional
class OrderRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @DisplayName("주문 상태를 통해 원하는 시작 시간과 종료시간을 지정하여 주문을 조회한다")
    @Test
    void findOrderBy() {
        //given
        LocalDateTime registerdDateTime = LocalDateTime.now();
        Product product1 = createProduct("001", HANDMADE, SELLING, "아메리카노", 4000);
        Product product2 = createProduct("002", HANDMADE, HOLD, "카페라떼", 4500);
        Product product3 = createProduct("003", HANDMADE, STOP_SELLING, "팥빙수", 7000);
        productRepository.saveAll(List.of(product1,product2,product3));
        List<Product> products1 = List.of(product1,product2);
        List<Product> products2 = List.of(product2,product3);
        Order order1 = Order.create(products1, registerdDateTime);
        Order order2 = Order.create(products2, registerdDateTime);
        orderRepository.saveAll(List.of(order1,order2));

        //when
        List<Order> findOrders = orderRepository.findOrdersBy(registerdDateTime, registerdDateTime.plusHours(1), INIT);

        //then
        assertThat(findOrders).hasSize(2)
            .extracting("totalPrice","orderStatus","registeredDateTime")
            .containsExactlyInAnyOrder(
                tuple(8500,INIT,registerdDateTime),
                tuple(11500,INIT,registerdDateTime)
            );
    }

    private Product createProduct(String productNumber, ProductType type, ProductSellingStatus productSellingStatus, String name, int price) {
        return Product.builder()
            .productNumber(productNumber)
            .type(type)
            .sellingStatus(productSellingStatus)
            .name(name)
            .price(price)
            .build();
    }
}