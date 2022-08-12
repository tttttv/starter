package com.vert.starter.handlers;

import com.vert.starter.APP_CONFIG;
import com.vert.starter.exceptions.NoTrackerExists;
import com.vert.starter.models.DataPackage;
import com.vert.starter.models.Tracker;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import io.vertx.pgclient.PgPool;

public class ConnectionHandler {
  PgPool pool;
  NetSocket socket;
  static Tracker tracker;

  public ConnectionHandler(PgPool npool, NetSocket nsocket){
    pool = npool;
    socket = nsocket;
  }

  public void handle_request(Buffer buffer){
    if(APP_CONFIG.DEBUG){
      System.out.printf("\n\n--------------------------");
      System.out.println("Message: ");
      for (int i = 0; i < buffer.length(); i += 1) {
        System.out.print(buffer.getUnsignedByte(i) + " ");
      }
      System.out.println();
    }


    PackageHandler handler = PackageHandler.get_handler(buffer, socket, pool);
    assert handler != null;

    if(handler.is_login_package()){ //Если пакет входа
      tracker = handler.get_tracker();
      Future<Boolean> exists = tracker.exists(pool);
      exists.onComplete(ar -> {
        if(ar.succeeded()){
          handler.handle_success();
        } else {
          handler.handle_failure();

          if(!(ar.cause() instanceof NoTrackerExists)){
            ar.cause().printStackTrace();
          } else {
            if(APP_CONFIG.DEBUG)
              System.out.println("No tracker found");
          }
          return;
        }
      });
    } else { //Пакет данных
      DataPackage pkg = handler.get_data_package();
      if(APP_CONFIG.DEBUG)
        pkg.log();
      tracker.log();

      pkg.setTracker(tracker);
      pkg.save(pool);
    }
  }
}
