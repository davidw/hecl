# Test the Hecl/Java integration provided by the Java command, which
# links a Java class to a Hecl command.

test java-1 {
    java java.util.Hashtable ht
    ht -new {}
} {{}}

test java-2 {
    java java.util.Hashtable ht
    set hasht [ht -new {}]
    $hasht put foo "bar"
    $hasht get foo
} {bar}

test java-3 {
    java java.util.Hashtable ht
    set hasht [ht -new {}]
    $hasht isempty
} {1}

test java-4 {
    java java.lang.System sys
    sys gc
} {}

test java-5 {
    java java.lang.System sys
    java java.io.PrintStream ps
    set prints [sys -field err]
    $prints println "yay!"
} {}

test java-6 {
    java java.lang.Integer int
    int parseint foo
} {{ERROR {int parseint error {ERROR {Problem invoking java.lang.Integer parseint/parseInt with arguments: int parseint foo  (Translated to:) foo  java.lang.NumberFormatException: For input string: "foo"}}}} {int 3}}

test java-7 {
    java java.lang.Integer int
    set newint [int -new 10]
    sort [int -methods]
} {{bitcount int} bytevalue {compareto Integer Object} {decode String} doublevalue {equals Object} floatvalue getclass {getinteger String String int String Integer} hashcode {highestonebit int} intvalue longvalue {lowestonebit int} notify notifyall {numberofleadingzeros int} {numberoftrailingzeros int} {parseint String int String} {reverse int} {reversebytes int} {rotateleft int int} {rotateright int int} shortvalue {signum int} {tobinarystring int} {tohexstring int} {tooctalstring int} {tostring int int int} {valueof String int String int} {wait long long int}}