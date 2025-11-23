# Caminho onde estão todas as APIs
$apisPath = "C:\APIs"

# Pega todas as pastas
$apiFolders = Get-ChildItem -Path $apisPath | Where-Object { $_.PSIsContainer }

foreach ($folder in $apiFolders) {
    $apiPath = Join-Path $apisPath $folder.Name
    Write-Host "------------------------------"
    Write-Host "Atualizando API: $($folder.Name)"
    Write-Host "Caminho: $apiPath"

    # Verifica se existe docker-compose.yml
    if (Test-Path "$apiPath\docker-compose.yml") {
        # Para e remove o container antigo
        docker-compose -f "$apiPath\docker-compose.yml" down

        # Sobe o container com rebuild
        docker-compose -f "$apiPath\docker-compose.yml" up -d --build

        Write-Host "$($folder.Name) iniciado com sucesso!"
    } else {
        Write-Host "docker-compose.yml não encontrado em $apiPath. Pulando esta API..."
    }
}
Write-Host "------------------------------"
Write-Host "Todas as APIs foram processadas."
