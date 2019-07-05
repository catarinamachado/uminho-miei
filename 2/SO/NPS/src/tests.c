// SPDX-License-Identifier: MIT

#include <unistd.h>
#include <sys/wait.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include <nps.h>

int verify(char *file1, char *file2)
{

	char c[1];
	int status, r, pd1[2], pd2[2];

	pipe(pd1);
	pipe(pd2);

	if (!fork()) {
		dup2(pd1[0], 0);
		close(pd1[1]);
		dup2(pd2[1], 1);

		execlp("wc", "wc", "-l", NULL);

		_exit(1);
	}

	if (!fork()) {
		dup2(pd1[1], 1);

		execlp("diff", "diff", file1, file2, NULL);

		_exit(2);
	}

	close(pd1[1]);
	wait(&status);

	if (WEXITSTATUS(status))
		return 1;

	read(pd2[0], c, 1);
	close(pd2[1]);

	wait(&status);

	if (WEXITSTATUS(status))
		return 1;

	r = atoi(c);

	return r;
}

int test_one(char *file)
{

	int r, len = strlen(file);

	char *input, *test, *output, *message;

	input = malloc(sizeof(char) * (len + 13));
	sprintf(input, "../input/%s.nb%c", file, '\0');

	test = malloc(sizeof(char) * (len + 18));
	sprintf(test, "../input/%s.nb.test%c", file, '\0');

	output = malloc(sizeof(char) * (len + 14));
	sprintf(output, "../output/%s.nb%c", file, '\0');

	if (!fork())
		execlp("cp", "cp", input, test, NULL);

	wait(NULL);

	nps(test, 1);
	r = verify(test, output);

	if (r) {
		message = malloc(sizeof(char) * (59 + 2 * len));
		sprintf(message,
			"Test failed for %s.nb - Check test/input/%s.nb.test for result\n",
			file, file
		);
		write(2, message, strlen(message));
		free(message);
	} else {
		message = malloc(sizeof(char) * (21 + len));
		sprintf(message, "Test correct for %s.nb\n", file);
		write(1, message, strlen(message));
		free(message);

		if (!fork())
			execlp("rm", "rm", test, NULL);

		wait(NULL);
	}

	free(input);
	free(test);
	free(output);

	return r;
}

int tests(void)
{

	int r = 0;

	r += test_one("exec");

	r += test_one("empty");

	r += test_one("text");

	r += test_one("erros");

	r += test_one("stderr");

	return r;
}
