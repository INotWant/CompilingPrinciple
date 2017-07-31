package CompilingPrinciple.SyntaxAnalysis.rdp;

/**
 * @author by kissx on 17-7-31.
 *         实现递归下降
 *         <p>
 *         待实现的产生式
 *         "E->T+E",
 *         "E->T",
 *         "T->F*T",
 *         "T->F",
 *         "F->num"
 */

public class RecursiveDescentParsing {

    private int num = 0;


    /**
     * 利用递归下降法做语法分析
     *
     * @param expr 表达式
     * @return 是 符合语法，否 不符合语法
     */
    public boolean rdp(String expr) {
        num = 0;
        boolean isAdopt = false;
        try {
            parse_E(expr);
            isAdopt = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isAdopt;
    }

    /**
     * 把左递归改为右递归后 E 产生式对于的方法
     *
     * @param expr 表达式
     * @throws Exception current position need operator
     */
    private void parse_E(String expr) throws Exception {
        parse_T(expr);
        if (num < expr.length()) {
            char c = expr.charAt(num);
            if (c == '+') {
                ++num;
                parse_E(expr);
            }
        }
        if (num != expr.length())
            throw new Exception(num + " position need operator!");
    }

    /**
     * 把左递归改为右递归后 T 产生式的方法
     *
     * @param expr 表达式
     */
    private void parse_T(String expr) throws Exception {
        parse_F(expr);
        if (num < expr.length()) {
            char c = expr.charAt(num);
            if (c == '*') {
                ++num;
                parse_T(expr);
            }
        }
    }

    /**
     * F 产生式的描述
     *
     * @param expr 表达式
     * @throws Exception
     */
    private void parse_F(String expr) throws Exception {
        if (num < expr.length()) {
            char c = expr.charAt(num);
            if (c >= '0' && c <= '9')
                ++num;
            else
                throw new Exception("'" + num + "' position should be a num!");
        } else
            throw new Exception("the end of '" + expr.charAt(num - 1) + "' need a num!");
    }

    public static void main(String[] args) {
        //测试 1+2_4 会抛出“第3个位置需要一个运算符”的异常
        RecursiveDescentParsing srp = new RecursiveDescentParsing();
        boolean result = srp.rdp("1+2_4");
        System.out.println(result);
    }
}
