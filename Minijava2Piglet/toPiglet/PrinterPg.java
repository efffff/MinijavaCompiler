package toPiglet;

public class PrinterPg {
    static String out = "";
    public static void add(String s) {
        out += s;
    }
    public static String pprint() {
        return out;
    } 
}