package shixia.moduletest.module_input_limit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import shixia.moduletest.R;

/**
 * Created by ShiXiuwen on 2017/5/25.
 * <p>
 * Email:shixiuwen1991@yeah.net
 * Description:
 */

public class InputLimitActivity extends AppCompatActivity {

    private String beforeText = "";
    private String afterText = "";

    private int indexStart = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_limit);

        final EditText etInput = (EditText) findViewById(R.id.et_input);
        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s != null) {
                    beforeText = s.toString();
                    Log.e("onTextChanged01", start + " " + count + " " + after);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("onTextChanged02", start + " " + before + " " + count);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || TextUtils.isEmpty(s.toString())) {
                    return;
                }
                afterText = s.toString();
                Pattern pattern = Pattern.compile("^0*\\d{0,7}$|^0*\\d{0,7}\\.\\d{0,2}$");
                Matcher matcher = pattern.matcher(afterText);
                if (!matcher.matches()) {
                    etInput.setText(beforeText);
                    etInput.setSelection(etInput.getText().toString().length());//将光标移至文字末尾
                }
            }
        });

    }
}
