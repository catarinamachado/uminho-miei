#include "bazierPatch.h"
#include <stdlib.h>
#include <vector>
#include <list>
#include <array>

using namespace std;

typedef struct patch {
    int numPatches;
    int ** indices;
    int numCtrlPoints; 
    Point * ctrlPoints;
}*BazierPatch;

BazierPatch mkBazierPatch( FILE* source );
void unmkBazierPatch(BazierPatch m);

BazierPatch mkBazierPatch( FILE* source ){
    /* parse filetype::
    degree(n,m) :- (n+1)(m+1) control points. 
    bicubic patches :- degree(3,3)

    # number of patches 
    [indice]*
    # number of control points
    [points]*
    
    */
    BazierPatch patch = (BazierPatch)malloc(sizeof(struct patch));
    fscanf(source,"%d",&(patch->numPatches));

    int ** v = (int**)malloc(sizeof(int*)*patch->numPatches);


    for( int i = 0; i<patch->numPatches; i++){

        v[i] = (int*)malloc( sizeof(int) * 16 );
        int tmp;
        
        for( int j=0; j<(3+1)*(3+1)-1; j++){
            fscanf(source, "%d, ",&tmp);

            v[i][j] = tmp;

        }

        fscanf(source,"%d",&tmp);
        
        v[i][15] = tmp;        

    }

    patch->indices = v;

    fscanf(source,"%d",&(patch->numCtrlPoints));

    patch->ctrlPoints = (Point*) malloc( sizeof(Point) * patch->numCtrlPoints );

    for( int i = 0; i< patch->numCtrlPoints; i++ ){
        float pv[3];
        float tmp1;
        for(int j = 0 ; j < 3 - 1; j++){
            fscanf(source,"%f, ",&tmp1);
            pv[j] = tmp1;    

        }

        fscanf(source,"%f",&tmp1);
        pv[2] = tmp1; 

        Point* point =  mkPoint(pv[0],pv[1],pv[2]);

        patch->ctrlPoints[i] = *point;

        unmkPoint(point);

    }
    return patch;
}

void unmkBazierPatch(BazierPatch m){
    for(int i=0; i< m->numPatches; i++)
        free(m->indices[i]);

    free(m->indices);
    free(m->ctrlPoints);
    free(m);
}

Point * getPatch(BazierPatch bazier, int ind){
    
    Point * r = (Point*)malloc(sizeof(Point) * 16);
    for(int i= 0; i< 16; i++)
        r[i] = bazier->ctrlPoints[bazier->indices[ind][i]];

    return r;
}

int getNumPatches(BazierPatch bazier){

    return bazier->numPatches;
}
