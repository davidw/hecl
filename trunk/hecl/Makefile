# Makefile for Hecl.

# $Id$

# Compiler to use.
#JAVAC=/usr/lib/j2se/1.4/bin/javac
JAVAC=gcj-3.4 -C
CC=gcj-3.4

# Use profiling or not.
PROFILE = -pg

# Regular old Java
SEFILES=LoadFile.java

# Cut down J2ME stuff.
MEFILES=LoadString.java

# All versions
FILES=AppendCmd.java \
ForeachCmd.java \
SortCmd.java \
BasicMathCmd.java \
GlobalCmd.java \
Load.java \
SourceCmd.java \
BreakCmd.java \
HashCmd.java \
SourceHereCmd.java \
CatchCmd.java \
HeclException.java \
Parse.java \
StringCmd.java \
CodeThing.java \
IfCmd.java \
ParseList.java \
Thing.java \
Command.java \
IncrCmd.java \
ProcCmd.java \
TimeCmd.java \
CopyCmd.java \
Interp.java \
Proc.java \
TrueCmd.java \
EqualsCmd.java \
IntrospectCmd.java \
PutsCmd.java \
WhileCmd.java \
EvalCmd.java \
JoinSplitCmd.java \
RefCmd.java \
Eval.java \
ListCmd.java \
ReturnCmd.java \
ForCmd.java \
LmatchCmd.java \
SetCmd.java

OBJECTS = $(patsubst %.java,$(srcdir)%.o,$(FILES))
CLASSES = $(patsubst %.java,$(srcdir)%.class,$(FILES))

SEOBJECTS = $(patsubst %.java,$(srcdir)%.o,$(SEFILES))
SECLASSES = $(patsubst %.java,$(srcdir)%.class,$(SEFILES))

MEOBJECTS = $(patsubst %.java,$(srcdir)%.o,$(MEFILES))
MECLASSES = $(patsubst %.java,$(srcdir)%.class,$(MEFILES))

srcdir=com/dedasys/hecl/

$(srcdir)%.class : $(srcdir)%.java
	$(JAVAC) $<

$(srcdir)%.o : $(srcdir)%.java
	$(CC) -c -O3 -g $(PROFILE) $< -o $@

classes: $(CLASSES) $(SECLASSES) $(MECLASSES)

ofiles: $(OBJECTS) $(SEOBJECTS) $(MEOBJECTS)

hecl.jar: classes $(CLASSES) $(MECLASSES)
	jar cvf hecl.jar $(CLASSES) $(MECLASSES) $(srcdir)CodeThing\$$Stanza.class $(srcdir)Parse\$$ParseState.class

hecl: ofiles
	gcj-3.4 -O3 -g -o hecl --main=Hecl Hecl.java $(OBJECTS) $(SEOBJECTS) $(PROFILE)

sahecl: ofiles
	gcj-3.4 -O3 -g -o sahecl --main=StandaloneHecl StandaloneHecl.java \
	$(OBJECTS) $(MEOBJECTS) $(PROFILE)

librl.so: /usr/share/java/libreadline-java.jar
	gcj-3.4 -fjni -fPIC -shared -Wl,-soname,librl.so -o librl.so \
	/usr/share/java/libreadline-java.jar

rlhecl: ofiles librl.so
	gcj-3.4 -g -o rlhecl --main=RLHecl RLHecl.java $(OBJECTS) $(SEOBJECTS) \
	-L. -lrl -I/usr/share/java/libreadline-java.jar $(PROFILE)

clean:
	-find . -name "*~" | xargs rm
	-find . -name "*.class" | xargs rm
	-find . -name "*.o" | xargs rm
	-rm hecl
	-rm rlhecl
	-rm librl.so

test: hecl
	./hecl tests/suite.hcl
