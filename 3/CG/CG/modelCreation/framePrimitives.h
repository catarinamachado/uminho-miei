#ifndef primitive_h
#define primitive_h

#import "coordinateFrame.h"

void frameStacker(CoordinateFrame reference, int points, int stacks, float (*f)(float) );
void frameHyperplane(CoordinateFrame reference, int divisions);
void frameCube(CoordinateFrame reference, int divisions);
void frameRegularPolygon(CoordinateFrame reference, int points);
void frameBazierPatch(CoordinateFrame reference, Point * points, int tesselation);

#endif
