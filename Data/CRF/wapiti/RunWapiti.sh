#!/bin/bash  
wapiti train -t 8 -p /home/simos/workspace/Master_thesis/Data/CRF/wapiti/pattern.txt -1 5 /home/simos/workspace/Master_thesis/Data/CRF/wapiti/train.txt /home/simos/workspace/Master_thesis/Data/CRF/wapiti/model
wapiti label -m /home/simos/workspace/Master_thesis/Data/CRF/wapiti/model /home/simos/workspace/Master_thesis/Data/CRF/wapiti/test.txt /home/simos/workspace/Master_thesis/Data/CRF/wapiti/result.txt

