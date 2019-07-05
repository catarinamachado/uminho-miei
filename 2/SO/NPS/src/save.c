// SPDX-License-Identifier: MIT

#include <unistd.h>
#include <fcntl.h>
#include <sys/wait.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>

#include "save.h"

void save(Notebook nb)
{

	int stream_size = 100;
	int notebook = open(nb->path, O_WRONLY | O_TRUNC, 0644);

	int tw_size = 0;
	char *to_write = NULL;

	int i, init = 0, end = 0, actual_command = 0;

	for (i = 0; i < nb->size_text; i++) {
		if (nb->text[i] == '$') {
			end = i;
			while (nb->text[end] != '\n' && end < nb->size_text)
				end++;

			to_write = realloc(
				to_write,
				tw_size + end - init +
					strlen(nb->results[actual_command]) + 8
				);

			strncat(to_write + tw_size, nb->text + init,
				end - init);

			strcat(to_write + tw_size + end - init, "\n>>>\n");

			strncat(to_write + tw_size + end - init + 5,
				nb->results[actual_command],
				strlen(nb->results[actual_command]));

			strcat(to_write + tw_size + end - init + 5 +
				strlen(nb->results[actual_command]), "<<<");

			tw_size += end - init +
				strlen(nb->results[actual_command++]) + 8;

			if (!strncmp(nb->text + end + 1, ">>>", 3)) {
				while (strncmp(nb->text + end, "<<<", 3))
					end++;
				end += 3;
			}

			init = end;
			i = end;
		}
	}

	to_write = realloc(to_write, tw_size + nb->size_text - init);
	strncat(to_write + tw_size, nb->text + init, nb->size_text - init);
	tw_size += nb->size_text - init;

	i = 0;
	while (tw_size > stream_size) {
		write(notebook, to_write + i, stream_size);
		i += stream_size;
		tw_size -= stream_size;
	}
	write(notebook, to_write + i, tw_size);

	close(notebook);
}
