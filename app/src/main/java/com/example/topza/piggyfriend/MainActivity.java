package com.example.topza.piggyfriend;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
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
import pl.droidsonroids.gif.GifImageView;


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
                achievement(message);
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
        } else if(coin == 20){
            ImageView coin20 = (ImageView) findViewById(R.id.coin1);
            coinAnimation(coin20);
        } else if(coin == 50){
            ImageView coin50 = (ImageView) findViewById(R.id.coin5);
            coinAnimation(coin50);
        } else if(coin == 100){
            ImageView coin100 = (ImageView) findViewById(R.id.coin1);
            coinAnimation(coin100);
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
        String money_test = "1000.00";
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
        animator.setDuration(2000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                ((TextView)findViewById(R.id.TextMoney)).setText(String.format("%.2f",(float) animation.getAnimatedValue()));
            }
        });
        animator.start();
        achievement(money_test);
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

    private void achievement(String money){
        StringTokenizer splitmoney = new StringTokenizer(money, ".");
        money = splitmoney.nextToken();
        int achievement_money = Integer.parseInt(money);

        final GifImageView achievement1 = (GifImageView)findViewById(R.id.achievement_gif1);
        final GifImageView achievement2 = (GifImageView)findViewById(R.id.achievement_gif2);
        final GifImageView achievement_super_prize = (GifImageView)findViewById(R.id.achievement_super_prize);

        SharedPreferences finished_achievement = getSharedPreferences("fin_achieve", 0);
        SharedPreferences.Editor fin_edit = finished_achievement.edit();
        final int fin = finished_achievement.getInt("key_fin", 0);
        int a = 0;
        int check_state = fin;

        if(achievement_money >= 100 && achievement_money < 500){
            a = 1;
        } else if(achievement_money >= 500 && achievement_money < 1000){
            a = 2;
        } else if(achievement_money >= 1000 && achievement_money < 2000){
            a = 3;
        } else{
            a = 0;
        }

        if(a == 1 && check_state != a){
            achievement1.setVisibility(View.VISIBLE);
            achievement2.setVisibility(View.VISIBLE);
            achievement1.setImageResource(R.drawable.firework_achievement_100);
            achievement2.setImageResource(R.drawable.firework_achievement_100);
            findViewById(R.id.layoutMoney).setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "Achievement Unlock", Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), "your money reached 100 Baht", Toast.LENGTH_SHORT).show();
        } else if(a == 2 && check_state != a){
            achievement1.setVisibility(View.VISIBLE);
            achievement2.setVisibility(View.VISIBLE);
            achievement1.setImageResource(R.drawable.pig_dancing_achievement_2);
            achievement2.setImageResource(R.drawable.pig_dancing_achievement_2);
            findViewById(R.id.layoutMoney).setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "Achievement Unlock", Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), "your money reached 500 Baht", Toast.LENGTH_SHORT).show();
        } else if(a == 3 && check_state != a){
            achievement_super_prize.setVisibility(View.VISIBLE);
            achievement_super_prize.setBackgroundResource(R.drawable.firework_achievement_super_prize);
            findViewById(R.id.layoutMoney).setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "Achievement Unlock", Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), "your money reached 1000 Baht", Toast.LENGTH_SHORT).show();
            MediaPlayer win_sound = MediaPlayer.create(this, R.raw.win_prize_sound);
            win_sound.setVolume(80, 80);
            win_sound.start();
        }

        fin_edit.putInt("key_fin", a).commit();
//        Toast.makeText(getApplicationContext(), String.valueOf(a) + "|" + String.valueOf(fin) + "|" + String.valueOf(achievement_money)
//                , Toast.LENGTH_SHORT).show();
        new CountDownTimer(5000, 1000) { // 5000 = 5 sec

            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                achievement1.setVisibility(View.INVISIBLE);
                achievement2.setVisibility(View.INVISIBLE);
                achievement_super_prize.setVisibility(View.INVISIBLE);
                findViewById(R.id.layoutMoney).setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private AlertDialog achievementDialog(final AppCompatActivity act, CharSequence title,
                                     CharSequence message, CharSequence buttonYes){
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title).setMessage(message).setPositiveButton(buttonYes, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        return downloadDialog.show();
    }
}
