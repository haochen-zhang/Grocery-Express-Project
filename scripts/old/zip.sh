#!/usr/bin/env bash

fileName=$1

zip -r "./${fileName}.zip" . -i "./src/*"
