Example on how to use RxJava to load data from multiple data sources: https://goo.gl/4ow84A

# Setup

## Install nodejs

### Using HomeBrew

```bash
/usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"

brew update
brew doctor
export PATH="/usr/local/bin:$PATH"
brew install node
```

### Using installer

Download & run the installer from https://nodejs.org/en/download/

## Install json-server

```bash
npm install -g json-server
```

# Run fake API server

```bash
json-server --watch mockserver/users.json
```

# Update AndroidApplication

Update your local IP address into `AndroidApplication#API_HOST`.

# Deploy and run example

```bash
./gradlew installDebug
```

![demo](http://recordit.co/NdDXxXW2gj.gif)

