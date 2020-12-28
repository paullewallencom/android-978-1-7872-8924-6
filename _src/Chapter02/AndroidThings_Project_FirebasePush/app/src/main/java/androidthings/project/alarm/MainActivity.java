package androidthings.project.alarm;

/*
 *  francesco Azzola
 *
 * (http://www.survivingwithandroid.com)
 *
 */

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import androidthings.project.alarm.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final View view = findViewById(R.id.mainView);

        Button btn = (Button) findViewById(R.id.btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseMessaging.getInstance().subscribeToTopic("alarm");
                String tkn = FirebaseInstanceId.getInstance().getToken();

                Snackbar.make(view, "Device subscribed to the channel", Snackbar.LENGTH_LONG).show();
                Log.d("App", "Token ["+tkn+"]");
            }
        });
    }
}
