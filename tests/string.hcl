
test string-1 {
    strlen "foo"
} 3

test string-2 {
    strindex "foo" 1
} o

test string-3 {
    strindex "foo" 10
} ""

test string-4 {
    set foo foobar
    strlen $foo
} 6

test string-bytelen-1 {
     strbytelen abc
} 3

test string-bytelen-2 {
     strbytelen €
} 3

test string-bytelen-3 {
     strbytelen abc€
} 6

test string-cmp-1 {
     strcmp aaa aaa
} 0
test string-cmp-2 {
     strcmp aaa b
} -1
test string-cmp-3 {
     strcmp bbb aaa
} 1

test string-len-1 {
     strlen abc
} 3

# These two seem to have troubles if the LANG environmental variable
# isn't set, on Linux, in an rxvt.
test string-len-2 {
     strlen €
} 1

test string-len-3 {
     strlen abc€
} 4

test string-range-1 {
     strrange abcdefg 0 -1
} abcdefg
test string-range-2 {
     strrange abcdefg 2 -2
} cdef
test string-range-3 {
     strrange abcdefg 4 4
} e

test string-repeat-1 {
     strrep abc 3
} abcabcabc

test string-lower-1 {
     strlower ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÜ€0123456789
} abcdefghijklmnopqrstuvwxyzäöü€0123456789

test string-upper-1 {
     strupper abcdefghijklmnopqrstuvwxyzäöü€0123456789
} ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÜ€0123456789

test string-trim-1 {
     strtrim abcdefga a
} bcdefg

test string-trim-2 {
     strtrim abcdefg x
} abcdefg

test string-trim-3 {
     strtrim "\t\n\r abcdefg \n\t\r"
} abcdefg

test string-trimr-1 {
     strtrimr abcdefg g
} abcdef

test string-trimr-2 {
     strtrimr abcdefg x
} abcdefg

test string-trimr-3 {
     strtrimr "\t\n\r abcdefg \n\t\r"
} "\t\n\r abcdefg"

test string-find-1 {
     strfind b abcdefg
} 1
test string-find-2 {
     strfind b abcdefgb
} 1
test string-find-3 {
     strfind b abcdefg 2
} -1
test string-find-2 {
     strfind b abcdefgb 4
} 7

test string-last-1 {
     strlast b abcdefg
} 1
test string-last-2 {
     strlast b abcdefgb
} 7
test string-last-3 {
     strlast b abcdefg 2
} 1
test string-last-2 {
     strlast b abcdefgb 0
} -1

test emptystring-1 {
    set a [puts a]
    append $a x
    set b [puts b]
} {}

test emptystring-2 {
    set a [puts a]
    append $a x
    set b [puts b]
    set a
} {x}

test strreplace-1 {
    set r {}
    append $r [strreplace {foo x} foobar]
    append $r "\n"
    append $r [strreplace {oo 1111} foobar]
    append $r "\n"
    append $r [strreplace {b aaa} foobar]
    append $r "\n"
    append $r [strreplace {r ""} foobar]
    set r
} {xbar
f1111bar
fooaaaar
fooba}

test strreplace-2 {
    strreplace {xxx yyy} foobar
} {foobar}

test strreplace-3 {
    strreplace {hi hello} xxxhiyyyhizzzhi
} {xxxhelloyyyhellozzzhello}

test strreplace-4 {
    strreplace {hi hello} xxxhiyyyhizzz
} {xxxhelloyyyhellozzz}

### Local Variables:
### mode:tcl
### coding:utf-8
### End:
