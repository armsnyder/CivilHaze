import random


class Building:
    height = 0
    width = 0

    def __init__(self):
        self.blocking = True
        pass

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
        for i in range(x, x+self.width):
            for j in range(y, y+self.height):
                if (i, j) != (x, y):
                    board.board[i][j] = (x, y)

        board.filled_spaces += self.width*self.height
        return True


class Prison(Building):
    height = 5
    width = 5
    color = (0, 0, 0)

    def __init__(self):
        Building.__init__(self)


class House(Building):
    height = 1
    width = 1
    color = (0, 150, 30)

    def __init__(self):
        Building.__init__(self)


class HouseMediumVertical(House):
    height = 2
    width = 1

    def __init__(self):
        House.__init__(self)


class HouseMediumHorizontal(HouseMediumVertical):
    height = 1
    width = 2

    def __init__(self):
        HouseMediumVertical.__init__(self)


class Church(Building):
    height = 3
    width = 3
    color = (100, 100, 100)

    def __init__(self):
        Building.__init__(self)


class MeetingHouse(Building):
    height = 3
    width = 4
    color = (0, 10, 130)

    def __init__(self):
        Building.__init__(self)


class Fence(Building):
    height = 1
    width = 1
    color = (0, 0, 0)

    def __init__(self):
        Building.__init__(self)

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