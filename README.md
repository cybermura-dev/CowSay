# CowSay Plugin

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technologies](#technologies)
- [Installation](#installation)
  - [Prequisites](#prequisites)
  - [Steps](#steps)
- [Usage](#usage)
  - [Player Command](#player-command)
  - [Admin Command](#admin-command)
- [Code Structure](#code-structure)
  - [Bukkit Plugin](#bukkit-plugin)
  - [BungeeCord Plugin](#bungeecord-plugin)
- [How it Works](#how-it-works)
  - [Command Execution](#command-execution)
  - [Cow Spawning](#cow-spawning)
  - [Movement and Effects](#movement-and-effects)
  - [Database Tracking](#database-tracking)
- [License](#license)

## Overview

The CowSay plugin is a fun and interactive Minecraft plugin that allows players to summon custom cows that display personalized messages and move in an animated fashion. This plugin integrates with BungeeCord to enable cross-server communication, allowing messages to be broadcasted across multiple servers. The plugin also tracks player usage and stores data in a MySQL database.

## Features

- **Custom Cow Spawning:** Players can summon cows that display a custom message.
- **Animated Cow Movement:** Spawned cows move in a circular pattern with adjustable speed and radius.
- **Particle Effects and Sounds:** Cows emit particles and sounds at specified intervals, enhancing the visual and auditory experience.
- **Per-Player Usage Tracking:** The plugin keeps track of how many times each player has used the command, storing data in a MySQL database.
- **Cross-Server Command Handling:** Utilizes BungeeCord to handle commands and messages across multiple servers.

## Technologies

- **Bukkit API:** For server-side plugin development.
- **BungeeCord API:** For proxy-side plugin development and cross-server communication.
- **MySQL Database:** For storing player usage data.
- **Kotlin Programming Language:** For plugin development.

## Installation

### Prerequisites

- **JDK 8u231:** Ensure you have JDK 8u231 installed. You can download it from the Oracle website.
- **Gradle 8.5:** Ensure you have Gradle 8.5 installed. You can download it from the Gradle website.
- **Spigot 1.12.2:** Ensure you have Spigot 1.12.2 installed. You can compile it using BuildTools.

### Steps

1. **Clone the Repository:**
   
    ```bash
    git clone https://github.com/takeshikodev/CowSay.git
    cd CowSay
    ```

2. **Set Up Spigot 1.12.2:**
   
    Download BuildTools from the Spigot website.
    Run the following command to compile Spigot 1.12.2:
    ```bash
    java -jar BuildTools.jar --rev 1.12.2
    ```
    Ensure the compiled Spigot JAR is placed in your local Maven repository:
    ```bash
    mvn install:install-file -Dfile=spigot-1.12.2.jar -DgroupId=org.spigotmc -DartifactId=spigot -Dversion=1.12.2-R0.1-SNAPSHOT -Dpackaging=jar
    ```
   
3. **Build the Project:**
   
    Use Gradle to build the project:
    ```bash
    ./gradlew build
    ```
   
    The compiled JAR files will be located in the build/libs/ directory.

4. **Place the JAR Files:**
   
    Put `CowSay-1.0.0.jar` into your Bukkit/Spigot and BungeeCord server's plugins/ directory.

5. **Configure the Plugin:**
   
   On the server side, configure plugins/CowSayBukkit/config.yml with desired settings.
   On the proxy side, configure plugins/CowSayBungee/config.yml, including database connection details.
   ```yaml
   database:
    host: localhost
    port: 3306
    database: cowsay
    username: root
    password: password

   cow:
    radius: 5.0
    speed: 1.5
    duration: 10
    moo_interval: 0.5
    particle: FLAME

6. **Set Up the Database:**
   
    Ensure a MySQL database is set up and accessible as specified in the proxy configuration.
    The plugin will automatically create the necessary tables upon enabling.

7. **Enable the Plugin:**
   
    Start your server and proxy. The plugin should enable automatically upon server start.

## Usage

### Player Command

Players can use `/cowsay <message>` to summon a cow that displays their message.
The cow will move in a circle around the player with the specified radius and speed.
The cow will emit particles and make sounds at set intervals.

### Admin Commands

Currently, there are no explicit admin commands provided, but admins can manage the plugin through configuration files.

## Code Structure

### Bukkit Plugin

- **Listeners:** Handle events such as cow spawning and movement.
- **Managers:** Handle cow spawning, movement tasks, and message processing.
- **Tasks:** Manage the animated movement of cows.
- **Main Class:** CowSayBukkit initializes the plugin and registers events and channels.

### BungeeCord Plugin

- **Commands:** Handle the `/cowsay` command execution.
- **Managers:** Handle database interactions and configuration loading.
- **Main Class:** CowSayBungee initializes the plugin, sets up the database, and registers commands and channels.

```bash
CowSay/
├── build.gradle.kts            # Gradle build script
├── settings.gradle.kts         # Gradle settings script
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   ├── ru/
│   │   │   │   └── takeshiko/
│   │   │   │       └── minecraft/
│   │   │   │           └── cowsay/
│   │   │   │               ├── bukkit/
│   │   │   │               │   ├── CowSayBukkit.kt          # Main Bukkit plugin class
│   │   │   │               │   ├── listeners/
│   │   │   │               │   │   └── CowSayListener.kt    # Event listener for Bukkit
│   │   │   │               │   ├── managers/
│   │   │   │               │   │   ├── CowManager.kt        # Manages cow spawning and movement
│   │   │   │               │   │   └── MessageManager.kt    # Manages plugin messages
│   │   │   │               │   └── tasks/
│   │   │   │               │       └── CowMovementTask.kt   # Task for cow movement
│   │   │   │               └── bungee/
│   │   │   │                   ├── CowSayBungee.kt          # Main BungeeCord plugin class
│   │   │   │                   ├── commands/
│   │   │   │                   │   └── CowSayCommand.kt     # Command handler for BungeeCord
│   │   │   │                   └── managers/
│   │   │   │                       ├── ConfigManager.kt     # Manages configuration loading and saving
│   │   │   │                       └── DatabaseManager.kt   # Manages database interactions
│   │   └── resources/
│   │       ├── bungee.yml       # Configuration file for BungeeCord
│   │       ├── config.yml       # Default configuration
└── └──     └── plugin.yml       # Configuration file for Bukkit/Spigot
```

## How it Works

### Command Execution

When a player executes `/cowsay <message>`, the BungeeCord plugin captures this command, records the usage in the database, and sends a message to the server where the player is located.

### Cow Spawning

The server-side plugin receives the message, spawns a cow at the player's location with the specified message as its name.
The cow is assigned a movement task that moves it in a circular path.

### Movement and Effects

The movement task updates the cow's position, emits particles, and plays sounds at set intervals.
After a specified duration, the cow explodes and is removed from the world.

### Database Tracking

Each time a player uses the command, the plugin records the event in the MySQL database, tracking the player's name, the message, and the count of uses.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

Copyright (c) 2025 Takeshiko 
