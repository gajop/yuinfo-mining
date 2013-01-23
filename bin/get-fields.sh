#!/bin/bash
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
DATA_DIR="$SCRIPT_DIR/../data/"
if [[ -e "$DATA_DIR" ]]; then
    DATA_DIR="../data/";
fi
proceedings="20*-proceedings.html"
if [[ -n "$2" ]]; then
    proceedings="$2-proceedings.html"
fi
html=`grep "oblasti" -i "$DATA_DIR"$proceedings -A20 | grep htm`
if [ "$1" == "links" ]; then
    links=`echo "$html" | sed -e 's/.*<a href="//g' | sed -e 's/".*//g'`
    echo "$links"
elif [ "$1" == "text" ]; then
    text=`echo "$html" | sed -e 's/.*<a href="[^>]*>//g' | sed -e 's/<.*//g'`
    echo "$text"
fi
