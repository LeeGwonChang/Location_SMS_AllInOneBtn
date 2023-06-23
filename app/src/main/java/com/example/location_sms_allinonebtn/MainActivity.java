package com.example.location_sms_allinonebtn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

public class MainActivity extends AppCompatActivity {

    EditText phoneETxt;
    EditText msgETxt;
    Switch titleSw, gpsSw;
    Button sendBtn;

    String msgLocation = null;

    CountDownLatch latch = new CountDownLatch(1);
    String title = "Safer";


    static final int PERMISSIONS_REQUEST = 0x00000001;     // 모든 권한 요청 확인할 변수

    private String[] PERMISSIONS = {                        // 요청할 권한들
            Manifest.permission.SEND_SMS
    };

    // SMS 메서드 생성
    public void sendSMS(String phoneNum, String message){
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNum,null, message, pi, null);
        Toast.makeText(getApplicationContext(),"메세지 전송 완료", Toast.LENGTH_SHORT).show();
    }


    

    // 권한 허용
    public void onChackPermission() {

        int msgPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);    // 문자 권한 확인
        int gpsPermissionCheck_1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);   // GPS 권한 확인
        int gpsPermissionCheck_2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);    // GPS 권한 확인

        // 거부 또는 요청되지 않은 권한이 있다면 다시 권한 요청
        if (msgPermissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSIONS_REQUEST);
        }
        if (gpsPermissionCheck_1 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST);
        }
        if (gpsPermissionCheck_2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phoneETxt = findViewById(R.id.phoneETxt);
        msgETxt = findViewById(R.id.msgETxt);
        titleSw = findViewById(R.id.titleSw);
        gpsSw = findViewById(R.id.gpsSw);
        sendBtn = findViewById(R.id.sendBtn);
        onChackPermission();
        int[] SwitchState = new int[2];         // 1 = On, 2 = Off
                                                // SwitchState[0] : 타이틀 포함, SwitchState[1]: 위치 포함


        // 위치 관리자를 통해 초기화 -> 위치 업데이트를 받을 수 잇음
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // 위치 리스너 구현 -> 업데이트된 위치를 받아올 수 있음
        final LocationListener gpsLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                double longitude = location.getLongitude(); // 위도
                double latitude = location.getLatitude(); // 경도
                msgLocation = ("<위치정보> \n 위도 : " + longitude + "\n 경도 : " + latitude);
            }
        };




        gpsSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b){
                    // 위치 정보 업데이트
                    // 위치정보를 원하는 시간 거리마다 갱신
                    // GPS 위치 엄데이트 요청 메서드 = requestLocationUpdates()
                    // GPS 위치 제공자 = LocationManager.GPS_PROIDER
                    // 위치 업데이트 최소 시간 간격 = 1000ms
                    // 위치 업데이트 최소 거리 간격 = 1M
                    // 리스너 메서드인 gpsLocationListener 메서드 실행
                    // LocationManager.GPS_PROVIDER : GPS를 사용해서 위치정보 제공
                    // LocationManager.NETWORK_PROVIDER : 셀룰러 or Wi-Fi를 사용하여 위치정보 제공
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // GPS 위치 권한 요청 메세지 작성
                        System.out.println(msgLocation);
                    }
                    System.out.println(msgLocation);
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, gpsLocationListener);
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, gpsLocationListener);
                }else{
                    msgLocation = null;
                }
                System.out.println("위치 정보 포함 : " +gpsSw.isChecked());
            }
        });

        titleSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {

                }else{

                }
                System.out.println("Title 포함 : " + titleSw.isChecked());
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSMS(phoneETxt.toString(), msgETxt.toString());
                if (titleSw.isChecked()){
                    msgLocation = title + "\n\n" + msgLocation;
                }
                if (gpsSw.isChecked()){
                    msgLocation += "\n\n"+ msgETxt;
                }
            }
        });
    }
}