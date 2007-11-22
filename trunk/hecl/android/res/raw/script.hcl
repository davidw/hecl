activity setview [reslookup R.layout.main]
set button [button -id [reslookup R.id.execute]]
set layout [linearlayout -id [reslookup R.id.mainlayout]]

log "Hello, World!"
$button settext "GO!"
log [$button length]

set b2 [button -text "ALRIGHT!" -layout_width 100 -layout_height 50 -layout $layout]
