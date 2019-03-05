#Default

import sys
from time import time

from omniORB import CORBA, PortableServer

import CosNaming

def main(argv):
    # Initialize the ORB
    orb = CORBA.ORB_init(sys.argv, CORBA.ORB_ID)

    # Get a reference to the Naming service                 
    ns = orb.resolve_initial_references ("NameService");
    rootContext = ns._narrow(CosNaming.NamingContext)

    if rootContext == None : 
        print "Failed to narrow the root naming context"
        sys.exit(1)


    # resolve the Count object in the Naming service
    #exitGateName =  [CosNaming.NameComponent("exitGateName", "")]
    #exitGate = rootContext.resolve(exitGateName)  
 
    print("Pay Station in Python")


if __name__ == '__main__':
    main()
