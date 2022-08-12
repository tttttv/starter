package com.vert.starter;

import io.vertx.core.DeploymentOptions;
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

    Vertx vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(APP_CONFIG.WORKERS_AMOUNT));
    System.out.println("Hello");

    DeploymentOptions options = new DeploymentOptions().setInstances(APP_CONFIG.WORKERS_AMOUNT);
    vertx.deployVerticle(ServerVerticle.class, options);

  }
}
