package radio.ab3j.nfc;

import com.genymobile.scrcpy.wrappers.ServiceManager;

import android.content.ComponentName;
import android.content.Intent;

import android.os.Bundle;

import java.io.IOException;

public class ShellMain {

  public static void main(String[] args) throws IOException {
    final NfcShell shell = new NfcShell();

    final Intent intent = new Intent();

    intent.setComponent(new ComponentName("radio.ab3j.nfc", "radio.ab3j.nfc.NfcActivity"));

    final Bundle extras = new Bundle();

    extras.putBinder("token", shell);

    intent.putExtras(extras);

    ServiceManager.getActivityManager().startActivity(intent);

    final String protocol = args.length > 0 ? args[0] : null;

    if ("NfcA".equals(protocol)) {
      shell.start("android.nfc.tech.NfcA");
    } else if ("NfcB".equals(protocol)) {
      shell.start("android.nfc.tech.NfcB");
    } else if ("NfcF".equals(protocol)) {
      shell.start("android.nfc.tech.NfcF");
    } else if ("NfcV".equals(protocol)) {
      shell.start("android.nfc.tech.NfcV");
    } else if ("IsoDep".equals(protocol)) {
      shell.start("android.nfc.tech.IsoDep");
    } else {
      shell.start(protocol);
    }
  }

}
