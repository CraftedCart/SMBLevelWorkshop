package craftedcart.smblevelworkshop.resource;

import craftedcart.smblevelworkshop.exception.GLSLCompileException;
import craftedcart.smblevelworkshop.resource.model.OBJLoader;
import craftedcart.smblevelworkshop.resource.model.ResourceModel;
import craftedcart.smblevelworkshop.ui.LoadingScreen;
import craftedcart.smblevelworkshop.util.CrashHandler;
import craftedcart.smblevelworkshop.util.LogHelper;
import io.github.craftedcart.fluidui.FontCache;
import io.github.craftedcart.fluidui.util.UIColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GL20;
import org.newdawn.slick.SlickException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by CraftedCart on 28/03/2016 (DD/MM/YYYY)
 */
public class ResourceManager {

    public static ResourceBundle initResources;
    private static Map<String, IResource> resources = new HashMap<>();
    private static Map<String, ResourceTexture> textureResources = new HashMap<>(); //This exists to prevent casting from IResource to ResourceTexture

    private static final String vanillaResourcePath = "resources/vanilla";

    private static Map<String, File> fontResourcesToLoad = new HashMap<>(); //TTF font
    private static Map<String, File> fontCacheResourcesToLoad = new HashMap<>(); //JSON font cache
    private static Map<String, File> pngResourcesToLoad = new HashMap<>(); //PNG textures
    private static Map<String, File> langPackResourcesToLoad = new HashMap<>(); //XML language pack
    private static Map<String, File> vertShaderResourcesToLoad = new HashMap<>(); //Vertex shader
    private static Map<String, File> fragShaderResourcesToLoad = new HashMap<>(); //Fragment shader
    private static Map<String, File> shaderCacheResourcesToLoad = new HashMap<>(); //JSON shader cache
//    private static Map<String, URL> musicOggResourcesToLoad = new HashMap<>(); //OGG Music Audio
    private static Map<String, File> objResourcesToLoad = new HashMap<>(); //OBJ models

    private static Map<String, String> erroredResources = new HashMap<>();

    @Nullable private static File tempDir;

    public static void preInit() throws IOException, FontFormatException {
        initResources = ResourceBundle.getBundle("initResources");
    }

    public static void queueVanillaResources() {
        try {
            tempDir = File.createTempFile("SMBLevelWorkshop", null);
            tempDir.deleteOnExit();
            if (!tempDir.delete()) {
                throw new IOException("Could not delete temp file: " + tempDir.getAbsolutePath());
            }
            if (!tempDir.mkdir()) {
                throw new IOException("Could not create temp directory: " + tempDir.getAbsolutePath());
            }

            final File jarFile = new File(ResourceManager.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());

            if (!jarFile.isDirectory()) { //Run from JAR

                final JarFile jar = new JarFile(jarFile);
                final Enumeration<JarEntry> enumEntries = jar.entries();

                while (enumEntries.hasMoreElements()) { //Iterate through all resources
                    JarEntry file = enumEntries.nextElement();

                    if (!file.toString().startsWith(vanillaResourcePath) || file.isDirectory()) {
                        continue;
                    }

                    LogHelper.trace(ResourceManager.class, String.format("Found resource \"%s\"",
                            file.toString().substring(vanillaResourcePath.length() + 1)));


//                    if (file.toString().toUpperCase().endsWith(".BGMUSIC.OGG")) { //Check for ogg stuff here as it can't use a temp file
//                        musicOggResourcesToLoad.put(file.toString().substring(vanillaResourcePath.length() + 1),
//                                ResourceManager.class.getResource("/" + file.toString()));
//                    } else {

                    File outFile = new File(tempDir, file.toString());
                    outFile.deleteOnExit();

                    if (!outFile.getParentFile().exists()) {
                        if (!outFile.getParentFile().mkdirs()) {
                            throw new IOException("Error while creating directories for file: " + outFile.getAbsolutePath());
                        }
                    }
                    if (!outFile.createNewFile()) {
                        throw new IOException("Error while creating new file: " + outFile.getAbsolutePath());
                    }

                    Files.copy(ResourceManager.class.getResourceAsStream("/" + file.toString()), outFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    queueAddResource(new File(tempDir, file.toString()), file.toString().substring(vanillaResourcePath.length() + 1));
//                    }

                }

            } else { //Run from outside of JAR (Eg: IDE)

                final URL url = ResourceManager.class.getResource("/" + vanillaResourcePath);

                if (url != null) {

                    Files.walkFileTree(new File(url.getPath()).toPath(), new FileVisitor<Path>() {
                        @Override
                        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                            if (!file.toString().startsWith(url.getFile())) {
                                return FileVisitResult.CONTINUE;
                            }

                            LogHelper.trace(ResourceManager.class, String.format("Found resource \"%s\"", file.toString().substring(url.getFile().length() + 1)));

                            queueAddResource(file.toFile(), file.toString().substring(url.getFile().length() + 1));

                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                            LogHelper.fatal(getClass(), exc);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                            return FileVisitResult.CONTINUE;
                        }
                    });
                }

            }

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

    }

    /**
     * Will queue a resource to be added later by {@link ResourceManager#registerAllResources()}
     *
     * @param resourceFile The file of the resource to be queued
     * @param resourceID The name, or ID of the resource to be queued. This is typically the name of the file (with file type extension)
     * @throws IOException
     */
    private static void queueAddResource(@NotNull File resourceFile, @NotNull String resourceID) throws IOException {

        if (resourceID.toUpperCase().endsWith(".PNG")) { //PNG texture (Ignore .exclude.png)
            pngResourcesToLoad.put(resourceID, resourceFile);
        } else if (resourceID.toUpperCase().endsWith(".TTF")) { //TTF Font
            fontResourcesToLoad.put(resourceID, resourceFile);
        } else if (resourceID.toUpperCase().endsWith(".FONTS.JSON")) { //Fonts to add to the Font cache
            fontCacheResourcesToLoad.put(resourceID, resourceFile);
        } else if (resourceID.toUpperCase().endsWith(".LANG.XML")) { //Language packs
            langPackResourcesToLoad.put(resourceID, resourceFile);
        } else if (resourceID.toUpperCase().endsWith(".FRAG")) { //Fragment shader
            fragShaderResourcesToLoad.put(resourceID, resourceFile);
        } else if (resourceID.toUpperCase().endsWith(".VERT")) { //Vertex shader
            vertShaderResourcesToLoad.put(resourceID, resourceFile);
        } else if (resourceID.toUpperCase().endsWith(".SHADERS.JSON")) { //Shaders to add to the Shader cache
            shaderCacheResourcesToLoad.put(resourceID, resourceFile);
//        } else if (resourceID.toUpperCase().endsWith(".BGMUSIC.OGG")) { //OGG Background Music
//            musicOggResourcesToLoad.put(resourceID, resourceFile.toURI().toURL());
        } else if (resourceID.toUpperCase().endsWith(".OBJ")) { //Shaders to add to the Shader cache
            objResourcesToLoad.put(resourceID, resourceFile);
        } else if (!Objects.equals(resourceID.toUpperCase(), ".DS_STORE") && //Ignore .DS_STORE
                !Objects.equals(resourceID.toUpperCase(), "THUMBS.DB") && //Ignore THUMBS.DB
                !resourceID.toUpperCase().endsWith(".MTL")) { //Ignore .MTL (These are handled by the OBJLoader))
            addWarnedResource(resourceID, initResources.getString("unrecognisedFileExtension"));
            LogHelper.warn(ResourceManager.class, String.format("Unrecognised resource file extension: \"%s\"", resourceID));
        }

    }

    /**
     * Will register all resources in the correct order.
     */
    public static void registerAllResources() {
        erroredResources.clear();

        //The total number of resources to load
        int totalResources =
                fontResourcesToLoad.size() +
                fontCacheResourcesToLoad.size() +
                pngResourcesToLoad.size() +
                langPackResourcesToLoad.size() +
                vertShaderResourcesToLoad.size() +
                fragShaderResourcesToLoad.size() +
                shaderCacheResourcesToLoad.size() +
//                musicOggResourcesToLoad.size();
                objResourcesToLoad.size();
        int currentResource = 0;

        for (Map.Entry<String, File> resource : fontResourcesToLoad.entrySet()) { //Register Font TTF files
            currentResource++;
            LoadingScreen.infoMessage = resource.getKey();
            LoadingScreen.progress = currentResource / (double) totalResources;
            try {
                registerTTF(resource.getValue(), resource.getKey());
            } catch (IOException e) {
                addErroredResource(resource.getKey(), e.getMessage());
                LogHelper.error(ResourceManager.class, CrashHandler.getStackTraceString(e));
            }
        }

        for (Map.Entry<String, File> resource : fontCacheResourcesToLoad.entrySet()) { //Register Font cache JSON files
            currentResource++;
            LoadingScreen.infoMessage = resource.getKey();
            LoadingScreen.progress = currentResource / (double) totalResources;
            try {
                registerFontCache(resource.getValue(), resource.getKey());
            } catch (IOException | LWJGLException | SlickException | FontFormatException | ParseException e) {
                addErroredResource(resource.getKey(), e.getMessage());
                LogHelper.error(ResourceManager.class, CrashHandler.getStackTraceString(e));
            }
        }

        //Load and register the loading screen background first if it exists
        File loadBGFile = pngResourcesToLoad.get("_loadBackground.png");
        if (loadBGFile != null) {
            currentResource++;
            LoadingScreen.infoMessage = "_loadBackground.png";
            LoadingScreen.progress = currentResource / (double) totalResources;
            try {
                registerPNG(loadBGFile, "_loadBackground.png");
            } catch (Exception e) {
                addErroredResource("_loadBackground.png", e.getMessage());
                LogHelper.error(ResourceManager.class, CrashHandler.getStackTraceString(e));
            }

            pngResourcesToLoad.remove("_loadBackground.png");
        }

        for (Map.Entry<String, File> resource : pngResourcesToLoad.entrySet()) { //Register PNG textures
            currentResource++;
            LoadingScreen.infoMessage = resource.getKey();
            LoadingScreen.progress = currentResource / (double) totalResources;
            try {
                registerPNG(resource.getValue(), resource.getKey());
            } catch (Exception e) {
                addErroredResource(resource.getKey(), e.getMessage());
                LogHelper.error(ResourceManager.class, CrashHandler.getStackTraceString(e));
            }
        }

        for (Map.Entry<String, File> resource : langPackResourcesToLoad.entrySet()) { //Register Lang files
            currentResource++;
            LoadingScreen.infoMessage = resource.getKey();
            LoadingScreen.progress = currentResource / (double) totalResources;
            try {
                registerLangPack(resource.getValue(), resource.getKey());
            } catch (ParserConfigurationException | IOException | SAXException e) {
                addErroredResource(resource.getKey(), e.getMessage());
                LogHelper.error(ResourceManager.class, e);
                e.printStackTrace();
            }
        }

        for (Map.Entry<String, File> resource : vertShaderResourcesToLoad.entrySet()) { //Register Vertex shaders
            currentResource++;
            LoadingScreen.infoMessage = resource.getKey();
            LoadingScreen.progress = currentResource / (double) totalResources;
            try {
                registerShader(resource.getValue(), resource.getKey(), GL20.GL_VERTEX_SHADER);
            } catch (IOException | LWJGLException | GLSLCompileException e) {
                addErroredResource(resource.getKey(), e.getMessage());
                LogHelper.error(ResourceManager.class, CrashHandler.getStackTraceString(e));
            }
        }

        for (Map.Entry<String, File> resource : fragShaderResourcesToLoad.entrySet()) { //Register Fragment shaders
            currentResource++;
            LoadingScreen.infoMessage = resource.getKey();
            LoadingScreen.progress = currentResource / (double) totalResources;
            try {
                registerShader(resource.getValue(), resource.getKey(), GL20.GL_FRAGMENT_SHADER);
            } catch (IOException | LWJGLException | GLSLCompileException e) {
                addErroredResource(resource.getKey(), e.getMessage());
                LogHelper.error(ResourceManager.class, CrashHandler.getStackTraceString(e));
            }
        }

        for (Map.Entry<String, File> resource : shaderCacheResourcesToLoad.entrySet()) { //Register Shader cache JSON files
            currentResource++;
            LoadingScreen.infoMessage = resource.getKey();
            LoadingScreen.progress = currentResource / (double) totalResources;
            try {
                registerShaderProgram(resource.getValue(), resource.getKey());
            } catch (Exception e) {
                addErroredResource(resource.getKey(), e.getMessage());
                LogHelper.error(ResourceManager.class, CrashHandler.getStackTraceString(e));
            }
        }

//        for (Map.Entry<String, URL> resource : musicOggResourcesToLoad.entrySet()) { //Register OGG background music
//            currentResource++;
//            LoadingScreen.infoMessage = resource.getKey();
//            LoadingScreen.progress = currentResource / (double) totalResources;
//            try {
//                registerOggMusic(resource.getValue(), resource.getKey());
//            } catch (Exception e) {
//                addErroredResource(resource.getKey(), e.getMessage());
//                LogHelper.error(ResourceManager.class, CrashHandler.getStackTraceString(e));
//            }
//        }

        for (Map.Entry<String, File> resource : objResourcesToLoad.entrySet()) { //Register Shader cache JSON files
            currentResource++;
            LoadingScreen.infoMessage = resource.getKey();
            LoadingScreen.progress = currentResource / (double) totalResources;
            try {
                registerObj(resource.getValue(), resource.getKey());
            } catch (Exception e) {
                addErroredResource(resource.getKey(), e.getMessage());
                LogHelper.error(ResourceManager.class, CrashHandler.getStackTraceString(e));
            }
        }

        if (tempDir != null) {
            try {
                removeRecursive(tempDir.toPath());
            } catch (IOException e) {
                LogHelper.error(ResourceManager.class, "Failed to delete temp dir: " + tempDir.getAbsolutePath());
                LogHelper.error(ResourceManager.class, e);
            }
        }

    }

    /**
     * Will load and register a PNG texture
     *
     * @param resourceFile The file to be registered
     * @param resourceID The name, or ID of the resource to be registered. This is typically the name of the file (with .png extension)
     * @throws Exception
     */
    public static void registerPNG(@NotNull File resourceFile, @NotNull String resourceID) throws Exception {
        if (!resourceID.toUpperCase().endsWith(".EXCLUDE.PNG")) {
            LogHelper.trace(ResourceManager.class, String.format("Loading PNG texture from \"%s\"", resourceFile.getPath()));

            ResourceTexture tex = new ResourceTexture("PNG", resourceFile);
            textureResources.put(resourceID.substring(0, resourceID.length() - 4), tex);

            LogHelper.trace(ResourceManager.class, String.format("Added PNG texture \"%s\"", resourceID.substring(0, resourceID.length() - 4)));
        } else {
            LogHelper.trace(ResourceManager.class, String.format("Ignoring PNG texture \"%s\"", resourceID.substring(0, resourceID.length() - 4)));
        }
    }

    /**
     * Will load and register a TrueType font
     *
     * @param resourceFile The file to be registered
     * @param resourceID The name, or ID of the resource to be registered. This is typically the name of the file (with .ttf extension)
     * @throws IOException
     */
    public static void registerTTF(@NotNull File resourceFile, @NotNull String resourceID) throws IOException {
        LogHelper.trace(ResourceManager.class, String.format("Loading TTF font data from \"%s\"", resourceFile.getPath()));

        try {
            FontCache.registerAWTFont(resourceID.substring(0, resourceID.length() - 4), new FileInputStream(resourceFile));
            LogHelper.trace(ResourceManager.class, String.format("Added TTF font data \"%s\"", resourceID.substring(0, resourceID.length() - 4)));
        } catch (FontFormatException e) {
            addErroredResource(resourceID, e.getMessage());
            LogHelper.error(ResourceManager.class, String.format("Invalid font format \"%s\"", resourceFile.getPath()));
            LogHelper.error(ResourceManager.class, e);
        }
    }

    /**
     * Will load and register fonts and their sizes specified within the json file
     *
     * @param resourceFile The file to be registered
     * @param resourceID The name, or ID of the resource to be registered. This is typically the name of the file (with .fonts.json extension)
     * @throws IOException
     * @throws ParseException
     * @throws LWJGLException
     * @throws SlickException
     * @throws FontFormatException
     */
    public static void registerFontCache(File resourceFile, @NotNull String resourceID) throws IOException, ParseException, LWJGLException, SlickException, FontFormatException {
        LogHelper.trace(ResourceManager.class, String.format("Loading font JSON data from \"%s\"", resourceFile.getPath()));

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader(Objects.requireNonNull(resourceFile)));

        if (obj instanceof JSONArray) {
            JSONArray unicodeFonts = (JSONArray) obj;

            for (Object fontObject : unicodeFonts) {
                JSONObject fontData = (JSONObject) fontObject;

                if (fontData.containsKey("name") && fontData.containsKey("size")) {
                    if (fontData.get("name") instanceof String) {
                        if (fontData.get("size") instanceof Long) {

                            String name = (String) fontData.get("name");
                            int size = (int) (long) fontData.get("size");

                            craftedcart.smblevelworkshop.Window.drawable.makeCurrent();
                            FontCache.registerUnicodeFont(name, size); //Register the font

                        } else {
                            addErroredResource(resourceID, String.format(initResources.getString("errorFontSizeNotNumber"), fontData.toJSONString()));
                            LogHelper.error(ResourceManager.class, String.format("Invalid fonts definition \"%s\" - Entry \"size\" of \"%s\" is not a number",
                                    resourceID, fontData.toJSONString()));
                        }
                    } else {
                        addErroredResource(resourceID, String.format(initResources.getString("errorFontNameNotString"), fontData.toJSONString()));
                        LogHelper.error(ResourceManager.class, String.format("Invalid fonts definition \"%s\" - Entry \"name\" of \"%s\" is not a string",
                                resourceID, fontData.toJSONString()));
                    }
                } else {
                    addErroredResource(resourceID, String.format(initResources.getString("errorFontDefMissingKeys"), fontData.toJSONString()));
                    LogHelper.error(ResourceManager.class, String.format("Invalid fonts definition \"%s\" - Entry \"%s\" does not contain the keys \"name\" and \"size\"",
                            resourceID, fontData.toJSONString()));
                }
            }

        } else {
            addErroredResource(resourceID, initResources.getString("errorFontDefNotArray"));
            LogHelper.error(ResourceManager.class, String.format("Invalid fonts definition \"%s\" - File contents is not an array", resourceID));
        }

        LogHelper.trace(ResourceManager.class, String.format("Added font JSON data \"%s\"", resourceID.substring(0, resourceID.length() - 11)));
    }

    /**
     * Will load and register an XML language pack
     *
     * @param resourceFile The file to be registered
     * @param resourceID The name, or ID of the resource to be registered. This is typically the name of the file (with .lang.xml extension)
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public static void registerLangPack(@NotNull File resourceFile, @NotNull String resourceID) throws ParserConfigurationException, IOException, SAXException {
        LogHelper.trace(ResourceManager.class, String.format("Loading XML lang data from \"%s\"", resourceFile.getPath()));

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(resourceFile); //Parse the XML file

        Element root = document.getDocumentElement();
        if (!Objects.equals(root.getTagName(), "langPack")) { //The root tag is not "langPack"
            addErroredResource(resourceID, initResources.getString("errorLangNotLangPack"));
            LogHelper.error(ResourceManager.class, String.format("The root tag of \"%s\" is not \"langPack\"", resourceID));
            return;
        }

        String locale = root.getAttribute("locale");
        if (locale.isEmpty()) { //Error: No locale specified
            addErroredResource(resourceID, initResources.getString("errorLangNoLocale"));
            LogHelper.error(ResourceManager.class, String.format("No locale attribute specified for \"%s\"", resourceID));
            return;
        }

        for (int i = 0; i < root.getChildNodes().getLength(); i++) {
            Node child = root.getChildNodes().item(i);
            if (Objects.equals(child.getNodeName(), "entry")) {

                @Nullable Node key = child.getAttributes().getNamedItem("key");
                @Nullable Node val = child.getAttributes().getNamedItem("val");

                if (key != null && val != null) {
                    LangManager.addItem(locale, key.getTextContent(), val.getTextContent());
                } else {
                    addErroredResource(resourceID, initResources.getString("errorLangMissingKeyVal"));
                    LogHelper.error(ResourceManager.class, String.format("XML node entry in \"%s\" missing attributes \"key\" and \"val\"", resourceID));
                }

            }
        }

        LogHelper.trace(ResourceManager.class, String.format("Added XML lang data \"%s\"", resourceID.substring(0, resourceID.length() - 4)));
    }

    /**
     * Will load and compile a GLSL shader
     *
     * @param resourceFile The file to be compuled
     * @param resourceID The name, or ID of the resource to be registered. This is typically the name of the file (with .frag or .vert extension)
     * @param shaderType {@link GL20#GL_FRAGMENT_SHADER} or {@link GL20#GL_VERTEX_SHADER}
     * @throws IOException
     * @throws LWJGLException
     */
    public static void registerShader(@NotNull File resourceFile, @NotNull String resourceID, int shaderType) throws IOException, LWJGLException, GLSLCompileException {
        LogHelper.trace(ResourceManager.class, String.format("Loading GLSL shader from \"%s\"", resourceFile.getPath()));

        ResourceShader tex = new ResourceShader(shaderType, resourceFile);
        resources.put(resourceID.substring(0, resourceID.length() - 5), tex);

        LogHelper.trace(ResourceManager.class, String.format("Added GLSL shader \"%s\"", resourceID.substring(0, resourceID.length() - 5)));
    }

    /**
     * Will load shader programs from a vert and frag shader resource specified within the json file
     *
     * @param resourceFile The file to be registered
     * @param resourceID The name, or ID of the resource to be registered. This is typically the name of the file
     */
    public static void registerShaderProgram(@NotNull File resourceFile, @NotNull String resourceID) throws Exception {
        LogHelper.trace(ResourceManager.class, String.format("Loading shader program JSON data from \"%s\"", resourceFile.getPath()));

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader(resourceFile));

        if (obj instanceof JSONArray) {
            JSONArray programs = (JSONArray) obj;

            for (Object programObject : programs) {
                JSONObject programData = (JSONObject) programObject;

                if (programData.containsKey("name") && programData.containsKey("vert") && programData.containsKey("frag")) {
                    if (programData.get("name") instanceof String && programData.get("vert") instanceof String && programData.get("frag") instanceof String) {

                        String name = (String) programData.get("name");
                        String vert = (String) programData.get("vert");
                        String frag = (String) programData.get("frag");

                        ResourceShaderProgram shaderProgram = new ResourceShaderProgram(getShader(vert), getShader(frag));
                        resources.put(name, shaderProgram);

                    } else {
                        addErroredResource(resourceID, String.format(initResources.getString("errorShaderProgEntriesNotString"), programData.toJSONString()));
                        LogHelper.error(ResourceManager.class, String.format("Invalid shader program definition \"%s\" - Entry \"name\", \"vert\" and / or " +
                                "\"frag\" of \"%s\" is not a string",
                                resourceID, programData.toJSONString()));
                    }
                } else {
                    addErroredResource(resourceID, String.format(initResources.getString("errorShaderProgDefMissingKeys"), programData.toJSONString()));
                    LogHelper.error(ResourceManager.class, String.format("Invalid shader program definition \"%s\" - Entry \"%s\" does not contain the keys \"name\", \"vert\" and \"frag\"",
                            resourceID, programData.toJSONString()));
                }
            }

        } else {
            addErroredResource(resourceID, initResources.getString("errorShaderProgDefNotArray"));
            LogHelper.error(ResourceManager.class, String.format("Invalid shader program definition \"%s\" - File contents is not an array", resourceID));
        }

        LogHelper.trace(ResourceManager.class, String.format("Added shader program JSON data \"%s\"", resourceID.substring(0, resourceID.length() - 13)));
    }

//    /**
//     * Will load OGG music files
//     *
//     * @param resourceFile The file to be registered
//     * @param resourceID The name, or ID of the resource to be registered. This is typically the name of the file (with .bgMusic.ogg extension)
//     */
//    public static void registerOggMusic(@NotNull URL resourceFile, @NotNull String resourceID) throws Exception {
//        LogHelper.trace(ResourceManager.class, String.format("Loading OGG music data from \"%s\"", resourceFile.getPath()));
//
//        AudioUtils.addMusic(resourceID, resourceFile);
//
//        LogHelper.trace(ResourceManager.class, String.format("Added OGG music data \"%s\"", resourceID.substring(0, resourceID.length() - 12)));
//    }

    /**
     * Will load obj models
     *
     * @param resourceFile The file to be registered
     * @param resourceID The name, or ID of the resource to be registered. This is typically the name of the file (with .obj extension)
     */
    public static void registerObj(@NotNull File resourceFile, @NotNull String resourceID) throws Exception {
        LogHelper.trace(ResourceManager.class, String.format("Loading OBJ model data from \"%s\"", resourceFile.getPath()));

        resources.put(resourceID.substring(0, resourceID.length() - 4), OBJLoader.loadModel(resourceFile.getPath()));

        LogHelper.trace(ResourceManager.class, String.format("Added OBJ model data \"%s\"", resourceID.substring(0, resourceID.length() - 4)));
    }

    private static void addErroredResource(@NotNull String key, @NotNull String value) {
        erroredResources.put(key, value);
        if (LoadingScreen.debugMessagesOverlay == null) {
            LoadingScreen.debugMessagesOverlay = new LinkedHashMap<>();
        }
        //noinspection ConstantConditions
        LoadingScreen.debugMessagesOverlay.put(String.format("%s: %s", key, value), UIColor.matRed());
    }

    private static void addWarnedResource(String key, String value) {
        erroredResources.put(key, value);
        if (LoadingScreen.debugMessagesOverlay == null) {
            LoadingScreen.debugMessagesOverlay = new LinkedHashMap<>();
        }
        //noinspection ConstantConditions
        LoadingScreen.debugMessagesOverlay.put(String.format("%s: %s", key, value), UIColor.matYellow());
    }

    public static boolean doesTextureResourceExist(String resourceID) {
        return textureResources.containsKey(resourceID);
    }

    public static boolean doesResourceExist(String resourceID) {
        return resources.containsKey(resourceID);
    }

    public static IResource getResource(String resourceID) {
        return getResource(resourceID, true);
    }

    public static IResource getResource(String resourceID, boolean showErrorIfNonExistent) {
        if (resources.containsKey(resourceID)) {
            return resources.get(resourceID);
        } else if (showErrorIfNonExistent) {
            LogHelper.error(ResourceManager.class, String.format("Resource \"%s\" does not exist", resourceID));
        }
        return null;
    }

    public static ResourceTexture getTexture(String resourceID) {
        return getTexture(resourceID, true);
    }

    public static ResourceTexture getTexture(String resourceID, boolean showErrorIfNonExistent) {
        if (textureResources.containsKey(resourceID)) {
            return textureResources.get(resourceID);
        } else if (showErrorIfNonExistent) {
            LogHelper.error(ResourceManager.class, String.format("Texture resource \"%s\" does not exist", resourceID));
        }
        return null;
    }

    public static ResourceShader getShader(String resourceID) {
        return (ResourceShader) getResource(resourceID);
    }

    public static ResourceShaderProgram getShaderProgram(String resourceID) {
        return (ResourceShaderProgram) getResource(resourceID);
    }

    public static ResourceModel getModel(String resourceID) {
        return (ResourceModel) getResource(resourceID);
    }

    private static File getAppSupportDirectory() {

        String workingDirectory;
        String os = (System.getProperty("os.name")).toUpperCase();

        if (os.contains("WIN")) {
            //If on Windows, get the Application Data folder
            workingDirectory = System.getenv("AppData") + "\\SMBLevelWorkshop";
        } else if (os.contains("MAC OS X")) {
            //If we are on a Mac, goto the "Application Support" directory
            workingDirectory = System.getProperty("user.home") + "/Library/Application Support/SMBLevelWorkshop";
        } else {
            //Assume Linux or some other distro. Use the User's home folder
            workingDirectory = System.getProperty("user.home") + "/.config/SMBLevelWorkshop";
        }

        File fileAppSupportDirectory = new File(workingDirectory);

        tryCreateDirectory(fileAppSupportDirectory);
        return fileAppSupportDirectory;

    }

    private static void tryCreateDirectory(File directory) {
        if ((!directory.isDirectory() && directory.mkdirs()) || directory.isDirectory()) {
            return;
        }
        LogHelper.fatal(ResourceManager.class, String.format("Failed to create directories \"%s\"", directory.toString()));
        throw new RuntimeException(String.format("Failed to create directories \"%s\"", directory.toString()));
    }

    public static void removeRecursive(Path path) throws IOException
    {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>()
        {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException
            {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException
            {
                // try to delete the file anyway, even if its attributes
                // could not be read, since delete-only access is
                // theoretically possible
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
            {
                if (exc == null)
                {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
                else
                {
                    // directory iteration failed; propagate exception
                    throw exc;
                }
            }
        });
    }

}
