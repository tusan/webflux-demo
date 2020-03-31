package org.webflux.demo.account;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@Configuration
class DbConfig {
  private final static Logger LOGGER = Logger.getLogger(DbConfig.class.getName());

  @Bean
  Mono<Connection> connectionFactory() {
    return Mono.from(ConnectionFactories.get(ConnectionFactoryOptions
        .builder()
        .option(ConnectionFactoryOptions.USER, "postgres")
        .option(ConnectionFactoryOptions.PASSWORD, "password")
        .option(ConnectionFactoryOptions.HOST, "localhost")
        .option(ConnectionFactoryOptions.PORT, 5432)
        .option(ConnectionFactoryOptions.DRIVER, "postgres")
        .option(ConnectionFactoryOptions.DATABASE, "postgres")
        .build())
        .create());
  }

  @Bean
  CommandLineRunner initDatabase(Mono<Connection> connection) {
    return (args) -> connection
        .flatMap(conn ->
            Mono.from(conn.createBatch()
                .add("drop table if exists account")
                .add("CREATE TABLE account (" +
                    "  id serial NOT NULL," +
                    "  creation_date_time timestamp NOT NULL" +
                    ")")
                .execute())
                .doFinally(st -> conn.close()))
        .doFirst(() -> LOGGER.info("Initializing database"))
        .doFinally(st -> LOGGER.info("Database initialization completed"))
        .block();
  }
}
