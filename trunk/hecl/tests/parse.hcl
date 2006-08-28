# Funky parsing cases.

test parse-1 {
    set foo bee"bop"bee
    set foo
} {bee"bop"bee}

test parse-2 {
    set bee_bop 1
    set bee_bop
} {1}

proc printstuff {stuff} {
    return $stuff
}

proc printstuff2 {stuff} {
    return "new $stuff"
}

test parse-3 {
    set ps printstuff
    $ps foo
} {foo}

test parse-4 {
    set ps printstuff
    $ps foo
    set ps printstuff2
    $ps foo
} {new foo}

test parse-5 {
    set out {}
    set ps printstuff
    append $out [$ps foo]
    set ps printstuff2
    append $out [$ps foo]
    set out
} {foonew foo}

test parse-6 {
    foreach x y {
	set z 1
    }XXX
} {{ERROR {Extra characters after close-brace}}}

test parse-7 {
    set foo {};set bar 1
} {1}

test parse-8 {
    set foo [list a \
		 b \
		 c]
    set foo
} {a b c}

test parse-9 {
    set foo "foo\nbar"
} {foo
bar}

test parse-10 {
    set foo \u69
} {i}

test parse-11 {
    set foo \u262f
} \u262f

test parse-12 {
    set foo [list a "\n" b]
    lindex $foo 1
} {
}

test parse-13 {
    set foo foobar
    set bar ${foo}/beebop
    set bar
} {foobar/beebop}

test parse-14 {
     lindex {a\[ b} 0
} "a\["

#test parse-15 {
#     lindex "a\[ b" 0
#} "a\["
