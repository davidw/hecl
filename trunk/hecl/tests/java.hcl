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
    set prints [sys -field err]
    $prints println "yay!"
} {}

test java-6 {
    puts "This one prints out a big stack trace, but passes - it's checking for errors"
    java java.lang.Integer int
    catch {
	int parseint foo
    } err
    set err
} {{ERROR {int parseint error {ERROR {Problem invoking java.lang.Integer parseint/parseInt with arguments: int parseint foo  (Translated to:) foo  java.lang.NumberFormatException: For input string: "foo"}}}} {int 2}}

test java-7 {
    java java.lang.Integer int
    set newint [int -new 10]
    sort [int -methods]
} {{bitcount int} bytevalue {compareto Integer Object} {decode String} doublevalue {equals Object} floatvalue getclass {getinteger String String int String Integer} hashcode {highestonebit int} intvalue longvalue {lowestonebit int} notify notifyall {numberofleadingzeros int} {numberoftrailingzeros int} {parseint String int String} {reverse int} {reversebytes int} {rotateleft int int} {rotateright int int} shortvalue {signum int} {tobinarystring int} {tohexstring int} {tooctalstring int} {tostring int int int} {valueof String int String int} {wait long long int}}


# Set a field
test java-8 {
    java java.awt.Rectangle rect
    set r [rect -new [list]]
    set h1 [$r -field height]
    $r -field height 10
    list $h1 [$r -field height]
} {0 10}

# Wrong number of arguments
test java-9 {
    java java.awt.Rectangle rect
    set r [rect -new [list]]
    catch {
	set h1 [$r -field height has too many args]
    } err
    set err
} {{ERROR {wrong # args: should be "java.awt.Rectangle[x=0,y=0,width=0,height=0] -field fieldname ?fieldvalue?"} 2} {java.awt.Rectangle[x=0,y=0,width=0,height=0] 1}}

test java-10 {
    java java.lang.String str

    set s [str -new [list "foobar"]]
    $s equals [null]
} {0}

test java-11 {
    java java.lang.String str

    set s [str -new [list "foobar"]]
    set bytes [$s getBytes]
    set bytes
} {foobar}

test java-12 {
    java java.io.ByteArrayInputStream bais
    set b [bais -new [list "foobar"]]
    $b available
} {6}
