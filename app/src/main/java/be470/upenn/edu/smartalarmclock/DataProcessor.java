package be470.upenn.edu.smartalarmclock;

/**
 * Created by Sam on 4/28/17.
 */
import java.util.HashMap;
import java.util.Collections;

public class DataProcessor {
    private HashMap<Integer,Integer> heartRates;

    public DataProcessor() {
        heartRates = new HashMap<Integer,Integer>();
    }

    public void addToList(int time, int HR) { heartRates.put(time,HR); }

    /**
     *
     * @return returns whether a user is in REM sleep based on heart rate
     */
    public boolean isREM () {
        double avg = 0;
        HashMap<Integer,Integer> heartRatesCopy = heartRates;
        for(int i = 0; i < 10; i++) {
            avg += heartRatesCopy.get(Collections.max(heartRatesCopy.keySet()));
            heartRatesCopy.remove(Collections.max(heartRatesCopy.keySet()));
        }
        avg /= 10.0;
        return  (avg >= 82);
    }

}
