%{
#include <stdio.h>
#include <stdlib.h>
void yyerror(const char*);
#define YYSTYPE char *
%}

%token T_string T_int T_identifier T_Printf T_num

%left '+' '-'
%left '*' '/'
%right U_neg

%%

S	:	St				
	|	S St
	;

St	:	Declare ';'			{ printf("\n\n"); }
	|	Assign
	|	Print
	;

Declare	:	T_int T_identifier		{ printf("var %s", $2); }
	|	Declare ',' T_identifier	{ printf(", %s", $3); }
	;

Assign	:	T_identifier '=' E ';'		{ printf("pop %s\n\n", $1); }
	;

Print	:	T_Printf '(' T_string Actuals ')' ';'	{ printf("print %s\n\n", $3); }
	;

Actuals	:	/* empty */
	|   	Actuals ',' E
	;

E	:	E '+' E			{ printf("add\n"); }
	|   	E '-' E                 { printf("sub\n"); }
	|   	E '*' E                 { printf("mul\n"); }
	|   	E '/' E                 { printf("div\n"); }
	|   	'-' E %prec U_neg       { printf("neg\n"); }
	|   	T_num		       	{ printf("push %s\n", $1); }
	|   	T_identifier            { printf("push %s\n", $1); }
	|   	'(' E ')'
	;

%%

int main(){
	return yyparse();
}
