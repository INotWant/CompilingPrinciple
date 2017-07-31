package CompilingPrinciple.SyntaxAnalysis.ll;

import java.util.ArrayList;
import java.util.List;

/**
 * @author by kissx on 17-7-31.
 *         求 NullAble 集合
 */
public class NullAble {
    private String[] pArray = {
            "Z->d",
            "Z->XYZ",
            "Y->c",
            "Y->",
            "X->Y",
            "X->a"
    };

    private List<Character> nullableList;

    public List<Character> countNullable() {
        nullableList = new ArrayList<>();
        int num = 0;
        boolean isStart = true;
        while (nullableList.size() > num || isStart) {
            isStart = false;
            num = nullableList.size();
            for (String p : pArray) {
                Node pNode = splitP(p);
                if (pNode.rightP.equals("")) {
                    if (!nullableList.contains(pNode.leftP))
                        nullableList.add(pNode.leftP);
                } else {
                    String rightP = pNode.rightP;
                    boolean isIn = true;
                    for (int i = 0; i < rightP.length(); i++) {
                        char c = rightP.charAt(i);
                        if (!nullableList.contains(c)) {
                            isIn = false;
                            break;
                        }
                    }
                    if (isIn) {
                        if (!nullableList.contains(pNode.leftP))
                            nullableList.add(pNode.leftP);
                    }
                }
            }
        }
        return nullableList;
    }

    public List<Character> getNullableList() {
        return nullableList;
    }

    class Node {
        char leftP;
        String rightP = "";

        Node(char leftP, String rightP) {
            this.leftP = leftP;
            this.rightP = rightP;
        }
    }

    private Node splitP(String p) {
        String[] array = p.split("->");
        if (array.length == 2)
            return new Node(array[0].charAt(0), array[1]);
        else
            return new Node(array[0].charAt(0), "");
    }

    public static void main(String[] args) {
        NullAble nullAble = new NullAble();
        List<Character> nullableList = nullAble.countNullable();
        System.out.println(nullableList);
    }
}
