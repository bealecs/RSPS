#!/usr/bin/env python3
"""Test script to verify OSRS Wiki API fetching."""

import re
import json
import urllib.request
import urllib.parse

WIKI_API = "https://oldschool.runescape.wiki/api.php"
USER_AGENT = "Elvarg RSPS Item Stats Updater/1.0"

def fetch_osrs_item_data(item_name: str):
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

            if 'parse' not in data:
                print(f"  ERROR: No parse data in response")
                return None

            if 'wikitext' not in data['parse']:
                print(f"  ERROR: No wikitext in parse data")
                return None

            wikitext = data['parse']['wikitext']
            print(f"\n  Wikitext preview (first 500 chars):")
            print(f"  {wikitext[:500]}")

            # Extract bonuses
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

            bonuses = {}
            for key, pattern in bonus_patterns.items():
                match = re.search(pattern, wikitext, re.IGNORECASE)
                if match:
                    bonuses[key] = int(match.group(1))

            return bonuses

    except Exception as e:
        print(f"  ERROR: {e}")
        import traceback
        traceback.print_exc()
        return None

# Test with known items
test_items = [
    "Abyssal whip",
    "Dragon scimitar",
    "Rune scimitar",
    "Bandos chestplate",
    "Dragon defender"
]

print("Testing OSRS Wiki API fetching...\n")

for item_name in test_items:
    print(f"Fetching: {item_name}")
    bonuses = fetch_osrs_item_data(item_name)

    if bonuses:
        print(f"  SUCCESS! Found bonuses:")
        for key, value in bonuses.items():
            print(f"    {key}: {value}")
    else:
        print(f"  FAILED - No bonuses found")

    print()
