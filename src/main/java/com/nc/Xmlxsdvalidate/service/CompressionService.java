package com.nc.Xmlxsdvalidate.service;


import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;

@Service
public class CompressionService {

    public void compressFile(String sourcePath, String targetPath) throws IOException {
        try (TarArchiveOutputStream taos = new TarArchiveOutputStream(
                new GzipCompressorOutputStream(new FileOutputStream(targetPath)))) {
            File sourceFile = new File(sourcePath);
            TarArchiveEntry entry = new TarArchiveEntry(sourceFile, sourceFile.getName());
            taos.putArchiveEntry(entry);
            Files.copy(sourceFile.toPath(), taos);
            taos.closeArchiveEntry();
        }
    }

    public void decompressFile(String sourcePath, String targetDir) throws IOException {
        try (TarArchiveInputStream tais = new TarArchiveInputStream(
                new GzipCompressorInputStream(new FileInputStream(sourcePath)))) {
            TarArchiveEntry entry;
            while ((entry = tais.getNextTarEntry()) != null) {
                File outputFile = new File(targetDir, entry.getName());
                if (entry.isDirectory()) {
                    outputFile.mkdirs();
                } else {
                    outputFile.getParentFile().mkdirs();
                    try (OutputStream os = new FileOutputStream(outputFile)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = tais.read(buffer)) > 0) {
                            os.write(buffer, 0, len);
                        }
                    }
                }
            }
        }
    }
}
