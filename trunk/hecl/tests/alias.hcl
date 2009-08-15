
test alias-1 {
    alias set setalias
    set foo bar
    list [set foo] [setalias foo]
} {bar bar}

