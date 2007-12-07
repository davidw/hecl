# Tcl versions of Rivet commands.

# Copyright 2003-2004 The Apache Software Foundation

# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at

#	http://www.apache.org/licenses/LICENSE-2.0

# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# $Id: tclrivet.tcl 561309 2007-07-31 12:27:34Z davidw $

package provide tclrivet 0.1

source [file join [file dirname [info script]] tclrivetparser.tcl]

# rivet --
#
#	The rivet command runs a file in Rivet-like environment.
#
# Arguments:
#	filename - the name of the file to run.
#
# Side Effects:
#	Many, potentially.  Output is sent to stdout.
#
# Results:
#	None.

proc rivet { filename } {
    set fl [open $filename]
    set parsed [tclrivetparser::parserivetdata [read $fl]]
    close $fl
    eval $parsed
}

proc include { filename } {
    set fl [open $filename]
    fconfigure $fl -translation binary
    puts -nonewline [ read $fl ]
    close $fl
}

# We need to fill these in, of course.

proc makeurl {} {}
proc headers {} {}
proc load_env {} {}
proc load_headers {} {}
proc var {} {}
proc var_qs {} {}
proc var_post {} {}
proc upload {} {}
proc parse {} {}
proc no_body {} {}
proc env {} {}
proc abort_page {} {}
proc virtual_filename {} {}
