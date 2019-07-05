// SPDX-License-Identifier: MIT

#include <unistd.h>
#include <fcntl.h>
#include <string.h>
#include <stdlib.h>
#include <stdio.h>

#include "parse.h"

int get_text(Notebook nb)
{

	int stream_size = 100, read_size = 0, size = 0;
	char stream[stream_size];

	int notebook = open(nb->path, O_RDONLY);

	while ((read_size = read(notebook, stream, stream_size)) > 0) {
		nb->text = realloc(nb->text,
			sizeof(char) * (size + read_size + 1));
		strncpy(nb->text + size, stream, read_size);
		size += read_size;
	}
	nb->text[size] = 0;

	close(notebook);

	return size;
}

void parse(Notebook nb)
{

	int i, e, prev_cmd, commands_size = 10;
	int text_size = nb->size_text = get_text(nb);
	char *texto = nb->text;

	nb->commands = malloc(sizeof(char *) * commands_size);
	nb->flag = malloc(sizeof(int) * commands_size);
	nb->dependents = malloc(sizeof(int) * commands_size);
	nb->size_dependents = malloc(sizeof(int) * commands_size);

	for (i = 0; i < text_size; i++) {
		if (texto[i] == '$') {
			i++;

			e = i;
			while (texto[e] != '\n' && e < text_size)
				e++;

			if (commands_size >= nb->n_commands) {
				commands_size *= 2;

				nb->commands = realloc(
					nb->commands,
					sizeof(char *) * commands_size
					);

				nb->flag = realloc(
					nb->flag,
					sizeof(int) * commands_size
					);

				nb->dependents = realloc(
					nb->dependents,
					sizeof(int) * commands_size
					);
			}

			nb->flag[nb->n_commands] = 1;
			nb->dependents[nb->n_commands] = malloc(sizeof(int));
			nb->size_dependents[nb->n_commands] = 0;

			if (texto[i] != ' ') {
				if  (texto[i] == '|')
					prev_cmd = 1;
				else if (texto[i] >= '0' && texto[i] <= '9')
					sscanf(&(texto[i]), "%d|", &prev_cmd);

				nb->flag[nb->n_commands] = 0;

				nb->dependents[nb->n_commands-prev_cmd] =
					realloc(nb->dependents
						[nb->n_commands-prev_cmd],
						sizeof(int) *
						(nb->size_dependents
						[nb->n_commands-prev_cmd] + 2)
					);

				nb->dependents
					[nb->n_commands-prev_cmd]
					[(nb->size_dependents
						[nb->n_commands-prev_cmd])++]
					= nb->n_commands;
			}

			nb->commands[nb->n_commands]
				= malloc(sizeof(char) * (e - i));
			strncpy(nb->commands[nb->n_commands], texto + i, e - i);
			strcpy(nb->commands[(nb->n_commands)++] + e - i, "\0");

			i = e;
		}
	}
}
