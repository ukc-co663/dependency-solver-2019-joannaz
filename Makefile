all: compile

compile: deps
	sh ./compile.sh

deps:
	sh ./install_deps.sh
	touch deps

test: compile
	sh ./run_tests.sh

clean:
	rm -rf classes

reallyclean: clean
	rm -rf lib deps

.PHONY: all compile test clean reallyclean
