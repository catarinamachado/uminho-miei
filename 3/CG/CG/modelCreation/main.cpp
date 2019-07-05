#include <math.h>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <iostream>

#if defined(_WIN32)
    #include "GLUT/glut.h"
#else
    #include <GLUT/glut.h>
#include <iostream>

#endif

#include "coordinateFrame.h"
#include "framePrimitives.h"
#include "bazierPatch.h"

float lin(float dh){
    return (1 - dh);
}

float smartshephere(float dh){
	return sqrt( 1 - pow(1 - 2*dh,2) + 1e-7);
}

void sphere(CoordinateFrame reference, float radius, int slices, int stacks) {
	CoordinateFrame nw = mkCoordinateFrame(reference);

	frameScale(nw, radius, 2 * radius, radius);
    frameTranslate(nw, 0.0, -0.5, 0.0);
	frameStacker(nw,slices,stacks,smartshephere);

	unmkCoordinateFrame(nw);
}

void cone(CoordinateFrame reference, float radius, float height, int slices, int stacks) {
    CoordinateFrame nw = mkCoordinateFrame(reference);
    frameScale(nw, radius, height, radius);
    frameStacker(nw, slices, stacks, lin);
    unmkCoordinateFrame(nw);
}

void box(CoordinateFrame reference, int dx, int dy, int dz, int divisions) {
    CoordinateFrame nw = mkCoordinateFrame(reference);

    frameScale(reference,dx,dy,dz);
    frameCube(reference,divisions);

    unmkCoordinateFrame(nw);
}


void notEnoughArguments() {
    std::cout << "Not enough arguments";
    exit(1);
}

char * createBox(int argc, char **argv, CoordinateFrame reference) {
    // x , y , z , divisions(optional)
    if(argc < 6) notEnoughArguments();
    if(argc == 6) {
        box(reference, atof(argv[2]),atof(argv[3]),atof(argv[4]),1);
        return argv[5];
    } else {
        box(reference, atof(argv[2]),atof(argv[3]),atof(argv[4]),atoi(argv[5]));
        return argv[6];
    }
}

char * createSphere(int argc, char **argv, CoordinateFrame reference) {
    if(argc < 5) notEnoughArguments();
    //radius, slices, stacks
    sphere(reference, atof(argv[2]), atoi(argv[3]), atoi(argv[4]));
    return argv[5];
}

char * createCone(int argc, char **argv, CoordinateFrame reference) {
    if(argc < 6) notEnoughArguments();
    //bottom radius, height, slices and stacks
    cone(reference, atof(argv[2]), atof(argv[3]), atoi(argv[4]), atoi(argv[5]));
    return argv[6];
}

char * createBazierSurface(int argc, char **argv, CoordinateFrame reference){
    if(argc < 4) notEnoughArguments();

    FILE* fdes = fopen(argv[2],"r");

    if( fdes == NULL){
        std::cout << "Patch file not found";
        exit(2);
    }

    BazierPatch patch = mkBazierPatch(fdes);
    fclose(fdes);

    Point * points;
    int tesselation = atoi(argv[3]);
    
    for(int i = 0; i < getNumPatches(patch) ; i++){
        points = getPatch(patch,i);
        frameBazierPatch(reference, points, tesselation);
        free(points);
    }

    unmkBazierPatch(patch);
    return argv[4];
}

char * parseInput(int argc, char **argv, CoordinateFrame reference) {
    if(strcmp(argv[1], "plane") == 0) {
        frameHyperplane(reference,1);
        return argv[2];
    } else if(strcmp(argv[1], "box") == 0) {
        return createBox(argc, argv, reference);
    } else if(strcmp(argv[1], "sphere") == 0) {
        return createSphere(argc, argv, reference);
    } else if(strcmp(argv[1], "cone") == 0) {
        return createCone(argc, argv, reference);
    } else if(!strcmp(argv[1],"bazier")){
        return createBazierSurface(argc, argv,reference);
    } else {
        std::cout << "Unknown model";
        exit(2);
    }
}

int main(int argc, char **argv) {
    if(argc == 1) notEnoughArguments();

    CoordinateFrame reference = mkCoordinateFrame();
    
    char * filename = parseInput(argc, argv, reference);
    
    frameTrace(reference, filename, "figure");
    unmkCoordinateFrame(reference);

    return 0;
}
