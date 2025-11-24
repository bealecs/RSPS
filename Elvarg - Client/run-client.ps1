Set-Location "$PSScriptRoot"
Write-Output "Starting Client (foreground)."
Write-Output "Working directory: $PWD"
Write-Output "Classpath: .\out;bin;..\lib\*"

java -Xms512m -Xmx1g -cp "out;bin;..\lib\*" com.runescape.Client
