package shixia.moduletest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import shixia.moduletest.module_calendar.CalendarActivity;
import shixia.moduletest.module_imei.IMEIActivity;
import shixia.moduletest.module_pcpre.PicPreviewActivity;
import shixia.moduletest.module_save_pic.SavePicActivity;
import shixia.moduletest.module_tree.RecyclerTreeActivity;
import shixia.moduletest.numberpicker.NumberPickerActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnReadPhoneNumber;

    private Button btnActivityCalendar;

    private Button btnActivityNumberPicker;

    private Button btnActivityTreeRecycler;

    private Button btnActivityPicPreview;

    private Button btnActivitySavePic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnReadPhoneNumber = (Button) findViewById(R.id.btn_read_phone_number);

        btnActivityCalendar = (Button) findViewById(R.id.btn_activity_calendar);

        btnActivityNumberPicker = (Button) findViewById(R.id.btn_activity_number_picker);

        btnActivityTreeRecycler = (Button) findViewById(R.id.btn_activity_tree_recycler);

        btnActivityPicPreview = (Button) findViewById(R.id.btn_activity_pic_preview);

        btnActivitySavePic = (Button) findViewById(R.id.btn_activity_save_pic);

        initClickEvent();
    }

    private void initClickEvent() {
        btnReadPhoneNumber.setOnClickListener(this);
        btnActivityCalendar.setOnClickListener(this);
        btnActivityNumberPicker.setOnClickListener(this);
        btnActivityTreeRecycler.setOnClickListener(this);
        btnActivityPicPreview.setOnClickListener(this);
        btnActivitySavePic.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btn_read_phone_number:
                Intent intent = new Intent(this,IMEIActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_activity_calendar:
                Intent calendarIntent = new Intent(this,CalendarActivity.class);
                startActivity(calendarIntent);
                break;
            case R.id.btn_activity_number_picker:
                Intent numberPickerIntent = new Intent(this,NumberPickerActivity.class);
                startActivity(numberPickerIntent);
                break;
            case R.id.btn_activity_tree_recycler:
                Intent treeRecyclerIntent = new Intent(this,RecyclerTreeActivity.class);
                startActivity(treeRecyclerIntent);
                break;
            case R.id.btn_activity_pic_preview:
                Intent pcPreviewIntent = new Intent(this,PicPreviewActivity.class);
                startActivity(pcPreviewIntent);
                break;
            case R.id.btn_activity_save_pic:
                Intent savePicIntent = new Intent(this,SavePicActivity.class);
                startActivity(savePicIntent);
                break;
            default:
                break;
        }
    }
}
