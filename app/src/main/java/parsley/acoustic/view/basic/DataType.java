package parsley.acoustic.view.basic;

import android.provider.ContactsContract;

/**
 * Created by tomsp on 2017/12/20.
 */

public enum DataType {
    COMPLEX("complex"),
    INTEGER("integer"),
    STRING("string"),
    BYTE("byte"),
    BIT("bit"),
    FLOAT("float");

    private String name;

    DataType(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
