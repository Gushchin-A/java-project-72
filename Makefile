build:
	cd app && ./gradlew build

test: # короткий вариант запуска исполняемого файла
	cd app && ./gradlew test

.PHONY: build test