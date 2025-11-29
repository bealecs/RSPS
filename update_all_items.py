#!/usr/bin/env python3
"""
Non-interactive script to update all Elvarg RSPS item stats to match OSRS.
"""

import re
import json
import urllib.request
import urllib.parse
from typing import Dict, List, Optional
import time

# OSRS Wiki API endpoint
WIKI_API = "https://oldschool.runescape.wiki/api.php"
USER_AGENT = "Elvarg RSPS Item Stats Updater/1.0"

# Bonus index mapping
BONUS_INDICES = {
    'astab': 0,    # Attack stab
    'aslash': 1,   # Attack slash
    'acrush': 2,   # Attack crush
    'amagic': 3,   # Attack magic
    'arange': 4,   # Attack ranged
    'dstab': 5,    # Defence stab
    'dslash': 6,   # Defence slash
    'dcrush': 7,   # Defence crush
    'dmagic': 8,   # Defence magic
    'drange': 9,   # Defence ranged
    'str': 10,     # Melee strength
    'prayer': 11,  # Prayer bonus
    'rstr': 12,    # Ranged strength
    'mdmg': 13     # Magic damage %
}

def fetch_osrs_item_data(item_name: str) -> Optional[Dict]:
    """Fetch item data from OSRS Wiki API."""
    try:
        params = {
            'action': 'parse',
            'page': item_name,
            'prop': 'wikitext',
            'format': 'json',
            'formatversion': '2'
        }

        url = f"{WIKI_API}?{urllib.parse.urlencode(params)}"
        req = urllib.request.Request(url, headers={'User-Agent': USER_AGENT})

        with urllib.request.urlopen(req, timeout=10) as response:
            data = json.loads(response.read().decode())

            if 'parse' not in data or 'wikitext' not in data['parse']:
                return None

            wikitext = data['parse']['wikitext']
            bonuses = extract_bonuses_from_wikitext(wikitext)
            return bonuses if bonuses else None

    except Exception as e:
        return None

def extract_bonuses_from_wikitext(wikitext: str) -> Optional[Dict[str, float]]:
    """Extract equipment bonuses from wiki template text."""
    bonuses = {}

    bonus_patterns = {
        'astab': r'\|\s*astab\s*=\s*([+-]?\d+)',
        'aslash': r'\|\s*aslash\s*=\s*([+-]?\d+)',
        'acrush': r'\|\s*acrush\s*=\s*([+-]?\d+)',
        'amagic': r'\|\s*amagic\s*=\s*([+-]?\d+)',
        'arange': r'\|\s*arange\s*=\s*([+-]?\d+)',
        'dstab': r'\|\s*dstab\s*=\s*([+-]?\d+)',
        'dslash': r'\|\s*dslash\s*=\s*([+-]?\d+)',
        'dcrush': r'\|\s*dcrush\s*=\s*([+-]?\d+)',
        'dmagic': r'\|\s*dmagic\s*=\s*([+-]?\d+)',
        'drange': r'\|\s*drange\s*=\s*([+-]?\d+)',
        'str': r'\|\s*str\s*=\s*([+-]?\d+)',
        'rstr': r'\|\s*rstr\s*=\s*([+-]?\d+)',
        'prayer': r'\|\s*prayer\s*=\s*([+-]?\d+)',
    }

    for key, pattern in bonus_patterns.items():
        match = re.search(pattern, wikitext, re.IGNORECASE)
        if match:
            bonuses[key] = float(match.group(1))

    return bonuses if bonuses else None

def parse_items_txt(filepath: str) -> List[Dict]:
    """Parse the items.txt file into a list of item dictionaries."""
    items = []
    current_item = None

    with open(filepath, 'r', encoding='utf-8', errors='ignore') as f:
        for line in f:
            line = line.strip()

            if line.startswith('Item Id:'):
                if current_item:
                    items.append(current_item)
                current_item = {'lines': [], 'bonuses': {}}

            if current_item is not None:
                current_item['lines'].append(line)

                if line.startswith('Item Id:'):
                    current_item['id'] = int(line.split(':')[1].strip())
                elif line.startswith('Name:'):
                    current_item['name'] = line.split(':', 1)[1].strip()
                elif line.startswith('EquipmentType:'):
                    current_item['equipment_type'] = line.split(':')[1].strip()
                elif line.startswith('Bonus['):
                    match = re.match(r'Bonus\[(\d+)\]:\s*([+-]?\d+\.?\d*)', line)
                    if match:
                        index = int(match.group(1))
                        value = float(match.group(2))
                        current_item['bonuses'][index] = value

    if current_item:
        items.append(current_item)

    return items

def update_item_bonuses(item: Dict, osrs_bonuses: Dict[str, float]) -> Dict:
    """Update an item's bonuses based on OSRS data."""
    # Clear existing bonuses from lines
    item['lines'] = [line for line in item['lines'] if not line.startswith('Bonus[')]

    # Find where to insert bonuses (before 'finish')
    finish_idx = next((i for i, line in enumerate(item['lines']) if line == 'finish'), len(item['lines']) - 1)

    # Convert OSRS bonuses to Elvarg format
    new_bonuses = []
    for osrs_key, elvarg_idx in BONUS_INDICES.items():
        if osrs_key in osrs_bonuses:
            value = osrs_bonuses[osrs_key]
            if value != 0:
                new_bonuses.append((elvarg_idx, f"Bonus[{elvarg_idx}]: {int(value)}"))

    # Sort bonuses by index for consistency
    new_bonuses.sort(key=lambda x: x[0])

    # Insert bonuses before 'finish'
    for _, bonus_line in reversed(new_bonuses):
        item['lines'].insert(finish_idx, bonus_line)

    return item

def write_items_txt(items: List[Dict], filepath: str):
    """Write updated items back to items.txt."""
    with open(filepath, 'w', encoding='utf-8') as f:
        for item in items:
            for line in item['lines']:
                f.write(line + '\n')
            f.write('\n')

def main():
    items_file = r"Elvarg - Server\data\cache\definitions\items.txt"

    print("="*60)
    print("OSRS Item Stats Updater - Full Mode")
    print("="*60)
    print()

    print("Loading items.txt...")
    items = parse_items_txt(items_file)
    print(f"Found {len(items)} total items")

    # Filter to only equipment items
    equipment_items = [item for item in items if 'equipment_type' in item and item['equipment_type'] != 'NONE']
    print(f"Found {len(equipment_items)} equipment items")
    print()

    updated_count = 0
    failed_count = 0
    skipped_count = 0

    start_time = time.time()

    print("Starting update process...")
    print("This will take approximately {:.1f} minutes".format(len(equipment_items) / 60))
    print()

    for i, item in enumerate(equipment_items):
        if 'name' not in item or not item['name']:
            skipped_count += 1
            continue

        # Progress indicator every 50 items
        if (i + 1) % 50 == 0:
            elapsed = time.time() - start_time
            rate = (i + 1) / elapsed
            remaining = (len(equipment_items) - i - 1) / rate / 60
            print(f"\nProgress: {i+1}/{len(equipment_items)} ({updated_count} updated, {failed_count} failed)")
            print(f"Estimated time remaining: {remaining:.1f} minutes\n")

        print(f"[{i+1}/{len(equipment_items)}] {item['name'][:40]:40} (ID {item.get('id', '?'):5})...", end=' ')

        osrs_bonuses = fetch_osrs_item_data(item['name'])

        if osrs_bonuses:
            update_item_bonuses(item, osrs_bonuses)
            print(f"[OK]")
            updated_count += 1
        else:
            print(f"[SKIP]")
            failed_count += 1

        # Rate limiting
        time.sleep(1.0)

    print()
    print("="*60)
    print("Writing updated items to file...")
    write_items_txt(items, items_file)

    elapsed = time.time() - start_time
    print()
    print("="*60)
    print("Update Complete!")
    print("="*60)
    print(f"  Updated:  {updated_count}")
    print(f"  Failed:   {failed_count}")
    print(f"  Skipped:  {skipped_count}")
    print(f"  Total:    {len(equipment_items)}")
    print(f"  Time:     {elapsed/60:.1f} minutes")
    print("="*60)

if __name__ == "__main__":
    main()
