const express = require('express')
const bodyParser = require('body-parser')
const db = require('./queries')

const server = express()
const port = 8000

server.use(bodyParser.json())
server.use(
    bodyParser.urlencoded({
        extended: true,
    })
)

server.get('/', (request, response) => {
    response.json({
        info: 'Node.js, Express, and Postgres API'
    })
})

server.get('/health', (request, response) => {
    response.json({
        status: 'OK'
    })	
})

server.get('/users', db.getUsers)
server.get('/users/:id', db.getUserById)
server.post('/users', db.createUser)
server.put('/users/:id', db.updateUser)
server.delete('/users/:id', db.deleteUser)

server.listen(port, () => {
    console.log(`Server is running on ${port} port!`)
})

