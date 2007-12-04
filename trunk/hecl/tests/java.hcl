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
    $prints println "damn!"
} {}