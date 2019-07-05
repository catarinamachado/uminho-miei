#ifndef branch_h
#define branch_h

typedef unsigned char BranchType;
#define EMPTY 0
#define FAKE 1
#define ANIMATION 2
#define TRANSFORMATION 3
#define MODEL 4

typedef unsigned char AnimationType;
#define CURVED_TRANSLATION 9
#define ROTATION 8

typedef struct branch{
    BranchType type;
    void * node;
} *Branch;

typedef struct transformation *Transformation;
typedef struct animation *Animation;
typedef struct model *Model;

#include "assembler.h"
#include <vector>
#include "../common/point.h"
#include "../common/matop.h"

Branch mkBranch( Transformation t );
Branch mkBranch( Animation a );
Branch mkBranch( Model m );

void unmkBranch( struct branch b );
void unmkBranch( Branch b );

void addDescendent( Animation ani, Branch descendent );
void addDescendent( Transformation t, Branch descendent );

Transformation mkTransformation(Mat mat);
void unmkTransformation( Transformation t);
void assemblerTransformate( Assembler ass, Mat mat);

Model mkModel(long start, long end);
void unmkModel(Model model);
void assemblerModelate( Assembler ass, float x, float y, float z);

Animation mkAnimation(int period, std::vector<Point> * controlpoints, AnimationType type);
void unmkAnimation(Animation ani);
void assemblerAnimate( Assembler ass, int period, std::vector<Point> * controlpoints, AnimationType type );

void branchInterpret(Branch b, std::vector<Point>* inpoints, Point * outpoints, int time);

void branchBufferData(Branch root);
void branchOptimize(std::vector<Point> * points , Branch b);
void branchDraw(Branch root, int time);

#endif