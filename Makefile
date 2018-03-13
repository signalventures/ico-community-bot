SHELL := /bin/bash

all: help

# Get the location of this makefile.
ROOT_DIR := $(dir $(abspath $(lastword $(MAKEFILE_LIST))))

# Specify the binary dependencies
REQUIRED_BINS := docker docker-compose java javac
$(foreach bin,$(REQUIRED_BINS),\
    $(if $(shell command -v $(bin) 2> /dev/null),$(),$(error Please install `$(bin)` first!)))

.PHONY : help
help : Makefile
	@sed -n 's/^##//p' $<

## package         : Package the bot
.PHONY : package
package:
	@./mvnw package

## up              : Start bot
.PHONY : up
up:
	@docker-compose up -d

## down            : Shutdown bot
.PHONY : down
down:
	@docker-compose down

## logs            : Tail logs
.PHONY : logs
logs:
	@docker-compose logs -f -t

## ps              : View running processes
.PHONY : ps
ps:
	@docker-compose ps

## git-pull        : Git pull latest
.PHONY : git-pull
git-pull:
	@git pull

## upgrade         : Stop all, git pull, and start all
.PHONY : upgrade
upgrade: down git-pull package up
