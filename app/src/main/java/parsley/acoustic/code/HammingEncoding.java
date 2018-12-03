package parsley.acoustic.code;

import java.util.ArrayList;

public class HammingEncoding {
    //private int rdd;
    private final int HAMMING_P1_BITMASK=0X0D;
    private final int HAMMING_P2_BITMASK=0X0B;
    private final int HAMMING_P4_BITMASK=0X07;
    private final int HAMMING_P8_BITMASK=0XFF;


    private ArrayList<Byte> in = new ArrayList<>();
    private ArrayList<Byte> out = new ArrayList<>();

    public HammingEncoding(ArrayList<Byte> data_in){
        for(Byte b:data_in){
            in.add(b);
        }
        work();
    }

    public void work(){
        byte p1,p2,p4,p8;
        //byte mask;
        for(int i = 0; i < in.size(); i++){
            byte b = in.get(i);
            p1 = parity(b,(byte)HAMMING_P1_BITMASK);
            p2 = parity(b,(byte)HAMMING_P2_BITMASK);
            p4 = parity(b,(byte)HAMMING_P4_BITMASK);
            p8 = parity((byte)((int)b | (int)p1 << 7 | (int)p2 << 6 | (int) p4 << 4),
                    (byte)HAMMING_P8_BITMASK);
            out.add(
                    (byte)((int) p1 << 7| (int) p2 << 6 | (int) p4 << 4 | (int) p8 << 5| (int)b & 0xF)
            );

        }
    }

    private byte parity(byte c, byte bitmask){
        int c_0 = c;
        int bitmask_0 = bitmask;
        int parity = 0;
        int shiftme = c & bitmask;
        for(int i = 0; i < 8; i++){
            if((shiftme & 1) > 0) parity++;
            shiftme = shiftme >> 1;
        }
        return (byte)(parity % 2);
    }

    public ArrayList<Byte> getOutput(){
        return out;
    }
}
