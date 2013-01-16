#!/bin/bash
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
DATA_DIR="$SCRIPT_DIR/../data/"
echo $DATA_DIR
proceedings="20*-proceedings"
if [[ -n "$2" ]]; then
    proceedings="$2-proceedings"
fi
html=`grep "oblasti" -i "$DATA_DIR"$proceedings -A20 | grep htm`
#echo "$html"
if [ "$1" == "links" ]; then
    links=`echo "$html" | sed -e 's/.*<a href="//g' | sed -e 's/".*//g'`
    echo "$links"
elif [ "$1" == "text" ]; then
    text=`echo "$html" | sed -e 's/.*<a href="[^>]*>//g' | sed -e 's/<.*//g'`
    echo "$text"
fi
