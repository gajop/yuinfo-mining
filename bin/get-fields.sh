#!/bin/bash
html=`grep "oblasti" -i 20*-proceedings -A20 | grep htm`
txt=``
links=`echo "$html" | sed -e 's/.*<a href="//g' | sed -e 's/".*//g'`
echo "$html"
#echo "$links"
echo "$txt"
