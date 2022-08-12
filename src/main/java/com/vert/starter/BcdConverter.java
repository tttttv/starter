package com.vert.starter;

public class BcdConverter {

  public static byte[] longToBcd(long num) {
    if (num < 0) throw new IllegalArgumentException(
      "The method decimalToBcd doesn't support negative numbers." +
        " Invalid argument: " + num);

    int digits = 0;

    long temp = num;
    while (temp != 0) {
      digits++;
      temp /= 10;
    }

    int byteLen = digits % 2 == 0 ? digits / 2 : (digits + 1) / 2;

    byte[] bcd = new byte[byteLen];

    for (int i = 0; i < digits; i++) {
      byte tmp = (byte) (num % 10);

      if (i % 2 == 0) {
        bcd[i / 2] = tmp;
      } else {
        bcd[i / 2] |= (byte) (tmp << 4);
      }

      num /= 10;
    }

    for (int i = 0; i < byteLen / 2; i++) {
      byte tmp = bcd[i];
      bcd[i] = bcd[byteLen - i - 1];
      bcd[byteLen - i - 1] = tmp;
    }

    return bcd;
  }

  public static long bcdToLong(byte[] bcd) {
    return Long.parseLong(bcdToString(bcd));
  }

  public static String bcdToString(byte[] bcd) {
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < bcd.length; i++) {
      sb.append(bcdToString(bcd[i]));
    }

    return sb.toString();
  }

  public static String bcdToString(byte bcd) {
    StringBuilder sb = new StringBuilder();

    byte high = (byte) (bcd & 0xf0);
    high >>>= (byte) 4;
    high = (byte) (high & 0x0f);
    byte low = (byte) (bcd & 0x0f);

    sb.append(high);
    sb.append(low);

    return sb.toString();
  }

}
