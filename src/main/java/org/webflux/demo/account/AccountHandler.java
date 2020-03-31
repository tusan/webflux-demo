package org.webflux.demo.account;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.notFound;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Service
class AccountHandler {
  private final AccountRepository accountRepository;

  AccountHandler(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  Mono<ServerResponse> insertInto(ServerRequest request) {
    return request.bodyToMono(Account.class)
        .flatMap(accountRepository::insert)
        .flatMap(newAccount -> ok().bodyValue(newAccount));
  }

  Mono<ServerResponse> getAll() {
    return ok().body(accountRepository.findAll(), Account.class);
  }

  Mono<ServerResponse> getById(ServerRequest request) {
    return accountRepository.findById(Integer.parseInt(request.pathVariable("id")))
        .flatMap(res -> ok().bodyValue(res))
        .switchIfEmpty(notFound().build());
  }
}
