package be470.upenn.edu.smartalarmclock;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class MenuBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_base);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.help:
                return true;
            case R.id.settings:
                Intent goToSettings = new Intent(this, Settings.class);
                startActivity(goToSettings);
                return true;
            case R.id.cancel_alarms:
                //Intent goToSettings = new Intent(this, Settings.class);
                //startActivity(goToSettings);
                return true;
            case R.id.current_alarms:
                Intent goToAlarms = new Intent(this, CurrentAlarms.class);
                startActivity(goToAlarms);

                return true;

            case R.id.home:
                Intent goHome = new Intent(this, HomeScreen.class);
                startActivity(goHome);
                return true;
            case R.id.bluetooth:
                //Intent goToBT = new Intent(this, DeviceControlActivity.class);
                //startActivity(goToBT);
                //PublicVars.sleepMode=false;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
