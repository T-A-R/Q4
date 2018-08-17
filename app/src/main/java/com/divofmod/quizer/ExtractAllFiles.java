package com.divofmod.quizer;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;

class ExtractAllFiles {

    ExtractAllFiles(String zipPath, String outPath, String password) {

        try {
            // Initiate ZipFile object with the path/name of the zip file.
            // Инициализируем объект Zip файла, используя его путь и название.
            ZipFile zipFile = new ZipFile(zipPath);

            // Check to see if the zip file is password protected.
            // Проверяем, защищен ли архив с помощь пароля.
            if (zipFile.isEncrypted()) {
                // if yes, then set the password for the zip file.
                // если да, то указаваем его здесь.
                zipFile.setPassword(password);
            }

            // Extracts all files to the path specified.
            // Извлекаем все файлы по указанному пути.
            zipFile.extractAll(outPath);

            // Delete ZipFile.
            // Удаляем архив.
            new File(zipPath).delete();

        } catch (ZipException e) {
            e.printStackTrace();
        }
    }
}
