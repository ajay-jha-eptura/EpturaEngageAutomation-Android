#!/usr/bin/env python3
"""Fix AndroidDriver casting issue in Utility.java"""

import os

script_dir = os.path.dirname(os.path.abspath(__file__))
project_dir = os.path.dirname(script_dir)
utility_path = os.path.join(project_dir, 'src', 'main', 'java', 'appium', 'webdriver', 'extensions', 'Utility.java')

print(f"Fixing: {utility_path}")

with open(utility_path, 'r', encoding='utf-8') as f:
    content = f.read()

# Fix: add cast to AndroidDriver
old_text = 'AndroidDriver driver = DriverManager.getDriver();'
new_text = 'AndroidDriver driver = (AndroidDriver) DriverManager.getDriver();'

count = content.count(old_text)
print(f"Found {count} occurrence(s) to fix")

content = content.replace(old_text, new_text)

with open(utility_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Fixed AndroidDriver casting in Utility.java")
