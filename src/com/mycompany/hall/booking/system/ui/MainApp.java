// MainApp.java
// This is the main entry point of the application.
// It starts with a splash screen and then shows the login view.
package com.mycompany.hall.booking.system.ui;

import java.util.Locale;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class MainApp {

    public static void main(String[] args) {
        // Set UI locale to Arabic
        Locale.setDefault(new Locale("ar"));
        UIManager.put("OptionPane.okButtonText", "موافق");
        UIManager.put("OptionPane.cancelButtonText", "إلغاء");
        UIManager.put("OptionPane.yesButtonText", "نعم");
        UIManager.put("OptionPane.noButtonText", "لا");
        
        // Run the GUI on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            SplashScreen splash = new SplashScreen();
            splash.setVisible(true);

            // Use a thread to close the splash screen after 4 seconds
            // and open the login window.
            new Thread(() -> {
                try {
                    Thread.sleep(4000); // Display splash for 4 seconds
                    splash.dispose();
                    new LoginView().setVisible(true);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }
}
