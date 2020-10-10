package toSpiglet;

public class Tmp {
    static int cnt = 20;
    int num;
    static public void setInitCnt(int i) 
    {
        if(i > cnt)
            cnt = i;
    }
    Tmp()
    {
        cnt++;
        num = cnt;
    }
    Tmp(int n)
    {
        num = n;
    }
    public String toString()
    {
        return "TEMP " + num;
    }
}