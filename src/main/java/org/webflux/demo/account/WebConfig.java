package org.webflux.demo.account;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@EnableWebFlux
class WebConfig implements WebFluxConfigurer {
  private final AccountHandler accountHandler;

  WebConfig(AccountHandler accountHandler) {
    this.accountHandler = accountHandler;
  }

  @Bean
  RouterFunction<ServerResponse> accountRouter() {
    return route()
        .path("account", builder ->
            builder
                .GET("{id}", accountHandler::getById)
                .GET("", ignored -> accountHandler.getAll())
                .POST("", accountHandler::insertInto))
        .build();
  }
}
