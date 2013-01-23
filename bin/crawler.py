#!/usr/bin/python
import re
import mechanize
import subprocess
import os
import sys
#import cPickle as Serializer
import json as Serializer
from urlparse import urljoin

start_year = 2006
end_year = 2012

SCRIPT_PATH = os.path.realpath(__file__)
SCRIPT_DIR = os.path.dirname(SCRIPT_PATH) + "/"

sys.path.insert(0, os.path.realpath(SCRIPT_DIR + "../beautifulsoup4-4.1.3/"))
from bs4 import BeautifulSoup

DATA_DIR = os.path.realpath(SCRIPT_DIR + "/../data") + "/"

br = mechanize.Browser()
#br.set_proxies({"http": "proxy.uns.ac.rs:8080", })

def makeDirs():
    if not os.path.exists(DATA_DIR):
        os.mkdir(DATA_DIR)
    for year in range(start_year, end_year + 1):
        yearDir = DATA_DIR + str(year) + "/"
        if not os.path.exists(yearDir):
            os.mkdir(yearDir)
        
def getArticlesFromPage(docListing):
    soup = BeautifulSoup(docListing)
    soup = BeautifulSoup(soup.prettify())
    aTags = soup.find_all("a")
    aTags = [aTag for aTag in aTags if re.match(r"pdf/\d+\.pdf", aTag.get("href"))]

    articles = []
    for aTag in aTags:
        authors = aTag.parent.previous_sibling.previous_sibling.previous_sibling.previous_sibling.text
        authors = authors.strip()
        title = aTag.text.strip()
        pdfName = re.sub("pdf/(\d+)\.pdf", r"\1.pdf", aTag.get("href"))

        article = {"authors":authors, "title":title, "pdfName":pdfName, "url":aTag.get("href")}
        articles.append(article)

    return articles


def grabAllListings():
    baseURL = "http://www.e-drustvo.org/proceedings/YuInfo$YEAR/html/proceedings.html"
    for year in range(start_year, end_year + 1):
        yearDir = DATA_DIR + str(year) + "/"
        url = baseURL.replace("$YEAR", str(year))
        [links, text] = getTopicsByYear(year)
        #currentPage = "http://www.e-drustvo.org/proceedings/YuInfo" + str(year) + "/html/search.html"
        br.open(url)
        #response = mechanize.urlopen(currentPage)
        #print response.read()
        brLinks = [link for link in br.links()]
        for link in brLinks:
#            http://www.e-drustvo.org/proceedings/YuInfo2006/html/simulation.html
            if link.url in links:
                print link.url + " year : " + str(year)
                topic = link.url.split('.htm')[0]

                topicDir = yearDir + topic + "/"
                if not os.path.exists(topicDir):
                    os.mkdir(topicDir)

                docListing = br.follow_link(link).read()
                filePath = yearDir + topic + "-listing.html"
                f = open(filePath, "w")
                f.write(docListing)
                f.close()

                articles = getArticlesFromPage(docListing)
                f = open(yearDir + topic + "-meta.txt", "w")
                serializedArticles = Serializer.dumps(articles)
                f.write(serializedArticles)
                f.close()

                listingURL = br.geturl()
                for article in articles:
                    link = urljoin(listingURL, article["url"])
                    print "Fetching file... " + link
                    try:
                        pdfContent = mechanize.urlopen(link).read()
                        f = open(topicDir + article["pdfName"], "w")
                        f.write(pdfContent)
                        f.close()
                    except:
                        print "Failure fetching file " + link

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
        filePath = DATA_DIR + str(year) + "-proceedings.html"
        f = open(filePath, "w")
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
grabAllListings()
