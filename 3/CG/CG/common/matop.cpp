#include "matop.h"
#include <math.h>
#include <cstdlib>
#include <cstring>

typedef struct mat{
    float * mat;
} *Mat;

static int rowMaj(int i, int j){
    return i*4 + j;
}

static int colMaj(int i, int j){
    return i + j*4;
}

static float get(Mat t, int i, int j){
    return t->mat[colMaj(i,j)];
}

static void set(Mat t, int i, int j, float value){
    t->mat[colMaj(i,j)] = value;
}

float * getMat( Mat m ) {
    return m->mat;
}

static Mat alloc(){

    Mat m = (Mat)malloc(sizeof(struct mat));
    m->mat = (float*)malloc(sizeof(float)*4*4);
    //for(int i = 0; i< 4; i++)
    //    m->mat[i] = (float*)malloc( sizeof(float) * 4 );

    return m;
}

void freeMat(Mat t){

    //for(int i = 0; i< 4; i++)
    //    free(t->mat[i]);

    free(t->mat);
    free(t);
}

Mat identity(){
    Mat m = alloc();
    for(int i = 0; i< 4; i++) {
        for (int j = 0; j < 4; j++)
            set(m, i, j, (i == j));
    }

    return m;
}

Mat zeros(){
    Mat m = alloc();
    for(int i = 0; i< 4; i++) {
        for (int j = 0; j < 4; j++)
            set(m, i, j, 0);
    }

    return m;
}

Mat matRx(float angle){
    Mat m = identity();

    float rad = (M_PI/180.0)*angle;

    set(m,1,1,cos(rad));
    set(m,1,2,-sin(rad));
    set(m,2,1,sin(rad));
    set(m,2,2,cos(rad));

    return m;
}

Mat matRy(float angle){
    Mat t = identity();

    float rad = (M_PI/180.0)*angle;

    set(t,0,0,cos(rad));
    set(t,0,2,sin(rad));
    set(t,2,0,-sin(rad));
    set(t,2,2,cos(rad));

    return t;
}

Mat matRz(float angle){
    Mat t = identity();

    float rad = (M_PI/180.0)*angle;

    set(t,0,0,cos(rad));
    set(t,0,1,-sin(rad));
    set(t,1,0,sin(rad));
    set(t,1,1,cos(rad));

    return t;
}

Mat matmul(Mat a, Mat b){
    Mat result = zeros();

    for( int i = 0; i<4; i++ )
        for(int j = 0;j<4; j++)
            for(int k = 0; k<4; k++)
                set(result,i,j, (get(result,i,j) + get(a,i,k)*get(b,k,j)) );

    return result;
}

float* crossVecProd(float *a, float *b) {

    float * res = (float*)malloc( sizeof(float)*3 );

    res[0] = a[1]*b[2] - a[2]*b[1];
    res[1] = a[2]*b[0] - a[0]*b[2];
    res[2] = a[0]*b[1] - a[1]*b[0];

    return res;
}

Mat upsideMat(float *deriv, float *norm){


    float* z = crossVecProd(deriv,norm);

    float* yn = crossVecProd(z,deriv);


    float lyn = sqrt(yn[0]*yn[0] + yn[1]*yn[1] + yn[2]*yn[2]);
    float lz = sqrt(z[0]*z[0]  + z[1]*z[1] + z[2]*z[2]);
    float lderiv = sqrt(deriv[0]*deriv[0] + deriv[1]*deriv[1] + deriv[2]*deriv[2]);

    for(int i=0; i< 3; i++){
        yn[i]= yn[i]/lyn;
        z[i]= z[i]/lz;
        deriv[i]= deriv[i]/lderiv;
    }

    norm[0]=yn[0];
    norm[1]=yn[1];
    norm[2]=yn[2];

    free(yn);

    Mat m = zeros();

    set(m,0,0,deriv[0]); set(m,0,1,norm[0]); set(m,0,2,z[0]);

    set(m,1,0,deriv[1]); set(m,1,1,norm[1]); set(m,1,2,z[1]);

    set(m,2,0,deriv[2]); set(m,2,1,norm[2]); set(m,2,2,z[2]);

    set(m,3,3,1);

    free(z);

    return m;
}

float * vecMul(Mat mat, float *vec, int n){

    float * r = (float*)malloc( sizeof(float)*n);

    for(int i=0; i< n; i++){
        r[i] = 0;
        for(int j=0; j< 4; j++) {
            r[i] += get(mat,i,j) * vec[j];
        }
    }

    return r;
}

Mat matRotate(float angle, float vx, float vy, float vz){
    float l = sqrt(vx*vx + vy*vy + vz*vz);

    float rx = vx*angle/l;
    float ry = vy*angle/l;
    float rz = vz*angle/l;

    Mat mrx = matRx(rx);
    Mat mry = matRy(ry);
    Mat mrz = matRz(rz);

    Mat mr1 = matmul(mrx,mry);
    Mat mr2 = matmul(mr1,mrz);

    freeMat(mr1);

    freeMat(mrx);
    freeMat(mry);
    freeMat(mrz);

    return mr2;
}

Mat matTranslate(float x, float y, float z){
    Mat t = identity();

    set(t,0,3,x);
    set(t,1,3,y);
    set(t,2,3,z);

    return t;
}

Mat matScale(float xaxis, float yaxis, float zaxis){
    Mat t = identity();

    set(t,0,0,xaxis);
    set(t,1,1,yaxis);
    set(t,2,2,zaxis);

    return t;
}

void matAssign(Mat value, float r[4][4]){

    for( int i = 0; i<4; i++ )
        for(int j = 0;j<4; j++)
            set(value,i,j,r[i][j]);
}

Mat catMullMat(){
    float m[4][4] = {{-0.5f,  1.5f, -1.5f,  0.5f},
                     { 1.0f, -2.5f,  2.0f, -0.5f},
                     {-0.5f,  0.0f,  0.5f,  0.0f},
                     { 0.0f,  1.0f,  0.0f,  0.0f}};

    Mat result = zeros();

    matAssign(result,m);

    return result;
}