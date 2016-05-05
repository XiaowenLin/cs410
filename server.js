var http = require('http');
var fs = require('fs');
var formidable = require("formidable");
var util = require('util');

var server = http.createServer(function (req, res) {
    //read in the css file for page styling 
    if(req.url.indexOf('.css') != -1){
      fs.readFile('style.css', function (err, data) {
        if (err) console.log(err);
        res.writeHead(200, {'Content-Type': 'text/css'});
        res.write(data);
        res.end();
      });
    }
    
    if (req.method.toLowerCase() == 'get') {
        displayForm(res);
    } else if (req.method.toLowerCase() == 'post') {
        //processAllFieldsOfTheForm(req, res);
        processFormFieldsIndividual(req, res);
    }
});

function displayForm(res) {
    fs.readFile('form.html', function (err, data) {
        res.writeHead(200, {
            'Content-Type': 'text/html',
                'Content-Length': data.length
        });
        res.write(data);
        res.end();
    });
}

function processAllFieldsOfTheForm(req, res) {
    var form = new formidable.IncomingForm();

    form.parse(req, function (err, fields, files) {
        //Store the data from the fields in your data store.
        //The data store could be a file or database or any other store based
        //on your application.
        res.writeHead(200, {
            'content-type': 'text/plain'
        });
        res.write('received the data:\n\n');
        res.end(util.inspect({
            fields: fields,
            files: files
        }));
    });
}

function processFormFieldsIndividual(req, res) {
    //Store the data from the fields in your data store.
    //The data store could be a file or database or any other store based
    //on your application.
    var fields = [];

    var form = new formidable.IncomingForm();
    form.on('field', function (field, value) {
        console.log(field);
        console.log(value);
        fields[field] = value;

    });

    form.on('end', function () {
	
        res.writeHead(200, {
            'content-type': 'text/plain'
        });
        res.write('received the data:\n\n');

	var net = require('net');

	var HOST = '127.0.0.1';
	var PORT = 9000;

	var client = new net.Socket();
	client.connect(PORT, HOST, function() {

	    console.log('CONNECTED TO: ' + HOST + ':' + PORT);
	    // Write a message to the socket as soon as the client is connected, the server will receive it as message from the client 
	    client.write(fields['Find'] + ', ' + fields['Near']);
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

	/*
	var net = require('net');

	var client = new net.Socket();
	client.connect(9000, '127.0.0.1', function() {
		console.log('Connected');
		client.write('Hello, server! Love, Client.');
	});
	
        var spawn = require('child_process').spawn,
        py    = spawn('python', ['compute_input.py']),
        data = [1,2,3,4,5,6,7,8,9],
        dataString = '';

        py.stdout.on('data', function(data){
          dataString += data.toString();
        });
        py.stdout.on('end', function(){
          console.log('Sum of numbers=',dataString);
        });
        py.stdin.write(JSON.stringify(data));
        py.stdin.end();
	*/

	
        res.end(util.inspect({
            fields: fields
        }));
    });
    form.parse(req);
}

server.listen(1185);
console.log("server listening on 1185");
