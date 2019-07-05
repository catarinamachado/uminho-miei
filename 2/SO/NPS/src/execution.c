// SPDX-License-Identifier: MIT

#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/wait.h>
#include <string.h>
#include <stdio.h>

#include "execution.h"

int execute_cmd(Notebook nb, int i)
{

	int stream_size, n_spaces, p_size, size, status, prev_cmd, dependent;
	int pd[2], stderr[2], pipeline[2];

	stream_size = 100;
	status = prev_cmd = p_size = 0;

	char stream[stream_size];
	char cmd_result[stream_size];

	pipe(pd);
	pipe(stderr);

	if (!fork()) {
		int cmd_pipe[2];

		pipe(pipeline);

		if (nb->commands[i][0] == '|') {
			nb->commands[i] += 1;
			write(
				pipeline[1],
				nb->results[i-1],
				strlen(nb->results[i-1])
			);

			close(pipeline[1]);

		} else if (nb->commands[i][0] >= '0'
			&& nb->commands[i][0] <= '9') {

			char num[12];

			status = sscanf(nb->commands[i], "%d|", &prev_cmd);
			sprintf(num, "%d", prev_cmd);

			nb->commands[i] += strlen(num) + 1;
			write(
				pipeline[1],
				nb->results[i-prev_cmd],
				strlen(nb->results[i-prev_cmd])
			);

			close(pipeline[1]);
		}

		int cmd = 0;

		for (char *pch = strtok(nb->commands[i], "|"); pch;
			pch = strtok(NULL, "|")) {

			if (cmd == 0)
				dup2(pipeline[0], 0);
			else
				dup2(cmd_pipe[0], 0);

			pipe(cmd_pipe);

			if (!fork()) {
				char **res  = NULL;

				n_spaces = 0;

				for (char *p = strtok(pch, " "); p;
					p = strtok(NULL, " ")) {

					res = realloc(res,
						sizeof(char *) * (n_spaces+1));
					res[n_spaces++] = p;
				}

				res = realloc(res,
					sizeof(char *) * (n_spaces+1));
				res[n_spaces] = 0;

				dup2(cmd_pipe[1], 1);
				dup2(stderr[1], 2);

				execvp(res[0], res);
				_exit(1);
			}

			wait(&status);
			close(cmd_pipe[1]);
			cmd++;

			if (WEXITSTATUS(status))
				_exit(1);
		}

		while ((size = read(cmd_pipe[0], cmd_result, stream_size)) > 0)
			write(pd[1], cmd_result, size);

		_exit(0);
	}

	wait(&status);
	close(pd[1]);
	close(stderr[1]);

	if (WEXITSTATUS(status) || read(stderr[0], stream, 1) > 0) {
		nb->n_results = i;
		return 1;
	}

	while ((size = read(pd[0], stream, stream_size)) > 0) {
		nb->results[i] = realloc(
			nb->results[i],
			sizeof(char) * (size + p_size + 1)
			);

		strncat(nb->results[i], stream, size);
		strcat(nb->results[i], "\0");

		p_size += size;
	}

	for (int a = 0; a < nb->size_dependents[i] ; a++) {
		dependent = nb->dependents[i][a];
		nb->flag[dependent] = 1;
	}

	return 0;
}

int execute(Notebook nb)
{

	int i;

	nb->results = malloc(sizeof(char *) * nb->n_commands);

	for (i = 0; i < nb->n_commands; i++)
		nb->results[i] = NULL;

	for (i = 0; i < nb->n_commands; i++)
		if (execute_cmd(nb, i)) {
			nb->n_results = i;
			return 1;
		}

	nb->n_results = i;
	return 0;
}
