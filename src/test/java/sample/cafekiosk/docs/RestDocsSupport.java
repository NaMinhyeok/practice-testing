package sample.cafekiosk.docs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

@ExtendWith(RestDocumentationExtension.class)
public abstract class RestDocsSupport {

    protected MockMvc mockMvc;
    protected ObjectMapper objectMapper = new ObjectMapper();

    // 방법 1. @SpringBootTest을 달아서 이용해야 한다. 하지만 SpringBootTest를 하는 비용을 생각해야함
//    @BeforeEach
//    void setUp(WebApplicationContext webApplicationContext,
//               RestDocumentationContextProvider provider) {
//        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
//            .apply(documentationConfiguration(provider))
//            .build();
//    }
    // 방법 2.
    @BeforeEach
    void setUp(RestDocumentationContextProvider provider) {
        this.mockMvc = MockMvcBuilders.standaloneSetup(initController())
            .apply(documentationConfiguration(provider))
            .build();
    }

    protected abstract Object initController();
}
