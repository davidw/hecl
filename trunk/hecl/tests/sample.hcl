set bar [list a b   c]

puts $bar

puts "X${bar}X"

puts [listlen $bar]

set bar {
    a
    b
    c
}

puts "Should be 3: [listlen $bar]"

set bar "1 2   3"
puts [listlen $bar]

set bar {
    a [b c] d
}

puts "Should be: 3 is: [listlen $bar]"

set bar {
    a {b c} d
}

puts "Should be: 3 is: [listlen $bar]"
puts "Should be: b c is: [index $bar 1]"


set foo "hello world"

puts [set foo]

puts $foo

+ 1 3

puts blah

puts [- [+ 1 2] 1]

puts "more ambitious [+ 12 12] xxx"

if { = 1 1 } {
    puts "yeah"
} else {
    puts "nah"
}

set foo 1
puts "foo is $foo"

while { < $foo 100 } {
#    puts "yeah $foo"
    set foo [+ $foo 1]
}

proc foo {x} {
    puts "X is $x"
#    puts "foo is $foo"
}

foo 1

foreach x {a b c} {
    puts "x is now $x"
}

foreach x [list a b c] {
    puts "x is now $x"
}

set i 0

while true {
    if { > $i 100 } {
	break
    } else {
	puts "i is $i"
    }
    set i [+ $i 1]
}

set i 0
while {< $i 100} {
    puts "continue i is $i"
    set i [+ $i 1]
    continue
    puts "i is NOW $i"
}

set lst [list a [list 1 2 3] b c]

puts "lst is $lst"

proc a {} {
    puts a
    b
}
proc b {} {
    puts b
    c
}
proc c {} {
    puts c
    sdfsf
}

puts [intro commands]

source tests/test.hcl

a
bsdfdfds foo


puts "Tests completed"