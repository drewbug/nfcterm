package radio.ab3j.nfc.wrappers;

import radio.ab3j.nfc.Ln;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;

import java.lang.reflect.Method;

public final class NfcManager {
    private final IInterface manager;
    private Method setReaderModeMethod;

    static NfcManager create() {
        IInterface manager = ServiceManager.getService("nfc", "android.nfc.INfcAdapter");
        return new NfcManager(manager);
    }

    private NfcManager(IInterface manager) {
        this.manager = manager;
    }

    private Method setReaderModeMethod() throws NoSuchMethodException, ClassNotFoundException {
        if (setReaderModeMethod == null) {
            Class<?> IAppCallbackClass = Class.forName("android.nfc.IAppCallback");
            setReaderModeMethod = manager.getClass()
                    .getMethod("setReaderMode", IBinder.class, IAppCallbackClass, int.class, Bundle.class);
        }
        return setReaderModeMethod;
    }

    public void setReaderMode(IBinder b, Object callback, int flags, Bundle extras) throws Exception {
            Method method = setReaderModeMethod();
            method.invoke(manager, b, callback, flags, extras);

    }
}
