#!/usr/bin/env bash

app='/Applications/Eclipse/eclipse.app'

if [ ! -d "$app" ]; then
  echo "ERROR: can't find Eclipse app! (missing directory \"$app\")"
  echo "       (please download Eclipse tar.gz file and unpack into /Applications)"
  exit 1
fi

# your workspace can be any directory you like
workspace=$HOME/eclipse/lpp
if [ ! -d $workspace ];then
  echo "ERROR: did not find directory \"$workspace\""
  echo "       That directory is the presumed 'Eclipse workspace' which will hold"
  echo "       references to both the LinkDeveloper project and the LinkDeveloperExample"
  echo "       project.  Please create that directory, and re-run this script."
 #mkdir -p $workspace
fi

open $app --args -data $workspace
echo "Note: See the README.md file regarding the use of this project from"
echo "      withing Eclipse."
