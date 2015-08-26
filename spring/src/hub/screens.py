__author__ = 'Adam Snyder'

import Tkinter
import server
import buildings
import random
import game_objects
import util
import tkFont

class Screen(Tkinter.Frame):

    def __init__(self, game):
        Tkinter.Frame.__init__(self, game.frame)
        self.game = game
        self.canvas = Tkinter.Canvas(self, height=game.height, width=game.width)
        self.canvas.pack()
        self.id = None

    def main_loop(self):
        pass

class LobbyScreen(Screen):

    def __init__(self, game):
        Screen.__init__(self, game)
        self.id = 'lobby'
        self.canvas.bind("<Button-1>", self.click)
        self.font_big = tkFont.Font(size=36, weight='bold')
        self.font_med = tkFont.Font(size=24, underline=1)
        self.font_sm = tkFont.Font(size=18)

    def main_loop(self):
        self.canvas.delete(Tkinter.ALL)
        self.canvas.create_text(self.game.width/2, 50, text='Game ID: '+server.serverIP,
                                font=self.font_big)
        self.canvas.create_text(self.game.width/2, 100, text='Connected Players',
                                font=self.font_med)
        i = 0
        for player in server.players.values():
            self.canvas.create_text(self.game.width/2, i*25+140, text=player['name'], font=self.font_sm)
            i += 1
        self.canvas.create_rectangle(self.game.width/2-50, i*25+160, self.game.width/2+50, i*25+200,
                                     fill='green', tags='ok_button')
        self.canvas.create_text(self.game.width/2, i*25+180, text='Ready', font=self.font_sm, tags='ok_button2')

    def click(self, event):
        if self.canvas.find_withtag(Tkinter.CURRENT) == self.canvas.find_withtag('ok_button') \
                or self.canvas.find_withtag(Tkinter.CURRENT) == self.canvas.find_withtag('ok_button2'):
            self.canvas.itemconfig(Tkinter.CURRENT, fill="blue")
            self.canvas.update_idletasks()
            self.canvas.after(200)
            self.game.load_screen(GameScreen)

class GameOverScreen(Screen):

    def __init__(self, game):
        Screen.__init__(self, game)
        self.id = 'gameover'
        self.font_big = tkFont.Font(size=36, weight='bold')
        self.font_med = tkFont.Font(size=24)
        self.counter = 0

    def main_loop(self):
        try:
            self.canvas.delete(Tkinter.ALL)
            self.canvas.create_text(self.game.width/2, self.game.height/3, text=self.game.caught_player+' was caught!',
                                    font=self.font_big)
            self.canvas.create_text(self.game.width/2, self.game.height*2/3, text="""How could you fail! I believed in you!!
Okay, I will give you one more chance.
But to increase your chance to escape next time,
You should decide who should be left here.
Hmm...""", font=self.font_med)
        except Exception:
            pass

class GameScreen(Screen):

    def __init__(self, game):
        Screen.__init__(self, game)

        class Board:
            def __init__(self, width=15, height=15, cell_width=30, density=1):
                self.width = width
                self.height = height
                self.cell_width = cell_width
                self.density = density
                self.filled_spaces = 0
                self.board = [[None]*height for _ in range(width)]
                buildings.Prison(self).place(self, (self.width+1)/2-3, (self.height+1)/2-3)
                self.fence_pos = buildings.Fence(self).place(self)
                self.key_pos = game_objects.Key(self).place(self)

                attempts = 0
                max_attempts = 10000
                while True:
                    if self.filled_spaces*1.0/(self.width*self.height) > density or attempts > max_attempts:
                        break
                    options = [buildings.House, buildings.HouseMediumVertical, buildings.Church, buildings.MeetingHouse]
                    choice = random.choice(options)(self)
                    if choice.place(self, random.randint(0, self.width-1), random.randint(0, self.width-1)):
                        attempts = 0
                    else:
                        attempts += 1

            def open_gate(self):
                gate = self.board[self.fence_pos[0]][self.fence_pos[1]]
                gate.blocking = False
                gate.color = 'white'
                self.board[self.key_pos[0]][self.key_pos[1]] = None
                self.key_pos = (-1, -1)

            def reset(self):
                self.__init__(self.width, self.height, self.cell_width, self.density)

        self.id = 'game'
        self.cell_width = int(round(self.game.height/15/50)*50)
        self.board = Board(self.game.width/self.cell_width, self.game.height/self.cell_width, self.cell_width)
        self.key = self.board.board[self.board.key_pos[0]][self.board.key_pos[1]]
        self.object_list = [self.key]
        background_image = Tkinter.PhotoImage(file='img/dirt3.gif')
        ratio = int(round(float(self.board.cell_width)*2/background_image.height()))
        self.background_image = background_image.zoom(ratio, ratio)
        for player in server.players.values():
            if not player['exiled']:
                player['player'] = game_objects.Player(util.random_color(), self.board, player['name'])
        for _ in range(16):
            self.object_list.append(game_objects.Guard(self.board, self.game))

    def main_loop(self):
        while not server.control_queue.empty():
            message = server.control_queue.get()
            print message
            if message['playerId'] in server.players:
                server.players[message['playerId']]['player'].keys[message['button']] = message['status']

        players_in_game = [p for p in server.players.values() if not p['player'].free and not p['exiled']]
        if not players_in_game and server.players:
            self.board.reset()
            for player in server.players.values():
                player['player'].free = False
                player['player'].position = {
                    'x': player['player'].cell['x']*self.board.cell_width+self.board.cell_width/2,
                    'y': player['player'].cell['y']*self.board.cell_width+self.board.cell_width/2
                }

        for player in players_in_game:
            if isinstance(player['player'], game_objects.Player):
                player['player'].tick(self.board)

        for item in self.object_list:
            if isinstance(item, game_objects.GameObject):
                item.tick(self.board)

        if self.game.caught_player:
            self.game.load_screen(GameOverScreen)
            return

        self.canvas.delete(Tkinter.ALL)

        for i in range((self.board.width+1)/2+1):
            for j in range((self.board.height+1)/2+1):
                self.canvas.create_image(i*self.cell_width*2, j*self.cell_width*2, image=self.background_image,
                                         anchor=Tkinter.NW)

        for i in range(self.board.width):
            for j in range(self.board.height):
                val = self.board.board[i][j]
                if val and not isinstance(val, game_objects.Key):
                    if isinstance(val, game_objects.GameObject):
                        val.draw(self.canvas, i*self.board.cell_width, j*self.board.cell_width)

        for item in self.object_list:
            if isinstance(item, game_objects.GameObject):
                item.draw(self.canvas)

        for player in players_in_game:
            if isinstance(player['player'], game_objects.Player):
                player['player'].draw(self.canvas)
