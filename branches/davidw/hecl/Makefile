PROFILE =

srcdir=com/dedasys/hecl/

classes:
	javac $(srcdir)*.java

ofiles: classes
	gcj-3.4 -g -c $(srcdir)*.class

hecl: classes
	gcj-3.4 -g -o hecl --main=com.dedasys.hecl.Hecl $(srcdir)*.class $(PROFILE)

clean:
	-find . -name "*~" | xargs rm
	-find . -name "*.class" | xargs rm
	-find . -name "*.o" | xargs rm
	-rm hecl

test: hecl
	./hecl tests/suite.hcl
