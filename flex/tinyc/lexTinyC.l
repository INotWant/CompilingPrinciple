%{
#include "token.h"
int currentLineNum = 1;
void init();
void error(char,int);
%}

Operator	([+*-/%=,;!<>(){}\[\]])
Word		[_a-zA-Z][_a-zA-Z0-9]*
Num		[0-9]+
String		(\042[^\042\n]*\042)
String_Error	(\042[^\042\n]*)
Char		(\047[^\047\n]*\047)
Char_Error	(\047[^\047\n]*)
SINGLE_COMMENT1     ("//"[^\n]*)
SINGLE_COMMENT2     ("#"[^\n]*)

%%

[\n]            currentLineNum++;
[ \t\r\a]+          { /* ignore all spaces */               }
{SINGLE_COMMENT1}   { /* skip for single line comment */    }
{SINGLE_COMMENT2}   { /* skip for single line commnet */    }

{Operator}	return yytext[0];

"=="		return T_e;
"!="		return T_ne;
">="		return T_ge;
"<="		return T_le;
"&&"		return T_and;
"||"		return T_or;

{String}	{return T_string;}
{Char}		{return T_char ;}
{String_Error}	{error(yytext[0],currentLineNum); return -1;}
{Char_Error}	{error(yytext[0],currentLineNum); return -2;}

"void"		return T_void;
"int"		return T_int;
"while"		return T_while;
"if"		return T_if;
"else"		return T_else;
"return"	return T_return;
"break"		return T_break;
"continue"	return T_continue;
"print"		return T_print;
"readint"	return T_readint;

{Num}		return T_Num;
{Word}		return T_Word;

<<EOF>>		return 0;
%%


int main(int argc,char* argv[]){
	int type;
	init();
	while(type = yylex()){
		if(type > 0){
			print_token(type);
			printf("%s",yytext);		
		}
		printf("\n");
	}
	return 0;
}

void error(char c,int currentLineNum){
	printf("In %-d row, %c should pair up!",currentLineNum,c);
}

void init() {
    printf("%-20s%s\n", "TOKEN-TYPE", "TOKEN-VALUE");
    printf("-------------------------------------------------\n");
}

int yywrap(){
	return 1;
}
