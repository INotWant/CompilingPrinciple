package CompilingPrinciple.lex;

import java.util.*;

/**
 * @author kissx on 2017/7/26.
 *         实现子集构造算法 ( NFA --> DFA )
 *         <p>
 *         要点：工作表算法 --> 用一个表存储未处理的
 */
public class SubsetConstruction {

    private Map<Integer, String> closureMap;            //存储 epsilon-闭包结果
    private List<DfaNode> dfaList;                      //Dfa List 结构描述
    private int num = 0;
    private final char EPSILON = '空';                   //代表空字符

    public class DfaNode implements Comparable<DfaNode> {
        int id;                             //dfa 标号
        String describe;                    //描述该节点来源于哪些或哪个 nfaNode
        Map<Integer, Character> nextId;     //同 NfaNode 中的 nextId
        boolean isEnd;                      //是否为终节点

        DfaNode(int id, String describe, Map<Integer, Character> nextId) {
            this.id = id;
            this.describe = describe;
            this.nextId = nextId;
        }

        @Override
        public String toString() {
            if (nextId != null) {
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<Integer, Character> entry : nextId.entrySet())
                    sb.append(entry.getKey()).append("-").append(entry.getValue()).append("&");
                if (sb.length() > 0)
                    sb.delete(sb.length() - 1, sb.length());
                return "(did: " + id + " nextId: " + sb.toString() + " describe: " + describe + " isEnd: " + isEnd + ")";
            } else
                return "(did: " + id + " describe: " + describe + " isEnd: " + isEnd + ")";
        }

        @Override
        public int compareTo(DfaNode o) {
            return this.describe.compareTo(o.describe);
        }

        @Override
        public boolean equals(Object obj) {
            return compareTo((DfaNode) obj) == 0;
        }
    }

    /**
     * 首先应该调用此方法，为接下来使用 子集构造方法 做准备。
     * 这里 Map结果中 key：代表 nfa中的一状态 value 对于key中状态的 闭包结果
     *
     * @param nfaArray 输入 nfa 二维表描述
     * @return 所有状态（针对 nfa 的状态）的 epsilon-闭包
     */
    public Map<Integer, String> getClosure(char[][] nfaArray) {
        closureMap = new LinkedHashMap<>();
        for (int i = 0; i < nfaArray.length; i++) {
            StringBuilder sb = new StringBuilder();
            sb.append(i);
            for (int j = 0; j < nfaArray[i].length; j++) {
                if (j < i && nfaArray[i][j] == EPSILON) {
                    String closure = closureMap.get(j);     //利用闭包计算性质，小优化
                    sb.append(",").append(closure);
                } else if (nfaArray[i][j] == EPSILON) {
                    //查找算法
                    LinkedList<Integer> list = new LinkedList<>();
                    list.addFirst(j);
                    bfs(nfaArray, list, sb);
                }
            }
            //最后查重，这里借助 Set 集合特性查重
            Set<String> closureSet = new TreeSet<>(Arrays.asList(sb.toString().split(",")));
            closureMap.put(i, setToString(closureSet));
        }
        return closureMap;
    }

    /**
     * 利用广度优先求闭包
     */
    private void bfs(char[][] nfaArray, LinkedList<Integer> list, StringBuilder sb) {
        if (list.size() > 0) {
            int j = list.pop();
            sb.append(",").append(j);
            for (int i = 0; i < nfaArray[j].length; i++) {
                if (nfaArray[j][i] == EPSILON)
                    list.addLast(i);
            }
            bfs(nfaArray, list, sb);
        }
    }

    /**
     * @return epsilon-闭包结果
     */
    public Map<Integer, String> getClosureMap() {
        return closureMap;
    }

    /**
     * Set 集合显示为 String
     */
    private String setToString(Set<String> set) {
        StringBuilder sb = new StringBuilder();
        for (String str : set)
            sb.append(str).append(",");
        sb.delete(sb.length() - 1, sb.length());
        return sb.toString();
    }

    public List<DfaNode> subsetConstruction(int start, int end, List<Thompson.NfaNode> nfaList) {
        dfaList = new ArrayList<>();
        List<DfaNode> dfaNodes = subsetConstruction(start, nfaList);
        for (DfaNode dfaNode : dfaNodes) {
            dfaNode.isEnd = false;
            String[] strings = dfaNode.describe.split(",");
            for (String str : strings) {
                if (Integer.parseInt(str) == end) {
                    dfaNode.isEnd = true;
                    break;
                }
            }
        }
        return dfaNodes;
    }

    /**
     * nfa --> dfa （子集构造算法）
     * 要点：工作表算法
     * 遇到问题：node节点的标记与其在 List 中的位置 弄混乱了
     *
     * @param start   nfa List描述中，开始节点在 list 中的位置
     * @param nfaList nfa List描述
     * @return Dfa List结构
     */
    private List<DfaNode> subsetConstruction(int start, List<Thompson.NfaNode> nfaList) {
        dfaList = new ArrayList<>();
        num = 0;
        Map<Integer, Character> nMap = nfaList.get(start).nextId;
        if (nMap != null) {
            LinkedList<DfaNode> list = new LinkedList<>();
            if (nMap.size() == 1) {             //某个字符开头
                char c = '无';
                for (Map.Entry<Integer, Character> entry : nMap.entrySet()) {
                    c = entry.getValue();
                }
                Map<Integer, Character> dMap = new HashMap<>();
                dMap.put(1, c);
                DfaNode dfaNode = new DfaNode(num++, String.valueOf(start), dMap);
                dfaList.add(dfaNode);
                //注意
                list.addFirst(new DfaNode(num++, String.valueOf(start), nfaList.get(start).nextId));
            } else if (nMap.size() == 2) {      //'空'开头
                DfaNode dfaNode = new DfaNode(num++, closureMap.get(start), null);
                list.addFirst(dfaNode);
                dfaList.add(dfaNode);
            }
            scDfs(list, nfaList);
        }
        return dfaList;
    }

    private void scDfs(LinkedList<DfaNode> list, List<Thompson.NfaNode> nfaList) {
        List<DfaNode> saveNode = new ArrayList<>();
        while (list.size() > 0) {
            DfaNode dfaNode = list.pop();
            Map<Integer, Character> nMap = dfaNode.nextId;
            if (nMap != null && nMap.size() > 0) {
                int label = -1;
                for (Map.Entry<Integer, Character> entry : nMap.entrySet())
                    label = entry.getKey();
                DfaNode newDfaNode = new DfaNode(dfaNode.id, closureMap.get(label), null);
                // 判断是否已经存在
                if (!dfaList.contains(newDfaNode)) {
                    list.addFirst(newDfaNode);
                    dfaList.add(newDfaNode);
                }
            } else {
                String[] describes = dfaNode.describe.split(",");
                Map<Integer, Character> dMap = new HashMap<>();
                for (String label : describes) {
                    Thompson.NfaNode nfaNode = nfaList.get(Integer.parseInt(label));
                    if (nfaNode.nextId != null)
                        for (Map.Entry<Integer, Character> entry : nfaNode.nextId.entrySet()) {
                            if (entry.getValue() != EPSILON) {
                                DfaNode newDfaNode = new DfaNode(num++, label, nfaList.get(Integer.parseInt(label)).nextId);
                                if (!saveNode.contains(newDfaNode)) {
                                    saveNode.add(newDfaNode);
                                    list.addFirst(newDfaNode);
                                } else
                                    --num;
                                dMap.put(saveNode.get(saveNode.indexOf(newDfaNode)).id, entry.getValue());      //Warning
                            }
                        }
                }
                dfaNode.nextId = dMap;
            }
        }
    }

    /**
     * @return dfa List结构描述
     */
    public List<DfaNode> getDfaList() {
        return dfaList;
    }

    public static void main(String[] args) {
        //由 a.(b|c)* 正则表达式 转换成 dfa 测试

        //[1] 先把 re --> nfa
        Thompson thompson = new Thompson();
        List<Thompson.NfaNode> nfaList = thompson.thompson("a.(b|c)*");
        char[][] nfaArray = thompson.toArray();
        //[2] 再 nfa --> dfa
        SubsetConstruction subsetConstruction = new SubsetConstruction();
        //  2.1 需要先求 nfa 所有状态点的 epsilon-闭包 为下面做准备
        Map<Integer, String> closure = subsetConstruction.getClosure(nfaArray);
        //  输出闭包，验证对应算法
        System.out.println(closure);
        //  2.1 转换为 dfa
        List<DfaNode> dfaList = subsetConstruction.subsetConstruction(thompson.getStart(), thompson.getEnd(), nfaList);
        System.out.println(dfaList);
    }
}
