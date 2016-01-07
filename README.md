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
This project was the final submission of a game engine class that I took in Fall 2015. Over the course of the semester we were tasked with developing our own game engine from scratch and developing a basic 2D platformer alongside it. As part of the final project, we were asked to design a completely different game using the same engine. I was able to reuse my engine to develop this SpaceInvaders game in just 2 days.

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
