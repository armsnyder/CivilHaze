# Civil Haze
Civil Haze is a light, cooperative game in which players must work together to survive for as long as possible without
getting lost in an ever-encroaching dense fog. It is an entertaining group experience that engages players by 
requiring them to use their smart phones to interact with the game. While the gameplay takes place on a central screen, 
such as a television or computer monitor, players' phones transform into controllers equipped with joysticks and 
buttons that they use to move and control their on-screen characters.

## Starting The Game
You can play Civil Haze right now! It is incredibly easy to set up.
1. [Download the latest release](https://github.com/armsnyder/CivilHaze/releases/latest) 
(requires [Java](https://java.com/download))
2. Run the game
3. Have all players open a web browser on their smart phones and navigate to [civilhaze.com](http://civilhaze.com)
4. The phones are automatically connected directly to your game

## How It Works
There are three modules that make up the Civil Haze code base: the game application, the central server, and the mobile 
controller.

### The Game Application
The game application is the downloadable release that users run on their machine in order to play the game. It houses 
the code that runs the game instance and runs a local server to listen for controller input from players' mobile 
devices. It is written in [Java](www.oracle.com/technetwork/java/javase/downloads/), complies with the 
[Maven build framework](https://maven.apache.org/), and uses the [Slick2D game library](slick.ninjacave.com/), which is 
built on top of [LWJGL](https://www.lwjgl.org/).

When the application is launched, it initializes an HTTP server on port 8000, which it uses to receive input from 
controllers. It then attempts to connect to the central Civil Haze game server in order to register the game instance 
(more on that below). The application's internal server interprets the incoming messages from player controllers and 
then passes them on to a message pool, which is available to the game instance and is checked on every game tick. There 
are three types of messages that the application's internal server handles: connection, ping, and input messages. 
Connection messages are used to handle the creation and destruction of players, including a handshake that determines 
that player's unique ID and player color. Ping messages are periodically sent by player controllers to let the 
application know that the player is still connected. If the interval between pings is too large, the connection times 
out and the player is disconnected. Finally, input messages are used to let the game instance know that status of the 
buttons and joysticks on the players' mobile controllers, which cause player characters to move and act on-screen.

### The Central Server
The goal of the central server is to facilitate the connection between the player controllers and the running game 
instance. Without the central server, connecting player controllers to a game would be a manual process that would 
require typing in the IP address of the machine running the game. The central server acts as a known connection point, 
hosted at [civilhaze.com](http://civilhaze.com), and is able to direct mobile controllers to the correct game 
instance in a manner similar to how DNS lookup works on the web. The central server code is written in 
[Javascript](https://www.javascript.com/) as a [Node.js](https://nodejs.org) application with the 
[Express](http://expressjs.com/) web framework. The database used is [MongoDB](https://www.mongodb.org/), and the whole 
package is hosted using an [Amazon Web Services](https://aws.amazon.com/) EC2 instance.

When a user starts a new game instance (see above), the game instance connects to the central server and sends its own 
private IP address. The central server now has the game's private IP, from the message's payload, and the game's public
IP, from the message's header. (As a quick aside for non-engineers, a public IP is an address that can be accessed over 
the Internet, whereas a private IP is an address that can only be accessed from within the same local network, such as 
a home or school network.) Now that the central server has both IPs, it stores them in a database along with a 
timestamp. This database will act as a lookup table for connecting player controllers.

When a user opens up the mobile controller on their personal device (more on that below), the controller sends a 
request to the central server for the private IP address of the game application. The central server receives the 
public IP address from the user's mobile device from the request header. If the mobile device and the game instance 
are on the same local network, then they will have matching public IP addresses. So, the central server then looks at 
teh most recent database entry for the public IP and responds with the last known private IP for a game instance on 
that network. Now that the mobile controller knows the private IP address of the game instance, the central server's 
job is finished and is no longer needed.

### The Mobile Controller
The mobile controller is the web application the runs on the players' personal devices. It is written in 
[Javascript](https://www.javascript.com/) using the [AngularJS](https://angularjs.org/) framework. The application is 
hosted on the central server (see above) and is served when the central server receives any non-API request (i.e. the 
root [civilhaze.com](http://civilhaze.com) page serves the controller).

Once the web application is loaded onto a player's mobile device, it sends a request to the central server for the game 
instance's private IP (as explained above). Once it has this private IP it is able to send all of its inputs to the 
game instance directly. The mobile application then initiates a handshake with the game instance, letting the game 
instance know that a new player is connecting and receiving a unique player ID, as well as a player color. The 
background of the web application then changes color to this player color so that the player knows which color 
character they are in the game. From then on, any time a player presses or releases a button or moves a joystick in the 
web application, the web application sends a HTTP request to the game instance containing data about the player ID and 
input.

### License

Copyright (C) 2016 Adam Snyder. All rights reserved.