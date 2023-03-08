# OSRS-Server

Revision 211 OSRS server written in Kotlin.

<a href="#"><img src="https://img.shields.io/badge/Build:%20-211-blue.svg"/></a>
[![Discord](https://img.shields.io/discord/212385463418355713?color=%237289DA&logo=Discord&logoColor=%237289DA)](https://discord.gg/3scgBkrfMG)
[![wakatime](https://wakatime.com/badge/user/00b793fe-9bcc-4e7a-88c2-7c1879c548ce/project/ed70e7ef-2223-4791-91ae-3c27fa5f8c89.svg)](https://wakatime.com/badge/user/00b793fe-9bcc-4e7a-88c2-7c1879c548ce/project/ed70e7ef-2223-4791-91ae-3c27fa5f8c89)
<a href="#"><img src="https://img.shields.io/badge/Powered%20by-Kotlin%201.8.0-blue.svg"/></a>
![](https://tokei.rs/b1/github/tyler27/osrs-server)
![](https://tokei.rs/b1/github/tyler27/osrs-server?category=files)

# Dependencies
- [Kotlin](https://kotlinlang.org/docs/home.html) (Language)
- [Kotlin-guice](https://github.com/misfitlabsdev/kotlin-guice) (Dependecy injection)
- [Ktor](https://ktor.io/) (Networking - HTTP Server and our internal netcode is built on-top of Ktor)
- [Runetopic Cache Library](https://github.com/runetopic/cache-lib) (Library for reading game files)

# Project structure

The server is broken up into multiple modules to make things easier to manage and test individual components.

- [application](/application) (This is used to bootstrap and launch the server. This ties all the modules together using guice)
- [game-server](/game-server) (This is used to hold all of the core game related code and submodules.)
- [http-server](/http-server) (This is used to setup an HTTP server to serve the OSRS client the static files. For example: jav_config.ws and latest gamepack.jar)

# Getting Started

### Client setup

- Download the [OpenOSRS](https://github.com/runetopic/openosrs/tree/localhost) client from our runetopic organization. This branch is configured to connect to the server  automatically.

### MongoDB Setup (Optional if local flag in ApplicationConfig is enabled)
- This project has support for mongoDB for account creation and verification as well as local disk storage. 
- If you're working on local development, and need to work offline, you may set the ```game.local``` flag to true in the ```application.conf``` 
- Please setup either a cloud instance, or a local instance. I will be providing this via docker in the future, but this is a manual process for now.
- Create a database called ```api``` and a collection called ```account``` if they do not get created automatically.
- Upon start of the server, an admin account will be created for you:
  - *Credentials: (Username=admin, password=password)*

### Application configuration
_This project uses ktor for the networking, therefore the application is powered via the application.conf file._
- Create a new file called ```application.conf``` inside of the [resources](./application/src/main/resources/) directory.
- Place the following contents inside of the application.conf:
  ```
  ktor {
      development = true
      deployment {
          port = 43594
          watch = [ classes, resources ]
      }
  }

  game {
      local = true
      benchmarking = false
      cores = 4
      build {
          major = 211
          minor = 1
          token = "ElZAIrq5NpKN6D3mDdihco3oPeYN2KFy2DCquj7JMmECPmLrDP3Bnw"
      }
      packet {
          sizes = [ 11,-2,14,13,10,7,15,-1,7,6,5,9,-1,8,11,16,7,15,-1,3,3,3,-1,8,8,8,0,8,-1,8,4,-1,8,7,3,11,7,-1,3,3,-1,3,-1,-1,3,15,8,3,8,2,-1,8,-2,7,8,3,8,8,6,7,2,4,9,-1,8,4,-1,8,-2,8,7,-1,7,3,-1,0,7,0,-1,2,4,8,0,11,16,6,8,3,1,7,3,8,-1,0,-1,-1,-1,8,-1,2,3,-1,4,-1,2,15,16,22 ]
      }
      cache {
          path = "./data/cache/"
          parallel = true
      }
      configuration {
          players = "./players/" 
          ui = "/ui/interface_info.yaml"
          xteas = "/map/xteas211.json"
      }
      rsa {
          exponent = "c1d3827d26f642394175bc3c67ed5edc848f2654b28977678b911008d30988f4a6425e0af3cf8dc27d6d4c986726f9a99308d5ad21cdd72e07782bdd82fddc74fab02d5650848b8867b72728a42eec96f85f1682e515cc0881932d80dc9d4e2664e3d4983b295b2cd8cd62871db459178d194c6ada9e4faf9ac49af0cb28a89"
          modulus = "9081ec4aacbe0be1718d5dcdf7bbfd3ba738b15b4ed5e7e9a4769f0fda07e8e2094b08553ae1b78c794a1e064d29613f80495e303fbaa4f056f77b8b162a96616b2ca50dcd1a76bee4ba9fb67c4b7cd463da1f8c610f9a2e108efd5a571a958c78c4e4a5bfb40ee9bd2d99ae56f7ba18574b5a71d037ad538aee992bbee56375"
      }
  }
  
  mongo {
      connection = "mongodb://username:password@ip:port"
  }
  ```
- _**The mongo configuration is optional as well as the player's directory configuration  if the ```local``` flag is true**_
- **_Update the connection string for mongo using the following format:_** ```mongodb://username:password@ip:port```This is also recommend injecting from an environment variable - checkout [Ktor Environment Variables](https://ktor.io/docs/configuration-file.html#environment-variables) for more information

### Server configuration (Required upon intial setup and revision upgrades)
- Download the supported revision cache (.dat2 format) and map keys (.json format) from [OpenRS2](https://archive.openrs2.org/caches).
- Place the cache files you downloaded into the ``./data/cache/`` directory. This directory is configurable from within the ```application.conf```
- Place the map keys you downloaded into the ``./application/resources/map/`` directory. ``xteas210.json`` is an example of how the file should be named.
- Download the revision gamepack.jar from [Runestats](https://archive.runestats.com/osrs/) and place this in the ``./application/resources/client_config/`` directory. ``gamepack.jar`` is an example of how the file should be named.
- You'll also need to update the jav_config file if you update the build to a more recent one that's included on this repo. To do this, grab the config from  [Runestats](https://archive.runestats.com/osrs/) that you downloaded already, and update the params that are included in the one you downloaded. They will look like this for example:
  ```
  param=2=https://payments.jagex.com/
  param=3=true
  param=4=1
  param=5=1
  param=6=0
  param=7=0
  param=8=true
  param=9=ElZAIrq5NpKN6D3mDdihco3oPeYN2KFy2DCquj7JMmECPmLrDP3Bnw
  param=10=5
  param=11=https://auth.jagex.com/
  param=12=337
  param=13=.runescape.com
  param=14=0
  param=15=0
  param=16=false
  param=17=http://www.runescape.com/g=oldscape/slr.ws?order=LPWM
  param=18=
  param=19=196515767263-1oo20deqm6edn7ujlihl6rpadk9drhva.apps.googleusercontent.com
  param=20=https://social.auth.jagex.com/
  param=21=0
  param=25=210
  param=28=https://account.jagex.com/
  ```
- Run the application!

# Features

### Zones:

Processing:
- Clear out the previously tracked zones and zone buffer.
- Collect a set of all the observed zones for each tick
- Iterate through the observed zones and build out all of the updates.
- Iterate through all of the players in the observed zones and write the updates.
- Clear out the zone of any pending updates

Shared Updates:
  - Shared updates are for updates visible to everyone in the observed zones 
  - This means we can generate the updates to send to each player every tick instead of having to build this out for each player.
  - Shared updates are always sent using enclosed packet and consist of:
      - ObjDelPacket
      - MapProjAnimPacket
      - LocAddPacket
      - LocDelPacket

Private Updates:
  - Private updates are used for things private to the player. These are things only the current observing player may see this tick.
  - Private updates are sent using the partial follows packet and consist of:
      - ObjUpdatePacket
      - ObjAddPacket
          - The way this works is they have an item collection that is serialized to the player. (This is used for instanced items, grave items, or when hopping worlds)
          - Each zone can hold up to 129 of the same item, and if another item is added, it replaces the least valuable item with the new one.

### Packets

  #### Client Packets Implemented:
  - Idle
  - IfButton
  - MoveGame
  - MoveMiniMap
  - NoTimeout
  - WindowStatus
  #### Server Packets Implemented:
   - CameraReset
   - HintArrow
   - IfOpenSub
   - IfOpenTop
   - MessageGame
   - MidiSong
   - ObjAdd
   - PlayerInfo
   - RebuildNormal
   - RunClientScript
   - SetPlayerOption
   - UpdateContainerFull
   - UpdateRunEnergy
   - UpdateStat
   - UpdateZoneFullFollows
   - UpdateZonePartialEnclosed
   - UpdateZonePartialFollows
   - VarpLarge
   - VarpSmall

### Media
![Test](https://i.gyazo.com/a2e43f3143a2a7f67fc2d22a84d2000c.jpg)

If you need help, have any questions, or just wanna chat, feel free to join our [discord!](https://discord.gg/3scgBkrfMG)
