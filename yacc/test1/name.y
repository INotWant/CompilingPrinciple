%{
#include <stdio.h>
%}

%token NAME EQ AGE

%%

file	  : 	record file
	      | 	record
	      ;
record  :	  NAME EQ AGE	{printf("%s is %s years old!",$1,$3);}
	      ;

%%

int main(){
	yyparse();
	return 0;
}

void yyerror(char* msg){
	printf("Error encountered: %s \n",msg);
}
