package radio.ab3j.nfc;

import android.app.Activity;

import android.nfc.NfcAdapter;
import android.nfc.Tag;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import android.view.View;
import android.view.WindowManager;

public class NfcActivity extends Activity implements NfcAdapter.ReaderCallback, IBinder.DeathRecipient {

    private INfcShell mShell = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setFinishOnTouchOutside(false);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        IBinder token = getIntent().getExtras().getBinder("token");

        mShell = INfcShell.Stub.asInterface(token);

        try {
            token.linkToDeath(this, 0);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        final int flags = NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_NFC_B |
                          NfcAdapter.FLAG_READER_NFC_F | NfcAdapter.FLAG_READER_NFC_V |
                          NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;

        NfcAdapter.getDefaultAdapter(this).enableReaderMode(this, this, flags, null);
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        try {
            mShell.onTagDiscovered(tag);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void binderDied() {
        finish();
    }

}
