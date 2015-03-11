package org.fao.geonet.services.metadata.format.cache;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;

import org.apache.commons.io.FileUtils;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.lib.Lib;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PreDestroy;

import jeeves.utils.IO;
import jeeves.utils.Log;
import static org.fao.geonet.constants.Params.Access.PRIVATE;
import static org.fao.geonet.constants.Params.Access.PUBLIC;

/**
 * A {@link org.fao.geonet.services.metadata.format.cache.PersistentStore} that saves the files to disk.
 *
 * @author Jesse on 3/5/2015.
 */
public class FilesystemStore implements PersistentStore {
    private static final String BASE_CACHE_DIR = "formatter-cache";
    private static final String INFO_TABLE = "info";
    private static final String KEY = "keyhash";
    private static final String CHANGE_DATE = "changedate";
    private static final String PUBLISHED = "published";
    private static final String PATH = "path";
    private static final String STATS_TABLE = "stats";
    private static final String NAME = "name";
    private static final String CURRENT_SIZE = "currentsize";
    private static final String VALUE = "value";
    public static final String WITHHELD_MD_DIRNAME = "withheld_md";
    public static final String FULL_MD_NAME = "full_md";

    private static final String QUERY_GET_INFO = "SELECT * FROM " + INFO_TABLE + " WHERE " + KEY + "=?";
    private static final String QUERY_GET_INFO_FOR_RESIZE = "SELECT " +KEY + "," + PATH + " FROM " + INFO_TABLE + " ORDER BY " + CHANGE_DATE + " ASC";
    private static final String QUERY_PUT = "MERGE INTO " + INFO_TABLE + " (" + KEY + "," + CHANGE_DATE + "," + PUBLISHED + "," + PATH + ") VALUES (?,?,?, ?)";
    private static final String QUERY_REMOVE = "DELETE FROM " + INFO_TABLE + " WHERE " + KEY + "=?";
    public static final String QUERY_SETCURRENT_SIZE = "MERGE INTO "+STATS_TABLE + " (" + NAME + ", " + VALUE + ") VALUES ('" + CURRENT_SIZE + "', ?)";
    public static final String QUERY_GETCURRENT_SIZE = "SELECT "+VALUE+" FROM " + STATS_TABLE + " WHERE "+NAME+" = '"+CURRENT_SIZE+"'";

    @Autowired
    private GeonetworkDataDirectory geonetworkDataDir;
    @VisibleForTesting
    Connection metadataDb;
    private boolean testing = false;
    private volatile long maxSizeB = 10000;
    private volatile long currentSize = 0;
    private volatile boolean initialized = false;

    private synchronized void init() throws SQLException {
        if (!initialized) {
            // using a h2 database and not normal geonetwork DB to ensure that the accesses are always on localhost and therefore
            // hopefully quick.
            try {
                Class.forName("org.h2.Driver");
            } catch (ClassNotFoundException e) {
                throw new Error(e);
            }

            String[] initSql = {
                    "CREATE SCHEMA IF NOT EXISTS " + INFO_TABLE,
                    "CREATE TABLE IF NOT EXISTS " + INFO_TABLE + "(" + KEY + " INT PRIMARY KEY, " + CHANGE_DATE + " BIGINT NOT NULL, " +
                    PUBLISHED + " BOOL NOT NULL, " + PATH + " CLOB  NOT NULL)",
                    "CREATE TABLE IF NOT EXISTS " + STATS_TABLE + " (" + NAME + " VARCHAR(64) PRIMARY KEY, " + VALUE + " VARCHAR(32) NOT NULL)"

            };
            String init = ";INIT=" + Joiner.on("\\;").join(initSql) + ";DB_CLOSE_DELAY=-1";
            String dbPath = testing ? "mem:" + UUID.randomUUID() : new File(getBaseCacheDir(), "info-store").toString();
            metadataDb = DriverManager.getConnection("jdbc:h2:" + dbPath + init, "fsStore", "");

            Statement statement = null;
            ResultSet rs = null;
            try {
                statement = metadataDb.createStatement();
                rs = statement.executeQuery(QUERY_GETCURRENT_SIZE);
            } finally {
                if (rs != null)
                    rs.close();
                if (statement != null)
                    statement.close();
            }
            initialized = true;
        }

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    close();
                } catch (ClassNotFoundException e) {
                    Log.error(Geonet.FORMATTER, "Error shutting down FilesystemStore Database", e);
                } catch (SQLException e) {
                    Log.error(Geonet.FORMATTER, "Error shutting down FilesystemStore Database", e);                    
                }
            }
        }));
    }

    @PreDestroy
    synchronized void close() throws ClassNotFoundException, SQLException {
        if (metadataDb != null) {
            metadataDb.close();
        }
    }

    @Override
    public synchronized StoreInfoAndData get(@Nonnull Key key) throws IOException, SQLException {
        init();
        StoreInfo info = getInfo(key);
        if (info == null) {
            return null;
        }

        byte[] data = FileUtils.readFileToByteArray(getPrivatePath(key));
        return new StoreInfoAndData(info, data);
    }

    @Override
    public synchronized StoreInfo getInfo(@Nonnull Key key) throws SQLException {
        init();
        PreparedStatement statement = null;
        try {
            statement = this.metadataDb.prepareStatement(QUERY_GET_INFO);
            statement.setInt(1, key.hashCode());
            ResultSet resultSet = null;
            try {
                resultSet = statement.executeQuery();
            if (resultSet.next()) {
                long date = resultSet.getLong(CHANGE_DATE);
                boolean isPublished = resultSet.getBoolean(PUBLISHED);
                return new StoreInfo(date, isPublished);
            } else {
                return null;
            }
            } finally {
                if (resultSet != null)
                    resultSet.close();
            }
        } finally {
            if (statement != null)
                statement.close();
        }
    }

    @Override
    public synchronized void put(@Nonnull Key key, @Nonnull StoreInfoAndData data) throws IOException, SQLException {
        init();
        resizeIfRequired(key, data);
        final File privatePath = getPrivatePath(key);

        if (privatePath.exists()) {
            currentSize -= privatePath.length();
        }
        FileUtils.forceMkdir(privatePath.getParentFile());
        FileUtils.writeByteArrayToFile(privatePath, data.data);

        currentSize += data.data.length;

        updateDbCurrentSize();

        File publicPath = getPublicPath(key);
        FileUtils.deleteQuietly(publicPath);
        // only publish if withheld (hidden) elements are hidden.
        if (data.isPublished() && key.hideWithheld) {
            FileUtils.forceMkdir(publicPath.getParentFile());
          
            // Develop version of GeoNetwork makes use of java7, so with java.nio.files
            // API available. We have no other chance that making a copy of the
            // file, considering the need of a backward compat. on java6 :(
            FileUtils.copyFile(privatePath, publicPath);          
        }
        PreparedStatement statement = null;
        try {
            statement = this.metadataDb.prepareStatement(QUERY_PUT);
            statement.setInt(1, key.hashCode());
            statement.setLong(2, data.getChangeDate());
            statement.setBoolean(3, data.isPublished());
            statement.setString(4, privatePath.toURI().toString());
            statement.execute();
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    private void updateDbCurrentSize() throws SQLException {
        PreparedStatement statement = null;
        
        try {
            statement = this.metadataDb.prepareStatement(QUERY_SETCURRENT_SIZE);
            statement.setString(1, String.valueOf(currentSize));
            statement.execute();
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    private void resizeIfRequired(Key key, StoreInfoAndData data) throws IOException, SQLException {
        if (this.currentSize + data.data.length > this.maxSizeB) {
            final File privatePath = getPrivatePath(key);
            if (privatePath.exists()) {
                long fileSize = privatePath.length();
                if (currentSize - fileSize + data.data.length > this.maxSizeB) {
                    resize();
                }
            } else {
                resize();
            }
        }
    }

    private void resize() throws SQLException, IOException {
        int targetSize = (int) (maxSizeB / 2);
        Log.warning(Geonet.FORMATTER, "Resizing Formatter cache.  Required to reduce size by " + targetSize);
        long startTime = System.currentTimeMillis();
        Statement statement = null;
        ResultSet resultSet = null;
        
        try {
            statement = metadataDb.createStatement();
            resultSet = statement.executeQuery(QUERY_GET_INFO_FOR_RESIZE);

            while (currentSize > targetSize && resultSet.next()) {
                File path = IO.toPath(new URI(resultSet.getString(PATH)));
                doRemove(path, resultSet.getInt(KEY), false);
            }
        } catch (URISyntaxException e) {
            throw new Error(e);
        } finally {
            if (resultSet != null)
                resultSet.close();
            if (statement != null)
                statement.close();
        }
        Log.warning(Geonet.FORMATTER, "Resize took " + (System.currentTimeMillis() - startTime) + "ms to complete");
    }

    @Nullable
    @Override
    public byte[] getPublished(@Nonnull Key key) throws IOException {
        try {
            init();
        } catch (SQLException e) {
            throw new Error(e);
        }
        final File publicPath = getPublicPath(key);
        if (publicPath.exists()) {
            return FileUtils.readFileToByteArray(publicPath);
        } else {
            return null;
        }
    }

    @Override
    public synchronized void remove(@Nonnull Key key) throws IOException, SQLException {
        init();
        final File path = getPrivatePath(key);
        final int keyHashCode = key.hashCode();
        doRemove(path, keyHashCode, true);
    }

    @Override
    public void setPublished(int metadataId, final boolean published) throws IOException {

        final File metadataDir = Lib.resource.getMetadataDir(new File(getBaseCacheDir(), PRIVATE), String.valueOf(metadataId));
        if (metadataDir.exists()) {
            
            // TODO: not sure on how to port it ...
            // skip full-md ?
            Files.walkFileTree(metadataDir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (dir.getFileName().toString().equals(FULL_MD_NAME)) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    return super.preVisitDirectory(dir, attrs);
                }

                @Override
                public FileVisitResult visitFile(Path privatePath, BasicFileAttributes attrs) throws IOException {
                    final Path publicPath = toPublicPath(privatePath);
                    if (published) {
                        if (!Files.exists(publicPath)) {
                            if (!Files.exists(publicPath.getParent())) {
                                Files.createDirectories(publicPath.getParent());
                            }
                            Files.createLink(publicPath, privatePath);
                        }
                    } else {
                        Files.deleteIfExists(publicPath);
                    }
                    return super.visitFile(privatePath, attrs);
                }
            });
        }
    }

    private void doRemove(File privatePath, int keyHashCode, boolean updateDbCurrentSize) throws IOException, SQLException {
        try {
            if (privatePath.exists()) {
                currentSize -= privatePath.length();
                FileUtils.deleteQuietly(privatePath);
            }
        } finally {
            try {
                final File publicPath = toPublicPath(privatePath);
                FileUtils.deleteQuietly(publicPath);
            } finally {
                try (PreparedStatement statement = metadataDb.prepareStatement(QUERY_REMOVE)) {
                    statement.setInt(1, keyHashCode);
                    statement.execute();
                } finally {
                    if (updateDbCurrentSize) {
                        updateDbCurrentSize();
                    }
                }
            }
        }
    }

    private File toPublicPath(File privatePath) {
        // TODO Implement relativize in Java6 ??? 
        File relativePrivate = getBaseCacheDir().resolve(PRIVATE).relativize(privatePath);
        return new File(new File(getBaseCacheDir(),PUBLIC).toString(),relativePrivate.toString());
    }

    public void setGeonetworkDataDir(GeonetworkDataDirectory geonetworkDataDir) {
        this.geonetworkDataDir = geonetworkDataDir;
    }

    public File getPrivatePath(Key key) {
        return getCacheFile(key, false);
    }

    public File getPublicPath(Key key) {
        return getCacheFile(key, true);
    }

    private File getCacheFile(Key key, boolean isPublicCache) {
        final String accessDir = isPublicCache ? PUBLIC : PRIVATE;
        final String sMdId = String.valueOf(key.mdId);
        // ???
        final File metadataDir = Lib.resource.getMetadataDir(new File(getBaseCacheDir(), accessDir), sMdId);
        String hidden = key.hideWithheld ? WITHHELD_MD_DIRNAME : FULL_MD_NAME;

        return new File(String.format("%s%s%s%s%s%s%s%s%s%s",
                metadataDir.toString(), File.separator, key.formatterId,
                File.separator, key.lang, File.separator, hidden,
                File.separator, key.hashCode(), key.formatType.name()));
    }

    private File getBaseCacheDir() {
        return new File(geonetworkDataDir.getHtmlCacheDir(), BASE_CACHE_DIR);
    }

    public void setMaxSizeKb(long maxSize) {
        this.maxSizeB = maxSize * 1024;
    }

    public void setMaxSizeMb(int maxSize) {
        setMaxSizeKb(maxSize * 1024);
    }

    public void setMaxSizeGb(int maxSize) {
        setMaxSizeMb(maxSize * 1024);
    }

    public void setTesting(boolean testing) {
        this.testing = testing;
    }
}
