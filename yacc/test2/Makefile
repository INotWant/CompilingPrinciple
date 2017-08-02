run:	calc
	./calc

name:	y.tab.h y.tab.c lex.yy.c
	gcc -o calc y.tab.c lex.yy.c

y.tab.h y.tab.c y.output:	calc.y
	bison -vdy calc.y

lex.yy.c:	calc.l
	flex	calc.l
