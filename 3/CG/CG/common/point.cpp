#include "point.h"
#include <stdlib.h>
#include <cmath>

Point* mkPoint(float x, float y, float z){
	Point* m = (Point*) malloc( sizeof(struct point) );
	m->p[0] = x;
	m->p[1] = y;
	m->p[2] = z;
	return m;
}

void unmkPoint(Point* p){
	free(p);
}