# Makefile for Hecl.

# $Id$

# Compiler to use:
#JAVAC=/usr/lib/j2se/1.4/bin/javac
JAVAC=gcj-3.4 -C
CC=gcj-3.4

# Use profiling or not.
PROFILE = -pg

# Regular old Java uses these files.
SEFILES=LoadFile.java

# These files are used in the cut down J2ME stuff.
MEFILES=LoadString.java

# All versions use these files.
FILES=AppendCmd.java \
BasicMathCmd.java \
BreakCmd.java \
CatchCmd.java \
CodeThing.java \
Command.java \
Compare.java \
CopyCmd.java \
EqualsCmd.java \
Eval.java \
EvalCmd.java \
FilterCmd.java \
ForCmd.java \
ForeachCmd.java \
GlobalCmd.java \
GroupThing.java \
HashCmd.java \
HashThing.java \
HeclException.java \
IfCmd.java \
IncrCmd.java \
Interp.java \
IntrospectCmd.java \
IntThing.java \
JoinSplitCmd.java \
ListCmd.java \
ListThing.java \
Load.java \
Parse.java \
ParseList.java \
Proc.java \
ProcCmd.java \
PutsCmd.java \
RealThing.java \
RefCmd.java \
ReturnCmd.java \
SetCmd.java \
SortCmd.java \
SourceCmd.java \
SourceHereCmd.java \
StringCmd.java \
StringThing.java \
Thing.java \
TimeCmd.java \
TrueCmd.java \
UpCmd.java \
WhileCmd.java \

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
	jar cvf hecl.jar $(CLASSES) $(MECLASSES) $(srcdir)CodeThing\$$Stanza.class \
	$(srcdir)Parse\$$ParseState.class

hecl: ofiles
	gcj-3.4 -O3 -g -o hecl --main=Hecl Hecl.java $(OBJECTS) $(SEOBJECTS) $(PROFILE)

# "Stand alone" hecl.  Has a script compiled into it.
sahecl: ofiles
	gcj-3.4 -O3 -g -o sahecl --main=StandaloneHecl StandaloneHecl.java \
	$(OBJECTS) $(MEOBJECTS) $(PROFILE)

# Needed for libreadline.
librl.so: /usr/share/java/libreadline-java.jar
	gcj-3.4 -fjni -fPIC -shared -Wl,-soname,librl.so -o librl.so \
	/usr/share/java/libreadline-java.jar

# Readline version of the command line program.
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

# Run the tests.
test: hecl
	./hecl tests/suite.hcl

docs: docs/hecl.html docs/index.html

# Generate html docs.
docs/hecl.html: docs/hecl.xml docs/hecl.xsl docs/nochunk.xsl
	xsltproc --nonet --stringparam html.stylesheet hecl.css -o docs/hecl.html docs/nochunk.xsl docs/hecl.xml

docs/index.html: docs/hecl.xml docs/hecl.xsl docs/chunk.xsl
	xsltproc --nonet --stringparam html.stylesheet hecl.css -o docs/ docs/chunk.xsl docs/hecl.xml