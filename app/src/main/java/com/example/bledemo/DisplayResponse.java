package com.example.bledemo;

import java.util.Comparator;

public class DisplayResponse {

    private Integer id;
    private Integer gatewayId;
    private Integer shopId;
    private String shopName;
    private int rssi;

    public DisplayResponse(AllShopMappingResponse r, int rssi) {
        id = r.getId();
        gatewayId = r.getGatewayId();
        shopId = r.getShopId();
        shopName = r.getShopName();
        this.rssi = rssi;
    }

    public Integer getId() {
    return id;
    }

    public void setId(Integer id) {
    this.id = id;
    }

    public Integer getGatewayId() {
    return gatewayId;
    }

    public void setGatewayId(Integer gatewayId) {
    this.gatewayId = gatewayId;
    }

    public Integer getShopId() {
    return shopId;
    }

    public void setShopId(Integer shopId) {
    this.shopId = shopId;
    }

    public String getShopName() {
    return shopName;
    }

    public void setShopName(String shopName) {
    this.shopName = shopName;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }


    static Comparator<DisplayResponse> rssiComparator = new Comparator<DisplayResponse>() {
        @Override
        public int compare(DisplayResponse o1, DisplayResponse o2) {
            int type1 = o1.rssi;
            int type2 = o2.rssi;

            return type2 - type1;
        }
    };

}