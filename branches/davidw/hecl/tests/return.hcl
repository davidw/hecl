proc returntest {arg} {
    return [+ $arg 10]
}

test return-1 {
    returntest 10
} 20
