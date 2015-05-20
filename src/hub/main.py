__author__ = 'Adam Snyder'

import server
import buildings
import random
import copy
import Tkinter


def main():
    server.start_server()
    Game()
    server.stop_server()


class Screen(Tkinter.Frame):

    def __init__(self, game):
        Tkinter.Frame.__init__(self, game.frame)
        self.game = game
        self.canvas = Tkinter.Canvas(self, height=game.height, width=game.width)
        self.canvas.pack()

    def main_loop(self):
        self.canvas.delete(Tkinter.ALL)


class LobbyScreen(Screen):

    def __init__(self, game):
        Screen.__init__(self, game)

    def main_loop(self):
        pass


class GameScreen(Screen):

    def __init__(self, game):
        Screen.__init__(self, game)

    def main_loop(self):
        while not server.control_queue.empty():
            message = server.control_queue.get()
            print message
            if message['playerId'] not in self.players:
                self.players[message['playerId']] = Player(message['playerId'],
                                                           (random.randint(50, 200),
                                                            random.randint(50, 200),
                                                            random.randint(50, 200)),
                                                           self.board)
            self.players[message['playerId']].keys[message['button']] = message['status']

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
        players_in_game = [p for p in self.players.values() if not p.free]
        if not players_in_game and self.players:
            self.board.reset()
            for player in self.players.values():
                player.free = False
                player.position = {
                    'x': player.cell['x']*self.board.cell_width+self.board.cell_width/2,
                    'y': player.cell['y']*self.board.cell_width+self.board.cell_width/2
                }
        for player in players_in_game:
            if isinstance(player, Player):
                player.update_position()
                self.canvas.create_oval(player.position['x']-player.width/2, player.position['y']-player.width/2,
                                        player.position['x']+player.width/2, player.position['y']+player.width/2,
                                        fill='black')
                # pygame.draw.circle(self.screen, player.color, (player.position['x'], player.position['y']),
                #                    player.width/2, 0)
                # self.screen.blit(player.graphic, (player.position['x'], player.position['y']))
            else:
                print 'ERROR: ', type(player)


class Game:

    def __init__(self, width=2000, height=1000):
        self.width = width
        self.height = height
        self.root = Tkinter.Tk()
        self.frame = Tkinter.Frame()
        self.frame.pack()
        self.screen = None
        self.cell_width = 50
        self.players = {}
        self.board = Board(self.width/self.cell_width, self.height/self.cell_width, self.cell_width)
        self.key = self.board.board[self.board.key_pos[0]][self.board.key_pos[1]]
        self.fps = 30
        self.load_screen()
        self.main_loop()
        self.root.mainloop()

    def load_screen(self, screen):
        self.screen = screen(self)

    def main_loop(self):
        self.screen.main_loop()
        self.root.after(1000/self.fps, self.main_loop)


class Player:
    def __init__(self, player_id=None, _color=(250, 250, 0), board=None):
        self.board = board
        self.graphic = None
        self.player_id = player_id
        self.keys = {
            'arrowUp': False,
            'arrowLeft': False,
            'arrowDown': False,
            'arrowRight': False
        }
        self.height = board.cell_width*2/3
        self.width = board.cell_width*2/3
        self.free = False
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
        occupied_cells = set([(x/self.board.cell_width, y/self.board.cell_width) for x, y in corners])
        move = True
        for cell_x, cell_y in occupied_cells:
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
        if move and self.board.key_pos in occupied_cells:
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