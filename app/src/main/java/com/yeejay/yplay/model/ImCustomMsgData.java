package com.yeejay.yplay.model;

/**
 * 自定义消息
 * Created by Administrator on 2017/11/23.
 */

public class ImCustomMsgData {

    private int DataType;
    private String Data;

    public int getDataType() {
        return DataType;
    }

    public void setDataType(int dataType) {
        DataType = dataType;
    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }

    @Override
    public String toString() {
        return "ImCustomMsgData{" +
                "DataType=" + DataType +
                ", Data='" + Data + '\'' +
                '}';
    }
}
