__author__ = 'Adam Snyder'

import random
import buildings
import server
import Tkinter
import copy


class GameObject:
    height = 1
    width = 1

    def __init__(self, board):
        self.board = board
        self.position = {
            'x': None,
            'y': None
        }
        self.graphic = None

    def tick(self, board):
        pass

    def draw(self, canvas, x=None, y=None):
        if isinstance(canvas, Tkinter.Canvas) and self.graphic:
            if not x or not y:
                x = self.position['x']
                y = self.position['y']
            canvas.create_image(x, y, image=self.graphic, anchor=Tkinter.NW)

    def load_image(self, filename):
        image = Tkinter.PhotoImage(file=filename)
        ratio = abs(int(round(float(self.board.cell_width)*self.height/image.height())))
        if ratio < 1:
            ratio = 1
        scaled_image = image.zoom(ratio, ratio)
        self.graphic = scaled_image


class Guard(GameObject):
    def __init__(self, board=None, game=None, _color='black'):
        GameObject.__init__(self, board)
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

    def tick(self, board):
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

    def draw(self, canvas, x=None, y=None):
        canvas.create_oval(self.position['x']-self.width/2, self.position['y']-self.width/2,
                           self.position['x']+self.width/2, self.position['y']+self.width/2,
                           fill=self.color, outline='red')


class Player(GameObject):
    def __init__(self, _color='orange', board=None, player_name=''):
        GameObject.__init__(self, board)
        self.player_name = player_name
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

    def tick(self, board):
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

    def draw(self, canvas, x=None, y=None):
        if isinstance(canvas, Tkinter.Canvas):
            canvas.create_oval(self.position['x']-self.width/2, self.position['y']-self.width/2,
                               self.position['x']+self.width/2, self.position['y']+self.width/2,
                               fill=self.color)
            canvas.create_text(self.position['x'], self.position['y']-self.height, text=self.player_name)


class Key(GameObject):
    def __init__(self, board):
        GameObject.__init__(self, board)
        self.load_image('img/key.gif')

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
                self.position = {
                    'x': cell_x*board.cell_width,
                    'y': cell_y*board.cell_width
                }
                return cell_x, cell_y
