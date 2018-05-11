package com.gokseluyulmaz.flutternfc;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import io.flutter.app.FlutterActivity;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;

public class MainActivity extends FlutterActivity {

    private static final String CHANNEL = "nfc";
    private static IsoDep isoDep = null;
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;

    public static String ByteArrayToHexString(byte[] bytes) {
        final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] hexChars = new char[bytes.length * 2]; // Each byte has two hex characters (nibbles)
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF; // Cast bytes[j] to int, treating as unsigned value
            hexChars[j * 2] = hexArray[v >>> 4]; // Select hex character from upper nibble
            hexChars[j * 2 + 1] = hexArray[v & 0x0F]; // Select hex character from lower nibble
        }
        return new String(hexChars);
    }

    public static byte[] HexStringToByteArray(String s) throws IllegalArgumentException {
        int len = s.length();
        if (len % 2 == 1) {
            throw new IllegalArgumentException("Hex string must have even number of characters");
        }
        byte[] data = new byte[len / 2]; // Allocate 1 byte per 2 hex characters
        for (int i = 0; i < len; i += 2) {
            // Convert each character into a integer (base-16), then bit-shift into place
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GeneratedPluginRegistrant.registerWith(this);

        Log.i("", "onCreate");

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            Log.i("", "mAdapter null");
        }

        mPendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        mFilters = new IntentFilter[]{new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)};

        mTechLists = new String[][]{new String[]{IsoDep.class.getName()}};

        new MethodChannel(getFlutterView(), CHANNEL).setMethodCallHandler(
                new MethodChannel.MethodCallHandler() {
                    @Override
                    public void onMethodCall(MethodCall call, MethodChannel.Result result) {
                        if (call.method.equals("getPlatformVersion")) {
                            result.success("Android ${android.os.Build.VERSION.RELEASE}");
                        } else if (call.method.equals("getCardUID")) {
                            result.success(getCardUID());
                        } else if (call.method.equals("getVersion")) {
                            String command = call.argument("command");
                            result.success(getVersion(command));
                        } else {
                            result.notImplemented();
                        }
                    }
                });
    }

    @SuppressLint("MissingPermission")
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("", "onResume");
        if (mAdapter != null) {
            mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
        } else {
            Log.i("", "mAdapter null");
        }
        setIsoDep(getIntent());

    }

    @SuppressLint("MissingPermission")
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onPause() {
        super.onPause();
        Log.i("", "onPause");
        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(this);
        } else {
            Log.i("", "mAdapter null");
        }
    }

    @SuppressLint("MissingPermission")
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setIsoDep(Intent intent) {
        try {
            if (intent == null) {
                return;
            }
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            if (tag == null) {
                return;
            }

            isoDep = IsoDep.get(tag);

            if (isoDep != null && !isoDep.isConnected()) {
                isoDep.connect();
            }
        } catch (Exception e) {

        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private String getCardUID() {
        try {
            if (isoDep != null && isoDep.isConnected()) {
                byte[] uid = isoDep.getTag().getId();
                return ByteArrayToHexString(uid);
            } else {
                return "not connected";
            }
        } catch (Exception e) {
            return e.toString();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @SuppressLint("MissingPermission")
    private String getVersion(String command) {
        Log.i("getVersion", command);
        try {
            if (isoDep != null && isoDep.isConnected()) {
                String[] cmdArray = command.split("#");
                StringBuilder responses = new StringBuilder();
                for (int i = 0; i < cmdArray.length; i++) {
                    Log.i("", cmdArray[i]);
                    byte[] cmd = HexStringToByteArray(cmdArray[i]);
                    byte[] res = isoDep.transceive(cmd);
                    responses.append(ByteArrayToHexString(res));
                }
                return responses.toString();
            } else {
                return "not connected";
            }
        } catch (Exception e) {
            return e.toString();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i("", "onNewIntent");
        if (intent != null && NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            setIsoDep(intent);
        }
    }

}
