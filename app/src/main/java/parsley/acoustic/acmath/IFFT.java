package parsley.acoustic.acmath;

public class IFFT {
    public static Complex [] ifft_impl(Complex [] seq){
        int n = seq.length;
        if(n <= 1){
            return seq;
        }
        try{
            int r = n / 2;
            Complex [] _even = new Complex[r];
            Complex [] _odd = new Complex[r];
            Complex [] even = new Complex[r];
            Complex [] odd = new Complex[r];
            for(int i = 0, k = 0; i < n; i+=2,k++){
                _even[k] = seq[i];
            }
            even = ifft_impl(_even);

            for(int i = 1, k = 0; i < n; i+=2, k++){
                _odd[k] = seq[i];
            }
            odd = ifft_impl(_odd);

            Complex [] T = new Complex[r];
            for(int i = 0; i < r; i++){
                double x = 2*Math.PI*i/n;
                double real = Math.cos(x);
                double imag = Math.sin(x);
                Complex tmp = new Complex(real,imag);
                T[i] = Complex.mulComplex(tmp,odd[i]);
            }
            //result
            Complex [] res = new Complex[n];
            for(int i = 0; i < r; i++){
                res[i] = Complex.addComplex(even[i],T[i]);
            }
            for(int i = 0; i < r; i++){
                res[r+i] = Complex.decComplex(even[i],T[i]);
            }
            return res;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static Complex [] ifft(Complex [] in){
        int l = in.length;
        int n =  (int)Math.pow(2,(int)(Math.log(l-1) / Math.log(2))+1);
        int paddingzero_num =n-l;
        Complex [] x = new Complex [n];
        for(int i = 0; i < n; i++){
            if(i < l)
                x[i] = in[i];
            else
                x[i] = new Complex(0,0);
        }


        Complex [] out = ifft_impl(x);
        for(Complex i:out){
            i.setReal(i.getReal()/l);
            i.setImag(i.getImag()/l);
        }
        return out;
    }
}
