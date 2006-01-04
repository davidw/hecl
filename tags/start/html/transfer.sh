#!/bin/sh

find . -name "*~" | xargs rm

rsync -avz -e ssh . dedasys.com:/var/www/sites/hecl.org/html
