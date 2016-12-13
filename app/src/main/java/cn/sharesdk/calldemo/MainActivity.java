package cn.sharesdk.calldemo;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private static final int REQUEST_CODE = 0;

    EditText mEditTextPhoneNumber1;
    EditText mEditTextPhoneNumber2;
    Button mBtnStartCallNumber1SpecTime;
    Button mBtnStartCallNumber2SpecTime;
    Button mBtnStartCallNumber1Now;
    Button mBtnStartCallNumber2Now;
    EditText mEditTextHour;
    EditText mEditTextMin;
    EditText mEditTextSec;
    Intent mIntent;
    String mNumber1;
    String mNumber2;
    int mHour;
    int mMin;
    int mSec;
    PendingIntent mPendingIntent;
    AlarmManager mAlarmManager;
    Calendar mCalendar;

    private boolean mIsCallNow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAlarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        mEditTextPhoneNumber1 = (EditText) findViewById(R.id.et_phonenumber1);
        mBtnStartCallNumber1SpecTime = (Button) findViewById(R.id.btn_call_number1_spec_time);
        mBtnStartCallNumber1Now = (Button) findViewById(R.id.btn_call_number1_now);
        mEditTextPhoneNumber2 = (EditText) findViewById(R.id.et_phonenumber2);
        mBtnStartCallNumber2SpecTime = (Button) findViewById(R.id.btn_call_number2_spec_time);
        mBtnStartCallNumber2Now = (Button) findViewById(R.id.btn_call_number2_now);
        mEditTextHour = (EditText) findViewById(R.id.et_hour);
        mEditTextMin = (EditText) findViewById(R.id.et_min);
        mEditTextSec = (EditText) findViewById(R.id.et_sec);

        mBtnStartCallNumber1SpecTime.setOnClickListener(this);
        mBtnStartCallNumber2SpecTime.setOnClickListener(this);
        mBtnStartCallNumber1Now.setOnClickListener(this);
        mBtnStartCallNumber2Now.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mNumber1 = mEditTextPhoneNumber1.getText().toString();
        mNumber2 = mEditTextPhoneNumber2.getText().toString();
        mHour = Integer.parseInt(mEditTextHour.getText().toString());
        mMin = Integer.parseInt(mEditTextMin.getText().toString());
        mSec = Integer.parseInt(mEditTextSec.getText().toString());
        switch (v.getId()){
            case R.id.btn_call_number1_spec_time:
                if (!TextUtils.isEmpty(mNumber1)){
                    Toast.makeText(this,"Wait for start call",Toast.LENGTH_SHORT).show();
                    startCallSpecTime(mNumber1);
                }else {
                    Toast.makeText(this,"Please input number", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_call_number1_now:
                if (!TextUtils.isEmpty(mNumber1)){
                    startCallNow(mNumber1);
                }else {
                    Toast.makeText(this,"Please input number", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_call_number2_spec_time:
                if (!TextUtils.isEmpty(mNumber2)){
                    Toast.makeText(this,"Wait for start call",Toast.LENGTH_SHORT).show();
                    startCallSpecTime(mNumber2);
                }else {
                    Toast.makeText(this,"Please input number", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_call_number2_now:
                if (!TextUtils.isEmpty(mNumber2)){
                    startCallNow(mNumber2);
                }else {
                    Toast.makeText(this,"Please input number", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    /**
     * 等待到特定时刻拨打
     * */
    private void startCallSpecTime(String number) {
        mIsCallNow = false;
        mIntent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel" , number, null));
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mPendingIntent = PendingIntent.getActivities(this, 0, new Intent[]{mIntent}, 0);

        mCalendar = Calendar.getInstance();
        // 根据用户选择时间来设置Calendar对象
        mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
        mCalendar.set(Calendar.MINUTE, mMin);
        mCalendar.set(Calendar.SECOND, mSec);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            //没有权限，需要申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE);
            return;
        }else {
            //已有权限， 可直接拨打
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(),mPendingIntent);
        }
    }

    /**
     * 立即拨打
     * */
    private void startCallNow(String number) {
        mIsCallNow = true;
        mIntent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel" , number, null));
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            //没有权限，需要申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE);
            return;
        }else {
            //已有权限， 可直接拨打
            startActivity(mIntent);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE){
            if (permissions[0].equals(Manifest.permission.CALL_PHONE) && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //用户同意
                if (mIsCallNow){
                    startActivity(mIntent);
                }else {
                    mAlarmManager.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(),mPendingIntent);
                }
            }else {
                Toast.makeText(this, "Has no permission", Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    }

    /**
     * 如果一次电话没有接通，则再次拨打，直到打通为止，需要监听通话状态
     * */

}
