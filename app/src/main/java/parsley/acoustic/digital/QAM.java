package parsley.acoustic.digital;

import java.util.ArrayList;
import parsley.acoustic.acmath.Complex;

public class QAM {
    private int bitNum;//2,4,8,16,64
    private ArrayList<Complex> out = new ArrayList<Complex>();


    private Complex _16_QAM[] = {new Complex(1,1), new Complex(3,1), new Complex(1,3), new Complex(3,3),
        new Complex(1,-1), new Complex(1,-3), new Complex(3,-1), new Complex(3,-3),
        new Complex(-1,1), new Complex(-1,3), new Complex(-3,1), new Complex(-3,3),
        new Complex(-1,-1), new Complex(-3,-1), new Complex(-1,-3), new Complex(-3, -3)
    };

    public QAM(int bitnum){
        bitNum = bitnum;
    }


    public ArrayList<Complex> work(ArrayList<Byte> in){
        int lsb, msb;
        for(Byte b: in){
            lsb = b & 0xF;
            msb = b & 0xF0 >> 4;
            out.add(QAM_map(lsb));
            out.add(QAM_map(msb));
        }
        return out;
    }

    private Complex QAM_map(int x){
        if(bitNum == 16){
            return _16_QAM[x];
        }
        else{
            return new Complex();
        }
    }
}
