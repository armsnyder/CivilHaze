__author__ = 'Adam Snyder'

import BaseHTTPServer
import SocketServer
import cgi
import socket
import threading
import json
import Queue
import urlparse
import time


httpd = None
control_queue = Queue.Queue()
players = {}
game = None
serverIP = ''


class MyHandler(BaseHTTPServer.BaseHTTPRequestHandler):

    def __init__(self, request, client_address, server):
        BaseHTTPServer.BaseHTTPRequestHandler.__init__(self, request, client_address, server)

    def do_POST(self):
        parsed_path = urlparse.urlparse(self.path)
        ctype, pdict = cgi.parse_header(self.headers['content-type'])
        if ctype == 'application/json':
            content_len = int(self.headers.getheader('content-length', 0))
            post_body = json.loads(self.rfile.read(content_len))
            self.update_connected_players(post_body)

            if parsed_path.path == '/button':
                post_body['playerId'] = self.address_string()
                control_queue.put(post_body)
                self.send_response(200)
                self.send_header('Access-Control-Allow-Origin', '*')
                self.end_headers()
            elif parsed_path.path == '/ask_ready':

                self.send_response(200)
                self.send_header('Access-Control-Allow-Origin', '*')
                self.send_header('Content-type', 'application/json')
                self.end_headers()
                if game.screen.id == 'lobby':
                    self.wfile.write('{"ready": false}')
                else:
                    pass
                    self.wfile.write('{"ready": true}')

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

    def update_connected_players(self, post_body):
        player_id = self.address_string()
        if player_id in players:
            players[player_id]['last_ping'] = time.time()
        elif game.screen.id == 'lobby':
            if 'playerName' in post_body:
                players[player_id] = {
                    'last_ping': time.time(),
                    'name': post_body['playerName']
                }
            else:
                self.send_error(404, 'Bad Request')
                self.send_header('Access-Control-Allow-Origin', '*')
                self.end_headers()
        else:
            self.send_error(404, 'Bad Request')
            self.send_header('Access-Control-Allow-Origin', '*')
            self.end_headers()


class MyServer(BaseHTTPServer.HTTPServer):
    def __init__(self, server, handler):
        BaseHTTPServer.HTTPServer.__init__(self, server, handler)


class ThreadedHTTPServer(SocketServer.ThreadingMixIn, MyServer):
    """Handle requests in a separate thread."""


def start_server(game_arg):
    global httpd, game, serverIP
    game = game_arg
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
    serverIP = sa[0]
    threading.Thread(target=httpd.serve_forever).start()


def stop_server():
    global httpd
    if not httpd:
        print 'Server is not running'
        return
    httpd.shutdown()


def get_address():
    return socket.gethostbyname(socket.gethostname())