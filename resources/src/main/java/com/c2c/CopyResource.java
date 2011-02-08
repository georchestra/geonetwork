package com.c2c;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.*;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Goal which copies all resource from source directory to destination directory and replaces the
 * @xxx@ tags in the file
 *
 * @goal copy-config
 * 
 * @phase process-sources
 */
public class CopyResource
    extends AbstractMojo
{

    private Pattern pattern = Pattern.compile("\\$\\{(.+?)}");
    /**
     * Location of the file.
     * @parameter
     * @required
     */
    private File outputDirectory;

    /**
     * Location of the file.
     * @parameter
     * @required
     */
    private File[] sourceDirectories;

    /**
     * Location of the file.
     * @parameter
     */
    private File[] excludes = new File[0];

    /**
     * Filters to use for filtering the files
     * @parameter
     * @required
     */
    private File filter;

    public void execute()
        throws MojoExecutionException
    {

        Properties filterProperties = getFilterProperties();
        for (File sourceDirectory : sourceDirectories) {
            getLog().info("Copying resources from "+sourceDirectory+" to "+ outputDirectory);
            for (File file : sourceDirectory.listFiles()) {
                copyResources(sourceDirectory, file, filterProperties);
            }
        }

    }

    private Properties getFilterProperties() throws MojoExecutionException {
        FileReader in = null;
        try {
            in = new FileReader(filter);

            Properties filterProperties = new Properties();
            filterProperties.load(in);
            filterProperties.putAll(System.getenv());
            filterProperties.putAll(System.getProperties());

            getLog().debug("Filter properties: "+filterProperties);

            String next = findVariable(filterProperties);
            while(next != null) {
                String value = filterProperties.getProperty(next);
                String newVal = filterText(value, filterProperties);
                filterProperties.put(next, newVal);
                next = findVariable(filterProperties);
            }

            return filterProperties;
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to load filter file: "+filter);
        } finally {
            if(in != null) try {
                in.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String findVariable(Properties filterProperties) {
        for (Map.Entry<Object, Object> entry : filterProperties.entrySet()) {
            String value = (String) entry.getValue();
            Matcher matcher = pattern.matcher(value);

            if(matcher.find()) return entry.getKey().toString();
        }

        return null;
    }

    private void copyResources(File root, File file, Properties filterProperties) throws MojoExecutionException {
        if(exclude(file)) return;

        if(file.isDirectory()) {
            File dir = new File(outputDirectory, relative(root, file));
            if (!dir.exists() && !dir.mkdir()) {
                throw new MojoExecutionException("Failed to create directory "+relative(outputDirectory,dir)+" root: "+root+" source:"+file);
            }
            for (File next : file.listFiles()) {
                copyResources(root, next, filterProperties);
            }
        } else {
            File target = new File(outputDirectory,relative(root,file));
            try {
                copy(file,target,filterProperties);
            } catch (Throwable e) {
                throw new MojoExecutionException("Failed to copy file"+relative(outputDirectory,target)+" root: "+root+" source:"+relative(root,file), e);
            }
        }
    }

    private boolean exclude(File file) {
        for (File exclude : excludes) {
            if(file.getName().equals(exclude.getName())) return true;
        }

        return false;
    }

    private void copy(File file, File target, Properties filterProperties) throws IOException, MojoExecutionException {
        String text = read(file);
        String updatedText = filterText(text,filterProperties);
        write(target,updatedText);
    }

    private String filterText(String text, Properties filterProperties) throws MojoExecutionException {
        Matcher matcher = pattern.matcher(text);

        String result = text;
        while (matcher.find()) {
            String key = matcher.group(1);
            String replacement = filterProperties.getProperty(key);
            if(filterProperties.containsKey(key)) {
                result = result.replace(matcher.group(),replacement);
            } else {
                throw new MojoExecutionException("Unable to find replacement for "+matcher.group());
            }
        }
        return result;
    }

    private void write(File target, String updatedText) throws IOException {
        Writer out = null;
        try {
            out = new FileWriter(target);
            out.write(updatedText);
        } finally {
            if(out !=null) out.close();
        }
    }

    private String read(File file) throws IOException {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            String next = in.readLine();
            StringBuilder b = new StringBuilder();
            while(next != null) {
                b.append(next);
                b.append('\n');
                next = in.readLine();
            }
            return b.toString();
        } finally {
            if(in !=null) in.close();
        }
    }

    private String relative(File root, File file) {
        return file.getPath().substring(root.getPath().length());
    }
}
