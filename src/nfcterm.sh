#!/bin/sh
adb shell "CLASSPATH=\$(pm path radio.ab3j.nfc) app_process / radio.ab3j.nfc.ShellMain $@"
