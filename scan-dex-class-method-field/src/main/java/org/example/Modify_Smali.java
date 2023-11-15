package org.example;

import com.baidu.titan.dex.DexConst;
import com.baidu.titan.dex.DexRegister;
import com.baidu.titan.dex.DexRegisterList;
import com.baidu.titan.dex.DexString;
import com.baidu.titan.dex.DexType;
import com.baidu.titan.dex.DexTypeList;
import com.baidu.titan.dex.Dops;
import com.baidu.titan.dex.node.DexAnnotationNode;
import com.baidu.titan.dex.node.DexClassNode;
import com.baidu.titan.dex.node.DexCodeNode;
import com.baidu.titan.dex.node.DexFieldNode;
import com.baidu.titan.dex.node.DexFileNode;
import com.baidu.titan.dex.node.DexMethodNode;
import com.baidu.titan.dex.node.MultiDexFileNode;
import com.baidu.titan.dex.node.insn.DexInsnNode;
import com.baidu.titan.dex.node.insn.DexPseudoInsnNode;
import com.baidu.titan.dex.reader.DexFileReader;
import com.baidu.titan.dex.reader.MultiDexFileReader;
import com.baidu.titan.dex.visitor.DexClassNodeVisitor;
import com.baidu.titan.dex.visitor.DexClassVisitor;
import com.baidu.titan.dex.visitor.DexClassVisitorInfo;
import com.baidu.titan.dex.visitor.DexCodeVisitor;
import com.baidu.titan.dex.visitor.DexLabel;
import com.baidu.titan.dex.visitor.DexMethodVisitor;
import com.baidu.titan.dex.visitor.DexMethodVisitorInfo;
import com.baidu.titan.dex.visitor.MultiDexFileNodeVisitor;
import com.baidu.titan.dex.writer.DexFileWriter;
import com.baidu.titan.dex.writer.MultiDexFileWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tong
 */
public class Modify_Smali {

    static byte[] getFileContent(File f) throws IOException {
        FileInputStream in = null;
        try {
            in = new FileInputStream(f);
            byte[] buf = new byte[16 * 1024];
            int len;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((len = in.read(buf)) > 0) {
                baos.write(buf, 0, len);
            }
            return baos.toByteArray();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        //https://source.android.com/docs/core/dalvik/dalvik-bytecode?hl=zh-cn
        //https://source.android.com/docs/core/dalvik/dex-format?hl=zh-cn#code-item

        File testBase = new File(".test");

        File dexFile = new File(testBase, "classes.dex");
        System.out.println("dexFile Path = " + dexFile.getAbsolutePath());
        DexFileWriter writer = new DexFileWriter() {
            @Override
            public DexClassVisitor visitClass(DexClassVisitorInfo classInfo) {
                DexClassVisitor dexClassVisitor = super.visitClass(classInfo);
                if ("Lcom/example/smali_modify_demo/MainActivity;".equals(classInfo.type.toTypeDescriptor())) {
                    return new DexClassVisitor(dexClassVisitor){
                        @Override
                        public DexMethodVisitor visitMethod(DexMethodVisitorInfo methodInfo) {
                            DexMethodVisitor dexMethodVisitor = super.visitMethod(methodInfo);

                            if ("test".equals(methodInfo.name.toString())) {
                                return new DexMethodVisitor(dexMethodVisitor) {
                                    @Override
                                    public DexCodeVisitor visitCode() {
                                        DexCodeVisitor dexCodeVisitor = super.visitCode();
                                        /**
                                         *     new-instance v0, Ljava/lang/RuntimeException;
                                         *     const-string v1, "ErrorMock throwRuntimeException"
                                         *     invoke-direct {v0, v1}, Ljava/lang/RuntimeException;-><init>(Ljava/lang/String;)V
                                         *     throw v0
                                         */
                                        dexCodeVisitor.visitConstInsn(Dops.NEW_INSTANCE
                                                , DexRegisterList.newBuilder().addReg(DexRegister.makeLocalReg(0)).build()
                                                , DexConst.ConstType.make(new DexType("Ljava/lang/RuntimeException;")));

                                        dexCodeVisitor.visitConstInsn(Dops.CONST_STRING
                                                , DexRegisterList.newBuilder().addReg(DexRegister.makeLocalReg(1)).build()
                                                , DexConst.ConstString.make("ErrorMock throwRuntimeException"));

                                        dexCodeVisitor.visitConstInsn(Dops.INVOKE_DIRECT
                                                , DexRegisterList.newBuilder().addReg(DexRegister.makeLocalReg(0)).addReg(DexRegister.makeLocalReg(1)).build()
                                                , DexConst.ConstMethodRef.make(new DexType("Ljava/lang/RuntimeException;"), new DexString("<init>"), new DexType("V"), DexTypeList.newBuilder().addType(new DexType("Ljava/lang/String;")).build()));

                                        dexCodeVisitor.visitSimpleInsn(Dops.THROW
                                                , DexRegisterList.newBuilder().addReg(DexRegister.makeLocalReg(0)).build());
                                        return new DexCodeVisitor(dexCodeVisitor) {
                                            @Override
                                            public void visitRegisters(int localRegCount, int parameterRegCount) {
                                                super.visitRegisters(localRegCount + 2, parameterRegCount);
                                            }

                                            @Override
                                            public void visitConstInsn(int op, DexRegisterList regs, DexConst dexConst) {
                                                super.visitConstInsn(op, regs, dexConst);
                                            }
                                        };
                                    }
                                };
                            }

                            return dexMethodVisitor;
                        }
                    };
                }
                return dexClassVisitor;
            }
        };

        DexFileReader reader = new DexFileReader(getFileContent(dexFile));


        reader.accept(writer);

        File outDexFile = new File(testBase, "out.dex");

        byte[] content = writer.toByteArray();

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outDexFile);
            fos.write(content);
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
    }
}