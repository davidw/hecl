# heclfile.hcl - this lets us run a default file or select some other
# file to run.

# Fetch the stored file, if it exists.  Otherwise, use 'hecl.hcl' on
# the first root.
proc StoredHeclFile {} {
    set hf ""
    catch {
	set hf [rms.get heclfile]
    }
    if { ne $hf "" } { return $hf }
    set root [lindex [file.devs] 0]
    return "file:///${root}/hecl.hcl"
}

# Save the selected file.
proc SaveHeclFile {fname} {
    if { < 0 [llen [rms.list heclfile]] } {
	rms.set heclfile 1 $fname
    } else {
	rms.add heclfile $fname
    }
}

# Callback when we select a file.
proc SelectedFile {fname} {
    SaveHeclFile $fname
    source $fname
}

set fname [StoredHeclFile]

set err ""
catch {
    source $fname
} err

# If there's a problem, we run a file selection dialog.
if { ne "" $err } {
    set ff [filefinder -selectedcmd SelectedFile]
    set a [lcdui.alert -text "Error loading $fname : ${err}\nPlease select another file to run" -type confirmation -timeout forever]
    $a setcurrent $ff
}
