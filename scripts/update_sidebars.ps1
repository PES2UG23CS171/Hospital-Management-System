$templateDir = Join-Path $PSScriptRoot "..\src\main\resources\templates"

# Get all HTML files
$files = Get-ChildItem -Path $templateDir -Filter "*.html" -Recurse

foreach ($file in $files) {
    $content = Get-Content $file.FullName -Raw
    $modified = $false

    # Skip if already has medical-records link (the medical-records templates)
    if ($file.DirectoryName -like "*medical-records*") {
        # These already have the medical-records link but are missing Admin section
        # Check if Admin section is missing
        if ($content -notmatch "Admin Panel") {
            # Add Admin section before </nav>
            $content = $content -replace '(<a[^>]*medical-records[^>]*>.*?Medical Records</a>\s*)', ('$1
        <div class="nav-section">Admin</div>
        <a th:href="@{/admin/dashboard}" class="nav-item"><span class="icon">&#x1F6E1;&#xFE0F;</span> Admin Panel</a>
        <a th:href="@{/admin/reports}" class="nav-item"><span class="icon">&#x1F4C8;</span> Reports</a>
')
            $modified = $true
        }
    }
    else {
        # These need the medical-records link added after Staff
        if ($content -notmatch "medical-records") {
            # Pattern: after Staff link, before Admin section div
            $content = $content -replace '(<a[^>]*href="@\{/staff\}"[^>]*>.*?Staff</a>)', ('$1
        <a th:href="@{/medical-records}" class="nav-item"><span class="icon">&#x1F4CB;</span> Medical Records</a>')
            $modified = $true
        }
    }

    if ($modified) {
        Set-Content -Path $file.FullName -Value $content -NoNewline
        Write-Host "Updated: $($file.FullName)"
    } else {
        Write-Host "Skipped (no change needed): $($file.FullName)"
    }
}

Write-Host "`nDone! All sidebars updated."
