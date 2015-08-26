__author__ = 'Adam Snyder'

import server
import Tkinter
import screens

def main():
    App()

class App:

    def __init__(self):
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
        self.screen = screens.Screen(self)
        self.fps = 30
        self.load_screen(screens.LobbyScreen)
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

if __name__ == '__main__':
    main()
