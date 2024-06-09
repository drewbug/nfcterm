package radio.ab3j.nfc;

import android.nfc.Tag;
import android.nfc.TagLostException;

import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.nfc.tech.TagTechnology;

import java.io.IOException;

import java.lang.reflect.Method;

import java.lang.ReflectiveOperationException;

import java.util.concurrent.Semaphore;

import java.util.Scanner;

public class NfcShell extends INfcShell.Stub {

  private String mProtocol = null;

  private TagTechnology mTag = null;

  private Semaphore mLock = new Semaphore(0);

  public void start(String protocol) throws IOException {
    this.mProtocol = protocol;

    System.err.println("Listening for NFC tag!");

    this.mLock.acquireUninterruptibly();

    byte[] uid = this.mTag.getTag().getId();

    System.err.println("Found UID: " + byteArrayToHexString(uid));

    this.mTag.connect();

    System.err.println("Connected: " + this.mTag.getClass().getName());

    Scanner scanner = new Scanner(System.in);

    while (true) {
      String inputHex = scanner.nextLine().trim();

      byte[] payload = hexStringToByteArray(inputHex);

      if (payload == null) {
        System.err.println("Invalid payload, please try again");
        continue;
      }

      System.out.println("> " + byteArrayToHexString(payload));

      try {
        byte[] response = (byte[]) Class.forName(this.mProtocol).getMethod("transceive", byte[].class).invoke(this.mTag, payload);
        System.out.println("< " + byteArrayToHexString(response));
      } catch (ReflectiveOperationException e) {
        System.err.println("Tag lost");
        break;
      }
    }
  }

  @Override
  public void onTagDiscovered(Tag tag) {
    if (this.mTag != null) { return; }

    if (this.mProtocol == null) {
      this.mProtocol = tag.getTechList()[0];
    }

    try {
      this.mTag = (TagTechnology) Class.forName(this.mProtocol).getMethod("get", Tag.class).invoke(null, tag);
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException(e);
    }

    if (this.mTag != null) { this.mLock.release(); }
  }

  private static byte[] hexStringToByteArray(String s) {
    int len = s.length();
    if (len % 2 != 0) {
      return null; // invalid hex string
    }
    byte[] data = new byte[len / 2];
    try {
      for (int i = 0; i < len; i += 2) {
        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                              + Character.digit(s.charAt(i+1), 16));
      }
    } catch (NumberFormatException e) {
      return null; // invalid hex digit
    }
    return data;
  }

  private static String byteArrayToHexString(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
      sb.append(String.format("%02X", b));
    }
    return sb.toString();
  }

}
