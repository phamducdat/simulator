package com.wiinvent.lotusmile.domain.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Lotusmile Account Service",
        version = "v1",
        contact = @Contact(
            name = "wiinvent",
            email = "info@wiinvent.tv",
            url = "https://wiinvent.tv/"
        ),
        license = @License(
            name = "", url = ""
        ),
        description = """
            API `login` sẽ tạo ra một token sau khi đăng nhập thành công. Sau khi nhận được token, tất cả các yêu cầu API tiếp theo sẽ tự động bao gồm header `X-User-Id`, được lấy từ token.
            
            Đối với tất cả các API, cần phải cung cấp các header sau:
            - `X-Forwarded-For`: Địa chỉ IP của client.
            - `X-Device-ID`: ID duy nhất của thiết bị.
            - `Accept-Language`: Ngôn ngữ của user: vi hoặc en (mặc định là vi)"""

    ),
    servers = {
        @Server(url = "https://dev-lotusmile-api.wiinvent.tv", description = "Development Server"),
    },
    security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
public class OpenAPISecurityConfiguration {
  @Bean
  public OpenAPI openAPI() {
//    http://localhost:8084/v1/docs/account/swagger-ui/index.html
    return new OpenAPI();
  }
}