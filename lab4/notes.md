# 4.5

`docker pull selenium/standalone-chrome`

`docker run -d -p 4444:4444 --shm-size=2g selenium/standalone-chrome`

`docker stop $(docker ps -q --filter ancestor=selenium/standalone-chrome)`

`docker rm $(docker ps -aq --filter ancestor=selenium/standalone-chrome)`