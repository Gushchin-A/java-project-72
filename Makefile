build:
	cd app && ./gradlew build

run:
	cd app && ./gradlew run

test:
	cd app && ./gradlew test

.PHONY: build test