
test sort-1 {
    set foo [sort {f a sd d d e q f j}]
    set foo
} {a d d e f f j q sd}

test sort-2 {
    set foo [sort { 5 6 7 1 1 2 3 4 2 1}]
    set foo
} {1 1 1 2 2 3 4 5 6 7}

test sort-3 {
    set foo [sort {5 6 7 10 100 20 3 4 2 1} by int]
    set foo
} {1 2 3 4 5 6 7 10 20 100}

test sort-4 {
    set emptylist {}
    sort $emptylist
} {}

test sort-5 {
    sort {padova belluno venezia rovigo verona treviso}
} {belluno padova rovigo treviso venezia verona}

test sort-6 {
    sort {5 6 7 10 100 20 3 4 2 1} by string
} {1 10 100 2 20 3 4 5 6 7}

proc mysort {l1 l2} {
    set t1 [lindex $l1 2]
    set t2 [lindex $l2 2]

    if {= $t1 $t2} {
	return 0
    } elseif {< $t1 $t2} {
	return -1;
    } else {
	return 1;
    }
}

test sort-7 {
    set lol {
	{a b 4}
	{b c 2}
	{d e 1}
	{f g 100}
	{g h 20}
	{g h 35}
    }
    sort $lol by proc command mysort
} {{d e 1} {b c 2} {a b 4} {g h 20} {g h 35} {f g 100}}

proc badsort {a b} {
    return 1
}

test sort-8 {
    set lol {
	{a b 4}
	{b c 2}
	{d e 1}
	{f g 100}
	{g h 20}
	{g h 35}
    }
    sort $lol by proc command badsort
} {{a b 4} {b c 2} {d e 1} {f g 100} {g h 20} {g h 35}}
