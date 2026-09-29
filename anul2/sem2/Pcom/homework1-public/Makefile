PROJECT=router
C_SOURCES=lib/queue.c lib/list.c lib/lib.c
CPP_SOURCES=router.cpp trie.cpp
LIBRARY=nope
INCPATHS=include
LIBPATHS=.
LDFLAGS=
CFLAGS=-c -Wall -Werror -Wno-error=unused-variable
CXXFLAGS=-c -Wall -Werror -Wno-error=unused-variable -std=c++17
CC=gcc
CXX=g++

# Automatic generation of some important lists
C_OBJECTS=$(C_SOURCES:.c=.o)
CPP_OBJECTS=$(CPP_SOURCES:.cpp=.o)
OBJECTS=$(C_OBJECTS) $(CPP_OBJECTS)
INCFLAGS=$(foreach TMP,$(INCPATHS),-I$(TMP))
LIBFLAGS=$(foreach TMP,$(LIBPATHS),-L$(TMP))

# Set up the output file names for the different output types
BINARY=$(PROJECT)

all: $(C_SOURCES) $(CPP_SOURCES) $(BINARY)

$(BINARY): $(OBJECTS)
	$(CXX) $(LIBFLAGS) $(OBJECTS) $(LDFLAGS) -o $@

.c.o:
	$(CC) $(INCFLAGS) $(CFLAGS) -fPIC $< -o $@

.cpp.o:
	$(CXX) $(INCFLAGS) $(CXXFLAGS) -fPIC $< -o $@

clean:
	rm -rf $(OBJECTS) router hosts_output router_*

run_router0: all
	./router rtable0.txt rr-0-1 r-0 r-1

run_router1: all
	./router rtable1.txt rr-0-1 r-0 r-1
