<html>
 <head>
  <title>Multi-Document Summarizer</title>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <link rel="stylesheet" type="text/css" href="jscss.css" />
 </head>
 <body>
 <?php
 //mb_internal_encoding("UTF-8");
 //mb_http_output("ISO-8859-1");
ini_set('max_execution_time', 300);
/****
 * Simple PHP application for using the Bing Search API
*/

$acctKey  = 'cRdfvkKK1oCOBV5d/Ym4kJaaMb1oPmUZNJtKIYUnKz0=';
$rootUri = 'https://api.datamarket.azure.com/Bing/Search';

// Read the contents of the .html file into a string.
$contents = file_get_contents('bing_basic.html');

if ($_POST['query'])
{
  // Here is where you'll process the query. 
  // The rest of the code samples in this tutorial are inside this conditional block.
  // Encode the query and the single quotes that must surround it.
	$query = urlencode("'{$_POST['query']}'");
	  
	// Get the selected service operation (Web or Image).
	$serviceOp = $_POST['service_op'];

	// Construct the full URI for the query.
	$requestUri = "$rootUri/$serviceOp?\$format=json&Query=$query";
		// Encode the credentials and create the stream context.
	$auth = base64_encode("$acctKey:$acctKey");
	$data = array(
	  'http' => array(
		'request_fulluri' => true,
		// ignore_errors can help debug – remove for production. This option added in PHP 5.2.10
		'ignore_errors' => true,
		'proxy' => '172.31.1.4:8080',
		'header'  => "Authorization: Basic $auth")
	  );
	  //var_dump($data);
	$context = stream_context_create($data);
	// Get the response from Bing.
	$response = file_get_contents($requestUri, 0, $context); #reads entire file in a string
	// Decode the response.
	$jsonObj = json_decode($response);

	$resultStr = '';

	// Parse each result according to its metadata type.
	foreach($jsonObj->d->results as $value)
	{
	  switch ($value->__metadata->type)
	  {
		case 'WebResult':
		/*
		  $resultStr .= 
			"<a href=\"{$value->Url}\">{$value->Title}</a><br>";#<p>{$value->Description}</p>"; this line was added by me.
			*/
			$resultStr .= "$value->Url"."\n";
		  break;
		case 'NewsResult':
		  $resultStr .= "$value->Url"."\n";
		  break;
	  }
	}

	// Substitute the results placeholder. Ready to go.
	#$contents = str_replace('{RESULTS}', $resultStr, $contents);
	$urlfile = fopen("C:\wamp\www\mdsnew\urlFile.txt", "w") or die("Unable to open file!");
	fwrite($urlfile, $resultStr);
	#$txt = "Jane Doe\n";
	#fwrite($myfile, $txt);
	fclose($urlfile);
	
	// Java file run through terminal
	
	exec ("javac -cp \"boilerlib/boilerpipe-1.2.0.jar;boilerlib/nekohtml-1.9.13.jar;boilerlib/xerces-2.9.1.jar;\" multiDocumentSummarization.java -Xlint");
	//echo "pehli cheli :P<br />";
	exec("java -cp \"boilerlib/boilerpipe-1.2.0.jar;boilerlib/nekohtml-1.9.13.jar;boilerlib/xerces-2.9.1.jar;\" multiDocumentSummarization");
	//echo "<br />dusri cheli :P<br/>";
	
	
	//Writing summary to file
	$myfile = fopen("C:\wamp\www\mdsnew\output.txt", "r") or die("Unable to open file!");}?>
	
	
	
	<div id = "content1">
	<div class = "bar">
	<?php echo fread($myfile,filesize("output.txt"));fclose($myfile);?>
	</div>
	</div>
	
</body>
</html>
