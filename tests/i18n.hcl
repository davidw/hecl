test i18n-1 {
    set x [base64::encode "abcd"]
    base64::decode $x
} {abcd}

test i18n-2 {
    set x [base64::encode "«Andrò avanti»"]
    strlen [base64::decode $x]
} [strlen "«Andrò avanti»"]
