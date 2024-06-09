package radio.ab3j.nfc;

interface INfcShell {
    oneway void onTagDiscovered(in Tag tag);
}
