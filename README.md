<h1 align="center">Turtle Graphics</h1>

<p align="center">
  <img src="media/turtle_graphics_logo.svg" style="width: 20%; aspect-ratio: 1/1; border-radius: 50%; object-fit: cover;" />
</p>

<p align="center">
Turtle started as an re-implementation of TurtleGraphics on Android OS with simple language, but then that simple language `Lilo`
turned into a python implementation that support GPU programming and allow you to mix CPU/GPU code in a nice way inspired by CUDA, Mojo and Python of course 😎.
</p>

# Download

[<img src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png"
alt="Get it on Google Play"
height="80">](https://play.google.com/store/apps/details?id=com.amrdeveloper.turtle)

## Screenshots

<p align="center">
  <img src="media/screenshots/screenshot_code.png" width="23%" height="500">
  <img src="media/screenshots/screenshot_gpu.png" width="23%" height="500">
  <img src="media/screenshots/screenshot_preview.png" width="23%" height="500">
  <img src="media/screenshots/screenshot_terminal.png" width="23%" height="500">
</p>

```py
from gpu import (
    gpu,
    Dim,
    LaunchConfig,
    ConfiguredKernal
)

@gpu
def vec_add(a, b, out c):
  i = gpu.global_id.x
  c[i] = a[i] + b[i]

a = [1.0, 2.0, 3.0, 4.0]
b = [5.0, 6.0, 7.0, 8.0]
c = [0.0, 0.0, 0.0, 0.0]

blocks = Dim(1, 1, 1)
threads = Dim(4, 1, 1)
config = LaunchConfig(blocks, threads)
kernal = ConfiguredKernal(vec_add, config)
kernal(a, b, c)
print("Output: ", c)
```

## Features
- Free And open source with no ads.
- Python implementation from scratch that support GPU Programming with user-friendly error messages.
- Supports most of Python stdlib modules.
- Preview custom view for turtle graphics execution.
- Shipped with a lot of examples to learn how to use GPU, Turtle and Python features.

### License
```
MIT License

Copyright (c) 2022 - Present Amr Hesham

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
