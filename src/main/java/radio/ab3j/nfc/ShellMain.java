package radio.ab3j.nfc;

import radio.ab3j.nfc.wrappers.ServiceManager;

import android.content.Context;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;

import android.nfc.NfcAdapter;
import android.nfc.IAppCallback;
import android.nfc.Tag;

import android.os.Binder;
import android.os.IBinder;

public class ShellMain {

  public static void main(String[] args) {
    try {
      System.out.println("test");

      Intent intent = new Intent(Intent.ACTION_MAIN);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      intent.addCategory(Intent.CATEGORY_LAUNCHER);
      intent.setComponent(new ComponentName("com.android.shell", "com.android.shell.HeapDumpActivity"));
      ServiceManager.getActivityManager().startActivity(intent);

      IBinder token = new Binder();

      IAppCallback.Stub mBinder = new IAppCallback.Stub() {
        @Override
        public void onTagDiscovered(Tag tag) {
            System.out.println("tag found");
        }
      };

      ServiceManager.getNfcManager().setReaderMode(token, mBinder, 159, null);

      Thread.currentThread().join();

    } catch(Throwable t) {
      t.printStackTrace();
    }
  }
}
