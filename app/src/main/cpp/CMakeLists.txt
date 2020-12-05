# Sets the minimum version of CMake required to build the native
# library.
cmake_minimum_required(VERSION 3.4.1)

set(EXECUTABLE_OUTPUT_PATH "${CMAKE_CURRENT_SOURCE_DIR}/../assets/${ANDROID_ABI}")

set(SOURCE_FILES NetIFace.cpp Net.cpp NetCap.cpp)

add_executable(netcap ${SOURCE_FILES})