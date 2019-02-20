all: compile

compile:
	-wget -q https://packages.microsoft.com/config/ubuntu/18.04/packages-microsoft-prod.deb
	-dpkg -i packages-microsoft-prod.deb
	-add-apt-repository universe
	-curl https://packages.microsoft.com/keys/microsoft.asc | gpg --dearmor > microsoft.gpg
	-mv microsoft.gpg /etc/apt/trusted.gpg.d/microsoft.gpg
	-sh -c 'echo "deb [arch=amd64] https://packages.microsoft.com/repos/microsoft-ubuntu-bionic-prod bionic main" > /etc/apt/sources.list.d/dotnetdev.list'

	-apt-get install apt-transport-https -y
	-apt-get update -y
	-apt-get install dotnet-sdk-2.2 -y
	dotnet restore DepSolver
	dotnet build DepSolver

# https://stackoverflow.com/questions/54065894/cannot-install-net-core-2-2-on-ubuntu-18-04