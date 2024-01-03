package moses.test.config;

import java.util.ArrayList;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("moses.test.controller"))
				.paths(PathSelectors.any())
				.build()
		.apiInfo(apiInfo());
	}

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "My API",
                "Description of my API",
                "1.0",
                null,
                new Contact("Your Name", "Your Website", "your.email@example.com"),
                "License",
                "http://localhost:8080/swagger-ui/index.html#/",
                new ArrayList<>()
        );
    }

}
