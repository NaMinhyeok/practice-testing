package sample.cafekiosk.docs.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import sample.cafekiosk.api.controller.product.ProductController;
import sample.cafekiosk.api.controller.product.dto.request.ProductCreateRequest;
import sample.cafekiosk.api.service.product.ProductService;
import sample.cafekiosk.api.service.product.request.ProductCreateServiceRequest;
import sample.cafekiosk.api.service.product.response.ProductResponse;
import sample.cafekiosk.docs.RestDocsSupport;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sample.cafekiosk.domain.product.ProductSellingStatus.HOLD;
import static sample.cafekiosk.domain.product.ProductSellingStatus.SELLING;
import static sample.cafekiosk.domain.product.ProductType.HANDMADE;

public class ProductControllerDocsTest extends RestDocsSupport {

    private final ProductService productService = mock(ProductService.class);

    @Override
    protected Object initController() {
        return new ProductController(productService);
    }

    @DisplayName("신규 상품을 등록하는 API")
    @Test
    void createProduct() throws Exception {
        ProductCreateRequest request = ProductCreateRequest.builder()
            .type(HANDMADE)
            .sellingStatus(SELLING)
            .name("아메리카노")
            .price(4000)
            .build();

        given(productService.createProduct(any(ProductCreateServiceRequest.class)))
            .willReturn(ProductResponse.builder()
                .id(1L)
                .productNumber("001")
                .type(HANDMADE)
                .sellingStatus(SELLING)
                .name("아메리카노")
                .price(4000)
                .build());

        mockMvc.perform(
                post("/api/v1/products/new")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("product-create",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("type").type(JsonFieldType.STRING)
                        .description("상품 타입"),
                    fieldWithPath("sellingStatus").type(JsonFieldType.STRING)
                        .optional()
                        .description("상품 판매 상태"),
                    fieldWithPath("name").type(JsonFieldType.STRING)
                        .description("상품 이름"),
                    fieldWithPath("price").type(JsonFieldType.NUMBER)
                        .description("상품 가격")
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
                        .description("상품 ID"),
                    fieldWithPath("data.productNumber").type(JsonFieldType.STRING)
                        .description("상품 번호"),
                    fieldWithPath("data.type").type(JsonFieldType.STRING)
                        .description("상품 타입"),
                    fieldWithPath("data.sellingStatus").type(JsonFieldType.STRING)
                        .description("상품 판매 상태"),
                    fieldWithPath("data.name").type(JsonFieldType.STRING)
                        .description("상품 이름"),
                    fieldWithPath("data.price").type(JsonFieldType.NUMBER)
                        .description("상품 가격")
                )
            ));
    }

    @DisplayName("판매된 상품을 조회하는 API")
    @Test
    void getSellingProducts() throws Exception {
        given(productService.getSellingProducts())
            .willReturn(
                List.of(
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
                        .build())
            );

        mockMvc.perform(
            get("/api/v1/products/selling")
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andDo(document("get-selling-products",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").type(JsonFieldType.NUMBER)
                        .description("코드"),
                    fieldWithPath("status").type(JsonFieldType.STRING)
                        .description("상태"),
                    fieldWithPath("message").type(JsonFieldType.STRING)
                        .description("메시지"),
                    fieldWithPath("data").type(JsonFieldType.ARRAY)
                        .description("응답 데이터"),
                    fieldWithPath("data[].id").type(JsonFieldType.NUMBER)
                        .description("상품 ID"),
                    fieldWithPath("data[].productNumber").type(JsonFieldType.STRING)
                        .description("상품 번호"),
                    fieldWithPath("data[].type").type(JsonFieldType.STRING)
                        .description("상품 타입"),
                    fieldWithPath("data[].sellingStatus").type(JsonFieldType.STRING)
                        .description("상품 판매 상태"),
                    fieldWithPath("data[].name").type(JsonFieldType.STRING)
                        .description("상품 이름"),
                    fieldWithPath("data[].price").type(JsonFieldType.NUMBER)
                        .description("상품 가격")
                ))
            );
    }
}
