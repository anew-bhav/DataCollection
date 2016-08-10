package datacollection.dic.datacollection;


import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Recording {

    private static final int RECORDER_BPP = 16;
    private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    private static final String AUDIO_RECORDER_FOLDER = "Recordings";
    private static final String APP_DATA_FOLDER = "DataCollectionApp";
    private static final String FILE_PREFIX  = "Rec";
    private static final String PREFIX_SEPARATOR = "_";
    private static final String FILE_SEPARATOR = "/";
    private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
    private static final int[] RECORDER_SAMPLERATE = {8000, 16000, 44100, 48000};
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    boolean isRecording = false;
    Context mContext;
    private AudioRecord mRecorder = null;
    private Thread recordingThread = null;
    private String [] timestamp;
    public Recording(Context context) {
        mContext = context;
    }
    //Get the  minimum buffer required to store PCM bits

    public int getBufferSize(int i){
        int bufferSize;
         bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE[i],
                 RECORDER_CHANNELS,
                 RECORDER_AUDIO_ENCODING)*3;
        return bufferSize;
    }

    public String[] startRecording(){
        String fileName ;
        String[] itemsToBeReturned = new String[3];
        mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE[1],
                RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING,
                getBufferSize(1));
        int i = mRecorder.getState();


        if(i==1){
            mRecorder.startRecording();
        }

        isRecording = true;

        recordingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                writeAudioDataToFile();
            }
        },"Audio Recorder Thread");

        recordingThread.start();
        timestamp = getTimeStamp();// index 0 contains timestamp,index 1 contains currentTimeMillis
        Message.message(mContext,"Recording Started");
        fileName = getFilename(false);
        itemsToBeReturned[0]=timestamp[0];
        itemsToBeReturned[1]=timestamp[1];
        itemsToBeReturned[2]=fileName;
        return itemsToBeReturned;
    }

    private void writeAudioDataToFile(){
        byte data[] = new byte[getBufferSize(1)];//TODO bufferSize Used so samplerate array to be taken care of here
        String filename = getTempFilename();
        FileOutputStream os = null;

        try{
            os = new FileOutputStream(filename);
        }catch(FileNotFoundException e){
           Message.message(mContext,"Temporary file cannot be created");
        }

        int read;
        if (os !=null){
            while (isRecording){
                read = mRecorder.read(data,0,getBufferSize(1));
                if(AudioRecord.ERROR_INVALID_OPERATION!=read){
                    try {
                        os.write(data);
                    } catch (IOException e) {
                       Message.message(mContext,"Cannot write to temp File");
                    }
                }
            }
            try {
                os.close();
            } catch (IOException e) {
                Message.message(mContext,"Some Error Occurred");
            }
        }
    }

    private String getFilename(boolean type){
        String filepath = Environment.getExternalStorageDirectory().getPath();
        String fileName = FILE_PREFIX + PREFIX_SEPARATOR + timestamp[0] + AUDIO_RECORDER_FILE_EXT_WAV;
        File file = new File(filepath,APP_DATA_FOLDER+FILE_SEPARATOR+AUDIO_RECORDER_FOLDER);

        if(!file.exists()){
            boolean x  = file.mkdirs();
            if(x){
               Message.message(mContext,"File created for the first time");
            }
        }
        if(type){
            return (file.getAbsolutePath()+FILE_SEPARATOR+fileName);//want timeStamp in place of millis
        }
        else{
            return fileName;
        }




    }

    public String[] stopRecording(){
        String[] timestamp;
        timestamp  = null;
        if (mRecorder!=null){
            isRecording = false;
            int i = mRecorder.getState();

            if (i==1){
                mRecorder.stop();
                timestamp = getTimeStamp();
            }
            mRecorder.release();
            mRecorder = null;
            recordingThread = null;
        }
        copyWaveFile(getTempFilename(),getFilename(true));
        deleteTempFile();
        return timestamp;
    }

    private String getTempFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            boolean y = file.mkdirs();
            if(y){
                Message.message(mContext,"Temp File created");
            }
        }

        File tempFile = new File(filepath, AUDIO_RECORDER_TEMP_FILE);

        if (tempFile.exists()){
            boolean isDeleted = tempFile.delete();
            if(isDeleted){
                Message.message(mContext,"TempFile Deleted Successfully");
            }
        }



        return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
    }

    private void deleteTempFile(){
        File file = new File(getTempFilename());
        boolean isDeleted = file.delete();
        if(isDeleted){
           Message.message(mContext,"TempFile Deleted Successfully");
        }
    }

    private void copyWaveFile(String inFilename, String outFilename) {
        FileInputStream in ;
        FileOutputStream out;
        int channels = 1;// TODO:
        long totalAudioLen ;
        long totalDataLen =  36;
        long longSampleRate = RECORDER_SAMPLERATE[1];

        long byteRate = (RECORDER_BPP * RECORDER_SAMPLERATE[1] * channels) / 8;

        byte[] data = new byte[getBufferSize(1)];//TODO related to buffer and sample rate

        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + totalDataLen;

            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, 1, byteRate);

            while (in.read(data) != -1) {
                out.write(data);
            }

            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void WriteWaveFileHeader(
            FileOutputStream out, long totalAudioLen,
            long totalDataLen, long longSampleRate, int channels,
            long byteRate) throws IOException {
        byte[] header = new byte[44];

        header[0] = 'R';  // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f';  // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1;  // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (channels * 16 / 8);  // block align
        header[33] = 0;
        header[34] = RECORDER_BPP;  // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        out.write(header, 0, 44);
    }

    private String[] getTimeStamp() {
        Locale locale;
        locale = Locale.ENGLISH;
        String[] itemsToBeReturned = new String[2];
        long millis = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_h-mm-ss_a", locale);
        final String timeStamp = sdf.format(millis);
        itemsToBeReturned[0] = timeStamp;
        itemsToBeReturned[1] = String.valueOf(millis);

        return itemsToBeReturned;
    }





}
