package iincinerator.pfm;

import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

// Copyright 2020, Idealism Incinerator. All Rights Reserved.

public class Modifications {
    public static void start() {
        for (ModFileInfo info : FMLLoader.getLoadingModList().getModFiles()) {
            boolean isPFM = false;
            for (IModInfo modInfo : info.getMods()) {
                if (modInfo.getModId().equals("pfm")) {
                    isPFM = true;
                    break;
                }
            }
            if (isPFM) continue;
            Path path = info.getFile().getFilePath();
            try {
                for (String className : getOffendingEntries(getAllJarEntries(new JarInputStream(new FileInputStream(path.toFile()))))) {
                    Class<?> clazz = Class.forName(className);
                    for (Field field : clazz.getDeclaredFields()) {
                        if (field.getName().equals("ELECTION_DAY")) {
                            setPrivateStaticFinalField(field, LocalDate.of(1969, Month.APRIL, 1)); // My favorite date!
                        }
                    }
                }
            } catch (ClassNotFoundException | IOException | NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private static List<JarEntry> getAllJarEntries(JarInputStream jis) throws IOException {
        List<JarEntry> output = new ArrayList<>();
        boolean reading = true;
        while (reading) {
            JarEntry nextJarEntry = jis.getNextJarEntry();
            if (nextJarEntry != null) {
                output.add(nextJarEntry);
            } else {
                reading = false;
            }
        }
        return output;
    }

    private static List<String> getOffendingEntries(List<JarEntry> jarEntries) {
        List<String> output = new ArrayList<>();
        for (JarEntry jarEntry : jarEntries) {
            if (jarEntry.getName().toLowerCase().contains("vote")) {
                if (!jarEntry.getName().startsWith("assets") && !jarEntry.getName().contains("package-info") && !isStringLowerCase(jarEntry.getName())) {
                    output.add(jarEntry.getName().replaceAll("/", ".").replaceFirst(".class", ""));
                }
            }
        }
        return output;
    }

    private static boolean isStringLowerCase(String str){
        char[] charArray = str.toCharArray();
        for (char c : charArray) {
            if (Character.isLetter(c) && Character.isUpperCase(c))
                return false;
        }
        return true;
    }

    private static void setPrivateStaticFinalField(Field field, Object value) throws NoSuchFieldException, IllegalAccessException {
        field.setAccessible(true);

        Field modifiers = Field.class.getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, value);
    }
}
