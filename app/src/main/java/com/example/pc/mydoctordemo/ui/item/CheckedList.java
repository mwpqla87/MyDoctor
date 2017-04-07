package com.example.pc.mydoctordemo.ui.item;

/**
 * Created by PC on 2017-04-02.
 */

public class CheckedList {
    private String listName;
    private int imageIcon;
    private int data;

    public CheckedList(String name, int imageIcon){
        this.listName = name;
        this.imageIcon = imageIcon;
    }



    public void setData(int data){
        this.data = data;
    }

    public String getListName() {
        return listName;
    }

    public int getImageIcon() {
        return imageIcon;
    }
}
