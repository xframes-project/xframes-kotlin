# xframes-kotlin

## Prerequisites

Either [download](https://github.com/xframes-project/xframes-jni-library/releases) or build the JNI DLL and dependent DLL files.

The JNI DLL requires the generated DLL files to be in your system PATH.

On Windows:
- fmt.dll (fmtd.dll in Debug mode)
- glfw3.dll

#### Windows
Temporary (Command Prompt):
`set PATH=%PATH%;C:\path\to\dlls\folder`

Temporary (PowerShell):
`$env:PATH += ";C:\path\to\dlls\folder"`

#### Linux/macOS
`export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/path/to/dlls/folder`

### With Gradle

No need to run batch/bash files if you already installed gradle globally

#### On Windows

`.\gradlew.bat runMain`

#### On Linux/macOS

`./gradlew runMain`

#### Additional build options

`./gradlew runMain --info`    # For detailed build information

`./gradlew clean runMain`     # For clean build

### Generate a 'fat' JAR file using Gradle

This is plug and play, though OS dependent due to the JNI library

`.\gradelw.bat fatJar`

`java -Djava.library.path=./native -jar app/build/libs/app.jar`

### Screenshots

Windows, OpenJDK 22

![image](https://github.com/user-attachments/assets/96e3a157-80e6-4b4a-bce9-64b8a749f056)
