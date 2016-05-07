####################################### Group 1 #######################################

The leader we have developed is under "Group1Leader.java"

We have written different implementations to play against each opponent/follower, which
can be specified as the first argument to the leader program in the following way:

java -Djava.rmi.server.hostname=127.0.0.1 Group1Leader $1

Where $1 represents the follower Mk number.
For example: "java Group1Leader 3" will run our instance designed to play against Mk3