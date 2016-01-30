## Video Demo
[![http://www.youtube.com/watch?v=gwAct4jfT10](http://img.youtube.com/vi/gwAct4jfT10/0.jpg)](http://www.youtube.com/watch?v=gwAct4jfT10 "http://www.youtube.com/watch?v=gwAct4jfT10")

## Play the Game!
1. Ensure the latest version of the [Java runtime environment](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html) is installed
2. Open and run "SpaceInvaders.jar"
3. On the "Server" tab, select a port of your choosing (or leave as default) and click "Start" to host a server
4. Change to the "Client" tab, enter the server address (or leave default for localhost) and the previously chosen port, then click "Connect"

You may play with a friend by giving them [your IP address](http://whatismyipaddress.com/) and having them connect to your server! The game starts as soon as a player shoots for the first time, and newly connected clients can only have a ship created for them when a level is not active.

## Controls
You can move horizontally by using either A/D or LeftArrow/RightArrow  
You can shoot by using W, UpArrow, or SpaceBar

## Gameplay
- The goal is simply to defeat the alien invaders before they reach the bottom of the screen.
- Aliens speed up when fewer are remaining, and they collectively change direction when any alien in the pack collides with the side of the screen.
- The aliens become 20% faster on each new level.  The first level can be cleared by a single player but is quite challenging to do so.

## Made With
- Language used: Java
- Software used: Eclipse
- Libraries used: Processing 2

## Project Notes
This project was the final submission of a game engine class that I took in Fall 2015. Over the course of the semester I was tasked with developing my own game engine from scratch, with a focus on providing support for a multithreaded client-server network architecture.  I developed a basic 2D platformer alongside it, and as part of the final project, I was asked to design a completely different game using the same engine. Due to the robust design and functionality of my original engine I was able to use it to create this multiplayer remake of Space Invaders in just two days.

## Reuse Permissions
This project should not be used as a resource by anyone tasked with a similar academic project.  Otherwise, this project may be freely used and redistributed.

## To Do List
- Implement HUD support for: player lives, points, level tracking
- Background music, sound effects
- Explosion animations
- Special Alien mother ship (as in the original)
- Scale difficulty with client count
- Refactor ServerLogic and ClientLogic classes
- Determine cleaner way to handle initComponents and unregisterForAllEvents calls
- Cleaner event handling and/or switch to HashTable design
- Fix rare edge case where player can kill one of the new aliens during game reset
- Ensure consistent component execution order

## Citations
- Laser beam art assets borrowed from [Rawdanitsu](http://opengameart.org/content/lasers-and-beams)
- Alien and player ship art are graphically modified versions of the original [Space Invaders](http://villains.wikia.com/wiki/Space_Invaders)
