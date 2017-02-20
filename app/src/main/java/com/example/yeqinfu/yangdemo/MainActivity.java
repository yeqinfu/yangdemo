package com.example.yeqinfu.yangdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    MoveView moveView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        moveView= (MoveView) findViewById(R.id.move_view);
        findViewById(R.id.btn_add).setOnClickListener(this);
        findViewById(R.id.btn_reduce).setOnClickListener(this);
        findViewById(R.id.btn_reset).setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.btn_add){
            moveView.setViewNumber(moveView.getViewNumber()+1);
        }else if (view.getId()==R.id.btn_reduce){
            moveView.setViewNumber(moveView.getViewNumber()-1);
        }else{
            moveView.resetView();
        }
    }
}
