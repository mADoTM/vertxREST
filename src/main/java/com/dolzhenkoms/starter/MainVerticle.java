package com.dolzhenkoms.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgConnection;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

import java.util.stream.StreamSupport;

public class MainVerticle extends AbstractVerticle {


  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    var pgPool = pgPool();

    pgPool
      .withTransaction(
        conn -> conn.query("SELECT * from persons").execute()
      )
      .onSuccess(data -> StreamSupport.stream(data.spliterator(), true)
        .forEach(row -> System.out.println(row.toJson().toString())))
      .onComplete(
        r -> {
          //client.close(); will block the application.
          System.out.println("Data initialization is done...");
        }
      )
      .onFailure(
        throwable -> System.out.println("Data initialization is failed:" + throwable.getMessage())
      );
    //Creating PostRepository
    var postRepository = PersonRepository.getRepository(pgPool);

    //Creating PostHandler
    var postHandlers = new PersonHandler(postRepository);

    // Configure routes
    var router = routes(postHandlers);

    // Create the HTTP server
    vertx.createHttpServer()
      // Handle every request using the router
      .requestHandler(router)
      // Start listening
      .listen(8888)
      // Print the port
      .onSuccess(server -> {
        startPromise.complete();
        System.out.println("HTTP server started on port " + server.actualPort());
      })
      .onFailure(event -> {
        startPromise.fail(event);
        System.out.println("Failed to start HTTP server:" + event.getMessage());
      })
    ;
  }

  private Router routes(PersonHandler handlers) {
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());

    router.get("/persons").produces("application/json").handler(handlers::all);
    router.post("/persons").consumes("application/json").handler(BodyHandler.create()).handler(handlers::save);
    router.get("/persons/:id").produces("application/json").handler(handlers::get).failureHandler(frc -> frc.response().setStatusCode(404).end());
    router.put("/persons/:id").consumes("application/json").handler(BodyHandler.create()).handler(handlers::update);
    router.delete("/persons/:id").handler(handlers::delete);

    router.get("/hello").handler(rc -> rc.response().end("Hello world!"));

    return router;
  }

  private PgPool pgPool() {
    PgConnectOptions connectOptions = new PgConnectOptions()
      .setPort(5432)
      .setDatabase("persons")
      .setHost("localhost")
      .setUser("postgres")
      .setPassword("root");

    PoolOptions poolOptions = new PoolOptions().setMaxSize(10);

    PgPool pool = PgPool.pool(vertx, connectOptions, poolOptions);

    return pool;
  }
}
