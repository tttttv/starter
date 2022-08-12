package com.vert.starter.handlers;

import com.vert.starter.handlers.starline.M15M17_data;
import com.vert.starter.models.DataPackage;
import com.vert.starter.models.Tracker;
import com.vert.starter.handlers.starline.M15M17_login;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import io.vertx.pgclient.PgPool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class PackageHandler {
  public PackageHandler() {

  }

  public boolean is_login_package(){return false;};
  public Buffer buffer;
  public NetSocket socket;
  public PgPool pool;

  public PackageHandler(Buffer nbuffer, NetSocket nsocket, PgPool npool) {
    buffer = nbuffer;
    socket = nsocket;
    pool = npool;
  }

  public static PackageHandler get_handler(Buffer nbuffer, NetSocket nsocket, PgPool npool){
    if(nbuffer.length() == 19){
      if(nbuffer.getUnsignedByte(0) == (short) 65) //Starline login package
        return new M15M17_login(nbuffer, nsocket, npool);
    } else if(nbuffer.length() == 34){
      if(nbuffer.getUnsignedByte(0) == (short) 2)
        return new M15M17_data(nbuffer, nsocket, npool);
    }
    return null;
  }

  public Tracker get_tracker(){
    return null;
  }
  public void handle_request(){}
  public void handle_failure(){}
  public void handle_success(){}
  public String get_result_query(){return null;}
  public String get_search_query(){return null;}
  public DataPackage get_data_package(){return null;}


  private byte[] bytes_array_to_decimal(byte[] parent, int from, int to, int len, int to_fill) {
    byte[] data = Arrays.copyOfRange(parent, from, to);
    byte[] zeroes = new byte[len - (to-from)];
    Arrays.fill(zeroes, (byte) to_fill);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try {
      if(len > (to-from))
        outputStream.write(zeroes);
      outputStream.write(data);
    }

    catch (IOException e){
      System.out.println("IOException");
      e.printStackTrace();
    }
    return outputStream.toByteArray( );
  };

  private byte[] bytes_array_to_decimal(byte[] parent, int from, int to, int len) {
    return bytes_array_to_decimal(parent, from, to, len, 0);
  }

  public byte[] bytes_array_to_decimal(byte[] parent, int from, int to){
    return bytes_array_to_decimal(parent, from, to, 4);
  }

  public byte[] bytes_array_to_decimal(byte[] parent){
    return bytes_array_to_decimal(parent, 0, parent.length, 4);
  }

  public byte[] bytes_array_to_negative_decimal(byte[] parent){
    return bytes_array_to_decimal(parent, 0, parent.length, 4, 1);
  }

  public Short[] buffer_to_byte_array(int from, int to){
    List<Short> ar = new ArrayList<Short>();

    for(int i = 0; i < (to - from); i++)
      ar.add(buffer.getUnsignedByte(i));

    Short[] array = ar.toArray(new Short[0]);
    return array;
  }

  public Future<Void> save(PgPool pool){
    return null;
  };
}
