
test eval-1 {
    set code {set foo 1}
    eval $code
    set foo
} {1}