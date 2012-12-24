#!/usr/bin/python
from __future__ import print_function
import os
import re
encoding = "utf-8"

SCRIPT_PATH = os.path.realpath(__file__)
SCRIPT_DIR = os.path.dirname(SCRIPT_PATH)
DATA_DIR = os.path.realpath(SCRIPT_DIR + "/../data")

def parseReferences(content):
    references = []
    refStartMatch = re.search("LITERATURA.*", content)
    if refStartMatch:
        startPos = refStartMatch.span()[0]
        endPos = refStartMatch.span()[1]
        refLines = content[endPos:]
        p = re.compile(r'\[\d*\]')
        for part in p.split(refLines):
            reference = ""
            for l in part.split("\n"):
                if l.strip() == "":
                    break
                reference = reference + " " + l
            if reference.strip() != "":
                references.append(reference)
    return references

def preAbstract(content):
    abstractStart = int(10e50)

    #todo add checks for cyrillic 
    abstractRSMatch = re.search(r"Sadr|Apstrakt", content)
    if abstractRSMatch:
        abstractStart = min(abstractRSMatch.span()[0], abstractStart)
    abstractENMatch = re.search(r"Abstract", content)
    if abstractENMatch:
        abstractStart = min(abstractENMatch.span()[0], abstractStart)

    return content[:abstractStart]

def parseTitle(content):
    titleEN = None
    titleRS = None
    preAbstractContent = preAbstract(content)
    print(preAbstractContent)
    titleENMatch = re.search(r"[A-Z\s]*", preAbstractContent)

    if titleENMatch:
        begin, end = titleENMatch.span()
        titleEN = preAbstractContent[begin:end]
        print("EN: " + titleEN)
    return [titleEN, titleRS]

for fname in os.listdir(DATA_DIR):
    if re.match(".*.txt", fname):
        filePath = os.path.realpath(DATA_DIR + "/" + fname)
        print("Parsing plaintext file: " + filePath)
        content = open(filePath).read()
#        print(content)

        [titleEN, titleRS] = parseTitle(content)
        continue
        authorsEN = None
        authorsRS = None
        abstractEN = None
        abstractRS = None
        references = parseReferences(content)
        
        if titleEN or titleRS:
            print("Titles: ")
            if titleEN:
                print("Title EN: " + titleEN)
            if titleRS:
                print("Title RS: " + titleRS)
        print("References: ")
        for i, ref in enumerate(references):
            print(str(i+1) + "." + ref)
        break

