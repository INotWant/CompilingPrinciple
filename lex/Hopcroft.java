package CompilingPrinciple.lex;

import java.util.*;

/**
 * @author kissx on 2017/7/27.
 *         Hopcroft 算法
 *         实现优化 dfa
 *         <p>
 *         要点：切分集合
 *         遇到问题：Node 的标记问题
 */
public class Hopcroft {

    private List<DfaNodeOpt> dfaNodeListOpt;     //优化后的 dfa List结构描述
    private final char EPSILON = '空';           //代表空字符

    public class DfaNodeOpt {       //基本等同于 DfaNode
        int id;
        String describe;
        Map<Integer, String> nextId;    //区别在此， value 类型改为 String
        boolean isEnd;

        DfaNodeOpt(int id, String describe, Map<Integer, String> nextId) {
            this.id = id;
            this.describe = describe;
            this.nextId = nextId;
        }

        @Override
        public String toString() {
            if (nextId != null) {
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<Integer, String> entry : nextId.entrySet())
                    sb.append(entry.getKey()).append("-").append(entry.getValue()).append("&");
                if (sb.length() > 0)
                    sb.delete(sb.length() - 1, sb.length());
                return "(did: " + id + " nextId: " + sb.toString() + " describe: " + describe + " isEnd: " + isEnd + ")";
            } else
                return "(did: " + id + " describe: " + describe + " isEnd: " + isEnd + ")";
        }
    }

    /**
     * 优化 dfa
     *
     * @param dfaNodeList dfa List描述
     * @return 优化后的 dfa
     */
    public List<DfaNodeOpt> hopcroft(List<SubsetConstruction.DfaNode> dfaNodeList) {
        dfaNodeListOpt = new ArrayList<>();
        int num = 0;
        LinkedList<String> waitList = new LinkedList<>();   //待分的集合串
        List<String> finalList = new ArrayList<>();         //不可分的集合串
        //1.首先分为 “不可接受集合” “可接受集合”
        StringBuilder Nsb = new StringBuilder();    //不可接受
        StringBuilder Asb = new StringBuilder();    //可接受
        for (SubsetConstruction.DfaNode dfaNode : dfaNodeList) {
            if (dfaNode.isEnd)
                Asb.append(dfaNode.id).append(",");
            else
                Nsb.append(dfaNode.id).append(",");
        }
        Nsb.delete(Nsb.length() - 1, Nsb.length());
        Asb.delete(Asb.length() - 1, Asb.length());
        waitList.addFirst(Nsb.toString());
        waitList.addFirst(Asb.toString());
        //2.基于工作表，进行集合切分处理
        while (waitList.size() > 0) {
            String waitSplit = waitList.pop();
            split(waitSplit, waitList, finalList, dfaNodeList);
        }
        //标记问题
        for (String label : finalList) {
            Map<Integer, String> newDMap = new HashMap<>();
            String[] elements = label.split(",");
            for (String idStr : elements) {
                int id = Integer.parseInt(idStr);
                Map<Integer, Character> dMap = findDfaNode(id, dfaNodeList).nextId;
                for (Map.Entry<Integer, Character> entry : dMap.entrySet()) {
                    String listStr = String.valueOf(entry.getValue());
                    String lastListStr = newDMap.get(findId(entry.getKey(), finalList));
                    if (lastListStr != null && !lastListStr.contains(listStr))
                        listStr = listStr + "," + lastListStr;
                    newDMap.put(findId(entry.getKey(), finalList), listStr);
                }
            }
            dfaNodeListOpt.add(new DfaNodeOpt(num++, label, newDMap));
        }
        //3.添加 isEnd 标记（终结点）
        for (DfaNodeOpt dfaNodeOpt : dfaNodeListOpt) {
            String idStr = dfaNodeOpt.describe.split(",")[0];
            int id = Integer.parseInt(idStr);
            for (String labelStr : Asb.toString().split(",")) {
                int label = Integer.parseInt(labelStr);
                if (id == label) {
                    dfaNodeOpt.isEnd = true;
                    break;
                }
            }
        }
        return dfaNodeListOpt;
    }

    /**
     * 切分函数
     */
    private void split(String splitStr, LinkedList<String> waitList, List<String> finalList, List<SubsetConstruction.DfaNode> dfaNodeList) {
        List<Character> cList = new ArrayList<>();
        String[] waitSplits = splitStr.split(",");
        boolean flag = false;       // 用于判读是否切得更小了
        if (waitSplits.length > 1) {
            for (String idStr : waitSplits) {
                int id = Integer.parseInt(idStr);
                Map<Integer, Character> dMap = findDfaNode(id, dfaNodeList).nextId;
                for (Map.Entry<Integer, Character> entry : dMap.entrySet()) {
                    if (entry.getValue() != EPSILON) {
                        if (!cList.contains(entry.getValue()))
                            cList.add(entry.getValue());
                    }
                }
            }
            for (char c : cList) {
                List<Couple> tempList = new ArrayList<>();      //暂存对某字符所有状态的转向
                for (String idStr : splitStr.split(",")) {
                    int id = Integer.parseInt(idStr);
                    Map<Integer, Character> dMap = findDfaNode(id, dfaNodeList).nextId;
                    int label = -1;     //转向的状态
                    if (dMap.containsValue(c)) {
                        for (Map.Entry<Integer, Character> entry : dMap.entrySet()) {
                            if (entry.getValue() == c) {
                                label = entry.getKey();
                                break;
                            }
                        }
                        tempList.add(new Couple(id, find(label, waitSplits, waitList, finalList)));
                    }
                }
                Map<Integer, String> splitMap = new HashMap<>();    //对某字符的 Split 结果
                for (Couple couple : tempList) {
                    int pos = couple.pos;
                    String list = splitMap.get(pos); //存储的对应的有几个元素
                    if (list == null) {
                        list = String.valueOf(couple.id);
                    } else
                        list = list + ',' + couple.id;
                    splitMap.put(pos, list);
                }
                // 注意下面这句的判定
                if (splitMap.size() > 1 || (!splitMap.containsKey(0) && ((String) (splitMap.values().toArray()[0])).split(",").length < waitSplits.length)) {
                    flag = true;
                    List<Integer> all = new ArrayList<>();
                    for (String idStr : waitSplits)
                        all.add(Integer.parseInt(idStr));
                    for (Map.Entry<Integer, String> entry : splitMap.entrySet()) {
                        if (entry.getKey() != 0) {
                            waitList.addFirst(entry.getValue());
                            for (String label : entry.getValue().split(","))
                                all.remove(all.indexOf(Integer.parseInt(label)));
                        }
                    }
                    StringBuilder sb = new StringBuilder();
                    for (int i : all)
                        sb.append(i).append(",");
                    if (sb.length() > 0) {
                        sb.delete(sb.length() - 1, sb.length());
                        waitList.addFirst(sb.toString());
                    }
                    break;  //不再用这些字符分
                }
            }
        }
        if (waitSplits.length == 1 || !flag)
            finalList.add(splitStr);
    }

    /**
     * 查找标记
     */
    private int findId(int key, List<String> finalList) {
        int i = 0;
        for (String label : finalList) {
            String[] elements = label.split(",");
            for (String idStr : elements) {
                int id = Integer.parseInt(idStr);
                if (key == id)
                    return i;
            }
            ++i;
        }
        return -1;
    }

    class Couple {
        int id;     //对应的是哪个结点
        int pos;    //对应在哪个 集合 中

        Couple(int id, int pos) {
            this.id = id;
            this.pos = pos;
        }
    }

    /**
     * 解决标记问题1
     */
    private SubsetConstruction.DfaNode findDfaNode(int id, List<SubsetConstruction.DfaNode> dfaNodeList) {
        for (SubsetConstruction.DfaNode dfaNode : dfaNodeList)
            if (dfaNode.id == id)
                return dfaNode;
        return null;
    }

    /**
     * 解决标记问题2
     */
    private int find(int label, String[] waitSplits, LinkedList<String> waitList, List<String> finalList) {
        if (label != -1) {
            for (String waitSplit : waitSplits) {
                if (String.valueOf(label).equals(waitSplit))
                    return 0;
            }
            int i = 1;
            for (String waitStr : waitList) {
                for (String waitSplit : waitStr.split(",")) {
                    if (String.valueOf(label).equals(waitSplit))
                        return i;
                }
                ++i;
            }
            for (String finalStr : finalList) {
                for (String finalLabel : finalStr.split(",")) {
                    if (String.valueOf(label).equals(finalLabel))
                        return i;
                }
                ++i;
            }
        }
        return -1;
    }

    /**
     * @return 返回优化后的 DfaList
     */
    public List<DfaNodeOpt> getDfaNodeListOpt() {
        return dfaNodeListOpt;
    }

    /**
     * @return 返回优化后 dfa 开始节点在 List 中的位置
     */
    public int getStart() {
        int i = 0;
        for (DfaNodeOpt dfaNodeOpt : dfaNodeListOpt) {
            if (dfaNodeOpt.describe.equals("0"))
                return i;
        }
        ++i;
        return i;
    }

    public static void main(String[] args) {
        //测试: 由 f.(e.e|i.e) 产生 优化后的 dfa

        //1 re --> nfa
        Thompson thompson = new Thompson();
        List<Thompson.NfaNode> nfaList = thompson.thompson("f.(e.e|i.e)");
        char[][] nfaArray = thompson.toArray();
        //2 nfa --> dfa
        SubsetConstruction subsetConstruction = new SubsetConstruction();
        subsetConstruction.getClosure(nfaArray);
        subsetConstruction.subsetConstruction(thompson.getStart(), thompson.getEnd(), nfaList);
        //3 优化 dfa
        Hopcroft hopcroft = new Hopcroft();
        List<DfaNodeOpt> dfaNodeListOpt = hopcroft.hopcroft(subsetConstruction.getDfaList());
        System.out.println(dfaNodeListOpt);
    }
}
