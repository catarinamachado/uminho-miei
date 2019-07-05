#if defined(_WIN32)
    #include "GL/glut.h"
    #include "GL/glew.h"
#else
    #include <GLUT/glut.h>
#endif
#include "assembler.h"
#include <iostream>
#include <cmath>


using namespace std;

float camX = 0, camY, camZ = 5;
int startX, startY, tracking = 0;
int alpha = 0, beta = 0, r = 70;

Assembler mainframe;

void changeSize(int w, int h) {
	if(h == 0) h = 1;

    double ratio = w * 1.0 / h;

	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
    glViewport(0, 0, w, h);
	gluPerspective(45.0f ,ratio, 1.0f ,1000.0f);
	glMatrixMode(GL_MODELVIEW);
}

void init() {

	assemblerBufferData(mainframe);

}

void renderScene() {
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

	glLoadIdentity();
    gluLookAt(camX, camY, camZ,
              0.0,0.0,0.0,
              0.0f,1.0f,0.0f);

	glColor3f(0.6,0.0,0.8);

	int time = glutGet(GLUT_ELAPSED_TIME);

    glMatrixMode(GL_MODELVIEW);

    assemblerDraw(mainframe, time);

	glutSwapBuffers();
}

void processMouseButtons(int button, int state, int xx, int yy) {
    if (state == GLUT_DOWN)  {
        startX = xx;
        startY = yy;
        if (button == GLUT_LEFT_BUTTON)
            tracking = 1;
        else if (button == GLUT_RIGHT_BUTTON)
            tracking = 2;
        else
            tracking = 0;
    }
    else if (state == GLUT_UP) {
        if (tracking == 1) {
            alpha += (xx - startX);
            beta += (yy - startY);
        }
        else if (tracking == 2) {
            r -= yy - startY;
            if (r < 3)
                r = 3.0;
        }
        tracking = 0;
    }
}

void processMouseMotion(int xx, int yy) {
    int deltaX, deltaY;
    int alphaAux, betaAux;
    int rAux;
    if (!tracking)
        return;
    deltaX = xx - startX;
    deltaY = yy - startY;
    if (tracking == 1) {
        alphaAux = alpha + deltaX;
        betaAux = beta + deltaY;
        if (betaAux > 85.0)
            betaAux = 85.0;
        else if (betaAux < -85.0)
            betaAux = -85.0;
        rAux = r;
    }
    else if (tracking == 2) {
        alphaAux = alpha;
        betaAux = beta;
        rAux = r - deltaY;
        if (rAux < 3)
            rAux = 3;
    }
    camX = rAux * sin(alphaAux * 3.14 / 180.0) * cos(betaAux * 3.14 / 180.0);
    camZ = rAux * cos(alphaAux * 3.14 / 180.0) * cos(betaAux * 3.14 / 180.0);
    camY = rAux *							     sin(betaAux * 3.14 / 180.0);
}

void glut(int argc, char **argv) {
    glutInit(&argc, argv);
    glutInitDisplayMode(GLUT_DEPTH|GLUT_DOUBLE|GLUT_RGBA);
    glutInitWindowPosition(0,0);
	glutInitWindowSize(1450,900);
    glutCreateWindow(argv[1]);

    #ifndef __APPLE__
        glewInit();
    #endif

    glutDisplayFunc(renderScene);
    glutReshapeFunc(changeSize);

    glutMouseFunc(processMouseButtons);
    glutMotionFunc(processMouseMotion);

    glEnable(GL_DEPTH_TEST);
	glEnable(GL_CULL_FACE);

	init();

    glutMainLoop();
}

int main(int argc, char ** argv) {
	if (argc < 2) {
		cout << "No scene file provided." << endl;
		return 2;
	}

    mainframe = mkAssembler(argv[1]);

	if (!mainframe) {
        cout << "File not found" << endl;
		return 3;
	}

	glut(argc, argv);

	unmkAssembler(mainframe);

	return 1;
}




