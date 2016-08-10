package datacollection.dic.datacollection;
//This Class Facilitates the display of toasts

import android.content.Context;
import android.widget.Toast;

public class Message {
    public static void message(Context context, String message){
        Toast.makeText(context,message, Toast.LENGTH_LONG).show();
    }

}
