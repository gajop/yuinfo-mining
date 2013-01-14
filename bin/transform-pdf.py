#!/usr/bin/python
from __future__ import print_function
import os
import multiprocessing
import re

SCRIPT_PATH = os.path.realpath(__file__)
SCRIPT_DIR = os.path.dirname(SCRIPT_PATH)
DATA_DIR = os.path.realpath(SCRIPT_DIR + "/../data")

def execute(name):
    os.execv("/usr/bin/pdftotext", ["", filePath])

for fname in os.listdir(DATA_DIR):
    if re.match(".*.pdf", fname):
        filePath = os.path.realpath(DATA_DIR + "/" + fname)
        print(filePath)
        if os.fork() == 0:
            os.execv("/usr/bin/pdftotext", ["", filePath])
