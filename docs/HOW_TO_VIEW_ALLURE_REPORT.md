# How to View Allure Report from Azure DevOps Pipeline

This guide explains how to view Allure test reports after your CI/CD pipeline execution completes.

---

## 📋 Available Artifacts After Pipeline Run

After your pipeline completes, you'll find these artifacts in Azure DevOps:

| Artifact Name | Description |
|---------------|-------------|
| **AllureResults** | Raw JSON test results (needs processing) |
| **AllureReport** | Generated HTML report (ready to view) |

---

## 🚀 Option 1: View Directly in Azure DevOps (Recommended)

### Step 1: Install Allure Azure DevOps Extension

1. Go to **Azure DevOps Marketplace**: https://marketplace.visualstudio.com/items?itemName=Micro-Focus.alm-octane-test-result-viewer
   
   Or search for **"Allure Report"** in the marketplace

2. Click **"Get it free"** and install it to your Azure DevOps organization

3. After installation, the Allure Report tab will appear automatically in your pipeline runs

### Step 2: View Report in Pipeline

1. Go to your **Pipeline** → **Runs**
2. Click on the completed pipeline run
3. Look for the **"Allure Report"** tab (appears after extension is installed)
4. Click to view the interactive report directly in Azure DevOps!

---

## 📥 Option 2: Download and View Locally

### Step 1: Download the Artifact

1. Go to **Azure DevOps** → **Pipelines** → **Your Pipeline**
2. Click on the completed **Pipeline Run**
3. Click on **"Published"** or **"Artifacts"** (usually shown as "X published")
4. Download **"AllureReport"** (NOT AllureResults)

### Step 2: View the Report

#### Method A: Using the Helper Script (Easiest)

```cmd
view_allure_report.bat "C:\Downloads\AllureReport"
```

#### Method B: Using Allure CLI

```cmd
allure open "C:\Downloads\AllureReport"
```

#### Method C: Using Python HTTP Server (No Allure CLI needed)

```cmd
cd "C:\Downloads\AllureReport"
python -m http.server 8080
```
Then open http://localhost:8080 in your browser.

#### Method D: Using VS Code Live Server

1. Open the `AllureReport` folder in VS Code
2. Install "Live Server" extension
3. Right-click on `index.html` → "Open with Live Server"

---

## ⚠️ Why Does `index.html` Show a Blank Screen?

When you double-click `index.html` directly, it shows a blank screen because:

- Allure reports use JavaScript to load data dynamically
- Browsers block local JavaScript file loading (CORS/security policy)
- The report needs a web server to work properly

**Solution:** Always use one of the methods above (Allure CLI, Python server, or Live Server).

---

## 🔧 Installing Allure CLI on Windows

If you don't have Allure CLI installed:

### Using Scoop (Recommended)
```powershell
# Install Scoop first (if not installed)
Set-ExecutionPolicy RemoteSigned -Scope CurrentUser
irm get.scoop.sh | iex

# Install Allure
scoop install allure
```

### Using Chocolatey
```powershell
choco install allure
```

### Manual Installation
1. Download from: https://github.com/allure-framework/allure2/releases
2. Extract to a folder (e.g., `C:\allure`)
3. Add `C:\allure\bin` to your PATH environment variable

### Verify Installation
```cmd
allure --version
```

---

## 📊 Understanding the Allure Report

Once opened, the Allure Report shows:

| Section | Description |
|---------|-------------|
| **Overview** | Summary dashboard with pass/fail statistics |
| **Suites** | Test results organized by test suites |
| **Graphs** | Visual charts showing test trends |
| **Timeline** | Test execution timeline |
| **Behaviors** | Tests grouped by features/stories |
| **Categories** | Tests grouped by failure categories |
| **Packages** | Tests organized by Java packages |

### Key Files in AllureReport Folder

```
AllureReport/
├── index.html      ← Main entry point (open via server)
├── app.js          ← JavaScript application
├── styles.css      ← Styling
├── data/           ← Test result data (JSON)
├── widgets/        ← Dashboard widget data
├── history/        ← Historical trend data
└── export/         ← Export configurations
```

---

## 🔄 Quick Reference Commands

```cmd
# View local report (after running tests locally)
view_allure_report.bat

# View downloaded artifact
view_allure_report.bat "C:\path\to\AllureReport"

# Generate report from results
allure generate target\allure-results -o target\allure-report --clean

# Serve and open report
allure serve target\allure-results

# Open existing report
allure open target\allure-report
```

---

## 📝 Troubleshooting

### Problem: "allure is not recognized as a command"
**Solution:** Install Allure CLI using the instructions above, or use Python HTTP server method.

### Problem: Report shows blank or loading forever
**Solution:** You're opening `index.html` directly. Use `allure open` or a web server instead.

### Problem: Can't find artifacts in pipeline
**Solution:** 
1. Make sure the pipeline completed (even with test failures)
2. Check the "Published" section in the pipeline run summary
3. Artifacts are available even if tests fail (we use `condition: always()`)

### Problem: AllureReport artifact is missing
**Solution:** The `AllureResults` folder must exist for the report to generate. Check if:
1. Tests actually ran (check Maven test step)
2. Allure dependencies are in `pom.xml`
3. Check pipeline logs for "Generate Allure HTML Report" step errors
