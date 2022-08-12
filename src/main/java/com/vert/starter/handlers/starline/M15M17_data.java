package com.vert.starter.handlers.starline;

import com.vert.starter.handlers.PackageHandler;
import com.vert.starter.models.DataPackage;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import io.vertx.pgclient.PgPool;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;

public class M15M17_data extends PackageHandler {
  public boolean is_login_package(){return false;};
  public M15M17_data(Buffer nbuffer, NetSocket nsocket, PgPool pool) {
    super(nbuffer, nsocket, pool);
  }

  public DataPackage get_data_package(){

    byte[] STAT = buffer.getBytes(1, 15);
    byte[] GPS = buffer.getBytes(15, 33);

    byte[] BATTERY = Arrays.copyOfRange(STAT, 0, 1);
    byte[] BALANCE = new byte[3];
    BALANCE[0] = STAT[4];
    BALANCE[1] = STAT[2];
    BALANCE[2] = STAT[1];
    byte[] TEMPERATURE = Arrays.copyOfRange(STAT, 3, 4);
    byte[] SLEEP = Arrays.copyOfRange(STAT, 5, 6);

    byte[] GPS_STATUS = Arrays.copyOfRange(GPS, 0, 1);
    byte[] TIME = Arrays.copyOfRange(GPS, 1, 4);
    byte[] DATE = Arrays.copyOfRange(GPS, 4, 7);
    byte[] LAT_DEG = Arrays.copyOfRange(GPS, 7, 8);
    byte[] LAT = Arrays.copyOfRange(GPS, 8, 11);
    byte[] LONG_DEG = Arrays.copyOfRange(GPS, 11, 12);
    byte[] LONG = Arrays.copyOfRange(GPS, 12, 15);
    byte[] VELOCITY = Arrays.copyOfRange(GPS, 15, 16);
    byte[] COURCE = Arrays.copyOfRange(GPS, 16, 18);




    DataPackage pkg = new DataPackage();
    pkg.setGps_type((GPS_STATUS[0] >> 6));


    int time = java.nio.ByteBuffer.wrap(bytes_array_to_decimal(TIME)).getInt();
    LocalTime ltime = LocalTime.of(time / 10000,
      (time - ((time / 10000) * 10000)) / 100,
      time - ((time / 10000) * 10000) - ((time - ((time / 10000) * 10000)) / 100)* 100
      );

    int date = java.nio.ByteBuffer.wrap(bytes_array_to_decimal(DATE)).getInt();
    LocalDate ldate = LocalDate.of((date - ((date / 10000) * 10000) - ((date - ((date / 10000) * 10000)) / 100)* 100) + 2000,
      (date - ((date / 10000) * 10000)) / 100,
      date / 10000
    );

    pkg.setPackage_dt(LocalDateTime.of(ldate, ltime));


    int latitude = java.nio.ByteBuffer.wrap(bytes_array_to_decimal(LAT_DEG)).getInt();
    int latitude_decimal = java.nio.ByteBuffer.wrap(
      bytes_array_to_decimal(bytes_array_to_decimal(LAT))).getInt();
    latitude_decimal = (latitude_decimal >> 4) / 60;
    pkg.setLatitude(latitude + latitude_decimal / (Math.pow(10, Math.ceil(Math.log10(latitude_decimal)))));

    int la_semisphere = java.nio.ByteBuffer.wrap(
      bytes_array_to_decimal(Arrays.copyOfRange(bytes_array_to_decimal(LAT), 3, 4))).getInt();
    pkg.setLa_semisphere(la_semisphere % 2);

    int longitude = java.nio.ByteBuffer.wrap(bytes_array_to_decimal(LONG_DEG)).getInt();
    int longitude_decimal = java.nio.ByteBuffer.wrap(
      bytes_array_to_decimal(bytes_array_to_decimal(LONG))).getInt();
    longitude_decimal = (longitude_decimal >> 4) / 60;
    pkg.setLongitude(longitude + longitude_decimal / (Math.pow(10, Math.ceil(Math.log10(longitude_decimal)))));

    int lo_semisphere = java.nio.ByteBuffer.wrap(
      bytes_array_to_decimal(Arrays.copyOfRange(bytes_array_to_decimal(LONG), 3, 4))).getInt();
    pkg.setLo_semisphere(lo_semisphere % 2);

    pkg.setVelocity(java.nio.ByteBuffer.wrap(bytes_array_to_decimal(VELOCITY)).getInt());
    pkg.setCourse(java.nio.ByteBuffer.wrap(bytes_array_to_decimal(COURCE)).getInt());
    //2 100 255 255 33 24 72 71 60 250 20 230 2 21 177 141 3 33 176 1 137 214 56 36 32 177 44 6 105 145 0 0 122 122
      //STAT=100 255 255 33 24 72 71 60 250 20 230 2 21 177
        //100 - battery
        //255 255 24 - balance
        //33 - temperature
      //GPS=141 3 33 176 1 137 214 56 36 32 177 44 6 105 145 0 0 122
        //141 - gps data
        //3 33 176 - time
          // 11 00100001 10110000 = 20:52:32
        //1 137 214 - date
          // 1 10001001 11010110 = 10.08.22
        //56 - lat.deg
        //36 32 177 - lat
          // 100100 00100000 10110001 = 147979 - настоящее,

              //36 43 241
              //100100 00101011 11110001 = 148159

        //44 - long.deg
        //6 105 145 - long
        // 110 01101001 10010001 = 26265 - настоящее

            //6 99 161
            //110 1100011 10100001 = 13882
        //0 - Velocity
        //0 122 - Cource

    int battery_level = java.nio.ByteBuffer.wrap(bytes_array_to_decimal(BATTERY)).getInt();
    pkg.setBattery_level(((battery_level << 1) >> 1));

    if(BALANCE[2] == -1) //если баланс отрицательный
      pkg.setBalance(java.nio.ByteBuffer.wrap(bytes_array_to_negative_decimal(BALANCE)).getInt());
    else
      pkg.setBalance(java.nio.ByteBuffer.wrap(bytes_array_to_decimal(BALANCE)).getInt());

    pkg.setTemperature(java.nio.ByteBuffer.wrap(bytes_array_to_decimal(TEMPERATURE)).getInt());

    pkg.setOn_alarm(((battery_level >> 7) & 1) == 1);

    //pkg.sleep_time = java.nio.ByteBuffer.wrap(bytes_array_to_decimal(SLEEP)).getInt();

    return pkg;
  }
}
