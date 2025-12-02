$filePath = 'c:\Users\paulv\OneDrive\Desktop\RSPS\Elvarg - Server\src\com\elvarg\net\packet\impl\CommandPacketListener.java'
$lines = Get-Content $filePath

# Find the cleararea section and replace it completely
$newCode = @(
'		if (parts[0].equalsIgnoreCase("cleararea")) {',
'			int radius = 10; // default radius',
'			if (parts.length >= 2) {',
'				try {',
'					radius = Integer.parseInt(parts[1]);',
'				} catch (NumberFormatException e) {',
'					player.getPacketSender().sendMessage("Invalid radius. Using default: 10");',
'				}',
'			}',
'			',
'			Position center = player.getPosition();',
'			int count = 0;',
'',
'			// Remove all objects within the radius on all height levels',
'			for (int z = 0; z < 4; z++) {',
'				for (int x = center.getX() - radius; x <= center.getX() + radius; x++) {',
'					for (int y = center.getY() - radius; y <= center.getY() + radius; y++) {',
'						Position pos = new Position(x, y, z);',
'						',
'						// Try to get and remove the object from RegionClipping',
'						GameObject obj = com.elvarg.world.collision.region.RegionClipping.getGameObject(pos);',
'						if (obj != null) {',
'							// Remove from RegionClipping (also removes collision)',
'							com.elvarg.world.collision.region.RegionClipping.removeObject(obj);',
'							count++;',
'						}',
'					}',
'				}',
'			}',
'			',
'			// Also remove any dynamically spawned objects from ObjectHandler',
'			java.util.List<GameObject> objectsToRemove = new java.util.ArrayList<>();',
'			for (GameObject obj : ObjectHandler.getObjects()) {',
'				if (obj == null) continue;',
'				Position objPos = obj.getPosition();',
'				if (objPos.getX() >= center.getX() - radius && ',
'					objPos.getX() <= center.getX() + radius &&',
'					objPos.getY() >= center.getY() - radius && ',
'					objPos.getY() <= center.getY() + radius &&',
'					objPos.getZ() >= 0 && objPos.getZ() <= 3) {',
'					objectsToRemove.add(obj);',
'				}',
'			}',
'			for (GameObject obj : objectsToRemove) {',
'				ObjectHandler.despawnGlobalObject(obj);',
'			}',
'			',
'			// Force region reload by moving player temporarily',
'			player.getPacketSender().sendMessage("Cleared " + count + " objects. Reloading region...");',
'			Position returnPos = player.getPosition().copy();',
'			player.moveTo(new Position(returnPos.getX(), returnPos.getY() + 50, returnPos.getZ()));',
'			',
'			// Teleport back after a brief moment',
'			TaskManager.submit(new Task(1, player, false) {',
'				@Override',
'				public void execute() {',
'					player.moveTo(returnPos);',
'					player.getPacketSender().sendMessage("Area cleared successfully with radius " + radius + ".");',
'					stop();',
'				}',
'			});',
'		}'
)

# Find start and end of cleararea command
$startIdx = -1
$endIdx = -1
for ($i = 0; $i -lt $lines.Length; $i++) {
    if ($lines[$i] -match 'if \(parts\[0\]\.equalsIgnoreCase\("cleararea"\)\)') {
        $startIdx = $i
    }
    if ($startIdx -ge 0 -and $lines[$i] -match 'if \(parts\[0\]\.equalsIgnoreCase\("removeobject"\)') {
        $endIdx = $i - 1
        break
    }
}

if ($startIdx -ge 0 -and $endIdx -ge 0) {
    $newLines = @()
    $newLines += $lines[0..($startIdx-1)]
    $newLines += $newCode
    $newLines += $lines[$endIdx..($lines.Length-1)]
    
    $newLines | Set-Content $filePath
    Write-Host "Updated cleararea command successfully"
} else {
    Write-Host "Could not find cleararea command boundaries"
}
