package org.webflux.demo.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.StringJoiner;

class Account {
  @JsonProperty
  final Integer id;
  @JsonProperty
  final Instant creationDateTime;

  @JsonCreator
  Account(@JsonProperty("id") final Integer id,
          @JsonProperty("creationDateTime") final Instant creationDateTime) {
    this.id = id;
    this.creationDateTime = creationDateTime;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Account.class.getSimpleName() + "[", "]")
        .add("id='" + id + "'")
        .add("creationDateTime=" + creationDateTime)
        .toString();
  }
}
