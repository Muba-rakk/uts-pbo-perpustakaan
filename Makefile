.PHONY: all clean run build

JAR = lib/gson-2.13.2.jar
SRC = src/models/*.java src/services/*.java src/utils/*.java src/main/*.java
BIN = bin

all: run

build:
	mkdir -p $(BIN)
	javac -cp $(JAR) -d $(BIN) $(SRC)

run: build
	java -cp $(BIN):$(JAR) main.Main

clean:
	rm -rf $(BIN)