#!/bin/sh

./rungamefile org/jzuul/games/zuul/initial.xml -history org/jzuul/games/zuul/demo.xml -gui SwingGui -debug 0 &

./rungamefile org/jzuul/games/zuul/initial.xml -history org/jzuul/games/zuul/demo.xml -gui SwtGui -debug 0 &

./rungamefile org/jzuul/games/zuul/initial.xml -history org/jzuul/games/zuul/demo.xml -gui TextUi -debug 0
