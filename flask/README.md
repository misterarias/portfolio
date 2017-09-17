flask-bootstrap
===============

Create the simplest possible [Flask](http://flask.pocoo.org/) app, so I can start fast Mock Servers from scratch.

## Mock server?

A local server that mimics the path of your app, but returns a static/custom template, so you can do integration tests of APIs and sercies without external connectivity.

## How to use

Assuming you have Docker installed, follow the next steps to use this.

### Build

```
app=flask-app

docker build -t "${app}" image/
```

### Run

This will launch the sample app in port 8080

```
app=flask-app

docker --rm -v $(pwd)/server:/opt/flask -p 8080:80 -ti ${app}
```

### Verify?

```
# Mandatory Hello world
curl localhost:8080/

# Should return a 200 OK with a JSON response
curl  localhost:8080/api/v1/register

# Should return a 404 Error with a custom message
curl  localhost:8080/api/v1/i_dont_exist
```

### Stop

```
app=flask-app

docker stop $(docker ps -q  --filter=ancestor=${app})
```
