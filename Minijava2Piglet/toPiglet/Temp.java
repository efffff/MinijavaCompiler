package toPiglet;

public class Temp {
    static int cnt = 20;
    int num;
    Temp()
    {
        
        num = cnt;
        cnt++;
    }
    Temp(int n)
    {
        num = n;
    }
    public static int getTempNo20()
    {
        int ret = cnt;
        cnt++;
        return ret;
    }
    public String tostring()
    {
        return "TEMP " + num;
    }
}