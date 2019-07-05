// SPDX-License-Identifier: MIT

#ifndef __STRUCT_H__
#define __STRUCT_H__

typedef struct notebook {
        const char *path;

        char *text;
        int size_text;

        char **commands;
        int n_commands;

        char **results;
        int n_results;

        int **dependents;
        int *size_dependents;
        int *flag;
} notebook;

typedef struct notebook *Notebook;

#endif
