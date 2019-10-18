package com.example.pump;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Home extends AppCompatActivity {

    private ViewPager Pager;
    private SlideAdapter myadapter;
    private LinearLayout status;
    private TextView txtViewPing;
    private ImageView imageViewSignal;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        SQLManager sqlManager = new SQLManager(this);
        Cursor path = sqlManager.getPath();
        if (path.getCount() < 1) {
            //------------------------------------------------------------------
            Intent Controller = new Intent(this,Add_Controller.class);
            startActivity(Controller);
            //------------------------------------------------------------------
        }
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        Pager = findViewById(R.id.viewPager); //Creates Fragment
        txtViewPing = findViewById(R.id.TxtViewPing);
        imageViewSignal = findViewById(R.id.ImageViewSignal);
        imageViewSignal.setBackgroundResource(R.drawable.ic_action_no_signal);
        myadapter = new SlideAdapter(this);
        Pager.setAdapter(myadapter);
        Pager.setCurrentItem(0);

        bottomNavigationView.setSelectedItemId(R.id.scheduleNav);


        myadapter.setCustomObjectListener(new SlideAdapter.MyCustomObjectListener() {
            @Override
            public void onObjectReady(String title) {
                int time = Integer.parseInt(title);
                if (time == 0) {
                    imageViewSignal.setBackgroundResource(R.drawable.ic_action_no_signal);
                    txtViewPing.setText("No Connection!!!");
                } else if (time < 250) {
                    imageViewSignal.setBackgroundResource(R.drawable.ic_action_4_bar);
                    txtViewPing.setText(title + "ms");

                } else if (time < 500) {
                    imageViewSignal.setBackgroundResource(R.drawable.ic_action_3_bar);
                    txtViewPing.setText(title + "ms");

                } else if (time < 1000) {
                    imageViewSignal.setBackgroundResource(R.drawable.ic_action_2_bar);
                    txtViewPing.setText(title + "ms");

                } else {
                    imageViewSignal.setBackgroundResource(R.drawable.ic_action_1_bar);
                    txtViewPing.setText(title + "ms");

                }


            }
        });

        Pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == 0) {
                    bottomNavigationView.setSelectedItemId(R.id.scheduleNav);
                } else if (i == 1) {
                    bottomNavigationView.setSelectedItemId(R.id.manualNav);
                } else if (i == 2) {
                    bottomNavigationView.setSelectedItemId(R.id.settingNav);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.scheduleNav:
                                Pager.setCurrentItem(0);
                                break;
                            case R.id.manualNav:
                                Pager.setCurrentItem(1);
                                break;
                            case R.id.settingNav:
                                Pager.setCurrentItem(2);
                                break;


                        }


                        return true;
                    }
                });


    }
}
