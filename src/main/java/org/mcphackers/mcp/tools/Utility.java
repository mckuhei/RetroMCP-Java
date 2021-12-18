package org.mcphackers.mcp.tools;

//import com.sun.nio.zipfs.ZipFileSystem;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;

public class Utility {

    public static int runCommand(String cmd) throws IOException, IllegalArgumentException {
        Process proc = Runtime.getRuntime().exec(cmd);
        while (proc.isAlive()) ;
        return proc.exitValue();
    }

    public static void unzip(final InputStream input, final Path destDirectory) throws IOException {
        Path zipPath = null;
        try {
            zipPath = Files.createTempFile("unzipStream", ".zip");
            Files.copy(input, zipPath, StandardCopyOption.REPLACE_EXISTING);
            unzip(zipPath, destDirectory);
        } finally {
            input.close();
            if (zipPath != null) {
                Files.deleteIfExists(zipPath);
            }
        }
    }

    public static void unzip(final Path zipFile, final Path destDir) throws IOException {
        if (Files.notExists(destDir)) {
            Files.createDirectories(destDir);
        }

//        try (ZipFileSystem zipFileSystem = (ZipFileSystem) FileSystems.newFileSystem(zipFile, null)) {
//            final Path root = zipFileSystem.getRootDirectories().iterator().next();
//
//            Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
//                @Override
//                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//                    final Path destFile = Paths.get(destDir.toString(), file.toString());
//                    try {
//                        Files.copy(file, destFile, StandardCopyOption.REPLACE_EXISTING);
//                    } catch (DirectoryNotEmptyException ignore) {
//                    }
//                    return FileVisitResult.CONTINUE;
//                }
//
//                @Override
//                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
//                    final Path dirToCreate = Paths.get(destDir.toString(), dir.toString());
//                    if (Files.notExists(dirToCreate)) {
//                        Files.createDirectory(dirToCreate);
//                    }
//                    return FileVisitResult.CONTINUE;
//                }
//            });
//        }
    }

    public static void downloadFile(URL url, String fileName) {
        try {
            ReadableByteChannel channel = Channels.newChannel(url.openStream());
            try (FileOutputStream stream = new FileOutputStream(fileName)) {
                FileChannel fileChannel = stream.getChannel();
                fileChannel.transferFrom(channel, 0, Long.MAX_VALUE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String getOperatingSystem() {
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().startsWith("windows")) {
            return "windows";
        } else {
            // TODO: Make this work for other OSes
            return "null";
        }
    }

    public static JSONObject parseJSONFile(Path path) throws JSONException, IOException {
        String content = new String(Files.readAllBytes(path));
        return new JSONObject(content);
    }

    public static void deleteDirectoryStream(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach((file) -> {
                boolean deleted = file.delete();
                if (!deleted) {
                    System.err.println("Failed to delete " + file.getAbsolutePath());
                }
            });
        }
    }

    public static void copyDirectory(Path sourceFolder, Path targetFolder) throws IOException {
        Files.walk(sourceFolder).forEach(source -> {
            Path destination = Paths.get(targetFolder.toString(), source.toString().substring(sourceFolder.toString().length()));
            try {
                Files.copy(source, destination);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
