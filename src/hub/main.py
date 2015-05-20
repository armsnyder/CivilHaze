__author__ = 'Adam Snyder'

import server
import buildings
import random
import copy
import Tkinter
import tkFont
import util
import sys


def main():
    Game()


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
        self.counter = 0

    def main_loop(self):
        self.canvas.delete(Tkinter.ALL)
        self.canvas.create_text(self.game.width/2, self.game.height/2, text=self.game.caught_player+' was caught',
                                font=self.font_big)
        self.counter += 1
        if self.counter > 100:
            self.game.caught_player = None
            self.game.load_screen(GameScreen)


class GameScreen(Screen):

    def __init__(self, game):
        Screen.__init__(self, game)
        self.cell_width = self.game.height/24
        self.board = Board(self.game.width/self.cell_width, self.game.height/self.cell_width, self.cell_width)
        self.key = self.board.board[self.board.key_pos[0]][self.board.key_pos[1]]
        self.guards = []
        for player in server.players.values():
            player['player'] = Player(util.random_color(), self.board)
        for _ in range(16):
            self.guards.append(Guard(self.board, self.game))

    def main_loop(self):
        while not server.control_queue.empty():
            message = server.control_queue.get()
            print message
            if message['playerId'] in server.players:
                server.players[message['playerId']]['player'].keys[message['button']] = message['status']

        self.canvas.delete(Tkinter.ALL)

        for i in range(self.board.width):
            for j in range(self.board.height):
                val = self.board.board[i][j]
                if val and not isinstance(val, Key):
                    if isinstance(val, tuple):
                        real_cell = self.board.board[val[0]][val[1]]
                        if isinstance(real_cell, buildings.Fence):
                            _color = '#000'
                        else:
                            _color = real_cell.color
                    else:
                        _color = val.color
                    self.canvas.create_rectangle(i*self.cell_width, j*self.cell_width,
                                                 (i+1)*self.cell_width, (j+1)*self.cell_width, fill=_color)
        self.canvas.create_image(self.board.key_pos[0]*self.cell_width, self.board.key_pos[1]*self.cell_width,
                                 image=self.key.graphic)
        players_in_game = [p for p in server.players.values() if not p['player'].free]
        if not players_in_game and server.players:
            self.board.reset()
            for player in server.players.values():
                player['player'].free = False
                player['player'].position = {
                    'x': player['player'].cell['x']*self.board.cell_width+self.board.cell_width/2,
                    'y': player['player'].cell['y']*self.board.cell_width+self.board.cell_width/2
                }
        for player in players_in_game:
            if isinstance(player['player'], Player):
                player['player'].update_position()
                self.canvas.create_oval(player['player'].position['x']-player['player'].width/2, player['player'].position['y']-player['player'].width/2,
                                        player['player'].position['x']+player['player'].width/2, player['player'].position['y']+player['player'].width/2,
                                        fill=player['player'].color)
                self.canvas.create_text(player['player'].position['x'], player['player'].position['y']-player['player'].height, text=player['name'])
                # pygame.draw.circle(self.screen, player.color, (player.position['x'], player.position['y']),
                #                    player.width/2, 0)
                # self.screen.blit(player.graphic, (player.position['x'], player.position['y']))
            else:
                print 'ERROR: ', type(player)
        for guard in self.guards:
            guard.update_position()
            if self.game.caught_player:
                self.game.load_screen(GameOverScreen)
                break
            self.canvas.create_oval(guard.position['x']-guard.width/2, guard.position['y']-guard.width/2,
                                    guard.position['x']+guard.width/2, guard.position['y']+guard.width/2,
                                    fill=guard.color, outline='red')


class Game:

    def __init__(self, width=2000, height=1000):
        server.start_server(self)
        self.root = Tkinter.Tk()
        self.width = self.root.winfo_screenwidth()
        self.height = self.root.winfo_screenheight()-20
        self.root.overrideredirect(1)
        self.root.geometry("%dx%d+0+20" % (self.width, self.height))
        self.root.focus_set()
        self.caught_player = None

        self.frame = Tkinter.Frame()
        self.frame.pack()
        self.screen = Screen(self)
        self.fps = 30
        self.load_screen(LobbyScreen)
        self.main_loop()
        self.root.mainloop()
        server.stop_server()

    def load_screen(self, screen):
        self.screen.destroy()
        self.screen = screen(self)
        self.screen.pack()

    def main_loop(self):
        self.screen.main_loop()
        self.root.after(1000/self.fps, self.main_loop)


class Guard:
    def __init__(self, board=None, game=None, _color='black'):
        self.board = board
        self.game = game
        self.graphic = None
        self.height = board.cell_width*2/3
        self.width = board.cell_width*2/3
        self.cell = {
            'x': 0,
            'y': 0
        }
        self.init_position()
        self.position = {
            'x': self.cell['x']*self.board.cell_width+self.board.cell_width/2,
            'y': self.cell['y']*self.board.cell_width+self.board.cell_width/2
        }
        self.max_speed = 5
        self.color = _color
        self.direction = random.choice(['up', 'down', 'left', 'right'])
        self.occupied_cells = []
        self.last_choice_at = ()

    def init_position(self):
        count = 0
        max_count = 100
        zone = 3
        while count < max_count:
            cell = {
                'x': random.randint(1, self.board.width-2),
                'y': random.randint(1, self.board.height-2)
            }
            if self.board.board[cell['x']][cell['y']]:
                continue
            ok = True
            for i in range(cell['x']-zone, cell['x']+zone):
                for j in range(cell['y']-zone, cell['y']+zone):
                    try:
                        c_cell = self.board.board[i][j]
                    except IndexError:
                        continue
                    if isinstance(c_cell, tuple):
                        c_cell = self.board.board[c_cell[0]][c_cell[1]]
                    if isinstance(c_cell, buildings.Prison):
                        ok = False
                        break
                if not ok:
                    break
            if ok:
                self.cell = cell
                break
            count += 1

    def update_position(self):
        corners = [
            (self.position['x']-self.width/2+1, self.position['y']-self.height/2+1),
            (self.position['x']+self.width/2-1, self.position['y']-self.height/2+1),
            (self.position['x']+self.width/2-1, self.position['y']+self.height/2-1),
            (self.position['x']-self.width/2+1, self.position['y']+self.height/2-1)]
        self.occupied_cells = set([(x/self.board.cell_width, y/self.board.cell_width) for x, y in corners])
        for player in server.players.values():
            for cell in player['player'].occupied_cells:
                for t_cell in self.occupied_cells:
                    if cell == t_cell:
                        self.game.caught_player = player['name']
        # if len(self.occupied_cells) == 1:
        self.change_direction()
        if self.direction == 'up':
            self.position['y'] -= self.max_speed
        elif self.direction == 'down':
            self.position['y'] += self.max_speed
        elif self.direction == 'left':
            self.position['x'] -= self.max_speed
        elif self.direction == 'right':
            self.position['x'] += self.max_speed

    def change_direction(self):
        for cell in self.occupied_cells:
            change = False
            try:
                if self.direction == 'up':
                    if self.board.board[cell[0]][cell[1]+1]:
                        change = True
                elif self.direction == 'down':
                    if self.board.board[cell[0]][cell[1]-1]:
                        change = True
                elif self.direction == 'left':
                    if self.board.board[cell[0]-1][cell[1]]:
                        change = True
                elif self.direction == 'right':
                    if self.board.board[cell[0]+1][cell[1]]:
                        change = True
            except IndexError:
                change = True
            if change:
                choices = ['up', 'down', 'left', 'right']
                choices.remove(self.direction)
                if self.direction == 'up':
                    choices.remove('down')
                elif self.direction == 'down':
                    choices.remove('up')
                elif self.direction == 'left':
                    choices.remove('right')
                elif self.direction == 'right':
                    choices.remove('left')
                self.direction = random.choice(choices)
        self.last_choice_at = self.occupied_cells



class Player:
    def __init__(self, _color='orange', board=None):
        self.board = board
        self.graphic = None
        self.keys = {
            'arrowUp': False,
            'arrowLeft': False,
            'arrowDown': False,
            'arrowRight': False
        }
        self.height = board.cell_width*2/3
        self.width = board.cell_width*2/3
        self.free = False
        self.occupied_cells = []
        self.cell = {
            'x': 0,
            'y': 0
        }
        for i in range(board.width):
            for j in range(board.height):
                if isinstance(board.board[i][j], buildings.Prison):
                    self.cell = {
                        'x': i+buildings.Prison.width/2,
                        'y': j+buildings.Prison.height
                    }

        self.position = {
            'x': self.cell['x']*self.board.cell_width+self.board.cell_width/2,
            'y': self.cell['y']*self.board.cell_width+self.board.cell_width/2
        }
        self.max_speed = 5
        self.color = _color

    def update_position(self):
        new_position = copy.copy(self.position)

        if self.keys['arrowUp']:
            new_position['y'] -= self.max_speed
        if self.keys['arrowDown']:
            new_position['y'] += self.max_speed

        if self.check_collision(new_position):
            self.position = new_position

        new_position = copy.copy(self.position)

        if self.keys['arrowLeft']:
            new_position['x'] -= self.max_speed
        if self.keys['arrowRight']:
            new_position['x'] += self.max_speed

        if self.check_collision(new_position):
            self.position = new_position

    def check_collision(self, new_position):
        _buffer = 1
        corners = [
            (new_position['x']-self.width/2+_buffer, new_position['y']-self.height/2+_buffer),
            (new_position['x']+self.width/2-_buffer, new_position['y']-self.height/2+_buffer),
            (new_position['x']+self.width/2-_buffer, new_position['y']+self.height/2-_buffer),
            (new_position['x']-self.width/2+_buffer, new_position['y']+self.height/2-_buffer)]
        self.occupied_cells = set([(x/self.board.cell_width, y/self.board.cell_width) for x, y in corners])
        move = True
        for cell_x, cell_y in self.occupied_cells:
            if cell_x < 0 or cell_y < 0:
                self.free = True
                break
            try:
                cell = self.board.board[cell_x][cell_y]
            except IndexError:
                self.free = True
                break
            if cell:
                if isinstance(cell, tuple):
                    cell = self.board.board[cell[0]][cell[1]]
                    if isinstance(cell, buildings.Fence):
                        move = False
                        break
                if isinstance(cell, buildings.Building) and cell.blocking:
                    move = False
                    break
        if move and self.board.key_pos in self.occupied_cells:
            self.board.open_gate()
        return move


class Key:
    def __init__(self):
        self.graphic = Tkinter.PhotoImage(file='img/key.gif')

    def place(self, board):
        fence_pos = (0, 0)
        for i in range(board.width):
            for j in range(board.height):
                if isinstance(board.board[i][j], buildings.Fence):
                    fence_pos = (i, j)
        i_range = (0, board.width/3)
        j_range = (0, board.height/3)
        if fence_pos[0] == 0:
            i_range = (board.width*2/3, board.width-1)
        if fence_pos[1] == 0:
            j_range = (board.height*2/3, board.height-1)

        while True:
            cell_x = random.randint(i_range[0], i_range[1])
            cell_y = random.randint(j_range[0], j_range[1])
            cell_choice = board.board[cell_x][cell_y]
            if not cell_choice:
                board.board[cell_x][cell_y] = self
                return cell_x, cell_y


class Board:
    def __init__(self, width=15, height=15, cell_width=30, density=1):
        self.width = width
        self.height = height
        self.cell_width = cell_width
        self.density = density
        self.filled_spaces = 0
        self.board = [[None]*height for _ in range(width)]
        buildings.Prison().place(self, (self.width+1)/2-3, (self.height+1)/2-3)
        self.fence_pos = buildings.Fence().place(self)
        self.key_pos = Key().place(self)

        attempts = 0
        max_attempts = 10000
        while True:
            if self.filled_spaces*1.0/(self.width*self.height) > density or attempts > max_attempts:
                break
            options = [buildings.House, buildings.HouseMediumVertical, buildings.Church,
                       buildings.HouseMediumHorizontal, buildings.MeetingHouse]
            choice = random.choice(options)()
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


if __name__ == '__main__':
    main()