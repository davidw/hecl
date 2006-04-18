#!/bin/sh

find . -name "*~" | xargs rm

rsync -avz -e ssh . dedasys.com:/var/www/hecl.org/html
