package com.vert.starter;

import com.vert.starter.exceptions.NoTrackerExists;
import com.vert.starter.handlers.ConnectionHandler;
import com.vert.starter.handlers.PackageHandler;
import com.vert.starter.models.DataPackage;
import com.vert.starter.models.Tracker;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;
import io.vertx.core.tracing.TracingPolicy;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlConnectOptions;


public class ServerVerticle extends AbstractVerticle {

  PgPool pool;

  public void start(Promise<Void> startPromise) {

    PgConnectOptions connectOptions = new PgConnectOptions()
      .setPort(5432)
      .setHost("localhost")
      .setDatabase("avilon")
      .setUser("avilon")
      .setPassword("123456")
      .setTcpKeepAlive(false);

    PoolOptions poolOptions = new PoolOptions();
    pool = PgPool.pool(vertx, connectOptions, poolOptions);

    NetServer server = vertx.createNetServer().connectHandler(socket -> {
      ConnectionHandler connectionHandler = new ConnectionHandler(pool, socket);

      socket.handler(connectionHandler::handle_request);

      /*
      socket.closeHandler(v -> {
        System.out.println("Socket closed");
      });
       */
    });

    server.listen(APP_CONFIG.SERVER_PORT, APP_CONFIG.SERVER_HOST, res -> {
      if (res.succeeded()) {
        System.out.println("Listening on " + APP_CONFIG.SERVER_HOST + ":" + APP_CONFIG.SERVER_PORT);
        startPromise.complete();
      } else {
        System.out.println("Error listening");
        res.cause().printStackTrace();
        startPromise.fail(res.cause());
      }
    });
  }

}
