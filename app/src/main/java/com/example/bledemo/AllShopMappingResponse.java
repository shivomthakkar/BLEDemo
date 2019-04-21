package com.example.bledemo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AllShopMappingResponse {

@SerializedName("_id")
@Expose
private Integer id;
@SerializedName("gatewayId")
@Expose
private Integer gatewayId;
@SerializedName("shopId")
@Expose
private Integer shopId;
@SerializedName("shopName")
@Expose
private String shopName;

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

}