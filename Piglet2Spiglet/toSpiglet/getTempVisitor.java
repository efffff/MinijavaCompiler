package toSpiglet;

import Piglet.visitor.GJDepthFirst;
import Piglet.syntaxtree.*;
import Piglet.visitor.*;

public class getTempVisitor extends GJDepthFirst<String,Object> {
    /**
    * f0 -> "TEMP"
    * f1 -> IntegerLiteral()
    */
    public String visit(Temp n, Object argu) {
        String tNum = n.f1.f0.tokenImage;
        int num = Integer.parseInt(tNum);
        Tmp.setInitCnt(num);
        return null;
    }

}