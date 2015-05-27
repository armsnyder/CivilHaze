__author__ = 'Adam Snyder'

import random


def random_color():
    vals = '3456789ABCDE'
    return '#%s%s%s' % (random.choice(vals), random.choice(vals), random.choice(vals))
