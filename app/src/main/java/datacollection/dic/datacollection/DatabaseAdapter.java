package datacollection.dic.datacollection;


import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseAdapter {

    DatabaseHelper helper;

    public DatabaseAdapter(Context context) {
        helper = new DatabaseHelper(context);
    }

    public SQLiteDatabase getReadableDatabase(){
        return helper.getReadableDatabase();
    }



    public String getTableName(){
        return DatabaseHelper.TABLE_NAME;
    }

    public long insertData(String mode, String file_name, String start_time, String end_time, long duration) {

        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.MODE, mode);
        contentValues.put(DatabaseHelper.FILE_NAME, file_name);
        contentValues.put(DatabaseHelper.START_TIME, start_time);
        contentValues.put(DatabaseHelper.END_TIME, end_time);
        contentValues.put(DatabaseHelper.DURATION, duration);
        contentValues.put(DatabaseHelper.SAMPLING_RATE,16000);
        contentValues.put(DatabaseHelper.BPP,16);
        contentValues.put(DatabaseHelper.CHANNELS,1);
        contentValues.put(DatabaseHelper.HUMAN_PERCEPTION_OF_FLOW,"");//TODO
        contentValues.put(DatabaseHelper.AIR,"");//TODO
        contentValues.put(DatabaseHelper.ENVIRONMENTAL_CONDITION,"");//TODO
        contentValues.put(DatabaseHelper.FILE_EXTENSION,"wav");

        long id = db.insert(DatabaseHelper.TABLE_NAME, null, contentValues);
        return id;
    }

    static class DatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "DataCollectionDB";
        private static final String TABLE_NAME = "RootTable";
        private static final int DATABASE_VERSION = 9;
        private static final String UID = "_id";
        private static final String FILE_NAME = "FileName";
        private static final String START_TIME = "StartTime";
        private static final String END_TIME = "EndTime";
        private static final String DURATION = "DurationInMillisec";
        private static final String SAMPLING_RATE = "SamplingRate";
        private static final String BPP = "RecordingBitRate";
        private static final String CHANNELS = "ChannelConfiguration";
        private static final String HUMAN_PERCEPTION_OF_FLOW = "PerceptionOfTraffic";
        private static final String AIR = "Air";
        private static final String ENVIRONMENTAL_CONDITION= "Climate";
        private static final String REMARKS ="Remarks";
        private static final String MODE = "Mode";
        private static final String FILE_EXTENSION = "FileType";
        private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + MODE + " VARCHAR(255), " + FILE_NAME + " VARCHAR(255), " + START_TIME + " VARCHAR(255), " + END_TIME + " VARCHAR(255), " + DURATION + " INTEGER, " + SAMPLING_RATE +" INTEGER, "+ BPP +" INTEGER, "+ CHANNELS+" INTEGER, "+ HUMAN_PERCEPTION_OF_FLOW +" VARCHAR(255), "+ AIR +" VARCHAR(255), "+ ENVIRONMENTAL_CONDITION +" VARCHAR(255), "+ REMARKS +" VARCHAR(255), "+FILE_EXTENSION+" VARCHAR(255)"+ ");";

        private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        private Context context;

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
            Message.message(context, "Constructor Called");
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_TABLE);
                Message.message(context, "onCreate Called");
            } catch (SQLException e) {
                Message.message(context, "" + e);
                Log.d("Error", e.toString());
            }

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                Message.message(context, "onUpgrade Called");
                db.execSQL(DROP_TABLE);
                onCreate(db);

            } catch (SQLException e) {
                Message.message(context, "" + e);
                Log.d("Error", e.toString());
            }

        }
    }

}