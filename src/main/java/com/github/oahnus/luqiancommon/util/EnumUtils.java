package com.github.oahnus.luqiancommon.util;

import com.github.oahnus.luqiancommon.mybatis.BaseEnum;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by oahnus on 2019-11-20
 * 14:47.
 * 生成 enum.js
 */
public class EnumUtils {
    /**
     * @param enumPackageName 枚举类所在package name  e.g. com.github.oahnus.enum
     * @param targetFilePath 生成js文件路径   xxx/xxx/xxx/enum.js
     * @throws FileNotFoundException e
     */
    public static void generateEnumJs(String enumPackageName, String targetFilePath) throws FileNotFoundException {
        String packagePath = enumPackageName.replace(".", "/");

        URL url = EnumUtils.class.getClassLoader().getResource("");
        String basePath = url.getPath();

        File enumDir = new File(basePath + packagePath);
        File[] files = enumDir.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("Dir Is Empty");
            return;
        }

        File output = new File(targetFilePath);
        PrintWriter pw = new PrintWriter(new FileOutputStream(output));

        pw.println("// @see EnumUtils " + LocalDate.now());
        for (File file : files) {
            String fileName = file.getName();
            String classSimpleName = fileName.substring(0, fileName.lastIndexOf("."));
            Map<Class, Boolean> verifyMap = new HashMap<>();
            try {
                Class<?> enumClass = Class.forName(enumPackageName + "." + classSimpleName);
                Method[] methods = enumClass.getDeclaredMethods();
                boolean containMsgMethod = Arrays.stream(methods)
                        .anyMatch(method -> method.getName().equals("getMsg"));

                Boolean contain = verifyMap.get(enumClass);
                if (contain == null || !verifyMap.get(enumClass)) {
                    verifyMap.put(enumClass, containMsgMethod);
                }

                Field[] declaredFields = enumClass.getDeclaredFields();

                pw.println(String.format("export const %s = {", enumClass.getSimpleName()));
                for (Field field : declaredFields) {
                    if (field.isEnumConstant()) {
                        Serializable enumValue = ((BaseEnum) field.get(null)).getCode();
                        String enumName = field.getName();

                        if (verifyMap.get(enumClass)) {
                            Serializable msg = ((BaseEnum) field.get(null)).getMsg();
                            pw.println(String.format("\t%s: [\"%s\", \"%s\"],", enumValue, enumName, msg));
                        } else {
                            pw.println(String.format("\t%s: [\"%s\"],", enumValue, enumName));
                        }
//                        pw.println(String.format("\t%s: []"));
                    }
                }
                pw.println("}");

                pw.println(String.format("export const %sEnum = {", enumClass.getSimpleName()));
                for (Field field : declaredFields) {
                    if (field.isEnumConstant()) {
                        Serializable enumValue = ((BaseEnum) field.get(null)).getCode();
                        String enumName = field.getName();
                        pw.println(String.format("\t%s: %s,", enumName, ((BaseEnum) field.get(null)).getCode()));
                    }
                }
                pw.println("}");
            } catch (Exception e) {
                e.printStackTrace();
            }
//            System.out.println(classSimpleName);
        }
        pw.println("export const map2ListOptions = (map) => {\n" +
                "  let list = [];\n" +
                "  for(let key in map){\n" +
                "    let lt = {label: map[key][1], value: Number(key)}\n" +
                "    list.push(lt)\n" +
                "  }\n" +
                "  return list;\n" +
                "}\n" +
                "\n" +
                "export const map2Filter = (map) => {\n" +
                "  return (value) => {\n" +
                "    if (value === null || value === undefined) {\n" +
                "      return '-'\n" +
                "    }\n" +
                "    let val = map[value]\n" +
                "    return val[1]\n" +
                "  }\n" +
                "}");

        pw.flush();
        pw.close();
        System.out.println("Generate Finish");
    }
}
