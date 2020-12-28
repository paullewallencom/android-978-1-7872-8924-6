package androidthings.project.rgbapp;

import android.app.DownloadManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;

import me.priyesh.chroma.ChromaDialog;
import me.priyesh.chroma.ColorMode;
import me.priyesh.chroma.ColorSelectListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private int red;
    private int green;
    private int blue;

    private int direction;
    private int func;

    private OkHttpClient client;
    private static final String baseUrl = "http://192.168.1.4:8180/home.html";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final EditText edt = (EditText) findViewById(R.id.delVal);

        // Init OkHTTP
        client = new OkHttpClient();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String delVal = edt.getText().toString();
                Log.d(TAG, "Func ["+func+"] - Red ["+red+"] - Green ["+green+"] "
                        + "- Blue ["+blue+"] - Dir ["+direction+"] - Delay ["+delVal+ "]");


                HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl).newBuilder()
                        .addQueryParameter("action", String.valueOf(func))
                        .addQueryParameter("red", String.valueOf(red))
                        .addQueryParameter("green", String.valueOf(green))
                        .addQueryParameter("blue", String.valueOf(blue))
                        .addQueryParameter("dir", String.valueOf(direction))
                        .addQueryParameter("delay", delVal);

                Request req = new Request.Builder()
                        .url(urlBuilder.build().toString())
                        .build();

                client.newCall(req).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "Error");
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.i("TAG", "Response.." + response.body().string());
                    }
                });

            }
        });

        Button btn = (Button) findViewById(R.id.btnColor);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ChromaDialog.Builder()
                        .initialColor(Color.BLUE)
                        .colorMode(ColorMode.RGB)
                        .onColorSelected(new ColorSelectListener() {
                            @Override public void onColorSelected(int color) {
                                Log.d(TAG, "Color selected");
                                red = Color.red(color);
                                green = Color.green(color);
                                blue = Color.blue(color);

                            }
                        })
                        .create()
                        .show(getSupportFragmentManager(), "dialog");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onDirectionClick(View v) {
        switch (v.getId()) {
            case R.id.dirFwd:
                direction = 1;
                break;
            case R.id.dirRev:
                direction = 2;
                break;
        }

    }

    public void onFunctionClick(View v) {
        switch (v.getId()) {
            case R.id.color:
                func = 0;
                break;
            case R.id.clear:
                func = 1;
                break;
            case R.id.rainbow:
                func = 2;
                break;
        }

    }
}
