# Suite.hcl - Hecl test suite.

# Move to the correct directory.
set destdir [listtofile [lrange [filetolist [currentfile]] 0 -2]]
puts "Running in $destdir"
cd $destdir

source harness.hcl

testfiles {
    after.hcl
    append.hcl
    break.hcl
    catch.hcl
    circular.hcl
    deepcopy.hcl
    equality.hcl
    eval.hcl
    filter.hcl
    for.hcl
    foreach.hcl
    global.hcl
    hash.hcl
    http.hcl
    if.hcl
    incr.hcl
    index.hcl
    intro.hcl
    java.hcl
    join.hcl
    list.hcl
    listlen.hcl
    logic.hcl
    lset.hcl
    math.hcl
    parse.hcl
    path.hcl
    proc.hcl
    return.hcl
    search.hcl
    set.hcl
    sort.hcl
    source.hcl
    split.hcl
    string.hcl
    threads.hcl
    throw.hcl
    upeval.hcl
    while.hcl
}

totals