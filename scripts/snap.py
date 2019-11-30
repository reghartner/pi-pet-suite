#!/usr/bin/env python
import camera
import sys

if len(sys.argv) > 1:
    camera.snap(str(sys.argv[1]))
else:
    camera.snap('')