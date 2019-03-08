#Default

import sys

from EntryGate import EntryGate

def main(argv):
    try:
        eg = EntryGate("machine", sys.argv)
        eg.turn_on()
    except Exception as inst:
        print(inst)


if __name__ == '__main__':
    sys.exit(main(sys.argv))
