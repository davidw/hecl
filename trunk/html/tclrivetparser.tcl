# tclrivetparser.tcl -- parse Rivet files in pure Tcl.

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

# $Id: tclrivetparser.tcl 561309 2007-07-31 12:27:34Z davidw $

package provide tclrivetparser 0.1

namespace eval tclrivetparser {
    set starttag <?
    set endtag   ?>
    set outputcmd {puts -nonewline}
    namespace export parserivetdata
}

# tclrivetparser::setoutputcmd --
#
#	Set the output command used.  In regular Rivet scripts, we use
#	puts, but that might not be ideal if you want to parse Rivet
#	pages in a Tcl script.
#
# Arguments:
#	newcmd - if empty, return the current command, if not, set the
#	command.
#
# Side Effects:
#	May set the output command used.
#
# Results:
#	The current output command.

proc tclrivetparser::setoutputcmd { {newcmd ""} } {
    variable outputcmd

    if { $outputcmd == "" } {
	return $outputcmd
    }
    set outputcmd $newcmd
}

# tclrivetparser::parse --
#
#	Parse a buffer, transforming <? and ?> into the appropriate
#	Tcl strings.  Note that initial 'puts "' is not performed
#	here.
#
# Arguments:
#	data - data to scan.
#	outbufvar - name of the output buffer.
#
# Side Effects:
#	None.
#
# Results:
#	Returns the $inside variable - 1 if we are inside a <? ?>
#	section, 0 if we outside.

proc tclrivetparser::parse { data outbufvar } {
    variable outputcmd
    variable starttag
    variable endtag
    set inside 0

    upvar $outbufvar outbuf

    set i 0
    set p 0
    set len [expr {[string length $data] + 1}]
    set next [string index $data 0]
    while {$i < $len} {
	incr i
	set cur $next
	set next [string index $data $i]
	if { $inside == 0 } {
	    # Outside the delimiting tags.
	    if { $cur == [string index $starttag $p] } {
		incr p
		if { $p == [string length $starttag] } {
		    append outbuf "\"\n"
		    set inside 1
		    set p 0
		    continue
		}
	    } else {
		if { $p > 0 } {
		    append outbuf [string range $starttag 0 [expr {$p - 1}]]
		    set p 0
		}
		switch -exact -- "$cur" {
		    {\{} {
			append outbuf "\\{"
		    }
		    {\}} {
			append outbuf "\\}"
		    }
		    "\$" {
			append outbuf "\\$"
		    }
		    {[} {
			append outbuf {\[}
		    }
		    {]} {
			append outbuf {\]}
		    }
		    "\"" {
			append outbuf "\\\""
		    }
		    "\\" {
			append outbuf "\\\\"
		    }
		    default {
			append outbuf $cur
		    }
		}
		continue
	    }
	} else {
	    # Inside the delimiting tags.
	    if { $cur == [string index $endtag $p] } {
		incr p
		if { $p == [string length $endtag] } {
		    append outbuf "\n$outputcmd \""
		    set inside 0
		    set p 0
		}
	    } else {
		if { $p > 0 } {
		    append outbuf [string range $endtag 0 $p]
		    set p 0
		}
		append outbuf $cur
	    }
	}
    }

    return $inside
}


# tclrivetparser::parserivetdata --
#
#	Parse a rivet script, and add the relavant opening and closing
#	bits.
#
# Arguments:
#	data - data to parse.
#
# Side Effects:
#	None.
#
# Results:
#	Returns the parsed script.

proc tclrivetparser::parserivetdata { data } {
    variable outputcmd
    set outbuf {}
    append outbuf "$outputcmd \""
    if { [parse $data outbuf] == 0 } {
	append outbuf "\"\n"
    }
    return $outbuf
}
