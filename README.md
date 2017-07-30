# CompilingPrinciple

1. lex 文件夹

> 此文件夹有 3 个 *Java类* ，这三个类的终极目标是实现:自动生成任意语言的 **词法分析器** 。就像 **lex/flex**。水平有限只粗糙实现了核心算法以作练习。

- `Thompson.java` 实现：**Re(正则表达式)** --> **NFA**，使用的是 `Thompson算法`；
- `SubsetConstruction.java` 实现： **NFA** --> **DFA**，使用的是 `子集构造算法`；
- `Hopcroft.java` 实现：**优化DFA**，使用的是 `Hopcroft算法`；
- 最后的一个 *表驱动算法* ，由于对于输入、输出没有确定，没有写完，故没有粘贴。

2. flexTool 文件夹

> 此文件夹
