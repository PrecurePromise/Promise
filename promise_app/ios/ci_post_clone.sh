# Install CocoaPods and npm using homebrew.
brew install cocoapods
brew install npm

echo "Running pod install"
npm install --legacy-peer-deps
cd ios
pod intsall