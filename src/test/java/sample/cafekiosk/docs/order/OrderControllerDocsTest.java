package sample.cafekiosk.docs.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import sample.cafekiosk.api.controller.order.OrderController;
import sample.cafekiosk.api.controller.order.request.OrderCreateRequest;
import sample.cafekiosk.api.service.order.OrderService;
import sample.cafekiosk.api.service.order.request.OrderCreateServiceRequest;
import sample.cafekiosk.api.service.order.response.OrderResponse;
import sample.cafekiosk.api.service.product.response.ProductResponse;
import sample.cafekiosk.docs.RestDocsSupport;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sample.cafekiosk.domain.product.ProductSellingStatus.HOLD;
import static sample.cafekiosk.domain.product.ProductSellingStatus.SELLING;
import static sample.cafekiosk.domain.product.ProductType.HANDMADE;

public class OrderControllerDocsTest extends RestDocsSupport {

    private final OrderService orderService = mock(OrderService.class);

    @Override
    protected Object initController() {
        return new OrderController(orderService);
    }

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @DisplayName("신규 주문을 생성하는 API")
    @Test
    void createOrder() throws Exception {
        OrderCreateRequest request = OrderCreateRequest.builder()
            .productNumbers(List.of("001", "002"))
            .build();

        List<ProductResponse> productResponses = List.of(
            ProductResponse.builder()
                .id(1L)
                .productNumber("001")
                .type(HANDMADE)
                .sellingStatus(SELLING)
                .name("아메리카노")
                .price(4000)
                .build(),
            ProductResponse.builder()
                .id(2L)
                .productNumber("002")
                .type(HANDMADE)
                .sellingStatus(HOLD)
                .name("카페라떼")
                .price(4500)
                .build()
        );


        given(orderService.createOrder(any(OrderCreateServiceRequest.class), any(LocalDateTime.class)))
            .willReturn(OrderResponse.builder()
                .id(1L)
                .totalPrice(8500)
                .registeredDateTime(LocalDateTime.now())
                .products(productResponses)
                .build());

        mockMvc.perform(
                post("/api/v1/orders/new")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("order-create",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("productNumbers").type(JsonFieldType.ARRAY)
                        .description("상품 번호 리스트")
                ),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER)
                        .description("코드"),
                    fieldWithPath("status").type(JsonFieldType.STRING)
                        .description("상태"),
                    fieldWithPath("message").type(JsonFieldType.STRING)
                        .description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.OBJECT)
                        .description("응답 데이터"),
                    fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                        .description("주문 ID"),
                    fieldWithPath("data.totalPrice").type(JsonFieldType.NUMBER)
                        .description("주문 총합 가격"),
                    fieldWithPath("data.registeredDateTime").type(JsonFieldType.STRING)
                        .description("상품 주문 시간"),
                    fieldWithPath("data.products").type(JsonFieldType.ARRAY)
                        .description("상품 판매 목록"),
                    fieldWithPath("data.products[].id").type(JsonFieldType.NUMBER)
                        .description("상품 ID"),
                    fieldWithPath("data.products[].productNumber").type(JsonFieldType.STRING)
                        .description("상품 번호"),
                    fieldWithPath("data.products[].type").type(JsonFieldType.STRING)
                        .description("상품 타입"),
                    fieldWithPath("data.products[].sellingStatus").type(JsonFieldType.STRING)
                        .description("상품 판매 상태"),
                    fieldWithPath("data.products[].name").type(JsonFieldType.STRING)
                        .description("상품 이름"),
                    fieldWithPath("data.products[].price").type(JsonFieldType.NUMBER)
                        .description("상품 가격")
                )
            ));
    }
}
