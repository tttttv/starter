package com.vert.starter;

import io.vertx.core.Future;
import io.vertx.core.VertxOptions;
import io.vertx.core.net.NetServer;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServerOptions;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgConnection;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.*;


public class App {
  public static void main(String[] args) throws Exception {

    /*
    System.out.println("Hello World!"); // Display the string.
    NetServerOptions options = new NetServerOptions().setPort(9002);
    NetServer server = Vertx.vertx().createNetServer(options);

    server.connectHandler(socket -> {
      socket.handler(buffer -> {
        System.out.println("I received some bytes: " + buffer.length());
        socket.write("some data");
        socket.close();
      });
    });

    server.listen(9002, "65.21.131.239");
    */

    Vertx vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(APP_CONFIG.WORKERS_AMOUNT));

    System.out.println("Hello");

    PgConnectOptions connectOptions = new PgConnectOptions()
      .setPort(5432)
      .setHost("localhost")
      .setDatabase("avilon")
      .setUser("avilon")
      .setPassword("123456");

    // Pool options
    PoolOptions poolOptions = new PoolOptions();

    // Create the client pool
    PgPool pool = PgPool.pool(vertx, connectOptions, poolOptions.setMaxWaitQueueSize(1000));
    //SqlClient client = PgPool.client(vertx, connectOptions, poolOptions);

    for (int i = 0; i < 11; i++) {
      NetServer server = vertx.createNetServer();
      server.connectHandler(socket -> {
        socket.handler(buffer -> {
          // Just echo back the data
          System.out.println("I received some bytes: " + buffer.length());
          socket.write("some data1");
          socket.close();

          System.out.println("Sent");

          Future<RowSet<Row>> res2 = pool.query("INSERT INTO locations (imei, test) VALUES ('1234567989', 4);").execute();

          res2.onComplete(ar -> {
            if(ar.succeeded())
              System.out.println("Done");
            else{
              ar.cause().printStackTrace();
              System.out.println("Failure: " + ar.cause().getMessage());
            }
          });

        });
      });
      server.listen(9002, "65.21.131.239");
      //server.listen(9002, "65.21.177.114");
      //server.listen(9002, "localhost");
    }

  }
}
