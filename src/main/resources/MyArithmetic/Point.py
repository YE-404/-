import numpy as np
class Point():
    def __init__(self, x, y):
        self.x = x
        self.y = y


def getCircle(p1, p2, p3):
    x21 = p2.x - p1.x
    y21 = p2.y - p1.y
    x32 = p3.x - p2.x
    y32 = p3.y - p2.y
    # three colinear
    if (x21 * y32 - x32 * y21 == 0):
        return None
    xy21 = p2.x * p2.x - p1.x * p1.x + p2.y * p2.y - p1.y * p1.y
    xy32 = p3.x * p3.x - p2.x * p2.x + p3.y * p3.y - p2.y * p2.y
    y0 = (x32 * xy21 - x21 * xy32) / 2 * (y21 * x32 - y32 * x21)
    x0 = (xy21 - 2 * y0 * y21) / (2.0 * x21)
    R = ((p1.x - x0) ** 2 + (p1.y - y0) ** 2) ** 0.5
    return x0, y0, R


def circle(x1, y1, x2, y2, x3, y3):
    """
    :return:  x0 and y0 is center of a circle, r is radius of a circle
    """
    a = x1 - x2
    b = y1 - y2
    c = x1 - x3
    d = y1 - y3
    a1 = ((x1 * x1 - x2 * x2) + (y1 * y1 - y2 * y2)) / 2.0
    a2 = ((x1 * x1 - x3 * x3) + (y1 * y1 - y3 * y3)) / 2.0
    theta = b * c - a * d
    if abs(theta) < 1e-7:
        raise RuntimeError('There should be three different x & y !')
    x0 = (b * a2 - d * a1) / theta
    y0 = (c * a1 - a * a2) / theta
    r = np.sqrt(pow((x1 - x0), 2) + pow((y1 - y0), 2))
    return x0, y0, r



