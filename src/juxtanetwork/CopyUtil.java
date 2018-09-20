package juxtanetwork;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.sql.Timestamp;
import java.util.EnumSet;
import java.util.Objects;
/**
 *
 * @author Java Project Team
 */
public class CopyUtil {
    public static void copyDirectoryContent (File sourceFolder,
                        File destinationFolder) throws IOException {
        if (sourceFolder.isDirectory()) {

            if (destinationFolder.exists() && destinationFolder.isFile()) {
                throw new IllegalArgumentException(
                                    "Destination exists but is not a folder: "
                                                        + destinationFolder
                                                        .getAbsolutePath());
            }

            if (!destinationFolder.exists()) {
                Files.createDirectory(destinationFolder.toPath());
            }

            for (File file : sourceFolder.listFiles()) {
                if (file.isDirectory()) {
                    copyDirectory(file, destinationFolder);
                } else {
                    copyFile(file, destinationFolder);
                }
            }
        }
    }

    public static void copyDirectory (File fromFile, File toParentFile)
                        throws IOException {
        Path from = fromFile.toPath();
        String nodeFolderName = toParentFile.getAbsolutePath() + File.separatorChar + fromFile.getName();

        File nodeFolder = new File(nodeFolderName);
        if (!nodeFolder.exists()) {
            nodeFolder.mkdir();
        }
        
        File timeStampFolder = new File(nodeFolderName + File.separatorChar
                + String.valueOf(new Timestamp(System.currentTimeMillis())).replace(':', '_').replace(' ', '_')
                        .substring(0, 19)); // timestamp path in Node folder. Format: 2018-09-13_22_04_59
        timeStampFolder.mkdir();
        
        Path to =  Paths.get(timeStampFolder.getAbsolutePath());
                
        Files.walkFileTree(from, EnumSet.of(FileVisitOption.FOLLOW_LINKS),
                           Integer.MAX_VALUE, new MyCopyDirVisitor(from, to));
    }

    public static void copyFile (File toCopy, File mainDestination)
                        throws IOException {
        if (!mainDestination.exists()) {
            mainDestination.mkdirs();
        }
        Path to = Paths.get(mainDestination.getAbsolutePath() +
                                                File.separatorChar + toCopy.getName());

        Files.copy(toCopy.toPath(), to, StandardCopyOption.REPLACE_EXISTING);
    }
}
