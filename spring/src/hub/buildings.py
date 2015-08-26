__author__ = 'Adam Snyder'

import random
import game_objects
import Tkinter


class Building(game_objects.GameObject):
    height = 0
    width = 0

    def __init__(self, board):
        game_objects.GameObject.__init__(self, board)
        self.blocking = True
        self.cell = {
            'x': None,
            'y': None
        }

    def place(self, board, x, y):
        if x+self.width > board.width or y+self.height > board.height:
            return False

        for i in range(x-1, x+self.width+1):
            for j in range(y-1, y+self.height+1):
                try:
                    if board.board[i][j]:
                        return False
                except IndexError:
                    pass

        board.board[x][y] = self
        self.cell['x'] = x
        self.cell['y'] = y

        for i in range(x, x+self.width):
            for j in range(y, y+self.height):
                if (i, j) != (x, y):
                    board.board[i][j] = (x, y)

        board.filled_spaces += self.width*self.height
        return True


class Prison(Building):
    height = 5
    width = 5
    color = '#000'

    def __init__(self, board):
        Building.__init__(self, board)
        self.load_image('img/prison.gif')


class House(Building):
    height = 1
    width = 1
    color = 'green'

    def __init__(self, board):
        Building.__init__(self, board)
        self.load_image('img/house'+random.choice(['1', '2'])+'.gif')


class HouseMediumVertical(House):
    height = 2
    width = 1

    def __init__(self, board):
        House.__init__(self, board)
        self.load_image('img/house3.gif')


class Church(Building):
    height = 3
    width = 3
    color = '#AAA'

    def __init__(self, board):
        Building.__init__(self, board)
        self.load_image('img/church.gif')


class MeetingHouse(Building):
    height = 3
    width = 4
    color = 'blue'

    def __init__(self, board):
        Building.__init__(self, board)
        self.load_image('img/meetinghouse.gif')


class Fence(Building):
    height = 1
    width = 1
    color = '#000'

    def __init__(self, board):
        Building.__init__(self, board)

    def place(self, board, x=None, y=None):
        fence_blocks = []
        for i in range(board.width):
            fence_blocks.append((i, 0))
            fence_blocks.append((i, board.height-1))
        for j in range(board.height):
            fence_blocks.append((0, j))
            fence_blocks.append((board.width-1, j))
        choice = (0, 0)
        while choice == (0, 0) or choice == (0, board.height-1)\
                or choice == (board.width-1, 0) or choice == (board.width-1, board.height-1):
            choice = random.choice(fence_blocks)
        for block_x, block_y in fence_blocks:
            board.board[block_x][block_y] = choice
        board.board[choice[0]][choice[1]] = self
        return choice
