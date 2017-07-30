run:
	./lexTinyC < tinyc.c > result.txt

lexTinyC: lex.yy.c token.h
	gcc -o $@ $<

lex.yy.c: lexTinyC.l
	flex $<
