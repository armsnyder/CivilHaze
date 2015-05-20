__author__ = 'Adam Snyder'

import BaseHTTPServer
import SocketServer
import cgi
import socket
import threading
import json
import Queue


httpd = None
control_queue = Queue.Queue()


class MyHandler(BaseHTTPServer.BaseHTTPRequestHandler):

    def __init__(self, request, client_address, server):
        BaseHTTPServer.BaseHTTPRequestHandler.__init__(self, request, client_address, server)

    def do_POST(self):
        ctype, pdict = cgi.parse_header(self.headers['content-type'])
        if ctype == 'application/json':
            content_len = int(self.headers.getheader('content-length', 0))
            post_body = self.rfile.read(content_len)
            control_queue.put(json.loads(post_body))
            self.send_response(200, 'done')
            self.send_header('Access-Control-Allow-Origin', '*')
            self.end_headers()
        else:
            print ctype
            print pdict
        self.connection.shutdown(1)

    def do_OPTIONS(self):
        print 'options'
        self.send_response(200, 'ok')
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, PUT, POST, OPTIONS')
        self.send_header('Access-Control-Allow-Headers', 'Content-Type')
        self.end_headers()
        self.connection.shutdown(1)


class MyServer(BaseHTTPServer.HTTPServer):
    def __init__(self, server, handler):
        BaseHTTPServer.HTTPServer.__init__(self, server, handler)


class ThreadedHTTPServer(SocketServer.ThreadingMixIn, MyServer):
    """Handle requests in a separate thread."""


def start_server():
    global httpd
    if httpd:
        print 'Server is already running'
        return

    protocol = "HTTP/1.1"
    host = get_address()
    port = 8001

    MyHandler.protocol_version = protocol
    httpd = ThreadedHTTPServer((host, port), MyHandler)
    sa = httpd.socket.getsockname()
    print "Serving HTTP on", sa[0], "port", sa[1], "..."
    threading.Thread(target=httpd.serve_forever).start()


def stop_server():
    global httpd
    if not httpd:
        print 'Server is not running'
        return
    httpd.shutdown()


def get_address():
    return socket.gethostbyname(socket.gethostname())