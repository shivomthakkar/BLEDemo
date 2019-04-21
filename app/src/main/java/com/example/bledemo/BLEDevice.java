package com.example.bledemo;

import java.util.Comparator;

class BLEDevice {
    String name;
    int rssi;

    BLEDevice(String name, int rssi) {
        this.name = name;
        this.rssi = rssi;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }


    static Comparator<BLEDevice> rssiComparator = new Comparator<BLEDevice>() {
        @Override
        public int compare(BLEDevice o1, BLEDevice o2) {
            int type1 = o1.rssi;
            int type2 = o2.rssi;

            return type2 - type1;
        }
    };

}