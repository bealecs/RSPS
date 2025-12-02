$filePath = 'c:\Users\paulv\OneDrive\Desktop\RSPS\Elvarg - Server\src\com\elvarg\net\packet\impl\CommandPacketListener.java'
$content = Get-Content $filePath -Raw

# Fix the formatting issue from the previous edit
$content = $content -replace 'player\.getPacketSender\(\)\.sendMapRegion\(\);\s+// Also remove', @'
player.getPacketSender().sendMapRegion();
		
		// Also remove
'@

# Remove the extra count++ and closing brace issues
$content = $content -replace 'ObjectHandler\.despawnGlobalObject\(obj\);\s+count\+\+;', 'ObjectHandler.despawnGlobalObject(obj);'

# Fix the final message
$content = $content -replace '\}\s+\}\s+if \(parts\[0\]\.equalsIgnoreCase\("removeobject"\)', @'
		}
		
		player.getPacketSender().sendMessage("Area cleared successfully with radius " + radius + ".");
	}
	if (parts[0].equalsIgnoreCase("removeobject")
'@

Set-Content $filePath $content -NoNewline
Write-Host "File fixed!"
