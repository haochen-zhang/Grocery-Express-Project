param
(
    [String] $FileName
)

$compress = @{
    Path = "./src/"
    CompressionLevel = "Fastest"
    DestinationPath = "./$($FileName).zip"
}

Compress-Archive @compress -Force