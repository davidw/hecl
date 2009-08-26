# Test the http commands.

test http-1 {
    puts "WARNING: this test will hang if you're not connected to the internet!"
    set res [http.geturl http://www.hecl.org]
    list [hget $res ncode]
} {200}
