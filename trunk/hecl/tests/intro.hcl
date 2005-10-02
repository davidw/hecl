
test intro-1 {
    set commands [sort [intro commands]]
    set commands
} {!= * + - / < = > append break catch cd continue currentfile eq eval fail filetolist filter for foreach global globaltestreadfoo globaltestsetfoo hash hget hset http if incr intro join lappend lindex list listtofile llen lrange lset ne ok proc puts return search set settwoglobals sindex slen sort source split test testfiles time totals true twoglobals upeval while}
