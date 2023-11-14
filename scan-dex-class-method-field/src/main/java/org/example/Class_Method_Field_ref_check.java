package org.example;

import com.baidu.titan.dex.DexConst;
import com.baidu.titan.dex.DexRegisterList;
import com.baidu.titan.dex.DexString;
import com.baidu.titan.dex.DexType;
import com.baidu.titan.dex.Dops;
import com.baidu.titan.dex.node.MultiDexFileNode;
import com.baidu.titan.dex.reader.DexFileReader;
import com.baidu.titan.dex.visitor.DexClassVisitor;
import com.baidu.titan.dex.visitor.DexClassVisitorInfo;
import com.baidu.titan.dex.visitor.DexCodeVisitor;
import com.baidu.titan.dex.visitor.DexLabel;
import com.baidu.titan.dex.visitor.DexMethodVisitor;
import com.baidu.titan.dex.visitor.DexMethodVisitorInfo;
import com.baidu.titan.dex.writer.DexFileWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author tong
 */
public class Class_Method_Field_ref_check {

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

    public static void main(String[] args) throws Throwable {
        //https://source.android.com/docs/core/dalvik/dalvik-bytecode?hl=zh-cn
        //https://source.android.com/docs/core/dalvik/dex-format?hl=zh-cn#code-item

        visit();
    }

    private static void visit() throws Throwable {
        File testBase = new File(".test");

        File dexFile = new File(testBase, "classes.dex");
        System.out.println("dexFile Path = " + dexFile.getAbsolutePath());
        DexFileWriter writer = new DexFileWriter() {
            @Override
            public DexClassVisitor visitClass(DexClassVisitorInfo classInfo) {
                return new DexClassVisitor(super.visitClass(classInfo)){
                    @Override
                    public DexMethodVisitor visitMethod(DexMethodVisitorInfo methodInfo) {

                        return new DexMethodVisitor(super.visitMethod(methodInfo)) {
                            @Override
                            public DexCodeVisitor visitCode() {

                                DexCodeVisitor dexCodeVisitor = super.visitCode();

                                return new DexCodeVisitor(super.visitCode()) {

                                    @Override
                                    public void visitConstInsn(int op, DexRegisterList regs, DexConst dexConst) {
                                        switch (op) {
                                            //函数
                                            case Dops.INVOKE_VIRTUAL:
                                            case Dops.INVOKE_VIRTUAL_RANGE:
                                            case Dops.INVOKE_SUPER:
                                            case Dops.INVOKE_SUPER_RANGE: {

                                                break;
                                            }
                                            case Dops.INVOKE_DIRECT:
                                            case Dops.INVOKE_DIRECT_RANGE: {

                                                break;
                                            }

                                            case Dops.INVOKE_STATIC:
                                            case Dops.INVOKE_STATIC_RANGE: {

                                                break;
                                            }

                                            case Dops.INVOKE_INTERFACE:
                                            case Dops.INVOKE_INTERFACE_RANGE: {

                                                break;
                                            }

                                            //访问实例字段
                                            case Dops.IGET:
                                            case Dops.IGET_WIDE:
                                            case Dops.IGET_OBJECT:
                                            case Dops.IGET_BOOLEAN:
                                            case Dops.IGET_BYTE:
                                            case Dops.IGET_CHAR:
                                            case Dops.IGET_SHORT: {

                                                break;
                                            }

                                            //写实例字段
                                            case Dops.IPUT:
                                            case Dops.IPUT_WIDE:
                                            case Dops.IPUT_OBJECT:
                                            case Dops.IPUT_BOOLEAN:
                                            case Dops.IPUT_BYTE:
                                            case Dops.IPUT_CHAR:
                                            case Dops.IPUT_SHORT: {

                                                break;
                                            }

                                            //访问静态字段
                                            case Dops.SGET:
                                            case Dops.SGET_WIDE:
                                            case Dops.SGET_OBJECT:
                                            case Dops.SGET_BOOLEAN:
                                            case Dops.SGET_BYTE:
                                            case Dops.SGET_CHAR:
                                            case Dops.SGET_SHORT: {

                                                break;
                                            }

                                            //写对象静态字段
                                            case Dops.SPUT:
                                            case Dops.SPUT_WIDE:
                                            case Dops.SPUT_OBJECT:
                                            case Dops.SPUT_BOOLEAN:
                                            case Dops.SPUT_BYTE:
                                            case Dops.SPUT_CHAR:
                                            case Dops.SPUT_SHORT: {

                                                break;
                                            }
                                        }
                                        super.visitConstInsn(op, regs, dexConst);
                                    }
                                };
                            }
                        };
                    }
                };
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