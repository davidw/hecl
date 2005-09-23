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
    append &out [$ps foo]
    set ps printstuff2
    append &out [$ps foo]
    set out
} {foonew foo}

test parse-6 {
    foreach x y {
	set z 1
    }XXX
} {{ERROR {extra characters after close-brace}}}

test parse-7 {
    set foo {};set bar 1
} {1}