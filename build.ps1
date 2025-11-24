# Build both server and client
Set-Location "$PSScriptRoot"
Write-Output "Compiling server..."
Set-Location "$PSScriptRoot\Elvarg - Server"
New-Item -ItemType Directory -Force -Path .\out | Out-Null
$serverFiles = Get-ChildItem -Path .\src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -d .\out -cp "..\lib\*;." $serverFiles 2>&1 | Tee-Object -FilePath compile_server.log
Write-Output "Server compile finished. See 'Elvarg - Server\compile_server.log' for details."

Write-Output "Compiling client..."
Set-Location "$PSScriptRoot\Elvarg - Client"
New-Item -ItemType Directory -Force -Path .\out | Out-Null
$clientFiles = Get-ChildItem -Path .\src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -d .\out -cp ".\bin;..\lib\*" $clientFiles 2>&1 | Tee-Object -FilePath compile_client.log
Write-Output "Client compile finished. See 'Elvarg - Client\compile_client.log' for details."

<<<<<<< HEAD
=======
# Copy compiled classes to bin directory to ensure they're used
Write-Output "Copying compiled classes to bin directory..."
Copy-Item -Path ".\out\*" -Destination ".\bin\" -Recurse -Force

>>>>>>> 2f6ed6014430cc7a2d06ba9c9b9b1a6e1b5c7497
Write-Output "Build complete."
