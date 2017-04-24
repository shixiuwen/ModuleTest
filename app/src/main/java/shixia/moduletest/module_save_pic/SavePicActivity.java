package shixia.moduletest.module_save_pic;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import shixia.moduletest.R;

public class SavePicActivity extends AppCompatActivity {

    private ImageView ivFromInternet;
    private Button btnSavePic;
    private static File myCaptureFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_pic);

        ivFromInternet = (ImageView) findViewById(R.id.iv_from_internet);
        btnSavePic = (Button) findViewById(R.id.btn_save_pic);

        btnSavePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission();
            }
        });
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
        } else {
//            saveFile("aaaaaaaaaaaaaaaa.jpg", "http://oa-jiurong.oss-cn-shanghai.aliyuncs.com/erp/base/2017-04-18/301/71520/2/149249892758f5b9ef0b9dd.jpg");
            saveManyFile();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200) {
//            saveFile("aaaaaaaaaaaaaaaa.jpg", "http://oa-jiurong.oss-cn-shanghai.aliyuncs.com/erp/base/2017-04-18/301/71520/2/149249892758f5b9ef0b9dd.jpg");
            saveManyFile();
        }
    }

    private void saveManyFile() {
        for (int i = 0; i < 10; i++) {
            saveFile(i + "" + i + ".jpg", "http://img2.niutuku.com/desk/1208/2024/ntk-2024-4229.jpg");
        }
    }

    public void saveFile(final String fileName, String imgUrl) {
        String ALBUM_PATH = Environment.getExternalStorageDirectory() + "/temp/";
        File dirFile = new File(ALBUM_PATH);
        if (!dirFile.exists()) {
            boolean mkdir = dirFile.mkdir();
            if (!mkdir) {
                return;
            }
        }
        final File myCaptureFile = new File(ALBUM_PATH + fileName);

        try {
            URL url = new URL(imgUrl);
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5 * 1000);
            conn.setRequestMethod("GET");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int contentLength = 0;
                    int totalCount = 0;
                    int responseCode = 0;
                    try {
                        responseCode = conn.getResponseCode();
                        InputStream is = null;
                        contentLength = conn.getContentLength();
                        Log.e("IOException", contentLength + "");
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            is = conn.getInputStream();
                        }
                        if (is == null) {
                            return;
                        }
                        //创建文件
                        boolean newFile = myCaptureFile.createNewFile();
                        if (!newFile) {
                            return;
                        }
                        FileOutputStream fileOutputStream = new FileOutputStream(myCaptureFile);
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = is.read(buf)) != -1) {
                            fileOutputStream.write(buf, 0, len);
                            totalCount += len;
                        }
                        is.close();
                        fileOutputStream.close();
                        // TODO: 2017/4/18 下载成功，此处handler处理
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        Log.e("IOException", "totalCount:" + totalCount + " contentLength:" + contentLength);
                        if (totalCount != 0 && totalCount == contentLength) {
                            Log.e("IOException", fileName + " " + "已下载成功");
                        } else {
                            // TODO: 2017/4/24 下载未成功，可以删除下载了一半的文件，如有需要可以保存当前失败文件的文件名和url，网络环境好的时候再重新下载
                            boolean delete = myCaptureFile.delete();
                            if (delete) {
                                Log.e("IOException", fileName + " " + "未下载成功，删除临时文件成功！");
                            } else {
                                Log.e("IOException", fileName + " " + "未下载成功，删除临时文件失败！");
                            }
                        }
                    }

                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
