# Makefile for J2ME Hecl.

CLDCAPI=/home/davidw/Desktop/WTK2.2/lib/cldcapi10.jar
MIDPAPI=/home/davidw/Desktop/WTK2.2/lib/midpapi10.jar

all: Hecl.jar

heclclasses:
	javac -target cldc1.0 -source 1.4 -bootclasspath $(CLDCAPI):$(MIDPAPI) -sourcepath ../core/ ../core/org/hecl/*.java -d classes/

Hecl.jar: Hecl.java GUICmds.java
	javac -target cldc1.0 -source 1.4 -bootclasspath $(CLDCAPI):$(MIDPAPI) -classpath ../out/heclCore.jar Hecl.java GUICmds.java -d classes/
	jar cfm Hecl.jar MANIFEST.MF -C classes .

clean:
	-rm Hecl.jar