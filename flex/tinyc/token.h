#ifndef TOKEN_H
#define TOKEN_H

typedef enum{
	T_e = 256, T_ne, T_ge, T_le, T_and, T_or, T_string, T_char, T_void, T_int, T_while, T_if, T_else, T_return, T_break, T_continue, T_print, T_readint, T_Num, T_Word
} TokenType;

static void print_token(int token){
	static  char* tokens[] = {"T_e","T_ne","T_ge","T_le","T_and","T_or","T_string","T_char","T_void","T_int","T_while","T_if","T_else","T_return","T_break","T_continue","T_print","T_readint","T_num","T_word",};
	if(token < 256)
		printf("%-20c",token);
	else
		printf("%-20s",tokens[token-256]);
}

#endif
