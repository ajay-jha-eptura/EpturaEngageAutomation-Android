import re

with open('azure-pipelines.yml', 'r', encoding='utf-8') as f:
    content = f.read()

# Remove the Test Configuration variables section
content = re.sub(r'  # Test Configuration\n  DEVICE_NAME:.*\n  USE_EXTERNAL_APPIUM:.*\n  APPIUM_SERVER_URL:.*\n', '', content)

# Update Maven options to match working pipeline
content = re.sub(
    r"options: '-DsuiteXmlFile=testng.xml -DdeviceName=\$\(DEVICE_NAME\) \$\(MAVEN_OPTS\)'",
    "options: '-Dtestng.xml=testng.xml -DdeviceName=emulator-5554 $(MAVEN_OPTS)'",
    content
)

# Remove USE_EXTERNAL_APPIUM and APPIUM_SERVER_URL from env section
content = re.sub(r'              USE_EXTERNAL_APPIUM:.*\n', '', content)
content = re.sub(r'              APPIUM_SERVER_URL:.*\n', '', content)

with open('azure-pipelines.yml', 'w', encoding='utf-8') as f:
    f.write(content)

print('File updated successfully')
