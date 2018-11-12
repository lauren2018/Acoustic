package parsley.acoustic;

import android.annotation.TargetApi;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class Receiver implements Runnable{
    private FileWriter fw;
    private BufferedWriter bw;
    private Boolean isOn = false;

    int sample_rate = 44100;
    //int min_buffer_size = AudioRecord.getMinBufferSize(sample_rate,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);
    int bufferSize = 32000;
    AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sample_rate,
            AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT,bufferSize);

    @TargetApi(23)
    public void receive(){
        try{
            setOutputFile();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        short [] buffer = new short[bufferSize / 2];
        recorder.startRecording();
        int i = 0;
        while(isOn){
            int returnValue = recorder.read(buffer,0,buffer.length,AudioRecord.READ_BLOCKING);

            if(returnValue > 0) {
                for (short data : buffer) {
                    try {
                        bw.write(Short.toString(data) + " ");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                try {
                    bw.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else{
                if(returnValue == -1){
                    int xa = 1;
                }
                else if(returnValue == -2){
                    int xa = 2;
                }
                else if(returnValue == -3){
                    int xa = 3;
                }else if(returnValue == -6){
                    int xa = 4;
                }else if(returnValue == 0){
                    int xa = 5;

                }

            }
        }
    }

    private void setOutputFile() throws Exception{
        String path = Environment.getExternalStorageDirectory()+"/"+"rx_audio.txt";
        File file = new File(path);
        file.createNewFile();
        fw = new FileWriter(path, false);
        try{
            bw = new BufferedWriter(fw);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        receive();
    }

    public void setStart(){
        isOn = true;
    }

    public void setStop(){
        isOn = false;
        recorder.stop();
    }
}
