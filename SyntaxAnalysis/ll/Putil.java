package CompilingPrinciple.SyntaxAnalysis.ll;

/**
 * @author by computer on 17-8-1.
 *         产生式处理帮助类
 */
public class PUtil {
    static class Node {
        char leftP;
        String rightP = "";

        Node(char leftP, String rightP) {
            this.leftP = leftP;
            this.rightP = rightP;
        }
    }

    public static Node splitP(String p) {
        String[] array = p.split("->");
        if (array.length == 2)
            return new Node(array[0].charAt(0), array[1]);
        else
            return new Node(array[0].charAt(0), "");
    }
}
