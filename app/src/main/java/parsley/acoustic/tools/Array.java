package parsley.acoustic.tools;

public class Array {
    public static <T> T[] concat(T [] a, T [] b){
        T [] res = (T[])new Object [a.length+b.length];
        int i = 0;
        for(T x: a){
            res[i++] = x;
        }
        for(T x: b){
            res[i++] = x;
        }
        return res;
    }

    public static float [] concat(float [] a, float [] b){
        float [] res = new float[a.length+b.length];
        int i = 0;
        for(float x: a){
            res[i++] = x;
        }
        for(float x: b){
            res[i++] = x;
        }
        return res;
    }

    public static <T> void replace(T [] a, T [] b, int begin, int num){
        for(int i = 0; i < num; i++){
            a[begin + i] = b [i];
        }
    }

    public static void replace(float [] a, float [] b, int begin, int num){
        for(int i = 0; i < num; i++){
            a[begin + i] = b [i];
        }
    }
}
