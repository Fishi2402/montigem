#!/bin/bash

createIndexTS() {
  DIR="$1"
#  echo "Processing directory $DIR"
  INDEXTS="$DIR/index.ts"
  echo "" > $INDEXTS

  FILES="$DIR/*.ts"
  for f in $FILES
  do
    if [ -f "$f" ]
    then
      FILE="$(basename $f .ts)"
      if [ "$FILE" != "index" ] && [ "$FILE" != "public-api" ]
      then
#        echo "Processing file $FILE"
        echo "export * from './$FILE';" >> $INDEXTS
      fi
    fi
  done

  DIRS="$DIR/*"
  for d in $DIRS
  do
    if [ -d "$d" ]
    then
      DIRNAME="$(basename $d)"
#      echo "Processing subdirectory $DIRNAME"
      echo "export * from './$DIRNAME';" >> $INDEXTS
    fi
  done

  for d in $DIRS
  do
    if [ -d "$d" ]
    then
      DIRNAME="$(basename $d)"
#      echo "Proceeding with subdirectory $DIRNAME"
      createIndexTS $d
    fi
  done
}

BASEDIR=$1
if [ BASEDIR == "" ]; then
    echo "Please set the library base directory as only parameter"
    exit 0
fi
createIndexTS "$BASEDIR"
