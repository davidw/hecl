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
    puts $stuff
}

test parse-3 {
    set ps pritnstuff
    $ps foo
} {}
