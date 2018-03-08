package com.nan.customview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.nan.customview.view.ToggleView;

public class MainActivity extends AppCompatActivity {
    ToggleView mToggleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToggleView = findViewById(R.id.tv_toggle);

        //采用自定义属性替代
//        mToggleView.setToggleBackgroundResource(R.drawable.switch_background);
//        mToggleView.setToggleSlideResource(R.drawable.slide_button);
//        mToggleView.setToggle(true);
        mToggleView.setOnToggleChangeListener(new ToggleView.OnToggleChangeListener() {
            @Override
            public void onToggleChange(boolean open) {
                if (open) {
                    Toast.makeText(MainActivity.this, "toogle open", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "toogle close", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
