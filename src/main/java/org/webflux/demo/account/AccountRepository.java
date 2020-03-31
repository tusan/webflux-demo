package org.webflux.demo.account;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.function.BiFunction;
import java.util.function.Function;

@Repository
class AccountRepository {
  static final String GET_SINGLE_QUERY = "select id, creation_date_time from account where id = $1";
  static final String GET_ALL_QUERY = "select id, creation_date_time from account";
  static final String INSERT_ACCOUNT = "insert into account(creation_date_time) values ($1)";

  private final Mono<Connection> connection;

  AccountRepository(final Mono<Connection> connection) {
    this.connection = connection;
  }

  Mono<Account> findById(final int id) {
    return connection
        .flatMap(conn -> Mono.from(conn.createStatement(GET_SINGLE_QUERY)
            .bind("$1", id)
            .execute())
            .doFinally(st -> conn.close()))
        .flatMap(result -> Mono.from(result.map(createAccount())));
  }

  Flux<Account> findAll() {
    return connection
        .flatMapMany(conn -> Mono.from(conn.createStatement(GET_ALL_QUERY)
            .execute())
            .doFinally((st) -> conn.close()))
        .flatMap(result -> result.map(createAccount()));
  }

  Mono<Account> insert(final Account account) {
    return connection
        .flatMap(conn -> Mono.from(conn.beginTransaction())
            .then(Mono.from(conn.createStatement(INSERT_ACCOUNT)
                .bind("$1", account.creationDateTime)
                .returnGeneratedValues("id")
                .execute()))
            .flatMap(extractNewId(account))
            .delayUntil(st -> conn.commitTransaction())
            .doFinally(st -> conn.close()));
  }

  private Function<Result, Mono<Account>> extractNewId(Account account) {
    return result -> Mono.from(result.map((row, meta) ->
        new Account(row.get("id", Integer.class), account.creationDateTime)
    ));
  }

  private BiFunction<Row, RowMetadata, Account> createAccount() {
    return (row, meta) -> new Account(
        row.get("id", Integer.class),
        row.get("creation_date_time", Instant.class));
  }
}
