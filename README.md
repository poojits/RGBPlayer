Usage
=========
```
Usage: java -Xmx1024m RGBPlayer <filename> <scaleW> <scaleH> <fps> <anti-aliasing> <analysis>
   <filename>		Path to the *.rgb video file
   <scaleW>		Scaling factor for width
   <scaleH>		Scaling factor for height
   <fps>		Output frame rate of the video
   <anti-aliasing>	Switch to turn anti-aliasing on or off (default 0)
   <analysis>		Analysis and Extra credit (default 0)
   ```
Test Runs
========
Displays the video as is.

```
java -Xmx1024m RGBPlayer ../../../resources/Video1.rgb 1.0 1.0 30 0 0
```

Changes to half size with same fps.

```
java -Xmx1024m RGBPlayer ../../../resources/Video1.rgb 0.5 0.5 30 0 0
```
Changes to half size with same fps, but with anti-aliasing turned on.
```
java -Xmx1024m RGBPlayer ../../../resources/Video1.rgb 0.5 0.5 30 1 0
```

Changes the scale as specified and performs non-linear mapping
```
java -Xmx1024m RGBPlayer ../../../resources/Video1.rgb 2 1 30 0 1
```
Changes the scale as specified and performs non-linear mapping with anti-aliasing
```
java -Xmx1024m RGBPlayer ../../../resources/Video1.rgb 2 1 30 1 1
```