# description 
- a bunch of examples for spring integration java dsl
- important ! Configurations need to be activated explicitly by their according profile inside application.yml

# simple
- simple example with just channels and logging

# simpleR
- same as simple just with lambdas and other shortcuts

# file
- echo "yo" > /Users/andreas/Downloads/inbound/copyme.txt

# service
- curl -m 1 "http://localhost:50100/callees/sayMyName?name=yo"
- curl -m 1 "http://localhost:50100/callees/sayMyOtherName?name=yo"

# tcp
- based on https://github.com/spring-projects/spring-integration-samples/tree/main/basic/tcp-client-server
- echo -n "hello\r\n" | nc 127.0.0.1 5001
