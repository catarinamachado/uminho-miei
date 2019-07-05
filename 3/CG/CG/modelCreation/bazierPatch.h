#ifndef bazierPatch_h
#define bazierPatch_h

#include <stdio.h>
#include "../common/point.h"

typedef struct patch *BazierPatch;

BazierPatch mkBazierPatch( FILE* source );

void unmkBazierPatch(BazierPatch m);

int getNumPatches(BazierPatch bazier);

Point * getPatch(BazierPatch bazier, int ind);

#endif
