package edu.sjsu.kyle.todobucketlist;

import android.content.res.Configuration;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Configuration config = getResources().getConfiguration();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(config.orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            //fragmentTransaction.replace(R.id.landscape_fragment, workoutDetailsFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
        }
        else
        {
            //fragmentTransaction.replace(R.id.fragment_container, recordWorkoutFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
        }
    }
}
