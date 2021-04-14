package com.elikill58.negativity.sponge.metrics;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

/**
 * bStats collects some data for plugin authors.
 * <p>
 * Check out https://bStats.org/ to learn more about bStats!
 * <p>
 * DO NOT modify any of this class. Access it from your own plugin ONLY.
 */
public class MetricsLite2 implements Metrics {
    /**
     * Internal class for storing information about old bStats instances.
     */
    private static class OutdatedInstance implements Metrics {
        private Object instance;
        private Method method;
        private PluginContainer plugin;

        private OutdatedInstance(Object instance, Method method, PluginContainer plugin) {
            this.instance = instance;
            this.method = method;
            this.plugin = plugin;
        }

        @Override
        public void cancel() {
            // Do nothing, handled once elsewhere
        }

        @Override
        public List<Metrics> getKnownMetricsInstances() {
            return new ArrayList<>();
        }

        @Override
        public JsonObject getPluginData() {
            try {
                return (JsonObject) method.invoke(instance);
            } catch (ClassCastException | IllegalAccessException | InvocationTargetException ignored) { }
            return null;
        }

        @Override
        public PluginContainer getPluginContainer() {
            return plugin;
        }

        @Override
        public int getRevision() {
            return 0;
        }

        @Override
        public void linkMetrics(Metrics metrics) {
            // Do nothing
        }
    }

    /**
     * A factory to create new Metrics classes.
     */
    public static class Factory {

        private final PluginContainer plugin;
        private final Logger logger;
        private final Path configDir;

        // The constructor is not meant to be called by the user.
        // The instance is created using Dependency Injection (https://docs.spongepowered.org/master/en/plugin/injection.html)
        @Inject
        private Factory(PluginContainer plugin, Logger logger, @ConfigDir(sharedRoot = true) Path configDir) {
            this.plugin = plugin;
            this.logger = logger;
            this.configDir = configDir;
        }

        /**
         * Creates a new MetricsLite2 class.
         *
         * @param pluginId The id of the plugin.
         *                 It can be found at <a href="https://bstats.org/what-is-my-plugin-id">What is my plugin id?</a>
         *                 <p>Not to be confused with Sponge's {@link PluginContainer#getId()} method!
         * @return A MetricsLite2 instance that can be used to register custom charts.
         * <p>The return value can be ignored, when you do not want to register custom charts.
         */
        public MetricsLite2 make(int pluginId) {
            return new MetricsLite2(plugin, logger, configDir, pluginId);
        }
    }

    // This ThreadFactory enforces the naming convention for our Threads
    private final ThreadFactory threadFactory = task -> new Thread(task, "bStats-Metrics");

    // Executor service for requests
    // We use an executor service to be independent of server TPS
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, threadFactory);

    // The version of bStats info being sent
    public static final int B_STATS_VERSION = 1;

    // The version of this bStats class
    public static final int B_STATS_CLASS_REVISION = 2;

    // The url to which the data is sent
    private static final String URL = "https://bStats.org/submitData/sponge";

    // The logger
    private Logger logger;

    // The plugin
    private final PluginContainer plugin;

    // The plugin id
    private final int pluginId;

    // The uuid of the server
    private String serverUUID;

    // Should failed requests be logged?
    private boolean logFailedRequests = false;

    // Should the sent data be logged?
    private static boolean logSentData;

    // Should the response text be logged?
    private static boolean logResponseStatusText;

    // A list with all known metrics class objects including this one
    private final List<Metrics> knownMetricsInstances = new CopyOnWriteArrayList<>();

    // The config path
    private Path configDir;

    // The list of instances from the bStats 1 instance's that started first
    private List<Object> oldInstances = new ArrayList<>();

    // The constructor is not meant to be called by the user, but by using the Factory
    private MetricsLite2(PluginContainer plugin, Logger logger, Path configDir, int pluginId) {
        this.plugin = plugin;
        this.logger = logger;
        this.configDir = configDir;
        this.pluginId = pluginId;

        Sponge.getEventManager().registerListeners(plugin, this);
    }

    @Listener
    public void startup(GamePreInitializationEvent event) {
        try {
            loadConfig();
        } catch (IOException e) {
            // Failed to load configuration
            logger.warn("Failed to load bStats config!", e);
            return;
        }

        if (Sponge.getServiceManager().isRegistered(Metrics.class)) {
            Metrics provider = Sponge.getServiceManager().provideUnchecked(Metrics.class);
            provider.linkMetrics(this);
        } else {
            Sponge.getServiceManager().setProvider(plugin.getInstance().get(), Metrics.class, this);
            this.linkMetrics(this);
            startSubmitting();
        }
    }

    @Override
    public void cancel() {
        scheduler.shutdown();
    }

    @Override
    public List<Metrics> getKnownMetricsInstances() {
        return knownMetricsInstances;
    }

    @Override
    public PluginContainer getPluginContainer() {
        return plugin;
    }

    @Override
    public int getRevision() {
        return B_STATS_CLASS_REVISION;
    }

    /**
     * Links a bStats 1 metrics class with this instance.
     *
     * @param metrics An object of the metrics class to link.
     */
    private void linkOldMetrics(Object metrics) {
        try {
            Field field = metrics.getClass().getDeclaredField("plugin");
            field.setAccessible(true);
            PluginContainer plugin = (PluginContainer) field.get(metrics);
            Method method = metrics.getClass().getMethod("getPluginData");
            linkMetrics(new OutdatedInstance(metrics, method, plugin));
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException e) {
            // Move on, this bStats is broken
        }
    }

    /**
     * Links an other metrics class with this class.
     * This method is called using Reflection.
     *
     * @param metrics An object of the metrics class to link.
     */
    @Override
    public void linkMetrics(Metrics metrics) {
        knownMetricsInstances.add(metrics);
    }

    @Override
    public JsonObject getPluginData() {
        JsonObject data = new JsonObject();

        String pluginName = plugin.getName();
        String pluginVersion = plugin.getVersion().orElse("unknown");

        data.addProperty("pluginName", pluginName);
        data.addProperty("id", pluginId);
        data.addProperty("pluginVersion", pluginVersion);
        data.addProperty("metricsRevision", B_STATS_CLASS_REVISION);

        JsonArray customCharts = new JsonArray();
        data.add("customCharts", customCharts);

        return data;
    }

    @SuppressWarnings("unchecked")
	private void startSubmitting() {
        // bStats 1 cleanup. Runs once.
        try {
            Path configPath = configDir.resolve("bStats");
            configPath.toFile().mkdirs();
            String className = readFile(new File(configPath.toFile(), "temp.txt"));
            if (className != null) {
                try {
                    // Let's check if a class with the given name exists.
                    Class<?> clazz = Class.forName(className);

                    // Time to eat it up!
                    Field instancesField = clazz.getDeclaredField("knownMetricsInstances");
                    instancesField.setAccessible(true);
                    oldInstances = (List<Object>) instancesField.get(null);
                    for (Object instance : oldInstances) {
                        linkOldMetrics(instance); // Om nom nom
                    }
                    oldInstances.clear(); // Look at me. I'm the bStats now.

                    // Cancel its timer task
                    // bStats for Sponge version 1 did not expose its timer task - gotta go find it!
                    Map<Thread, StackTraceElement[]> threadSet = Thread.getAllStackTraces();
                    for (Map.Entry<Thread, StackTraceElement[]> entry : threadSet.entrySet()) {
                        try {
                            if (entry.getKey().getName().startsWith("Timer")) {
                                Field timerThreadField = entry.getKey().getClass().getDeclaredField("queue");
                                timerThreadField.setAccessible(true);
                                Object taskQueue = timerThreadField.get(entry.getKey());

                                Field taskQueueField = taskQueue.getClass().getDeclaredField("queue");
                                taskQueueField.setAccessible(true);
                                Object[] tasks = (Object[]) taskQueueField.get(taskQueue);
                                for (Object task : tasks) {
                                    if (task == null) {
                                        continue;
                                    }
                                    if (task.getClass().getName().startsWith(clazz.getName())) {
                                        ((TimerTask) task).cancel();
                                    }
                                }
                            }
                        } catch (Exception ignored) { }
                    }
                } catch (ReflectiveOperationException ignored) { }
            }
        } catch (IOException ignored) { }

        final Runnable submitTask = () -> {
            // Catch any stragglers from inexplicable post-server-load plugin loading of outdated bStats
            for (Object instance : oldInstances) {
                linkOldMetrics(instance); // Om nom nom
            }
            oldInstances.clear(); // Look at me. I'm the bStats now.
            // The data collection (e.g. for custom graphs) is done sync
            // Don't be afraid! The connection to the bStats server is still async, only the stats collection is sync ;)
            Scheduler scheduler = Sponge.getScheduler();
            Task.Builder taskBuilder = scheduler.createTaskBuilder();
            taskBuilder.execute(this::submitData).submit(plugin);
        };

        // Many servers tend to restart at a fixed time at xx:00 which causes an uneven distribution of requests on the
        // bStats backend. To circumvent this problem, we introduce some randomness into the initial and second delay.
        // WARNING: You must not modify and part of this Metrics class, including the submit delay or frequency!
        // WARNING: Modifying this code will get your plugin banned on bStats. Just don't do it!
        long initialDelay = (long) (1000 * 60 * (3 + Math.random() * 3));
        long secondDelay = (long) (1000 * 60 * (Math.random() * 30));
        scheduler.schedule(submitTask, initialDelay, TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixedRate(submitTask, initialDelay + secondDelay, 1000 * 60 * 30, TimeUnit.MILLISECONDS);

        // Let's log if things are enabled or not, once at startup:
        List<String> enabled = new ArrayList<>();
        List<String> disabled = new ArrayList<>();
        for (Metrics metrics : knownMetricsInstances) {
            if (Sponge.getMetricsConfigManager().areMetricsEnabled(metrics.getPluginContainer())) {
                enabled.add(metrics.getPluginContainer().getName());
            } else {
                disabled.add(metrics.getPluginContainer().getName());
            }
        }
        StringBuilder builder = new StringBuilder().append(System.lineSeparator());
        builder.append("bStats metrics is present in ").append((enabled.size() + disabled.size())).append(" plugins on this server.");
        builder.append(System.lineSeparator());
        if (enabled.isEmpty()) {
            builder.append("Presently, none of them are allowed to send data.").append(System.lineSeparator());
        } else {
            builder.append("Presently, the following ").append(enabled.size()).append(" plugins are allowed to send data:").append(System.lineSeparator());
            builder.append(enabled).append(System.lineSeparator());
        }
        if (disabled.isEmpty()) {
            builder.append("None of them have data sending disabled.");
            builder.append(System.lineSeparator());
        } else {
            builder.append("Presently, the following ").append(disabled.size()).append(" plugins are not allowed to send data:").append(System.lineSeparator());
            builder.append(disabled).append(System.lineSeparator());
        }
        builder.append("To change the enabled/disabled state of any bStats use in a plugin, visit the Sponge config!");
        logger.info(builder.toString());
    }

    /**
     * Gets the server specific data.
     *
     * @return The server specific data.
     */
    private JsonObject getServerData() {
        // Minecraft specific data
        int playerAmount = Math.min(Sponge.getServer().getOnlinePlayers().size(), 200);
        int onlineMode = Sponge.getServer().getOnlineMode() ? 1 : 0;
        String minecraftVersion = Sponge.getGame().getPlatform().getMinecraftVersion().getName();
        String spongeImplementation = Sponge.getPlatform().getContainer(Platform.Component.IMPLEMENTATION).getName();

        // OS/Java specific data
        String javaVersion = System.getProperty("java.version");
        String osName = System.getProperty("os.name");
        String osArch = System.getProperty("os.arch");
        String osVersion = System.getProperty("os.version");
        int coreCount = Runtime.getRuntime().availableProcessors();

        JsonObject data = new JsonObject();

        data.addProperty("serverUUID", serverUUID);

        data.addProperty("playerAmount", playerAmount);
        data.addProperty("onlineMode", onlineMode);
        data.addProperty("minecraftVersion", minecraftVersion);
        data.addProperty("spongeImplementation", spongeImplementation);

        data.addProperty("javaVersion", javaVersion);
        data.addProperty("osName", osName);
        data.addProperty("osArch", osArch);
        data.addProperty("osVersion", osVersion);
        data.addProperty("coreCount", coreCount);

        return data;
    }

    /**
     * Collects the data and sends it afterwards.
     */
    private void submitData() {
        final JsonObject data = getServerData();

        JsonArray pluginData = new JsonArray();
        // Search for all other bStats Metrics classes to get their plugin data
        for (Metrics metrics : knownMetricsInstances) {
            if (!Sponge.getMetricsConfigManager().areMetricsEnabled(metrics.getPluginContainer())) {
                continue;
            }
            JsonObject plugin = metrics.getPluginData();
            if (plugin != null) {
                pluginData.add(plugin);
            }
        }

        if (pluginData.size() == 0) {
            return; // All plugins disabled, so we don't send anything
        }

        data.add("plugins", pluginData);

        // Create a new thread for the connection to the bStats server
        new Thread(() -> {
            try {
                // Send the data
                sendData(logger, data);
            } catch (Exception e) {
                // Something went wrong! :(
                if (logFailedRequests) {
                    logger.warn("Could not submit plugin stats!", e);
                }
            }
        }).start();
    }

    /**
     * Loads the bStats configuration.
     *
     * @throws IOException If something did not work :(
     */
    private void loadConfig() throws IOException {
        Path configPath = configDir.resolve("bStats");
        configPath.toFile().mkdirs();
        File configFile = new File(configPath.toFile(), "config.conf");
        HoconConfigurationLoader configurationLoader = HoconConfigurationLoader.builder().setFile(configFile).build();
        CommentedConfigurationNode node;
        if (!configFile.exists()) {
            configFile.createNewFile();
            node = configurationLoader.load();

            // Add default values
            node.getNode("enabled").setValue(false);
            // Every server gets it's unique random id.
            node.getNode("serverUuid").setValue(UUID.randomUUID().toString());
            // Should failed request be logged?
            node.getNode("logFailedRequests").setValue(false);
            // Should the sent data be logged?
            node.getNode("logSentData").setValue(false);
            // Should the response text be logged?
            node.getNode("logResponseStatusText").setValue(false);

            node.getNode("enabled").setComment(
                    "Enabling bStats in this file is deprecated. At least one of your plugins now uses the\n" +
                            "Sponge config to control bStats. Leave this value as you want it to be for outdated plugins,\n" +
                            "but look there for further control");
            // Add information about bStats
            node.getNode("serverUuid").setComment(
                    "bStats collects some data for plugin authors like how many servers are using their plugins.\n" +
                            "To control whether this is enabled or disabled, see the Sponge configuration file.\n" +
                            "Check out https://bStats.org/ to learn more :)"
            );
            node.getNode("configVersion").setValue(2);

            configurationLoader.save(node);
        } else {
            node = configurationLoader.load();

            if (!node.getNode("configVersion").isVirtual()) {

                node.getNode("configVersion").setValue(2);

                node.getNode("enabled").setComment(
                        "Enabling bStats in this file is deprecated. At least one of your plugins now uses the\n" +
                                "Sponge config to control bStats. Leave this value as you want it to be for outdated plugins,\n" +
                                "but look there for further control");

                node.getNode("serverUuid").setComment(
                        "bStats collects some data for plugin authors like how many servers are using their plugins.\n" +
                                "To control whether this is enabled or disabled, see the Sponge configuration file.\n" +
                                "Check out https://bStats.org/ to learn more :)"
                );

                configurationLoader.save(node);
            }
        }

        // Load configuration
        serverUUID = node.getNode("serverUuid").getString();
        logFailedRequests = node.getNode("logFailedRequests").getBoolean(false);
        logSentData = node.getNode("logSentData").getBoolean(false);
        logResponseStatusText = node.getNode("logResponseStatusText").getBoolean(false);
    }

    /**
     * Reads the first line of the file.
     *
     * @param file The file to read. Cannot be null.
     * @return The first line of the file or {@code null} if the file does not exist or is empty.
     * @throws IOException If something did not work :(
     */
    private String readFile(File file) throws IOException {
        if (!file.exists()) {
            return null;
        }
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            return bufferedReader.readLine();
        }
    }

    /**
     * Sends the data to the bStats server.
     *
     * @param logger The used logger.
     * @param data The data to send.
     * @throws Exception If the request failed.
     */
    private static void sendData(Logger logger, JsonObject data) throws Exception {
        Validate.notNull(data, "Data cannot be null");
        if (logSentData) {
            logger.info("Sending data to bStats: {}", data);
        }
        HttpsURLConnection connection = (HttpsURLConnection) new URL(URL).openConnection();

        // Compress the data to save bandwidth
        byte[] compressedData = compress(data.toString());

        // Add headers
        connection.setRequestMethod("POST");
        connection.addRequestProperty("Accept", "application/json");
        connection.addRequestProperty("Connection", "close");
        connection.addRequestProperty("Content-Encoding", "gzip"); // We gzip our request
        connection.addRequestProperty("Content-Length", String.valueOf(compressedData.length));
        connection.setRequestProperty("Content-Type", "application/json"); // We send our data in JSON format
        connection.setRequestProperty("User-Agent", "MC-Server/" + B_STATS_VERSION);

        // Send data
        connection.setDoOutput(true);
        try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
            outputStream.write(compressedData);
        }

        StringBuilder builder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }
        }

        if (logResponseStatusText) {
            logger.info("Sent data to bStats and received response: {}", builder);
        }
    }

    /**
     * Gzips the given String.
     *
     * @param str The string to gzip.
     * @return The gzipped String.
     * @throws IOException If the compression failed.
     */
    private static byte[] compress(final String str) throws IOException {
        if (str == null) {
            return null;
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(outputStream)) {
            gzip.write(str.getBytes(StandardCharsets.UTF_8));
        }
        return outputStream.toByteArray();
    }

}
