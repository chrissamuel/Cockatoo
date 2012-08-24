# Makefile for Cockatoo
# (c) Chris Samuel 2012
# License: GPLv3. http://geekscape.org/static/parrot_license.html

all:		Cockatoo.class

Cockatoo:	Cockatoo.class

Cockatoo.class: Cockatoo.java  Display.java DroneInfo.java  KeyboardInput.java  ParrotCommunication.java  Server.java
	javac Cockatoo.java

run: all
	java Cockatoo

clean:
	@rm -fv *.class
