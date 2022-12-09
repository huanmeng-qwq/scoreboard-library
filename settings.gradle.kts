pluginManagement {
  repositories {
    gradlePluginPortal()
    maven("https://papermc.io/repo/repository/maven-public/")
  }
}

rootProject.name = "scoreboard-library-root"

include(":api")
include(":commons")
include(":implementation")
include("extra-kotlin")

include(":packet-adapter-base")
project(":packet-adapter-base").projectDir = file("versions/packet-adapter-base")

include(":packetevents")
project(":packetevents").projectDir = file("versions/packetevents")

include(":v1_19_R2")
project(":v1_19_R2").projectDir = file("versions/v1_19_R2")

include(":v1_8_R3")
project(":v1_8_R3").projectDir = file("versions/v1_8_R3")
