PRODUCT := nps

CC := gcc
LINKER := gcc
CFLAGS := -Wall -Wextra -std=c11 -g

SRCDIR := src
INCDIRS := -I include
OBJDIR := obj
TESTDIR := test

SRCFILES := $(wildcard $(SRCDIR)/*.c)
OBJFILES := $(patsubst $(SRCDIR)/%.c,$(OBJDIR)/%.o,$(SRCFILES))

all: dir $(PRODUCT)

dir:
	mkdir -p $(OBJDIR)

$(PRODUCT): $(OBJFILES)
	$(LINKER) $^ -o $@

$(OBJDIR)/%.o: $(SRCDIR)/%.c
	$(CC) $(CFLAGS) $(INCDIRS) -c $< -o $@

pdf:
	cd tex; \
		pdflatex relatorio.tex; \
		rm relatorio.aux relatorio.log relatorio.out relatorio.toc

remove tests: FORCE
	rm -rf $(TESTDIR)/input/*.test

FORCE:

clean:
	rm -rf $(OBJDIR)
	rm -rf $(TESTDIR)/input/*.test
	rm $(PRODUCT)
