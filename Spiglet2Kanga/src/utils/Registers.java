package utils;

public class Registers {
    public String[] reg = new String[24];
    public boolean[] used = new boolean[24];
    public boolean isFull = false;
    

    public Registers() {
        for(int i=0; i<24; i++) {
            reg[i] = null;
            used[i] = false;
        }
    }
    
    public int getOneFreeGenReg(String tempNum) {
        for(int i=6; i<24; i++) {
            if(used[i] == false) {
                reg[i] = tempNum;
                used[i] = true;
                return i;
            }
        }
        isFull = true;
        return -1;
    }

    public int whichReg(String tempNum) {
        for(int i=0; i<24; i++) {
            if(used[i] == true && reg[i] == tempNum) {
                return i;
            }
        }
        return -1;
    }

    public void clearOne(int regNum) {
        reg[regNum] = null;
        used[regNum] = false;
    }

    
}