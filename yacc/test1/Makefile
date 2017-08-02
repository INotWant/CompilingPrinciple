run:	name
	./name

name:	y.tab.h y.tab.c lex.yy.c
	gcc -o name y.tab.c lex.yy.c

y.tab.h y.tab.c y.output:	name.y
	bison -vdy name.y

lex.yy.c:	name.l
	flex	name.l
