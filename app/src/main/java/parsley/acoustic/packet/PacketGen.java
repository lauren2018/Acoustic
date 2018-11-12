package parsley.acoustic.packet;

import android.content.Context;
import android.renderscript.ScriptGroup;
import android.util.Log;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class PacketGen {
    private String fileName;
    private InputStream in;

    public PacketGen(InputStream e_in){
        in = e_in;
        work();
    }

    public PacketGen(String fn){
        fileName = fn;
        File file = new File(fileName);
        work();
    }

    void work(){
        File file = new File(fileName);
        try {
            //DataInputStream in = new DataInputStream(new FileInputStream(file));
            Log.i("Packet Gen","Successful In Printing!\n");
        }
        catch(Exception e){
            Log.w("Packet Gen","File Open Error!\n");
        }
    }

}
