#!/usr/bin/env bash
set -e

# Tested with Google and Digital Ocean Ubuntu 16.04 LTS Virtual Machines
#
#
# You can run this startup script manually, by copying it to the host,
# or by issuing this curl command. Since this command pipes the script
# directly into a bash shell you should examine the script before running.
#
#   curl -sSL https://raw.githubusercontent.com/signalventures/ico-community-bot/master/scripts/community-bot-installer.sh | bash
#
# Digital Ocean provides good documentation on how to manually install
# Docker on their platform.
#
#   https://www.digitalocean.com/community/tutorials/how-to-install-and-use-docker-on-ubuntu-16-04
#
# Pre-requisites:
# - 64-bit Ubuntu 16.04 server
# - Non-root user with sudo privileges
#

# Don't run this script more than once!
if [ -f /.community-bot-installer ]; then
    echo "Looks like this script has already been run. Exiting!"
    exit 0
fi

echo '#################################################'
echo 'Installing JDK'
echo '#################################################'
sudo apt-get install -y openjdk-8-jdk

echo '#################################################'
echo 'Installing Docker'
echo '#################################################'
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
sudo apt-get update
apt-cache policy docker-ce
sudo apt-get install -y docker-ce make

echo '#################################################'
echo 'Allow current user to use Docker without "sudo"'
echo 'REQUIRES SSH session logout + login'
echo '#################################################'
sudo usermod -aG docker ${USER}

echo '#################################################'
echo 'Installing Docker Compose'
echo '#################################################'
sudo mkdir -p /usr/local/bin
sudo curl -s -L "https://github.com/docker/compose/releases/download/1.16.1/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

echo '#################################################'
echo 'Downloading ico-community-bot Github Repository'
echo '#################################################'
if [ ! -d "~/ico-community-bot" ]; then
  cd ~ && git clone -b master https://github.com/signalventures/ico-community-bot
fi

echo '#################################################'
echo 'Packaging application'
echo '#################################################'
cd ~/ico-community-bot && chmod +x mvnw && make package

echo '#################################################'
echo 'All dependencies have been installed!'
echo 'Please now exit and restart this SSH session'
echo 'before continuing with the README instructions.'
echo '#################################################'
