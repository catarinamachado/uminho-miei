#import "framePrimitives.h"
#import "../common/matop.h"
#import <math.h>


#define back(X,Y) unmkCoordinateFrame(X);X = mkCoordinateFrame(Y)

void plataform(CoordinateFrame reference, int points, float bottomradius, float topradius, int downface, int upface){
	float rinner = 2 * M_PI /((float)points);

	CoordinateFrame db = mkCoordinateFrame(reference);
	CoordinateFrame ub = mkCoordinateFrame(reference);

	frameTranslate(ub, 0.0, 1.0, 0.0);
	frameRotate(db, 180, 1.0, 0.0, 0.0);

	frameScale(db, bottomradius, 0.0, bottomradius);
	frameScale(ub, topradius, 0.0, topradius);

	if (upface)
		frameRegularPolygon(ub, points);

	if (downface)
		frameRegularPolygon(db, points);
	
	frameRotate(db, 180, 1.0, 0.0, 0.0);
		
	for( int i = 0; i<points; i++ ){

		framePoint(db,cos(i* rinner),0.0,sin(i* rinner));
		framePoint(ub,cos(i* rinner),0.0,sin(i* rinner));
		framePoint(db,cos((i+1)*rinner),0.0,sin((i+1)*rinner));

		framePoint(db,cos((i+1)* rinner),0.0,sin((i+1)* rinner));
		framePoint(ub,cos(i* rinner),0.0,sin(i* rinner));
		framePoint(ub,cos((i+1)*rinner),0.0,sin((i+1)*rinner));			
		
	}
	
	unmkCoordinateFrame(db);
	unmkCoordinateFrame(ub);
}

void frameStacker(CoordinateFrame reference,int points, int stacks, float (*f)(float) ){
	float dh = 1.0/stacks;
	float currenth = dh;
	float f0 = f(0.0);
	float f1;
	CoordinateFrame nw = mkCoordinateFrame(reference);
	frameScale(nw,1.0f,dh,1.0f);
	for(int i = 0; i < stacks; i++){
		f1 = f(currenth);
		plataform(nw, points, f0, f1 ,(i==0),(i == stacks-1));
		frameTranslate(nw,0.0f,1.0f,0.0f);
		f0 = f1;
		currenth += dh;
	}

	unmkCoordinateFrame(nw);
}

void plane(CoordinateFrame reference) {
    CoordinateFrame nw = mkCoordinateFrame(reference);

    framePoint(nw,-0.5,0,-0.5);
    framePoint(nw,-0.5,0,0.5);
    framePoint(nw,0.5,0,-0.5);

    framePoint(nw,-0.5,0,0.5);
    framePoint(nw,0.5,0,0.5);
    framePoint(nw,0.5,0,-0.5);

    unmkCoordinateFrame(nw);
}

void frameHyperplane(CoordinateFrame reference, int divisions){
    CoordinateFrame nw = mkCoordinateFrame(reference);

    frameScale(nw, 1.0/(float)divisions, 1.0/(float)divisions, 1.0/(float)divisions);
    frameTranslate(nw, -0.5*(divisions-1), 0.0, -0.5*(divisions-1));

    for(int i = 0; i < divisions; i++){
        for(int j = 0; j < divisions; j++){
            plane(nw);
            frameTranslate(nw,1,0.0,0.0);
        }
        frameTranslate(nw,-divisions,0.0,1.0);
    }

    unmkCoordinateFrame(nw);
}

void frameCube(CoordinateFrame reference, int divisions) {
    CoordinateFrame nw = mkCoordinateFrame(reference);

	for(int i = 0; i < 3; i++){
		switch(i){
			case 1: frameRotate(nw,90,1,0,0);break;
			case 2: frameRotate(nw,90,0.0,0.0,-1.0);break;
            default:break;
		}

    	frameTranslate(nw, 0.0, 0.5, 0.0);
    	frameHyperplane(nw, divisions);

    	frameTranslate(nw, 0.0, -1.0, 0.0);
    	frameRotate(nw,180,1.0,0.0,0.0);
    	frameHyperplane(nw, divisions);

    	back(nw,reference);
	}

    unmkCoordinateFrame(nw);
}

void frameRegularPolygon(CoordinateFrame reference,int points){
	float inner = 2*M_PI/((float)points);
	CoordinateFrame mon = mkCoordinateFrame(reference);

    
	for(int i = 0; i< points;i++)
		frameTriangle(mon,i * inner, inner);

	unmkCoordinateFrame(mon);
}

void frameBazierPatch(CoordinateFrame reference, Point * points, int tesselation){
    /*superfice de bazie com bicubic patches (4,4)
     *
     * Uma forma numericamente estavel de fazer o algoritmo de casteljau's
     * através de polinomis na forma Bernstein
     *
     * */
	Point curvePoints[tesselation+1][tesselation+1];

	for(int ui = 0; ui <= tesselation; ui++){
		float u = float(ui)/float(tesselation);// calcular ut
		for(int vi = 0; vi <= tesselation; vi++){
			float v = float(vi)/float(tesselation);// calcular vt
			Point point;
			point.p[0] = 0;
			point.p[1] = 0;
			point.p[2] = 0;
			for(int m = 0; m < 4; m++){
				for(int n =0; n< 4; n++){
					float bm = bernstein(m, 3, u);
					float bn = bernstein(n, 3, v);
					float b = bm * bn;// calculo do coeficiente da formula
					point.p[0] += b *  points[4*m + n].p[0];
					point.p[1] += b *  points[4*m + n].p[1];
					point.p[2] += b *  points[4*m + n].p[2];
				}
			}
			curvePoints[ui][vi] = point;
		}
	}

	for(int ui = 0; ui < tesselation; ui++) 
        for(int vi = 0; vi < tesselation; vi++) {

            framePoint(
                    reference,
                    curvePoints[ui][vi+1].p[0],
                    curvePoints[ui][vi+1].p[1],
                    curvePoints[ui][vi+1].p[2]);

            framePoint(
                    reference,
                    curvePoints[ui+1][vi].p[0],
                    curvePoints[ui+1][vi].p[1],
                    curvePoints[ui+1][vi].p[2]);

            framePoint(
                    reference,
                    curvePoints[ui][vi].p[0],
                    curvePoints[ui][vi].p[1],
                    curvePoints[ui][vi].p[2]);

			// sencond triangle
            /*
            framePoint(
                    reference,
                    curvePoints[ui][vi+1].p[0],
                    curvePoints[ui][vi+1].p[1],
                    curvePoints[ui][vi+1].p[2]);

            framePoint(
                    reference,
                    curvePoints[ui+1][vi].p[0],
                    curvePoints[ui+1][vi].p[1],
                    curvePoints[ui+1][vi].p[2]);



            framePoint(
                    reference,
                    curvePoints[ui+1][vi+1].p[0],
                    curvePoints[ui+1][vi+1].p[1],
                    curvePoints[ui+1][vi+1].p[2]);
            */

            // third
            framePoint(
                    reference,
                    curvePoints[ui+1][vi].p[0],
                    curvePoints[ui+1][vi].p[1],
                    curvePoints[ui+1][vi].p[2]);


            framePoint(
                    reference,
                    curvePoints[ui][vi+1].p[0],
                    curvePoints[ui][vi+1].p[1],
                    curvePoints[ui][vi+1].p[2]);

            framePoint(
                    reference,
                    curvePoints[ui+1][vi+1].p[0],
                    curvePoints[ui+1][vi+1].p[1],
                    curvePoints[ui+1][vi+1].p[2]);
            // forth
            /*
            framePoint(
                    reference,
                    curvePoints[ui+1][vi].p[0],
                    curvePoints[ui+1][vi].p[1],
                    curvePoints[ui+1][vi].p[2]);


            framePoint(
                    reference,
                    curvePoints[ui][vi+1].p[0],
                    curvePoints[ui][vi+1].p[1],
                    curvePoints[ui][vi+1].p[2]);

            framePoint(
                    reference,
                    curvePoints[ui][vi].p[0],
                    curvePoints[ui][vi].p[1],
                    curvePoints[ui][vi].p[2]);

			*/

        }
	
}
