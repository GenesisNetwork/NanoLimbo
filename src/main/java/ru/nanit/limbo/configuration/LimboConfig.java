package ru.nanit.limbo.configuration;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import ru.nanit.limbo.server.data.*;
import ru.nanit.limbo.util.Colors;

import java.io.*;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class LimboConfig {

    private final Path root;

    private SocketAddress address;
    private int maxPlayers;
    private PingData pingData;

    private String dimensionType;
    private Position spawnPosition;
    private int gameMode;

    private boolean useJoinMessage;
    private boolean useBossBar;
    private String joinMessage;
    private BossBar bossBar;

    private InfoForwarding infoForwarding;
    private long readTimeout;
    private int debugLevel = 3;

    private boolean useEpoll;
    private int bossGroupSize;
    private int workerGroupSize;

    public LimboConfig(Path root){
        this.root = root;
    }

    public void load() throws Exception {
        ConfigurationOptions options = ConfigurationOptions.defaults().serializers(getSerializers());
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .source(this::getReader)
                .defaultOptions(options)
                .build();

        ConfigurationNode conf = loader.load();

        address = conf.node("bind").get(SocketAddress.class);
        maxPlayers = conf.node("maxPlayers").getInt();
        pingData = conf.node("ping").get(PingData.class);
        dimensionType = conf.node("dimension").getString();
        spawnPosition = conf.node("spawnPosition").get(Position.class);
        gameMode = conf.node("gameMode").getInt();
        useJoinMessage = conf.node("joinMessage", "enable").getBoolean();
        useBossBar = conf.node("bossBar", "enable").getBoolean();

        if (useJoinMessage)
            joinMessage = Colors.of(conf.node("joinMessage", "text").getString(""));

        if (useBossBar)
            bossBar = conf.node("bossBar").get(BossBar.class);

        infoForwarding = conf.node("infoForwarding").get(InfoForwarding.class);
        readTimeout = conf.node("readTimeout").getLong();
        debugLevel = conf.node("debugLevel").getInt();

        useEpoll = conf.node("netty", "useEpoll").getBoolean(true);
        bossGroupSize = conf.node("netty", "threads", "bossGroup").getInt(1);
        workerGroupSize = conf.node("netty", "threads", "workerGroup").getInt(4);
    }

    private BufferedReader getReader() throws IOException {
        String name = "settings.yml";
        Path filePath = Paths.get(root.toString(), name);

        if (!Files.exists(filePath)) {
            InputStream stream = getClass().getResourceAsStream( "/" + name);

            if (stream == null)
                throw new FileNotFoundException("Cannot find settings resource file");

            Files.copy(stream, filePath);
        }

        return Files.newBufferedReader(filePath);
    }

    private TypeSerializerCollection getSerializers() {
        return TypeSerializerCollection.builder()
                .register(SocketAddress.class, new SocketAddressSerializer())
                .register(InfoForwarding.class, new InfoForwarding.Serializer())
                .register(PingData.class, new PingData.Serializer())
                .register(BossBar.class, new BossBar.Serializer())
                .register(Position.class, new Position.Serializer())
                .build();
    }

    public SocketAddress getAddress() {
        return address;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public PingData getPingData() {
        return pingData;
    }

    public String getDimensionType() {
        return dimensionType;
    }

    public Position getSpawnPosition() {
        return spawnPosition;
    }

    public int getGameMode() {
        return gameMode;
    }

    public InfoForwarding getInfoForwarding() {
        return infoForwarding;
    }

    public long getReadTimeout() {
        return readTimeout;
    }

    public int getDebugLevel() {
        return debugLevel;
    }

    public boolean isUseJoinMessage() {
        return useJoinMessage;
    }

    public boolean isUseBossBar() {
        return useBossBar;
    }

    public String getJoinMessage() {
        return joinMessage;
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    public boolean isUseEpoll() {
        return useEpoll;
    }

    public int getBossGroupSize() {
        return bossGroupSize;
    }

    public int getWorkerGroupSize() {
        return workerGroupSize;
    }
}