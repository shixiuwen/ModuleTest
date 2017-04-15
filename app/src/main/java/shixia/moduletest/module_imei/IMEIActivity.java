package shixia.moduletest.module_imei;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import shixia.moduletest.R;

/**
 * Created by ShiXiuwen on 2017/2/20.
 * Description:
 */

public class IMEIActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imei_layout);

        TextView tvImei = (TextView) findViewById(R.id.tv_imei);

        Log.i("deviceId", TestInstallation.id(this));
//        tvImei.setText(TestInstallation.id(this));

        String m_szDevIDShort = "35" + //we make this look like a valid IMEI
                Build.BOARD.length()%10 + Build.BRAND.length()%10 +
                Build.CPU_ABI.length()%10 + Build.DEVICE.length()%10 +
                Build.DISPLAY.length()%10 + Build.HOST.length()%10 +
                Build.ID.length()%10 + Build.MANUFACTURER.length()%10 +
                Build.MODEL.length()%10 + Build.PRODUCT.length()%10 +
                Build.TAGS.length()%10 + Build.TYPE.length()%10 + Build.USER.length()%10 ; //13 digits


        tvImei.setText(m_szDevIDShort);

    }
}
