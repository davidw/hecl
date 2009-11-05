
test intro-1 {
    set commands [sort [intro commands]]
    set missing [list "Missing commands:" ]
    # We should better split the commands into multiple lists so we can
    # separate extensions from core commands.
    foreach exp {
	!= % * + - / 1+ 1- < <= = > >= abs acos after alias and
	append asin atan base64::decode base64::encode bgerror break catch cbrt
	ceil classof clock continue copy cos cosh double eq eval exit exp expm1
	fail false
	file.absolutepath file.cd file.canonicalpath
    	file.current file.delete file.devs file.du
	file.exists file.hidden file.isabsolute file.isdirectory file.isopen
	file.join file.getcwd file.list file.mkdir file.mtime file.name
	file.path
	file.readable file.rename file.size
	file.split file.truncate file.writable
	filter float floor for foreach
	global hasclass hash hclear hcontains hget hkeys hremove hset http.data
	http.formatQuery http.geturl http.ncode http.status hypot if incr
	int intro java join lappend lindex linsert list llen load log log10 log1p
	long lrange lset ne not null ok open or pow proc puts random rename
	return round runtime.freememory runtime.totalmemory search set
	signum sin sinh sort source split sqrt strbytelen strcmp
	strfind strindex strlast strlen strlower strrange strrep strtrim strtriml
	strtrimr strupper system.gc system.getproperty system.hasproperty tan tanh
	test testfiles throw time tnotify toDegrees toRadians totals true twait
	unset upeval while write} {
	if { != 1 [llen [search $commands x {eq $x $exp}]] } {
	    lappend $missing $exp;
	}
    }
    set missing;
}  [list "Missing commands:" ]

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
