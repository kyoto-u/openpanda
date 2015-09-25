#! /usr/bin/python
#coding: utf-8

# Copyright (c) 2013, Daisuke Deguchi
# All rights reserved.
# 
# Redistribution and use in source and binary forms, with or without modification,
# are permitted provided that the following conditions are met:
# 
#     1. Redistributions of source code must retain the above copyright notice,
#        this list of conditions and the following disclaimer. 
# 
#     2. Redistributions in binary form must reproduce the above copyright notice,
#        this list of conditions and the following disclaimer in the documentation
#        and/or other materials provided with the distribution.
# 
#     3. Neither the name of the Nagoya University nor the names of its contributors
#        may be used to endorse or promote products derived from this software
#        without specific prior written permission. 
# 
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
# IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
# FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
# CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
# DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
# DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
# IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
# THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.


import os
import sys
import datetime, time
import re

import argparse

import polib
import jprops

try:
	from collections import OrderedDict
except ImportError:
	from ordereddict import OrderedDict

# Sakai source path
srcroot = '..'

# Search pattern for translated .properties
tpro = re.compile( '.+_[a-zA-Z][a-zA-Z](_DEBUG|_Pearson|_UPMC)?$', re.IGNORECASE )
tprolng = re.compile( '.+_[a-zA-Z][a-zA-Z]$', re.IGNORECASE )

# Change working directory to the script directory (l10n)
os.chdir( os.path.dirname( os.path.abspath( __file__ ) ) )

# Escaping HTML special characters
html_escape_table = {
	"&": "&amp;",
	'"': "&quot;",
	"'": "&apos;",
	">": "&gt;",
	"<": "&lt;",
}

def html_escape( text ):
	return "".join( html_escape_table.get( c, c ) for c in text )

def html_unescape(s):
	s = s.replace( "&lt;", "<" )
	s = s.replace( "&gt;", ">" )
	s = s.replace( "&quot;", '"' )
	s = s.replace( "&apos;", "'" )
	s = s.replace( "&amp;", "&" )
	return s

# Enumerate .properties files
def EnumPropertiesFiles( root, verbose = False ):
	# Load ignore list
	ignore = []
	f = open( "ignore.txt", "rU" )
	for line in f:
		if not line.startswith( "#" ) and len( line ) > 0:
			#print line.strip( )
			ignore.append( line.strip( ) )
	f.close( )


	list = []
	for ( path, dirs, files ) in os.walk( root ):
		# Ignore special folders
		if path.find( "apache" ) >= 0 or path.find( "target" ) >= 0 or path.find( "commons-logging" ) >= 0:
			continue

		for filename in files:
			( fname, ext ) = os.path.splitext( filename );

			if  ext == ".properties":
				found = False
				for sss in ignore:
					if sss.endswith( ".properties" ):
						if filename == sss:
							found = True
							break
					elif filename.find( sss ) >= 0:
						found = True
						break

				if not found:
					if tpro.match( fname ) is None:
						list.append( ( os.path.join( path, filename ), 1 ) )
					elif tprolng.match( fname ):
						list.append( ( os.path.join( path, filename ), 0 ) )
				elif verbose:
					print "  [ignored] " + filename
	return( list )


# Save .pot file from .properties files found under searchpath
def PotFromProperties( searchpath, language, verbose = False ):
	# Construct pot file
	pot = polib.POFile( )
	pot.metadata = {
		'Project-Id-Version': 'TMX Project 0.1a',
		'Report-Msgid-Bugs-To': 'ddeguchi@nagoya-u.jp',
		'POT-Creation-Date': datetime.datetime.utcnow( ).strftime("%Y-%m-%d %H:%M") + "+00:00",
		'PO-Revision-Date': 'YEAR-MO-DA HO:MI+ZONE',
		'Last-Translator': 'FULL NAME <EMAIL@ADDRESS>',
		'Language-Team': 'LANGUAGE <EMAIL@ADDRESS>',
		'MIME-Version': '1.0',
		'Content-Type': 'text/plain; charset=utf-8',
		'Content-Transfer-Encoding': '8bit',
	}

	found = False
	list = EnumPropertiesFiles( searchpath, verbose )
	for ( filename, ismaster ) in list:
		if not ismaster:
			continue

		found = True
		fr = open( filename, 'rU' )
		p = jprops.load_properties( fr, OrderedDict )
		fr.close( )

		count = 1
		for ( key, value ) in p.items( ):
			if value != "":
				fname = filename[len( srcroot )+1:]
				e = polib.POEntry(
					comment = key,
					msgctxt = fname + ':' + key,
					msgid = value,
					msgstr = value if language is None else '',
					#msgid = html_escape( value ),
					#msgstr = html_escape( value ) if language is None else '',
					occurrences = [( fname, count )]
				)
				pot.append( e )
				count = count + 1

	if not found:
		return( None )

	if language is not None:
		for ( filename, ismaster ) in list:
			if filename.find( '_' + language ) < 0:
				continue

			fr = open( filename, 'rU' )
			p = jprops.load_properties( fr )
			fr.close( )

			( root, ext ) = os.path.splitext( filename );
			master = root[len( srcroot )+1:-len(language)-1] + ext

			for ( key, value ) in p.items( ):
				e = pot.find( key, by='comment', msgctxt=master+':'+key )
				if e is not None:
					e.msgstr = value

	return( pot )



# Update .properties files from .po file
def PoToProperties( pofilename, language, keep_comments, verbose = False ):
	if language is None:
		return

	pp = { }
	po = polib.pofile( pofilename )
	for e in po:
		for ( fname, key ) in e.occurrences:
			if fname not in pp:
				pp[ fname ] = OrderedDict( )
			# Use original text if translation was not finished
			pp[ fname ][ e.comment.strip( ) ] = e.msgstr if e.msgstr != "" else e.msgid
			#pp[ fname ][ e.comment.strip( ) ] = html_unescape( e.msgstr if e.msgstr != "" else e.msgid )

	for ( filename, msgs ) in pp.items( ):
		( root, ext ) = os.path.splitext( filename )
		fname = os.path.join( srcroot, root + "_" + language + ext )
		dname = os.path.dirname( fname )
		if not os.path.exists( dname ):
			os.makedirs( dname )

		master_filename = os.path.join( srcroot, filename )
		if keep_comments and os.path.exists( master_filename ):
			try: # check for file open
				# First, read master file with comments
				fr = open( master_filename, 'rU' )
				mpo = jprops.load_properties( fr, OrderedDict, True )
				fr.close( )

				fw = open( fname, 'w' )
				try: # check for writing contents
					for key in mpo:
						if key[:2] == '#@':
							fw.write( "\n" )
						elif key[:2] == '##':
							jprops.write_comment( fw, mpo[ key ][1:] )
						else:
							if key in msgs:
								jprops.write_property( fw, key, msgs[ key ] )
							else:
								jprops.write_property( fw, key, mpo[ key ] )
				except IOError, ( errno, msg ):
					print 'except: Cannot write to %s' % fname
					print 'errid: [%d] msg: [%s]' % (errno, msg)
				finally:
					fw.close( )
			except IOError, ( errno, msg ):
				print 'except: Cannot open "%s"' % fname
				print 'errid: [%d] msg: [%s]' % (errno, msg)
		else:
			try: # check for file open
				fw = open( fname, 'w' )
				try: # check for writing contents
					for key in msgs:
						jprops.write_property( fw, key, msgs[ key ] )
				except IOError, ( errno, msg ):
					print 'except: Cannot write to %s' % fname
					print 'errid: [%d] msg: [%s]' % (errno, msg)
				finally:
					fw.close( )
			except IOError, ( errno, msg ):
				print 'except: Cannot open "%s"' % fname
				print 'errid: [%d] msg: [%s]' % (errno, msg)


def ReadProperties( module, language, verbose = False ):
	pot = PotFromProperties( os.path.join( srcroot, module ), language, verbose )

	if pot is None:
		if verbose:
			print "  [no resource] '" + module + "' does not have .properties file."
		return
	if language is None:
		path = os.path.join( '.', 'templates' )
		if not os.path.exists( path ):
			os.mkdir( path )
		pot.save( os.path.join( path, module + '.pot' ) )
	else:
		path = os.path.join( '.', language )
		if not os.path.exists( path ):
			os.mkdir( path )
		pot.save( os.path.join( path, module + '.po' ) )


def WriteProperties( module, language, keep_comments, verbose = False ):
	path = os.path.join( os.path.join( '.', language ), module + '.po' )

	if not os.path.exists( path ):
		return

	PoToProperties( path, language, keep_comments, verbose )


def l10n( modules, write, language, keep_comments, verbose ):
	if not modules:
		print( 'You must specify module name.' )
		return( False )
	elif write:
		if language is None:
			print( "You cannot specify '--master' option in write mode." )
			return( False )
		else:
			for module in modules:
				print( "Write .properties for %s..." % module )
				WriteProperties( module, language, keep_comments, verbose )
	else:
		for module in modules:
			print( "Read .properties from %s..." % module )
			ReadProperties( module, language, verbose )

	return( True )


# Main function
if __name__ == "__main__":

	parser    = argparse.ArgumentParser( description='Transfer tools of translation resources between Sakai CLE and Transifex' )
	parser.add_argument( 'module', nargs='*', help='target module name' )

	parser.add_argument( '-r', '--read', action='store_true', help='read translated resource strings from java .properties files, and construct .po file [default]' )
	parser.add_argument( '-w', '--write', action='store_true', help='write resource strings to java .properties files from .po file' )
	parser.add_argument( '-m', '--master', action='store_true', help='construct master.pot from java .properties files' )
	parser.add_argument( '-l', '--language', help='target language (e.g. ja_JP). if not specified, target is set to master template.' )
	parser.add_argument( '-c', '--comment', action='store_true', help='keep comment messages when writing resource strings to java .properties files.' )
	parser.add_argument( '-v', '--verbose', action='store_true', help='output many messages.' )
	parser.add_argument( '--version', action='version', version='%(prog)s 0.1' )

	# Parse command line argments
	args = parser.parse_args( )

	if not l10n( args.module, args.write, args.language, args.comment, args.verbose ):
		parser.print_usage( )
