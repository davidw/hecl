
test catch-1 {
    catch fakecommand foo
    set foo
} {{ERROR {Command fakecommand does not exist}}}