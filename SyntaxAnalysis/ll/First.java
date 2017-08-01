package CompilingPrinciple.SyntaxAnalysis.ll;

import java.util.*;

/**
 * @author by kissx on 17-7-31.
 *         求 First 集合
 *         <p>
 *         注意：为了快速原型，设定单个大写字母为 ‘非终结符’ 且每个 ‘终结符’ 只为其他的单个字符
 */
public class First {
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

    private Map<Character, String> firstMap;

    public First(String[] pArray, List<Character> nullableList) {
        this.pArray = pArray;
        this.nullableList = nullableList;
    }

    public First(List<Character> nullableList) {
        this.nullableList = nullableList;
    }

    public Map<Character, String> countFirst() {
        firstMap = new HashMap<>();
        boolean isChange = true;
        while (isChange) {
            isChange = false;
            for (String p : pArray) {
                PUtil.Node pNode = PUtil.splitP(p);
                if (!"".equals(pNode.rightP)) {
                    char c = pNode.rightP.charAt(0);
                    if (c < 'A' || c > 'Z') {
                        if (firstMap.get(pNode.leftP) == null) {
                            firstMap.put(pNode.leftP, String.valueOf(c));
                            isChange = true;
                        } else {
                            if (add(String.valueOf(c), pNode.leftP))
                                isChange = true;
                        }
                    } else {
                        String rightP = pNode.rightP;
                        for (int i = 0; i < rightP.length(); i++) {
                            char c1 = rightP.charAt(i);
                            if (i == 0 || nullableList.contains(rightP.charAt(i - 1))) {
                                String first = firstMap.get(pNode.leftP);
                                if (first != null && firstMap.get(c1) != null) {
                                    if (add(firstMap.get(c1), pNode.leftP))
                                        isChange = true;
                                } else if (firstMap.get(c1) != null) {
                                    firstMap.put(pNode.leftP, firstMap.get(c1));
                                    isChange = true;
                                }
                            } else
                                break;
                        }
                    }
                }
            }
        }

        return firstMap;
    }

    private boolean add(String newPart, char key) {
        boolean isAdd = false;
        String first = firstMap.get(key);
        String[] arr1 = first.split(",");
        String[] arr2 = newPart.split(",");
        Set<String> firstSet = new HashSet<>(Arrays.asList(arr1));
        firstSet.addAll(Arrays.asList(arr2));
        StringBuilder sb = new StringBuilder();
        for (String element : firstSet) {
            sb.append(element).append(',');
        }
        sb.delete(sb.length() - 1, sb.length());
        if (sb.length() != first.length()) {
            isAdd = true;
            firstMap.put(key, sb.toString());
        }
        return isAdd;
    }

    public Map<Character, String> getFirstMap() {
        return firstMap;
    }

    public static void main(String[] args) {
        String[] pArray = {
//                "S->Z",
                "Z->d",
                "Z->XYZ",
                "Y->c",
                "Y->",
                "X->Y",
                "X->a"
        };
        NullAble nullAble = new NullAble(pArray);
        List<Character> nullableList = nullAble.countNullable();
        System.out.println(nullableList);
        First first = new First(pArray, nullableList);
        Map<Character, String> firstMap = first.countFirst();
        System.out.println(firstMap);
    }
}
