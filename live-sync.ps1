param(
    [string]$DeviceId = ""
)

$ErrorActionPreference = "Stop"
$adb = "C:\Users\Acer\AppData\Local\Android\Sdk\platform-tools\adb.exe"

Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "   UnitedPay - Instant Silent Direct Deploy" -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host ""

# 1. Resolve Target Device
$target = $DeviceId
if (-not $target) {
    $deviceLines = & $adb devices | Where-Object { $_ -match "\tdevice$" }
    if (-not $deviceLines) {
        Write-Host "[ERROR] No Android device connected! Connect your phone via USB with USB Debugging enabled, or via Wireless Debugging." -ForegroundColor Red
        exit 1
    }
    $devices = $deviceLines | ForEach-Object { ($_ -split "\t")[0].Trim() }
    
    # Prioritize physical hardware phone over virtual emulators
    $target = $devices | Where-Object { $_ -notmatch "emulator" } | Select-Object -First 1
    if (-not $target) {
        $target = $devices | Select-Object -First 1
    }
}

$model = (& $adb -s $target shell getprop ro.product.model).Trim()
Write-Host "[1/3] Target device detected: $target ($model)" -ForegroundColor Green

# 2. Check APK
$apkPath = Join-Path $PSScriptRoot "app\build\outputs\apk\debug\app-debug.apk"
if (-not (Test-Path $apkPath)) {
    Write-Host "[2/3] Building debug APK with Gradle daemon..." -ForegroundColor Yellow
    & (Join-Path $PSScriptRoot "gradlew.bat") :app:assembleDebug
}

# 3. Streamed in-place install (Never uninstall, preserves user state)
Write-Host "[2/3] Streaming in-place update directly to $model (no uninstall)..." -ForegroundColor Yellow
$installOutput = & $adb -s $target install -r $apkPath
Write-Host $installOutput -ForegroundColor Green

# 4. Bring MainActivity to foreground immediately
Write-Host "[3/3] Bringing updated UnitedPay screen to foreground..." -ForegroundColor Yellow
& $adb -s $target shell am start -n com.unitedpay.upi.debug/com.unitedpay.app.MainActivity | Out-Null

Write-Host ""
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "   Done! Live update is active on your $model screen!" -ForegroundColor Green
Write-Host "========================================================" -ForegroundColor Cyan
