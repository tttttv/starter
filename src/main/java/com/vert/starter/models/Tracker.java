package com.vert.starter.models;

import com.vert.starter.exceptions.NoTrackerExists;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

public class Tracker {
  public static final String TYPE_STARLINE = "STARLINE";
  public static final String TYPE_DEFAULT = "DEFAULT";
  public long imei;
  public long phone_number;
  public short password;

  public String getType() {
    return type;
  }

  public void setType(String ntype) {
    type = ntype;
  }

  public String type = TYPE_DEFAULT;

  public Tracker(long i, long p, short ps){
    imei = i;
    phone_number = p;
    password = ps;
  }

  public Tracker() {

  }

  public Future<Boolean> exists(PgPool pool){
    Promise<Boolean> promise = Promise.promise();

    Future<RowSet<Row>> trackers = pool.query(String.format("SELECT imei FROM trackers WHERE imei=%d", imei)).execute();

    trackers.onComplete(ar -> {
      if(ar.succeeded()) {
        RowSet<Row> rows = ar.result();
        if(rows.size() > 0)
          promise.complete();
        else
          promise.fail(new NoTrackerExists());
      }
      else
        promise.fail(ar.cause());

    });

    return promise.future();
  };

  public void log(){
    System.out.printf("Tracker imei: %d\n", imei);
  }
}
