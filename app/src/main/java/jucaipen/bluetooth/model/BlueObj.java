package jucaipen.bluetooth.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/8.
 */

public class BlueObj implements Serializable {
    private  String name;
    private  String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
