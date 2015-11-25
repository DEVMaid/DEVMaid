#!/usr/bin/python 

""" README 
Author: Ken Wu

https://github.com/DEVMaid/DEVMaid/issues/12

This script is to combine all separate js resources from elFinder (https://github.com/Studio-42/elFinder/) and reduce a single script
Ideally, the single output script should be saved to ./src/main/webapp/scripts/js/elfinder/elfinder.full.js

Parameters:
1)	local js directory location of the elFinder

Example Usage: 
./combineElFinderScriptResources.py /Users/ken/workspace/elFinder

"""

import sys
import os, fnmatch
from os.path import join

if len(sys.argv) != 2:
    print "Illegal number of parameters"
    print "Example of usage: ./combineElFinderScriptResources.py /Users/ken/workspace/elFinder"
    sys.exit()
    
PROJECT_ROOT = sys.argv[1]

""" configurations start here """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
"""
scriptsToBeCombined = ['js/elFinder.js',
'js/elFinder.version.js',
'js/jquery.elfinder.js',
'js/elFinder.options.js',
'js/elFinder.history.js',
'js/elFinder.command.js',
'js/elFinder.resources.js',
'js/jquery.dialogelfinder.js',
'js/i18n/elfinder.en.js',
'js/ui/button.js',
'js/ui/contextmenu.js',
'js/ui/cwd.js',
'js/ui/dialog.js',
'js/ui/navbar.js',
'js/ui/overlay.js',
'js/ui/panel.js',
'js/ui/path.js',
'js/ui/places.js',
'js/ui/searchbutton.js',
'js/ui/sortbutton.js',
'js/ui/stat.js',
'js/ui/toolbar.js',
'js/ui/tree.js',
'js/ui/uploadButton.js',
'js/ui/viewbutton.js',
'js/ui/workzone.js',
'js/commands/archive.js',
'js/commands/back.js',
'js/commands/copy.js',
'js/commands/cut.js',
'js/commands/download.js',
'js/commands/duplicate.js',
'js/commands/edit.js',
'js/commands/extract.js',
'js/commands/forward.js',
'js/commands/getfile.js',
'js/commands/help.js',
'js/commands/home.js',
'js/commands/info.js',
'js/commands/mkdir.js',
'js/commands/mkfile.js',
'js/commands/netmount.js',
'js/commands/open.js',
'js/commands/paste.js',
'js/commands/quicklook.js',
'js/commands/quicklook.plugins.js',
'js/commands/reload.js',
'js/commands/rename.js',
'js/commands/resize.js',
'js/commands/rm.js',
'js/commands/search.js',
'js/commands/sort.js',
'js/commands/up.js',
'js/commands/upload.js',
'js/commands/view.js']

#This one is to filter out any un-wanted contents for each file
contentsToBeFilteredOut=[('"use strict"', ''), ('"use strict";', ''), ('../img/','/images/elfinder/')]

jsHeaderStr = """
/*!
 * elFinder - file manager for web
 * Version 2.1 (Nightly: 916091e) (2013-08-20)
 * http://elfinder.org
 * 
 * Copyright 2009-2013, Studio 42
 * Licensed under a 3 clauses BSD license
 */
(function($) {

"""

jsFooterStr = """
})(jQuery);
"""

cssHeaderStr=''
cssFooterStr=''

outputJSFileName='combinedScript.js.out'
outputCSSFileName='combinedStyleSheet.css.out'

""" configurations ends here """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
"""

def find_files(directory, pattern):
    for root, dirs, files in os.walk(directory):
        for basename in files:
            if fnmatch.fnmatch(basename, pattern):
                filename = os.path.join(root, basename)
                yield filename

def retrieveFileAndProcess(path):
	thisScriptFile = open(wholePath, 'r')
	thisScriptContent = thisScriptFile.read()
	return thisScriptContent

def processAllFiles(allScripts):
	thisOutput=""
	for script in allScripts:
		if script[0]=='/':
			wholePath = script
		else:
			wholePath = join(PROJECT_ROOT,script)
		thisScriptHeader = """
/*
* File: %s
*/
		""" %(wholePath)

		thisScriptFile = open(wholePath, 'r')
		thisScriptContent = thisScriptFile.read()

		for contentTuple in contentsToBeFilteredOut:
			thisScriptContent = thisScriptContent.replace(contentTuple[0], contentTuple[1])
		filesBeenProcessed[wholePath] = True
		thisOutput=thisOutput+thisScriptHeader
		thisOutput=thisOutput+thisScriptContent
	return thisOutput

def writeToFile(f, c):
	target = open(f, 'w')
	target.truncate()
	target.write(c)
	target.close()

""" Now it starts the real execution of the script """
#Now this part is to scan for the whole js folder to make sure all files have been combined
filesBeenProcessed = {}
output=jsHeaderStr
output+=processAllFiles(scriptsToBeCombined)
JS_PROJECT_ROOT=join(PROJECT_ROOT,'js')
notProcessed=0
for filename in find_files(JS_PROJECT_ROOT, '*.js'):
    if filename not in filesBeenProcessed:
    	#print 'Warning: file - %s was new and did not get processed before..now processing it'%(filename)
    	output+=processAllFiles([filename])
    	notProcessed += 1

if notProcessed == 0:
	print 'No new file was added remotely and needed to be processed'

output=output+jsFooterStr
writeToFile(outputJSFileName, output)

output=cssHeaderStr
#Now for the css file
CSS_PROJECT_ROOT=join(PROJECT_ROOT,'css')
for filename in find_files(CSS_PROJECT_ROOT, '*.css'):
	output+=processAllFiles([filename])
output=output + cssFooterStr
writeToFile(outputCSSFileName, output)

print 'Combined contents have been writen to js file: %s'%(outputJSFileName) 
print 'Combined contents have been writen to file: %s'%(outputCSSFileName) 
print 'Next is to run the following command to copy the the elFinder.full.js:'
print '   cp ./%s ../web/src/main/webapp/scripts/js/elfinder/elfinder.full.js' %(outputJSFileName)
print '   cp ./%s ../web/src/main/webapp/stylesheets/elfinder/elfinder.full.css' %(outputCSSFileName)


