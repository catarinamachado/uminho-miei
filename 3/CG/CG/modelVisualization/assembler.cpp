#if defined(_WIN32)
    #include "GL/glut.h"
    #include "GL/glew.h"
#else
    #include <GLUT/glut.h>
#endif

#include "assembler.h"
#include "branch.h"
#include "../common/point.h"
#include "../common/tinyxml2.h"
#include "../common/matop.h"
#include <vector>
#include <utility>
#include <fstream>
#include <sstream>
#include <string>
#include <iostream>

using namespace std;
using namespace tinyxml2;

typedef struct assembler{
    struct branch root;
    Branch cur;
    vector< Point > * points;
    GLuint buffer;
} *Assembler;

void parseModel(const char * filename, Assembler state);
Assembler parseGroups(XMLNode * group, Assembler state);
vector<string> split(string strToSplit, char delimeter);

Assembler mkAssembler(){
    Assembler ass = (Assembler)malloc( sizeof(struct assembler) );
    ass->cur = &(ass->root);
    ass->points = new vector< Point >();
    ass->buffer= -1;

    ass->root.type = TRANSFORMATION;
    ass->root.node = mkTransformation(identity());
    return ass;
}

Assembler mkAssembler( Assembler origin ){
	Assembler r = (Assembler)malloc( sizeof(struct assembler) );
	r->root.type = FAKE;
	r->points = origin->points;
	r->buffer = origin->buffer;

    Branch bn = mkBranch(mkTransformation(identity()));

	switch( origin->cur->type ){
        case EMPTY: {
            origin->cur->node = mkTransformation(identity());
            origin->cur->type = TRANSFORMATION;
            addDescendent((Transformation)origin->cur->node, bn);
        }

        case ANIMATION: {
            addDescendent((Animation)origin->cur->node, bn);
            r->cur = bn;
            break;
        }

        case TRANSFORMATION: {
            addDescendent((Transformation)origin->cur->node, bn);
            r->cur = bn;
            break;
        }

        default : {
            printf("error: models must be the terminal nodes\n");
            exit(2);
        }
	}

	return r;
}

Assembler mkAssemblerModelView(Assembler origin){
    Assembler r = (Assembler)malloc( sizeof(struct assembler) );
    r->root.type = FAKE;
    r->points = origin->points;
    r->buffer = origin->buffer;

    Model mn = mkModel(assemblerNumberOfPoints(origin), assemblerNumberOfPoints(origin));

    switch( origin->cur->type ){
        case EMPTY: {
            origin->cur->node = mn;
            origin->cur->type = MODEL;
            r->cur = origin->cur;
        }

        case ANIMATION: {
            Branch tmp =  mkBranch(mn);
            addDescendent((Animation)origin->cur->node, tmp );
            r->cur = tmp;
            break;
        }

        case TRANSFORMATION: {
            Branch tmp =  mkBranch(mn);
            addDescendent((Transformation)origin->cur->node, tmp );
            r->cur = tmp;
            break;
        }

        case MODEL: {
            r->cur = origin->cur;
        }

        default : {
            printf("error: models must be the terminal nodes\n");
            exit(2);
        }
    }

    return r;
}

Assembler mkAssembler(const char * filename){
    XMLDocument xml_doc;
    //cout << "parse" << endl;
    XMLError eResult =
            xml_doc.LoadFile(filename);

    if (eResult != XML_SUCCESS) {
        cout << "error" << endl;
        return nullptr;
    }

    XMLNode* root = xml_doc.FirstChildElement("scene");

    if (root == nullptr) {
        cout << "no root" << endl;
        return nullptr;
    }

    Assembler frame = mkAssembler();

    frame = parseGroups(root, frame);

    xml_doc.Clear();

    return frame;
}

Branch view(Assembler source){
    return source->cur;
}

void addDescendentAndGo(Assembler source, Branch descendent){

    switch( source->cur->type ) {
        case ANIMATION: {
            addDescendent((Animation) source->cur->node, descendent);
            break;
        }

        case TRANSFORMATION: {
            addDescendent((Transformation) source->cur->node, descendent);
            break;
        }
    }

    source->cur = descendent;
}

void unmkAssembler( Assembler target ){

	if( target->root.type != FAKE ){
		unmkBranch(target->root);
		delete target->points;
        glDeleteBuffers(1, &target->buffer);
	}
	free(target);
}

void assemblerPoint(Assembler ass, Point point){
    ass->points->emplace_back(point);
}

long assemblerNumberOfPoints(Assembler ass){
    return ass->points->size();
}

void assemblerRotate(Assembler ass, float angle, float vx, float vy, float vz){
    assemblerTransformate(ass, matRotate(angle,vx,vy,vz));
}

void assemblerTranslate(Assembler ass, float x, float y, float z){

	assemblerTransformate(ass,matTranslate(x,y,z));
}

void assemblerScale(Assembler ass, float xaxis, float yaxis, float zaxis){
    //printf("%f %f %f \n",xaxis,yaxis,zaxis);
    assemblerTransformate(ass, matScale(xaxis,yaxis,zaxis));
}

void assemblerRotationAnimation(Assembler ass, int period, float vx, float vy, float vz){

    auto l = new vector<Point>();

    Point point;

    point.p[0] = vx;
    point.p[1] = vy;
    point.p[2] = vz;

    l->emplace_back(point);

    assemblerAnimate(ass, period, l, ROTATION );
}

void assemblerTranslationAnimation(Assembler ass, int period, vector<Point> * v ){

    Point norm;

    norm.p[0]=0;
    norm.p[1]=1;
    norm.p[2]=0;

    v->emplace_back(norm);

    assemblerAnimate(ass, period, v, CURVED_TRANSLATION );

}

vector<string> split(string strToSplit, char delimeter)
{
	stringstream ss(strToSplit);
	string item;
	vector<string> splittedStrings;

	while (getline(ss, item, delimeter)){
		splittedStrings.push_back(item);
	}

	return splittedStrings;
}

void parseModel(const char * filename, Assembler state)
{
	ifstream file(filename);
	if (file.is_open()) {
		string line;
		while (getline(file, line)) {
			if (line.find("point") != string::npos) {
				vector<string> s = split(line, '"');
				assemblerModelate(state, stod(s.at(1)), stod(s.at(3)), stod(s.at(5)));
			}
		}
		file.close();
	}
}

Assembler parseGroups(XMLNode * group, Assembler state)
{
	for(XMLNode * g = group->FirstChild();
		g != nullptr;
		g = g->NextSibling()
		)
	{
		const char * name = g->Value();
		//printf("%s \n",name);
		if(!strcmp(name,"models")){
            Assembler other = mkAssemblerModelView(state);
			XMLElement * e = g->FirstChildElement("model");
	
			while(e != nullptr) {
				parseModel(e->Attribute("file"),other);
				e = e->NextSiblingElement("model");
			}

			unmkAssembler(other);
		}else if(!strcmp(name,"translate")){

			XMLElement *e = (XMLElement*) g;

            if(e->IntAttribute("period",-1.0) < 0.0 ){
                assemblerTranslate(	state,
                        e->FloatAttribute("X"),
                        e->FloatAttribute("Y"),
                        e->FloatAttribute("Z")
                );
            }else{
                int period = e->IntAttribute("period");
                XMLElement * c = g->FirstChildElement("point");
                vector<Point> * vOfPoints = new vector<Point>();

                Point tmp;
                while(c != nullptr) {

                    tmp.p[0] = c->FloatAttribute("X");
                    tmp.p[1] = c->FloatAttribute("Y");
                    tmp.p[2] = c->FloatAttribute("Z");

                    vOfPoints->emplace_back(tmp);

                    c = c->NextSiblingElement("point");
                }

                assemblerTranslationAnimation(state,period,vOfPoints);

            }

		}else if(!strcmp(name,"rotate")){
			
			XMLElement *e = (XMLElement*) g;

            if(e->FloatAttribute("period",-1.0) < 0.0 ) {
                assemblerRotate(state,
                                e->FloatAttribute("angle"),
                                e->FloatAttribute("axisX"),
                                e->FloatAttribute("axisY"),
                                e->FloatAttribute("axisZ")
                );

            }else{
                assemblerRotationAnimation(state,
                                e->FloatAttribute("period"),
                                e->FloatAttribute("axisX"),
                                e->FloatAttribute("axisY"),
                                e->FloatAttribute("axisZ")
                );
            }
		}else if(!strcmp(name,"scale")){
		
			XMLElement *e = (XMLElement*) g;
			assemblerScale(state,
						e->FloatAttribute("stretchX", 1.0),
						e->FloatAttribute("stretchY", 1.0),
						e->FloatAttribute("stretchZ", 1.0)
			);
		
		}else if(!strcmp(name,"group")){
			unmkAssembler(parseGroups(g, mkAssembler(state)));
		}
	
	}

	return state;
}

void assemblerDraw(Assembler reference, int time){

    branchDraw( &(reference->root), time);

    glutPostRedisplay();

}

void assemblerOptimize(Assembler reference){
    branchOptimize(reference->points, &(reference->root) );
    reference->points->shrink_to_fit();
}

void assemblerBufferData(Assembler reference){

    glEnableClientState(GL_VERTEX_ARRAY);

    assemblerOptimize(reference);

    branchBufferData(&(reference->root) );
}



