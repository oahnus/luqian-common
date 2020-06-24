package com.github.oahnus.luqiancommon.util;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.inputstream.ZipInputStream;
import net.lingala.zip4j.model.LocalFileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * Created by oahnus on 2020-06-23
 */
public class ZipUtils {

    public static void zip(String zipPath, String password, File... files) throws IOException {
        File file = new File(zipPath);
        zip(file, password, files);
    }

    public static void zip(File destFile, String password, File... files) throws ZipException {
        ZipFile zipFile = new ZipFile(destFile, password.toCharArray());
        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setEncryptFiles(true);
        zipParameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);
        for (File f : files) {
            if (f.isFile()) {
                zipFile.addFile(f, zipParameters);
            } else {
                zipFile.addFolder(f, zipParameters);
            }
        }
    }

    public static void zip(OutputStream out, String password, File... files) throws IOException {
        File file = File.createTempFile("", "temp.zip");
        zip(file, password, files);
        IOUtils.copy(new FileInputStream(file), out);
        out.flush();
    }

    public static void unzip(File srcFile, String password, String extractPath) throws IOException {
        ZipFile zipFile = new ZipFile(srcFile, password.toCharArray());
        zipFile.extractAll(extractPath);
    }
    public static void unzip(String srcPath, String password, String extractPath) throws IOException {
        ZipFile zipFile = new ZipFile(new File(srcPath), password.toCharArray());
        zipFile.extractAll(extractPath);
    }

    public static void unzip(InputStream in, String password, String extractPath) throws IOException {
        LocalFileHeader localFileHeader;
        int readLen;
        byte[] readBuffer = new byte[4096];
        try (ZipInputStream zis = new ZipInputStream(in, password.toCharArray())) {
            while ((localFileHeader = zis.getNextEntry()) != null) {
                File extractedFile = new File(localFileHeader.getFileName());
                try (OutputStream outputStream = new FileOutputStream(extractedFile)) {
                    while ((readLen = zis.read(readBuffer)) != -1) {
                        outputStream.write(readBuffer, 0, readLen);
                    }
                }
            }
        }
    }
}
