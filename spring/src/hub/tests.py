__author__ = 'Adam Snyder'

import unittest
import main
import buildings


class Test(unittest.TestCase):

    def test_board_small(self):
        board = main.Board(7, 7)
        self.assertTrue(isinstance(board.board[1][1], buildings.Prison))
        self.assertEqual((1, 1), board.board[2][1])
        self.assertTrue(board.board[6][6])
