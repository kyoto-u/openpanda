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

import subprocess
import argparse
import glob

import l10n

# TMX Project Name on Transifex
#project_name = 'sakai-29x-all'
#project_name = 'tmx-test-project'
#project_name = 'sakai-293'
#project_name = 'sakai-trunk'
project_name = 'sakai10x'

# Extract path information
cwdir = os.path.dirname( os.path.abspath( __file__ ) )
cwdirname = os.path.basename( cwdir if not cwdir.endswith( '/' ) else cwdir[:-1] )

# Change working directory to the script directory (l10n)
os.chdir( cwdir )

# Sakai source path
srcroot = '..'

def __resource__( module ):
	return( project_name + '.' + module )

# Enumerate modules
def EnumModules( root ):
	list = [];
	for dirname in os.listdir( root ):
		# Ignore some special folders (including kernel module)
		#if dirname != cwdirname and not dirname.startswith( '.' ) and os.path.isdir( os.path.join( root, dirname ) ):
		if dirname != cwdirname and not dirname.startswith( '.' ) and os.path.isdir( os.path.join( root, dirname ) ) and dirname != 'kernel':
			list.append( dirname )

	return( list )

# Enumerate modules from .po files
def EnumModulesFromPo( language ):
	list = [];
	for filepath in glob.glob( os.path.join( os.path.join( '.', language ), '*.po' ) ):
		( ppp, filename ) = os.path.split( filepath );
		( fname, ext ) = os.path.splitext( filename );
		list.append( fname )

	return( list )

# Enumerate .properties files
def EnumLanguages( ):
	list = [];
	for dirname in os.listdir( cwdir ):
		if dirname != 'templates' and not dirname.startswith( '.' ) and os.path.isdir( os.path.join( cwdir, dirname ) ):
			list.append( dirname )

	return( list )


# Initialize Transifex settings
def InitializeTransifex( args ):
	subprocess.call( ['tx', 'init' ] )

	modules = EnumModules( srcroot )
	l10n.l10n( modules, False, None, False, False )

	for module in modules:
		subprocess.call( ['tx', 'set', '--auto-local', '-t', 'PO', '-r', __resource__( module ), '<lang>/' + module + '.po', '--source-lang', 'en', '--source-file', 'templates/' + module + '.pot', '--execute' ] )


# Upload resources to Transifex
def UploadToTransifex( args ):
	if not args.modules:
		modules = None
	else:
		modules = []
		for module in args.modules:
			if os.path.exists( os.path.join( srcroot, module ) ):
				modules.append( module )
			else:
				print( "Module '%s' does not exist." % module )

	if args.master:
		param = [ 'tx', 'push', '-sf', '--no-interactive']
		if modules is not None:
			param.append( '-r' )
			for module in modules:
				if args.update:
					l10n.l10n( [ module ], False, None, False, args.verbose )
				subprocess.call( param + [ __resource__( module ) ] )
		else:
			if args.update:
				modules = EnumModules( srcroot )
				l10n.l10n( modules, False, None, False, args.verbose )
			subprocess.call( param )

	elif args.language is not None:
		param = [ 'tx', 'push', '--skip', '-tf', '--no-interactive', '-l', args.language]
		if modules is not None:
			param.append( '-r' )
			for module in modules:
				if args.update:
					l10n.l10n( [ module ], False, args.language, False, args.verbose )
				subprocess.call( param + [ __resource__( module ) ] )
		else:
			if args.update:
				modules = EnumModules( srcroot )
				l10n.l10n( modules, False, args.language, False, args.verbose )
			subprocess.call( param )

	else:
		print( "'--master' (='-m') or '--language' (='-l') must be specified." )


# Download resources from Transifex
def DownloadFromTransifex( args ):
	if not args.modules:
		modules = None
	else:
		modules = []
		for module in args.modules:
			if os.path.exists( os.path.join( srcroot, module ) ):
				modules.append( module )
			else:
				print( "Module '%s' does not exist." % module )

	if args.language is not None:
		if args.reviewed:
			param = [ 'tx', 'pull', '--mode', 'reviewed', '--skip', '-f', '-l', args.language]
		else:
			param = [ 'tx', 'pull', '--skip', '-f', '-l', args.language]

		if modules is not None:
			param.append( '-r' )
			for module in modules:
				subprocess.call( param + [ __resource__( module ) ] )
				if args.update:
					l10n.l10n( [ module ], True, args.language, args.comment, args.verbose )
		else:
			subprocess.call( param )
			if args.update:
				modules = EnumModulesFromPo( args.language )
				l10n.l10n( modules, True, args.language, args.comment, args.verbose )

	else:
		if args.reviewed:
			subprocess.call( [ 'tx', 'pull', '--mode', 'reviewed', '--skip', '-f', '-a' ] )
		else:
			subprocess.call( [ 'tx', 'pull', '--skip', '-f', '-a' ] )

		if args.update:
			modules = EnumModules( srcroot )
			for language in EnumLanguages( ):
				l10n.l10n( modules, True, language, args.comment, args.verbose )


# Download resources from Transifex
def UpdateFromLocal( args ):
	if not args.modules:
		modules = None
	else:
		modules = []
		for module in args.modules:
			if os.path.exists( os.path.join( srcroot, module ) ):
				modules.append( module )
			else:
				print( "Module '%s' does not exist." % module )

	if args.po2java:
		if args.language is not None:
			if modules is not None:
				for module in modules:
					l10n.l10n( [ module ], True, args.language, args.comment, args.verbose )
			else:
				modules = EnumModulesFromPo( args.language )
				l10n.l10n( modules, True, args.language, args.comment, args.verbose )

		else:
			modules = EnumModules( srcroot )
			for language in EnumLanguages( ):
				l10n.l10n( modules, True, language, args.comment, args.verbose )

	elif args.java2po:
		if args.language is None:
			if modules is not None:
				for module in modules:
					l10n.l10n( [ module ], False, None, False, args.verbose )
			else:
				modules = EnumModules( srcroot )
				l10n.l10n( modules, False, None, False, args.verbose )
		else:
			if modules is not None:
				for module in modules:
					l10n.l10n( [ module ], False, args.language, False, args.verbose )
			else:
				modules = EnumModules( srcroot )
				l10n.l10n( modules, False, args.language, False, args.verbose )
	else:
		print( 'You must specify either of --po2java or --java2po')

# Main function
if __name__ == "__main__":

	parser   = argparse.ArgumentParser( description='Tool for TMX Project to transfer translation resources between Sakai CLE and Transifex' )
	sparsers = parser.add_subparsers( help='transfer mode ...' )

	parser1  = sparsers.add_parser( 'init', help = 'initialize Transifex settings and create .tx directory.' )
	parser1.set_defaults( func = InitializeTransifex )

	parser2  = sparsers.add_parser( 'upload', help = 'upload local resources to Transifex' )
	parser2.add_argument( '-u', '--update', action='store_true', help='update .po/.pot file from .properties before upload is finished' )
	parser2.add_argument( '-m', '--master', action='store_true', help='upload .pot (master) file to Transifex' )
	parser2.add_argument( '-l', '--language', help='upload .po file of each language (e.g. ja_JP) to Transifex' )
	parser2.add_argument( '-v', '--verbose', action='store_true', help='output many messages.' )
	parser2.add_argument( 'modules', nargs='*', help='specify which .po/.pot file you want to upload to Transifex (empty means all modules)' )
	parser2.set_defaults( func = UploadToTransifex )

	parser3  = sparsers.add_parser( 'download', help = 'download resources from Transifex to local' )
	parser3.add_argument( '-u', '--update', action='store_true', help='update .properties file from .po/.pot files after download is finished' )
	parser3.add_argument( '-l', '--language', help='download .po file of each language (e.g. ja_JP) from Transifex (defaults to all)' )
	parser3.add_argument( '-r', '--reviewed', action='store_true', help='download only reviewed translations.' )
	parser3.add_argument( '-c', '--comment', action='store_true', help='keep comment messages when writing resource strings to java .properties files.' )
	parser3.add_argument( '-v', '--verbose', action='store_true', help='output many messages.' )
	parser3.add_argument( 'modules', nargs='*', help='specify which .po/.pot file you want to download from Transifex (empty means all modules)' )
	parser3.set_defaults( func = DownloadFromTransifex )

	parser4  = sparsers.add_parser( 'update', help = 'update .properties files using local .po files' )
	parser4.add_argument( '-j', '--po2java', action='store_true', help='update .properties file from .po/.pot files' )
	parser4.add_argument( '-p', '--java2po', action='store_true', help='update .po/.pot file from .properties files' )
	parser4.add_argument( '-l', '--language', help='select language (e.g. ja_JP) for update (defaults is master)' )
	parser4.add_argument( '-c', '--comment', action='store_true', help='keep comment messages when writing resource strings to java .properties files.' )
	parser4.add_argument( '-v', '--verbose', action='store_true', help='output many messages.' )
	parser4.add_argument( 'modules', nargs='*', help='specify which .po/.pot file you want to download from Transifex (empty means all modules)' )
	parser4.set_defaults( func = UpdateFromLocal )


	# Parse command line argments
	args = parser.parse_args( )
	args.func( args )

