const http = require("http");

const port = 8000;

const requestListener = function (req, res) {
    console.log('request ', req.url);	
    if (req.url == "/health" || req.url == "/health/") {
    	res.writeHead(200);
        res.end("OK");
    }
};

const server = http.createServer(requestListener);
server.listen(port, () => {
    console.log(`Server is running on ${port} port!`);
});