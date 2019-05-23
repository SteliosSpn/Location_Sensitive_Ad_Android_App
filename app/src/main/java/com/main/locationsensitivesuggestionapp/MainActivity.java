package com.main.locationsensitivesuggestionapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    int progress_value = 1;
    boolean open;
    String placeType = "nothing";

    SeekBar max_bar;
    TextView text_bar;
    Switch switch_open;
    Spinner dropdownlist;
    String[] items = new String[]{"nothing" ,"restaurant", "bank", "gas_station", "bar", "bakery", "cafe", "gym", "police"};

    WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    void init(){
        dropdownlist = findViewById(R.id.dropdownlist);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdownlist.setAdapter(adapter);

        max_bar = findViewById(R.id.max_bar);
        switch_open = findViewById(R.id.switch_open);

        text_bar = findViewById(R.id.text_bar);
        text_bar.setText(max_bar.getProgress() + " / " + (max_bar.getMax()-1));

        dropdownlist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                placeType = parent.getItemAtPosition(position).toString();
                if(position == 0){
                    text_bar.setEnabled(true);
                    max_bar.setEnabled(true);
                }else{
                    text_bar.setEnabled(false);
                    max_bar.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                placeType = "nothing";
            }
        });

        switch_open.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    open = true;
                }
                else{
                    open = false;
                }
            }
        });

        max_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress_value = progress;
                text_bar.setText(progress_value + " / " + (max_bar.getMax()-1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                text_bar.setText(progress_value + " / " + (max_bar.getMax()-1));
            }
        });

        Button map_btn = (Button) findViewById(R.id.map_btn);

        map_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check WiFi Connection before you proceed
                checkWiFiStatus();
            }
        });
    }

    void checkWiFiStatus(){
        //init wifiManager
        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(wifiManager.isWifiEnabled()){
            //Go to map with filters selected
            Intent intent = new Intent(MainActivity.this,MapsActivityCurrentPlace.class);
            Bundle b = new Bundle();
            b.putInt("progress", progress_value);
            b.putBoolean("open",open);
            b.putString("placeType",placeType);
            intent.putExtras(b);
            startActivity(intent);
        }else {
            //wifiManager.setWifiEnabled(true);
            //Open Dialog
            openOptions();
        }

    }

    void openOptions(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No NetWork Detected");
        builder.setIcon(R.drawable.common_google_signin_btn_icon_dark_normal);
        builder.setMessage("Enable WiFi?");
        builder.setPositiveButton("NetWork Settings",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                });

        builder.setNeutralButton("Enable WiFi",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        //Enable WiFi
                        wifiManager.setWifiEnabled(true);
                        //Go to map with filters selected
                        Intent intent = new Intent(MainActivity.this,MapsActivityCurrentPlace.class);
                        Bundle b = new Bundle();
                        b.putInt("progress", progress_value);
                        b.putBoolean("open",open);
                        b.putString("placeType",placeType);
                        intent.putExtras(b);
                        startActivity(intent);
                    }
                });

        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }
}
