__author__ = 'Adam Snyder'

import server
import pygame
import sys
from pygame.locals import *


def main():
    MainWindow = PyManMain()
    MainWindow.MainLoop()


class PyManMain:
    """The Main PyMan Class - This class handles the main
    initialization and creating of the Game."""

    def __init__(self, width=640, height=480):
        """Initialize"""
        """Initialize PyGame"""
        pygame.init()
        """Set the window Size"""
        self.width = width
        self.height = height
        """Create the Screen"""
        self.screen = pygame.display.set_mode((self.width, self.height))
        server.start_server()

    def MainLoop(self):
        """This is the Main Loop of the Game"""
        while 1:
            # print server.control_queue
            for event in pygame.event.get():
                if event.type == pygame.QUIT:
                    sys.exit()
            if not server.control_queue.empty():
                print str(server.control_queue.get())
                # pygame.display.set_caption(str(server.control_queue.get()))


class Player:
    def __init__(self):
        self.graphic

if __name__ == '__main__':
    main()