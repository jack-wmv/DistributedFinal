# DistributedFinal

Election-Project
Distributed Systems final project - online election platform using java RMI

How to run:

save policy.txt and 1.txt to the src folder.

open 3 cmd windows, navigate to src folder. Run javac *.java

run rmiregistry in one window

run java -Djava.security.policy=policy.txt ElectionServer in another window

run java ElectionClient 127.0.0.1 in the last window and it will work.

user 1 is setup as a test user. username is 1, password is pass123, and they are account type voter who has not voted yet.
