# OSRS-Server

Revision 209 OSRS server written in Kotlin.

[![Discord](https://img.shields.io/discord/212385463418355713?color=%237289DA&logo=Discord&logoColor=%237289DA)](https://discord.gg/3scgBkrfMG)
[![wakatime](https://wakatime.com/badge/user/00b793fe-9bcc-4e7a-88c2-7c1879c548ce/project/ed70e7ef-2223-4791-91ae-3c27fa5f8c89.svg)](https://wakatime.com/badge/user/00b793fe-9bcc-4e7a-88c2-7c1879c548ce/project/ed70e7ef-2223-4791-91ae-3c27fa5f8c89)
# Dependencies
- [Kotlin](https://kotlinlang.org/docs/home.html) (Language)
- [Kotlin-guice](https://github.com/misfitlabsdev/kotlin-guice) (Dependecy injection)
- [Ktor](https://ktor.io/) (Networking - HTTP Server and our internal netcode is built on-top of Ktor)
- [Runetopic Cache Library](https://github.com/runetopic/cache-lib) (Library for reading game files)

# Project structure

The server is broken up into multiple modules to make things easier to manage and test individual components.

- [application](/application) (This is used to bootstrap and launch the server. This ties all the modules together using guice)
- [cache](/cache) (This is the module responsible for managing the game files)
- [game](/game) (This is used to hold all of the core game related code)
- [http](/http) (This is used to setup an HTTP server to serve the OSRS client the static files. For example: jav_config.ws and latest gamepack.jar)
- [network](/network) (This is the main netcode responsible for handling client sessions and communication between the two)

# Info

**Packet sizes for 202:**
```
packet {
    sizes = [ 0,9,14,-1,-1,3,8,0,-2,3,-1,7,8,-1,7,7,8,3,15,8,8,8,8,8,7,7,13,4,8,-1,7,3,7,4,-1,-1,4,8,-2,1,-1,-1,3,2,6,3,0,8,15,-1,3,5,8,4,15,6,3,16,16,8,-1,3,7,-1,8,8,-1,3,8,-2,6,3,11,-1,8,-1,-1,8,-1,-1,11,8,7,3,15,2,9,2,0,-1,4,8,3,7,-1,-1,-1,0,-1,2,2,10,3,16,11,7,11,22 ]
}
```
