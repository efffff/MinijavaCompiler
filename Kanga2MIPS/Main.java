import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import syntaxtree.*;
import visitor.*;

public class Main {
    public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			//String test = "/Users/yufeiwang/Downloads/Compiler-master/src/testsample/m_program/Factorial.java";
			InputStream in = new FileInputStream(args[0]);
			String fileName = "";
			if(args[0].lastIndexOf("/") >= 0){
				fileName = args[0].substring(args[0].lastIndexOf("/")+1, args[0].length()-3);
			}
			else {
				fileName = args[0].substring(args[0].lastIndexOf("\\")+1, args[0].length()-3);
            }
            
            Node root = new KangaParser(in).Goal();
			root.accept(new toMIPSVisitor(), null);
			
			String mipsTranslation = mPrinter.mprint();

			BufferedWriter out = new BufferedWriter(new FileWriter("./" + fileName + ".s"));
            out.write(mipsTranslation);
            out.close();

		} catch (Exception e) {
			e.printStackTrace();
        } 
    }
}