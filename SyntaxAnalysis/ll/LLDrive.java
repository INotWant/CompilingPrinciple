package CompilingPrinciple.SyntaxAnalysis.ll;

import java.util.*;

/**
 * @author by kissx on 17-7-31.
 *         LLDrive(1)
 *         <p>
 *         注意：为了快速原型，设定单个大写字母为 ‘非终结符’ 且每个 ‘终结符’ 只为其他的单个字符
 */
public class LLDrive {
    private int[][] table;
    private String[] pArray;
    private Map<Character, Integer> xcharMap;
    private Map<Character, Integer> ycharMap;

    private Map<Character, String> followMap;
    private Map<Character, String> firstMap;
    private List<Character> nullableList;

    private List<String> first_sList = new ArrayList<>();

    public LLDrive(String[] pArray) throws Exception {
        this.pArray = pArray;
        init();
    }

    private void init() throws Exception {
        NullAble nullAble = new NullAble(pArray);
        nullableList = nullAble.countNullable();
        First first = new First(pArray, nullableList);
        firstMap = first.countFirst();
        Follow follow = new Follow(pArray, nullableList, firstMap);
        followMap = follow.countFollow();

        xcharMap = new HashMap<>();
        ycharMap = new HashMap<>();
        int num = 0;
        for (String p : pArray) {
            char c = p.charAt(0);
            if (ycharMap.get(c) != null) {
                continue;
            }
            ycharMap.put(c, num++);
        }
        num = 0;
        for (String p : pArray) {
            String lp = PUtil.splitP(p).rightP;
            for (int i = 0; i < lp.length(); i++) {
                char c = lp.charAt(i);
                if (c < 'A' || c > 'Z') {
                    if (xcharMap.get(c) != null)
                        continue;
                    xcharMap.put(c, num++);
                }
            }
        }
        initTable();
    }

    private void countFirst_s() {
        for (String p : pArray) {
            StringBuilder sb = new StringBuilder();
            PUtil.Node pNode = PUtil.splitP(p);
            String rightP = pNode.rightP;
            for (int i = 0; i < rightP.length(); i++) {
                char c = rightP.charAt(i);
                if (c < 'A' || c > 'Z') {
                    if (sb.length() != 0)
                        sb.append(",");
                    sb.append(c);
                    break;
                } else {
                    if (sb.length() != 0)
                        sb.append(",");
                    sb.append(firstMap.get(c));
                    if (!nullableList.contains(c))
                        break;
                }
                String follow = followMap.get(pNode.leftP);
                if (follow != null) {
                    if (sb.length() > 0)
                        sb.append(",");
                    sb.append(follow);
                }
            }
            if ("".equals(rightP))
                sb.append(followMap.get(pNode.leftP));
            Set<String> first_sSet = new HashSet<>(Arrays.asList(sb.toString().split(",")));
            sb.delete(0, sb.length());
            for (String str : first_sSet)
                sb.append(str).append(",");
            sb.delete(sb.length() - 1, sb.length());
            first_sList.add(sb.toString());
        }
    }

    private void initTable() throws Exception {
        countFirst_s();
        //TODO 用于测试
        System.out.println(first_sList);

        table = new int[ycharMap.size()][xcharMap.size()];
        for (int[] aTable : table) {
            Arrays.fill(aTable, -1);
        }
        for (int i = 0; i < first_sList.size(); i++) {
            String list = first_sList.get(i);
            for (String cStr : list.split(",")) {
                char c = cStr.charAt(0);
                int x = xcharMap.get(c);
                int y = ycharMap.get(pArray[i].charAt(0));
                if (table[y][x] == -1)
                    table[y][x] = i;
                else
                    throw new Exception("请修改产生式，此非 LL(1) 文法！");
            }
        }
        //TODO 用于测试
        System.out.println(Arrays.deepToString(table));
    }

    public int[][] getTable() {
        return table;
    }

    public boolean drive(String[] content) throws Exception {
        boolean isTrue = true;
        LinkedList<String> stack = new LinkedList<>();
        addStack(0, stack);
        int i = 0;
        while (i < content.length) {
            String token = content[i];
            String element = stack.pop();
            if (element.charAt(0) < 'A' || element.charAt(0) > 'Z') {    //终结符
                if (token.equals(element))
                    ++i;
                else {
                    isTrue = false;
                    throw new Exception(i + " 处，应该为 " + element);
                }
            } else {                                                     //非终结符
                int y = ycharMap.get(element.charAt(0));
                int x = xcharMap.get(token.charAt(0));
                int pos = table[y][x];
                if (pos > 0)
                    addStack(pos, stack);
                else
                    throw new Exception(i + " 处出现错误！");
            }
        }
        if (stack.size() > 0)
            throw new Exception("不完整，请补充完整！");
        return isTrue;
    }

    private void addStack(int pos, LinkedList<String> stack) {
        String rp = PUtil.splitP(pArray[pos]).rightP;
        for (int i = 0; i < rp.length(); i++) {
            char c = rp.charAt(i);
            stack.addFirst(String.valueOf(c));
        }
    }

    public static void main(String[] args) {
        String[] pArray = {
                "S->NVN",
                "N->s",
                "N->t",
                "N->g",
                "N->w",
                "V->e",
                "V->d"
        };
        try {
            LLDrive llDrive = new LLDrive(pArray);
            String[] content1 = {
                    "s", "e", "w"
            };
            String[] content2 = {
                    "s", "e", "e"
            };
            boolean isTrue1 = llDrive.drive(content1);
            System.out.println(isTrue1);
            boolean isTrue2 = llDrive.drive(content2);
            System.out.println(isTrue2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
