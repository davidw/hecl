
test intro-1 {
    set commands [sort [intro commands]]
    set commands
} {!= % * + - / 1+ 1- < <= = > >= abs acos after and append asin atan base64::decode base64::encode bgerror break catch cbrt cd ceil classof clock continue copy cos cosh currentfile doappend double eq eval exit exp expm1 fail false fe7 filesize filetolist filter float floor for foreach foreach6cmd global global-4-1 global-4-2 global-5-1 global-6-1 globalincr globaltestreadfoo globaltestsetfoo hasclass hash hclear hcontains hget hkeys hremove hset http.data http.formatQuery http.geturl http.ncode http.status hypot if incr incrtmp int intro java join lappend lindex linsert list listtofile llen load log log10 log1p long lrange lset ne not ok or p2 pow proc puts random readall rename return round runtime.freememory runtime.totalmemory search set settwoglobals signum sin sinh sort source split sqrt strbytelen strcmp strfind strindex strlast strlen strlower strrange strrep strtrim strtriml strtrimr strupper system.gc system.getproperty system.hasproperty tan tanh test testfiles throw time tnotify toDegrees toRadians totals true twait twoglobals unset upeval while write}

test intro-2 {
    proc foo {} { set x [llen "foo bar baz"] }
    set x [foo]
    append $x [intro proccode foo]
} {3set x [llen {foo bar baz}]}

test intro-3 {
    proc bar {} { return puts }
    proc foo {} { [bar] xyz }
    foo
    intro proccode foo
} {[bar] xyz}
