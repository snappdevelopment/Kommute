#!/usr/bin/env zsh

echo "Running detekt check..."
OUTPUT="/tmp/detekt-$(date +%s)"
./gradlew detektAll -PdetektAutoFix=true > $OUTPUT
EXIT_CODE=$?
if [ $EXIT_CODE -ne 0 ]; then
  cat $OUTPUT
  rm $OUTPUT
  echo "***********************************************"
  echo "                 Detekt failed                 "
  echo " Please fix the above issues before committing "
  echo "***********************************************"
  exit $EXIT_CODE
fi
rm $OUTPUT