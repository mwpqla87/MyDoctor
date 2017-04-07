package com.example.pc.mydoctordemo.ui.item;

/**
 * Created by PC on 2017-04-01.
 */

public class SupportThing {
    private String companyName;
    private String description;
    private int icon;


    public SupportThing(String companyName, String description,int icon){
        this.companyName = companyName;
        this.description = description;
        this.icon = icon;
    }
    public int getIcon(){
        return icon;
    }
    public String getCompanyName() {
        return companyName;
    }
    public String getDescription() {
        return description;
    }


}
