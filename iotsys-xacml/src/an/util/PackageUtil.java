/**
 * This class is modified from m-m-m reflection util project, whose website is:
 * http://m-m-m.sourceforge.net/maven/mmm-util/mmm-util-reflect/
 * 
 * Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package an.util;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PackageUtil {
    public static void findClassesByPackage(String pkgName, boolean includeSubPackages, Set<Class<?>> result)
    throws IOException, ClassNotFoundException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String path = pkgName.replace('.', '/');
        String pathWithPrefix = path + '/';
        Enumeration<URL> urls = loader.getResources(path);
        StringBuilder qualifiedNameBuilder = new StringBuilder(pkgName);
        qualifiedNameBuilder.append('.');
        int qualifiedNamePrefixLength = qualifiedNameBuilder.length();

        while (urls.hasMoreElements()) {
            URL pkgUrl = urls.nextElement();
            String urlString = URLDecoder.decode(pkgUrl.getFile(), "UTF-8");
            String protocol = pkgUrl.getProtocol().toLowerCase();
            if ("file".equals(protocol)) {
                File pkgDir = new File(urlString);
                if (pkgDir.isDirectory()) {
                    if (includeSubPackages) {
                        findClassNamesRecursive(pkgDir, result, qualifiedNameBuilder, qualifiedNamePrefixLength);
                    }
                    else {
                        for (String fileName : pkgDir.list()) {
                            String simpleClassName = fixClassName(fileName);
                            if (simpleClassName != null) {
                                qualifiedNameBuilder.setLength(qualifiedNamePrefixLength);
                                qualifiedNameBuilder.append(simpleClassName);
                                Class<?> clazz = Class.forName(qualifiedNameBuilder.toString());
                                if (!clazz.isInterface() && !clazz.isAnnotation()) {
                                    result.add(clazz);
                                }
                            }
                        }
                    }
                }
            }
            else if ("jar".equals(protocol)) {
                // somehow the connection has no close method and can NOT be disposed
                JarURLConnection connection = (JarURLConnection) pkgUrl.openConnection();
                JarFile jarFile = connection.getJarFile();
                Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
                while (jarEntryEnumeration.hasMoreElements()) {
                    JarEntry jarEntry = jarEntryEnumeration.nextElement();
                    String absoluteFileName = jarEntry.getName();
                    if (absoluteFileName.endsWith(".class")) {
                        if (absoluteFileName.startsWith("/")) {
                            absoluteFileName.substring(1);
                        }
                        // special treatment for WAR files...
                        // "WEB-INF/lib/" entries should be opened directly in contained jar
                        if (absoluteFileName.startsWith("WEB-INF/classes/")) {
                            // "WEB-INF/classes/".length() == 16
                            absoluteFileName = absoluteFileName.substring(16);
                        }

                        boolean accept = true;
                        if (absoluteFileName.startsWith(pathWithPrefix)) {
                            String qualifiedName = absoluteFileName.replace('/', '.');
                            if (!includeSubPackages) {
                                int index = absoluteFileName.indexOf('/', qualifiedNamePrefixLength + 1);
                                if (index != -1) {
                                    accept = false;
                                }
                            }

                            if (accept) {
                                String className = fixClassName(qualifiedName);
                                if (className != null) {
                                    Class<?> clazz = Class.forName(className);
                                    if (!clazz.isInterface() && !clazz.isAnnotation()) {
                                        result.add(clazz);
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                throw new IllegalArgumentException("Unsupported protocol : " + protocol);
            }
        }
    }

    private static void findClassNamesRecursive(File pkgDir, Set<Class<?>> result,
            StringBuilder qualifiedNameBuilder, int qualifiedNamePrefixLength) throws ClassNotFoundException {
        for (File childFile : pkgDir.listFiles()) {
            String fileName = childFile.getName();
            if (childFile.isDirectory()) {
                qualifiedNameBuilder.setLength(qualifiedNamePrefixLength);
                StringBuilder subBuilder = new StringBuilder(qualifiedNameBuilder);
                subBuilder.append(fileName);
                subBuilder.append('.');
                findClassNamesRecursive(childFile, result, subBuilder, subBuilder.length());
            }
            else {
                String simpleClassName = fixClassName(fileName);
                if (simpleClassName != null) {
                    qualifiedNameBuilder.setLength(qualifiedNamePrefixLength);
                    qualifiedNameBuilder.append(simpleClassName);
                    Class<?> clazz = Class.forName(qualifiedNameBuilder.toString());
                    if (!clazz.isInterface() && !clazz.isAnnotation()) {
                        result.add(clazz);
                    }
                }
            }
        }
    }

    private static String fixClassName(String fileName) {
        if (fileName.endsWith(".class")) {
            // remove extension (".class".length() == 6)
            String nameWithoutExtension = fileName.substring(0, fileName.length() - 6);
            // handle inner classes...
            /*
             * int lastDollar = nameWithoutExtension.lastIndexOf('$');
             * if (lastDollar > 0) {
             *     char innerClassStart = nameWithoutExtension.charAt(lastDollar + 1);
             *     if ((innerClassStart >= '0') && (innerClassStart <= '9')) {
             *         // ignore anonymous class
             *     }
             *     else {
             *         return nameWithoutExtension.replace('$', '.');
             *     }
             * }
             * else {
             *     return nameWithoutExtension;
             * }
             */
            return nameWithoutExtension;
        }
        return null;
    }
}