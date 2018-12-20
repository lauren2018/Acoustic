package parsley.acoustic.acmath;



public class Complex{
    double real,imag;
    public Complex()
    {
        this.real=0;
        this.imag=0;
    }
    public Complex(double real,double imag)
    {
        this.real=real;
        this. imag = imag;
    }

    public Complex(Complex c)
    {
        this.real=c.real;
        this. imag = c.imag;
    }

    public static Complex polar(double ampl, double phase){
        Complex x = new Complex();
        x.real = ampl*Math.cos(phase);
        x.imag = ampl*Math.sin(phase);
        return x;
    }
    public double getReal()  { return this.real;    }
    public double getImag() { return this. imag;    }
    public double getReal(Complex c)    {    return c.real;        }
    public double getImag(Complex c)    {    return c. imag;            }
    public void setReal    (double real)    {    this.real=real;        }
    public void setImag(double  imag)    {    this. imag = imag;        }

    public static Complex addComplex(Complex a,Complex b)
    {
        Complex temp =new Complex();
        temp.real=a.real+b.real;
        temp. imag =a. imag +b. imag;
        return temp;
    }    
    public static Complex decComplex(Complex a,Complex b)
    {
        Complex temp = new Complex();
        temp.real = a.real - b.real;
        temp. imag  = a. imag  - b. imag;
        return temp;
    }
    public static Complex mulComplex(Complex a,Complex b)
    {
        Complex temp = new Complex();
        temp.real = a.real*b.real-a. imag*b. imag;
        temp. imag    = a.real*b. imag+a. imag*b.real;
        return temp;
    }
    public static Complex divComplex(Complex a,Complex b)
    {
        Complex temp = new Complex();
        temp.real=(a.real*b.real+a. imag*b. imag)/(b.real*b.real+b. imag*b. imag);
        temp. imag =(a. imag*b.real-a.real*b. imag)/(b.real*b.real+b. imag*b. imag);
        return temp;
    }
    
    public void addComplex(Complex cplx)
    {
        this.real=this.real+cplx.real;
        this. imag =this. imag +cplx. imag;
    }
    public void decComplex(Complex cplx)
    {
        this.real=this.real-cplx.real;
        this. imag =this. imag -cplx. imag;
    }
    public void mulComplex(Complex cplx)
    {
        double temp=this.real;
        this.real=this.real*cplx.real-this. imag*cplx. imag;
        this. imag =temp*cplx. imag+this. imag*cplx.real;
    }
    public void divComplex(Complex cplx)
    {
        double temp=this.real;
        this.real=(this.real*cplx.real+this. imag*cplx. imag)/(cplx.real*cplx.real+cplx. imag*cplx. imag);
        this. imag =(this. imag*cplx.real-temp*cplx. imag)/(cplx.real*cplx.real+cplx. imag*cplx. imag);
    }

    public Complex conjugate(){
        double newReal = this.real;
        double newImag = -this.imag;
        return new Complex(newReal, newImag);
    }

    public  Complex parseComplex(String s){
        String [] str = s.split("[\\+-ij]",2);
        real = Float.parseFloat(str[0]);
        imag = Float.parseFloat(str[1]);
        return this;
    }
}