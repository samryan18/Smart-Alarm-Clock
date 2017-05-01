package be470.upenn.edu.smartalarmclock;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CurrentAlarms extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_alarms);


        /*Toast.makeText(this,
                "Alarm will go off at a selected time between " +PublicVars.hour+ ":" +PublicVars.minutes+" and "
                        +PublicVars.hour+":"+Integer.parseInt(PublicVars.minutes)+30 +"!",
                Toast.LENGTH_SHORT).show();
        Toast.makeText(this,
                "If your android loses contact with heartbeat device, alarm will go off at  "+PublicVars.hour+ ":" +PublicVars.minutes,
                Toast.LENGTH_SHORT).show();
        Toast.makeText(this,
                "Happy sleeping!",
                Toast.LENGTH_SHORT).show();*/

        TextView lView = (TextView)findViewById(R.id.print_stuff); // initialize lView
        lView.setText("Alarm will go off at a selected time between " +PublicVars.hour+ ":" +PublicVars.minutes+" and "
                +PublicVars.hour+":"+(Integer.parseInt(PublicVars.minutes)+30) +"!"
                +"\n\nIf your android loses contact with heartbeat device, the alarm will go off at  "
                +PublicVars.hour+ ":" +PublicVars.minutes + "\n\n\nHappy sleeping!"
        );

    }

    public void goBackHome(View v) {
        Intent goHome = new Intent(this, HomeScreen.class);
        startActivity(goHome);
    }
}
