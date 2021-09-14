set APP_JAR_NAME=ytdl-gui-0.3-SNAPSHOT.jar

"%JAVA_HOME%\bin\jlink" --strip-debug --compress=2 --module-path "%JAVA_HOME%\jmods" --add-modules java.base,java.desktop --output "bundle\jre"
copy target\%APP_JAR_NAME% bundle\
copy youtube-dl.exe bundle\
copy ffmpeg.exe bundle\
copy media\icon.ico bundle\

echo start "YTDLGUI" jre\bin\javaw -jar %APP_JAR_NAME%>bundle\start_app.cmd
