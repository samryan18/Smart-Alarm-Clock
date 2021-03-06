package be470.upenn.edu.smartalarmclock;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.provider.AlarmClock;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.widget.Spinner; // for spinner
import android.widget.ArrayAdapter; // for spinner
import android.widget.AdapterView; // for spinner
import android.net.Uri; // for going to our website
import android.widget.TextView;
import android.widget.Toast;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

public class HomeScreen extends MenuBaseActivity {
    // alarm clock resources
    // https://developer.android.com/reference/android/provider/AlarmClock.html
    private Spinner hour_spinner;
    private Spinner minute_spinner;

    private int firstHour;
    private int firstMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);
        hour_spinner = setHourSpinner();
        minute_spinner = setMinuteSpinner();
        setWelcomeText();
    }

    private void setWelcomeText() {
        TextView lView = (TextView)findViewById(R.id.welcome); // initialize lView
        if (PublicVars.sleepMode) lView.setText("Alarm is set! HR is being monitored! \nHappy sleeping!");
        else lView.setText("Welcome to the smart alarm clock app!\n Click below for more info!");
    }



    /**************************************************************************
     ***************************** Util Methods *******************************
     **************************************************************************/

    /**
     * Sets an alarm at specified time
     * @param message   -message to display with alarm
     * @param hour      -hour of day
     * @param minutes   -minutes after hours
     *
     * see: https://developer.android.com/guide/components/intents-common.html
     *   for more details
     */
    public void createAlarm(String message, int hour, int minutes) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_MESSAGE, message)
                .putExtra(AlarmClock.EXTRA_HOUR, hour)
                .putExtra(AlarmClock.EXTRA_MINUTES, minutes);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);


        }
    }

    /**************************************************************************
     ************************** onClick Methods *******************************
     **************************************************************************/
    /*
     * Called when user hits set time button
     * Gets time from spinner and calls createAlarm() with those times
     * TODO: THIS
     */
    public void setAlarm(View v) {
        // Toast.makeText(this, "do deez", Toast.LENGTH_LONG).show();
        createAlarm("Cardialarm!", firstHour, firstMinute);
        if (firstMinute>=10) PublicVars.minutes = ""+firstMinute;
        else PublicVars.minutes = "0"+firstMinute;
        PublicVars.hour = firstHour;
        PublicVars.sleepMode = true;

    }

    /* Goes to settings page */
    public void goToSettings(View v) {
        Intent goToSettings = new Intent(this, Settings.class);
        startActivity(goToSettings);
    }

    /* Sends User to Our Site! */
    public void goToWeb(View v) {
        Uri url = Uri.parse("http://penngineering.weebly.com");
        Intent launchInternet = new Intent(Intent.ACTION_VIEW, url);
        startActivity(launchInternet);
    }



    /**************************************************************************
     ************************* Spinner Methods/Class **************************
     **************************************************************************/

    /*
     * Sets up the spinner for selecting alarm time
     * TODO: make legit spinners with hours and minutes options
     */
    private Spinner setHourSpinner() {
        //From Spinner Android Documentation
        Spinner hour_spinner = (Spinner) findViewById(R.id.hour_spinner);
        hour_spinner.getBackground().setColorFilter(0xFFFFFF, PorterDuff.Mode.SRC_ATOP);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.hour_choices, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        hour_spinner.setAdapter(adapter);
        hour_spinner.setOnItemSelectedListener(new SpinnerActivity());
        return hour_spinner;
    }

    /*
    * Sets up the spinner for selecting alarm time
    * TODO: make legit spinners with hours and minutes options
    */
    private Spinner setMinuteSpinner() {
        //From Spinner Android Documentation
        Spinner minute_spinner = (Spinner) findViewById(R.id.minute_spinner);
        minute_spinner.getBackground().setColorFilter(0xFFFFFF, PorterDuff.Mode.SRC_ATOP);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.minute_choices, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        minute_spinner.setAdapter(adapter);
        minute_spinner.setOnItemSelectedListener(new SpinnerActivity());
        return minute_spinner;
    }

    public class SpinnerActivity implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
            String selection = parent.getItemAtPosition(position).toString();
            if (selection.length() <= 2) /* if selection is of form 1 or 11 and not :00 */ {
                firstHour = Integer.parseInt(selection);
                //Toast.makeText(getParent(), firstHour, Toast.LENGTH_LONG).show();
            }
            else {
                firstMinute = Integer.parseInt(parent.getItemAtPosition(position).toString().substring(1));
                //Toast.makeText(getParent(), firstMinute, Toast.LENGTH_LONG).show();
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }
}