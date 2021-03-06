
#include "AdvancedDemo.h"
#include <stdio.h>

namespace Heeere {
    void ToThing::setForceInt(bool b) {
        forceInt = b;
    }
    void ToThing::intInput(int val) {
        input(val);
    }
    void ToThing::input(float val) {
        Thing t;
        t.c = new char[256];
        if (forceInt) {
            t.a = (int) val;
            t.b = t.a;
            sprintf(t.c,"%i", t.a);
        } else {
            t.b = val;
            t.a = (int) t.b;
            sprintf(t.c,"%f", t.b);
        }
        Thing *tt = &t;
        emitNamedEvent("string", t.c);
        emitNamedEvent("output", tt, tt);
        delete[] t.c;
    }
   void CompareThings::input(Thing *t1, Thing *t2) {
       int o = t1->b == t2->b ? 1 : 0;
       const char* s = t1->b == t2->b ? "equal" : "differ";
       emitNamedEvent("output", o);
       emitNamedEvent("string", s);
   }
}


void ProduceStl::input(int value) {
    std::string *s = new std::string("Haha");
    std::vector<int> *v = new std::vector<int>;
    v->push_back(value);
    emitNamedEvent("output", s, v);
    delete s;
    delete v;
}

void ConsumeStl::input(std::string *str, std::vector<int> *vec) {
    char *buf = new char[1024];
    sprintf(buf, "Hi: %s, %d\n", str->c_str(), (*vec)[0]);
    emitNamedEvent("output", buf);
    delete[]buf;
}

void ProduceAndConsumeCustomType::tick(int tick) {
    long size = 3;
    CustomType *arr = new CustomType[size];
    for (int i = 0; i<size; i++) {
        arr[i].x = tick;
        arr[i].y = tick * i;
        arr[i].score = (tick % size) == i ? 1.f*tick : -.001f*tick;
    }
    emitNamedEvent("output", arr); // actually semantically meaning we send the first one
    emitNamedEvent("outputArray", size, arr);
    delete[] arr;
}

void ProduceAndConsumeCustomType::input(CustomType *value) {
    printf("Received: %d,%d \t-> %f\n", value->x, value->y, value->score);
}

void ProduceAndConsumeCustomType::inputArray(long count, CustomType *values) {
    printf("Received array of size %d\n", (int)count);
    for (int i = 0; i<count; i++) {
        printf("  values[%d]: %d,%d \t-> %f\n", i, values[i].x, values[i].y, values[i].score);
    }
}


namespace heeere {
    void ProduceAndConsumeNSType::input(int i) {
        Type *c = new Type;
        c->x = i;
        c->y = i*10 + i;
        emitNamedEvent("output", c);
        delete c;
    }
    void ProduceAndConsumeNSType::o(Type *value) {
        printf("Received: %d,%d\n", value->x, value->y);
    }
}


void SimpleTypes::input(long i, double f, bool b, const char* s) {
    printf("Received: %d %f %s %s\n", (int)i, f, b ? "true" : "false", s);
}

