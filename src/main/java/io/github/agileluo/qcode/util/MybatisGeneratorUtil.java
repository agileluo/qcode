package io.github.agileluo.qcode.util;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.codegen.RootClassInfo;
import org.mybatis.generator.config.*;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.generator.internal.NullProgressCallback;
import org.mybatis.generator.internal.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

import static org.mybatis.generator.internal.util.ClassloaderUtility.getCustomClassloader;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * 代码生成工具
 */
public class MybatisGeneratorUtil {
    private static final Logger log = LoggerFactory.getLogger(MybatisGeneratorUtil.class);

    public static void genCode(String table)throws  Exception{
        genCodeInner(table, false);
    }

    public static void genCode(String table, boolean overwrite)throws  Exception{
        genCodeInner(table, overwrite);
    }

    static void genCodeInner(String table,boolean overwrite)throws  Exception{
        String baseDir = MybatisGeneratorUtil.class.getClassLoader().getResource(".").getPath();
        baseDir= baseDir.substring(0, baseDir.indexOf("target"));
        log.info("projectDir: {}", baseDir);

        Properties p = new Properties();
        p.load(MybatisGeneratorUtil.class.getClassLoader().getResourceAsStream("application.properties"));

        List<String> warnings = new ArrayList<String>();
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config =  new Configuration();
        Context context = new Context(ModelType.FLAT);
        context.setId("Mysql");
        context.setTargetRuntime("MyBatis3Simple");

        PluginConfiguration pluginConfig = new PluginConfiguration();
        pluginConfig.setConfigurationType("tk.mybatis.mapper.generator.MapperPlugin");
        pluginConfig.addProperty("mappers", "tk.mybatis.mapper.common.Mapper");
        pluginConfig.addProperty("caseSensitive", "true");
        context.addPluginConfiguration(pluginConfig);

        JDBCConnectionConfiguration jdbcConfig = new JDBCConnectionConfiguration();
        jdbcConfig.setDriverClass("com.mysql.jdbc.Driver");
        String jdbcUrl = p.getProperty("jdbc.url");
        String jdbcUsername = p.getProperty("jdbc.username");
        String jdbcPassword = p.getProperty("jdbc.password");
        log.info("jdbc: url={}, username={}, password={}", jdbcUrl, jdbcUsername, jdbcPassword);
        jdbcConfig.setConnectionURL(jdbcUrl);
        jdbcConfig.setUserId(jdbcUsername);
        jdbcConfig.setPassword(jdbcPassword);
        context.setJdbcConnectionConfiguration(jdbcConfig);

        String app = p.getProperty("spring.application.name");

        JavaModelGeneratorConfiguration model = new JavaModelGeneratorConfiguration();
        model.setTargetPackage("io.github.agileluo." + app + ".model");
        model.setTargetProject(baseDir + "/src/main/java");
        context.setJavaModelGeneratorConfiguration(model);

        SqlMapGeneratorConfiguration sql = new SqlMapGeneratorConfiguration();
        sql.setTargetPackage("mybatis_mapper");
        sql.setTargetProject(baseDir + "/src/main/resources");
        context.setSqlMapGeneratorConfiguration(sql);

        JavaClientGeneratorConfiguration mapper = new JavaClientGeneratorConfiguration();
        mapper.setTargetPackage("io.github.agileluo." + app + ".mapper");
        mapper.setTargetProject(baseDir + "/src/main/java");
        mapper.setConfigurationType("XMLMAPPER");
        context.setJavaClientGeneratorConfiguration(mapper);

        TableConfiguration tableConfig = new TableConfiguration(context);
        tableConfig.setTableName(table);
        context.addTableConfiguration(tableConfig);

        config.addContext(context);

        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        Generator myBatisGenerator = new Generator(config, callback, warnings);
        myBatisGenerator.generate(null);
        for (String warning : warnings) {
            log.info(warning);
        }
    }

    static class Generator{
        /** The configuration. */
        private Configuration configuration;

        /** The shell callback. */
        private ShellCallback shellCallback;

        /** The generated java files. */
        private List<GeneratedJavaFile> generatedJavaFiles;

        /** The generated xml files. */
        private List<GeneratedXmlFile> generatedXmlFiles;

        /** The warnings. */
        private List<String> warnings;

        /** The projects. */
        private Set<String> projects;

        /**
         * Constructs a MyBatisGenerator object.
         *
         * @param configuration
         *            The configuration for this invocation
         * @param shellCallback
         *            an instance of a ShellCallback interface. You may specify
         *            <code>null</code> in which case the DefaultShellCallback will
         *            be used.
         * @param warnings
         *            Any warnings generated during execution will be added to this
         *            list. Warnings do not affect the running of the tool, but they
         *            may affect the results. A typical warning is an unsupported
         *            data type. In that case, the column will be ignored and
         *            generation will continue. You may specify <code>null</code> if
         *            you do not want warnings returned.
         * @throws InvalidConfigurationException
         *             if the specified configuration is invalid
         */
        public Generator(Configuration configuration, ShellCallback shellCallback,
                                List<String> warnings) throws InvalidConfigurationException {
            super();
            if (configuration == null) {
                throw new IllegalArgumentException(getString("RuntimeError.2")); //$NON-NLS-1$
            } else {
                this.configuration = configuration;
            }

            if (shellCallback == null) {
                this.shellCallback = new DefaultShellCallback(false);
            } else {
                this.shellCallback = shellCallback;
            }

            if (warnings == null) {
                this.warnings = new ArrayList<String>();
            } else {
                this.warnings = warnings;
            }
            generatedJavaFiles = new ArrayList<GeneratedJavaFile>();
            generatedXmlFiles = new ArrayList<GeneratedXmlFile>();
            projects = new HashSet<String>();

            this.configuration.validate();
        }

        /**
         * This is the main method for generating code. This method is long running, but progress can be provided and the
         * method can be canceled through the ProgressCallback interface. This version of the method runs all configured
         * contexts.
         *
         * @param callback
         *            an instance of the ProgressCallback interface, or <code>null</code> if you do not require progress
         *            information
         * @throws SQLException
         *             the SQL exception
         * @throws IOException
         *             Signals that an I/O exception has occurred.
         * @throws InterruptedException
         *             if the method is canceled through the ProgressCallback
         */
        public void generate(ProgressCallback callback) throws SQLException,
                IOException, InterruptedException {
            generate(callback, null, null, true);
        }

        /**
         * This is the main method for generating code. This method is long running, but progress can be provided and the
         * method can be canceled through the ProgressCallback interface.
         *
         * @param callback
         *            an instance of the ProgressCallback interface, or <code>null</code> if you do not require progress
         *            information
         * @param contextIds
         *            a set of Strings containing context ids to run. Only the contexts with an id specified in this list
         *            will be run. If the list is null or empty, than all contexts are run.
         * @throws SQLException
         *             the SQL exception
         * @throws IOException
         *             Signals that an I/O exception has occurred.
         * @throws InterruptedException
         *             if the method is canceled through the ProgressCallback
         */
        public void generate(ProgressCallback callback, Set<String> contextIds)
                throws SQLException, IOException, InterruptedException {
            generate(callback, contextIds, null, true);
        }

        /**
         * This is the main method for generating code. This method is long running, but progress can be provided and the
         * method can be cancelled through the ProgressCallback interface.
         *
         * @param callback
         *            an instance of the ProgressCallback interface, or <code>null</code> if you do not require progress
         *            information
         * @param contextIds
         *            a set of Strings containing context ids to run. Only the contexts with an id specified in this list
         *            will be run. If the list is null or empty, than all contexts are run.
         * @param fullyQualifiedTableNames
         *            a set of table names to generate. The elements of the set must be Strings that exactly match what's
         *            specified in the configuration. For example, if table name = "foo" and schema = "bar", then the fully
         *            qualified table name is "foo.bar". If the Set is null or empty, then all tables in the configuration
         *            will be used for code generation.
         * @throws SQLException
         *             the SQL exception
         * @throws IOException
         *             Signals that an I/O exception has occurred.
         * @throws InterruptedException
         *             if the method is canceled through the ProgressCallback
         */
        public void generate(ProgressCallback callback, Set<String> contextIds,
                             Set<String> fullyQualifiedTableNames) throws SQLException,
                IOException, InterruptedException {
            generate(callback, contextIds, fullyQualifiedTableNames, true);
        }

        /**
         * This is the main method for generating code. This method is long running, but progress can be provided and the
         * method can be cancelled through the ProgressCallback interface.
         *
         * @param callback
         *            an instance of the ProgressCallback interface, or <code>null</code> if you do not require progress
         *            information
         * @param contextIds
         *            a set of Strings containing context ids to run. Only the contexts with an id specified in this list
         *            will be run. If the list is null or empty, than all contexts are run.
         * @param fullyQualifiedTableNames
         *            a set of table names to generate. The elements of the set must be Strings that exactly match what's
         *            specified in the configuration. For example, if table name = "foo" and schema = "bar", then the fully
         *            qualified table name is "foo.bar". If the Set is null or empty, then all tables in the configuration
         *            will be used for code generation.
         * @param writeFiles
         *            if true, then the generated files will be written to disk.  If false,
         *            then the generator runs but nothing is written
         * @throws SQLException
         *             the SQL exception
         * @throws IOException
         *             Signals that an I/O exception has occurred.
         * @throws InterruptedException
         *             if the method is canceled through the ProgressCallback
         */
        public void generate(ProgressCallback callback, Set<String> contextIds,
                             Set<String> fullyQualifiedTableNames, boolean writeFiles) throws SQLException,
                IOException, InterruptedException {

            if (callback == null) {
                callback = new NullProgressCallback();
            }

            generatedJavaFiles.clear();
            generatedXmlFiles.clear();
            ObjectFactory.reset();
            RootClassInfo.reset();

            // calculate the contexts to run
            List<Context> contextsToRun;
            if (contextIds == null || contextIds.size() == 0) {
                contextsToRun = configuration.getContexts();
            } else {
                contextsToRun = new ArrayList<Context>();
                for (Context context : configuration.getContexts()) {
                    if (contextIds.contains(context.getId())) {
                        contextsToRun.add(context);
                    }
                }
            }

            // setup custom classloader if required
            if (configuration.getClassPathEntries().size() > 0) {
                ClassLoader classLoader = getCustomClassloader(configuration.getClassPathEntries());
                ObjectFactory.addExternalClassLoader(classLoader);
            }

            // now run the introspections...
            int totalSteps = 0;
            for (Context context : contextsToRun) {
                totalSteps += context.getIntrospectionSteps();
            }
            callback.introspectionStarted(totalSteps);

            for (Context context : contextsToRun) {
                context.introspectTables(callback, warnings,
                        fullyQualifiedTableNames);
            }

            // now run the generates
            totalSteps = 0;
            for (Context context : contextsToRun) {
                totalSteps += context.getGenerationSteps();
            }
            callback.generationStarted(totalSteps);

            for (Context context : contextsToRun) {
                context.generateFiles(callback, generatedJavaFiles,
                        generatedXmlFiles, warnings);
            }

            // now save the files
            if (writeFiles) {
                callback.saveStarted(generatedXmlFiles.size()
                        + generatedJavaFiles.size());

                for (GeneratedXmlFile gxf : generatedXmlFiles) {
                    projects.add(gxf.getTargetProject());
                    writeGeneratedXmlFile(gxf, callback);
                }

                for (GeneratedJavaFile gjf : generatedJavaFiles) {
                    projects.add(gjf.getTargetProject());
                    writeGeneratedJavaFile(gjf, callback);
                }

                for (String project : projects) {
                    shellCallback.refreshProject(project);
                }
            }

            callback.done();
        }

        private void writeGeneratedJavaFile(GeneratedJavaFile gjf, ProgressCallback callback)
                throws InterruptedException, IOException {
            File targetFile;
            String source;
            try {
                File directory = shellCallback.getDirectory(gjf
                        .getTargetProject(), gjf.getTargetPackage());
                targetFile = new File(directory, gjf.getFileName());
                if (targetFile.exists()) {
                    if (shellCallback.isMergeSupported()) {
                        source = shellCallback.mergeJavaFile(gjf
                                        .getFormattedContent(), targetFile,
                                MergeConstants.OLD_ELEMENT_TAGS,
                                gjf.getFileEncoding());
                    } else if (shellCallback.isOverwriteEnabled()) {
                        source = gjf.getFormattedContent();
                        warnings.add(getString("Warning.11", //$NON-NLS-1$
                                targetFile.getAbsolutePath()));
                    } else {
                        log.warn("file exists: {}", targetFile.getAbsolutePath());
                        return;
                    }
                } else {
                    source = gjf.getFormattedContent();
                }

                callback.checkCancel();
                callback.startTask(getString(
                        "Progress.15", targetFile.getName())); //$NON-NLS-1$
                writeFile(targetFile, source, gjf.getFileEncoding());
            } catch (ShellException e) {
                warnings.add(e.getMessage());
            }
        }

        private void writeGeneratedXmlFile(GeneratedXmlFile gxf, ProgressCallback callback)
                throws InterruptedException, IOException {
            File targetFile;
            String source;
            try {
                File directory = shellCallback.getDirectory(gxf
                        .getTargetProject(), gxf.getTargetPackage());
                targetFile = new File(directory, gxf.getFileName());
                if (targetFile.exists()) {
                    if (shellCallback.isOverwriteEnabled()) {
                        source = gxf.getFormattedContent();
                        warnings.add(getString("Warning.11", //$NON-NLS-1$
                                targetFile.getAbsolutePath()));
                    } else {
                        log.warn("file exists: {}", targetFile.getAbsolutePath());
                        return;
                    }
                } else {
                    source = gxf.getFormattedContent();
                }

                callback.checkCancel();
                callback.startTask(getString(
                        "Progress.15", targetFile.getName())); //$NON-NLS-1$
                writeFile(targetFile, source, "UTF-8"); //$NON-NLS-1$
            } catch (ShellException e) {
                warnings.add(e.getMessage());
            }
        }

        /**
         * Writes, or overwrites, the contents of the specified file.
         *
         * @param file
         *            the file
         * @param content
         *            the content
         * @param fileEncoding
         *            the file encoding
         * @throws IOException
         *             Signals that an I/O exception has occurred.
         */
        private void writeFile(File file, String content, String fileEncoding) throws IOException {
            if(file.getName().endsWith("java") && content.contains("@Table")){
                content = content.substring(0, content.indexOf("@Table")) +
                        "import lombok.Data;\r\n@Data \r\n" +
                        content.substring(content.indexOf("@Table"));
                content = content.substring(0, content.indexOf("public", content.indexOf("private")));
                content = content.substring(0, content.lastIndexOf("/**")) + "\r\n}";
            }
            if(file.exists()){
                log.info("overwrite success: {}", file.getAbsolutePath());
            }else{
                log.info("gen success: {}", file.getAbsolutePath());
            }
            FileOutputStream fos = new FileOutputStream(file, false);
            OutputStreamWriter osw;
            if (fileEncoding == null) {
                osw = new OutputStreamWriter(fos);
            } else {
                osw = new OutputStreamWriter(fos, fileEncoding);
            }

            BufferedWriter bw = new BufferedWriter(osw);
            bw.write(content);
            bw.close();
        }

        /**
         * Gets the unique file name.
         *
         * @param directory
         *            the directory
         * @param fileName
         *            the file name
         * @return the unique file name
         */
        private File getUniqueFileName(File directory, String fileName) {
            File answer = null;

            // try up to 1000 times to generate a unique file name
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < 1000; i++) {
                sb.setLength(0);
                sb.append(fileName);
                sb.append('.');
                sb.append(i);

                File testFile = new File(directory, sb.toString());
                if (!testFile.exists()) {
                    answer = testFile;
                    break;
                }
            }

            if (answer == null) {
                throw new RuntimeException(getString(
                        "RuntimeError.3", directory.getAbsolutePath())); //$NON-NLS-1$
            }

            return answer;
        }

        /**
         * Returns the list of generated Java files after a call to one of the generate methods.
         * This is useful if you prefer to process the generated files yourself and do not want
         * the generator to write them to disk.
         *
         * @return the list of generated Java files
         */
        public List<GeneratedJavaFile> getGeneratedJavaFiles() {
            return generatedJavaFiles;
        }

        /**
         * Returns the list of generated XML files after a call to one of the generate methods.
         * This is useful if you prefer to process the generated files yourself and do not want
         * the generator to write them to disk.
         *
         * @return the list of generated XML files
         */
        public List<GeneratedXmlFile> getGeneratedXmlFiles() {
            return generatedXmlFiles;
        }
    }
}
