package com.dolzhenkoms.starter;

import io.vertx.core.Future;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlResult;
import io.vertx.sqlclient.Tuple;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PersonRepository {
  private static final Function<Row, Person> MAPPER = (row) ->
    Person.of(
      row.getString("name"),
      row.getInteger("id")
    );

  private final PgPool client;

  private PersonRepository(PgPool client) {
    this.client = client;
  }

  public static PersonRepository getRepository(PgPool client) {
    return new PersonRepository(client);
  }

  public Future<List<Person>> findAll() {
    return client.query("SELECT * FROM persons")
      .execute()
      .map(rs -> StreamSupport.stream(rs.spliterator(), false)
        .map(MAPPER)
        .collect(Collectors.toList())
      );
  }

  public Future<Person> findById(int id) {

    return client.preparedQuery("SELECT * FROM persons WHERE id=$1")
      .execute(Tuple.of(id))
      .map(RowSet::iterator)
      .map(iterator -> iterator.hasNext() ? MAPPER.apply(iterator.next()) : null)
      .map(Optional::ofNullable)
      .map(p ->
        p.orElseThrow(() -> new IllegalArgumentException(String.format("Person with %s id doesn't exist", id)))
      );
  }

  public Future<Integer> save(Person data) {
    return client.preparedQuery("INSERT INTO persons(name, id) VALUES ($1, $2) RETURNING (id)").execute(Tuple.of(data.getName(), data.getId()))
      .map(rs -> rs.iterator().next().getInteger("id"));
  }

  public Future<Integer> deleteById(int id) {
    return client.preparedQuery("DELETE FROM persons WHERE id=$1").execute(Tuple.of(id))
      .map(SqlResult::rowCount);
  }

  public Future<Integer> update(Person data) {
    return client.preparedQuery("UPDATE persons SET title=$1 WHERE id=$3")
      .execute(Tuple.of(data.getName(), data.getId()))
      .map(SqlResult::rowCount);
  }
}
