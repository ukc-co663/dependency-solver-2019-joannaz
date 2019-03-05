all: compile

compile: deps
	sudo ./compile.sh

deps:
	chmod -R 755 /
	./install_deps.sh
	touch deps

test: compile
	./run_tests.sh

clean:
	rm -rf classes

reallyclean: clean
	rm -rf lib deps

.PHONY: all compile test clean reallyclean
