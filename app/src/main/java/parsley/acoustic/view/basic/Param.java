package parsley.acoustic.view.basic;

import android.provider.ContactsContract;
import android.widget.Toast;
import parsley.acoustic.acmath.Complex;
import java.io.UnsupportedEncodingException;

/**
 * Created by tomsp on 2017/12/30.
 */

public class Param {
    private String key;
    private DataType type;
    private Object value;

    public Param(String key, DataType type, Object value){
        this.key = key;
        this.type = type;
        this.value = value;
    }

    public Object getValue(){
        switch (type){
            case FLOAT:
                float val1 = (float) value;
                return val1;
            case BYTE:
                byte val2 = (byte) value;
                return val2;
            case BIT:
                Boolean val3 = (Boolean) value;
                return val3;
            case INTEGER:
                int val4 = (int) value;
                return val4;
            case COMPLEX:
                Complex val5 = (Complex) value;
                return val5;
            case STRING:
                String val6 = (String) value;
                return val6;
            default:
                return value;
        }
    };

    public String getKey(){
        return key;
    }

    public DataType getType(){
        return type;
    }

    public void setValue(String s){
        value = convert_to_type(s);
    }

    private Object convert_to_type(String s){
        if(type == DataType.FLOAT){
            return Float.parseFloat(s);
        }else if(type == DataType.BYTE){
            byte[] strByte = null;
            try{
                strByte = s.getBytes("UTF-8");
                return strByte;
            }catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }
        }else if(type == DataType.BIT){
            if(s == "True" || s == "true"){
                return true;
            }
            else if(s == "False" || s == "false"){
                return false;
            }
            else{
                return -1;
            }

        }else if(type == DataType.INTEGER){
            return Integer.parseInt(s);
        }else if(type == DataType.COMPLEX){
            Complex c = new Complex();
            c.parseComplex(s);
            return c;
        }else if(type == DataType.STRING){
            return s;
        }
        return s;
    }

    public String toString(){
        if(type == DataType.FLOAT){
            return Float.toString((float)value);
        }else if(type == DataType.BYTE){
            String res = new String((byte[])value);
            return res;
        }else if(type == DataType.BIT){
            if((Boolean) value){
                return "True";
            }
            else{
                return "False";
            }
        }else if(type == DataType.INTEGER){
            return Integer.toString((int)value);
        }else if(type == DataType.COMPLEX){
            double real = ((Complex)value).getReal();
            double img = ((Complex)value).getImag();
            if(img >= 0){
                return Double.toString(real)+ "+"+Double.toString(img)+"j";
            }
            else {
                return Double.toString(real) + "-" + Double.toString(img) + "j";
            }
        }else if(type == DataType.STRING){
            return (String)value;
        }
        return (String) value;
    }
}
