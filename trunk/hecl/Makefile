PROFILE = -pg

srcdir=com/dedasys/hecl/

classes:
	gcj-3.4 -O3 -C $(srcdir)*.java

ofiles: classes
	gcj-3.4 -g -c $(srcdir)*.class

hecl: classes
	gcj-3.4 -O3 -g -o hecl --main=Hecl Hecl.java $(srcdir)*.class $(PROFILE)

librl.so: /usr/share/java/libreadline-java.jar
	gcj-3.4 -fjni -fPIC -shared -Wl,-soname,librl.so -o librl.so /usr/share/java/libreadline-java.jar

rlhecl: classes librl.so
	gcj-3.4 -g -o rlhecl --main=RLHecl RLHecl.java com/dedasys/hecl/*.class  -L. -lrl -I/usr/share/java/libreadline-java.jar $(PROFILE)

# /usr/share/java/libreadline-java.jar -L/usr/lib/jni/ -lJavaEditline


clean:
	-find . -name "*~" | xargs rm
	-find . -name "*.class" | xargs rm
	-find . -name "*.o" | xargs rm
	-rm hecl
	-rm rlhecl
	-rm librl.so

test: hecl
	./hecl tests/suite.hcl
