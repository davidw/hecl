
test intro-1 {
    set commands [sort [intro commands]]
    set commands
} {!= * + - / < = > append break catch continue eq eval fail filter for foreach global globaltestreadfoo globaltestsetfoo hash hget hset if incr intro join lappend lindex list llen lset module ne ok proc puts return search set settwoglobals sindex slen sort source sourcehere split test testfiles time totals true twoglobals upeval while}
