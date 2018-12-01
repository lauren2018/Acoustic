package parsley.acoustic.view.basic;

import android.provider.ContactsContract;
import android.widget.Toast;
import parsley.acoustic.acmath.Complex;
import java.io.UnsupportedEncodingException;
import java.util.Stack;
import java.util.Vector;

/**
 * Created by tomsp on 2017/12/30.
 */

public class Param {
    private String key;
    private DataType type;
    private Object value;
    private boolean isArray;

    public Param(String key, DataType type, Object value) {
        this.key = key;
        this.type = type;
        this.value = value;
    }

    public Object getValue() {
        switch (type) {
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
    }

    ;

    public String getKey() {
        return key;
    }

    public DataType getType() {
        return type;
    }

    public void setValue(String s) {
        value = fromString(s);
    }

    private Object fromString(String s) {
        if (!isArray) {
            if (type == DataType.FLOAT) {
                return Float.parseFloat(s);
            } else if (type == DataType.BYTE) {
                byte[] strByte = null;
                try {
                    strByte = s.getBytes("UTF-8");
                    return strByte;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else if (type == DataType.BIT) {
                if (s == "True" || s == "true") {
                    return true;
                } else if (s == "False" || s == "false") {
                    return false;
                } else {
                    return -1;
                }

            } else if (type == DataType.INTEGER) {
                return Integer.parseInt(s);
            } else if (type == DataType.COMPLEX) {
                Complex c = new Complex();
                c.parseComplex(s);
                return c;
            } else if (type == DataType.STRING) {
                return s;
            }
        } else {
            stringToArray(s);
        }

        return s;
    }

    public String toString() {
        if (type == DataType.FLOAT) {
            return Float.toString((float) value);
        } else if (type == DataType.BYTE) {
            String res = new String((byte[]) value);
            return res;
        } else if (type == DataType.BIT) {
            if ((Boolean) value) {
                return "True";
            } else {
                return "False";
            }
        } else if (type == DataType.INTEGER) {
            return Integer.toString((int) value);
        } else if (type == DataType.COMPLEX) {
            double real = ((Complex) value).getReal();
            double img = ((Complex) value).getImag();
            if (img >= 0) {
                return Double.toString(real) + "+" + Double.toString(img) + "j";
            } else {
                return Double.toString(real) + "-" + Double.toString(img) + "j";
            }
        } else if (type == DataType.STRING) {
            return (String) value;
        }
        return (String) value;
    }

    private Vector stringToArray(String str) {
        Stack st = new Stack();
        int b = 0, e = 0, idx = 0;
        int d = -1;
        boolean firstRightParenthesis = true;
        Vector[] allVectors = null;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '(') {
                st.push(i);
            } else if (str.charAt(i) == ')' && firstRightParenthesis) {
                /**Init d dimensions for all vectors*/
                firstRightParenthesis = false;
                d = st.size();
                allVectors = new Vector[d];
                for (int j = 0; j < d; j++) {
                    allVectors[j] = new Vector();
                }

                /**translate string to array*/
                idx = st.size() - 1;
                b = (int) st.pop() + 1;
                e = i;
                for (String substr : str.substring(b, e).split(" *, *")) {
                    allVectors[idx].add(fromString(substr));
                }
                allVectors[idx - 1].add(allVectors[idx]);
                allVectors[idx] = new Vector();
            } else if (str.charAt(i) == ')' && !firstRightParenthesis) {
                if (st.size() == d) {
                    /**translate string to array*/
                    idx = st.size() - 1;
                    b = (int) st.pop() + 1;
                    e = i;
                    for (String substr : str.substring(b, e).split(" *, *")) {
                        allVectors[idx].add(fromString(substr));
                    }
                    allVectors[idx - 1].add(allVectors[idx]);
                    allVectors[idx] = new Vector();
                } else if (st.size() > 1) {
                    /**translate string to array*/
                    idx = st.size() - 1;
                    allVectors[idx - 1].add(allVectors[idx]);
                    allVectors[idx] = new Vector();
                    st.pop();
                }
            }

        }
        Vector root = allVectors[0];
        return root;
    }
}