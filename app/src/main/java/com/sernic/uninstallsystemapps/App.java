package com.sernic.uninstallsystemapps;

import android.graphics.drawable.Drawable;

/**
 * Created by nicola on 11/12/17.
 */

public class App {
    private String name;
    private String path;
    private Drawable icon;
    private boolean isSelected;

    public App(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {return name; }

    public void setName(String name) {
        this.name = name;
    }
}