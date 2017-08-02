package CompilingPrinciple.SyntaxAnalysis.lr;

import CompilingPrinciple.SyntaxAnalysis.ll.PUtil;

import java.util.*;

/**
 * @author by computer on 17-8-2.
 *         实现 LR(0)
 */
public class LR {
    private String[] pArray = {
            "A->S$",
            "S->L=R",
            "S->R",
            "L->*R",
            "L->i",
            "R->L"
    };

    private List<LrNode> lrDfaList;


    public LR(String[] pArray) {
        this.pArray = pArray;
    }

    public LR() {
    }

    class LrNode implements Comparable<LrNode> {
        int id;
        List<String> contentList;
        Map<Character, Integer> nextId;

        LrNode(int id, List<String> contentList, Map<Character, Integer> nextId) {
            this.id = id;
            this.contentList = contentList;
            this.nextId = nextId;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Id: ").append(id).append("\n");
            sb.append("Content: " + "\n");
            for (String content : contentList)
                sb.append("\t").append(content).append("\n");
            sb.append("NextId: \n");
            for (Map.Entry<Character, Integer> entry : nextId.entrySet())
                sb.append('\t').append(entry.getKey()).append("-->").append(entry.getValue());
            return sb.toString();
        }

        @Override
        public int compareTo(LrNode o) {        //定义的时候由于左右两个在比较的时候处于不同的地位，所以在使用的时候要注意究竟要使用哪一个调用该函数，而另一个作为参数
            List<String> list1 = o.contentList;
            List<String> list2 = this.contentList;
            if (list1.size() >= list2.size()) {
                for (int i = 0; i < list2.size(); i++) {
                    String s1 = list1.get(i);
                    boolean isEqual = false;
                    for (int j = 0; j < list2.size(); j++) {
                        String s2 = list2.get(j);
                        if (s1.equals(s2))
                            isEqual = true;
                    }
                    if (!isEqual)
                        return -1;
                }
                return 0;
            } else
                return -1;
        }

        @Override
        public boolean equals(Object obj) {
            return compareTo((LrNode) obj) == 0;
        }

    }

    public void countLrDfa() {
        lrDfaList = new ArrayList<>();
        String startP = pArray[0];
        LinkedList<LrNode> waitList = new LinkedList<>();
        int num = 0;
        LrNode lrNode = new LrNode(num++, null, null);
        List<String> contentList = new ArrayList<>();
        contentList.add(modifyP(startP));
        lrNode.contentList = contentList;
        waitList.addFirst(lrNode);
        while (waitList.size() > 0) {
            LrNode currNode = waitList.pop();
            Set<Character> setChar = new LinkedHashSet<>();
            List<String> cList = currNode.contentList;
            for (int i = 0; i < cList.size(); i++) {
                String p = cList.get(i);
                if (p.indexOf('.') != p.length() - 1) {
                    char c = p.charAt(p.indexOf('.') + 1);
                    if (c >= 'A' && c <= 'Z') {
                        if (!setChar.contains(c)) {
                            cList.addAll(getP(c));
                            setChar.add(c);
                        }
                    }
                }
            }
            Map<Character, Integer> nextId = new HashMap<>();
            Map<Character, String> equalMap = equal(cList);
            for (Map.Entry<Character, String> entry : equalMap.entrySet()) {
                List<String> newContentList = new ArrayList<>();
                for (String iStr : entry.getValue().split(",")) {
                    int i = Integer.parseInt(iStr);
                    newContentList.add(modifyP(cList.get(i)));
                }
                LrNode newLrNode = new LrNode(num++, newContentList, null);
                if (!lrDfaList.contains(newLrNode) && !waitList.contains(newLrNode) && !newLrNode.equals(currNode)) {
                    waitList.addLast(newLrNode);
                    nextId.put(entry.getKey(), num - 1);
                } else {
                    --num;
                    if (newLrNode.equals(currNode))
                        nextId.put(entry.getKey(), currNode.id);
                    else
                        nextId.put(entry.getKey(), getId(newLrNode, waitList));
                }
            }
            currNode.nextId = nextId;
            lrDfaList.add(currNode);
        }
    }

    private int getId(LrNode lrNode, LinkedList<LrNode> waitList) {
        for (LrNode node : lrDfaList) {
            if (lrNode.equals(node))
                return node.id;
        }
        for (LrNode node : waitList)
            if (lrNode.equals(node))
                return node.id;
        return -1;
    }

    private Map<Character, String> equal(List<String> contentList) {
        Map<Character, String> resultMap = new HashMap<>();
        for (int i = 0; i < contentList.size(); i++) {
            String content = contentList.get(i);
            if (content.indexOf(".") != content.length() - 1) {
                char c = content.charAt(content.indexOf('.') + 1);
                if (resultMap.get(c) == null)
                    resultMap.put(c, String.valueOf(i));
                else
                    resultMap.put(c, resultMap.get(c) + ',' + i);
            }
        }
        return resultMap;
    }

    private String modifyP(String p) {
        String newP;
        if (!p.contains(".")) {
            PUtil.Node pNode = PUtil.splitP(p);
            newP = pNode.getLeftP() + "->." + pNode.getRightP();
        } else {
            int pos = p.indexOf('.');
            newP = p.replace("." + p.charAt(pos + 1), p.charAt(pos + 1) + ".");
        }
        return newP;
    }

    private List<String> getP(char c) {
        List<String> pList = new ArrayList<>();
        for (String p : pArray) {
            PUtil.Node pNode = PUtil.splitP(p);
            char leftP = pNode.getLeftP();
            if (c == leftP)
                pList.add(pNode.getLeftP() + "->." + pNode.getRightP());
        }
        return pList;
    }

    public List<LrNode> getLrDfaList() {
        return lrDfaList;
    }

    public static void main(String[] args) {
        LR lr = new LR();
        lr.countLrDfa();
        System.out.println(lr.getLrDfaList());
    }
}
