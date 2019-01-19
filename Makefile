
build:
	javac RAM.java
	javac CPU.java
	javac Controler.java
	javac Simulation.java
	javac Byte.java
	javac Register.java
	javac RegisterBank.java
	javac Bus.java

run:
	java Simulation

clean:
	rm -f *.class