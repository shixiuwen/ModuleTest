package shixia.moduletest.module_tree;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.List;

import shixia.moduletest.R;

/**
 * Created by ShiXiuwen on 2017/4/12.
 * Description:
 */

public class RecyclerTreeActivity extends AppCompatActivity {

    private RecyclerView rvTree;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tree_recycler);

        Gson gson = new Gson();
        CarBean carBean = gson.fromJson(json, CarBean.class);

        List data = carBean.getData();

        rvTree = (RecyclerView) findViewById(R.id.rv_tree);

        rvTree.setLayoutManager(new LinearLayoutManager(this));
        rvTree.setAdapter(new RecyclerAdapter(this,data));
    }

    private String json = "{\"status\":\"200\",\n" +
            " \"info\":\"successful\",\n" +
            " \"data\":[{\"brand\":\"LEVEL_01_A\"," +
            "\"child01\":[{\"child01Brand\":\"LEVEL_02_A\"," +
            "\"child02\":[\"LEVEL_03_A\",\"LEVEL_03_B\"," +
            "\"LEVEL_03_C\",\"LEVEL_03_D\"]}," +
            "{\"child01Brand\":\"LEVEL_02_B\"," +
            "\"child02\":[\"LEVEL_03_AA\"," +
            "\"LEVEL_03_BB\",\"LEVEL_03_CC\"," +
            "\"LEVEL_03_DD\"]}]},{\"brand\":" +
            "\"LEVEL_01_B\",\"child01\":[{\"child01Brand\":" +
            "\"LEVEL_02_AA\",\"child02\":[\"LEVEL_033_A\"," +
            "\"LEVEL_033_B\",\"LEVEL_033_C\",\"LEVEL_033_D\"]}," +
            "{\"child01Brand\":\"LEVEL_02_BB\",\"child02\":" +
            "[\"LEVEL_033_AA\",\"LEVEL_033_BB\",\"LEVEL_033_CC\"," +
            "\"LEVEL_033_DD\"]}]},{\"brand\":\"LEVEL_01_C\"," +
            "\"child01\":[{\"child01Brand\":\"LEVEL_02_AAA\"," +
            "\"child02\":[\"LEVEL_0333_A\",\"LEVEL_0333_B\"," +
            "\"LEVEL_0333_C\",\"LEVEL_0333_D\"]}," +
            "{\"child01Brand\":\"LEVEL_02_BBB\"," +
            "\"child02\":[\"LEVEL_0333_AA\"," +
            "\"LEVEL_0333_BB\",\"LEVEL_0333_CC\"," +
            "\"LEVEL_0333_DD\"]}]}]}";
}
