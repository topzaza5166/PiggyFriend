package com.example.topza.piggyfriend;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.StringTokenizer;

import app.akexorcist.bluetoothspp.BluetoothSPP;
import app.akexorcist.bluetoothspp.BluetoothState;
import app.akexorcist.bluetoothspp.DeviceList;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;

    private static float money_old = 0;
    private static int coin = 1;
    BluetoothSPP bt = new BluetoothSPP(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!bt.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_SHORT).show();
            finish();
        }

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                showAnimationMoney(message);
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext(), "Connected to " + name + "\n" + address
                        , Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() {
                Toast.makeText(getApplicationContext(), "Connection lost"
                        , Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() {
                Toast.makeText(getApplicationContext(), "Unable to connect"
                        , Toast.LENGTH_SHORT).show();
            }
        });

        bt.setAutoConnectionListener(new BluetoothSPP.AutoConnectionListener() {
            public void onNewConnection(String name, String address) {
                Log.i("Check", "New Connection - " + name + " - " + address);
            }

            public void onAutoConnectionStarted() {
                Log.i("Check", "Auto connection started");
            }
        });

        Button btnConnect = (Button)findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bt.disconnect();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
//        showAnimationMoney_test();
        if(!bt.isBluetoothEnabled()) {
            Intent intent = new Intent(getApplicationContext(), DeviceList.class);
            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
        } else {
            if(!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_ANDROID);
            }
        }
    }

    private void setup() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bt.stopService();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_ANDROID);
                setup();
            } else {
                Toast.makeText(getApplicationContext(), "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void showAnimationMoney(String money){
        ValueAnimator animator = new ValueAnimator();
        StringTokenizer splitmoney = new StringTokenizer(money, ".");
        money = splitmoney.nextToken();
        if(money_old == 0){
            animator.setFloatValues(0, Float.parseFloat(money));
        } else{
            animator.setFloatValues(money_old, Float.parseFloat(money));
        }
        coin = Integer.parseInt(money) - (int)Math.abs(money_old);
        money_old = Float.parseFloat(money);
//        Toast.makeText(getApplicationContext(), String.valueOf((int)Math.abs(money_old)), Toast.LENGTH_SHORT).show();
        if(coin == 1){
            ImageView coin1 = (ImageView) findViewById(R.id.coin1);
            coinAnimation(coin1);
        } else if(coin == 5){
            ImageView coin5 = (ImageView) findViewById(R.id.coin5);
            coinAnimation(coin5);
        } else if(coin == 10){
            ImageView coin10 = (ImageView) findViewById(R.id.coin10);
            coinAnimation(coin10);
        }
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                ((TextView)findViewById(R.id.TextMoney)).setText(String.format("%.2f",(float) animation.getAnimatedValue()));
            }
        });
        animator.start();
    }
    private void showAnimationMoney_test(){
        String money_test = "5.00";
        StringTokenizer splitmoney = new StringTokenizer(money_test, ".");
        money_test = splitmoney.nextToken();

        coin = Integer.parseInt(money_test) - (int)Math.abs(money_old);
        if(coin == 1){
            ImageView coin1 = (ImageView) findViewById(R.id.coin1);
            coinAnimation(coin1);
        } else if(coin == 5){
            ImageView coin5 = (ImageView) findViewById(R.id.coin5);
            coinAnimation(coin5);
        } else if(coin == 10){
            ImageView coin10 = (ImageView) findViewById(R.id.coin10);
            coinAnimation(coin10);
        }

        ValueAnimator animator = new ValueAnimator();
        animator.setFloatValues(0, Float.parseFloat(money_test));
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                ((TextView)findViewById(R.id.TextMoney)).setText(String.format("%.2f",(float) animation.getAnimatedValue()));
            }
        });
        animator.start();
    }

    private void coinAnimation(final ImageView coin){
        MediaPlayer coin_sound = MediaPlayer.create(this, R.raw.coin_drop_sound);
        Animation coinMoveAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_animation);
        coinMoveAnimation.setAnimationListener(new Animation.AnimationListener(){
            public void onAnimationEnd(Animation animation) {
                coin.setVisibility(View.GONE);
            }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
        });
        coin.setVisibility(View.VISIBLE);
        coin.startAnimation(coinMoveAnimation);
        coin_sound.setVolume(80, 80);
        coin_sound.start();
    }
}
