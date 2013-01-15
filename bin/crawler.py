#!/usr/bin/python
import re
import mechanize

start_year = 2006
end_year = 2012

def grabDocs():
    br = mechanize.Browser()
#br.set_proxies({"http": "proxy.uns.ac.rs:8080", })
    for year in range(start_year, end_year + 1):
        currentPage = "http://www.e-drustvo.org/proceedings/YuInfo" + str(year) + "/html/search.html"
        br.open(currentPage)
        response = mechanize.urlopen(currentPage)
        print response.read()
        for link in br.links():
            print link
        for link in br.links(url_regex="pdf"):
            print link
        #    br.follow_link(link)  # takes EITHER Link instance OR keyword args
        #    br.back()


def grabProceedings():
    baseURL = "http://www.e-drustvo.org/proceedings/YuInfo$YEAR/html/proceedings.html"
    for year in range(start_year, end_year + 1):
        url = baseURL.replace("$YEAR", str(year))
        print url
        response = mechanize.urlopen(url)
        f = open(str(year) + "-proceedings", "w")
        f.write(response.read())
        f.close()

grabProceedings()
