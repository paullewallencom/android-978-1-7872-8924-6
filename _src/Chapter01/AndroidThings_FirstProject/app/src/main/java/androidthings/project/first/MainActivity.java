package androidthings.project.first;


import android.app.Activity;
import android.os.Bundle;


import com.google.android.things.contrib.driver.button.Button;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

public class MainActivity extends Activity {

    private boolean redPressed = false;
    private boolean greenPressed = false;
    private boolean bluePressed = false;

    private Gpio redIO;
    private Gpio blueIO;
    private Gpio greenIO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            // Leds control Pins
            PeripheralManagerService manager = new PeripheralManagerService();
           // blueIO = manager.openGpio("BCM16");
            blueIO = manager.openGpio("IO7"); // Intel Edison
            blueIO.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
           // greenIO = manager.openGpio("BCM19");
            greenIO = manager.openGpio("IO4"); // Intel Edison
            greenIO.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);

            // redIO = manager.openGpio("BCM26");
            redIO = manager.openGpio("IO2"); // Intel Edison
            redIO.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            redIO.setValue(false);
            blueIO.setValue(false);
            greenIO.setValue(false);

            //Button button1 = new Button("BCM4", Button.LogicState.PRESSED_WHEN_LOW);
            Button button1 = new Button("IO13", Button.LogicState.PRESSED_WHEN_LOW);
            button1.setOnButtonEventListener(new Button.OnButtonEventListener() {
                @Override
                public void onButtonEvent(Button button, boolean pressed) {

                    if (pressed) {
                        redPressed = !redPressed;
                        try {
                            redIO.setValue(redPressed);
                        }
                        catch (IOException e1) {}
                    }

                }
            });
            // Button button2 = new Button("BCM17", Button.LogicState.PRESSED_WHEN_LOW);
            Button button2 = new Button("IO12", Button.LogicState.PRESSED_WHEN_LOW); // Intel Edison
            button2.setOnButtonEventListener(new Button.OnButtonEventListener() {
                @Override
                public void onButtonEvent(Button button, boolean pressed) {

                    if (pressed) {
                        greenPressed = !greenPressed;
                        try {
                            greenIO.setValue(greenPressed);
                        }
                        catch (IOException e1) {}
                    }
                }
            });
            //Button button3 = new Button("BCM27", Button.LogicState.PRESSED_WHEN_LOW);
            Button button3 = new Button("IO8", Button.LogicState.PRESSED_WHEN_LOW); // Intel Edison
            button3.setOnButtonEventListener(new Button.OnButtonEventListener() {
                @Override
                public void onButtonEvent(Button button, boolean pressed) {

                    if (pressed) {
                        bluePressed = !bluePressed;
                        try {
                            blueIO.setValue(bluePressed);
                        }
                        catch (IOException e1) {}
                    }
                }
            });
        }
        catch(Exception ioe) {
            ioe.printStackTrace();
        }
    }



}
