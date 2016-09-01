package xyz.hanks.numberanimation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import xyz.hanks.library.NumberView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void anim(View view) {
        if (view instanceof NumberView) {
            ((NumberView) view).play();
        }
    }
}
