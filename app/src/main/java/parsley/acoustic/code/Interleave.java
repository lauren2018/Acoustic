package parsley.acoustic.code;

import android.util.Log;

import java.util.ArrayList;

/**rdd+4 -> number of bits of each input codeword
 * ppm -> number of bits of each output symbol
 * */

public class Interleave {
    private final int INTERLEAVE_BLOCK_SIZE = 8;
    private int ppm = 6;
    private int rdd = 4;
    private boolean ldr = true; //low_data_rate


    private ArrayList<Byte> in = new ArrayList<>();
    private ArrayList<Short> out = new ArrayList<>();

    public Interleave(ArrayList<Byte> data_in){
        for(Byte b:data_in){
            in.add(b);
        }
        work();
    }

    public void work(){
        int bit_offset = 0;
        int bit_idx = 0;
        byte [] block = new byte [INTERLEAVE_BLOCK_SIZE];
        byte [] reordered = new byte [INTERLEAVE_BLOCK_SIZE];

        for(int block_count = 0; block_count < in.size() / ppm;block_count++) {
            bit_idx = 0;
            bit_offset = 0;
            block = new byte[INTERLEAVE_BLOCK_SIZE];
            if (ppm == 6) {
                reordered[4] = in.get(0 + block_count * ppm);
                reordered[5] = in.get(1 + block_count * ppm);
                reordered[2] = in.get(2 + block_count * ppm);
                reordered[3] = in.get(3 + block_count * ppm);
                reordered[0] = in.get(4 + block_count * ppm);
                reordered[1] = in.get(5 + block_count * ppm);
            } else if (ppm == 8) {
                reordered[0] = in.get(0 + block_count * ppm);
                reordered[7] = in.get(1 + block_count * ppm);
                reordered[2] = in.get(2 + block_count * ppm);
                reordered[1] = in.get(3 + block_count * ppm);
                reordered[4] = in.get(4 + block_count * ppm);
                reordered[3] = in.get(5 + block_count * ppm);
                reordered[6] = in.get(6 + block_count * ppm);
                reordered[5] = in.get(7 + block_count * ppm);
            }
            int x = 1;
            // Iterate through each bit in the interleaver block
            for (int bitcount = 0; bitcount < ppm * (4 + rdd); bitcount++) {

                if (((int) reordered[(bitcount / (4 + rdd))] & (0x1 << (bitcount % (4 + rdd)))) > 0) {
                    block[bitcount % (4 + rdd)] = (byte) (block[bitcount % (4 + rdd)] | ((0x1 << (ppm - 1)) >> ((bit_idx + bit_offset) % ppm)));
                }

                // bit idx walks through diagonal interleaving pattern
                if (bitcount % (4 + rdd) == (4 + rdd - 1)) {
                    bit_idx = 0;
                    bit_offset++;
                } else {
                    bit_idx++;
                }
            }
            x = 1;
            for (int block_idx = 0; block_idx < (4 + rdd); block_idx++) {
                out.add((short) block[block_idx]);
            }
        }
            int x = 1;
            // Swap MSBs of each symbol within buffer (one of LoRa's quirks). Actually, it just swaps (ppm-1)th and (ppm-2)th bit.
            for (int symbol_idx = 0; symbol_idx < out.size(); symbol_idx++)
            {
                short newv = (short)( (out.get(symbol_idx) &  (0x1 << (ppm-1))) >> 1 |
                        (out.get(symbol_idx) &  (0x1 << (ppm-2))) << 1 |
                        (out.get(symbol_idx) & ((0x1 << (ppm-2)) - 1))
                );
                out.set(symbol_idx, newv);
            }
//if low_data_rate
            int ldr_limit;
            if(ldr){
                ldr_limit = out.size();
            }else{
                ldr_limit = 8;
            }
            for (int i = 0; i < out.size() && i < ldr_limit; i++)
            {
                out.set(i,(short)(out.get(i) << 2));
            }
    }



    public ArrayList<Short> getOutput(){
        return out;
    }

//    private void whiten(){
//
//    }
//
//    private void to_gray() {
//        for (int i = 0; i < out.size(); i++) {
//            out.set(i,(short)((out.get(i) >> 1) ^ out.get(i)));
//        }
//    }
//
//    void from_gray() {
//        for (int i = 0; i < out.size(); i++)
//        {
//            out.set(i,(short)(out.get(i) ^ (out.get(i) >> 16)));
//            out.set(i,(short)(out.get(i) ^ (out.get(i) >> 8)));
//            out.set(i,(short)(out.get(i) ^ (out.get(i) >> 4)));
//            out.set(i,(short)(out.get(i) ^ (out.get(i) >> 2)));
//            out.set(i,(short)(out.get(i) ^ (out.get(i) >> 1)));
//        }
//    }
}
