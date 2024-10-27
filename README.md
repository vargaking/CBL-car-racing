# CBL-car-racing
By: Rafael Silva and Dániel Gergely


Car game for the CBL project written in Java and Swing. You can choose to play against a computer or another player.
You can control the car using WASD and in case of two players, the other player can use the arrow keys. The game ends when one of the
cars completes 3 laps. The project uses the Gradle build system, and we used Git (GitHub) for version control and collaboration.
You can find the project on GitHub at https://github.com/vargaking/CBL-car-racing. The game runs on java 17.

## Installation
To run the game locally, follow these steps:

1. Clone the repository from GitHub or download the zip file and extract it:
    ```sh
    git clone https://github.com/vargaking/CBL-car-racing
    cd CBL-car-racing
    ```

2. Build the project using Gradle:
    ```sh
    ./gradlew build
    ```

3. Run the application:
    ```sh
    ./gradlew run
    ```

or

2. If you have gradle installed on your machine, you can run and build with:
    ```sh
    gradle run
    ```

## Development notes
In `RaceTrack.java` we created a racetrack with arrays representing the walls and the checkpoints/finish line.
These are relative points and they are scaled to the screen size set in `App.java`. The `Car.java` class
contains all the parameters of the car, such as position, speed, and direction. It also contains the logic
for the car movement and hitbox calculations, along with transformations for rendering.

The bot logic is implemented in `Bot.java`. It uses a simple state space search algorithm to find the best
path. The scoring system decides based on distance to the next checkpoint (we use midlines/racing lines to calculate this),
collision and speed.
(The number of reached checkpoints is taken into account only to avoid the bot trying to cheat the system).
The logic runs in a separate thread to improve performance. We found the optimal
depth for the search to be around 7-8, so it can run on real-time on machines such
as laptops (tested on Ryzen 7 5600U and Core dual-core i5 1.6GHz).
It has limited options compared to a human player, since it can only perform 1 action every 500 milliseconds.
There is room for optimization, but the bot is quite competitive at its current state.

## Learning goals
- Version control and collaboration with Git
- Gradle build system
- Java and Swing GUI
- Algorithm design and optimization