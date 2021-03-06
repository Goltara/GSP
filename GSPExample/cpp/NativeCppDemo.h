
#include "framework.h"

class Div {
    float divisor;

public:
    Framework _framework; // the module must have this exact public field

    // setters
    void setDivisor(float d);

    // ports
    void input(float value); // "input" is the default when writing pipelines
    void intInput(int value);

    Div(); // the default constructor must exist (note: it is generated by the compiler if you define no constructor)

    // notifications, we don't need them here (default constructor is sufficient)
    //void initModule();
    //void stopModule();
};
CLASS_AS_MODULE(Div);


class Log {

public:
    Framework _framework; // the module must have this exact public field

    // setter
    void setHello(const char *value);

    // ports
    void input(const char *message);
    void highlight(const char *message);

    Log();
    ~Log();
    // notifications, we illustrate all of them here
    void initModule();
    void stopModule();
};
CLASS_AS_MODULE(Log);


class DivDouble {
    double divisor;

public:
    Framework _framework; // the module must have this exact public field

    // setters
    void setDivisor(double d);

    // ports
    void input(double value); // "input" is the default when writing pipelines
    void intInput(int value);

    DivDouble(); // the default constructor must exist (note: it is generated by the compiler if you define no constructor)

    // notifications, we don't need them here (default constructor is sufficient)
    //void initModule();
    //void stopModule();
};
CLASS_AS_MODULE(DivDouble);
