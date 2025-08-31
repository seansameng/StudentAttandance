package com.example.studentattandance.models;

public class SettingItem {
    
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;
    public static final int TYPE_SWITCH = 2;
    public static final int TYPE_INFO = 3;
    
    private String title;
    private String subtitle;
    private int type;
    private boolean switchValue;
    
    public SettingItem(String title, String subtitle, int type) {
        this.title = title;
        this.subtitle = subtitle;
        this.type = type;
        this.switchValue = false;
    }
    
    public SettingItem(String title, String subtitle, int type, boolean switchValue) {
        this.title = title;
        this.subtitle = subtitle;
        this.type = type;
        this.switchValue = switchValue;
    }
    
    // Getters and Setters
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getSubtitle() {
        return subtitle;
    }
    
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }
    
    public int getType() {
        return type;
    }
    
    public void setType(int type) {
        this.type = type;
    }
    
    public boolean isSwitchValue() {
        return switchValue;
    }
    
    public void setSwitchValue(boolean switchValue) {
        this.switchValue = switchValue;
    }
}
