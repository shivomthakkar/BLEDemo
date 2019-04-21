package com.example.bledemo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.bledemo.BLEDevice.rssiComparator;

public class MainActivity extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter;
    List<BLEDevice> tempList = new ArrayList<>(), calcList = new ArrayList<>();
    List<AllShopMappingResponse> mappingList = new ArrayList<>();
    Timer timer = new Timer();
    ListView lVDevices;
    ArrayList<String> list = new ArrayList<>();

    List<AllShopMappingResponse> allShopMappingResponses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lVDevices = findViewById(R.id.lVDevices);

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, 100);
        }

        AllShopInterface allShopInterface = RetrofitClient.getClient("http://rcityapi.quicsolv.com:5051/api/").create(AllShopInterface.class);
        allShopInterface.getMapping().enqueue(new Callback<List<AllShopMappingResponse>>() {
            @Override
            public void onResponse(Call<List<AllShopMappingResponse>> call, Response<List<AllShopMappingResponse>> response) {
                allShopMappingResponses = response.body();
            }

            @Override
            public void onFailure(Call<List<AllShopMappingResponse>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Couldn't get the mapping.", Toast.LENGTH_SHORT).show();
            }
        });

        if (timer != null) {
            timer.cancel();
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
                final BluetoothLeScanner bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
                timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        if (bluetoothAdapter != null) {
                            bluetoothAdapter.stopLeScan(mBLeScanCallback);
                        }
                        Log.d("MallMapFragment", "Stopping BLE scan from inside");
                        calcList.addAll(tempList);

                        if (calcList.size() != 0) {
                            Log.d("MallMapFragment", "Data found!");
                            Collections.sort(calcList, rssiComparator);

                        }
                        tempList.clear();
                        if (bluetoothAdapter != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    lVDevices.setAdapter(null);
                                    list.clear();
                                }
                            });
                            bluetoothAdapter.startLeScan(mBLeScanCallback);
                        }
                        Log.d("MallMapFragment", "Starting BLE scan from inside");
                    }
                }, 1000, 200000);
            }
        }

    }

    public AllShopMappingResponse searchList(BLEDevice device) {
        if (allShopMappingResponses != null) {
            for (AllShopMappingResponse a : allShopMappingResponses) {
                if (device.getName().equalsIgnoreCase(a.getGatewayId().toString())) {
                    return a;
                }
            }
        }
        return null;
    }

    public boolean isPresentInList(List<BLEDevice> list, String name) {
        for (BLEDevice device: list)
            if (device.getName().equals(name))
                return true;
        return false;
    }

    public void updateInList(List<BLEDevice> list, String name, int rssi) {
        for (BLEDevice device: list) {
            if (device.getName().equals(name)) {
                device.setRssi(rssi);
            }
        }
    }

    private BluetoothAdapter.LeScanCallback mBLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            int startByte = 2;
            boolean patternFound = false;
            while (startByte <= 5) {
                if (    ((int) scanRecord[startByte + 2] & 0xff) == 0x02 && //Identifies an iBeacon
                        ((int) scanRecord[startByte + 3] & 0xff) == 0x15) { //Identifies correct data length
                    patternFound = true;
                    break;
                }
                startByte++;
            }

            if (patternFound) {
                //Convert to hex String
                byte[] uuidBytes = new byte[16];
                System.arraycopy(scanRecord, startByte+4, uuidBytes, 0, 16);
                String hexString = bytesToHex(uuidBytes);

                //Here is your UUID
                String uuid =  hexString.substring(0,8) + "-" +
                        hexString.substring(8,12) + "-" +
                        hexString.substring(12,16) + "-" +
                        hexString.substring(16,20) + "-" +
                        hexString.substring(20,32);

                Log.d("UUID", uuid);

                //Here is your Major value
                int major = (scanRecord[startByte+20] & 0xff) * 0x100 + (scanRecord[startByte+21] & 0xff);

                //Here is your Minor value
                final int minor = (scanRecord[startByte+22] & 0xff) * 0x100 + (scanRecord[startByte+23] & 0xff);

                if (!isPresentInList(tempList, String.valueOf(minor)))
                    tempList.add(new BLEDevice(String.valueOf(minor), rssi));
                else {
                    updateInList(tempList, String.valueOf(minor), rssi);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        list.clear();
                        Collections.sort(tempList, rssiComparator);
                        for (BLEDevice d: tempList) {
                            list.add(d.name);
                        }
                        List<AllShopMappingResponse> rList = new ArrayList<>();
                        for (BLEDevice device: tempList) {
                            AllShopMappingResponse r = isInList(device);
                            if (r != null) {
                                rList.add(r);
                            }
                        }
                        ListAdapter adapter = new ListAdapter(MainActivity.this, rList);
//                            ArrayAdapter adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, list);
                        lVDevices.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                });


            }
        }
    };

    AllShopMappingResponse isInList(BLEDevice device) {
        for(AllShopMappingResponse response: allShopMappingResponses) {
            if (device.getName().equals(String.valueOf(response.getGatewayId()))) {
                return response;
            }
        }
        return null;
    }

    static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}