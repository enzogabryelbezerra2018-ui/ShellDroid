cmake_minimum_required(VERSION 3.10)
project(shelldroid-native)

set(CMAKE_CXX_STANDARD 17)
add_library(native-lib SHARED native-lib.cpp)

find_library(log-lib log)

target_link_libraries(native-lib ${log-lib})
