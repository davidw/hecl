
test throw-1 {
    throw "Houston, we have a problem"
} {{ERROR {Houston, we have a problem}} {throw 2}}

test throw-2 {
    throw "Page not found" 404ERROR
} {{404ERROR {Page not found}} {throw 2}}
