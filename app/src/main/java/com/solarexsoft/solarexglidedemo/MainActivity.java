package com.solarexsoft.solarexglidedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private String[] urls = {
            "http://5b0988e595225.cdn.sohucs.com/q_70,c_zoom,w_640/images/20171110/10af74d2206c4cd1adca4586932e7e89.jpeg",
            "http://image1.xyzs.com/upload/dd/06/114553/20130831/137795749842797_0.jpg",
            "http://i0.hdslb.com/bfs/article/cf9ab4cb114695bfacb24b5558256d037ebc7b70.jpg"
        };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


}
