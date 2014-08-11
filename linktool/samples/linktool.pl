#!/usr/bin/perl -w

#
# Perl CGI which demonstrates Sakai LinkTool interface
#

use strict;
use SOAP::Lite;

my $sakaiSigningUrl  = "/sakai-axis/SakaiSigning.jws?wsdl";
my ($queryString, %parms);

main:
{
   # Get the linktool parameters
   $queryString = $ENV{'QUERY_STRING'};
   foreach ( split /\&/, $queryString )
   {
       my ($key, $value) = split /=/;
       $parms{$key} = $value;
   }
	
   my $internaluser = $parms{'internaluser'};
   my $user = $parms{'user'};
   my $site = $parms{'site'};
   my $server = $parms{'serverurl'};
   my $session = $parms{'session'};
   my $placement = $parms{'placement'};
   my $role = $parms{'role'};
   my $sign = $parms{'sign'};
   my $time = $parms{'time'};
	
	# unescape percent-encoded characters
	$server =~ s/%([0-9A-Fa-f]{2})/chr(hex($1))/eg;
	
	print "Content-Type: text/html\n\n";
	print "<html><body>";
	print "<h2>Linktool Test</h2>";

	print "<table border='1'>";
	print "<tr><th>Parameter</th><th>Value</th></tr>";
	print "<tr><td>internaluser </td><td> $internaluser </td></tr>";
	print "<tr><td>user </td><td> $user </td></tr>";
	print "<tr><td>site </td><td> $site </td></tr>";
	print "<tr><td>server </td><td> $server </td></tr>";
	print "<tr><td>session </td><td> $session </td></tr>";
	print "<tr><td>placement </td><td> $placement </td></tr>";
	print "<tr><td>role </td><td> $role </td></tr>";
	print "<tr><td>sign </td><td> $sign </td></tr>";
	print "<tr><td>time </td><td> $time </td></tr>";
	print "</table>";
	
	print "<h2>Result</h2>";
	
   my $service = SOAP::Lite->service( $server . $sakaiSigningUrl )->on_fault (
       sub {
           my ( $soap, $res ) = @_;
			  print "<p>Error: ";
           print (ref $res ? $res->faultstring : $soap->transport->status, "\n");
			  print "</p>";
       }
   );
   
	# encode query string for soap/xml message
	$queryString =~ s/\&/\&amp;/g;
	
  	my $result =  $service->testsign($queryString); 
	if ( $result eq "true" )
	{
		 print "Success";
	}
	else
	{
		 print "testsign failed";
	}
	
	print "</body></html>";
}
