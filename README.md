# OSRS-Server

Revision 209 OSRS server written in Kotlin.

# Dependencies
- [Kotlin](https://kotlinlang.org/docs/home.html) (Language)
- [Kotlin-guice](https://github.com/misfitlabsdev/kotlin-guice) (Dependecy injection)
- [Ktor](https://ktor.io/) (Networking - HTTP Server and our internal netcode is built on-top of Ktor)
- [Runetopic Cache Library](https://github.com/runetopic/cache-lib) (Library for reading game files)

# Project structure

The server is broken up into multiple modules to make things easier to manage and test indivudual components.

- [application](/application) (This is used to bootstrap and launch the server. This ties all of the modules together using guice)
- [cache](/cache) (This is the module responsible for managing the game files)
- [game](/game) (This is used to hold all of the core game related code)
- [http](/http) (This is used to setup an HTTP server to serve the OSRS client the static files. For example: jav_config.ws and latest gamepack.jar)
- [network](/network) (This is the main netcode responsible for handling client sessions and communication between the two)
