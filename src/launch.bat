javac -d out/production/path game/*.java
jar cfm SixteenMahjong.jar manifest.txt -C out/production/path .

start /max cmd /k "color A2&chcp 65001&cls&title Sixteen Tiles Mahjong&java -jar SixteenMahjong.jar"