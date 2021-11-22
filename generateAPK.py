#!/usr/bin/python
# -*- coding: UTF-8 -*-

import os
import shutil
import sys

def deleteLastApks():
    apk_path = 'app/build/outputs/apk/release/'
    if os.path.isdir(apk_path):
        shutil.rmtree(apk_path, ignore_errors=True)

def getnerate_apk():
   # cmd_resguard = r'./gradlew assembleRelease'
    cmd_resguard = r'./gradlew resguardRelease'
    os.system(cmd_resguard)


if __name__ == '__main__':
    deleteLastApks()
    getnerate_apk()