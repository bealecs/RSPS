Set-Location "$PSScriptRoot"
Write-Output "Starting Elvarg server (foreground)."
Write-Output "Working directory: $PWD"
Write-Output "Classpath: .\out;..\lib\*;"

java -Xms512m -Xmx2g -cp "out;..\lib\*;." com.elvarg.Elvarg
