package datacollection.dic.datacollection;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    TextView pedestrian;
    TextView automobile;
    TextView twoWheeler;
    TextView car;
    TextView bus;

    DatabaseAdapter databasehelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databasehelper = new DatabaseAdapter(this);
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
                exportDBasCSV();
                return true;
            case R.id.settings:
                Message.message(this,"Settings Clicked");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void exportDBasCSV(){
        SQLiteDatabase db = databasehelper.getReadableDatabase();
        Cursor c ;
        long systemMillis = System.currentTimeMillis();
        Date date = new Date(systemMillis);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss,SSS", Locale.ENGLISH);

        try{
            c = db.rawQuery("SELECT * FROM "+databasehelper.getTableName(),null);
            int rowcount ;
            int colcount ;
            File filepath = Environment.getExternalStorageDirectory();
            String filename = sdf.format(date)+".csv";
            File saveFile  = new File(filepath,"DataCollectionApp/CSV"+filename);

            /*if(!saveFile.exists()){
                boolean x = saveFile.mkdirs();
                if (x){
                    Message.message(getBaseContext(),"CSV Directory Created");
                }
            }*/
            FileWriter fw = new FileWriter(saveFile);

            BufferedWriter bw = new BufferedWriter(fw);
            rowcount = c.getCount();
            colcount = c.getColumnCount();
            if(rowcount > 0){
                c.moveToFirst();
                for(int i = 0;i<colcount;i++){
                    if(i!=colcount-1){
                        bw.write(c.getColumnName(i)+",");
                    }else{
                        bw.write(c.getColumnName(i));
                    }
                }
                bw.newLine();
                for(int i = 0;i<rowcount;i++){
                    c.moveToPosition(i);

                    for (int j=0;j<colcount;j++){
                        if (j!=colcount-1)
                            bw.write(c.getString(j)+",");
                        else
                            bw.write(c.getString(j));
                    }
                    bw.newLine();
                }
                bw.flush();
                c.close();
                Message.message(getBaseContext(),"Exported Successfully");
            }
        }catch(Exception ex){
            if(db.isOpen()){
                db.close();
                Message.message(getBaseContext(),ex.getMessage());
            }
        }finally{
            if(db.isOpen()) {
                db.close();
            }
        }
    }
}

