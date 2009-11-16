# Suite.hcl - Hecl test suite.

# Move to the correct directory.
set destdir [file.join [lrange [file.split [file.current]] 0 -2]]
puts "Running in $destdir"
file.cd $destdir

source harness.hcl

if { = [llen $argv] 2 } {
    testfiles [list [lindex $argv 1]]
    totals
    exit
}

# intro.hcl must occur first!!
testfiles {
    intro.hcl
    after.hcl
    alias.hcl
    append.hcl
    break.hcl
    catch.hcl
    circular.hcl
    deepcopy.hcl
    equality.hcl
    eval.hcl
    files.hcl
    filter.hcl
    for.hcl
    foreach.hcl
    global.hcl
    hash.hcl
    http.hcl
    i18n.hcl
    if.hcl
    incr.hcl
    index.hcl
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
