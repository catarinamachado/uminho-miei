// SPDX-License-Identifier: MIT

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include "tests.h"
#include "nps.h"

int main(int argc, char const *argv[])
{

	char *error_message;

	if (argc != 2) {
		char num[12];

		sprintf(num, "%d", argc);

		error_message = malloc(sizeof(char) * (30 + strlen(num)));
		sprintf(error_message,
			"Invalid number of arguments: %d\n", argc);

		write(2, error_message, strlen(error_message));

		free(error_message);
		exit(1);
	}

	if (!strcmp(argv[1], "test")) {
		chdir("test/test_dir");
		int r = tests();

		if (r)
			exit(2);
		else
			write(1, "All Tests Correct\n", 18);
	}

	else {
		if (access(argv[1], F_OK) == -1) {
			char *error_message = malloc(sizeof(char) * 21);

			sprintf(error_message,
				"File %s doesn't exist.\n", argv[1]);

			write(2, error_message, strlen(error_message));

			free(error_message);
		} else
			nps(argv[1], 0);
	}

	return 0;
}
