package partner42.moduleapi.config.swagger;

import com.fasterxml.classmate.TypeResolver;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@RequiredArgsConstructor
@Configuration
public class SwaggerConfig {

    private final TypeResolver typeResolver;
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
            .ignoredParameterTypes(AuthenticationPrincipal.class)
            .useDefaultResponseMessages(true)
            .alternateTypeRules(AlternateTypeRules
                .newRule(typeResolver.resolve(Pageable.class), typeResolver.resolve(Page.class)))
            .select()
            .apis(RequestHandlerSelectors.basePackage("partner42.moduleapi.controller"))
            .paths(PathSelectors.any())
            .build()
            .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("42Partner API")
            .description("42Seoul에서 진행하는 오픈 프로젝트 42Partner팀 Backend API 문서입니다.")
            .version("1.0")
            .build();
    }

    @Getter
    @Setter
    @ApiModel
    public static class Page {
        @ApiModelProperty(value = "페이지 번호(0..N)")
        private Integer page;

        @ApiModelProperty(value = "페이지 크기", allowableValues="range[0, 100]")
        private Integer size;

        @ApiModelProperty(value = "정렬(사용법: 컬럼명,ASC|DESC)")
        private List<String> sort;
    }
}