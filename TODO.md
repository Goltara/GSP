
# TODO

## Python related
* error handling in general, incl: must not accept affect of inexisting parameter (should not start)

## CPP related TODO:
* when fail to create a module, tell which one
* when anything happen, is there a way of putting info in the stack trace? or thread name (only in debug mode)?
* full inheritance (call input port from parent)
* full inheritance (accept parameters from parent)
* full inheritance (call onChange from parent if exists)

## Framework 
* have a proper message (usage) when starting without options (and/or with --help) ...
* have a default value for variables in the xml file
* ^ should not start if unset example pure-java-5 should not start if parameters are missing!
* have some arithmetic/expressions with variables in the xml file (e.g. with juel, http://juel.sourceforge.net/)
* handle possibility for a module to know if it has some "clients" on an output (by asking the framework)
* GSP_DEBUG=threadname/logcalls/logprefix:=====/...

## Unclassified
* test missing input etc
* test python tuple param problem
* test with buggous modules on all aspects (constructor, initModule, setParam listener, stopModule, ...)
* handle all error reporting, not starting todos
* (should) cool JMX export
* (should) bash completion helper!!!
* (may) java gsp should accept plain classes with public field or javabeans (using a by-convention approach, even for callbacks (maybe gspModuleInit...)) then they get wrapped in a real module
* (may) string conversion? GC?
* (may) done? long support
* (may) add support for fancy command line stuffs e.g. for [ period=1 warmupDelay=0 ], or curly {} as it seems ok (allow also for[ period=1 warmup=0 ] (no space after for))
* design passive module stuff
* metamodules in .xml (a module as an assembly of modules) (could be a special type of modules like C modules with a namespace c: ==> e.g. xml:path/to/metatrucs.xml/Div)
* OPT: add a define that generate a human oriented version of the struct (use e.g. ___NL___ for new lines, then replace them all by a real new line in the helper script)
* a "-?" "--help" option that explores the pipeline and reports the variables, etc
* gsp-inspect?

## GSPExamples
* CPP: check in Div::input that the local vars are still needed. namespace the example
* java: check README instructions (how to compile, framework jar path)
* CPP: add examples using FIELD_WITH_SETTER and STRING_FIELD_WITH_SETTER
* more: add examples with (new) passive modules (reusing modules in a plain program)

## TODO from PipelineViewer
* IMPR: might use a left-input right-output convention?
* interactive version, as a web server? with more info on overlay?





# OLD VRAC
* fix in GSPBaseUtils the For module with 0 as a period

* handle the fact that string get garbage collected (C++ should do a copy on receive or at least we can try to prevent gc for parameters (not that simple) until the pipeline is gc'ed)

v
* better feedback on setter (c++) not found

* same better feedback for invocationtargetexception when binding
   wrongly connectors (java included)

* better feedback when a java class has multiple methods (event
   receivers) with the same name

* base utils
** timer/clock/thread (all the same thing) : done?
** thread fork/join
** merger/extractor (parameter list ...) (option: emit at each change or emit when all changed at least once or emit when all changed once (block others before that))


* handle cpp class inheritance if we can get the info somewhere

* CPP: namespaces

* unallocated module when stopping pipeline : done????

* idea: replace a#b#c by something asymetric (e.g. a(b)c or a[b]c or a]b[c or a)b(c (no <> as in xml)) ...
    <c chain="tick - intInput#half# - half - #th#string - str#dup#str - log"/>        current

    <c chain="tick - intInput(half - half - th)string - str(dup)str - log"/>

    <c chain="tick - intInput)half - half - th(string - str)dup(str - log"/>

    <c chain="tick - intInput[half - half - th]string - str[dup]str - log"/>

    <c chain="tick - intInput]half - half - th[string - str]dup[str - log"/>

    <c chain="tick - intInput(half) - half - (th)string - str(dup)str - log"/>

    <c chain="tick - intInput)half( - half - )th(string - str)dup(str - log"/>

    <c chain="tick - intInput[half] - half - [th]string - str[dup]str - log"/>

    <c chain="tick - intInput]half[ - half - ]th[string - str]dup[str - log"/>





