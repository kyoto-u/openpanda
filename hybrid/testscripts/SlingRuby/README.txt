This is an integration test for hybrid REST endpoints written in Ruby.

It should run on any platform with ruby installed, however it may need some extra components.

If you get any errors you may need to add some extra components. to go this use Ruby Gems

On OS X 10.5 I needed to do the following.

sudo gem update 
sudo gem install json 
sudo gem install curb

If you are running OS X 10.6, the following commands work:
sudo env ARCHFLAGS="-arch x86_64" gem update --system
sudo env ARCHFLAGS="-arch x86_64" gem update
sudo env ARCHFLAGS="-arch x86_64" gem install json
sudo env ARCHFLAGS="-arch x86_64" gem install curb



If you are using Windows XP, the following instruction may help.

How to install curl, ruby, and the rubygem libraries (curb and json) onto a windows xp machine.
===============================================================================================
Please note that more up to date documentation may be available on confluence. 

1) installing curl
==================
Go to http://curl.haxx.se/download.html , scroll down the list of packages looking for the "Win32 - Generic" section. Download the following package:

Package       Version Type    SSL Provider
Win32 2000/XP 7.21.1  libcurl ssl Gunter Knauf

Install the program by unzipping the file into a directory that has NO spaces in the directory path (ie do not install in C:\Program Files). Best to unzip the file into a directory something like C:\curl

Then add something like the following to your Path: C:\curl\curl-7.21.1-devel-mingw32\bin

Note might be a good idea to add "C:\curl\curl-7.21.1-devel-mingw32\bin" to the front of you Path statement, as it contains a recent version of libeay32.dll, and some of your other applications may contain older versions. 
Otherwise you might see the following error when attempting to run the integration tests "The procedure entry point EVP_CIPHER_CTX_get_app_data could not be located in the dynamic link library libeay32.dll."


Now test curl has been installed correctly.
Open up a new dos window, and enter the command "curl --version", and verify that the correct version of curl is returned.


2) install Ruby
===============
Go to http://rubyinstaller.org/downloads/ and under the heading "RubyInstallers" download the package "Ruby 1.9.1-p430"

Run the exe file you have just downloaded, and accept the default install directory (eg C:\Ruby191)
Then add the following to your Path: C:\Ruby191\bin

Now test ruby and gem have been installed correctly
Open up a new dos window, and enter the command "ruby --version", and verify that the correct version of ruby is returned.
Open up a new dos window, and enter the command "gem --version", and verify that gem can return a version number.


3) install Ruby Development Kit (required so that the gem "curb" package can use the curl package installed in step 1)
===============================
got to http://rubyinstaller.org/downloads/ and under the heading "Development Kit" download the package "DevKit-3.4.5-20100819-1535-sfx.exe"
Run the exe file and install in a directory with no space in the path name. Perhaps install in a directory something like C:\rubyDevKit345

Go to this page http://github.com/oneclick/rubyinstaller/wiki/Development-Kit and use the instructions to complete the installation of the devkit. To install the ruby devkit I only used instructions 3 and 4, that is:
cd C:\rubyDevKit345
ruby dk.rb init
ruby dk.rb review
ruby dk.rb install

4) Install gem library json
===========================
Open a new dos window and run the following command
gem install json --platform=ruby

The output will look something like:
----------------------------------------------------------------
|Temporarily enhancing PATH to include DevKit...
|Building native extensions.  This could take a while...
|Successfully installed json-1.4.6
|1 gem installed
|Installing ri documentation for json-1.4.6...
|Updating class cache with 0 classes...
|Installing RDoc documentation for json-1.4.6...
----------------------------------------------------------------

Note this command took quite some time to run on my laptop.



Check the json library has been installed, by running the following command "gem list".

And verify json is one of the libraries installed on your machine.


5) Install gem library curb 
===========================
Go to this page http://github.com/oneclick/rubyinstaller/wiki/Development-Kit, and under the heading "Example Native RubyGem Installations using the DevKit" look at the information on how to install curb. Using my suggested directory structure above, the following command will install the curb library:

gem install curb --platform=ruby -- --with-curl-lib="C:/curl/curl-7.21.1-devel-mingw32/bin" --with-curl-include="C:/curl/curl-7.21.1-devel-mingw32/include"

The output will look something like:
--------------------------------------------------------------
|Temporarily enhancing PATH to include DevKit...
|Building native extensions.  This could take a while...
|Successfully installed curb-0.7.8
|1 gem installed
|Installing ri documentation for curb-0.7.8...
|Updating class cache with 59 classes...
|Installing RDoc documentation for curb-0.7.8...
---------------------------------------------------------------

Note this command took quite some time to run on my laptop.

Check the curb library has been installed, by running the following command "gem list"

And verify curb is one of the libraries installed on your machine.

should see something like:
--------------------------------------
|D:\Data\csu\nakamura\ruby>gem list
|
|*** LOCAL GEMS ***
|
|curb (0.7.8)
|json (1.4.6)
|
|D:\Data\csu\nakamura\ruby>
--------------------------------------

6) Verify the above installation of every thing has worked
==========================================================

Use ruby program tests\hybrid-test.rb to run the integration test. At the dos command prompt enter the following command "ruby tests\hybrid-test.rb" as shown below, and you should see confirmation.

---------------------------------------
Loaded suite tests/hybrid-test
Started
....
Finished in 0.462906 seconds.

4 tests, 190 assertions, 0 failures, 0 errors
---------------------------------------

