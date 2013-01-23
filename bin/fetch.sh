#!/bin/bash

start_year=2006
end_year=2012

start_id=0
end_id=999
for ((year=$start_year; year<=end_year; year++)) do
    for ((id=$start_id; id<=end_id; id++)) do
        id_string=$id
        if (($id < 10)); then
            id_string="00""$id"
        elif (($id < 100)); then
            id_string="0""$id"
        fi
        url="http://www.e-drustvo.org/proceedings/YuInfo""$year""/html/pdf/""$id_string"".pdf"
        wget $url
    done
done
