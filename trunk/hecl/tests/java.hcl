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