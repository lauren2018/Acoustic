package parsley.acoustic.code;

import java.util.ArrayList;
import java.util.Vector;

import parsley.acoustic.acmath.Complex;

public class LoRaMod {
    /**parameters*/
    private int spreadingFactor=8;
    private int syncWord=18;
    private final int NUM_PREAMBLE_CHIRPS = 8;
    private final int SYNC_WORD_0 = 3;
    private final int SYNC_WORD_1 = 4;


    /**ports*/
    private ArrayList<Integer> in = new ArrayList<>();
    private ArrayList<Float> out = new ArrayList<>();

    /**View*/

    /**Other class members*/
    private int fft_size;
    private final float init_phase_up = -(float)Math.PI;
    private float accumulator_up = 0;
    private float phase_up = init_phase_up;
    //private float phase_down = init_phase_down;
    private ArrayList<Float> down_chirp = new ArrayList<>();
    private ArrayList<Float> up_chirp = new ArrayList<>();

    public LoRaMod(ArrayList<Short> data_in){
        fft_size = (1 << spreadingFactor);
        for(int i = 0; i<2*fft_size;i++){
            accumulator_up += phase_up;
            float angle = (float)(accumulator_up / Math.PI * 180);
            down_chirp.add((float)Math.sin(-accumulator_up));
            up_chirp.add((float)Math.sin(accumulator_up));
            phase_up +=(2*Math.PI)/fft_size;
        }
        for(Short b: data_in){
            in.add((int)b);
        }
        modulate();
    }

    private void modulate(){
        /**Prepend zero-mag*/
        for(int i = 0; i < 4*fft_size;i++){
            out.add((float)0.0);
        }
        /**preamble*/
        for(int i = 0; i < NUM_PREAMBLE_CHIRPS*fft_size;i++){
            out.add(up_chirp.get(i % fft_size));
        }
        /**sync word 0*/
        for(int i = 0; i < fft_size; i++){
            out.add(up_chirp.get(
                    (8*((syncWord & 0xF0) >> 4) + i) % fft_size
            ));
        }
        /**sync word 1*/
        for(int i = 0; i < fft_size; i++){
            out.add(up_chirp.get(
                    (8*(syncWord & 0xF0) + i) % fft_size
            ));
        }
        /**SFD DownChirps*/
        for (int i = 0; i < (2*fft_size+fft_size/4); i++)
        {
            out.add(down_chirp.get(i % fft_size));
        }
        /**Payload*/
        for (int i = 0; i < in.size(); i++)
        {
            for (int j = 0; j < fft_size; j++){
                out.add(up_chirp.get((in.get(i)+(fft_size/4)+j)%fft_size));
            }
        }
        /**tail*/
        for(int i = 0; i < 4*fft_size+128;i++){
            out.add((float)0.0);
        }

    }

    public ArrayList<Float> getOutput(){
        return out;
    }
}
