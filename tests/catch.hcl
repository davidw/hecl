
test catch-1 {
    catch fakecommand foo
    set foo
} {{ERROR {Command 'fakecommand' does not exist} 1}}

test catch-2 {
    catch {set foo 1} bar
    set bar
} {1}