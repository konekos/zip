package com.js;

import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author @Jasu
 * @date 2018-09-14 13:45
 */
public class App {
    private final static String repo = "D:/1111/场租合同";

    public static void main(String[] args) throws IOException {
        String repo = "";

        if (args.length != 1) {
            repo = "D:/1111/场租合同";
        } else {
            Path path = Paths.get(args[0]);
            if (Files.isDirectory(path) && Files.exists(path)) {
                repo = args[0];
            }
        }
        Files.walkFileTree(Paths.get(repo), new ZipVisitor());
        System.out.println("complete");
    }

    private static class ZipVisitor extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            if ("原合同".equals(dir.getFileName().toString())) {
                Stream<Path> list = Files.list(dir);
                if (list.count() > 0) {
                    if (Files.notExists(dir.getParent().resolve("原合同.rar"))) {
                        zip(dir.toString(), dir.getParent().resolve("原合同.rar").toString());
                    }
                }
            }
            return super.postVisitDirectory(dir, exc);
        }
    }


    public static void zip(String dir, String zippath) {
        List<String> paths = getFiles(dir);
        compressFilesZip(paths.toArray(new String[paths.size()]), zippath, dir);
    }

    public static List<String> getFiles(String dir) {
        List<String> lstFiles = null;
        if (lstFiles == null) {
            lstFiles = new ArrayList<String>();
        }
        File file = new File(dir);
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                lstFiles.add(f.getAbsolutePath());
                lstFiles.addAll(getFiles(f.getAbsolutePath()));
            } else {
                String str = f.getAbsolutePath();
                lstFiles.add(str);
            }
        }
        return lstFiles;
    }

    public static void compressFilesZip(String[] files, String zipFilePath, String dir) {
        if (files == null || files.length <= 0) {
            return;
        }
        ZipArchiveOutputStream zaos = null;
        try {
            File zipFile = new File(zipFilePath);
            zaos = new ZipArchiveOutputStream(zipFile);
            zaos.setUseZip64(Zip64Mode.AsNeeded);
            //将每个文件用ZipArchiveEntry封装
            //再用ZipArchiveOutputStream写到压缩文件中
            for (String strfile : files) {
                File file = new File(strfile);
                if (file != null) {
                    String name = getFilePathName(dir, strfile);
                    ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(file, name);
                    zaos.putArchiveEntry(zipArchiveEntry);
                    if (file.isDirectory()) {
                        continue;
                    }
                    InputStream is = null;
                    try {
                        is = new BufferedInputStream(new FileInputStream(file));
                        byte[] buffer = new byte[1024];
                        int len = -1;
                        while ((len = is.read(buffer)) != -1) {
                            //把缓冲区的字节写入到ZipArchiveEntry
                            zaos.write(buffer, 0, len);
                        }
                        zaos.closeArchiveEntry();
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    } finally {
                        if (is != null)
                            is.close();
                    }

                }
            }
            zaos.finish();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {
                if (zaos != null) {
                    zaos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

    }

    public static String getFilePathName(String dir, String path) {
        String p = path.replace(dir + File.separator, "");
        p = p.replace("\\", "/");
        return p;
    }
}
