package com.cyber.ui.swing;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class TrayIconComponent {

    SystemTray systemTray;
    Image image;
    PopupMenu menu;
    TrayIcon trayIcon;

    public TrayIconComponent() {
        this(new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB));
    }

    public TrayIconComponent(Image image) {
        if (!SystemTray.isSupported()) {
            System.err.println("System tray is not supported !!! ");
            return;
        }
        systemTray = SystemTray.getSystemTray();
        this.image = image;
        menu = new PopupMenu();
        trayIcon = new TrayIcon(image, null, menu);
        trayIcon.setImageAutoSize(true);

        try{
            systemTray.add(trayIcon);
        }catch(AWTException ex){
            System.err.println(ex.getMessage());
        }

    }

    public void addMenuItem(String label, ActionListener action){
        MenuItem menuItem = new MenuItem(label);
        menuItem.addActionListener(action);
        menu.add(menuItem);
    }

    public void addActionListener(ActionListener action){
        trayIcon.addActionListener(action);
    }

    public void dispose(){
        if (trayIcon!=null){
            systemTray.remove(trayIcon);
            trayIcon = null;
            menu = null;
            image = null;
        }
    }
}
