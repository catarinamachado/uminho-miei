#ifndef coordinateFrame_h
#define coordinateFrame_h

#include "../common/point.h"

typedef struct frame *CoordinateFrame;

CoordinateFrame mkCoordinateFrame();

CoordinateFrame mkCoordinateFrame(CoordinateFrame mold);

CoordinateFrame mkCoordinateFrame(const char * filename);

void unmkCoordinateFrame(CoordinateFrame m);

void frameRotate(CoordinateFrame m, float angle, float vx, float vy, float vz);

void frameTranslate(CoordinateFrame m, float x, float y, float z);

void frameScale(CoordinateFrame m, float vx, float vy, float vz );

void framePoint(CoordinateFrame m, float x, float y, float z);

void frameTriangle(CoordinateFrame m, float angle, float difs);

void frameTrace(CoordinateFrame m, char* filename, char* figure);

#endif