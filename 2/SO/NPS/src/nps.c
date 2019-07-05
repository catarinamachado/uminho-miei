// SPDX-License-Identifier: MIT

#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <unistd.h>

#include "parse.h"
#include "save.h"
#include "execution.h"
#include "struct.h"

void nps(const char *path, int testing)
{

	int error;

	Notebook nb = malloc(sizeof(notebook));

	nb->path = path;
	nb->text = malloc(sizeof(char *));

	nb->commands = NULL;
	nb->n_commands = 0;

	nb->results = NULL;
	nb->n_results = 0;

	parse(nb);

	error = execute(nb);

	if (!error)
		save(nb);
	else if (!testing) {
		char *error_message = malloc(sizeof(char) * 22);

		sprintf(error_message,
			"Error executing file.\n");

		write(2, error_message, strlen(error_message));

		free(error_message);
	}

	for (int i = 0; i < nb->n_commands; i++) {
		free(nb->commands[i]);
		free(nb->dependents[i]);
	}

	for (int i = 0; i < nb->n_results; i++)
		free(nb->results[i]);

	free(nb->text);
	free(nb->commands);
	free(nb->results);
	free(nb->size_dependents);
	free(nb->flag);
	free(nb->dependents);
	free(nb);
}
