var express = require('express');
var app = express();
var bodyParser = require('body-parser');

// Create application/x-www-form-urlencoded parser
var urlencodedParser = bodyParser.urlencoded({ extended: false })

app.use(express.static(__dirname + '/public'));

app.get('/form.html', function (req, res) {
   res.sendFile( __dirname + "/" + "form.html" );
})

app.post('/process_post', urlencodedParser, function (req, res) {

   // Prepare output in JSON format
   response = {
       Find:req.body.Find,
       Near:req.body.Near
   };

	var Find_string = "" + req.body.Find;
	var Near_string = "" + req.body.Near;

   console.log(response);
   res.end(JSON.stringify(response));
	

	var net = require('net');

	var HOST = '127.0.0.1';
	var PORT = 9000;

	var client = new net.Socket();
	client.connect(PORT, HOST, function() {

	    console.log('CONNECTED TO: ' + HOST + ':' + PORT);
	    // Write a message to the socket as soon as the client is connected, the server will receive it as message from the client 
	    client.write(Find_string + " " + Near_string);
	    client.end();

	});

	// Add a 'data' event handler for the client socket
	// data is what the server sent to this socket
	client.on('data', function(data) {
	    
	    console.log('DATA: ' + data);
	    // Close the client socket completely
	    client.destroy();
	    
	});

	// Add a 'close' event handler for the client socket
	client.on('close', function() {
	    console.log('Connection closed');
	});
	
})

var server = app.listen(8081, function () {

  var host = server.address().address
  var port = server.address().port

  console.log("Example app listening at http://%s:%s", host, port)

})
