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
