package shixia.moduletest.module_screem;

import android.content.Context;
import android.content.Intent;
import android.support.v4.util.ArrayMap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import shixia.moduletest.R;

/**
 * Created by AmosShi on 2018/1/11.
 * Email : shixiuwen1991@yeah.net
 * Abstract :
 */

public class ScreenActivity extends AppCompatActivity {

    private EditText etY;
    private EditText etX;
    private Button btnSure;

    private Map<String, Integer> map = new ArrayMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);

        etY = (EditText) findViewById(R.id.et_y);
        etX = (EditText) findViewById(R.id.et_x);
        btnSure = (Button) findViewById(R.id.btn_sure);

        initMap();

        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateFile(etY.getText().toString(), etX.getText().toString());
            }
        });

    }

    private void generateFile(String yBaseStr, String xBaseStr) {
        int yBase = Integer.parseInt(yBaseStr);
        int xBase = Integer.parseInt(xBaseStr);
        Set<String> yKeySets = map.keySet();
        File folder = new File(getDiskCacheDir(this, "screen"));
        if (!folder.exists()) {
            boolean mkdir = folder.mkdir();
            if (!mkdir) {
                return;
            }
        }

        for (String yStr : yKeySets) {
            Integer x = map.get(yStr);
            Integer y = Integer.parseInt(yStr.replaceAll("_", ""));
            File valueFolder = new File(getDiskCacheDir(this, "screen"), "values-" + y + "x" + x);
            if (!valueFolder.exists()) {
                boolean success = valueFolder.mkdir();
                if (success) {
                    File xFile = new File(getDiskCacheDir(this, "screen" + File.separator + "values-" + y + "x" + x), "lay_x.xml");
                    File yFile = new File(getDiskCacheDir(this, "screen" + File.separator + "values-" + y + "x" + x), "lay_y.xml");
                    if (!xFile.exists() && !yFile.exists()) {
                        try {
                            boolean newXFile = xFile.createNewFile();
                            boolean newYFile = yFile.createNewFile();
                            if (!newXFile || !newYFile) {
                                return;
                            }
                            generateFile(x, "x", xFile, xBase);
                            generateFile(y, "y", yFile, yBase);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }

            Toast.makeText(this, "生成适配文件成功！", Toast.LENGTH_SHORT).show();
        }
    }

    private void generateFile(Integer otherPx, String xOry, File file, Integer basePx) {
        try {
            String str = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<resources>\n";
            StringBuilder builder = new StringBuilder(str);
            for (Integer i = 1; i <= basePx; i++) {
                //首先计算像素值
                int value;
                if (i == 1) {
                    value = 1;
                } else {
                    value = (int) (i * ((float) otherPx / basePx) + 0.5);
                    if (value == 0) {
                        value = 1;
                    }
                }
                builder.append("<dimen name=\"" + xOry + i + "\">" + value + "px</dimen>\n");
            }
            builder.append("</resources>");

            FileOutputStream fos = new FileOutputStream(file, true);
            byte[] bytes = builder.toString().getBytes();
            fos.write(bytes);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获得Crash文件路径
     * <p>
     * getCacheDir和getFilesDir是放在/data/data/packagename下的，
     * 所以这个目录中的内容必须是root的手机在文件操作系统中才能看到。当然
     * 如果在应用程序中清空数据或者卸载应用，那么这俩个目录下的文件也将会
     * 被清空的。getExternalCacheDir和getExternalFilesDir是存放
     * 在/storage/sdcard0/Android/data/packagename下面的，这个是放
     * 在外置存储卡的，这个目录下的内容 可以使用文件浏览系统查看到，但是如果
     * 清空数据或者卸载应用，俩个目录下的文件也将被清空。或者也可以理解为带
     * external这样的是存储在外置sd卡的，而直接使用getFilesDir这种是放
     * 在/data/data下面的。
     *
     * @param context context
     * @param dirName dirName
     * @return dir
     */
    private String getDiskCacheDir(Context context, String dirName) {
        String cachePath = null;
        if ("mounted".equals(Environment.getExternalStorageState())) {
            File externalCacheDir = context.getExternalCacheDir();
            if (externalCacheDir != null) {
                cachePath = externalCacheDir.getPath();
            }
        }
        if (cachePath == null) {
            File cacheDir = context.getCacheDir();
            if ((cacheDir != null) && (cacheDir.exists())) {
                cachePath = cacheDir.getPath();
            }
        }
        //0/emulate/Android/data/data/com.***.********/crash/crash.log
        return cachePath + File.separator + dirName;
    }

    private void initMap() {
        map.put("4500", 3200);
        map.put("2960", 1440);
        map.put("2560_", 1600);
        map.put("2560__", 1536);
        map.put("2560___", 1440);
        map.put("2413", 1440);
        map.put("2390", 1440);
        map.put("2220", 1080);
        map.put("2160", 1080);
        map.put("2048", 1536);
        map.put("2040", 1080);
        map.put("1920", 1200);
        map.put("1920_", 1152);
        map.put("1920__", 1080);
        map.put("1824", 1200);
        map.put("1812", 1080);
        map.put("1800", 1080);
        map.put("1794", 1080);
        map.put("1776", 1080);
        map.put("1719", 1080);
        map.put("1280", 800);
        map.put("1280_", 768);
        map.put("1280__", 720);
        map.put("1220", 720);
        map.put("1196", 768);
        map.put("1196_", 720);
        map.put("1184", 720);
        map.put("1152", 735);
        map.put("1024", 768);
        map.put("1024_", 600);
        map.put("960", 640);
        map.put("960_", 540);
        map.put("800", 480);
    }
}
