#!/bin/bash
# arg1: path till the parent of the folder to copy
# arg2: name of the folder to copy
# e.g. To copy /home/biguser/spark/src to worker nodes, 
# bash rsync.sh /home/biguser/spark src
PREFIX="yahoo0"

for NO in {2,4}
do
	rsync -avz $1"/"$2 $PREFIX"0"$NO:$1
done

for NO in {32..42}
do
	rsync -avz $1"/"$2 $PREFIX$NO:$1
done

for NO in {44..45}
do
        rsync -avz $1"/"$2 $PREFIX$NO:$1
done

