package parsley.acoustic;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import static android.media.AudioTrack.MODE_STREAM;


public class MainActivity extends AppCompatActivity {
    private Button key ;
    private Boolean isOn = false;

    Transceiver tx = new Transceiver();
    Thread tx_thread = new Thread(tx);
    Receiver rx = new Receiver();
    Thread rx_thread = new Thread(rx);
    int flag = 0;//0: Tx 1: Rx

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //InputStream inputStream = getResources().openRawResource(R.raw.data);

        key = (Button) findViewById(R.id.key);
        if (flag == 0) {
            key.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isOn) {
                        key.setBackgroundColor(Color.GREEN);
                        isOn = !isOn;
                        tx.setStart();
                        tx_thread = null;
                        tx_thread = new Thread(tx);
                        tx_thread.start();

                    } else {
                        isOn = !isOn;
                        key.setBackgroundColor(Color.GRAY);
                        tx.setStop();
                        try {
                            tx_thread.interrupt();
                        } catch (Exception e) {
                            tx_thread = null;
                        }
                    }
                }
            });
        }
        else{
            key.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isOn) {
                        key.setBackgroundColor(Color.GREEN);
                        isOn = !isOn;
                        rx.setStart();
                        rx_thread = null;
                        rx_thread = new Thread(rx);
                        rx_thread.start();

                    } else {
                        isOn = !isOn;
                        key.setBackgroundColor(Color.GRAY);
                        rx.setStop();
                        try {
                            rx_thread.interrupt();
                        } catch (Exception e) {
                            rx_thread = null;
                        }
                    }
                }
            });
        }

    }

    @TargetApi(21)
    public void send(){
        int sample_rate = 44100;
        AudioTrack audio = new AudioTrack(AudioManager.STREAM_MUSIC,sample_rate,AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_FLOAT,1000,MODE_STREAM);

    }




}
