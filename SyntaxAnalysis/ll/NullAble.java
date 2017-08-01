package CompilingPrinciple.SyntaxAnalysis.ll;

import java.util.ArrayList;
import java.util.List;

import static CompilingPrinciple.SyntaxAnalysis.ll.PUtil.splitP;

/**
 * @author by kissx on 17-7-31.
 *         求 NullAble 集合
 *         <p>
 *         注意：为了快速原型，设定单个大写字母为 ‘非终结符’ 且每个 ‘终结符’ 只为其他的单个字符
 */
public class NullAble {
    private String[] pArray = {
            "S->Z",
            "Z->d",
            "Z->XYZ",
            "Y->c",
            "Y->",
            "X->Y",
            "X->a"
    };

    private List<Character> nullableList;

    public NullAble(String[] pArray) {
        this.pArray = pArray;
    }

    public NullAble() {
    }

    public List<Character> countNullable() {
        nullableList = new ArrayList<>();
        int num = 0;
        boolean isStart = true;
        while (nullableList.size() > num || isStart) {
            isStart = false;
            num = nullableList.size();
            for (String p : pArray) {
                PUtil.Node pNode = splitP(p);
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

    public static void main(String[] args) {
        NullAble nullAble = new NullAble();
        List<Character> nullableList = nullAble.countNullable();
        System.out.println(nullableList);
    }
}
