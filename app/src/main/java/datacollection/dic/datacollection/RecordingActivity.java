package datacollection.dic.datacollection;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class RecordingActivity extends AppCompatActivity {
    Context mContext;
    Recording mRecord;
    //UI Elements
    Button mStartButton;
    Button mStopButton;

    Bundle extras;
    String mode;

    String fileName ,startTime,stopTime,startMillis,stopMillis;
    long start,stop;
    long duration;

    private String[] dataFromStartRecording;
    private String[] dataFromStopRecording;

    private int seconds = 0;
    private boolean running;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
        mContext = this;
        //Create  Recorder Instance
        mRecord = new Recording(mContext);

        // Button Handlers
        mStartButton = (Button)findViewById(R.id.StartButton);
        mStopButton = (Button)findViewById(R.id.StopButton);

        extras = getIntent().getExtras();
        mode = extras.getString("KEY");

        mStopButton.setEnabled(false);

        // Button Click Listeners
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                running = true;
                dataFromStartRecording = mRecord.startRecording();
                mStartButton.setEnabled(false);
                mStopButton.setEnabled(true);
                fileName = dataFromStartRecording[2];
                startTime = dataFromStartRecording[0];
                startMillis  =dataFromStartRecording[1];
                start = Long.parseLong(startMillis);
                seconds = 0;
                runTimer();


            }
        });

        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataFromStopRecording = mRecord.stopRecording();
                mStartButton.setEnabled(true);
                mStopButton.setEnabled(false);
                stopTime = dataFromStopRecording[0];
                stopMillis = dataFromStopRecording[1];
                stop = Long.parseLong(stopMillis);
                duration = stop - start;
                running = false;
                Message.message(mContext,mode+" "+startTime+" "+stopTime+" "+fileName+" "+duration);

            }
        });


    }


    public void runTimer() {
        final TextView timeView = (TextView)findViewById(R.id.timer_textview);
        final Handler handler = new Handler();
        final Locale locale = Locale.ENGLISH;
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds/3600;
                int minutes = (seconds%3600)/60;
                int secs = seconds%60;
                String time = String.format(locale,"%02d:%02d.%02d", hours, minutes, secs);
                timeView.setText(time);
                if(running) {
                    seconds++;
                }
                handler.postDelayed(this, 10);
            }
        });
    }



}

