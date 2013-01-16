#!/usr/bin/python
import re
import mechanize
import subprocess
import os

start_year = 2006
end_year = 2012
links = None
text = None

SCRIPT_PATH = os.path.realpath(__file__)
SCRIPT_DIR = os.path.dirname(SCRIPT_PATH)
DATA_DIR = os.path.realpath(SCRIPT_DIR + "/../data/")

def makeDirs():
    if not os.path.exists(DATA_DIR):
        os.mkdir(DATA_DIR)
    for year in range(start_year, end_year + 1):
        yearDir = DATA_DIR + str(year) + "/"
        if not os.path.exists(yearDir):
            os.mkdir(yearDir)
        

def getDocListingOnPage(br):
    url = br.geturl()
    response = mechanize.urlopen(url)
    return response.read()

def grabAllDocs():
    br = mechanize.Browser()
    br.set_proxies({"http": "proxy.uns.ac.rs:8080", })
    baseURL = "http://www.e-drustvo.org/proceedings/YuInfo$YEAR/html/proceedings.html"
    for year in range(start_year, end_year + 1):
        url = baseURL.replace("$YEAR", str(year))
        [links, text] = getTopicsByYear(year)
        #currentPage = "http://www.e-drustvo.org/proceedings/YuInfo" + str(year) + "/html/search.html"
        br.open(url)
        #response = mechanize.urlopen(currentPage)
        #print response.read()
        for link in br.links():
            if link.url in links:
                br.follow_link(link)
                docListing = getDocListingOnPage(br)
                yearDir = DATA_DIR + str(year) + "/"
                f = open(yearDir + "listing.html", "w")
                f.write(docListing)
                f.close()
                br.back()
#        for link in br.links(url_regex="pdf"):
#            print link
        #      # takes EITHER Link instance OR keyword args
        #    br.back()


def grabProceedings():
    baseURL = "http://www.e-drustvo.org/proceedings/YuInfo$YEAR/html/proceedings.html"
    for year in range(start_year, end_year + 1):
        url = baseURL.replace("$YEAR", str(year))
        response = mechanize.urlopen(url)
        f = open(DATA_DIR + str(year) + "-proceedings", "w")
        f.write(response.read())
        f.close()

def getTopicsByYear(year):
    linkStr = subprocess.check_output(["/bin/bash", "get-fields.sh", "links", str(year)])
    textStr = subprocess.check_output(["/bin/bash", "get-fields.sh", "text", str(year)])
    links = []
    texts = []
    for link in linkStr.split('\n'):
        link = link.strip()
        if len(link) > 0:
            links.append(link)            
    for text in textStr.split('\n'):
        text = text.strip()
        if len(text) > 0:
            texts.append(text)
    return [links, texts]
    
makeDirs()
grabProceedings()
grabAllDocs()
