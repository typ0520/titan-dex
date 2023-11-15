package com.baidu.titan.dex.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                test("s", 2, 3);
            }
        });
    }

    private void test(String s1, int i2, long l3) {
        error();
    }

    private void error() {
        throw new RuntimeException("TEST ERROR");
    }
}