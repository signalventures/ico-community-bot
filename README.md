# ICO Community Bot

## About

A bot that is specifically tailored to help people administrate an ICO community channel, preventing scam attempts and flooding, amongst other functionalities. 

### Installation

#### System Requirements

* `Docker version 17.06.2-ce`
* `docker-compose version 1.14.0`
* `open-jdk version 8`

To make the installation process easier, you can install all runtime dependencies with a simple one-line comand:

```
curl -sSL https://raw.githubusercontent.com/signalventures/ico-community-bot/master/scripts/community-bot-installer.sh | bash
```

Simply copy/paste the command into your terminal, logged in as the root user, or another that has sudo privileges, and it will:

* Install Docker
* Install Docker Compose
* Download this repository to your home folder
* Assemble the bot from the source

### Configure your bot

Once all the dependencies have been installed, configuring the bot is as simple as editing the `.env` configuration file located in `~/ico-community-bot`, and changing the following values: 
 
 ```
 BOT_USERNAME=
 BOT_TOKEN=
 ```
 
`BOT_USERNAME` : should be set to the bot username (must end in `bot`).

`BOT_USERNAME` : should be set to the bot authentication token, as provided by the [`@BotFather`](http://telegram.me/BotFather) upon registration.


### Run Your Bot

When started, `docker-compose` will install and run two system components in the Docker virtual machine.

* Redis
* ICO Community Bot (Java)

They are started as a group and should not interfere with any other
software systems running on your server.

After finishing the configuration in the `.env` file and saving it make sure you are in the `~/ico-community-node` directory and run `make`. This will show you some Makefile commands that are available to you:

* `make up` : start the bot
* `make down` : stop the bot
* `make upgrade` : upgrade to newest release of the bot in git, restarting *all* services
* `make logs` : show, and tail, the `docker-compose` logfiles
* `make ps` : show the status of the running processes

To start the bot, simply run `make up`. This command will automatically pull down the appropriate Docker images (which might be a bit slow the first time) and start them.

### Monitor Your Bot

You can run `make ps` to see the services that are running.

You can run `make logs` to tail the logfiles for all `docker-compose` managed services.

### Advanced Bot Configuration

TODO