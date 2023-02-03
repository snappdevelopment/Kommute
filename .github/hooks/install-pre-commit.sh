#!/usr/bin/env zsh

if [ ! -f "$(pwd)/README.md" ]; then
  echo "This script needs to be executed from the project root. Aborted."
  exit 1
fi

SOURCE_FILE="$(pwd)/.github/hooks/pre-commit.sh"
DESTINATION_FILE="$(pwd)/.git/hooks/pre-commit.sh"

cp $SOURCE_FILE $DESTINATION_FILE
chmod +x $DESTINATION_FILE

if [ "$?"=="0" ]; then
  echo "Installing pre-commit hook successful."
fi