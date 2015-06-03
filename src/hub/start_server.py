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
import screens
import os


httpd = None
control_queue = Queue.Queue()
players = {}
serverIP = ''
move_on = False
screen = None


class MyHandler(BaseHTTPServer.BaseHTTPRequestHandler):

    def __init__(self, request, client_address, server):
        BaseHTTPServer.BaseHTTPRequestHandler.__init__(self, request, client_address, server)
    #
    # def do_GET(self):
    #     parsed_path = urlparse.urlparse(self.path)
    #     message_parts = []
    #     for name, value in sorted(self.headers.items()):
    #         message_parts.append('%s=%s' % (name, value.rstrip()))
    #     message_parts.append('')
    #     message = '\r\n'.join(message_parts)
    #     self.send_response(200)
    #     self.end_headers()
    #     self.wfile.write(message)
    #     return

    def do_GET(self):
        global screen
        parsed_path = urlparse.urlparse(self.path)
        try:
            post_body = json.loads('{'+parsed_path.query+'}')
        except Exception:
            post_body = parsed_path.query

        if parsed_path.path == '/button':
            self.update_connected_players(post_body)
            post_body['playerId'] = self.address_string()
            control_queue.put(post_body)
            self.send_response(200)
            self.send_header('Access-Control-Allow-Origin', '*')
            self.end_headers()

        elif parsed_path.path == '/ask_ready':
            self.update_connected_players(post_body)
            self.send_response(200)
            self.send_header('Access-Control-Allow-Origin', '*')
            self.send_header('Content-type', 'application/json')
            self.end_headers()
            self.wfile.write(json.dumps({'ready': screen == 'game'}))

        elif parsed_path.path == '/gamePing':
            self.update_connected_players(post_body)
            self.send_response(200)
            self.send_header('Access-Control-Allow-Origin', '*')
            self.send_header('Content-type', 'application/json')
            self.end_headers()
            response = {
                'ready': True,
                'players': [{
                    'name': players[player]['player'].player_name,
                    'id': player
                } for player in players.keys() if player != self.address_string()]
            }
            print response
            self.wfile.write(json.dumps(response))

        elif parsed_path.path == '/vote':
            self.update_connected_players(post_body)
            self.send_response(200)
            self.send_header('Access-Control-Allow-Origin', '*')
            self.send_header('Content-type', 'application/json')
            self.end_headers()
            players[self.address_string()]['vote'] = post_body['vote']

        elif parsed_path.path == '/tally':
            self.send_response(200)
            self.send_header('Access-Control-Allow-Origin', '*')
            self.send_header('Content-type', 'application/json')
            self.end_headers()
            voted_players = []
            ready = True
            for player in players.values():
                if player['vote'] is None:
                    ready = False
                    print player['vote'], 'not ready'
                    break
                voted_players.extend(player['vote'])
            if ready:
                print 'ready!'
                voted_players = set(voted_players)
                for player in voted_players:
                    players[player]['exiled'] = True
                    print 'loading screen'
                self.wfile.write(json.dumps(True))
            else:
                self.wfile.write(json.dumps(False))

        elif parsed_path.path == '/set_screen':
            self.send_response(200)
            self.send_header('Access-Control-Allow-Origin', '*')
            self.send_header('Content-type', 'application/json')
            self.end_headers()
            screen = post_body
            print screen
        self.connection.shutdown(1)


    def do_OPTIONS(self):
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
        elif screen == 'lobby':
            if 'playerName' in post_body:
                players[player_id] = {
                    'last_ping': time.time(),
                    'name': post_body['playerName'],
                    'vote': None,
                    'player': None,
                    'exiled': False
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


def start_server():
    global httpd, serverIP
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


if __name__ == '__main__':
    start_server()