package com.vert.starter.models;

import com.vert.starter.APP_CONFIG;
import io.vertx.core.Future;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class DataPackage {
  //public int input_status; //Состояние входа
  private int battery_level;
  private int balance;
  private int temperature;
  private boolean on_alarm; //Режим тревоги
  private int sleep_time; //Время сна
  private int gps_type; //0 - нет GPS, 1 - черный ящик, 2 - актуальные
  private LocalDateTime package_dt; //По гринвичу
  private double latitude; //широта
  private int la_semisphere; // полушарие широты 0 - юг 1 - север
  private double longitude; //долгота
  private int lo_semisphere; //полушарие долготы 0 - юг 1 - север
  private int velocity; //скорость
  private int course; //Угол поворота от направления на север по минутной стрелке.

  public void setBattery_level(int battery_level) {
    this.battery_level = battery_level;
  }

  public void setBalance(int balance) {
    this.balance = balance;
  }

  public void setTemperature(int temperature) {
    this.temperature = temperature;
  }

  public void setOn_alarm(boolean on_alarm) {
    this.on_alarm = on_alarm;
  }

  public void setSleep_time(int sleep_time) {
    this.sleep_time = sleep_time;
  }

  public void setGps_type(int gps_type) {
    this.gps_type = gps_type;
  }

  public void setPackage_dt(LocalDateTime package_dt) {
    this.package_dt = package_dt;
  }

  public int getBattery_level() {
    return battery_level;
  }

  public int getBalance() {
    return balance;
  }

  public int getTemperature() {
    return temperature;
  }

  public boolean isOn_alarm() {
    return on_alarm;
  }

  public int getSleep_time() {
    return sleep_time;
  }

  public int getGps_type() {
    return gps_type;
  }

  public LocalDateTime getPackage_dt() {
    return package_dt;
  }

  public double getLatitude() {
    return latitude;
  }

  public int getLa_semisphere() {
    return la_semisphere;
  }

  public double getLongitude() {
    return longitude;
  }

  public int getLo_semisphere() {
    return lo_semisphere;
  }

  public int getVelocity() {
    return velocity;
  }

  public int getCourse() {
    return course;
  }

  public Tracker getTracker() {
    return tracker;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public void setLa_semisphere(int la_semisphere) {
    this.la_semisphere = la_semisphere;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public void setLo_semisphere(int lo_semisphere) {
    this.lo_semisphere = lo_semisphere;
  }

  public void setVelocity(int velocity) {
    this.velocity = velocity;
  }

  public void setCourse(int course) {
    this.course = course;
  }

  public void setTracker(Tracker tracker) {
    this.tracker = tracker;
  }

  private Tracker tracker;

  public void log(){
    System.out.printf("Package data:\n");
    System.out.printf("[datetime] - " + getPackage_dt().toString() + "\n");
    System.out.printf("[coords] - %f %f\n", getLatitude(), getLongitude());
    System.out.printf("[stats] - battery:%d temperature:%d balance:%d\n", getBattery_level(), getTemperature(), getBalance());
  }

  public String get_data_bits(){
    byte[] test = new byte[8];

    if(tracker.getType() == Tracker.TYPE_STARLINE){

    }


    Arrays.fill(test, (byte) 0);
    StringBuilder result = new StringBuilder();
    for(int i=0; i<test.length; i++){
      result.append(String.format("%8s", Integer.toBinaryString(test[i] & 0xFF)).replace(' ', '0'));
    }
    return result.toString();
  }

  public void save(PgPool pool){
    byte[] test = new byte[8];


    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String raw = "INSERT INTO locations (imei, dt, lat, long, las, los, velocity, course, data) VALUES "
      + "(" + tracker.imei + ", '" +  package_dt.format(dateFormat) + "', " + latitude + ", " + longitude
      + ", " + (la_semisphere == 1) + ", " + (lo_semisphere == 1) + ", " + velocity + ", " +  course
      + ", B'" + get_data_bits() +"');";

    //System.out.println(raw);
    Future <RowSet<Row>> f = pool.query(raw).execute();


    //Future<RowSet<Row>> f = pool.preparedQuery(
    //    "INSERT INTO locations (imei,  data) VALUES ($1,  $9);")
    //  .execute();
      //.execute(Tuple.of(tracker.imei, package_dt, latitude, longitude,
      //  la_semisphere == 1, lo_semisphere == 1,  velocity, course, test));


    if(APP_CONFIG.DEBUG){
      f.onComplete(r -> {
        if(r.succeeded())
          System.out.println("[result] - Location successfully saved");
        else{
          System.out.println(r.cause().getMessage());
          r.cause().printStackTrace();
        }
      });
    }
  }
  //(id SERIAL PRIMARY KEY, imei BIGINT, dt TIMESTAMP, lat REAL, long REAL, las BOOL, los BOOL, velocity INT, course INT, data VARBIT(128))
  //"INSERT INTO locations (imei = $1, dt = $2, lat = $3,"
}
