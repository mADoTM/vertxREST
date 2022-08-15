package com.dolzhenkoms.starter;

import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class PersonHandler {
  private final PersonRepository personRepository;

  public PersonHandler(PersonRepository personRepository) {
    this.personRepository = personRepository;
  }

  public void delete(RoutingContext routingContext) {
    var params = routingContext.pathParams();
    var id = params.get("id");

    int uuid = Integer.parseInt(id);

    personRepository.findById(uuid)
      .compose(
        person -> personRepository.deleteById(uuid)
      )
      .onSuccess(
        data -> routingContext.response().setStatusCode(204).end()
      )
      .onFailure(
        throwable -> routingContext.fail(404, throwable)
      );
  }

  public void all(RoutingContext routingContext) {
    personRepository.findAll()
      .onSuccess(data -> routingContext.response().end(Json.encode(data)));
  }

  public void save(RoutingContext routingContext) {
    var body = routingContext.body().asJsonObject();
    var form = body.mapTo(CreatePersonDTO.class);

    personRepository.save(Person.of(form.getName(), form.getId()))
      .onSuccess(saveId ->
        routingContext.response()
          .putHeader("Location","/posts/" + saveId)
          .setStatusCode(201)
          .end()
      );
  }

  public void get(RoutingContext routingContext) {
    var params = routingContext.pathParams();
    var id = params.get("id");

    personRepository.findById(Integer.parseInt(id))
      .onSuccess(
        person -> routingContext.response().end(Json.encode(person))
      )
      .onFailure(
        throwable -> routingContext.fail(404, throwable)
      );
  }

  public void update(RoutingContext routingContext) {
    var params = routingContext.pathParams();
    String id = params.get("id");
    var body = routingContext.body().asJsonObject();

    CreatePersonDTO form = body.mapTo(CreatePersonDTO.class);

    personRepository.findById(Integer.parseInt(id))
      .compose(
        person -> {
          person.setName(form.getName());

          return personRepository.update(person);
        }
      )
      .onSuccess(
        data -> routingContext.response().setStatusCode(204).end()
      )
      .onFailure(
        throwable -> routingContext.fail(404, throwable)
      );
  }
}
