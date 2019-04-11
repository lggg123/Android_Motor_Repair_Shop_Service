package com.brainyapps.motolabz.Models;

/**
 * Created by HappyBear on 8/23/2018.
 */

public class ServiceListItem {
    public String service_name = "";
    public String service_rate = "";
    public boolean isSelected = false;

    public ServiceListItem(){
        service_name = "";
        service_rate = "";
        isSelected = false;
    }

    public void setSelected(boolean isChecked){
        isSelected = isChecked;
    }

    public boolean isSelected(){
        return isSelected;
    }
}
