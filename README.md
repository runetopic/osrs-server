# OSRS-Server

Revision 210.3 OSRS server written in Kotlin.

<a href="#"><img src="https://img.shields.io/badge/Build:%20-210-blue.svg"/></a>
[![Discord](https://img.shields.io/discord/212385463418355713?color=%237289DA&logo=Discord&logoColor=%237289DA)](https://discord.gg/3scgBkrfMG)
[![wakatime](https://wakatime.com/badge/user/00b793fe-9bcc-4e7a-88c2-7c1879c548ce/project/ed70e7ef-2223-4791-91ae-3c27fa5f8c89.svg)](https://wakatime.com/badge/user/00b793fe-9bcc-4e7a-88c2-7c1879c548ce/project/ed70e7ef-2223-4791-91ae-3c27fa5f8c89)
<a href="#"><img src="https://img.shields.io/badge/Powered%20by-Kotlin-blue.svg"/></a>
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
