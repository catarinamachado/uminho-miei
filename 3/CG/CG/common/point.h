#ifndef point_h
#define point_h

typedef struct point { float p[3]; } Point;

Point* mkPoint(float x, float y, float z);
void unmkPoint(Point* p);

#endif
