package com.vert.starter.handlers.starline;

import com.vert.starter.APP_CONFIG;
import com.vert.starter.BcdConverter;
import com.vert.starter.models.Tracker;
import com.vert.starter.handlers.PackageHandler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import io.vertx.pgclient.PgPool;

import java.util.Arrays;

public class M15M17_login extends PackageHandler {
  Tracker tracker;
  public boolean is_login_package(){return true;};

  public M15M17_login(Buffer nbuffer, NetSocket nsocket, PgPool npool) {
    super(nbuffer, nsocket, npool);
  }

  @Override
  public Tracker get_tracker(){
    byte[] IDE = buffer.getBytes(1, 18);
    long imei = BcdConverter.bcdToLong(Arrays.copyOfRange(IDE, 0, 8));
    long login = BcdConverter.bcdToLong(Arrays.copyOfRange(IDE, 10, 15));
    short password = (short) BcdConverter.bcdToLong(Arrays.copyOfRange(IDE, 15, 17));

    tracker = new Tracker(imei, login, password);

    if(APP_CONFIG.DEBUG){
      System.out.printf("Got tracker imei: %d\n", tracker.imei);
    }

    tracker.setType(Tracker.TYPE_STARLINE);

    return tracker;
  }

  @Override
  public void handle_success(){
    Buffer response = Buffer.buffer();

    short CRC = buffer.getUnsignedByte(18);

    response.appendString("resp_crc=");
    response.appendUnsignedByte(CRC);

    socket.write(response, ar -> {
      if(ar.succeeded()){} //видимо даже так нельзя закрывать
        //socket.close(); //Закрывать сокет только после отправки запроса (иначе старлайн не считает успехом)
      else{
        ar.cause().printStackTrace();
        socket.close();
      }
    });

    pool.query(get_result_query()).execute();
  }

  @Override
  public void handle_failure(){
    socket.close();
  }

  @Override
  public String get_result_query(){
    return String.format(
      "UPDATE trackers SET is_active = true WHERE imei = %d", tracker.imei);
  };


}
