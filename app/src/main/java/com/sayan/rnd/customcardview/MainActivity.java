package com.sayan.rnd.customcardview;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.sayan.rnd.customviews.PlanetaryView;

public class MainActivity extends AppCompatActivity {

    boolean isToggled = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final PlanetaryView planetaryView = findViewById(R.id.planetaryViewId);
        planetaryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isToggled){
                    planetaryView.setCenterCircleColor(Color.BLUE);
                    planetaryView.setCenterCircleRadius(80);
                    planetaryView.setOrbiterRadius(40);
                    planetaryView.setOrbiterRadius(30);
                    isToggled = false;
                }else {
                    planetaryView.setCenterCircleRadius(80);
                    planetaryView.setOrbiterRadius(40);
                    planetaryView.setCenterCircleColor(Color.RED);
                    planetaryView.setOrbiterRadius(45);
                    isToggled = true;
                }
            }
        });
    }
}
