package com.js;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author @Jasu
 * @date 2018-09-14 14:41
 */
public class Test {
    private final static String repo = "D:/1111/场租合同";
    public static void main(String[] args) throws IOException {
        Path path = Paths.get(repo);

        for (int i = 0; i < 100; i++) {
            Files.createDirectories(path.resolve(String.valueOf(i)));
            Files.createDirectory(path.resolve(String.valueOf(i)).resolve("合同"));
        }
    }
}
