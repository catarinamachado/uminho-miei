#ifndef mat_h_
#define mat_h_

typedef struct mat *Mat;

Mat identity();
Mat zeros();
void freeMat(Mat t);

Mat matRx(float angle);
Mat matRy(float angle);
Mat matRz(float angle);

Mat matmul(Mat a, Mat b);

Mat matRotate(float angle, float vx, float vy, float vz);
Mat matTranslate(float x, float y, float z);
Mat matScale(float xaxis, float yaxis, float zaxis);

float * vecMul(Mat mat, float *vec, int n);

float* crossVecProd(float *a, float *b);

Mat upsideMat(float *deriv, float *norm);
Mat catMullMat();

float * getMat( Mat m );

#endif