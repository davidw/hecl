
test intro-1 {
    set commands [sort [intro commands]]
    set commands
} {!= * + - / < = > and append break catch cd continue currentfile eq eval exit fail filesize filetolist filter for foreach foreach6cmd global globaltestreadfoo globaltestsetfoo hash hget hset if incr intro join lappend lindex list listtofile llen load lrange lset ne not ok or proc puts readall return round search set settwoglobals strindex slen sort source split test testfiles time totals true twoglobals upeval while write}
