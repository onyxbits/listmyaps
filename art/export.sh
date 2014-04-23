#!/bin/bash
for i in ic_launcher.svg; do 
  inkscape -w 48 -e ../res/drawable-mdpi/`basename $i .svg`.png  $i;
  inkscape -w 144 -e ../res/drawable-xxhdpi/`basename $i .svg`.png  $i;
  inkscape -w 72 -e ../res/drawable-hdpi/`basename $i .svg`.png  $i;
  inkscape -w 96 -e ../res/drawable-xhdpi/`basename $i .svg`.png  $i;
done;

