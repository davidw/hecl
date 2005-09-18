
all: Hecl.jar

hecl:
	javac -target cldc1.0 -source 1.4 -bootclasspath /home/davidw/Desktop/WTK2.2/lib/cldcapi10.jar:/home/davidw/Desktop/WTK2.2/lib/midpapi20.jar -sourcepath ../core/ ../core/org/hecl/*.java -d classes/

Hecl.jar: Hecl.java GUICmds.java
	javac -target cldc1.0 -source 1.4 -bootclasspath /home/davidw/Desktop/WTK2.2/lib/cldcapi10.jar:/home/davidw/Desktop/WTK2.2/lib/midpapi20.jar -classpath ../out/heclCore.jar Hecl.java GUICmds.java -d classes/
	jar cfm Hecl.jar MANIFEST.MF -C classes .
