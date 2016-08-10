package datacollection.dic.datacollection;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    TextView pedestrian;
    TextView automobile;
    TextView twoWheeler;
    TextView car;
    TextView bus;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Button Handlers
        pedestrian = (TextView)findViewById(R.id.ped_textview);
        automobile = (TextView)findViewById(R.id.automobile_textview);
        twoWheeler = (TextView)findViewById(R.id.twowheeler_textview);
        car = (TextView)findViewById(R.id.car_textview);
        bus = (TextView)findViewById(R.id.bus_textview);

        //Button Click Listeners
        pedestrian.setOnClickListener(this);
        automobile.setOnClickListener(this);
        twoWheeler.setOnClickListener(this);
        car.setOnClickListener(this);
        bus.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(MainActivity.this, RecordingActivity.class);
        switch (view.getId()){
            case R.id.ped_textview:
                intent.putExtra("KEY", "Pedestrian");
                startActivity(intent);
                break;
            case R.id.automobile_textview:
                intent.putExtra("KEY", "Automobile");
                startActivity(intent);
                break;
            case R.id.twowheeler_textview:
                intent.putExtra("KEY", "TwoWheeler");
                startActivity(intent);
                break;
            case R.id.car_textview:
                intent.putExtra("KEY", "Car");
                startActivity(intent);
                break;
            case R.id.bus_textview:
                intent.putExtra("KEY", "Bus");
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.export_csv:
                Message.message(this,"Exported As CSV");
                return true;
            case R.id.settings:
                Message.message(this,"Settings Clicked");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

