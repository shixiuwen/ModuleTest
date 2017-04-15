package shixia.moduletest.numberpicker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import shixia.moduletest.R;

/**
 * Created by ShiXiuwen on 2017/3/14.
 * Description:
 */

public class NumberPickerActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener {

    private NumberPicker numberPickerHour;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_picker);

        numberPickerHour = (NumberPicker) findViewById(R.id.numberPicker_hour);

        setNP_hour_Value();
    }

    public void setNP_hour_Value() {
        numberPickerHour.setMaxValue(23);
        numberPickerHour.setMinValue(0);
        numberPickerHour.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPickerHour.setWrapSelectorWheel(false);
        numberPickerHour.setOnValueChangedListener(this);
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        Log.e("onValueChange", "oldVal:" + oldVal + " newVal:" + newVal);
    }
}
