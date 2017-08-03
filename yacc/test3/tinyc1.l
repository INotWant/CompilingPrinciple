%{
#define YYSYTYPE char *
#include "y.tab.h"
int cur_line = 1;
void yyerror(const char* msg);
void unrecognized_char(char c);
#define _COPY { yylval = strdup(yytext); }
%}

operator 	([-/+*()=,;])
identifier	([_a-zA-Z][_a-zA-Z0-9]*)
num		([0-9]+)
blankChar	([ \t]*)
string		(\042[^\042\n]*\042)

%%

"print"		{ return T_Printf; }		/* 需要放到前面 */
"int"		{ return T_int; }

\n		{ cur_line++; }
{blankChar}	{ /* empty */ }
{operator}	{ return yytext[0]; }
{identifier}	{ _COPY; return T_identifier; }
{num}		{ _COPY; return T_num; }
{string}	{ _COPY; return T_string; }
.               { unrecognized_char(yytext[0]); }

%%

int yywrap(void){
	return 1;
}

void unrecognized_char(char c) {
    char buf[32] = "Unrecognized character: ?";
    buf[24] = c;
    yyerror(buf);
}

void yyerror(const char* msg){
	printf("Error at line %d:\n\t%s\n",cur_line,msg);
	exit(-1);
}
