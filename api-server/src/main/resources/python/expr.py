# -*- coding: UTF-8 -*-
import sys


def solve(eq, var='x'):
    eq1 = eq.replace("=", "-(") + ")"
    c = eval(eq1, {var: 1j})
    return -c.real / c.imag


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("error")
        sys.exit(1)
    expr = sys.argv[1]
    res = solve(expr, 'a')
    print(res)
