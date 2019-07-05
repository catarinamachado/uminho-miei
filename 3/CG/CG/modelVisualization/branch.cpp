#if defined(_WIN32)
#include "GL/glut.h"
    #include "GL/glew.h"
#else
#include <GLUT/glut.h>
#endif

#include "branch.h"
#include "assembler.h"
#include "../common/point.h"
#include "../common/matop.h"
#include <vector>
#include <utility>
#include <fstream>
#include <sstream>
#include <string>
#include <cmath>
#include <list>

using namespace std;

#define max(A,B) (A > B) ? A : B;
#define min(A,B) (A < B) ? A : B;

typedef struct animation{
    vector< Branch > * subbranch;
    int period;
    vector< Point > * auxpoints;
    AnimationType type;
} * Animation;

typedef struct model{
    long starti;/* including */
    long endi;/* not including */
    GLuint buffer;
    vector< Point > * points;
} *Model;

typedef struct transformation{
    vector< Branch > * subbranch;
    Mat mat;
} * Transformation;

/* Transformation*/
Transformation mkTransformation(Mat mat){
    Transformation t = (Transformation)malloc( sizeof(struct transformation) );
    t->subbranch = new vector<Branch>();
    t->mat = mat;
    return t;
}

void unmkTransformation( Transformation t){
    for( Branch b : *(t->subbranch) )
        unmkBranch(b);

    delete t->subbranch;

    freeMat(t->mat);
}

void assemblerTransformate( Assembler ass, Mat mat){

    switch( view(ass)->type ){
        case EMPTY: {
            view(ass)->node = mkTransformation(mat);
            view(ass)->type = TRANSFORMATION;
            break;
        }

        case ANIMATION: {
            addDescendentAndGo(ass, mkBranch(mkTransformation(mat)));
            break;
        }

        case TRANSFORMATION: {
            Transformation tmp3 = (Transformation) view(ass)->node;
            Mat tmp2 = matmul(tmp3->mat, mat);
            freeMat(tmp3->mat);
            tmp3->mat = tmp2;
            break;
        }

        default: {
            printf("error: models must be the terminal nodes\n");
            exit(2);
        }
    }

}

void applyTransformation( Transformation transformation ){
    
    glPushMatrix();
    glMultMatrixf(getMat(transformation->mat));
}

void applyTransformation( Transformation transformation, Point* outgoing, long start, long end){
    
    Point point;
    float p[4];
    
    p[3] = 1;
    
    for( long s = start; s< end; s++ ){
        p[0] = outgoing[s].p[0];
        p[1] = outgoing[s].p[1];
        p[2] = outgoing[s].p[2];
        
        float*tmp = vecMul( transformation->mat ,p, 3);
        
        point.p[0] = tmp[0] ;
        point.p[1] = tmp[1];
        point.p[2] = tmp[2];
        
        outgoing[s] = point;
        free(tmp);
    }
}

/* Model */

Model mkModel(long start, long end){
    Model m = (Model)malloc( sizeof(struct model) );
    m->starti = start;
    m->endi = end;
    m->buffer = -1;
    m->points = new vector< Point >();

    return m;
}

void unmkModel(Model model){
    delete model->points;
    free(model);
}

void assemblerModelate( Assembler ass, float x, float y, float z){

    switch( view(ass)->type ){
        case EMPTY: {
            view(ass)->node = mkModel(assemblerNumberOfPoints(ass), assemblerNumberOfPoints(ass));
            view(ass)->type = MODEL;
            break;
        }

        case ANIMATION: {
            addDescendentAndGo(ass, mkBranch(mkModel(assemblerNumberOfPoints(ass), assemblerNumberOfPoints(ass))));
            break;
        }

        case TRANSFORMATION: {
            addDescendentAndGo(ass, mkBranch(mkModel(assemblerNumberOfPoints(ass), assemblerNumberOfPoints(ass))));
            break;
        }

        case MODEL : break;

        default : {
            printf("error: models must be the terminal nodes\n");
            exit(2);
        }
    }

    Model mo = (Model)view(ass)->node;

    Point p;
    p.p[0]=x;
    p.p[1]=y;
    p.p[2]=z;

    assemblerPoint(ass, p);
    mo->points->emplace_back(p);

    mo->endi++;
}

/* Animation */

Animation mkAnimation(int period, vector<Point> * controlpoints, AnimationType type){

    Animation ani = (Animation)malloc( sizeof(struct animation) );
    ani->type = type;
    ani->period = period;
    ani->auxpoints = controlpoints;
    ani->subbranch = new vector< Branch >();
    return ani;
}

void unmkAnimation(Animation ani){
    for( Branch b : *(ani->subbranch) )
        unmkBranch(b);

    delete ani->subbranch;
    delete ani->auxpoints;

    free(ani);
}


void assemblerAnimate( Assembler ass, int period, vector<Point> * controlpoints, AnimationType type ){

    switch( view(ass)->type ){
        case EMPTY: {
            view(ass)->node = mkAnimation(period, controlpoints, type);
            view(ass)->type = ANIMATION;
            break;
        }

        case ANIMATION: {
            addDescendentAndGo(ass, mkBranch(mkAnimation(period, controlpoints, type)));
            break;
        }

        case TRANSFORMATION: {
            addDescendentAndGo(ass, mkBranch(mkAnimation(period, controlpoints, type)));
            break;
        }

        default : {
            printf("error: models must be the terminal nodes\n");
            exit(2);
        }
    }
}

void applyRotationAnimation(int period, Point axis, int elapsedtime){
    float w = (float)elapsedtime / (float)period;
    
    Mat mat = matRotate(360*w,axis.p[0],axis.p[1],axis.p[2]);
    
    Transformation t = mkTransformation(mat);
    
    applyTransformation(t);
    
    unmkTransformation(t);
}

void getCatmullRomPoint(float t,Point p0, Point p1, Point p2, Point p3, float *pos, float *deriv) {
    // catmull-rom matrix
    Mat m = catMullMat();
    // Compute A = M * P
    float * a[3];
    float x[4] = { p0.p[0],p1.p[0],p2.p[0],p3.p[0] };
    float y[4] = { p0.p[1],p1.p[1],p2.p[1],p3.p[1] };
    float z[4] = { p0.p[2],p1.p[2],p2.p[2],p3.p[2] };

    a[0] = vecMul(m, x, 4);
    a[1] = vecMul(m, y, 4);
    a[2] = vecMul(m, z, 4);

    // Compute pos = T * A
    float tt[4] = { t*t*t, t*t, t, 1 };
    for (int i = 0; i < 3; i++) {
        pos[i]=0;
        for(int j=0; j<4; j++)
            pos[i] += tt[j] * a[i][j];
    }

    // Compute deriv = T' * A
    float ttt[4] = { 3*t*t, 2*t , 1, 0 };
    for (int i = 0; i < 3; i++) {
        deriv[i]=0;
        for(int j=0; j<4; j++)
            deriv[i] += ttt[j] * a[i][j];

    }

    free(a[0]);
    free(a[1]);
    free(a[2]);
}

void getGlobalCatmullRomPoint(float gt, float *pos, float *deriv, vector<Point> * axis) {
    long count = axis->size();
    float t = gt * count; // this is the real global t
    int index = floor(t);  // which segment
    t = t - index; // where within  the segment
    // indices store the points
    int indices[4];
    indices[0] = (index + count-1)%count;
    indices[1] = (indices[0]+1)%count;
    indices[2] = (indices[1]+1)%count;
    indices[3] = (indices[2]+1)%count;

    getCatmullRomPoint(t, axis->at(indices[0]), axis->at(indices[1]), axis->at(indices[2]), axis->at(indices[3]), pos, deriv);
}

void applyTranslationAnimation( int period, vector<Point> * axis, int elapsed_time ){
    float pos[3];
    float deriv[3];
    float gt = (float)(elapsed_time%period) / (float)period;
    
    Point norm = axis->at( axis->size()-1 );
    
    axis->pop_back();
    
    getGlobalCatmullRomPoint( gt, pos, deriv, axis);
    
    Mat p1 = matTranslate(pos[0], pos[1], pos[2]);
    Mat p2 = upsideMat(deriv, norm.p);
    
    Transformation t = mkTransformation(matmul(p1,p2));
    
    applyTransformation(t);
    
    freeMat(p1);
    freeMat(p2);
    
    unmkTransformation(t);
    /*
     float posi[3];
     float derivi[3];
     glBegin(GL_LINE_LOOP);
     for (float gti = 0; gti < 1; gti += 0.001) {
     getGlobalCatmullRomPoint(gti,posi,derivi,axis);
     glVertex3f(posi[0],posi[1],posi[2]);
     }
     glEnd();
     */
     axis->emplace_back(norm);
     
}

void applyAnimation( Animation animation, int time){
    switch( animation->type ){
            
        case ROTATION:
            applyRotationAnimation(animation->period, animation->auxpoints->at(0), time);
            break;
        case CURVED_TRANSLATION:
            applyTranslationAnimation(animation->period, animation->auxpoints,
                time);
            break;
    }
}

/* Branch */

void addDescendent( Animation ani, Branch descendent ){
    ani->subbranch->emplace_back(descendent);
}

void addDescendent( Transformation t, Branch descendent ){
    t->subbranch->emplace_back(descendent);
}

Branch mkBranch( Transformation t ){
    Branch b =  (Branch)malloc( sizeof(struct branch) );
    b->type = TRANSFORMATION;
    b->node = t;
    return b;
}

Branch mkBranch( Animation a ){
    Branch b = (Branch)malloc( sizeof(struct branch) );
    b->type = ANIMATION;
    b->node = a;
    return b;
}

Branch mkBranch( Model m ){
    Branch b = (Branch)malloc( sizeof(struct branch) );
    b->type = MODEL;
    b->node = m;
    return b;
}

void unmkBranch( struct branch b ){
    switch( b.type ){
        case EMPTY: {
            break;
        }
        case ANIMATION: {
            unmkAnimation((Animation) b.node);
            break;
        }
        case TRANSFORMATION: {
            unmkTransformation((Transformation) b.node);
            break;
        }
        case MODEL: {
            unmkModel((Model) b.node);
            break;
        }
    }
}

void unmkBranch( Branch b ){
    unmkBranch(*b);
    free(b);
}


void branchOptimizeTransf( Branch b ){
    switch( b->type ){
        case EMPTY: {
            break;
        }
        case ANIMATION: {
            Animation t = (Animation)b->node;
            for(long i=0; i < t->subbranch->size(); i++) {

                Branch tmpb = t->subbranch->at(i);

                branchOptimizeTransf(tmpb);

                if( tmpb->type == MODEL ){
                    Model moo = (Model)tmpb->node;
                    if(moo->endi == moo->starti){
                        //printf("chop\n");
                        /* eliminar este ramo*/
                        Branch* hidbuff = t->subbranch->data();
                        hidbuff[i] = hidbuff[ t->subbranch->size() - 1 ];
                        t->subbranch->pop_back();
                        unmkModel(moo);
                        free(tmpb);
                    }
                }
            }

            if( t->subbranch->empty() ){
                //printf("chop\n");
                /*tranformações que não afetam ninguêm são eliminadas*/
                unmkAnimation(t);
                b->type = MODEL;
                b->node = mkModel(0L,0L);
            }


            break;
        }
        case TRANSFORMATION: {
            Transformation t = (Transformation)b->node;

            for(long i=0; i < t->subbranch->size(); i++) {

                Branch tmpb = t->subbranch->at(i);

                branchOptimizeTransf(tmpb);

                if( tmpb->type == MODEL ){
                    Model moo = (Model)tmpb->node;
                    if(moo->endi == moo->starti){
                        //printf("chop\n");
                        /* eliminar este ramo*/
                        Branch* hidbuff = t->subbranch->data();
                        hidbuff[i] = hidbuff[ t->subbranch->size() - 1 ];
                        t->subbranch->pop_back();
                        unmkModel(moo);
                        free(tmpb);
                    }
                }
            }

            if( t->subbranch->size() == 1 && t->subbranch->at(0)->type == TRANSFORMATION ){
                /* transformações seguidas são compostas*/
                //printf("chop\n");
                Transformation ut = (Transformation) t->subbranch->at(0)->node;

                Mat nmat = matmul(t->mat, ut->mat); /* a transformação torna-se a composição das duas transformações*/

                freeMat(t->mat);/* apaga matrizes antigas*/
                freeMat(ut->mat);/* apaga matrizes antigas*/

                free(t->subbranch->at(0));/* apaga o ramo do filho*/

                delete t->subbranch;/* apaga a lista de filhos*/


                t->mat = nmat;/* a nova matriz é a composição*/
                t->subbranch = ut->subbranch;/* netos tornam-se filhos*/
                free(ut);/* apaga memória do filho*/

            }else if( t->subbranch->empty() ){
                //printf("chop\n");
                /*tranformações que não afetam ninguêm são eliminadas*/
                unmkTransformation(t);
                b->type = MODEL;
                b->node = mkModel(0L,0L);
            }

            break;
        }
        case MODEL: {
            break;
        }
    }
}

void branchOptimizeModels( vector<Point> * points, Branch b){
    switch( b->type ){
        case EMPTY: { break; }

        case ANIMATION: {
            Animation t = (Animation)b->node;

            bool allmodels = true;

            for( Branch nb : *(t->subbranch) ) {
                branchOptimizeModels(points, nb);
                allmodels = allmodels && (nb->type == MODEL);
            }

            if( allmodels ){ /* every descendent is a model*/

                Model nm = mkModel(points->size(),-1);

                for( long i =0; i < t->subbranch->size(); i++ ) { /* transforms the models and combines them*/
                    Model model = (Model)(t->subbranch->at(i)->node);
                    nm->starti = min(nm->starti,model->starti);
                    nm->endi = max(nm->endi,model->endi)
                            ;
                    nm->points->insert(nm->points->end(), model->points->begin(), model->points->end());// append data

                    unmkBranch(t->subbranch->at(i));
                }

                delete t->subbranch;

                t->subbranch = new vector<Branch>();
                t->subbranch->emplace_back(mkBranch(nm));

            }

            break;
        }

        case TRANSFORMATION: {
            Transformation t = (Transformation)b->node;

            bool allmodels = true;

            for( Branch nb : *(t->subbranch) ) {
                branchOptimizeModels(points, nb);
                allmodels = allmodels && (nb->type == MODEL);
            }

            if( allmodels ){ /* every descendent is a model*/

                Model nm = mkModel(points->size(),-1);

                for( long i =0; i < t->subbranch->size(); i++ ) { /* transforms the models and combines them*/
                    Model model = (Model)(t->subbranch->at(i)->node);
                    nm->starti = min(nm->starti,model->starti);
                    nm->endi = max(nm->endi,model->endi);

                    //applyTransformation(t, points->data(), model->starti, model->endi);

                    applyTransformation(t, model->points->data(), 0, model->points->size());
                    nm->points->insert(nm->points->end(), model->points->begin(), model->points->end());// append data

                    unmkBranch(t->subbranch->at(i));
                }

                delete t->subbranch;
                freeMat(t->mat);
                free(t);

                b->node = nm;
                b->type = MODEL;/*agregates everything in a single model.*/
            }

            break;
        }

        case MODEL: { break; }
    }
}

void branchOptimize(vector<Point> * points, Branch root){
    branchOptimizeTransf(root);
    branchOptimizeModels(points,root);

}

void branchBufferData(Branch root){
    switch( root->type ){
        case EMPTY: { break; }

        case ANIMATION: {
            Animation t = (Animation)root->node;

            for( Branch nb : *(t->subbranch) )
                branchBufferData(nb);

            break;
        }

        case TRANSFORMATION: {
            Transformation t = (Transformation)root->node;

            for( Branch nb : *(t->subbranch) ) {
                branchBufferData(nb);
            }

            break;
        }

        case MODEL: {
            Model m = (Model)root->node;

            m->points->shrink_to_fit();


            glGenBuffers(1, &(m->buffer) );
            glBindBuffer(GL_ARRAY_BUFFER, m->buffer);
            glBufferData(
                    GL_ARRAY_BUFFER,
                    m->points->size() * sizeof(Point),
                    &(m->points->at(0)),
                    GL_STATIC_DRAW
            );

            break;
        }
    }
}

void branchDraw(Branch root, int time){
    switch( root->type ){
        case EMPTY: { break; }
            
        case ANIMATION: {
            Animation t = (Animation)root->node;
            
            applyAnimation(t, time);
            
            for( Branch nb : *(t->subbranch) )
                branchDraw(nb, time);
            
            glPopMatrix();
            
            break;
        }

        case TRANSFORMATION: {
            Transformation t = (Transformation)root->node;
            
            applyTransformation(t);
            
            for( Branch nb : *(t->subbranch) )
                branchDraw(nb, time);
            
            glPopMatrix();

            break;
        }

        case MODEL: {
            Model m = (Model)root->node;

            glBindBuffer(GL_ARRAY_BUFFER, m->buffer);

            glVertexPointer(3,GL_FLOAT,0,0);
            glDrawArrays(GL_TRIANGLES, 0, m->points->size());

            break;
        }
    }
}
