#!/usr/bin/python
import re
import mechanize

start_year = 2009
end_year = 2012

br = mechanize.Browser()
br.set_proxies({"http": "proxy.uns.ac.rs:8080",
    })
for year in range(start_year, end_year):
    br.open("http://www.e-drustvo.org/proceedings/YuInfo" + str(year) + "/html/search.html")
    for link in br.links():
        print link
    for link in br.links(url_regex="pdf"):
        print link
    #    br.follow_link(link)  # takes EITHER Link instance OR keyword args
    #    br.back()
