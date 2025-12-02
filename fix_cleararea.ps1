$filePath = 'c:\Users\paulv\OneDrive\Desktop\RSPS\Elvarg - Server\src\com\elvarg\net\packet\impl\CommandPacketListener.java'
$content = Get-Content $filePath -Raw

$oldCode = @'
			// Iterate through all objects in the game and remove those within the radius
			java.util.List<GameObject> objectsToRemove = new java.util.ArrayList<>();
			
			for (GameObject obj : ObjectHandler.getObjects()) {
				if (obj == null) {
					continue;
				}
				
				Position objPos = obj.getPosition();
				
				// Check if object is within the radius on any height level (0-3)
				if (objPos.getX() >= center.getX() - radius && 
					objPos.getX() <= center.getX() + radius &&
					objPos.getY() >= center.getY() - radius && 
					objPos.getY() <= center.getY() + radius &&
					objPos.getZ() >= 0 && objPos.getZ() <= 3) {
					objectsToRemove.add(obj);
				}
			}
			
			// Now remove all the objects we found
			for (GameObject obj : objectsToRemove) {
				ObjectHandler.despawnGlobalObject(obj);
				count++;
			}
'@

$newCode = @'
			// Remove all objects within the radius on all height levels
			for (int z = 0; z < 4; z++) {
				for (int x = center.getX() - radius; x <= center.getX() + radius; x++) {
					for (int y = center.getY() - radius; y <= center.getY() + radius; y++) {
						Position pos = new Position(x, y, z);
						GameObject obj = com.elvarg.world.collision.region.RegionClipping.getGameObject(pos);
						
						if (obj != null) {
							// Remove from RegionClipping (also removes collision)
							com.elvarg.world.collision.region.RegionClipping.removeObject(obj);
							
							// Send removal packet to all nearby players
							for (Player p : World.getPlayers()) {
								if (p != null && p.getPosition().isWithinDistance(pos, 60)) {
									p.getPacketSender().sendObjectRemoval(obj);
								}
							}
							count++;
						}
					}
				}
			}
			
			// Also remove any dynamically spawned objects from ObjectHandler
			java.util.List<GameObject> objectsToRemove = new java.util.ArrayList<>();
			for (GameObject obj : ObjectHandler.getObjects()) {
				if (obj == null) continue;
				Position objPos = obj.getPosition();
				if (objPos.getX() >= center.getX() - radius && 
					objPos.getX() <= center.getX() + radius &&
					objPos.getY() >= center.getY() - radius && 
					objPos.getY() <= center.getY() + radius &&
					objPos.getZ() >= 0 && objPos.getZ() <= 3) {
					objectsToRemove.add(obj);
				}
			}
			for (GameObject obj : objectsToRemove) {
				ObjectHandler.despawnGlobalObject(obj);
				count++;
			}
'@

$content = $content.Replace($oldCode, $newCode)
Set-Content $filePath $content -NoNewline

Write-Host "File updated successfully"
