package radio.ab3j.nfc;

import android.nfc.Tag;
import android.nfc.TagLostException;

import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.nfc.tech.TagTechnology;

import java.io.IOException;

import java.util.concurrent.Semaphore;

import java.util.Scanner;

public class NfcShell extends INfcShell.Stub {

  private TagTechnology mTag = null;

  private Semaphore mLock = new Semaphore(0);

  public void start() throws IOException {
    System.err.println("Listening for NFC tag!");

    this.mLock.acquireUninterruptibly();

    byte[] uid = this.mTag.getTag().getId();

    System.err.println("Found UID: " + byteArrayToHexString(uid));

    this.mTag.connect();

    if (this.mTag instanceof NfcA) {
      System.err.println("Connected: NfcA");
    } else if (this.mTag instanceof NfcB) {
      System.err.println("Connected: NfcB");
    } else if (this.mTag instanceof NfcF) {
      System.err.println("Connected: NfcF");
    } else if (this.mTag instanceof NfcV) {
      System.err.println("Connected: NfcV");
    }

    Scanner scanner = new Scanner(System.in);

    while (true) {
      String inputHex = scanner.nextLine().trim();

      byte[] payload = hexStringToByteArray(inputHex);

      if (payload == null) {
        System.err.println("Invalid payload, please try again");
        continue;
      }

      System.out.println("> " + byteArrayToHexString(payload));

      byte[] response = null;

      try {
        if (this.mTag instanceof NfcA) {
          response = ((NfcA) this.mTag).transceive(payload);
        } else if (this.mTag instanceof NfcB) {
          response = ((NfcB) this.mTag).transceive(payload);
        } else if (this.mTag instanceof NfcF) {
          response = ((NfcF) this.mTag).transceive(payload);
        } else if (this.mTag instanceof NfcV) {
          response = ((NfcV) this.mTag).transceive(payload);
        }
      } catch (TagLostException e) {
        System.err.println("Tag lost");
        break;
      }

      System.out.println("< " + byteArrayToHexString(response));
    }
  }

  @Override
  public void onTagDiscovered(Tag tag) {
    if (this.mTag == null) { this.mTag = NfcA.get(tag); } else { return; }
    if (this.mTag == null) { this.mTag = NfcB.get(tag); }
    if (this.mTag == null) { this.mTag = NfcF.get(tag); }
    if (this.mTag == null) { this.mTag = NfcV.get(tag); }

    this.mLock.release();
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
