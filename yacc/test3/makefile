run:	tinyc1
	./tinyc1

tinyc1:	y.tab.h y.tab.c lex.yy.c
	gcc -o tinyc1 y.tab.c lex.yy.c

y.tab.h y.tab.c y.output:	tinyc1.y
	bison -vdy tinyc1.y

lex.yy.c:	tinyc1.l
	flex	tinyc1.l
