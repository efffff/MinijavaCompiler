import Piglet.*;
import Piglet.syntaxtree.*;
import java.util.*;
import java.io.*;
import toSpiglet.*;

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
            
            Node root = new PigletParser(in).Goal();
            root.accept(new getTempVisitor(), null);
            //System.out.println(x);
			root.accept(new SpigletVisitor(), null);
			
			String spigletTranslation = Printer.pprint();
			//System.out.println(pigletTranslation);
			//File file = new File("/Users/yufeiwang/Downloads/Compiler-master/src/testsample/pg/" + fileName + ".pg");
			//file.createNewFile();

			BufferedWriter out = new BufferedWriter(new FileWriter("./" + fileName + ".spg"));
            out.write(spigletTranslation);
            out.close();

			/*
			FileOutputStream out = new FileOutputStream(file);
			byte bytes[] = new byte[99999999];
			bytes = pigletTranslation.getBytes();
			out.write(bytes);
			out.close();
			*/

		} catch (Exception e) {
			//ErrorPrinter.print();
			e.printStackTrace();
        } 
    }
}